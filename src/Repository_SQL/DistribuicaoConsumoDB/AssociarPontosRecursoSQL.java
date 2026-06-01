package Repository_SQL.DistribuicaoConsumoDB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public final class AssociarPontosRecursoSQL
{
  public static final String CONSULTAR_PONTOS = "SELECT codigo_pd, localizacao_pd, tipo_infraestrutura_pd, "
      + "capacidade_armazenamento_pd, volume_actual_pd, estado_operacional_pd, codigo_rh_actual, tipo_rh, "
      + "localizacao_rh FROM VW_PONTOS_RECURSO_ASSOC ORDER BY codigo_pd";

  public static final String CONSULTAR_RECURSOS = "SELECT codigo_rh, tipo_rh, localizacao_rh, volume_rh, "
      + "sazonalidade_rh, nivel_exploracao_rh FROM VW_RECURSO_HIDRICO ORDER BY codigo_rh";

  public static final String PRE_VALIDAR_ASSOCIACAO = "{call PRC_PRE_ASSOC_PONTOS_RECURSO(?,?,?,?,?)}";
  public static final String ASSOCIAR_PONTOS = "{call PRC_ASSOC_PONTOS_RECURSO(?,?,?,?,?)}";

  private AssociarPontosRecursoSQL()
  {}

  public static void carregarPontos(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_PONTOS, new String[]
      {
          "codigo_pd", "localizacao_pd", "tipo_infraestrutura_pd", "capacidade_armazenamento_pd",
          "volume_actual_pd", "estado_operacional_pd", "codigo_rh_actual", "tipo_rh", "localizacao_rh"
      });
  }

  public static void carregarRecursos(Connection connection, DefaultTableModel modeloTabela) throws Exception
  {
    carregarTabela(connection, modeloTabela, CONSULTAR_RECURSOS, new String[]
      {
          "codigo_rh", "tipo_rh", "localizacao_rh", "volume_rh", "sazonalidade_rh", "nivel_exploracao_rh"
      });
  }

  private static void carregarTabela(Connection connection, DefaultTableModel modeloTabela, String sql,
      String[] colunas) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      modeloTabela.setRowCount(0);
      ps = connection.prepareStatement(sql);
      rs = ps.executeQuery();

      while(rs.next())
      {
        Vector<Object> linha = new Vector<Object>();
        for(String coluna : colunas)
          linha.add(rs.getObject(coluna));
        modeloTabela.addRow(linha);
      }
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static ResultadoPreValidacao preValidarAssociacao(Connection connection, List<Integer> codigosPontos,
      int codigoRh) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(PRE_VALIDAR_ASSOCIACAO);
      cs.setString(1, formatarCodigos(codigosPontos));
      cs.setInt(2, codigoRh);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();

      return new ResultadoPreValidacao(cs.getInt(3), cs.getInt(4) == 1, cs.getString(5));
    } finally
    {
      fechar(cs);
    }
  }

  public static ResultadoAssociacao associarPontos(Connection connection, List<Integer> codigosPontos,
      int codigoRh) throws Exception
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(ASSOCIAR_PONTOS);
      cs.setString(1, formatarCodigos(codigosPontos));
      cs.setInt(2, codigoRh);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.registerOutParameter(4, Types.NUMERIC);
      cs.registerOutParameter(5, Types.VARCHAR);
      cs.execute();

      return new ResultadoAssociacao(cs.getInt(3), cs.getInt(4) == 1, cs.getString(5));
    } finally
    {
      fechar(cs);
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

  public static final class ResultadoPreValidacao
  {
    public final int totalPontos;
    public final boolean podeContinuar;
    public final String mensagem;

    public ResultadoPreValidacao(int totalPontos, boolean podeContinuar, String mensagem)
    {
      this.totalPontos = totalPontos;
      this.podeContinuar = podeContinuar;
      this.mensagem = mensagem;
    }
  }

  public static final class ResultadoAssociacao
  {
    public final int totalAssociados;
    public final boolean associou;
    public final String mensagem;

    public ResultadoAssociacao(int totalAssociados, boolean associou, String mensagem)
    {
      this.totalAssociados = totalAssociados;
      this.associou = associou;
      this.mensagem = mensagem;
    }
  }
}
