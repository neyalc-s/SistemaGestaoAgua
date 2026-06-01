package Repository_SQL.FamiliasCotasDB;

public final class DashboardFamiliasCotasSQL
{
  public static final String CONTAR_FAMILIAS =
      "SELECT COUNT(*) FROM VW_FAMILIA_BENEFICIARIA";

  public static final String CONTAR_FAMILIAS_COM_PONTO =
      "SELECT COUNT(*) FROM VW_FAMILIA_BENEFICIARIA WHERE codigo_pd IS NOT NULL";

  public static final String CONTAR_COTAS_VALIDAS =
      "SELECT COUNT(*) FROM VW_COTA_AGUA_STATUS WHERE status_validade = 'VALIDA'";

  public static final String CONTAR_COTAS_EXPIRADAS =
      "SELECT COUNT(*) FROM VW_COTA_AGUA_STATUS WHERE status_validade = 'EXPIRADA'";

  public static final String CONTAR_COTAS_SALDO_BAIXO =
      "SELECT COUNT(*) FROM VW_COTA_AGUA_STATUS WHERE NVL(saldo_disponivel_ca, 0) <= NVL(volume_semanal_ca, 0) * 0.2";

  public static final String CONTAR_NECESSIDADES =
      "SELECT COUNT(*) FROM VW_NECESSIDADE";

  public static final String CONTAR_ASSOCIACOES_NECESSIDADE =
      "SELECT COUNT(*) FROM VW_NECESS_ASSOC_FAMILIA";

  public static final String CONTAR_LOCALIZACOES =
      "SELECT COUNT(*) FROM VW_LOCALIZACAO";

  public static final String LISTAR_ULTIMAS_FAMILIAS =
      "SELECT principal, detalhe, valor FROM (SELECT nome_responsavel_fb principal, "
          + "'Codigo ' || codigo_fb detalhe, NVL(estado_fb, 'Sem estado') valor "
          + "FROM VW_FAMILIA_BENEFICIARIA ORDER BY codigo_fb DESC) WHERE ROWNUM <= 8";

  public static final String LISTAR_COTAS_MENOR_SALDO =
      "SELECT principal, detalhe, valor FROM (SELECT 'Familia ' || codigo_fb principal, "
          + "'Cota ' || codigo_cota detalhe, TO_CHAR(NVL(saldo_disponivel_ca, 0)) || ' L' valor "
          + "FROM VW_COTA_AGUA_STATUS ORDER BY NVL(saldo_disponivel_ca, 0) ASC) WHERE ROWNUM <= 8";

  private DashboardFamiliasCotasSQL()
  {}
}
