package Repository_SQL.AdministradorDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class DashboardSistemaSQL
{
  public static final String CONTAR_FAMILIAS =
      "SELECT COUNT(*) FROM VW_FAMILIA_BENEFICIARIA";

  public static final String CONTAR_PONTOS_ACTIVOS =
      "SELECT COUNT(*) FROM VW_PONTO_DISTRIBUICAO WHERE UPPER(TRIM(estado_operacional_pd)) IN ('ACTIVO')";

  public static final String CONTAR_RECURSOS =
      "SELECT COUNT(*) FROM VW_RECURSO_HIDRICO";

  public static final String CONTAR_COTAS_VALIDAS =
      "SELECT COUNT(*) FROM VW_COTA_AGUA_STATUS WHERE status_validade = 'VALIDA'";

  public static final String SOMAR_CONSUMO_ULTIMOS_7_DIAS =
      "SELECT NVL(SUM(volume_retirado_rc), 0) FROM VW_REGISTRO_CONSUMO "
          + "WHERE data_hora_rc >= CAST(SYSDATE - 7 AS TIMESTAMP)";

  public static final String SOMAR_VOLUME_PONTOS =
      "SELECT NVL(SUM(volume_actual_pd), 0) FROM VW_PONTO_DISTRIBUICAO";

  public static final String CONTAR_ABASTECIMENTOS_EM_CURSO =
      "SELECT COUNT(*) FROM VW_ABAST_EM_CURSO";

  public static final String CONTAR_MEDICOES_ULTIMOS_7_DIAS =
      "SELECT COUNT(*) FROM VW_MEDICAO_QUALIDADE_AGUA WHERE data_medicao >= SYSDATE - 7";

  public static final String GRAFICO_PONTOS_POR_ESTADO =
      "SELECT NVL(estado_operacional_pd, 'Sem estado'), COUNT(*) FROM VW_PONTO_DISTRIBUICAO "
          + "GROUP BY NVL(estado_operacional_pd, 'Sem estado') ORDER BY 2 DESC";

  public static final String GRAFICO_COTAS_POR_VALIDADE =
      "SELECT status_validade, COUNT(*) FROM VW_COTA_AGUA_STATUS GROUP BY status_validade ORDER BY 2 DESC";

  public static final String GRAFICO_RECURSOS_POR_TIPO =
      "SELECT NVL(tipo_rh, 'Sem tipo'), COUNT(*) FROM VW_RECURSO_HIDRICO "
          + "GROUP BY NVL(tipo_rh, 'Sem tipo') ORDER BY 2 DESC";

  public static final String GRAFICO_CONSUMO_POR_PONTO =
      "SELECT * FROM (SELECT 'PD ' || codigo_pd, SUM(volume_retirado_rc) FROM VW_REGISTRO_CONSUMO "
          + "GROUP BY codigo_pd ORDER BY 2 DESC) WHERE ROWNUM <= 5";

  public static final String LISTAR_COTAS_SALDO_BAIXO =
      "SELECT * FROM (SELECT 'Familia ' || codigo_fb, 'Cota ' || codigo_cota, "
          + "TO_CHAR(saldo_disponivel_ca) || ' L' FROM vw_familia_cota "
          + "WHERE status_saldo IN ('BAIXO', 'ZERADO')) WHERE ROWNUM <= 5";

  public static final String LISTAR_ABASTECIMENTOS_EM_CURSO =
      "SELECT * FROM (SELECT 'Abast. ' || cod_abastecimento, 'PD ' || codigo_pd || ' / RH ' || codigo_rh, "
          + "TO_CHAR(volume_abastecido) || ' L' FROM VW_ABAST_EM_CURSO ORDER BY data_inicio DESC) "
          + "WHERE ROWNUM <= 5";

  public static final String LISTAR_ULTIMAS_MEDICOES =
      "SELECT * FROM (SELECT nome_parametro, 'RH ' || codigo_rh || ' - ' || nome_analista, "
          + "TO_CHAR(valor) || ' ' || unidade_padrao FROM VW_MEDICAO_QUALIDADE_AGUA "
          + "ORDER BY data_medicao DESC) WHERE ROWNUM <= 5";

  private DashboardSistemaSQL()
  {}

  public static int contar(Connection connection, String sql) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = connection.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next())
        return rs.getInt(1);
      return 0;
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static double somar(Connection connection, String sql) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = connection.prepareStatement(sql);
      rs = ps.executeQuery();
      if(rs.next())
        return rs.getDouble(1);
      return 0.0;
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static List<ItemGrafico> carregarGrafico(Connection connection, String sql) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<ItemGrafico> itens = new ArrayList<ItemGrafico>();

    try
    {
      ps = connection.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next())
        itens.add(new ItemGrafico(rs.getString(1), rs.getDouble(2)));
      return itens;
    } finally
    {
      fechar(rs, ps);
    }
  }

  public static List<LinhaResumo> carregarLinhas(Connection connection, String sql) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<LinhaResumo> linhas = new ArrayList<LinhaResumo>();

    try
    {
      ps = connection.prepareStatement(sql);
      rs = ps.executeQuery();
      while(rs.next())
        linhas.add(new LinhaResumo(rs.getString(1), rs.getString(2), rs.getString(3)));
      return linhas;
    } finally
    {
      fechar(rs, ps);
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

  public static final class ItemGrafico
  {
    public final String rotulo;
    public final double valor;

    public ItemGrafico(String rotulo, double valor)
    {
      this.rotulo = rotulo;
      this.valor = valor;
    }
  }

  public static final class LinhaResumo
  {
    public final String principal;
    public final String detalhe;
    public final String valor;

    public LinhaResumo(String principal, String detalhe, String valor)
    {
      this.principal = principal;
      this.detalhe = detalhe;
      this.valor = valor;
    }
  }
}
