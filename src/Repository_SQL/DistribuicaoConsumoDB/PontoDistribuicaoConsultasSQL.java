package Repository_SQL.DistribuicaoConsumoDB;

public final class PontoDistribuicaoConsultasSQL
{
  public static final String VISUALIZAR_FAMILIA_TODAS = "SELECT * FROM vw_familias_por_ponto";

  public static final String VISUALIZAR_PONTO_REGISTRO = "SELECT * FROM vw_ponto_r_consumo";

  public static final String VISUALIZAR_PONTO_HISTORICO = "SELECT * FROM vw_historico_manutencao";

  private PontoDistribuicaoConsultasSQL()
  {}
}
