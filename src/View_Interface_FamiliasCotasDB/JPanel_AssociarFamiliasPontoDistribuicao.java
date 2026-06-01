package View_Interface_FamiliasCotasDB;

import Resources.MensagensInterface;

import Repository_SQL.FamiliasCotasDB.AssociarFamiliasPontoSQL;
import Resources.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JPanel_AssociarFamiliasPontoDistribuicao extends JPanel
{
  private static final String CARD_SELECIONAR_FAMILIAS = "selecionar_familias";
  private static final String CARD_SELECIONAR_PONTO = "selecionar_ponto";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final TratarButtons tratarButtons = new TratarButtons();

  private CardLayout cardLayout;
  private JPanel panelCards;

  private JTable tabelaFamilias;
  private DefaultTableModel modeloFamilias;
  private TableRowSorter<DefaultTableModel> sorterFamilias;
  private JTextField fieldPesquisaFamilias;
  private JComboBox<String> comboCriterioFamilias;
  private JButton buttonActualizarFamilias;
  private JButton buttonAvancarParaPontos;
  private JButton buttonVoltarDashboard;

  private JTable tabelaPontos;
  private DefaultTableModel modeloPontos;
  private TableRowSorter<DefaultTableModel> sorterPontos;
  private JTextField fieldPesquisaPontos;
  private JComboBox<String> comboCriterioPontos;
  private JTextField fieldFamiliasSelecionadas;
  private JButton buttonActualizarPontos;
  private JButton buttonVoltarParaFamilias;
  private JButton buttonAssociar;

  public JPanel_AssociarFamiliasPontoDistribuicao(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_AssociarFamiliasPontoDistribuicao(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_AssociarFamiliasPontoDistribuicao(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_AssociarFamiliasPontoDistribuicao(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
    panelCards.add(criarCardSelecionarFamilias(), CARD_SELECIONAR_FAMILIAS);
    panelCards.add(criarCardSelecionarPonto(), CARD_SELECIONAR_PONTO);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(18, 18, 18, 18);

    add(panelCards, gbc);

    if(carregarTabelasAutomaticamente)
    {
      carregarFamilias();
      carregarPontos();
    }
  }

  private JPanel criarCardSelecionarFamilias()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(criarTopoFamilias(), BorderLayout.NORTH);
    card.add(criarCentroFamilias(), BorderLayout.CENTER);
    card.add(criarRodapeFamilias(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCardSelecionarPonto()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(criarTopoPontos(), BorderLayout.NORTH);
    card.add(criarCentroPontos(), BorderLayout.CENTER);
    card.add(criarRodapePontos(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarTopoFamilias()
  {
    return InterfaceGraficaUtils.criarTopo("Associar Famílias a Ponto de Distribuição",
        "<html>Seleccione uma ou várias famílias que serão associadas ao mesmo ponto de distribuição.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarTopoPontos()
  {
    return InterfaceGraficaUtils.criarTopo("Selecionar Ponto de Distribuição",
        "<html>Escolha um ponto de distribuição para receber todas as famílias seleccionadas.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarCentroFamilias()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Famílias Beneficiárias",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(label, BorderLayout.NORTH);
    topoTabela.add(criarPainelPesquisaFamilias(), BorderLayout.SOUTH);

    painel.add(topoTabela, BorderLayout.NORTH);
    painel.add(criarScrollFamilias(), BorderLayout.CENTER);

    return painel;
  }

  private JPanel criarCentroPontos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    fieldFamiliasSelecionadas = new JTextField(20);
    fieldFamiliasSelecionadas.setEditable(false);
    fieldFamiliasSelecionadas.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    fieldFamiliasSelecionadas.setPreferredSize(new Dimension(650, 36));
    fieldFamiliasSelecionadas.setMinimumSize(new Dimension(650, 36));

    JPanel painelResumo = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painelResumo.add(InterfaceGraficaUtils.criarLabel("Famílias escolhidas:", InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_TEXTO));
    painelResumo.add(fieldFamiliasSelecionadas);

    JLabel label = InterfaceGraficaUtils.criarLabel("Pontos de Distribuição",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(painelResumo, BorderLayout.NORTH);
    topoTabela.add(label, BorderLayout.CENTER);
    topoTabela.add(criarPainelPesquisaPontos(), BorderLayout.SOUTH);

    painel.add(topoTabela, BorderLayout.NORTH);
    painel.add(criarScrollPontos(), BorderLayout.CENTER);

    return painel;
  }

  private JPanel criarRodapeFamilias()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("Use Ctrl ou Shift para selecionar várias famílias.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar");
    buttonAvancarParaPontos = criarBotao("Proceder");
    buttonVoltarDashboard.addActionListener(tratarButtons);
    buttonAvancarParaPontos.addActionListener(tratarButtons);
    painelBotoes.add(buttonVoltarDashboard);
    painelBotoes.add(buttonAvancarParaPontos);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarRodapePontos()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("A associação será pré-validada antes da confirmação.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarParaFamilias = criarBotao("Voltar");
    buttonAssociar = criarBotao("Associar");

    buttonVoltarParaFamilias.addActionListener(tratarButtons);
    buttonAssociar.addActionListener(tratarButtons);

    painelBotoes.add(buttonVoltarParaFamilias);
    painelBotoes.add(buttonAssociar);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarPainelPesquisaFamilias()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    fieldPesquisaFamilias = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboCriterioFamilias = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Família", "Nome Responsável", "Contacto", "Estado", "Código Ponto Actual"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(210, 36));
    buttonActualizarFamilias = criarBotao("Mostrar Todos");
    buttonActualizarFamilias.setPreferredSize(new Dimension(145, 36));

    fieldPesquisaFamilias.getDocument().addDocumentListener(new TratarPesquisaFamilias());
    comboCriterioFamilias.addActionListener(tratarButtons);
    buttonActualizarFamilias.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisaFamilias);
    painel.add(comboCriterioFamilias);
    painel.add(buttonActualizarFamilias);

    return painel;
  }

  private JPanel criarPainelPesquisaPontos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    fieldPesquisaPontos = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboCriterioPontos = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Ponto", "Localização", "Infraestrutura", "Estado"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(180, 36));
    buttonActualizarPontos = criarBotao("Mostrar Todos");
    buttonActualizarPontos.setPreferredSize(new Dimension(145, 36));

    fieldPesquisaPontos.getDocument().addDocumentListener(new TratarPesquisaPontos());
    comboCriterioPontos.addActionListener(tratarButtons);
    buttonActualizarPontos.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisaPontos);
    painel.add(comboCriterioPontos);
    painel.add(buttonActualizarPontos);

    return painel;
  }

  private JScrollPane criarScrollFamilias()
  {
    String[] colunas =
      {
          "Código Família", "Nome Responsável", "Contacto", "Estado", "Código Ponto Actual", "Localização"
      };

    modeloFamilias = criarModeloNaoEditavel(colunas);
    tabelaFamilias = criarTabela(modeloFamilias);
    tabelaFamilias.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sorterFamilias = new TableRowSorter<DefaultTableModel>(modeloFamilias);
    tabelaFamilias.setRowSorter(sorterFamilias);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaFamilias, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1180, 520));
    return scroll;
  }

  private JScrollPane criarScrollPontos()
  {
    String[] colunas =
      {
          "Código Ponto", "Localização", "Infraestrutura", "Capacidade", "Volume Actual", "Estado"
      };

    modeloPontos = criarModeloNaoEditavel(colunas);
    tabelaPontos = criarTabela(modeloPontos);
    tabelaPontos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterPontos = new TableRowSorter<DefaultTableModel>(modeloPontos);
    tabelaPontos.setRowSorter(sorterPontos);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaPontos, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1180, 500));
    return scroll;
  }

  private DefaultTableModel criarModeloNaoEditavel(String[] colunas)
  {
    return new DefaultTableModel(colunas, 0)
      {
        @Override
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
  }

  private JTable criarTabela(DefaultTableModel modelo)
  {
    JTable tabela = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabela.setModel(modelo);
    return tabela;
  }

  private JButton criarBotao(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(145, 42));
    IconesInterface.aplicarIconeBotao(botao);
    Resources.IconesInterface.aplicarEfeitoHoverBotaoFamilias(botao);
    return botao;
  }

  private void aplicarFiltroFamilias()
  {
    aplicarFiltro(sorterFamilias, fieldPesquisaFamilias, getIndiceColunaFamilias());
  }

  private void aplicarFiltroPontos()
  {
    aplicarFiltro(sorterPontos, fieldPesquisaPontos, getIndiceColunaPontos());
  }

  private void aplicarFiltro(TableRowSorter<DefaultTableModel> sorter, JTextField campo, int coluna)
  {
    if(sorter == null || campo == null)
      return;

    String texto = campo.getText().trim();

    if(texto.isEmpty())
    {
      sorter.setRowFilter(null);
      return;
    }

    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private int getIndiceColunaFamilias()
  {
    String criterio = comboCriterioFamilias.getSelectedItem().toString();

    switch(criterio)
    {
      case "Código Família":
        return 0;
      case "Nome Responsável":
        return 1;
      case "Contacto":
        return 2;
      case "Estado":
        return 3;
      case "Código Ponto Actual":
        return 4;
      default:
        return 0;
    }
  }

  private int getIndiceColunaPontos()
  {
    String criterio = comboCriterioPontos.getSelectedItem().toString();

    switch(criterio)
    {
      case "Código Ponto":
        return 0;
      case "Localização":
        return 1;
      case "Infraestrutura":
        return 2;
      case "Estado":
        return 5;
      default:
        return 0;
    }
  }

  private boolean validarFamiliasSelecionadas()
  {
    if(tabelaFamilias.getSelectedRows().length == 0)
    {
      JOptionPane.showMessageDialog(this, "Seleccione pelo menos uma família.");
      return false;
    }

    return true;
  }

  private boolean validarPontoSelecionado()
  {
    if(tabelaPontos.getSelectedRow() == -1)
    {
      JOptionPane.showMessageDialog(this, "Seleccione um ponto de distribuição.");
      return false;
    }

    return true;
  }

  private void carregarFamilias()
  {
    try
    {
      AssociarFamiliasPontoSQL.carregarFamilias(connection, modeloFamilias);
      aplicarFiltroFamilias();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar as famílias.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarPontos()
  {
    try
    {
      AssociarFamiliasPontoSQL.carregarPontos(connection, modeloPontos);
      aplicarFiltroPontos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os pontos de distribuição.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void prepararCardPontos()
  {
    fieldFamiliasSelecionadas.setText(formatarFamiliasSelecionadas());
  }

  private String formatarFamiliasSelecionadas()
  {
    int[] linhasSelecionadas = tabelaFamilias.getSelectedRows();
    List<String> codigos = new ArrayList<String>();

    for(int linhaView : linhasSelecionadas)
    {
      int linhaModel = tabelaFamilias.convertRowIndexToModel(linhaView);
      Object valor = modeloFamilias.getValueAt(linhaModel, 0);

      if(valor != null)
        codigos.add(valor.toString());
    }

    return codigos.isEmpty() ? "Nenhuma família seleccionada" : String.join(", ", codigos);
  }

  private List<Integer> getCodigosFamiliasSelecionadas()
  {
    int[] linhasSelecionadas = tabelaFamilias.getSelectedRows();
    List<Integer> codigos = new ArrayList<Integer>();

    for(int linhaView : linhasSelecionadas)
    {
      int linhaModel = tabelaFamilias.convertRowIndexToModel(linhaView);
      Object valor = modeloFamilias.getValueAt(linhaModel, 0);

      if(valor != null)
        codigos.add(Integer.valueOf(valor.toString()));
    }

    return codigos;
  }

  private Integer getCodigoPontoSelecionado()
  {
    int linhaView = tabelaPontos.getSelectedRow();

    if(linhaView == -1)
      return null;

    int linhaModel = tabelaPontos.convertRowIndexToModel(linhaView);
    Object valor = modeloPontos.getValueAt(linhaModel, 0);

    if(valor == null)
      return null;

    return Integer.valueOf(valor.toString());
  }

  private void associarFamiliasAoPonto()
  {
    if(!validarPontoSelecionado())
      return;

    List<Integer> codigosFamilias = getCodigosFamiliasSelecionadas();
    Integer codigoPonto = getCodigoPontoSelecionado();

    if(codigosFamilias.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Nenhuma família foi seleccionada.");
      cardLayout.show(panelCards, CARD_SELECIONAR_FAMILIAS);
      return;
    }

    try
    {
      AssociarFamiliasPontoSQL.ResultadoPreValidacao preValidacao =
          AssociarFamiliasPontoSQL.preValidarAssociacao(connection, codigosFamilias, codigoPonto.intValue());

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Associação bloqueada:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        carregarFamilias();
        carregarPontos();
        cardLayout.show(panelCards, CARD_SELECIONAR_FAMILIAS);
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Famílias seleccionadas: " + preValidacao.totalFamilias + "\nPonto de distribuição destino: " + codigoPonto
              + "\n\nConfirmar associação?",
          "Confirmar Associação", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      AssociarFamiliasPontoSQL.ResultadoAssociacao resultado =
          AssociarFamiliasPontoSQL.associarFamilias(connection, codigosFamilias, codigoPonto.intValue());

      if(!resultado.associou)
      {
        JOptionPane.showMessageDialog(this, "Associação não concluída:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          resultado.totalAssociadas + " família(s) associada(s) ao ponto " + codigoPonto + ".");

      carregarFamilias();
      carregarPontos();
      tabelaFamilias.clearSelection();
      tabelaPontos.clearSelection();
      fieldFamiliasSelecionadas.setText("");
      cardLayout.show(panelCards, CARD_SELECIONAR_FAMILIAS);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível associar famílias ao ponto:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private class TratarPesquisaFamilias implements DocumentListener
  {
    @Override
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroFamilias();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroFamilias();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroFamilias();
    }
  }

  private class TratarPesquisaPontos implements DocumentListener
  {
    @Override
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroPontos();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroPontos();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroPontos();
    }
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      if(e.getSource() == comboCriterioFamilias)
      {
        aplicarFiltroFamilias();
        return;
      }

      if(e.getSource() == comboCriterioPontos)
      {
        aplicarFiltroPontos();
        return;
      }

      if(e.getSource() == buttonActualizarFamilias)
      {
        carregarFamilias();
        return;
      }

      if(e.getSource() == buttonActualizarPontos)
      {
        carregarPontos();
        return;
      }

      if(e.getSource() == buttonAvancarParaPontos)
      {
        if(!validarFamiliasSelecionadas())
          return;

        prepararCardPontos();
        cardLayout.show(panelCards, CARD_SELECIONAR_PONTO);
        return;
      }

      if(e.getSource() == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }

      if(e.getSource() == buttonVoltarParaFamilias)
      {
        cardLayout.show(panelCards, CARD_SELECIONAR_FAMILIAS);
        return;
      }

      if(e.getSource() == buttonAssociar)
      {
        associarFamiliasAoPonto();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }
}
