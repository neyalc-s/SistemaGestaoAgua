package Resources;

import java.awt.Component;
import java.sql.Connection;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import Resources.VerificadorConexaoRemota.NoRemoto;

public final class ConsultaRemotaUtils
{
  private ConsultaRemotaUtils()
  {}

  public static boolean prepararConsulta(Connection connection, NoRemoto[] nosRemotos, JTable tabela,
      JComponent[] componentes)
  {
    VerificadorConexaoRemota.ResultadoVerificacao resultado =
        VerificadorConexaoRemota.verificar(connection, nosRemotos);

    if(!resultado.isDisponivel())
    {
      bloquearConsulta(tabela, componentes, resultado.getMensagem());
      return false;
    }

    habilitarConsulta(tabela, componentes);
    return true;
  }

  public static void bloquearConsulta(JTable tabela, JComponent[] componentes, String mensagem)
  {
    BloqueadorOperacaoRemota.mostrarMensagemNaTabela(tabela, mensagem);
    definirComponentesHabilitados(componentes, false);
  }

  public static void habilitarConsulta(JTable tabela, JComponent[] componentes)
  {
    if(tabela != null)
    {
      tabela.setEnabled(true);
      tabela.setRowSelectionAllowed(true);
      tabela.setColumnSelectionAllowed(false);
    }
    definirComponentesHabilitados(componentes, true);
  }

  public static void definirComponentesHabilitados(JComponent[] componentes, boolean habilitado)
  {
    if(componentes == null)
      return;

    for(int i = 0; i < componentes.length; i++)
      definirComponenteHabilitado(componentes[i], habilitado);
  }

  private static void definirComponenteHabilitado(Component component, boolean habilitado)
  {
    if(component == null)
      return;

    if(component instanceof AbstractButton)
      component.setEnabled(habilitado);
    else if(component instanceof JTextField)
      component.setEnabled(habilitado);
    else if(component instanceof JTextArea)
      component.setEnabled(habilitado);
    else if(component instanceof JComboBox)
      component.setEnabled(habilitado);
    else if(component instanceof JSpinner)
      component.setEnabled(habilitado);
    else if(component instanceof JList)
      component.setEnabled(habilitado);
  }
}
