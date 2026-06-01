package Repository_SQL.FamiliasCotasDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class AssociarFamiliasPontoSQL
{
  public static final String CONSULTAR_FAMILIAS = "SELECT codigo_fb, nome_responsavel_fb, contacto_fb, estado_fb, codigo_pd, aldeia "
      + "FROM vw_familias_por_ponto ORDER BY codigo_fb";

  public static final String CONSULTAR_PONTOS = "SELECT codigo_pd, localizacao_pd, tipo_infraestrutura_pd, "
      + "capacidade_armazenamento_pd, volume_actual_pd, estado_operacional_pd "
      + "FROM VW_PONTO_DISTRIBUICAO ORDER BY codigo_pd";

  public static final String PRE_VALIDAR_ASSOCIACAO = "{call PRC_PRE_ASSOC_FAMILIAS_PONTO(?,?,?,?,?)}";
  public static final String ASSOCIAR_FAMILIAS = "{call PRC_ASSOC_FAMILIAS_PONTO(?,?,?,?,?)}";

  private AssociarFamiliasPontoSQL()
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
        linha.add(rs.getInt("codigo_fb"));
        linha.add(rs.getString("nome_responsavel_fb"));
        linha.add(rs.getString("contacto_fb"));
        linha.add(rs.getString("estado_fb"));
        linha.add(rs.getInt("codigo_pd"));
        linha.add(rs.getString("aldeia"));
        modeloTabela.addRow(linha);
      }
    } finally
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
  }

  public static void carregarPontos(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(CONSULTAR_PONTOS);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        linha.add(rs.getInt("codigo_pd"));
        linha.add(rs.getString("localizacao_pd"));
        linha.add(rs.getString("tipo_infraestrutura_pd"));
        linha.add(rs.getObject("capacidade_armazenamento_pd"));
        linha.add(rs.getObject("volume_actual_pd"));
        linha.add(rs.getString("estado_operacional_pd"));
        modeloTabela.addRow(linha);
      }
    } finally
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
  }

  public static ResultadoPreValidacao preValidarAssociacao(Connection connection, List<Integer> codigosFamilias,
      int codigoPonto) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_ASSOCIACAO);
      cs.setString(1, formatarCodigos(codigosFamilias));
      cs.setInt(2, codigoPonto);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();

      return new ResultadoPreValidacao(cs.getInt(3), cs.getInt(4) == 1, cs.getString(5));
    } finally
    {
      try
      {
        if(cs != null)
          cs.close();
      } catch(Exception ignored)
      {}
    }
  }

  public static ResultadoAssociacao associarFamilias(Connection connection, List<Integer> codigosFamilias,
      int codigoPonto) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(ASSOCIAR_FAMILIAS);
      cs.setString(1, formatarCodigos(codigosFamilias));
      cs.setInt(2, codigoPonto);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();

      return new ResultadoAssociacao(cs.getInt(3), cs.getInt(4) == 1, cs.getString(5));
    } finally
    {
      try
      {
        if(cs != null)
          cs.close();
      } catch(Exception ignored)
      {}
    }
  }

  private static String formatarCodigos(List<Integer> codigos)
  {
    if(codigos == null || codigos.isEmpty())
      return null;

    StringBuilder texto = new StringBuilder();

    for(int i = 0; i < codigos.size(); i++)
    {
      if(i > 0)
        texto.append(",");

      texto.append(codigos.get(i));
    }

    return texto.toString();
  }

  public static final class ResultadoPreValidacao
  {
    public final int totalFamilias;
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoPreValidacao(int totalFamilias, boolean podeContinuar, String mensagem)
    {
      this.totalFamilias = totalFamilias;
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoAssociacao
  {
    public final int totalAssociadas;
    public final boolean associou;
    public final String mensagem;

    public ResultadoAssociacao(int totalAssociadas, boolean associou, String mensagem)
    {
      this.totalAssociadas = totalAssociadas;
      this.associou = associou;
      this.mensagem = mensagem;
    }
  }
}
