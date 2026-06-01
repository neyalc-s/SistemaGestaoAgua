package Resources;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public final class BloqueadorOperacaoRemota
{
  private BloqueadorOperacaoRemota()
  {}

  public static final String MENSAGEM_PADRAO =
      "Não é possível realizar esta operação neste momento, tente de novo mais tarde.";

  public static void bloquear(JPanel painel, String mensagem)
  {
    if(painel == null)
      return;

    String texto = normalizarMensagem(mensagem);

    List<JTable> tabelas = new ArrayList<JTable>();
    recolherComponentes(painel, JTable.class, tabelas);

    for(int i = 0; i < tabelas.size(); i++)
      mostrarMensagemNaTabela(tabelas.get(i), texto);

    bloquearComponentes(painel);
    painel.revalidate();
    painel.repaint();
  }

  public static String normalizarMensagem(String mensagem)
  {
    if(mensagem == null || mensagem.trim().length() == 0)
      return MENSAGEM_PADRAO;
    return MENSAGEM_PADRAO;
  }

  public static void mostrarMensagemNaTabela(JTable tabela, String mensagem)
  {
    if(tabela == null)
      return;

    if(tabela.getModel() instanceof DefaultTableModel)
      ((DefaultTableModel) tabela.getModel()).setRowCount(0);

    InterfaceGraficaUtils.definirMensagemTabelaVazia(tabela, normalizarMensagem(mensagem));
    tabela.setEnabled(false);
    tabela.setRowSelectionAllowed(false);
    tabela.setColumnSelectionAllowed(false);
    tabela.repaint();
  }

  public static void bloquearComponentes(Component component)
  {
    if(component == null)
      return;

    if(component instanceof JTable)
    {
      JTable tabela = (JTable) component;
      tabela.setEnabled(false);
      tabela.setRowSelectionAllowed(false);
      tabela.setColumnSelectionAllowed(false);
    }
    else if(component instanceof AbstractButton)
    {
      AbstractButton botao = (AbstractButton) component;
      botao.setEnabled(ehBotaoVoltar(botao));
    }
    else if(component instanceof JTextField)
      component.setEnabled(false);
    else if(component instanceof JTextArea)
      component.setEnabled(false);
    else if(component instanceof JComboBox)
      component.setEnabled(false);
    else if(component instanceof JSpinner)
      component.setEnabled(false);
    else if(component instanceof JList)
      component.setEnabled(false);

    if(component instanceof Container)
    {
      Component[] filhos = ((Container) component).getComponents();
      for(int i = 0; i < filhos.length; i++)
        bloquearComponentes(filhos[i]);
    }
  }

  private static boolean ehBotaoVoltar(AbstractButton botao)
  {
    String texto = botao.getText();
    return texto != null && texto.trim().equalsIgnoreCase("Voltar");
  }

  private static <T> void recolherComponentes(Component component, Class<T> tipo, List<T> encontrados)
  {
    if(component == null)
      return;

    if(tipo.isInstance(component))
      encontrados.add(tipo.cast(component));

    if(component instanceof Container)
    {
      Component[] filhos = ((Container) component).getComponents();
      for(int i = 0; i < filhos.length; i++)
        recolherComponentes(filhos[i], tipo, encontrados);
    }
  }
}
