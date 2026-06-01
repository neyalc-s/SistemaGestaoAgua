package NoAdministrador_Interface;

import java.sql.Connection;

/**
 * Classe de compatibilidade para evitar duplicacao com
 * View_Interface_AdministradorDB.JPanel_AuditoriaSessoesFuncionarios.
 *
 * A implementacao real do painel fica em View_Interface_AdministradorDB.
 * Este ficheiro existe apenas para manter compatibilidade com codigo antigo
 * que ainda possa referenciar NoAdministrador_Interface.JPanel_AuditoriaSessoesFuncionarios.
 */
public class JPanel_AuditoriaSessoesFuncionarios
    extends View_Interface_AdministradorDB.JPanel_AuditoriaSessoesFuncionarios
{
  public JPanel_AuditoriaSessoesFuncionarios(Connection connection)
  {
    super(connection);
  }

  public JPanel_AuditoriaSessoesFuncionarios(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    super(connection, carregarTabelasAutomaticamente);
  }
}
