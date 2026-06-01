package Repository_SQL.TransferenciasRecursosDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class RegistarMedidaProteccaoSQL
{
  public static final String CONSULTAR_RESPONSAVEIS = "SELECT cod_responsavel, nome_responsavel "
      + "FROM VW_MEDIDA_PROT_RESPONSAVEL ORDER BY cod_responsavel";

  public static final String INSERIR_RESPONSAVEL = "{call PRC_INS_MED_PROT_RESP(?,?,?)}";
  public static final String PRE_VALIDAR_REGISTO = "{call PRC_PRE_REG_MED_PROT(?,?,?,?)}";
  public static final String REGISTAR_MEDIDA = "{call PRC_REG_MED_PROT(?,?,?,?,?)}";

  private RegistarMedidaProteccaoSQL()
  {}

  public static void carregarResponsaveis(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_RESPONSAVEIS);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("cod_responsavel"));
        linha.add(rs.getString("nome_responsavel"));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static ResultadoOperacao inserirResponsavel(Connection connection, String nomeResponsavel) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(INSERIR_RESPONSAVEL);
      cs.setString(1, nomeResponsavel);
      cs.registerOutParameter(2, Types.NUMERIC);
      cs.registerOutParameter(3, Types.VARCHAR);
      cs.execute();
      return new ResultadoOperacao(cs.getInt(2), cs.getString(3));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoValidacao preValidarRegisto(Connection connection, int codResponsavel, String descricao)
      throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_REGISTO);
      cs.setInt(1, codResponsavel);
      cs.setString(2, descricao);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.VARCHAR);
      cs.execute();
      return new ResultadoValidacao(cs.getInt(3) == 1, cs.getString(4));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoRegisto registarMedida(Connection connection, int codResponsavel, String descricao)
      throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(REGISTAR_MEDIDA);
      cs.setInt(1, codResponsavel);
      cs.setString(2, descricao);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();
      return new ResultadoRegisto(cs.getInt(3), cs.getInt(4) == 1, cs.getString(5));
    } finally
    {
      fechar(cs);
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

  private static void fechar(CallableStatement cs)
  {
    try
    {
      if(cs != null)
        cs.close();
    } catch(Exception ignored)
    {}
  }

  public static final class ResultadoOperacao
  {
    public final int sucesso;
    public final String mensagem;

    public ResultadoOperacao(int sucesso, String mensagem)
    {
      this.sucesso = sucesso;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoValidacao
  {
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoValidacao(boolean podeContinuar, String mensagem)
    {
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoRegisto
  {
    public final int codigoMedida;
    public final boolean registado;
    public final String mensagem;

    public ResultadoRegisto(int codigoMedida, boolean registado, String mensagem)
    {
      this.codigoMedida = codigoMedida;
      this.registado = registado;
      this.mensagem = mensagem;
    }
  }
}
