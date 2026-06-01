package Resources;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public final class InterfaceGraficaUtils
{
  public static final Color COR_FUNDO = new Color(7, 16, 28);
  public static final Color COR_CARD = Color.WHITE;
  public static final Color COR_AZUL = new Color(41, 128, 185);
  public static final Color COR_TEXTO = new Color(45, 45, 45);
  public static final Color COR_SUBTEXTO = new Color(90, 90, 90);
  public static final Color COR_BORDA_TABELA = new Color(220, 220, 220);
  public static final Color COR_GRID_TABELA = new Color(235, 235, 235);
  public static final Color COR_SELECAO_TABELA = new Color(220, 235, 245);
  public static final Color COR_BRANCO = Color.WHITE;

  public static final Font FONT_TITULO = new Font("Segoe UI", Font.BOLD, 28);
  public static final Font FONT_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 16);
  public static final Font FONT_LABEL_SECAO = new Font("Segoe UI", Font.BOLD, 16);
  public static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
  public static final Font FONT_LABEL_FORM = FONT_LABEL;
  public static final Font FONT_LABEL_PESQUISA = FONT_LABEL;
  public static final Font FONT_BOTAO = FONT_LABEL;
  public static final Font FONT_TABELA = new Font("Segoe UI", Font.PLAIN, 13);
  public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);
  public static final Font FONT_ABA = FONT_HEADER;
  public static final Font FONT_AJUDA = FONT_TABELA;
  public static final Font FONT_CAMPO = new Font("Segoe UI", Font.PLAIN, 14);

  private InterfaceGraficaUtils()
  {}

  public static RoundedPanel criarCardArredondado(int arc, LayoutManager layout, Color corFundo, Border border,
      Dimension tamanhoPreferido)
  {
    RoundedPanel card = new RoundedPanel(arc);
    card.setLayout(layout);
    card.setBackground(corFundo);
    card.setBorder(border);
    card.setPreferredSize(tamanhoPreferido);
    return card;
  }

  public static JPanel criarPainelTransparente(LayoutManager layout)
  {
    JPanel panel = new JPanel(layout);
    panel.setOpaque(false);
    return panel;
  }

  public static JPanel criarTopo(String tituloTexto, String subtituloHtml, Font fontTitulo, Font fontSubtitulo,
      Color corTexto, Color corSubtexto)
  {
    JPanel topo = new JPanel();
    topo.setOpaque(false);
    topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));

    JLabel titulo = new JLabel(tituloTexto);
    titulo.setFont(fontTitulo);
    titulo.setForeground(corTexto);
    titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel subtitulo = new JLabel(subtituloHtml);
    subtitulo.setFont(fontSubtitulo);
    subtitulo.setForeground(corSubtexto);
    subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

    topo.add(titulo);
    topo.add(Box.createVerticalStrut(8));
    topo.add(subtitulo);

    return topo;
  }

  public static JLabel criarLabel(String texto, Font font, Color cor)
  {
    JLabel label = new JLabel(texto);
    label.setFont(font);
    label.setForeground(cor);
    return label;
  }

  public static JTextField criarCampoTexto(int colunas, Font font, Dimension tamanhoPreferido)
  {
    JTextField campo = new JTextField(colunas);
    campo.setFont(font);
    campo.setPreferredSize(tamanhoPreferido);
    aplicarCantosArredondados(campo);
    return campo;
  }

  public static JComboBox<String> criarCombo(String[] opcoes, Font font, Dimension tamanhoPreferido)
  {
    JComboBox<String> combo = new JComboBox<>(opcoes);
    combo.setFont(font);
    combo.setPreferredSize(tamanhoPreferido);
    aplicarCantosArredondados(combo);
    return combo;
  }

  public static JSpinner criarSpinnerNumero(int valorInicial, int minimo, int maximo, int passo, Font font,
      Dimension tamanhoPreferido)
  {
    JSpinner spinner = new JSpinner(new SpinnerNumberModel(valorInicial, minimo, maximo, passo));
    estilizarSpinner(spinner, font, tamanhoPreferido);
    return spinner;
  }

  public static void estilizarSpinner(JSpinner spinner, Font font, Dimension tamanhoPreferido)
  {
    spinner.setFont(font);
    spinner.setPreferredSize(tamanhoPreferido);
    aplicarCantosArredondados(spinner);

    JComponent editor = spinner.getEditor();
    if(editor instanceof JSpinner.DefaultEditor)
    {
      JTextField textField = ((JSpinner.DefaultEditor) editor).getTextField();
      textField.setFont(font);
      aplicarCantosArredondados(textField);
    }
  }

  public static void removerSeparadorMilhares(JSpinner spinner)
  {
    if(spinner == null)
      return;

    JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "#");
    spinner.setEditor(editor);
    estilizarSpinner(spinner, spinner.getFont(), spinner.getPreferredSize());
  }

  public static JButton criarBotao(String texto, Font font, Color corFundo, Color corTexto, Dimension tamanhoPreferido)
  {
    JButton botao = new JButton(texto);
    botao.setFont(font);
    botao.setBackground(corFundo);
    botao.setForeground(corTexto);
    botao.setFocusPainted(false);
    botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    botao.setPreferredSize(tamanhoPreferido);
    aplicarCantosArredondados(botao);
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }


public static void configurarTemaAplicacao()
{
  UIManager.put("Component.arc", Integer.valueOf(12));
  UIManager.put("Button.arc", Integer.valueOf(12));
  UIManager.put("TextComponent.arc", Integer.valueOf(12));
  UIManager.put("ComboBox.arc", Integer.valueOf(12));
  UIManager.put("ScrollBar.width", Integer.valueOf(12));
  UIManager.put("Component.focusWidth", Integer.valueOf(1));
  UIManager.put("Component.hideMnemonics", Boolean.FALSE);
  UIManager.put("Button.innerFocusWidth", Integer.valueOf(0));
  UIManager.put("PasswordField.showRevealButton", Boolean.FALSE);
}

public static void aplicarCantosArredondados(JComponent componente)
{
  if(componente == null)
    return;

  if(componente instanceof JButton)
    componente.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
  else if(componente instanceof JSpinner)
    componente.putClientProperty(FlatClientProperties.COMPONENT_ROUND_RECT, true);
}


public static final String MENSAGEM_TABELA_PESQUISE = "Pesquise ou clique Mostrar Todos para carregar dados.";

  public static JTable criarTabelaBase(Font fontTabela, Font fontHeader, Color corGrid, Color corSelecao,
      Color corTextoSelecao)
  {
    JTable tabela = new JTable()
      {
        protected void paintComponent(Graphics g)
        {
          super.paintComponent(g);
          pintarMensagemTabelaVazia(this, g);
        }
      };
    configurarTabela(tabela, fontTabela, fontHeader, corGrid, corSelecao, corTextoSelecao);
    tabela.setFillsViewportHeight(true);
    definirMensagemTabelaVazia(tabela, MENSAGEM_TABELA_PESQUISE);
    return tabela;
  }

  public static void definirMensagemTabelaVazia(JTable tabela, String mensagem)
  {
    if(tabela == null)
      return;

    tabela.putClientProperty("mensagemTabelaVazia", mensagem == null ? MENSAGEM_TABELA_PESQUISE : mensagem);
    tabela.repaint();
  }

  private static void pintarMensagemTabelaVazia(JTable tabela, Graphics g)
  {
    if(tabela == null || tabela.getRowCount() > 0)
      return;

    Object mensagem = tabela.getClientProperty("mensagemTabelaVazia");
    String texto = mensagem == null ? MENSAGEM_TABELA_PESQUISE : mensagem.toString();
    if(texto.trim().length() == 0)
      return;

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g2.setFont(FONT_AJUDA.deriveFont(Font.BOLD));
    g2.setColor(COR_SUBTEXTO);

    FontMetrics fm = g2.getFontMetrics();
    int x = Math.max(12, (tabela.getWidth() - fm.stringWidth(texto)) / 2);
    int headerHeight = tabela.getTableHeader() == null ? 0 : tabela.getTableHeader().getHeight();
    int y = Math.max(45, ((tabela.getHeight() + headerHeight) / 2));
    g2.drawString(texto, x, y);
    g2.dispose();
  }

  public static void padronizarPainelComTabelas(JPanel painel)
  {
    if(painel == null)
      return;

    List<JTable> tabelas = new ArrayList<JTable>();
    recolherComponentes(painel, JTable.class, tabelas);

    for(int i = 0; i < tabelas.size(); i++)
    {
      JTable tabela = tabelas.get(i);
      if(tabela.getClientProperty("mensagemTabelaVazia") == null)
        definirMensagemTabelaVazia(tabela, MENSAGEM_TABELA_PESQUISE);
    }

    padronizarBotoesPesquisa(painel);
    painel.revalidate();
    painel.repaint();
  }

  private static void padronizarBotoesPesquisa(Component component)
  {
    if(!(component instanceof Container))
      return;

    Container container = (Container) component;
    if(container instanceof JPanel && container.getLayout() instanceof FlowLayout)
      padronizarLinhaPesquisa((JPanel) container);

    Component[] filhos = container.getComponents();
    for(int i = 0; i < filhos.length; i++)
      padronizarBotoesPesquisa(filhos[i]);
  }

  private static void padronizarLinhaPesquisa(JPanel painel)
  {
    if(!contemLabelPesquisa(painel))
      return;

    JButton buttonMostrarTodos = encontrarBotaoPorTexto(painel, "Mostrar Todos");
    JButton buttonActualizar = encontrarBotaoPorTexto(painel, "Actualizar");

    if(buttonMostrarTodos == null && buttonActualizar != null)
    {
      buttonActualizar.setText("Mostrar Todos");
      buttonMostrarTodos = buttonActualizar;
      buttonActualizar = null;
    }

    if(buttonMostrarTodos == null)
      return;

    JTextField campoPesquisa = encontrarPrimeiroCampoTexto(painel);
    JButton buttonPesquisar = encontrarBotaoPorTexto(painel, "Pesquisar");

    boolean pesquisarCriadoAutomaticamente = false;
    if(buttonPesquisar == null)
    {
      buttonPesquisar = criarBotao("Pesquisar", FONT_BOTAO, COR_AZUL, COR_BRANCO, new Dimension(130, 36));
      IconesInterface.aplicarIconeBotao(buttonPesquisar);
      IconesInterface.aplicarEfeitoHoverBotaoFamilias(buttonPesquisar);
      inserirAntes(painel, buttonPesquisar, buttonMostrarTodos);
      pesquisarCriadoAutomaticamente = true;
    }

    if(buttonActualizar == null)
    {
      buttonActualizar = criarBotao("Actualizar", FONT_BOTAO, COR_AZUL, COR_BRANCO, new Dimension(140, 36));
      IconesInterface.aplicarIconeBotao(buttonActualizar);
      IconesInterface.aplicarEfeitoHoverBotaoFamilias(buttonActualizar);
      inserirDepois(painel, buttonActualizar, buttonMostrarTodos);
    }

    buttonActualizar.setEnabled(false);
    ligarComportamentoPesquisaPadrao(campoPesquisa, buttonPesquisar, buttonMostrarTodos, buttonActualizar,
        pesquisarCriadoAutomaticamente);
  }

  private static void ligarComportamentoPesquisaPadrao(final JTextField campoPesquisa, final JButton buttonPesquisar,
      final JButton buttonMostrarTodos, final JButton buttonActualizar, final boolean pesquisarCriadoAutomaticamente)
  {
    if(buttonPesquisar.getClientProperty("pesquisaPadronizada") == null)
    {
      buttonPesquisar.putClientProperty("pesquisaPadronizada", Boolean.TRUE);
      buttonPesquisar.addActionListener(new java.awt.event.ActionListener()
        {
          public void actionPerformed(java.awt.event.ActionEvent e)
          {
            if(buttonActualizar != null)
              buttonActualizar.setEnabled(true);

            if(pesquisarCriadoAutomaticamente && buttonMostrarTodos != null)
            {
              String texto = campoPesquisa == null ? "" : campoPesquisa.getText();
              buttonMostrarTodos.doClick();
              if(campoPesquisa != null && texto != null && texto.length() > 0)
              {
                campoPesquisa.setText("");
                campoPesquisa.setText(texto);
              }
            }
          }
        });
    }

    if(buttonMostrarTodos.getClientProperty("mostrarTodosPadronizado") == null)
    {
      buttonMostrarTodos.putClientProperty("mostrarTodosPadronizado", Boolean.TRUE);
      buttonMostrarTodos.addActionListener(new java.awt.event.ActionListener()
        {
          public void actionPerformed(java.awt.event.ActionEvent e)
          {
            if(buttonActualizar != null)
              buttonActualizar.setEnabled(true);
          }
        });
    }

    if(buttonActualizar.getClientProperty("actualizarPadronizado") == null)
    {
      buttonActualizar.putClientProperty("actualizarPadronizado", Boolean.TRUE);
      buttonActualizar.addActionListener(new java.awt.event.ActionListener()
        {
          public void actionPerformed(java.awt.event.ActionEvent e)
          {
            if(campoPesquisa != null && campoPesquisa.getText().trim().length() > 0)
              buttonPesquisar.doClick();
            else if(buttonMostrarTodos != null)
              buttonMostrarTodos.doClick();
          }
        });
    }
  }

  private static boolean contemLabelPesquisa(Container container)
  {
    Component[] componentes = container.getComponents();
    for(int i = 0; i < componentes.length; i++)
    {
      if(componentes[i] instanceof JLabel)
      {
        String texto = ((JLabel) componentes[i]).getText();
        if(texto != null && texto.toLowerCase().contains("pesquisar"))
          return true;
      }
    }
    return false;
  }

  private static JButton encontrarBotaoPorTexto(Container container, String textoProcurado)
  {
    Component[] componentes = container.getComponents();
    for(int i = 0; i < componentes.length; i++)
    {
      if(componentes[i] instanceof JButton)
      {
        String texto = ((JButton) componentes[i]).getText();
        if(texto != null && texto.equalsIgnoreCase(textoProcurado))
          return (JButton) componentes[i];
      }
    }
    return null;
  }

  private static JTextField encontrarPrimeiroCampoTexto(Container container)
  {
    Component[] componentes = container.getComponents();
    for(int i = 0; i < componentes.length; i++)
      if(componentes[i] instanceof JTextField)
        return (JTextField) componentes[i];
    return null;
  }

  private static void inserirAntes(JPanel painel, Component novo, Component referencia)
  {
    Component[] componentes = painel.getComponents();
    painel.removeAll();
    for(int i = 0; i < componentes.length; i++)
    {
      if(componentes[i] == referencia)
        painel.add(novo);
      painel.add(componentes[i]);
    }
  }

  private static void inserirDepois(JPanel painel, Component novo, Component referencia)
  {
    Component[] componentes = painel.getComponents();
    painel.removeAll();
    for(int i = 0; i < componentes.length; i++)
    {
      painel.add(componentes[i]);
      if(componentes[i] == referencia)
        painel.add(novo);
    }
  }

  private static <T extends Component> void recolherComponentes(Component component, Class<T> tipo, List<T> destino)
  {
    if(tipo.isInstance(component))
      destino.add(tipo.cast(component));

    if(component instanceof Container)
    {
      Component[] filhos = ((Container) component).getComponents();
      for(int i = 0; i < filhos.length; i++)
        recolherComponentes(filhos[i], tipo, destino);
    }
  }

  public static JScrollPane criarScrollTabela(JTable tabela, Color corBorda)
  {
    JScrollPane scroll = new JScrollPane(tabela);
    scroll.setBorder(BorderFactory.createLineBorder(corBorda));
    scroll.getViewport().setBackground(COR_BRANCO);
    scroll.getHorizontalScrollBar().setUnitIncrement(16);
    return scroll;
  }

  public static DefaultTableModel criarModeloTabela(ResultSet rs) throws Exception
  {
    ResultSetMetaData meta = rs.getMetaData();
    int colunas = meta.getColumnCount();

    Vector<String> nomesColunas = new Vector<String>();
    for(int i = 1; i <= colunas; i++)
      nomesColunas.add(meta.getColumnLabel(i));

    Vector<Vector<Object>> dados = new Vector<Vector<Object>>();

    while(rs.next())
    {
      Vector<Object> linha = new Vector<Object>();
      for(int i = 1; i <= colunas; i++)
        linha.add(rs.getObject(i));
      dados.add(linha);
    }

    return new DefaultTableModel(dados, nomesColunas)
      {
        @Override
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
  }

  public static DefaultTableModel carregarModeloTabela(Connection connection, String sql) throws Exception
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = connection.prepareStatement(sql);
      rs = ps.executeQuery();
      return criarModeloTabela(rs);
    } finally
    {
      try
      {
        if(rs != null)
          rs.close();
      } catch(Exception ignored)
      {}

      try
      {
        if(ps != null)
          ps.close();
      } catch(Exception ignored)
      {}
    }
  }

  public static void configurarTabela(JTable tabela, Font fontTabela, Font fontHeader, Color corGrid, Color corSelecao,
      Color corTextoSelecao)
  {
    tabela.setFont(fontTabela);
    tabela.setRowHeight(30);
    tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    tabela.setGridColor(corGrid);
    tabela.setSelectionBackground(corSelecao);
    tabela.setSelectionForeground(corTextoSelecao);

    JTableHeader header = tabela.getTableHeader();
    header.setFont(fontHeader);
    header.setReorderingAllowed(false);
  }

  public static void ajustarLarguraColunas(JTable tabela, Font fontTabela, Font fontHeader)
  {
    if(tabela == null || tabela.getColumnModel().getColumnCount() == 0)
      return;

    FontMetrics headerMetrics = tabela.getTableHeader().getFontMetrics(fontHeader);
    FontMetrics cellMetrics = tabela.getFontMetrics(fontTabela);

    int totalLargura = 0;
    int numeroColunas = tabela.getColumnCount();
    int[] larguras = new int[numeroColunas];

    for(int coluna = 0; coluna < numeroColunas; coluna++)
    {
      String nomeColuna = tabela.getColumnName(coluna);
      int larguraMaxima = headerMetrics.stringWidth(nomeColuna) + 30;

      for(int linha = 0; linha < tabela.getRowCount(); linha++)
      {
        Object valor = tabela.getValueAt(linha, coluna);
        String texto = valor == null ? "" : valor.toString();
        larguraMaxima = Math.max(larguraMaxima, cellMetrics.stringWidth(texto) + 30);
      }

      larguraMaxima = Math.max(larguraMaxima, 130);
      larguraMaxima = Math.min(larguraMaxima, 320);

      larguras[coluna] = larguraMaxima;
      totalLargura += larguras[coluna];
    }

    int larguraViewport = 0;
    if(tabela.getParent() instanceof JViewport)
      larguraViewport = tabela.getParent().getWidth();

    if(larguraViewport > totalLargura && numeroColunas > 0)
    {
      int extraPorColuna = (larguraViewport - totalLargura) / numeroColunas;
      int resto = (larguraViewport - totalLargura) % numeroColunas;

      for(int i = 0; i < numeroColunas; i++)
      {
        larguras[i] += extraPorColuna;
        if(i < resto)
          larguras[i] += 1;
      }
    }

    for(int coluna = 0; coluna < numeroColunas; coluna++)
      tabela.getColumnModel().getColumn(coluna).setPreferredWidth(larguras[coluna]);
  }

  public static int encontrarIndiceColuna(DefaultTableModel modelo, String nomeColuna)
  {
    if(modelo == null)
      return 0;

    for(int i = 0; i < modelo.getColumnCount(); i++)
    {
      if(modelo.getColumnName(i).equalsIgnoreCase(nomeColuna))
        return i;
    }

    return 0;
  }

  public static class RoundedPanel extends JPanel
  {
    private final int arc;

    public RoundedPanel(int arc)
    {
      this.arc = arc;
      setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(getBackground());
      g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
      g2.dispose();
    }
  }
}
