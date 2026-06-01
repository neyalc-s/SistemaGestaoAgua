package Resources;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class JPanel_OperacaoRemotaIndisponivel extends JPanel
{
  private final String tituloOperacao;
  private final String mensagem;
  private final Runnable voltarDashboard;

  public JPanel_OperacaoRemotaIndisponivel(String tituloOperacao, String mensagem, Runnable voltarDashboard)
  {
    this.tituloOperacao = tituloOperacao == null ? "Operação indisponível" : tituloOperacao;
    this.mensagem = mensagem == null || mensagem.trim().length() == 0
        ? "Não é possível realizar esta operação neste momento."
        : mensagem;
    this.voltarDashboard = voltarDashboard;

    construirInterface();
  }

  private void construirInterface()
  {
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    JPanel card = InterfaceGraficaUtils.criarCardArredondado(24, new BorderLayout(18, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(28, 34, 28, 34), null);

    JPanel topo = InterfaceGraficaUtils.criarTopo(tituloOperacao,
        "<html>Verificação de disponibilidade das bases de dados remotas.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO,
        InterfaceGraficaUtils.COR_TEXTO, InterfaceGraficaUtils.COR_SUBTEXTO);
    card.add(topo, BorderLayout.NORTH);

    JTable tabelaMensagem = criarTabelaMensagem();
    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaMensagem, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(900, 260));
    card.add(scroll, BorderLayout.CENTER);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 12, 0));

    JButton buttonVoltar = InterfaceGraficaUtils.criarBotao("Voltar", InterfaceGraficaUtils.FONT_BOTAO,
        new Color(120, 130, 140), InterfaceGraficaUtils.COR_BRANCO, new Dimension(130, 38));
    buttonVoltar.addActionListener(e ->
      {
        if(voltarDashboard != null)
          voltarDashboard.run();
      });

    JButton buttonProceder = InterfaceGraficaUtils.criarBotao("Proceder", InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(130, 38));
    buttonProceder.setEnabled(false);
    buttonProceder.setToolTipText("Esta operação depende de uma base de dados remota indisponível.");

    botoes.add(buttonVoltar);
    botoes.add(buttonProceder);
    card.add(botoes, BorderLayout.SOUTH);

    add(card, BorderLayout.CENTER);
  }

  private JTable criarTabelaMensagem()
  {
    DefaultTableModel modelo = new DefaultTableModel(new Object[] { "Estado da Operação" }, 0)
    {
      public boolean isCellEditable(int row, int column)
      {
        return false;
      }
    };

    modelo.addRow(new Object[] { mensagem });

    JTable tabela = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA, InterfaceGraficaUtils.FONT_HEADER,
        InterfaceGraficaUtils.COR_GRID_TABELA, InterfaceGraficaUtils.COR_SELECAO_TABELA,
        InterfaceGraficaUtils.COR_TEXTO);
    tabela.setModel(modelo);
    tabela.setRowHeight(130);
    tabela.setEnabled(false);
    tabela.setFocusable(false);
    tabela.setRowSelectionAllowed(false);
    tabela.setColumnSelectionAllowed(false);

    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
    renderer.setHorizontalAlignment(SwingConstants.CENTER);
    renderer.setVerticalAlignment(SwingConstants.CENTER);
    renderer.setFont(InterfaceGraficaUtils.FONT_LABEL);
    renderer.setForeground(new Color(90, 90, 90));
    renderer.setText(mensagem);
    tabela.getColumnModel().getColumn(0).setCellRenderer(renderer);

    return tabela;
  }
}
