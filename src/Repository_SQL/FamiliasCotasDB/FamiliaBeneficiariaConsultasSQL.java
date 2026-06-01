package Repository_SQL.FamiliasCotasDB;

import Resources.InterfaceGraficaUtils;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;

public final class FamiliaBeneficiariaConsultasSQL
{
  public static final String VISUALIZAR_LOCALIZACAO_TODAS = "SELECT * FROM vw_localizacao_todas_familias";
  public static final String VISUALIZAR_TRANSFERENCIAS_TODAS = "SELECT * FROM vw_transferencias_familia";
  public static final String VISUALIZAR_COTAS_TODAS = "SELECT * FROM vw_cotas_familias";
  public static final String VISUALIZAR_NECESSIDADES_TODAS = "SELECT * FROM vw_necess_todas_familias";

  private FamiliaBeneficiariaConsultasSQL()
  {}

  public static DefaultTableModel carregarModeloTabela(Connection connection, final String sql) throws Exception
  {
    return InterfaceGraficaUtils.carregarModeloTabela(connection, sql);
  }
}
