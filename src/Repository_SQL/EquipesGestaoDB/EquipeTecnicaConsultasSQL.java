package Repository_SQL.EquipesGestaoDB;

import Resources.InterfaceGraficaUtils;
import java.sql.Connection;
import javax.swing.table.DefaultTableModel;

public final class EquipeTecnicaConsultasSQL
{
  public static final String VISUALIZAR_TECNICOS_MANUTENCAO =
    "SELECT * FROM vw_tecnico_manutencao ORDER BY equipe_id";

  public static final String VISUALIZAR_ANALISTAS_QUALIDADE =
    "SELECT equipe_id, nome, area_actuacao, nivel_formacao, contacto, supervisor_responsavel, "
      + "codigo_parametro, especialidade_analise, frequencia_amostragem "
      + "FROM vw_analista_qualidade ORDER BY equipe_id";

  public static final String VISUALIZAR_EDUCADORES_COMUNITARIOS =
    "SELECT * FROM vw_educador_comunitario ORDER BY equipe_id";

  public static final String VISUALIZAR_EQUIPE_TECNICA =
    "SELECT * FROM vw_equipe_tecnica ORDER BY equipe_id";

  public static final String VISUALIZAR_EQUIPE_COM_PDS =
    "SELECT * FROM vw_equipe_tecnica_com_pds ORDER BY equipe_id";

  public static final String VISUALIZAR_HISTORICO_EQUIPE =
    "SELECT * FROM vw_historico_equipe_tecnica ORDER BY equipe_id";

  private EquipeTecnicaConsultasSQL()
  {
  }

  public static DefaultTableModel carregarModeloTabela(Connection connection, String sql) throws Exception
  {
    return InterfaceGraficaUtils.carregarModeloTabela(connection, sql);
  }
}
