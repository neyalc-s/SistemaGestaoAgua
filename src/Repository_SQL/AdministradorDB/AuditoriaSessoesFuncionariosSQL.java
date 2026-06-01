
package Repository_SQL.AdministradorDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class AuditoriaSessoesFuncionariosSQL
{
  public static final String REFRESH_AUDITORIA =
      "{call PRC_REFRESH_AUDITORIA_SESSOES(?, ?)}";

  public static final String CONSULTAR_NOS_AUDITORIA =
      "SELECT DISTINCT nome_no FROM VW_AUDITORIA_SESSOES_ADMIN ORDER BY nome_no";

  public static final String CONSULTAR_AUDITORIA_BASE =
      "SELECT codigo_sessao, cod_funcionario, nome_funcionario, username_oracle, nome_no, "
          + "data_inicio, data_fim, estado_sessao, modo_login "
          + "FROM VW_AUDITORIA_SESSOES_ADMIN WHERE 1 = 1 ";

  public static final String FILTRAR_POR_NO =
      "AND nome_no = ? ";

  public static final String FILTRAR_POR_ESTADO =
      "AND estado_sessao = ? ";

  public static final String FILTRAR_POR_NOME_FUNCIONARIO =
      "AND UPPER(nome_funcionario) LIKE ? ";

  public static final String FILTRAR_POR_USERNAME_ORACLE =
      "AND UPPER(username_oracle) LIKE ? ";

  public static final String FILTRAR_POR_CODIGO_FUNCIONARIO =
      "AND TO_CHAR(cod_funcionario) LIKE ? ";

  public static final String ORDENAR_AUDITORIA =
      "ORDER BY data_inicio DESC, codigo_sessao DESC";

  private AuditoriaSessoesFuncionariosSQL()
  {}

  public static ResultadoRefresh actualizarAuditoria(Connection connection) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REFRESH_AUDITORIA);
      cs.registerOutParameter(1, Types.NUMERIC);
      cs.registerOutParameter(2, Types.VARCHAR);
      cs.execute();
      return new ResultadoRefresh(cs.getInt(1) == 1, cs.getString(2));
    } finally
    {
      fechar(cs);
    }
  }

  public static List<SessaoAuditoria> pesquisar(Connection connection, FiltroAuditoria filtro) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<Object> params = new ArrayList<Object>();
    StringBuilder sql = new StringBuilder();

    sql.append(CONSULTAR_AUDITORIA_BASE);

    if(filtro != null && temTexto(filtro.nomeNo) && !"Todos".equalsIgnoreCase(filtro.nomeNo))
    {
      sql.append(FILTRAR_POR_NO);
      params.add(filtro.nomeNo);
    }

    if(filtro != null && temTexto(filtro.estadoSessao) && !"Todos".equalsIgnoreCase(filtro.estadoSessao))
    {
      sql.append(FILTRAR_POR_ESTADO);
      params.add(filtro.estadoSessao.toUpperCase());
    }

    if(filtro != null && temTexto(filtro.textoPesquisa))
      adicionarFiltroPesquisa(sql, params, filtro);

    sql.append(ORDENAR_AUDITORIA);

    try
    {
      ps = connection.prepareStatement(sql.toString());
      for(int i = 0; i < params.size(); i++)
        ps.setObject(i + 1, params.get(i));

      rs = ps.executeQuery();
      List<SessaoAuditoria> sessoes = new ArrayList<SessaoAuditoria>();
      while(rs.next())
        sessoes.add(new SessaoAuditoria(rs.getInt("codigo_sessao"), rs.getInt("cod_funcionario"),
            rs.getString("nome_funcionario"), rs.getString("username_oracle"), rs.getString("nome_no"),
            rs.getTimestamp("data_inicio"), rs.getTimestamp("data_fim"), rs.getString("estado_sessao"),
            rs.getString("modo_login")));
      return sessoes;
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static List<SessaoAuditoria> pesquisarPorNo(Connection connection, FiltroAuditoria filtro, String nomeNo)
      throws Exception
  {
    if(filtro == null)
      return pesquisar(connection, new FiltroAuditoria(null, "Codigo Funcionario", nomeNo, "Todos"));

    return pesquisar(connection, new FiltroAuditoria(filtro.textoPesquisa, filtro.criterioPesquisa, nomeNo,
        filtro.estadoSessao));
  }

  public static List<String> carregarNos(Connection connection) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<String> nos = new ArrayList<String>();

    try
    {
      ps = connection.prepareStatement(CONSULTAR_NOS_AUDITORIA);
      rs = ps.executeQuery();
      while(rs.next())
        nos.add(rs.getString("nome_no"));
      return nos;
    } finally
    {
      fechar(rs, ps);
    }
  }

  private static void adicionarFiltroPesquisa(StringBuilder sql, List<Object> params, FiltroAuditoria filtro)
  {
    String criterio = filtro.criterioPesquisa == null ? "Codigo Funcionario" : filtro.criterioPesquisa.trim();
    String texto = filtro.textoPesquisa.trim();

    if("Nome Funcionario".equalsIgnoreCase(criterio) || "Nome Funcionário".equalsIgnoreCase(criterio))
    {
      sql.append(FILTRAR_POR_NOME_FUNCIONARIO);
      params.add("%" + texto.toUpperCase() + "%");
    } else if("Username Oracle".equalsIgnoreCase(criterio))
    {
      sql.append(FILTRAR_POR_USERNAME_ORACLE);
      params.add("%" + texto.toUpperCase() + "%");
    } else
    {
      sql.append(FILTRAR_POR_CODIGO_FUNCIONARIO);
      params.add("%" + texto + "%");
    }
  }

  private static boolean temTexto(String valor)
  {
    return valor != null && valor.trim().length() > 0;
  }

  private static void fechar(CallableStatement cs)
  {
    try
    {
      if(cs != null)
        cs.close();
    } catch(Exception ignored)
    {}
  }

  private static void fechar(ResultSet rs, PreparedStatement ps)
  {
    try
    {
      if(rs != null)
        rs.close();
    } catch(Exception ignored)
    {}

    try
    {
      if(ps != null)
        ps.close();
    } catch(Exception ignored)
    {}
  }

  public static final class FiltroAuditoria
  {
    public final String textoPesquisa;
    public final String criterioPesquisa;
    public final String nomeNo;
    public final String estadoSessao;

    public FiltroAuditoria(String textoPesquisa, String criterioPesquisa, String nomeNo, String estadoSessao)
    {
      this.textoPesquisa = textoPesquisa;
      this.criterioPesquisa = criterioPesquisa;
      this.nomeNo = nomeNo;
      this.estadoSessao = estadoSessao;
    }
  }

  public static final class SessaoAuditoria
  {
    public final int codigoSessao;
    public final int codigoFuncionario;
    public final String nomeFuncionario;
    public final String usernameOracle;
    public final String nomeNo;
    public final java.util.Date dataInicio;
    public final java.util.Date dataFim;
    public final String estadoSessao;
    public final String modoLogin;

    public SessaoAuditoria(int codigoSessao, int codigoFuncionario, String nomeFuncionario, String usernameOracle,
        String nomeNo, java.util.Date dataInicio, java.util.Date dataFim, String estadoSessao, String modoLogin)
    {
      this.codigoSessao = codigoSessao;
      this.codigoFuncionario = codigoFuncionario;
      this.nomeFuncionario = nomeFuncionario;
      this.usernameOracle = usernameOracle;
      this.nomeNo = nomeNo;
      this.dataInicio = dataInicio;
      this.dataFim = dataFim;
      this.estadoSessao = estadoSessao;
      this.modoLogin = modoLogin;
    }
  }

  public static final class ResultadoRefresh
  {
    public final boolean sucesso;
    public final String mensagem;

    public ResultadoRefresh(boolean sucesso, String mensagem)
    {
      this.sucesso = sucesso;
      this.mensagem = mensagem;
    }
  }
}
