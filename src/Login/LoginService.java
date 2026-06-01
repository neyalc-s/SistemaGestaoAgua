package Login;

import Connection.OracleConnection;
import Resources.MensagensInterface;
import Connection.OracleConnection.NoBD;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public final class LoginService
{
  private static final String CONSULTAR_FUNCIONARIO_ADMIN =
      "SELECT f.cod_funcionario, f.codigo_no, n.nome_no, f.estado_funcionario, n.estado_no "
          + "FROM ADM_OWNER.VW_LOGIN_FUNCIONARIO_ADMIN f "
          + "JOIN ADM_OWNER.VW_LOGIN_NO_ADMIN n ON n.codigo_no = f.codigo_no "
          + "WHERE UPPER(TRIM(f.username_oracle)) = UPPER(TRIM(?))";

  private static final String CONSULTAR_FUNCIONARIO_LOCAL =
      "SELECT cod_funcionario, codigo_no, nome_funcionario, estado_funcionario "
          + "FROM %s.VW_LOGIN_FUNCIONARIO_LOCAL "
          + "WHERE UPPER(TRIM(username_oracle)) = UPPER(TRIM(?))";

  private static final String ABRIR_SESSAO_ADMINISTRADOR =
      "{call PRC_ABRIR_SESSAO_FUNC(?, ?, ?, ?, ?, ?, ?, ?)}";

  private static final String ABRIR_SESSAO_LOCAL =
      "{call PRC_ABRIR_SESSAO_FUNC_LOCAL(?, ?, ?, ?, ?, ?, ?, ?)}";

  private static final String MSG_SESSAO_ORACLE_ACTIVA =
      "Este utilizador já possui uma sessão activa.\nFeche a sessão anterior antes de iniciar uma nova.";
  private static final String MSG_CREDENCIAIS_INVALIDAS = "Utilizador não existe ou credenciais inválidas.";
  private static final String MSG_NO_INDISPONIVEL = "O nó correspondente está temporariamente indisponível.";
  private static final String MSG_FALHA_CONEXAO = "Não foi possível conectar ao sistema. Tente novamente.";
  private static final String MSG_ADMIN_OFFLINE_FALLBACK_FALHOU =
      "AdministradorDB está indisponível e nenhum nó operacional aceitou este utilizador.";

  private static final NoBD[] NOS_OPERACIONAIS =
    {
        NoBD.FAMILIAS_COTAS, NoBD.DISTRIBUICAO_CONSUMO, NoBD.TRANSFERENCIAS_RECURSOS, NoBD.EQUIPES_GESTAO
    };

  public ResultadoLogin autenticar(String username, String senha) throws SQLException
  {
    Connection connectionAdmin = null;
    DadosFuncionario dados;
    try
    {
      connectionAdmin = OracleConnection.getConnectionLogin(NoBD.ADMINISTRADOR);
      dados = identificarFuncionarioNoAdmin(connectionAdmin, username);
    } catch(SQLException ex)
    {
      if(isErroIndisponibilidade(ex))
        return autenticarFallbackOperacional(username, senha);

      throw normalizarErroLogin(ex, null);
    } finally
    {
      OracleConnection.closeConnection(connectionAdmin);
    }

    return autenticarNoIdentificado(dados, username, senha);
  }

  private ResultadoLogin autenticarNoIdentificado(DadosFuncionario dados, String username, String senha)
      throws SQLException
  {
    Connection connectionNo = null;
    try
    {
      connectionNo = OracleConnection.getConnection(dados.no, username, senha);

      SessaoAberta sessao;
      if(dados.no == NoBD.ADMINISTRADOR)
        sessao = abrirSessaoAdministrador(connectionNo, username);
      else
        sessao = abrirSessaoLocal(connectionNo, username, "ONLINE");

      Connection connectionAceite = connectionNo;
      connectionNo = null;
      return new ResultadoLogin(true, sessao.mensagem, dados.no, sessao.codigoNo, dados.nomeNo, username,
          sessao.codFuncionario, sessao.nomeFuncionario, sessao.codigoSessao, connectionAceite, false);
    } catch(SQLException ex)
    {
      throw normalizarErroLogin(ex, dados.no);
    } finally
    {
      OracleConnection.closeConnection(connectionNo);
    }
  }

  private ResultadoLogin autenticarFallbackOperacional(String username, String senha) throws SQLException
  {
    SQLException ultimoErro = null;

    for(int i = 0; i < NOS_OPERACIONAIS.length; i++)
    {
      NoBD no = NOS_OPERACIONAIS[i];
      Connection connectionLogin = null;
      Connection connectionNo = null;

      try
      {
        connectionLogin = OracleConnection.getConnectionLogin(no);
        identificarFuncionarioNoLocal(connectionLogin, no, username);
      } catch(SQLException ex)
      {
        ultimoErro = ex;

        if(isFuncionarioNaoPertenceAoNo(ex))
          continue;

        if(isErroIndisponibilidade(ex))
          continue;

        throw normalizarErroLogin(ex, no);
      } finally
      {
        OracleConnection.closeConnection(connectionLogin);
      }

      try
      {
        connectionNo = OracleConnection.getConnection(no, username, senha);
        SessaoAberta sessao = abrirSessaoLocal(connectionNo, username, "OFFLINE");

        Connection connectionAceite = connectionNo;
        connectionNo = null;
        return new ResultadoLogin(true, sessao.mensagem, no, sessao.codigoNo, no.getNome(), username,
            sessao.codFuncionario, sessao.nomeFuncionario, sessao.codigoSessao, connectionAceite, true);
      } catch(SQLException ex)
      {
        ultimoErro = ex;

        if(isErroSessaoOracleActiva(ex) || isErroSessaoAberta(ex))
          throw normalizarErroLogin(ex, no);

        if(isErroLoginOracle(ex))
          throw erroAmigavel(MSG_CREDENCIAIS_INVALIDAS, ex);

        throw normalizarErroLogin(ex, no);
      } finally
      {
        OracleConnection.closeConnection(connectionNo);
      }
    }

    if(ultimoErro != null && (isErroLoginOracle(ultimoErro) || isFuncionarioNaoPertenceAoNo(ultimoErro)))
      throw erroAmigavel(MSG_CREDENCIAIS_INVALIDAS, ultimoErro);

    throw erroAmigavel(MSG_ADMIN_OFFLINE_FALLBACK_FALHOU,
        ultimoErro == null ? new SQLException(MSG_ADMIN_OFFLINE_FALLBACK_FALHOU) : ultimoErro);
  }

  private DadosFuncionario identificarFuncionarioNoAdmin(Connection connectionAdmin, String username) throws SQLException
  {
    if(username == null || username.trim().length() == 0)
      throw new SQLException("Username Oracle é obrigatório.");

    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try
    {
      statement = connectionAdmin.prepareStatement(CONSULTAR_FUNCIONARIO_ADMIN);
      statement.setString(1, username);
      resultSet = statement.executeQuery();

      if(!resultSet.next())
        throw new SQLException(MSG_CREDENCIAIS_INVALIDAS);

      int codFuncionario = resultSet.getInt(1);
      int codigoNo = resultSet.getInt(2);
      String nomeNo = resultSet.getString(3);
      String estadoFuncionario = resultSet.getString(4);
      String estadoNo = resultSet.getString(5);

      if(resultSet.next())
        throw new SQLException("Username Oracle duplicado no AdministradorDB.");

      if(!isEstadoActivo(estadoFuncionario))
        throw new SQLException("Funcionário Inactivo.");

      if(!isEstadoActivo(estadoNo))
        throw new SQLException("Nó do funcionário não está Activo.");

      NoBD no = NoBD.porCodigo(codigoNo);
      return new DadosFuncionario(codFuncionario, no, nomeNo);
    } finally
    {
      fecharResultSet(resultSet);
      fecharStatement(statement);
    }
  }

  private DadosFuncionario identificarFuncionarioNoLocal(Connection connectionLogin, NoBD no, String username)
      throws SQLException
  {
    if(username == null || username.trim().length() == 0)
      throw new SQLException("Username Oracle é obrigatório.");

    PreparedStatement statement = null;
    ResultSet resultSet = null;
    try
    {
      statement = connectionLogin.prepareStatement(String.format(CONSULTAR_FUNCIONARIO_LOCAL, no.getOwner()));
      statement.setString(1, username);
      resultSet = statement.executeQuery();

      if(!resultSet.next())
        throw new SQLException("Funcionário não encontrado neste nó.");

      int codFuncionario = resultSet.getInt(1);
      int codigoNo = resultSet.getInt(2);
      String nomeFuncionario = resultSet.getString(3);
      String estadoFuncionario = resultSet.getString(4);

      if(resultSet.next())
        throw new SQLException("Username Oracle duplicado neste nó.");

      if(!isEstadoActivo(estadoFuncionario))
        throw new SQLException("Funcionário Inactivo.");

      NoBD noLocal = NoBD.porCodigo(codigoNo);
      if(noLocal != no)
        throw new SQLException("Funcionário não encontrado neste nó.");

      return new DadosFuncionario(codFuncionario, no, no.getNome());
    } finally
    {
      fecharResultSet(resultSet);
      fecharStatement(statement);
    }
  }

  private SessaoAberta abrirSessaoAdministrador(Connection connection, String username) throws SQLException
  {
    CallableStatement statement = null;
    try
    {
      statement = connection.prepareCall(ABRIR_SESSAO_ADMINISTRADOR);
      preencherParametrosAbrirSessao(statement, username, "ONLINE");
      statement.execute();

      int podeEntrar = statement.getInt(7);
      String mensagem = statement.getString(8);
      if(podeEntrar != 1)
        throw new SQLException(mensagem == null ? "Não foi possível abrir sessão administrativa." : mensagem);

      return new SessaoAberta(statement.getInt(3), statement.getInt(4), statement.getInt(5), null, mensagem);
    } finally
    {
      fecharStatement(statement);
    }
  }

  private SessaoAberta abrirSessaoLocal(Connection connection, String username, String modoLogin) throws SQLException
  {
    CallableStatement statement = null;
    try
    {
      statement = connection.prepareCall(ABRIR_SESSAO_LOCAL);
      preencherParametrosAbrirSessao(statement, username, modoLogin);
      statement.execute();
      return lerSessaoAberta(statement, "Não foi possível abrir sessão local.");
    } finally
    {
      fecharStatement(statement);
    }
  }

  private void preencherParametrosAbrirSessao(CallableStatement statement, String username, String modoLogin)
      throws SQLException
  {
    statement.setString(1, username);
    statement.setString(2, modoLogin);
    statement.registerOutParameter(3, Types.NUMERIC);
    statement.registerOutParameter(4, Types.NUMERIC);
    statement.registerOutParameter(5, Types.NUMERIC);
    statement.registerOutParameter(6, Types.VARCHAR);
    statement.registerOutParameter(7, Types.NUMERIC);
    statement.registerOutParameter(8, Types.VARCHAR);
  }

  private SessaoAberta lerSessaoAberta(CallableStatement statement, String mensagemPadrao) throws SQLException
  {
    int podeEntrar = statement.getInt(7);
    String mensagem = statement.getString(8);
    if(podeEntrar != 1)
      throw new SQLException(mensagem == null ? mensagemPadrao : mensagem);

    return new SessaoAberta(statement.getInt(3), statement.getInt(4), statement.getInt(5), statement.getString(6),
        mensagem);
  }

  private boolean isEstadoActivo(String estado)
  {
    if(estado == null)
      return false;

    String estadoNormalizado = estado.trim().toUpperCase();
    return "ACTIVO".equals(estadoNormalizado) || "ATIVO".equals(estadoNormalizado);
  }

  private boolean isErroLoginOracle(SQLException ex)
  {
    return ex.getErrorCode() == 1017 || contemMensagem(ex, "invalid username/password")
        || contemMensagem(ex, "invalid username") || contemMensagem(ex, "senha inválida")
        || contemMensagem(ex, "credenciais inválidas");
  }

  private boolean isErroIndisponibilidade(SQLException ex)
  {
    int codigo = ex.getErrorCode();
    return codigo == 17002 || codigo == 17410 || codigo == 1089 || codigo == 1033 || codigo == 1034 || codigo == 3113
        || codigo == 3114 || codigo == 12505 || codigo == 12514 || codigo == 12519 || codigo == 12528
        || codigo == 12541 || codigo == 12154 || codigo == 2068 || contemMensagem(ex, "connection refused")
        || contemMensagem(ex, "listener") || contemMensagem(ex, "the network adapter could not establish")
        || contemMensagem(ex, "io error") || contemMensagem(ex, "no more data to read from socket")
        || contemMensagem(ex, "database not open") || contemMensagem(ex, "oracle not available")
        || contemMensagem(ex, "end-of-file on communication channel") || contemMensagem(ex, "not connected to oracle")
        || contemMensagem(ex, "preceding line from") || contemMensagem(ex, "dblink");
  }

  private boolean isFuncionarioNaoPertenceAoNo(SQLException ex)
  {
    return contemMensagem(ex, "não encontrado no fragmento local") || contemMensagem(ex, "não encontrado");
  }

  private boolean isErroSessaoAberta(SQLException ex)
  {
    return contemMensagem(ex, "sessão local aberta") || contemMensagem(ex, "sessão aberta")
        || contemMensagem(ex, "ja possui sessão");
  }

  private boolean isErroSessaoOracleActiva(SQLException ex)
  {
    return ex.getErrorCode() == 2391 || contemMensagem(ex, "ORA-02391")
        || contemMensagem(ex, "exceeded simultaneous SESSIONS_PER_USER limit");
  }

  private SQLException normalizarErroLogin(SQLException causa, NoBD noIdentificado)
  {
    if(isErroSessaoOracleActiva(causa) || isErroSessaoAberta(causa))
      return erroAmigavel(MSG_SESSAO_ORACLE_ACTIVA, causa);

    if(isErroLoginOracle(causa) || isFuncionarioNaoPertenceAoNo(causa))
      return erroAmigavel(MSG_CREDENCIAIS_INVALIDAS, causa);

    if(isErroIndisponibilidade(causa))
      return erroAmigavel(noIdentificado == null ? MSG_FALHA_CONEXAO : MSG_NO_INDISPONIVEL, causa);

    if(isMensagemTecnica(causa))
      return erroAmigavel(MSG_FALHA_CONEXAO, causa);

    return causa;
  }

  private SQLException erroAmigavel(String mensagem, SQLException causa)
  {
    registarErroTecnico(causa);
    return new SQLException(mensagem, causa);
  }

  private boolean contemMensagem(SQLException ex, String texto)
  {
    String textoLower = texto.toLowerCase();
    Throwable actual = ex;
    while(actual != null)
    {
      String mensagem = actual.getMessage();
      if(mensagem != null && mensagem.toLowerCase().contains(textoLower))
        return true;
      actual = actual.getCause();
    }

    SQLException proximo = ex.getNextException();
    while(proximo != null)
    {
      String mensagem = proximo.getMessage();
      if(mensagem != null && mensagem.toLowerCase().contains(textoLower))
        return true;
      proximo = proximo.getNextException();
    }

    return false;
  }

  private boolean isMensagemTecnica(SQLException ex)
  {
    return contemMensagem(ex, "ORA-") || contemMensagem(ex, "java.sql") || contemMensagem(ex, "Io exception")
        || contemMensagem(ex, "The Network Adapter") || contemMensagem(ex, "JDBC");
  }

  private void registarErroTecnico(SQLException ex)
  {
    String mensagem = MensagensInterface.formatarErro(ex);
    if(mensagem != null)
      System.err.println(mensagem);
  }

  private void fecharStatement(CallableStatement statement)
  {
    if(statement == null)
      return;
    try
    {
      statement.close();
    } catch(SQLException ignored)
    {}
  }

  private void fecharStatement(PreparedStatement statement)
  {
    if(statement == null)
      return;
    try
    {
      statement.close();
    } catch(SQLException ignored)
    {}
  }

  private void fecharResultSet(ResultSet resultSet)
  {
    if(resultSet == null)
      return;
    try
    {
      resultSet.close();
    } catch(SQLException ignored)
    {}
  }

  private static final class DadosFuncionario
  {
    private final int codFuncionario;
    private final NoBD no;
    private final String nomeNo;

    private DadosFuncionario(int codFuncionario, NoBD no, String nomeNo)
    {
      this.codFuncionario = codFuncionario;
      this.no = no;
      this.nomeNo = nomeNo;
    }
  }

  private static final class SessaoAberta
  {
    private final int codigoSessao;
    private final int codFuncionario;
    private final int codigoNo;
    private final String nomeFuncionario;
    private final String mensagem;

    private SessaoAberta(int codigoSessao, int codFuncionario, int codigoNo, String nomeFuncionario, String mensagem)
    {
      this.codigoSessao = codigoSessao;
      this.codFuncionario = codFuncionario;
      this.codigoNo = codigoNo;
      this.nomeFuncionario = nomeFuncionario;
      this.mensagem = mensagem;
    }
  }
}
