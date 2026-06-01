package Repository_SQL.TransferenciasRecursosDB;

public final class TransferenciaCotaSQL
{
  public static final String VISUALIZAR_FAMILIAS =
    "SELECT codigo_fb, nome_responsavel_fb, contacto_fb, num_membros_fb, perfil_socioeconomico_fb, estado_fb " +
      "FROM VW_FAMILIA_BENEFICIARIA ORDER BY codigo_fb";

  public static final String VISUALIZAR_COTAS_FAMILIA =
    "SELECT codigo_fb, nome_responsavel_fb, num_membros_fb, contacto_fb, perfil_socioeconomico_fb, estado_fb, " +
      "codigo_cota, cota_atribuida, periodo_validade_ca, ajuste_sazonal_ca, transferencia_autorizada_ca, " +
      "saldo_disponivel_ca, minimo_vital_familia, status_cota, status_validade_cota, uso_percent, status_saldo " +
      "FROM vw_familia_cota WHERE codigo_fb = ? ORDER BY codigo_cota DESC";

  public static final String TRANSFERIR_COTA = "{call prc_transferir_cota(?,?,?,?,?,?,?,?)}";

  private TransferenciaCotaSQL()
  {
  }
}
