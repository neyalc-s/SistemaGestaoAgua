package Repository_SQL.TransferenciasRecursosDB;

public final class DashboardTransferenciasRecursosSQL
{
  public static final String CONTAR_RECURSOS =
      "SELECT COUNT(*) FROM VW_RECURSO_HIDRICO";

  public static final String CONTAR_MEDICOES =
      "SELECT COUNT(*) FROM VW_MEDICAO_QUALIDADE_AGUA";

  public static final String CONTAR_ALERTAS =
      "SELECT COUNT(*) FROM VW_ALERTA_QUALIDADE_AGUA";

  public static final String CONTAR_TRANSFERENCIAS =
      "SELECT COUNT(*) FROM VW_TRANSFERENCIA_COTA";

  public static final String CONTAR_MEDIDAS =
      "SELECT COUNT(*) FROM VW_MEDIDA_PROTECCAO";

  public static final String CONTAR_RECURSOS_PROTEGIDOS =
      "SELECT COUNT(DISTINCT codigo_rh) FROM VW_REC_APLICA_MED_PROT";

  public static final String CONTAR_RESPONSAVEIS =
      "SELECT COUNT(*) FROM VW_MEDIDA_PROT_RESPONSAVEL";

  public static final String CONTAR_PARAMETROS =
      "SELECT COUNT(*) FROM VW_PARAMETRO_QUALIDADE";

  public static final String LISTAR_ULTIMAS_MEDICOES =
      "SELECT principal, detalhe, valor FROM (SELECT 'RH ' || codigo_rh principal, "
          + "'Parametro ' || codigo_parametro detalhe, TO_CHAR(valor) || ' - ' || TO_CHAR(data_medicao, 'DD/MM/YYYY') valor "
          + "FROM VW_MEDICAO_QUALIDADE_AGUA ORDER BY data_medicao DESC) WHERE ROWNUM <= 8";

  public static final String LISTAR_TRANSFERENCIAS_RECENTES =
      "SELECT principal, detalhe, valor FROM (SELECT 'Transferencia ' || codigo_tc principal, "
          + "'Fam. ' || cod_fam_doadora_tc || ' -> ' || cod_fam_receptora_tc detalhe, "
          + "TO_CHAR(volume_cedido_tc) || ' L' valor FROM VW_TRANSFERENCIA_COTA ORDER BY data_aprovacao_tc DESC NULLS LAST) "
          + "WHERE ROWNUM <= 8";

  private DashboardTransferenciasRecursosSQL()
  {}
}
