package View_Interface_DistribuicaoConsumoDB;

import Repository_SQL.DistribuicaoConsumoDB.RetirarAguaSQL;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.regex.Pattern;

public class JPanel_RetirarAgua extends JPanel
{

  private final Connection connection;

  private CardLayout cardLayout;
  private JPanel panelCards;

  // Card 1 - Pontos
  private JTable tabelaPontos;
  private DefaultTableModel modeloPontos;
  private TableRowSorter<DefaultTableModel> sorterPontos;
  private JTextField fieldPesquisaPontos;
  private JComboBox<String> comboCriterioPontos;
  private JButton buttonActualizarPontos;
  private JButton buttonProcederPonto;
  private JButton buttonVoltarMenu;

  // Card 2 - Familias
  private JTable tabelaFamilias;
  private DefaultTableModel modeloFamilias;
  private TableRowSorter<DefaultTableModel> sorterFamilias;
  private JTextField fieldPesquisaFamilias;
  private JComboBox<String> comboCriterioFamilias;
  private JButton buttonActualizarFamilias;
  private JButton buttonVoltarPontos;
  private JButton buttonProcederFamilia;
  private JTextField fieldCodigoPontoFamilias;

  // Card 3 - Cotas
  private JTable tabelaCotas;
  private DefaultTableModel modeloCotas;
  private TableRowSorter<DefaultTableModel> sorterCotas;
  private JTextField fieldPesquisaCotas;
  private JComboBox<String> comboCriterioCotas;
  private JButton buttonActualizarCotas;
  private JButton buttonVoltarFamilias;
  private JButton buttonProcederCota;
  private JTextField fieldCodigoPontoCotas;
  private JTextField fieldCodigoFamiliaCotas;

  // Card 4 - Formulario retirada
  private JTextField fieldCodigoPdSelecionado;
  private JTextField fieldCodigoFbSelecionado;
  private JTextField fieldCodigoCotaSelecionada;
  private JTextField fieldNomePessoaColeta;
  private JTextField fieldVolumeARetirar;
  private JTextArea areaObservacao;
  private JButton buttonVoltarCotas;
  private JButton buttonRegistarRetirada;
  private final Runnable voltarDashboard;

  public JPanel_RetirarAgua(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_RetirarAgua(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_RetirarAgua(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_RetirarAgua(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = new JPanel(cardLayout);
    panelCards.setOpaque(false);

    panelCards.add(criarCardSelecionarPonto(), "selecionar_ponto");
    panelCards.add(criarCardSelecionarFamilia(), "selecionar_familia");
    panelCards.add(criarCardSelecionarCota(), "selecionar_cota");
    panelCards.add(criarCardFormularioRetirada(), "formulario_retirada");

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
      carregarPontosDistribuicao();
    }
  }

  private JPanel criarCardSelecionarPonto()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    RoundedPanel card = new RoundedPanel(32);
    card.setLayout(new BorderLayout(0, 22));
    card.setBackground(InterfaceGraficaUtils.COR_CARD);
    card.setBorder(new EmptyBorder(30, 30, 30, 30));

    card.add(criarTopo("Retirar Água", "Primeiro, selecione o ponto de distribuição onde a retirada será feita."),
        BorderLayout.NORTH);

    JPanel centro = new JPanel(new BorderLayout(0, 12));
    centro.setOpaque(false);

    JLabel labelTabela = new JLabel("Pontos de Distribuição");
    labelTabela.setFont(InterfaceGraficaUtils.FONT_LABEL_SECAO);
    labelTabela.setForeground(InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = new JPanel(new BorderLayout(0, 10));
    topoTabela.setOpaque(false);
    topoTabela.add(labelTabela, BorderLayout.NORTH);
    topoTabela.add(criarPainelPesquisaPontos(), BorderLayout.SOUTH);

    centro.add(topoTabela, BorderLayout.NORTH);
    centro.add(criarScrollTabelaPontos(), BorderLayout.CENTER);

    card.add(centro, BorderLayout.CENTER);
    card.add(criarRodapePontos(), BorderLayout.SOUTH);

    card.setPreferredSize(new Dimension(1180, 720));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;

    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCardSelecionarFamilia()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    RoundedPanel card = new RoundedPanel(32);
    card.setLayout(new BorderLayout(0, 22));
    card.setBackground(InterfaceGraficaUtils.COR_CARD);
    card.setBorder(new EmptyBorder(30, 30, 30, 30));

    card.add(
        criarTopo("Selecionar Família", "Agora, escolha uma família associada ao ponto de distribuição seleccionado."),
        BorderLayout.NORTH);

    JPanel centro = new JPanel(new BorderLayout(0, 12));
    centro.setOpaque(false);

    JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    info.setOpaque(false);

    JLabel lblPonto = new JLabel("Código do ponto:");
    lblPonto.setFont(InterfaceGraficaUtils.FONT_LABEL_FORM);
    lblPonto.setForeground(InterfaceGraficaUtils.COR_TEXTO);

    fieldCodigoPontoFamilias = new JTextField(12);
    fieldCodigoPontoFamilias.setEditable(false);
    estilizarCampo(fieldCodigoPontoFamilias);

    info.add(lblPonto);
    info.add(fieldCodigoPontoFamilias);

    JLabel labelTabela = new JLabel("Famílias Associadas ao Ponto");
    labelTabela.setFont(InterfaceGraficaUtils.FONT_LABEL_SECAO);
    labelTabela.setForeground(InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = new JPanel(new BorderLayout(0, 10));
    topoTabela.setOpaque(false);
    topoTabela.add(labelTabela, BorderLayout.NORTH);
    topoTabela.add(info, BorderLayout.CENTER);
    topoTabela.add(criarPainelPesquisaFamilias(), BorderLayout.SOUTH);

    centro.add(topoTabela, BorderLayout.NORTH);
    centro.add(criarScrollTabelaFamilias(), BorderLayout.CENTER);

    card.add(centro, BorderLayout.CENTER);
    card.add(criarRodapeFamilias(), BorderLayout.SOUTH);

    card.setPreferredSize(new Dimension(1180, 720));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;

    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCardSelecionarCota()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    RoundedPanel card = new RoundedPanel(32);
    card.setLayout(new BorderLayout(0, 22));
    card.setBackground(InterfaceGraficaUtils.COR_CARD);
    card.setBorder(new EmptyBorder(30, 30, 30, 30));

    card.add(criarTopo("Selecionar Cota de Água", "Seleccione uma cota da família escolhida."), BorderLayout.NORTH);

    JPanel centro = new JPanel(new BorderLayout(0, 12));
    centro.setOpaque(false);

    JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    info.setOpaque(false);

    JLabel lblPonto = new JLabel("Ponto:");
    lblPonto.setFont(InterfaceGraficaUtils.FONT_LABEL_FORM);
    lblPonto.setForeground(InterfaceGraficaUtils.COR_TEXTO);

    fieldCodigoPontoCotas = new JTextField(10);
    fieldCodigoPontoCotas.setEditable(false);
    estilizarCampo(fieldCodigoPontoCotas);

    JLabel lblFamilia = new JLabel("Família:");
    lblFamilia.setFont(InterfaceGraficaUtils.FONT_LABEL_FORM);
    lblFamilia.setForeground(InterfaceGraficaUtils.COR_TEXTO);

    fieldCodigoFamiliaCotas = new JTextField(10);
    fieldCodigoFamiliaCotas.setEditable(false);
    estilizarCampo(fieldCodigoFamiliaCotas);

    info.add(lblPonto);
    info.add(fieldCodigoPontoCotas);
    info.add(lblFamilia);
    info.add(fieldCodigoFamiliaCotas);

    JLabel labelTabela = new JLabel("Cotas da Família");
    labelTabela.setFont(InterfaceGraficaUtils.FONT_LABEL_SECAO);
    labelTabela.setForeground(InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = new JPanel(new BorderLayout(0, 10));
    topoTabela.setOpaque(false);
    topoTabela.add(labelTabela, BorderLayout.NORTH);
    topoTabela.add(info, BorderLayout.CENTER);
    topoTabela.add(criarPainelPesquisaCotas(), BorderLayout.SOUTH);

    centro.add(topoTabela, BorderLayout.NORTH);
    centro.add(criarScrollTabelaCotas(), BorderLayout.CENTER);

    card.add(centro, BorderLayout.CENTER);
    card.add(criarRodapeCotas(), BorderLayout.SOUTH);

    card.setPreferredSize(new Dimension(1180, 720));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;

    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCardFormularioRetirada()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    RoundedPanel card = new RoundedPanel(32);
    card.setLayout(new BorderLayout(0, 18));
    card.setBackground(InterfaceGraficaUtils.COR_CARD);
    card.setBorder(new EmptyBorder(30, 30, 30, 30));

    card.add(criarTopo("Dados da Retirada", "Preencha os dados finais da retirada de água."), BorderLayout.NORTH);

    card.add(criarCentroFormularioRetirada(), BorderLayout.CENTER);
    card.add(criarRodapeFormularioRetirada(), BorderLayout.SOUTH);

    card.setPreferredSize(new Dimension(1180, 720));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;

    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarTopo(String tituloTexto, String subtituloTexto)
  {
    JPanel painelTopo = new JPanel();
    painelTopo.setOpaque(false);
    painelTopo.setLayout(new BoxLayout(painelTopo, BoxLayout.Y_AXIS));

    JLabel titulo = new JLabel(tituloTexto);
    titulo.setFont(InterfaceGraficaUtils.FONT_TITULO);
    titulo.setForeground(InterfaceGraficaUtils.COR_TEXTO);
    titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel subtitulo = new JLabel("<html>" + subtituloTexto + "</html>");
    subtitulo.setFont(InterfaceGraficaUtils.FONT_SUBTITULO);
    subtitulo.setForeground(InterfaceGraficaUtils.COR_SUBTEXTO);
    subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

    painelTopo.add(titulo);
    painelTopo.add(Box.createVerticalStrut(8));
    painelTopo.add(subtitulo);

    return painelTopo;
  }

  private JPanel criarPainelPesquisaPontos()
  {
    JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painel.setOpaque(false);

    JLabel label = new JLabel("Pesquisar:");
    label.setFont(InterfaceGraficaUtils.FONT_LABEL_PESQUISA);
    label.setForeground(InterfaceGraficaUtils.COR_TEXTO);

    fieldPesquisaPontos = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));

    comboCriterioPontos = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código", "Localização", "Tipo de Infraestrutura", "Fonte de Abastecimento", "Estado Operacional"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(230, 36));

    buttonActualizarPontos = criarBotao("Mostrar Todos");
    buttonActualizarPontos.setPreferredSize(new Dimension(145, 36));

    fieldPesquisaPontos.getDocument().addDocumentListener(new TratarPesquisaPontos());
    comboCriterioPontos.addActionListener(new TratarButtons());
    buttonActualizarPontos.addActionListener(new TratarButtons());

    painel.add(label);
    painel.add(fieldPesquisaPontos);
    painel.add(comboCriterioPontos);
    painel.add(buttonActualizarPontos);

    return painel;
  }

  private JPanel criarPainelPesquisaFamilias()
  {
    JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painel.setOpaque(false);

    JLabel label = new JLabel("Pesquisar:");
    label.setFont(InterfaceGraficaUtils.FONT_LABEL_PESQUISA);
    label.setForeground(InterfaceGraficaUtils.COR_TEXTO);

    fieldPesquisaFamilias = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));

    comboCriterioFamilias = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código", "Nome", "Contacto", "Número de Membros", "Perfil", "Estado"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(220, 36));

    buttonActualizarFamilias = criarBotao("Mostrar Todos");
    buttonActualizarFamilias.setPreferredSize(new Dimension(145, 36));

    fieldPesquisaFamilias.getDocument().addDocumentListener(new TratarPesquisaFamilias());
    comboCriterioFamilias.addActionListener(new TratarButtons());
    buttonActualizarFamilias.addActionListener(new TratarButtons());

    painel.add(label);
    painel.add(fieldPesquisaFamilias);
    painel.add(comboCriterioFamilias);
    painel.add(buttonActualizarFamilias);

    return painel;
  }

  private JPanel criarPainelPesquisaCotas()
  {
    JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painel.setOpaque(false);

    JLabel label = new JLabel("Pesquisar:");
    label.setFont(InterfaceGraficaUtils.FONT_LABEL_PESQUISA);
    label.setForeground(InterfaceGraficaUtils.COR_TEXTO);

    fieldPesquisaCotas = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));

    comboCriterioCotas = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código da Cota", "Validade", "Ajuste Sazonal", "Transferência", "Status"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(220, 36));

    buttonActualizarCotas = criarBotao("Mostrar Todos");
    buttonActualizarCotas.setPreferredSize(new Dimension(145, 36));

    fieldPesquisaCotas.getDocument().addDocumentListener(new TratarPesquisaCotas());
    comboCriterioCotas.addActionListener(new TratarButtons());
    buttonActualizarCotas.addActionListener(new TratarButtons());

    painel.add(label);
    painel.add(fieldPesquisaCotas);
    painel.add(comboCriterioCotas);
    painel.add(buttonActualizarCotas);

    return painel;
  }

  private JScrollPane criarScrollTabelaPontos()
  {
    String[] colunas =
      {
          "Código", "Localização", "Tipo de Infraestrutura", "Volume Atual", "Fonte de Abastecimento",
          "Estado Operacional"
      };

    modeloPontos = new DefaultTableModel(colunas, 0)
      {
        @Override
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };

    tabelaPontos = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaPontos.setModel(modeloPontos);

    sorterPontos = new TableRowSorter<>(modeloPontos);
    tabelaPontos.setRowSorter(sorterPontos);
    tabelaPontos.getSelectionModel().addListSelectionListener(new TratarSelecaoPontos());

    JScrollPane scroll = new JScrollPane(tabelaPontos);
    scroll.setBorder(BorderFactory.createLineBorder(InterfaceGraficaUtils.COR_BORDA_TABELA));
    scroll.getViewport().setBackground(InterfaceGraficaUtils.COR_BRANCO);
    scroll.setPreferredSize(new Dimension(1100, 470));

    return scroll;
  }

  private JScrollPane criarScrollTabelaFamilias()
  {
    String[] colunas =
      {
          "Código", "Nome do Responsável", "Contacto", "Número de Membros", "Perfil Socioeconómico", "Estado"
      };

    modeloFamilias = new DefaultTableModel(colunas, 0)
      {
        @Override
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };

    tabelaFamilias = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaFamilias.setModel(modeloFamilias);

    sorterFamilias = new TableRowSorter<>(modeloFamilias);
    tabelaFamilias.setRowSorter(sorterFamilias);
    tabelaFamilias.getSelectionModel().addListSelectionListener(new TratarSelecaoFamilias());

    JScrollPane scroll = new JScrollPane(tabelaFamilias);
    scroll.setBorder(BorderFactory.createLineBorder(InterfaceGraficaUtils.COR_BORDA_TABELA));
    scroll.getViewport().setBackground(InterfaceGraficaUtils.COR_BRANCO);
    scroll.setPreferredSize(new Dimension(1100, 470));

    return scroll;
  }

  private JScrollPane criarScrollTabelaCotas()
  {
    String[] colunas =
      {
          "Código da Cota", "Volume Semanal", "Saldo Disponível", "Validade", "Ajuste Sazonal", "Transferência",
          "Status"
      };

    modeloCotas = new DefaultTableModel(colunas, 0)
      {
        @Override
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };

    tabelaCotas = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaCotas.setModel(modeloCotas);

    sorterCotas = new TableRowSorter<>(modeloCotas);
    tabelaCotas.setRowSorter(sorterCotas);
    tabelaCotas.getSelectionModel().addListSelectionListener(new TratarSelecaoCotas());

    JScrollPane scroll = new JScrollPane(tabelaCotas);
    scroll.setBorder(BorderFactory.createLineBorder(InterfaceGraficaUtils.COR_BORDA_TABELA));
    scroll.getViewport().setBackground(InterfaceGraficaUtils.COR_BRANCO);
    scroll.setPreferredSize(new Dimension(1100, 470));

    return scroll;
  }

  private JPanel criarRodapePontos()
  {
    JPanel painelRodape = new JPanel(new BorderLayout());
    painelRodape.setOpaque(false);

    JLabel labelAjuda = new JLabel("Seleccione um ponto de distribuição para continuar.");
    labelAjuda.setFont(InterfaceGraficaUtils.FONT_AJUDA);
    labelAjuda.setForeground(InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    painelBotoes.setOpaque(false);

    buttonVoltarMenu = criarBotao("Voltar");
    buttonProcederPonto = criarBotao("Proceder");
    buttonProcederPonto.setEnabled(false);

    buttonVoltarMenu.addActionListener(new TratarButtons());
    buttonProcederPonto.addActionListener(new TratarButtons());

    painelBotoes.add(buttonVoltarMenu);
    painelBotoes.add(buttonProcederPonto);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarRodapeFamilias()
  {
    JPanel painelRodape = new JPanel(new BorderLayout());
    painelRodape.setOpaque(false);

    JLabel labelAjuda = new JLabel("Seleccione uma família associada ao ponto.");
    labelAjuda.setFont(InterfaceGraficaUtils.FONT_AJUDA);
    labelAjuda.setForeground(InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    painelBotoes.setOpaque(false);

    buttonVoltarPontos = criarBotao("Voltar");
    buttonProcederFamilia = criarBotao("Proceder");
    buttonProcederFamilia.setEnabled(false);

    buttonVoltarPontos.addActionListener(new TratarButtons());
    buttonProcederFamilia.addActionListener(new TratarButtons());

    painelBotoes.add(buttonVoltarPontos);
    painelBotoes.add(buttonProcederFamilia);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarRodapeCotas()
  {
    JPanel painelRodape = new JPanel(new BorderLayout());
    painelRodape.setOpaque(false);

    JLabel labelAjuda = new JLabel("Seleccione uma cota de água para continuar.");
    labelAjuda.setFont(InterfaceGraficaUtils.FONT_AJUDA);
    labelAjuda.setForeground(InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    painelBotoes.setOpaque(false);

    buttonVoltarFamilias = criarBotao("Voltar");
    buttonProcederCota = criarBotao("Proceder");
    buttonProcederCota.setEnabled(false);

    buttonVoltarFamilias.addActionListener(new TratarButtons());
    buttonProcederCota.addActionListener(new TratarButtons());

    painelBotoes.add(buttonVoltarFamilias);
    painelBotoes.add(buttonProcederCota);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarCentroFormularioRetirada()
  {
    JPanel painel = new JPanel(new GridBagLayout());
    painel.setOpaque(false);

    fieldCodigoPdSelecionado = new JTextField(20);
    fieldCodigoPdSelecionado.setEditable(false);

    fieldCodigoFbSelecionado = new JTextField(20);
    fieldCodigoFbSelecionado.setEditable(false);

    fieldCodigoCotaSelecionada = new JTextField(20);
    fieldCodigoCotaSelecionada.setEditable(false);

    fieldNomePessoaColeta = new JTextField(20);
    fieldVolumeARetirar = new JTextField(20);
    areaObservacao = new JTextArea(5, 20);
    areaObservacao.setLineWrap(true);
    areaObservacao.setWrapStyleWord(true);
    areaObservacao.setFont(InterfaceGraficaUtils.FONT_CAMPO);

    estilizarCampo(fieldCodigoPdSelecionado);
    estilizarCampo(fieldCodigoFbSelecionado);
    estilizarCampo(fieldCodigoCotaSelecionada);
    estilizarCampo(fieldNomePessoaColeta);
    estilizarCampo(fieldVolumeARetirar);

    JScrollPane scrollObs = new JScrollPane(areaObservacao);
    scrollObs.setPreferredSize(new Dimension(320, 110));

    int linha = 0;
    adicionarLinhaFormulario(painel, linha++, "Código do ponto:", fieldCodigoPdSelecionado);
    adicionarLinhaFormulario(painel, linha++, "Código da família:", fieldCodigoFbSelecionado);
    adicionarLinhaFormulario(painel, linha++, "Código da cota:", fieldCodigoCotaSelecionada);
    adicionarLinhaFormulario(painel, linha++, "Nome da pessoa que colecta:", fieldNomePessoaColeta);
    adicionarLinhaFormulario(painel, linha++, "Volume a retirar:", fieldVolumeARetirar);
    adicionarLinhaFormulario(painel, linha++, "Observação:", scrollObs);

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = linha;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);

    return painel;
  }

  private JPanel criarRodapeFormularioRetirada()
  {
    JPanel painelRodape = new JPanel(new BorderLayout());
    painelRodape.setOpaque(false);

    JLabel labelAjuda = new JLabel("Preencha os dados finais da retirada.");
    labelAjuda.setFont(InterfaceGraficaUtils.FONT_AJUDA);
    labelAjuda.setForeground(InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    painelBotoes.setOpaque(false);

    buttonVoltarCotas = criarBotao("Voltar");
    buttonRegistarRetirada = criarBotao("Registar");

    buttonVoltarCotas.addActionListener(new TratarButtons());
    buttonRegistarRetirada.addActionListener(new TratarButtons());

    painelBotoes.add(buttonVoltarCotas);
    painelBotoes.add(buttonRegistarRetirada);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private void configurarTabela(JTable tabela)
  {
    tabela.setRowHeight(30);
    tabela.setFont(InterfaceGraficaUtils.FONT_TABELA);
    tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tabela.setFillsViewportHeight(true);
    tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    tabela.setGridColor(InterfaceGraficaUtils.COR_GRID_TABELA);
    tabela.setSelectionBackground(InterfaceGraficaUtils.COR_SELECAO_TABELA);
    tabela.setSelectionForeground(InterfaceGraficaUtils.COR_TEXTO);
    tabela.getTableHeader().setFont(InterfaceGraficaUtils.FONT_HEADER);
    tabela.getTableHeader().setReorderingAllowed(false);
  }

  private void adicionarLinhaFormulario(JPanel painel, int linha, String textoLabel, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();

    JLabel label = new JLabel(textoLabel);
    label.setFont(InterfaceGraficaUtils.FONT_LABEL_FORM);
    label.setForeground(InterfaceGraficaUtils.COR_TEXTO);

    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(8, 0, 8, 16);
    painel.add(label, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 0, 8, 0);
    painel.add(campo, gbc);
  }

  private void estilizarCampo(JTextField campo)
  {
    campo.setPreferredSize(new Dimension(320, 36));
    campo.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    InterfaceGraficaUtils.aplicarCantosArredondados(campo);
  }

  private JButton criarBotao(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(145, 42));
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  private void carregarPontosDistribuicao()
  {
    try
    {
      RetirarAguaSQL.carregarPontosDistribuicao(connection, modeloPontos);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os pontos de distribuição.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarFamiliasDoPonto(int codigoPd)
  {
    try
    {
      RetirarAguaSQL.carregarFamiliasDoPonto(connection, modeloFamilias, codigoPd);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar as famílias deste ponto.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarCotasDaFamilia(int codigoFb)
  {
    try
    {
      RetirarAguaSQL.carregarCotasDaFamilia(connection, modeloCotas, codigoFb);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar as cotas da família.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltroPontos()
  {
    if(sorterPontos == null || fieldPesquisaPontos == null || comboCriterioPontos == null)
      return;

    String texto = fieldPesquisaPontos.getText().trim();

    if(texto.isEmpty())
    {
      sorterPontos.setRowFilter(null);
      return;
    }

    int coluna = switch(comboCriterioPontos.getSelectedItem().toString())
    {
      case "Código" -> 0;
      case "Localização" -> 1;
      case "Tipo de Infraestrutura" -> 2;
      case "Fonte de Abastecimento" -> 4;
      case "Estado Operacional" -> 5;
      default -> 0;
    };

    sorterPontos.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private void aplicarFiltroFamilias()
  {
    if(sorterFamilias == null || fieldPesquisaFamilias == null || comboCriterioFamilias == null)
      return;

    String texto = fieldPesquisaFamilias.getText().trim();

    if(texto.isEmpty())
    {
      sorterFamilias.setRowFilter(null);
      return;
    }

    int coluna = switch(comboCriterioFamilias.getSelectedItem().toString())
    {
      case "Código" -> 0;
      case "Nome" -> 1;
      case "Contacto" -> 2;
      case "Número de Membros" -> 3;
      case "Perfil" -> 4;
      case "Estado" -> 5;
      default -> 0;
    };

    sorterFamilias.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private void aplicarFiltroCotas()
  {
    if(sorterCotas == null || fieldPesquisaCotas == null || comboCriterioCotas == null)
      return;

    String texto = fieldPesquisaCotas.getText().trim();

    if(texto.isEmpty())
    {
      sorterCotas.setRowFilter(null);
      return;
    }

    int coluna = switch(comboCriterioCotas.getSelectedItem().toString())
    {
      case "Código da Cota" -> 0;
      case "Validade" -> 3;
      case "Ajuste Sazonal" -> 4;
      case "Transferência" -> 5;
      case "Status" -> 6;
      default -> 0;
    };

    sorterCotas.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private String getCodigoPontoSelecionado()
  {
    int linhaView = tabelaPontos.getSelectedRow();

    if(linhaView == -1)
      return null;

    int linhaModel = tabelaPontos.convertRowIndexToModel(linhaView);
    return modeloPontos.getValueAt(linhaModel, 0).toString();
  }

  private String getEstadoPontoSelecionado()
  {
    int linhaView = tabelaPontos.getSelectedRow();

    if(linhaView == -1)
      return null;

    int linhaModel = tabelaPontos.convertRowIndexToModel(linhaView);
    Object estado = modeloPontos.getValueAt(linhaModel, 5);
    return estado == null ? null : estado.toString();
  }

  private boolean pontoSelecionadoPermiteRetirada()
  {
    String estado = getEstadoPontoSelecionado();

    if(estado == null || estado.trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Estado operacional do ponto de distribuição está indefinido.");
      return false;
    }

    String estadoNormalizado = estado.trim().toUpperCase();
    if(!estadoNormalizado.equals("ACTIVO") && !estadoNormalizado.equals("ATIVO")
        && !estadoNormalizado.equals("OPERACIONAL"))
    {
      JOptionPane.showMessageDialog(this,
          "Não é possível retirar água de um ponto de distribuição inactivo ou em manutenção.\n"
              + "As famílias podem continuar associadas a este ponto, mas a retirada fica bloqueada.");
      return false;
    }

    return true;
  }

  private String getCodigoFamiliaSelecionada()
  {
    int linhaView = tabelaFamilias.getSelectedRow();

    if(linhaView == -1)
      return null;

    int linhaModel = tabelaFamilias.convertRowIndexToModel(linhaView);
    return modeloFamilias.getValueAt(linhaModel, 0).toString();
  }

  private String getCodigoCotaSelecionada()
  {
    int linhaView = tabelaCotas.getSelectedRow();

    if(linhaView == -1)
      return null;

    int linhaModel = tabelaCotas.convertRowIndexToModel(linhaView);
    return modeloCotas.getValueAt(linhaModel, 0).toString();
  }

  private boolean validarFormularioRetirada()
  {
    if(fieldCodigoPdSelecionado.getText().trim().isEmpty() || fieldCodigoFbSelecionado.getText().trim().isEmpty()
        || fieldCodigoCotaSelecionada.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Faltam códigos seleccionados.");
      return false;
    }

    if(fieldNomePessoaColeta.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Informe o nome da pessoa que colecta.");
      return false;
    }

    if(fieldVolumeARetirar.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Informe o volume a retirar.");
      return false;
    }

    try
    {
      double volume = Double.parseDouble(fieldVolumeARetirar.getText().trim());
      if(volume <= 0)
      {
        JOptionPane.showMessageDialog(this, "O volume a retirar deve ser maior que zero.");
        return false;
      }
    } catch(NumberFormatException ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return false;

      JOptionPane.showMessageDialog(this, "Informe um volume a retirar válido.");
      return false;
    }

    return true;
  }

  private void registarRetiradaNoBanco()
  {
    try
    {
      String nomePessoaColeta = fieldNomePessoaColeta.getText().trim();
      double volumeARetirar = Double.parseDouble(fieldVolumeARetirar.getText().trim());
      String observacao = areaObservacao.getText().trim();

      int codigoPd = Integer.parseInt(fieldCodigoPdSelecionado.getText().trim());
      int codigoFb = Integer.parseInt(fieldCodigoFbSelecionado.getText().trim());
      int codigoCota = Integer.parseInt(fieldCodigoCotaSelecionada.getText().trim());

      RetirarAguaSQL.PreValidacaoRetirada preValidacao = RetirarAguaSQL.preValidarRetirada(connection, codigoPd,
          codigoFb, codigoCota, volumeARetirar);
      if(preValidacao == null)
        return;

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Retirada não pode continuar:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        actualizarDadosSelecionadosDepoisDaValidacao(codigoPd, codigoFb);
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Dados validados para continuação da operação:" + "\nEstado do ponto: " + preValidacao.estadoPonto
              + "\nVolume actual do ponto: " + preValidacao.volumeActualPonto + "\nSaldo actual da cota: "
              + preValidacao.saldoCota + "\nValidade da cota: " + preValidacao.validadeCota + "\nVolume a retirar: "
              + volumeARetirar + "\nMensagem: " + MensagensInterface.formatarMensagem(preValidacao.mensagem) + "\n\nDeseja executar a retirada?",
          "Confirmar Retirada", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      RetirarAguaSQL.ResultadoRetirada resultado = RetirarAguaSQL.registarRetirada(connection,
          new RetirarAguaSQL.DadosRetirada(nomePessoaColeta, volumeARetirar, observacao, codigoPd, codigoFb,
              codigoCota));

      if(resultado.falhou)
      {
        JOptionPane.showMessageDialog(this, "Retirada não executada:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        actualizarDadosSelecionadosDepoisDaValidacao(codigoPd, codigoFb);
        return;
      }

      if(resultado.pendente)
      {
        JOptionPane.showMessageDialog(this,
            "A operação foi registada como pendente e será processada posteriormente." + "\nCódigo da pendência: "
                + resultado.codigoRegistoConsumoGerado);
      }
      else
      {
        JOptionPane.showMessageDialog(this, "Retirada registada com sucesso." + "\nCódigo do registo de consumo: "
            + resultado.codigoRegistoConsumoGerado + "\nMensagem: " + MensagensInterface.formatarMensagem(resultado.mensagem));
      }

      limparTudo();
      carregarPontosDistribuicao();
      cardLayout.show(panelCards, "selecionar_ponto");
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível registar retirada:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void actualizarDadosSelecionadosDepoisDaValidacao(int codigoPd, int codigoFb)
  {
    carregarPontosDistribuicao();
    carregarFamiliasDoPonto(codigoPd);
    carregarCotasDaFamilia(codigoFb);
  }

  private void limparTudo()
  {
    fieldCodigoPontoFamilias.setText("");
    fieldCodigoPontoCotas.setText("");
    fieldCodigoFamiliaCotas.setText("");

    fieldCodigoPdSelecionado.setText("");
    fieldCodigoFbSelecionado.setText("");
    fieldCodigoCotaSelecionada.setText("");
    fieldNomePessoaColeta.setText("");
    fieldVolumeARetirar.setText("");
    areaObservacao.setText("");

    if(tabelaPontos != null)
      tabelaPontos.clearSelection();
    if(tabelaFamilias != null)
      tabelaFamilias.clearSelection();
    if(tabelaCotas != null)
      tabelaCotas.clearSelection();

    if(modeloFamilias != null)
      modeloFamilias.setRowCount(0);
    if(modeloCotas != null)
      modeloCotas.setRowCount(0);

    if(buttonProcederPonto != null)
      buttonProcederPonto.setEnabled(false);
    if(buttonProcederFamilia != null)
      buttonProcederFamilia.setEnabled(false);
    if(buttonProcederCota != null)
      buttonProcederCota.setEnabled(false);

    if(fieldPesquisaPontos != null)
      fieldPesquisaPontos.setText("");
    if(fieldPesquisaFamilias != null)
      fieldPesquisaFamilias.setText("");
    if(fieldPesquisaCotas != null)
      fieldPesquisaCotas.setText("");

    if(comboCriterioPontos != null)
      comboCriterioPontos.setSelectedIndex(0);
    if(comboCriterioFamilias != null)
      comboCriterioFamilias.setSelectedIndex(0);
    if(comboCriterioCotas != null)
      comboCriterioCotas.setSelectedIndex(0);
  }

  private class TratarSelecaoPontos implements ListSelectionListener
  {
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
      if(!e.getValueIsAdjusting())
        buttonProcederPonto.setEnabled(tabelaPontos.getSelectedRow() != -1);
    }
  }

  private class TratarSelecaoFamilias implements ListSelectionListener
  {
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
      if(!e.getValueIsAdjusting())
        buttonProcederFamilia.setEnabled(tabelaFamilias.getSelectedRow() != -1);
    }
  }

  private class TratarSelecaoCotas implements ListSelectionListener
  {
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
      if(!e.getValueIsAdjusting())
        buttonProcederCota.setEnabled(tabelaCotas.getSelectedRow() != -1);
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

  private class TratarPesquisaCotas implements DocumentListener
  {
    @Override
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroCotas();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroCotas();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroCotas();
    }
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      Object source = e.getSource();

      if(source == comboCriterioPontos)
      {
        aplicarFiltroPontos();
        return;
      }

      if(source == comboCriterioFamilias)
      {
        aplicarFiltroFamilias();
        return;
      }

      if(source == comboCriterioCotas)
      {
        aplicarFiltroCotas();
        return;
      }

      if(source == buttonActualizarPontos)
      {
        carregarPontosDistribuicao();
        aplicarFiltroPontos();

        if(tabelaPontos != null)
          tabelaPontos.clearSelection();

        buttonProcederPonto.setEnabled(false);
        return;
      }

      if(source == buttonActualizarFamilias)
      {
        if(fieldCodigoPontoFamilias.getText().trim().isEmpty())
        {
          JOptionPane.showMessageDialog(JPanel_RetirarAgua.this, "Seleccione primeiro um ponto de distribuição.");
          return;
        }

        carregarFamiliasDoPonto(Integer.parseInt(fieldCodigoPontoFamilias.getText().trim()));
        aplicarFiltroFamilias();

        if(tabelaFamilias != null)
          tabelaFamilias.clearSelection();

        buttonProcederFamilia.setEnabled(false);
        return;
      }

      if(source == buttonActualizarCotas)
      {
        if(fieldCodigoFamiliaCotas.getText().trim().isEmpty())
        {
          JOptionPane.showMessageDialog(JPanel_RetirarAgua.this, "Seleccione primeiro uma família.");
          return;
        }

        carregarCotasDaFamilia(Integer.parseInt(fieldCodigoFamiliaCotas.getText().trim()));
        aplicarFiltroCotas();

        if(tabelaCotas != null)
          tabelaCotas.clearSelection();

        buttonProcederCota.setEnabled(false);
        return;
      }

      if(source == buttonProcederPonto)
      {
        String codigoPonto = getCodigoPontoSelecionado();

        if(codigoPonto == null)
        {
          JOptionPane.showMessageDialog(JPanel_RetirarAgua.this, "Seleccione um ponto de distribuição.");
          return;
        }

        if(!pontoSelecionadoPermiteRetirada())
          return;

        fieldCodigoPontoFamilias.setText(codigoPonto);
        carregarFamiliasDoPonto(Integer.parseInt(codigoPonto));
        cardLayout.show(panelCards, "selecionar_familia");
        return;
      }

      if(source == buttonProcederFamilia)
      {
        String codigoFamilia = getCodigoFamiliaSelecionada();

        if(codigoFamilia == null)
        {
          JOptionPane.showMessageDialog(JPanel_RetirarAgua.this, "Seleccione uma família.");
          return;
        }

        fieldCodigoPontoCotas.setText(fieldCodigoPontoFamilias.getText());
        fieldCodigoFamiliaCotas.setText(codigoFamilia);
        carregarCotasDaFamilia(Integer.parseInt(codigoFamilia));
        cardLayout.show(panelCards, "selecionar_cota");
        return;
      }

      if(source == buttonProcederCota)
      {
        String codigoCota = getCodigoCotaSelecionada();

        if(codigoCota == null)
        {
          JOptionPane.showMessageDialog(JPanel_RetirarAgua.this, "Seleccione uma cota.");
          return;
        }

        fieldCodigoPdSelecionado.setText(fieldCodigoPontoCotas.getText());
        fieldCodigoFbSelecionado.setText(fieldCodigoFamiliaCotas.getText());
        fieldCodigoCotaSelecionada.setText(codigoCota);
        cardLayout.show(panelCards, "formulario_retirada");
        return;
      }

      if(source == buttonVoltarPontos)
      {
        cardLayout.show(panelCards, "selecionar_ponto");
        return;
      }

      if(source == buttonVoltarFamilias)
      {
        cardLayout.show(panelCards, "selecionar_familia");
        return;
      }

      if(source == buttonVoltarCotas)
      {
        cardLayout.show(panelCards, "selecionar_cota");
        return;
      }

      if(source == buttonRegistarRetirada)
      {
        if(!validarFormularioRetirada())
          return;

        registarRetiradaNoBanco();
        return;
      }

      if(source == buttonVoltarMenu)
      {
        voltarAoDashboard();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }

  private static class RoundedPanel extends JPanel
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
