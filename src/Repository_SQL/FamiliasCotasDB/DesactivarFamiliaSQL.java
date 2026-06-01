package Repository_SQL.FamiliasCotasDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class DesactivarFamiliaSQL
{
  public static final String CONSULTAR_FAMILIAS = "SELECT codigo_fb, nome_responsavel_fb, contacto_fb, "
      + "num_membros_fb, estado_fb, aldeia, coordenadas_gps "
      + "FROM vw_localizacao_todas_familias ORDER BY codigo_fb";

  public static final String PRE_VALIDAR_DESACTIVACAO = "{call PRC_PRE_DESACT_FAMILIA(?,?,?,?)}";
  public static final String DESACTIVAR_FAMILIA = "{call PRC_DESACT_FAMILIA(?,?,?,?)}";

  private DesactivarFamiliaSQL()
  {}

  public static void carregarFamilias(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_FAMILIAS);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getObject("codigo_fb"));
        linha.add(rs.getObject("nome_responsavel_fb"));
        linha.add(rs.getObject("contacto_fb"));
        linha.add(rs.getObject("num_membros_fb"));
        linha.add(rs.getObject("estado_fb"));
        linha.add(rs.getObject("aldeia"));
        linha.add(rs.getObject("coordenadas_gps"));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static ResultadoOperacao preValidarDesactivacao(Connection connection, int codigoFamilia) throws Exception
  {
    return executarProcedure(connection, PRE_VALIDAR_DESACTIVACAO, codigoFamilia);
  }

  public static ResultadoOperacao desactivarFamilia(Connection connection, int codigoFamilia) throws Exception
  {
    return executarProcedure(connection, DESACTIVAR_FAMILIA, codigoFamilia);
  }

  private static ResultadoOperacao executarProcedure(Connection connection, String chamada, int codigoFamilia)
      throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(chamada);
      cs.setInt(1, codigoFamilia);
      cs.registerOutParameter(2, Types.VARCHAR);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.VARCHAR);
      cs.execute();
      return new ResultadoOperacao(cs.getString(2), cs.getInt(3) == 1, cs.getString(4));
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
    public final String accao;
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoOperacao(String accao, boolean podeContinuar, String mensagem)
    {
      this.accao = accao;
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }
}
