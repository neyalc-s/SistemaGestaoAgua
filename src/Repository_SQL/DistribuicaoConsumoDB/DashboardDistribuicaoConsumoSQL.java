package Repository_SQL.DistribuicaoConsumoDB;

public final class DashboardDistribuicaoConsumoSQL
{
  public static final String CONTAR_PONTOS =
      "SELECT COUNT(*) FROM VW_PONTO_DISTRIBUICAO";

  public static final String CONTAR_PONTOS_OPERACIONAIS =
      "SELECT COUNT(*) FROM VW_PONTO_DISTRIBUICAO WHERE estado_operacional_pd = 'Activo'";

  public static final String CONTAR_PONTOS_MANUTENCAO =
      "SELECT COUNT(*) FROM VW_PONTO_DISTRIBUICAO WHERE estado_operacional_pd = 'Em Manutencao'";

  public static final String CONTAR_RETIRADAS_HOJE =
      "SELECT COUNT(*) FROM VW_REGISTRO_CONSUMO WHERE TRUNC(CAST(data_hora_rc AS DATE)) = TRUNC(SYSDATE)";

  public static final String CONTAR_ABASTECIMENTOS_EM_CURSO =
      "SELECT COUNT(*) FROM VW_ABAST_EM_CURSO";

  public static final String CONTAR_HISTORICO_ABASTECIMENTO =
      "SELECT COUNT(*) FROM VW_HISTORICO_ABASTECIMENTO";

  public static final String CONTAR_MANUTENCOES =
      "SELECT COUNT(*) FROM VW_HISTORICO_MANUTENCAO";

  public static final String CONTAR_CONSUMOS =
      "SELECT COUNT(*) FROM VW_REGISTRO_CONSUMO";

  public static final String LISTAR_ULTIMOS_CONSUMOS =
      "SELECT principal, detalhe, valor FROM (SELECT 'Registro ' || codigo_rc principal, "
          + "'PD ' || codigo_pd || ' / Familia ' || codigo_fb detalhe, TO_CHAR(NVL(volume_retirado_rc, 0)) || ' L' valor "
          + "FROM VW_REGISTRO_CONSUMO ORDER BY data_hora_rc DESC) WHERE ROWNUM <= 8";

  public static final String LISTAR_MANUTENCOES_RECENTES =
      "SELECT principal, detalhe, valor FROM (SELECT 'PD ' || codigo_pd principal, "
          + "NVL(tipo_manutencao, 'Sem descricao') detalhe, TO_CHAR(data_manutencao, 'DD/MM/YYYY') valor "
          + "FROM VW_HISTORICO_MANUTENCAO ORDER BY data_manutencao DESC) WHERE ROWNUM <= 8";

  private DashboardDistribuicaoConsumoSQL()
  {}
}
