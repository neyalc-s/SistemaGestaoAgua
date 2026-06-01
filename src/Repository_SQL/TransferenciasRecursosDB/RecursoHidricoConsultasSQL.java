package Repository_SQL.TransferenciasRecursosDB;

import Resources.InterfaceGraficaUtils;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;

public final class RecursoHidricoConsultasSQL
{
  public static final String VISUALIZAR_RECURSOS_MEDIDAS =
    "SELECT * FROM vw_rh_med_prot_responsavel ORDER BY codigo_rh";

  public static final String VISUALIZAR_RECURSOS_PONTOS =
    "SELECT * FROM vw_rh_ponto_distribuicao ORDER BY codigo_rh";

  public static final String VISUALIZAR_QUALIDADE_AGUA =
    "SELECT codigo_rh, tipo_rh, localizacao_rh, volume_rh, sazonalidade_rh, vulnerabilidade_rh, "
      + "nivel_exploracao_rh, cod_qualidade_agua, codigo_parametro, nome_parametro, unidade_padrao, "
      + "valor, data_medicao, equipe_id, nome_analista FROM vw_rh_qualidade_agua ORDER BY data_medicao";

  public static final String VISUALIZAR_HISTORICO_ABASTECIMENTO =
    "SELECT cod_abastecimento, codigo_rh, tipo_rh, localizacao_rh, sazonalidade_rh, nivel_exploracao_rh, "
      + "codigo_pd, localizacao_pd, volume_abastecido, data_inicio, data_fim, duracao_horas, "
      + "estado_abastecimento FROM VW_HISTORICO_ABASTECIMENTO ORDER BY data_inicio DESC";

  private RecursoHidricoConsultasSQL()
  {}

  public static DefaultTableModel carregarModeloTabela(Connection connection, String sql) throws Exception
  {
    return InterfaceGraficaUtils.carregarModeloTabela(connection, sql);
  }
}
