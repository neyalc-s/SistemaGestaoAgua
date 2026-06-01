package Repository_SQL.AdministradorDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public final class RegistarFuncionarioSQL
{
  public static final String CONSULTAR_NOS_ACTIVOS =
      "SELECT codigo_no, nome_no FROM VW_NO_SISTEMA_ACTIVO ORDER BY codigo_no";

  public static final String CONSULTAR_FUNCIONARIOS =
      "SELECT cod_funcionario, nome_funcionario, username_oracle, nome_no, estado_funcionario "
          + "FROM VW_FUNCIONARIO_LISTAGEM ORDER BY cod_funcionario DESC";

  public static final String REGISTAR_FUNCIONARIO =
      "{call PRC_INSERIR_FUNCIONARIO(?, ?, ?, ?, ?, ?)}";

  private RegistarFuncionarioSQL()
  {}

  public static List<NoSistema> carregarNos(Connection connection) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<NoSistema> nos = new ArrayList<NoSistema>();

    try
    {
      ps = connection.prepareStatement(CONSULTAR_NOS_ACTIVOS);
      rs = ps.executeQuery();

      while(rs.next())
        nos.add(new NoSistema(rs.getInt("codigo_no"), rs.getString("nome_no")));

      return nos;
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static List<FuncionarioResumo> carregarFuncionarios(Connection connection) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<FuncionarioResumo> funcionarios = new ArrayList<FuncionarioResumo>();

    try
    {
      ps = connection.prepareStatement(CONSULTAR_FUNCIONARIOS);
      rs = ps.executeQuery();

      while(rs.next())
        funcionarios.add(new FuncionarioResumo(rs.getInt("cod_funcionario"), rs.getString("nome_funcionario"),
            rs.getString("username_oracle"), rs.getString("nome_no"), rs.getString("estado_funcionario")));

      return funcionarios;
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static ResultadoRegisto registarFuncionario(Connection connection, String nomeFuncionario,
      String usernameOracle, int codigoNo) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_FUNCIONARIO);
      cs.setString(1, nomeFuncionario);
      cs.setString(2, usernameOracle);
      cs.setInt(3, codigoNo);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.NUMERIC);
      cs.registerOutParameter(6, Types.VARCHAR);
      cs.execute();

      int codigoFuncionario = cs.getInt(4);
      if(cs.wasNull())
        codigoFuncionario = 0;

      return new ResultadoRegisto(codigoFuncionario, cs.getInt(5) == 1, cs.getString(6));
    } finally
    {
      if(cs != null)
        cs.close();
    }
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

  public static final class NoSistema
  {
    public final int codigoNo;
    public final String nomeNo;

    public NoSistema(int codigoNo, String nomeNo)
    {
      this.codigoNo = codigoNo;
      this.nomeNo = nomeNo;
    }

    public String toString()
    {
      return codigoNo + " - " + nomeNo;
    }
  }

  public static final class FuncionarioResumo
  {
    public final int codigoFuncionario;
    public final String nomeFuncionario;
    public final String usernameOracle;
    public final String nomeNo;
    public final String estadoFuncionario;

    public FuncionarioResumo(int codigoFuncionario, String nomeFuncionario, String usernameOracle, String nomeNo,
        String estadoFuncionario)
    {
      this.codigoFuncionario = codigoFuncionario;
      this.nomeFuncionario = nomeFuncionario;
      this.usernameOracle = usernameOracle;
      this.nomeNo = nomeNo;
      this.estadoFuncionario = estadoFuncionario;
    }
  }

  public static final class ResultadoRegisto
  {
    public final int codigoFuncionario;
    public final boolean sucesso;
    public final String mensagem;

    public ResultadoRegisto(int codigoFuncionario, boolean sucesso, String mensagem)
    {
      this.codigoFuncionario = codigoFuncionario;
      this.sucesso = sucesso;
      this.mensagem = mensagem;
    }
  }
}
