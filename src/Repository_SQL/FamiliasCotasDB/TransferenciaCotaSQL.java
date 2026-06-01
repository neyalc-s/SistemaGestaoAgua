package Repository_SQL.FamiliasCotasDB;

public final class TransferenciaCotaSQL
{
  public static final String VISUALIZAR_FAMILIAS =
    "SELECT codigo_fb, nome_responsavel_fb, contacto_fb, num_membros_fb, perfil_socioeconomico_fb, estado_fb " +
      "FROM VW_FAMILIA_BENEFICIARIA ORDER BY codigo_fb";

  public static final String VISUALIZAR_COTAS_FAMILIA =
    "SELECT codigo_fb, nome_responsavel_fb, num_membros_fb, contacto_fb, perfil_socioeconomico_fb, " +
      "codigo_cota, volume_semanal_ca, cota_atribuida, periodo_validade_ca, ajuste_sazonal_ca, " +
      "transferencia_autorizada_ca, saldo_disponivel_ca, minimo_vital_familia, status_cota, " +
      "status_validade_cota, uso_percent, status_saldo " +
      "FROM VW_FAMILIA_COTA WHERE codigo_fb = ? ORDER BY codigo_cota DESC";

  public static final String TRANSFERIR_COTA = "{call PRC_DOAR_COTA_FAM(?,?,?,?,?,?,?,?)}";

  private TransferenciaCotaSQL()
  {}
}
