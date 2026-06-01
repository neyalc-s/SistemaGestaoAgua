package View_Interface_FamiliasCotasDB;

import Resources.MensagensInterface;

import Repository_SQL.FamiliasCotasDB.RegistarFamiliaSQL;
import Resources.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

public class JPanel_RegistarFamilia extends JPanel
{

  private static final String CARD_SELECIONAR_PONTO = "selecionar_ponto";
  private static final String CARD_NECESSIDADES = "necessidades";
  private static final String CARD_DADOS_FAMILIA = "dados_familia";

  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  private CardLayout cardLayout;
  private JPanel panelCards;

  private JTable tabelaPontos;
  private JTable tabelaNecessidades;
  private DefaultTableModel modeloTabela;
  private DefaultTableModel modeloTabelaNecessidades;
  private TableRowSorter<DefaultTableModel> sorter;
  private TableRowSorter<DefaultTableModel> sorterNecessidades;
  private boolean dadosPontosCarregados = false;
  private boolean dadosNecessidadesCarregados = false;

  private JTextField fieldPesquisa;
  private JTextField fieldPesquisaNecessidades;
  private JComboBox<String> comboCriterioPesquisa;
  private JComboBox<String> comboCriterioPesquisaNecessidades;
  private JLabel labelEstadoPesquisa;
  private JLabel labelEstadoPesquisaNecessidades;

  private JButton buttonActualizarPontos;
  private JButton buttonMostrarTodosPontos;
  private JButton buttonRecarregarPontos;
  private JButton buttonProceder;
  private JButton buttonVoltar;
  private JButton buttonActualizarNecessidades;
  private JButton buttonMostrarTodasNecessidades;
  private JButton buttonRecarregarNecessidades;
  private JButton buttonInserirNecessidade;
  private JButton buttonVoltarParaPontosNecessidades;
  private JButton buttonContinuarParaDadosFamilia;

  private JTextField fieldCodigoPontoSelecionado;
  private JTextField fieldAldeia;
  private JTextField fieldCoordenadasGps;
  private JTextField fieldNomeResponsavel;
  private JTextField fieldContacto;
  private JTextField fieldVolumeSemanalOpcional;
  private JTextField fieldNecessidadesSelecionadas;

  private JSpinner spinnerNumMembros;
  private JComboBox<String> comboPerfilSocioeconomico;
  private JComboBox<String> comboAjusteSazonal;
  private JComboBox<String> comboTransferenciaAutorizada;

  private JButton buttonVoltarParaPontos;
  private JButton buttonRegistarFamilia;

  private boolean ultimaPesquisaPontosFoiMostrarTodos = true;
  private String ultimoTextoPesquisaPontos = "";
  private String ultimoCriterioPesquisaPontos = "Código";
  private boolean existeUltimaPesquisaPontos = false;
  private boolean ultimaPesquisaNecessidadesFoiMostrarTodos = true;
  private String ultimoTextoPesquisaNecessidades = "";
  private String ultimoCriterioPesquisaNecessidades = "Código";
  private boolean existeUltimaPesquisaNecessidades = false;
  private final Runnable voltarDashboard;

  public JPanel_RegistarFamilia(Connection connection)
  {
    this(connection, false, null);
  }

  public JPanel_RegistarFamilia(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_RegistarFamilia(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);

    panelCards.add(criarCardSelecionarPonto(), CARD_SELECIONAR_PONTO);
    panelCards.add(criarCardNecessidades(), CARD_NECESSIDADES);
    panelCards.add(criarCardDadosFamilia(), CARD_DADOS_FAMILIA);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(18, 18, 18, 18);

    add(panelCards, gbc);

    actualizarEstadoPesquisaPontos();
    actualizarEstadoPesquisaNecessidades();

    if(carregarTabelasAutomaticamente)
    {
      carregarPontosDistribuicao();
      carregarNecessidades();
    }
  }

  // ========================= INICIO DO CODIGO DE INTERFACE GRAFICA
  // =========================

  private JPanel criarCardSelecionarPonto()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1180, 720));

    card.add(criarPainelTopoSelecao(), BorderLayout.NORTH);
    card.add(criarPainelCentroSelecao(), BorderLayout.CENTER);
    card.add(criarPainelRodapeSelecao(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;

    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCardDadosFamilia()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1180, 720));

    card.add(criarPainelTopoFormulario(), BorderLayout.NORTH);
    card.add(criarPainelCentroFormulario(), BorderLayout.CENTER);
    card.add(criarPainelRodapeFormulario(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;

    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCardNecessidades()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1180, 720));

    card.add(criarPainelTopoNecessidades(), BorderLayout.NORTH);
    card.add(criarPainelCentroNecessidades(), BorderLayout.CENTER);
    card.add(criarPainelRodapeNecessidades(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;

    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarPainelTopoSelecao()
  {
    return criarTopoComIcone(FontAwesomeSolid.MAP_MARKED_ALT, "Registar Família",
        "<html>Primeiro, selecione o ponto de distribuição ao qual a família ficará associada.</html>",
        30);
  }

  private JPanel criarPainelCentroSelecao()
  {
    JPanel painelCentro = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel labelTabela = InterfaceGraficaUtils.criarLabel("Pontos de Distribuição Disponíveis",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL);

    JPanel painelTopoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    painelTopoTabela.add(labelTabela, BorderLayout.NORTH);
    painelTopoTabela.add(criarPainelPesquisa(), BorderLayout.SOUTH);

    painelCentro.add(painelTopoTabela, BorderLayout.NORTH);
    painelCentro.add(criarScrollTabela(), BorderLayout.CENTER);

    return painelCentro;
  }

  private JPanel criarPainelRodapeSelecao()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("Seleccione uma linha da tabela para proceder.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));

    buttonVoltar = criarBotao("Voltar");
    buttonProceder = criarBotao("Proceder");
    buttonProceder.setEnabled(false);

    buttonVoltar.addActionListener(tratarButtons);
    buttonProceder.addActionListener(tratarButtons);

    painelBotoes.add(buttonVoltar);
    painelBotoes.add(buttonProceder);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarPainelTopoFormulario()
  {
    return criarTopoComIcone(FontAwesomeSolid.USER_EDIT, "Dados da Família",
        "<html>Preencha os dados da família e clique em Registar.</html>", 30);
  }

  private JPanel criarPainelTopoNecessidades()
  {
    return criarTopoComIcone(FontAwesomeSolid.LIST_UL, "Necessidades da Família",
        "<html>Seleccione zero, uma ou várias necessidades específicas para associar à família.</html>",
        28);
  }

  private JPanel criarPainelCentroNecessidades()
  {
    JPanel painelCentro = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel labelTabela = InterfaceGraficaUtils.criarLabel("Necessidades Disponíveis",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL);

    JPanel painelTopoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    painelTopoTabela.add(labelTabela, BorderLayout.NORTH);
    painelTopoTabela.add(criarPainelPesquisaNecessidades(), BorderLayout.SOUTH);

    painelCentro.add(painelTopoTabela, BorderLayout.NORTH);
    painelCentro.add(criarScrollTabelaNecessidades(), BorderLayout.CENTER);

    return painelCentro;
  }

  private JPanel criarPainelRodapeNecessidades()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("A seleção é opcional. Use Ctrl ou Shift para selecionar várias linhas.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));

    buttonVoltarParaPontosNecessidades = criarBotao("Voltar");
    buttonContinuarParaDadosFamilia = criarBotao("Proceder");

    buttonVoltarParaPontosNecessidades.addActionListener(tratarButtons);
    buttonContinuarParaDadosFamilia.addActionListener(tratarButtons);

    painelBotoes.add(buttonVoltarParaPontosNecessidades);
    painelBotoes.add(buttonContinuarParaDadosFamilia);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarPainelCentroFormulario()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldCodigoPontoSelecionado = new JTextField(20);
    fieldCodigoPontoSelecionado.setEditable(false);

    fieldAldeia = new JTextField(20);
    fieldCoordenadasGps = new JTextField(20);
    fieldNomeResponsavel = new JTextField(20);
    fieldContacto = new JTextField(20);
    fieldVolumeSemanalOpcional = new JTextField(20);
    fieldNecessidadesSelecionadas = new JTextField(20);
    fieldNecessidadesSelecionadas.setEditable(false);

    spinnerNumMembros = InterfaceGraficaUtils.criarSpinnerNumero(1, 1, 1000, 1, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(110, 36));
    spinnerNumMembros.setMinimumSize(new Dimension(110, 36));

    comboPerfilSocioeconomico = new JComboBox<>(new String[]
      {
          "Baixa renda", "Media renda", "Alta renda"
      });

    comboAjusteSazonal = new JComboBox<>(new String[]
      {
          "Verão", "Inverno", "Chuvoso", "Seco", "Transição Seca", "Pico Seco", "Início Chuvoso", "Alta Demanda",
          "Fim de Ano", "Normal"
      });

    comboTransferenciaAutorizada = new JComboBox<>(new String[]
      {
          "SIM", "NAO"
      });

    estilizarCampo(fieldCodigoPontoSelecionado);
    estilizarCampo(fieldAldeia);
    estilizarCampo(fieldCoordenadasGps);
    estilizarCampo(fieldNomeResponsavel);
    estilizarCampo(fieldContacto);
    estilizarCampo(fieldVolumeSemanalOpcional);
    estilizarCampo(fieldNecessidadesSelecionadas);
    estilizarCombo(comboPerfilSocioeconomico);
    estilizarCombo(comboAjusteSazonal);
    estilizarCombo(comboTransferenciaAutorizada);

    int linha = 0;
    adicionarLinhaFormulario(painel, linha++, "", 0, "Código do ponto seleccionado:", fieldCodigoPontoSelecionado);
    adicionarLinhaFormulario(painel, linha++, "", 0, "Necessidades escolhidas:", fieldNecessidadesSelecionadas);
    adicionarLinhaFormulario(painel, linha++, "", 0, "Aldeia:", fieldAldeia);
    adicionarLinhaFormulario(painel, linha++, "", 0, "Coordenadas GPS:", fieldCoordenadasGps);
    adicionarLinhaFormulario(painel, linha++, "", 0, "Nome do responsável:", fieldNomeResponsavel);
    adicionarLinhaFormulario(painel, linha++, "", 0, "Contacto:", fieldContacto);
    adicionarLinhaFormulario(painel, linha++, "", 0, "Número de membros:", spinnerNumMembros);
    adicionarLinhaFormulario(painel, linha++, "", 0, "Perfil socioeconómico:",
        comboPerfilSocioeconomico);
    adicionarLinhaFormulario(painel, linha++, "", 0, "Ajuste sazonal:", comboAjusteSazonal);
    adicionarLinhaFormulario(painel, linha++, "", 0, "Transferência autorizada:",
        comboTransferenciaAutorizada);
    adicionarLinhaFormulario(painel, linha++, "", 0, "Volume semanal (Opcional):",
        fieldVolumeSemanalOpcional);

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = linha;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);

    return painel;
  }

  private JPanel criarPainelRodapeFormulario()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("Confira os dados antes de registar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));

    buttonVoltarParaPontos = criarBotao("Voltar");
    buttonRegistarFamilia = criarBotao("Registar");

    buttonVoltarParaPontos.addActionListener(tratarButtons);
    buttonRegistarFamilia.addActionListener(tratarButtons);

    painelBotoes.add(buttonVoltarParaPontos);
    painelBotoes.add(buttonRegistarFamilia);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarPainelPesquisa()
  {
    JPanel painelPesquisa = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 6));
    JPanel linhaPesquisa = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    JLabel labelPesquisar = InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO);

    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(20, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(220, 36));

    comboCriterioPesquisa = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código", "Localização", "Tipo de Infraestrutura", "Fonte de Abastecimento", "Estado Operacional"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(220, 36));

    labelEstadoPesquisa = InterfaceGraficaUtils.criarLabel("", InterfaceGraficaUtils.FONT_AJUDA,
        InterfaceGraficaUtils.COR_SUBTEXTO);

    buttonActualizarPontos = criarBotao("Pesquisar");
    buttonActualizarPontos.setPreferredSize(new Dimension(130, 36));

    buttonMostrarTodosPontos = criarBotao("Mostrar Todos");
    buttonMostrarTodosPontos.setPreferredSize(new Dimension(160, 36));

    buttonRecarregarPontos = criarBotao("Actualizar");
    buttonRecarregarPontos.setPreferredSize(new Dimension(140, 36));
    buttonRecarregarPontos.setEnabled(false);

    fieldPesquisa.getDocument().addDocumentListener(new TratarPesquisa());
    comboCriterioPesquisa.addActionListener(tratarButtons);
    buttonActualizarPontos.addActionListener(tratarButtons);
    buttonMostrarTodosPontos.addActionListener(tratarButtons);
    buttonRecarregarPontos.addActionListener(tratarButtons);

    linhaPesquisa.add(labelPesquisar);
    linhaPesquisa.add(fieldPesquisa);
    linhaPesquisa.add(comboCriterioPesquisa);
    linhaPesquisa.add(buttonActualizarPontos);
    linhaPesquisa.add(buttonMostrarTodosPontos);
    linhaPesquisa.add(buttonRecarregarPontos);

    painelPesquisa.add(linhaPesquisa, BorderLayout.NORTH);
    painelPesquisa.add(labelEstadoPesquisa, BorderLayout.SOUTH);

    return painelPesquisa;
  }

  private JPanel criarPainelPesquisaNecessidades()
  {
    JPanel painelPesquisa = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 6));
    JPanel linhaPesquisa = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    JLabel labelPesquisar = InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO);

    fieldPesquisaNecessidades = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(220, 36));

    comboCriterioPesquisaNecessidades = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código", "Descrição"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(150, 36));

    labelEstadoPesquisaNecessidades = InterfaceGraficaUtils.criarLabel("", InterfaceGraficaUtils.FONT_AJUDA,
        InterfaceGraficaUtils.COR_SUBTEXTO);

    buttonActualizarNecessidades = criarBotao("Pesquisar");
    buttonActualizarNecessidades.setPreferredSize(new Dimension(130, 36));

    buttonMostrarTodasNecessidades = criarBotao("Mostrar Todos");
    buttonMostrarTodasNecessidades.setPreferredSize(new Dimension(160, 36));

    buttonRecarregarNecessidades = criarBotao("Actualizar");
    buttonRecarregarNecessidades.setPreferredSize(new Dimension(140, 36));
    buttonRecarregarNecessidades.setEnabled(false);

    buttonInserirNecessidade = criarBotao("Nova Necess.");
    buttonInserirNecessidade.setPreferredSize(new Dimension(155, 36));

    fieldPesquisaNecessidades.getDocument().addDocumentListener(new TratarPesquisaNecessidades());
    comboCriterioPesquisaNecessidades.addActionListener(tratarButtons);
    buttonActualizarNecessidades.addActionListener(tratarButtons);
    buttonMostrarTodasNecessidades.addActionListener(tratarButtons);
    buttonRecarregarNecessidades.addActionListener(tratarButtons);
    buttonInserirNecessidade.addActionListener(tratarButtons);

    linhaPesquisa.add(labelPesquisar);
    linhaPesquisa.add(fieldPesquisaNecessidades);
    linhaPesquisa.add(comboCriterioPesquisaNecessidades);
    linhaPesquisa.add(buttonActualizarNecessidades);
    linhaPesquisa.add(buttonMostrarTodasNecessidades);
    linhaPesquisa.add(buttonRecarregarNecessidades);
    linhaPesquisa.add(buttonInserirNecessidade);

    painelPesquisa.add(linhaPesquisa, BorderLayout.NORTH);
    painelPesquisa.add(labelEstadoPesquisaNecessidades, BorderLayout.SOUTH);

    return painelPesquisa;
  }

  private JScrollPane criarScrollTabela()
  {
    String[] colunas =
      {
          "Código", "Localização", "Tipo de Infraestrutura", "Capacidade", "Fonte de Abastecimento",
          "Estado Operacional"
      };

    modeloTabela = new DefaultTableModel(colunas, 0)
      {
        @Override
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };

    tabelaPontos = criarTabelaComMensagemVazia("Pesquise pontos ou clique Mostrar Todos para carregar dados.",
        "Nenhum ponto encontrado no filtro actual. Altere o texto ou clique Pesquisar.");
    tabelaPontos.setModel(modeloTabela);
    tabelaPontos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    sorter = new TableRowSorter<>(modeloTabela);
    tabelaPontos.setRowSorter(sorter);

    tabelaPontos.getSelectionModel().addListSelectionListener(new TratarSelecaoTabela());

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaPontos, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1100, 470));

    return scroll;
  }

  private JScrollPane criarScrollTabelaNecessidades()
  {
    String[] colunas =
      {
          "Código", "Descrição"
      };

    modeloTabelaNecessidades = new DefaultTableModel(colunas, 0)
      {
        @Override
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };

    tabelaNecessidades = criarTabelaComMensagemVazia(
        "Pesquise necessidades ou clique Mostrar Todos para carregar dados.",
        "Nenhuma necessidade encontrada no filtro actual. Altere o texto ou clique Pesquisar.");
    tabelaNecessidades.setModel(modeloTabelaNecessidades);
    tabelaNecessidades.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    sorterNecessidades = new TableRowSorter<>(modeloTabelaNecessidades);
    tabelaNecessidades.setRowSorter(sorterNecessidades);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaNecessidades,
        InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1100, 470));

    return scroll;
  }

  private JTable criarTabelaComMensagemVazia(String mensagemSemDados, String mensagemSemFiltro)
  {
    JTable tabela = new JTable()
      {
        @Override
        protected void paintComponent(Graphics g)
        {
          super.paintComponent(g);

          if(getRowCount() > 0)
            return;

          Object mensagemPersonalizada = getClientProperty("mensagemTabelaVazia");
          boolean temDadosCarregados = getModel() != null && getModel().getRowCount() > 0;
          String mensagem = mensagemPersonalizada == null ? (temDadosCarregados ? mensagemSemFiltro : mensagemSemDados)
              : mensagemPersonalizada.toString();

          Graphics2D g2 = (Graphics2D) g.create();
          g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g2.setFont(InterfaceGraficaUtils.FONT_AJUDA.deriveFont(Font.BOLD, 15f));
          g2.setColor(InterfaceGraficaUtils.COR_SUBTEXTO);

          FontMetrics metrics = g2.getFontMetrics();
          int x = Math.max(16, (getWidth() - metrics.stringWidth(mensagem)) / 2);
          int y = Math.max(metrics.getHeight(), (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent());

          g2.drawString(mensagem, x, y);
          g2.dispose();
        }
      };

    InterfaceGraficaUtils.configurarTabela(tabela, InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabela.setFillsViewportHeight(true);
    return tabela;
  }

  private void carregarPontosDistribuicao()
  {
    try
    {
      if(sorter != null)
        sorter.setRowFilter(null);

      RegistarFamiliaSQL.carregarPontosDistribuicao(connection, modeloTabela);
      dadosPontosCarregados = modeloTabela.getRowCount() > 0;
      ultimaPesquisaPontosFoiMostrarTodos = true;
      ultimoTextoPesquisaPontos = "";
      ultimoCriterioPesquisaPontos = comboCriterioPesquisa.getSelectedItem().toString();
      existeUltimaPesquisaPontos = true;
      actualizarEstadoPesquisaPontos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os pontos de distribuição.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarNecessidades()
  {
    try
    {
      if(sorterNecessidades != null)
        sorterNecessidades.setRowFilter(null);

      RegistarFamiliaSQL.carregarNecessidades(connection, modeloTabelaNecessidades);
      dadosNecessidadesCarregados = modeloTabelaNecessidades.getRowCount() > 0;
      ultimaPesquisaNecessidadesFoiMostrarTodos = true;
      ultimoTextoPesquisaNecessidades = "";
      ultimoCriterioPesquisaNecessidades = comboCriterioPesquisaNecessidades.getSelectedItem().toString();
      existeUltimaPesquisaNecessidades = true;
      actualizarEstadoPesquisaNecessidades();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar necessidades:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void adicionarLinhaFormulario(JPanel painel, int linha, String nomeTabela, int linhasTabela, String textoLabel,
      JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    JLabel label = new JLabel(textoLabel);
    label.setFont(InterfaceGraficaUtils.FONT_LABEL_FORM);
    label.setForeground(InterfaceGraficaUtils.COR_TEXTO);

    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(8, 0, 8, 18);
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
    Dimension tamanho = new Dimension(320, 36);
    campo.setPreferredSize(tamanho);
    campo.setMinimumSize(tamanho);
    campo.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    InterfaceGraficaUtils.aplicarCantosArredondados(campo);
  }

  private void estilizarCombo(JComboBox<String> combo)
  {
    Dimension tamanho = new Dimension(320, 36);
    combo.setPreferredSize(tamanho);
    combo.setMinimumSize(tamanho);
    combo.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    InterfaceGraficaUtils.aplicarCantosArredondados(combo);
  }

  private JButton criarBotao(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(145, 42));
    IconesInterface.aplicarIconeBotao(botao);
    Resources.IconesInterface.aplicarEfeitoHoverBotaoFamilias(botao);
    return botao;
  }

  private JPanel criarTopoComIcone(FontAwesomeSolid icone, String tituloTexto, String subtituloHtml, int tamanhoIcone)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(14, 0));

    JLabel labelIcone = new JLabel(criarIcone(icone, tamanhoIcone, InterfaceGraficaUtils.COR_AZUL));
    labelIcone.setBorder(new EmptyBorder(3, 0, 0, 0));
    labelIcone.setVerticalAlignment(SwingConstants.TOP);

    painel.add(labelIcone, BorderLayout.WEST);
    painel.add(InterfaceGraficaUtils.criarTopo(tituloTexto, subtituloHtml, InterfaceGraficaUtils.FONT_TITULO,
        InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO, InterfaceGraficaUtils.COR_SUBTEXTO),
        BorderLayout.CENTER);

    return painel;
  }

  private FontIcon criarIcone(FontAwesomeSolid icone, int tamanho, Color cor)
  {
    return FontIcon.of(icone, tamanho, cor);
  }

  // ========================== FIM DO CODIGO DE INTERFACE GRAFICA
  // ==========================

  // ========================== INICIO DA LOGICA DA INTERFACE GRAFICA
  // ==========================

  private void aplicarFiltro()
  {
    if(sorter == null || fieldPesquisa == null || comboCriterioPesquisa == null)
      return;

    if(!dadosPontosCarregados || modeloTabela == null || modeloTabela.getRowCount() == 0)
      return;

    String texto = fieldPesquisa.getText().trim();

    if(texto.isEmpty())
    {
      sorter.setRowFilter(null);
      actualizarEstadoPesquisaPontos();
      return;
    }

    int coluna = getIndiceColunaSelecionada();
    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
    actualizarEstadoPesquisaPontos();
  }

  private void aplicarFiltroNecessidades()
  {
    if(sorterNecessidades == null || fieldPesquisaNecessidades == null || comboCriterioPesquisaNecessidades == null)
      return;

    if(!dadosNecessidadesCarregados || modeloTabelaNecessidades == null || modeloTabelaNecessidades.getRowCount() == 0)
      return;

    String texto = fieldPesquisaNecessidades.getText().trim();

    if(texto.isEmpty())
    {
      sorterNecessidades.setRowFilter(null);
      actualizarEstadoPesquisaNecessidades();
      return;
    }

    sorterNecessidades.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto),
        getIndiceColunaNecessidadeSelecionada()));
    actualizarEstadoPesquisaNecessidades();
  }

  private int getIndiceColunaSelecionada()
  {
    String criterio = comboCriterioPesquisa.getSelectedItem().toString();

    switch(criterio)
    {
      case "Código":
        return 0;
      case "Localização":
        return 1;
      case "Tipo de Infraestrutura":
        return 2;
      case "Fonte de Abastecimento":
        return 4;
      case "Estado Operacional":
        return 5;
      default:
        return 0;
    }
  }

  private int getIndiceColunaNecessidadeSelecionada()
  {
    String criterio = comboCriterioPesquisaNecessidades.getSelectedItem().toString();

    switch(criterio)
    {
      case "Código":
        return 0;
      case "Descrição":
        return 1;
      default:
        return 1;
    }
  }

  private void pesquisarPontosDistribuicao()
  {
    String texto = fieldPesquisa.getText().trim();

    if(texto.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Digite um critério para pesquisar pontos de distribuição na base de dados.");
      return;
    }

    try
    {
      sorter.setRowFilter(null);
      RegistarFamiliaSQL.pesquisarPontosDistribuicao(connection, modeloTabela,
          comboCriterioPesquisa.getSelectedItem().toString(), texto);
      dadosPontosCarregados = modeloTabela.getRowCount() > 0;
      ultimaPesquisaPontosFoiMostrarTodos = false;
      ultimoTextoPesquisaPontos = texto;
      ultimoCriterioPesquisaPontos = comboCriterioPesquisa.getSelectedItem().toString();
      existeUltimaPesquisaPontos = true;
      actualizarEstadoPesquisaPontos();
      tabelaPontos.clearSelection();
      buttonProceder.setEnabled(false);

      if(!dadosPontosCarregados)
        JOptionPane.showMessageDialog(this, "Nenhum ponto de distribuição encontrado para a pesquisa informada.");
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível pesquisar pontos de distribuição:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void pesquisarNecessidades()
  {
    String texto = fieldPesquisaNecessidades.getText().trim();

    if(texto.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Digite um critério para pesquisar necessidades na base de dados.");
      return;
    }

    try
    {
      sorterNecessidades.setRowFilter(null);
      RegistarFamiliaSQL.pesquisarNecessidades(connection, modeloTabelaNecessidades,
          comboCriterioPesquisaNecessidades.getSelectedItem().toString(), texto);
      dadosNecessidadesCarregados = modeloTabelaNecessidades.getRowCount() > 0;
      ultimaPesquisaNecessidadesFoiMostrarTodos = false;
      ultimoTextoPesquisaNecessidades = texto;
      ultimoCriterioPesquisaNecessidades = comboCriterioPesquisaNecessidades.getSelectedItem().toString();
      existeUltimaPesquisaNecessidades = true;
      actualizarEstadoPesquisaNecessidades();
      tabelaNecessidades.clearSelection();

      if(!dadosNecessidadesCarregados)
        JOptionPane.showMessageDialog(this, "Nenhuma necessidade encontrada para a pesquisa informada.");
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível pesquisar necessidades:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void recarregarUltimaPesquisaPontos()
  {
    if(!existeUltimaPesquisaPontos)
      return;

    if(ultimaPesquisaPontosFoiMostrarTodos)
    {
      carregarPontosDistribuicao();
      return;
    }

    try
    {
      if(sorter != null)
        sorter.setRowFilter(null);

      RegistarFamiliaSQL.pesquisarPontosDistribuicao(connection, modeloTabela, ultimoCriterioPesquisaPontos,
          ultimoTextoPesquisaPontos);
      dadosPontosCarregados = modeloTabela.getRowCount() > 0;
      actualizarEstadoPesquisaPontos();

      if(tabelaPontos != null)
        tabelaPontos.clearSelection();

      if(buttonProceder != null)
        buttonProceder.setEnabled(false);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível actualizar pontos de distribuição:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void recarregarUltimaPesquisaNecessidades()
  {
    if(!existeUltimaPesquisaNecessidades)
      return;

    if(ultimaPesquisaNecessidadesFoiMostrarTodos)
    {
      carregarNecessidades();
      return;
    }

    try
    {
      if(sorterNecessidades != null)
        sorterNecessidades.setRowFilter(null);

      RegistarFamiliaSQL.pesquisarNecessidades(connection, modeloTabelaNecessidades,
          ultimoCriterioPesquisaNecessidades, ultimoTextoPesquisaNecessidades);
      dadosNecessidadesCarregados = modeloTabelaNecessidades.getRowCount() > 0;
      actualizarEstadoPesquisaNecessidades();

      if(tabelaNecessidades != null)
        tabelaNecessidades.clearSelection();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível actualizar necessidades:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void actualizarEstadoPesquisaPontos()
  {
    if(labelEstadoPesquisa == null)
      return;

    int linhas = modeloTabela == null ? 0 : modeloTabela.getRowCount();
    int visiveis = tabelaPontos == null ? 0 : tabelaPontos.getRowCount();
    labelEstadoPesquisa.setText("Resultados: " + visiveis + " de " + linhas
        + ". Pesquise ou clique em Mostrar Todos.");

    if(buttonRecarregarPontos != null)
      buttonRecarregarPontos.setEnabled(existeUltimaPesquisaPontos);
  }

  private void actualizarEstadoPesquisaNecessidades()
  {
    if(labelEstadoPesquisaNecessidades == null)
      return;

    int linhas = modeloTabelaNecessidades == null ? 0 : modeloTabelaNecessidades.getRowCount();
    int visiveis = tabelaNecessidades == null ? 0 : tabelaNecessidades.getRowCount();
    labelEstadoPesquisaNecessidades.setText("Resultados: " + visiveis + " de " + linhas
        + ". Pesquise ou clique em Mostrar Todos.");

    if(buttonRecarregarNecessidades != null)
      buttonRecarregarNecessidades.setEnabled(existeUltimaPesquisaNecessidades);
  }

  public String getCodigoPontoSelecionado()
  {
    int linhaView = tabelaPontos.getSelectedRow();

    if(linhaView == -1)
      return null;

    int linhaModel = tabelaPontos.convertRowIndexToModel(linhaView);
    return modeloTabela.getValueAt(linhaModel, 0).toString();
  }

  private List<Integer> getCodigosNecessidadesSelecionadas()
  {
    List<Integer> codigos = new ArrayList<Integer>();

    if(tabelaNecessidades == null)
      return codigos;

    int[] linhasSelecionadas = tabelaNecessidades.getSelectedRows();

    for(int linhaView : linhasSelecionadas)
    {
      int linhaModel = tabelaNecessidades.convertRowIndexToModel(linhaView);
      Object valor = modeloTabelaNecessidades.getValueAt(linhaModel, 0);
      codigos.add(Integer.valueOf(valor.toString()));
    }

    return codigos;
  }

  private void actualizarCampoNecessidadesSelecionadas()
  {
    List<Integer> codigos = getCodigosNecessidadesSelecionadas();

    if(codigos.isEmpty())
    {
      fieldNecessidadesSelecionadas.setText("Nenhuma necessidade seleccionada");
      return;
    }

    StringBuilder texto = new StringBuilder();

    for(int i = 0; i < codigos.size(); i++)
    {
      if(i > 0)
        texto.append(", ");

      texto.append(codigos.get(i));
    }

    fieldNecessidadesSelecionadas.setText(texto.toString());
  }

  private boolean validarCamposFormulario()
  {
    if(fieldCodigoPontoSelecionado.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Nenhum ponto de distribuição foi seleccionado.");
      return false;
    }

    if(fieldAldeia.getText().trim().isEmpty() || fieldCoordenadasGps.getText().trim().isEmpty()
        || fieldNomeResponsavel.getText().trim().isEmpty() || fieldContacto.getText().trim().isEmpty()
        || spinnerNumMembros == null)
    {
      JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios antes de continuar.");
      return false;
    }

    try
    {
      spinnerNumMembros.commitEdit();
      int numMembros = ((Number) spinnerNumMembros.getValue()).intValue();
      if(numMembros <= 0)
      {
        JOptionPane.showMessageDialog(this, "O número de membros deve ser maior que zero.");
        return false;
      }
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return false;

      JOptionPane.showMessageDialog(this, "Número de membros inválido.");
      return false;
    }

    String contacto = fieldContacto.getText().trim();
    if(!contacto.matches("\\d+"))
    {
      JOptionPane.showMessageDialog(this, "Contacto inválido. Use apenas números.");
      return false;
    }

    try
    {
      long contactoNumero = Long.parseLong(contacto);
      if(contactoNumero < 820000000L || contactoNumero > 879999999L)
      {
        JOptionPane.showMessageDialog(this, "Contacto inválido. Deve estar entre 820000000 e 879999999.");
        return false;
      }
    } catch(NumberFormatException ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return false;

      JOptionPane.showMessageDialog(this, "Contacto inválido.");
      return false;
    }

    if(!fieldVolumeSemanalOpcional.getText().trim().isEmpty())
    {
      try
      {
        double volume = Double.parseDouble(fieldVolumeSemanalOpcional.getText().trim());
        if(volume <= 0)
        {
          JOptionPane.showMessageDialog(this, "O volume semanal opcional deve ser maior que zero.");
          return false;
        }
      } catch(NumberFormatException ex)
      {
        if(Resources.TratadorConexaoFechada.tratar(null, ex))
          return false;

        JOptionPane.showMessageDialog(this, "Volume semanal opcional inválido.");
        return false;
      }
    }

    return true;
  }

  private void registarFamiliaNoBanco()
  {
    try
    {
      RegistarFamiliaSQL.DadosRegistoFamilia dados = criarDadosRegistoFamilia();
      List<Integer> necessidadesSelecionadas = getCodigosNecessidadesSelecionadas();
      RegistarFamiliaSQL.PreValidacaoRegistoFamilia preValidacao = RegistarFamiliaSQL
          .preValidarRegistoFamilia(connection, dados, necessidadesSelecionadas);

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Família não pode ser registada:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        recarregarUltimaPesquisaPontos();
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Dados actuais confirmados na base de dados:" + "\nEstado do ponto: " + preValidacao.estadoPonto
              + "\nVolume actual do ponto: " + preValidacao.volumeActualPonto + "\nCota semanal calculada: "
              + preValidacao.volumeCalculado + "\nFamília: " + dados.nomeResponsavelFb + "\nNúmero de membros: "
              + dados.numMembrosFb + "\nAldeia: " + dados.aldeia + "\nCoordenadas GPS: " + dados.coordenadasGps
              + "\nNecessidades seleccionadas: " + necessidadesSelecionadas.size()
              + "\n\nDeseja registar a família?",
          "Confirmar Registo de Família", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      RegistarFamiliaSQL.ResultadoRegistoFamilia resultado = RegistarFamiliaSQL.registarFamilia(connection, dados);

      if(resultado.falhou)
      {
        JOptionPane.showMessageDialog(this, "Família não registada:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        recarregarUltimaPesquisaPontos();
        return;
      }

      try
      {
        RegistarFamiliaSQL.associarNecessidadesFamilia(connection, resultado.codigoFamiliaGerado,
            necessidadesSelecionadas);
      } catch(Exception ex)
      {
        if(Resources.TratadorConexaoFechada.tratar(null, ex))
          return;

        JOptionPane.showMessageDialog(this,
            "Família registada, mas não foi possível associar as necessidades seleccionadas:\n" + MensagensInterface.formatarErro(ex));
      }

      JOptionPane.showMessageDialog(this,
          "Família registada com sucesso." + "\nCódigo da família: " + resultado.codigoFamiliaGerado
              + "\nCódigo da cota: " + resultado.codigoCotaGerada + "\nCódigo da localização: "
              + resultado.codigoLocalizacaoGerada + "\nVolume calculado: " + resultado.volumeCalculado + "\nMensagem: "
              + MensagensInterface.formatarMensagem(resultado.mensagem));

      limparFormulario();
      recarregarUltimaPesquisaPontos();
      cardLayout.show(panelCards, CARD_SELECIONAR_PONTO);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível registar família:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private RegistarFamiliaSQL.DadosRegistoFamilia criarDadosRegistoFamilia()
  {
    Double volumeSemanalOpcional = null;
    if(!fieldVolumeSemanalOpcional.getText().trim().isEmpty())
      volumeSemanalOpcional = Double.parseDouble(fieldVolumeSemanalOpcional.getText().trim());

    return new RegistarFamiliaSQL.DadosRegistoFamilia(Integer.parseInt(fieldCodigoPontoSelecionado.getText().trim()),
        fieldNomeResponsavel.getText().trim(), fieldContacto.getText().trim(),
        ((Number) spinnerNumMembros.getValue()).intValue(), comboPerfilSocioeconomico.getSelectedItem().toString(),
        fieldAldeia.getText().trim(), fieldCoordenadasGps.getText().trim(),
        comboAjusteSazonal.getSelectedItem().toString(), comboTransferenciaAutorizada.getSelectedItem().toString(),
        volumeSemanalOpcional);
  }

  private void limparFormulario()
  {
    fieldCodigoPontoSelecionado.setText("");
    fieldAldeia.setText("");
    fieldCoordenadasGps.setText("");
    fieldNomeResponsavel.setText("");
    fieldContacto.setText("");
    fieldVolumeSemanalOpcional.setText("");
    fieldNecessidadesSelecionadas.setText("");
    spinnerNumMembros.setValue(1);
    comboPerfilSocioeconomico.setSelectedIndex(0);
    comboAjusteSazonal.setSelectedIndex(0);
    comboTransferenciaAutorizada.setSelectedIndex(0);
    tabelaPontos.clearSelection();
    tabelaNecessidades.clearSelection();
    buttonProceder.setEnabled(false);
  }

  private void inserirNovaNecessidade()
  {
    String descricao = JOptionPane.showInputDialog(this, "Descrição da nova necessidade:", "Nova Necessidade",
        JOptionPane.PLAIN_MESSAGE);

    if(descricao == null)
      return;

    descricao = descricao.trim();

    if(descricao.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "A descrição da necessidade é obrigatória.");
      return;
    }

    try
    {
      RegistarFamiliaSQL.inserirNecessidade(connection, descricao);
      fieldPesquisaNecessidades.setText(descricao);
      pesquisarNecessidades();
      JOptionPane.showMessageDialog(this, "Necessidade inserida com sucesso.");
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível inserir necessidade:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private class TratarSelecaoTabela implements ListSelectionListener
  {
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
      if(!e.getValueIsAdjusting())
        buttonProceder.setEnabled(tabelaPontos.getSelectedRow() != -1);
    }
  }

  private class TratarPesquisa implements DocumentListener
  {
    @Override
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }
  }

  private class TratarPesquisaNecessidades implements DocumentListener
  {
    @Override
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroNecessidades();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroNecessidades();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroNecessidades();
    }
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      if(e.getSource() == comboCriterioPesquisa)
      {
        aplicarFiltro();
        return;
      }

      if(e.getSource() == comboCriterioPesquisaNecessidades)
      {
        aplicarFiltroNecessidades();
        return;
      }

      if(e.getSource() == buttonActualizarPontos)
      {
        pesquisarPontosDistribuicao();
        return;
      }

      if(e.getSource() == buttonMostrarTodosPontos)
      {
        carregarPontosDistribuicao();
        if(fieldPesquisa != null)
          fieldPesquisa.setText("");
        if(tabelaPontos != null)
          tabelaPontos.clearSelection();
        buttonProceder.setEnabled(false);
        return;
      }

      if(e.getSource() == buttonRecarregarPontos)
      {
        recarregarUltimaPesquisaPontos();
        return;
      }

      if(e.getSource() == buttonProceder)
      {
        String codigoPonto = getCodigoPontoSelecionado();

        if(codigoPonto == null)
        {
          JOptionPane.showMessageDialog(JPanel_RegistarFamilia.this,
              "Seleccione um ponto de distribuição para proceder.");
          return;
        }

        fieldCodigoPontoSelecionado.setText(codigoPonto);
        cardLayout.show(panelCards, CARD_NECESSIDADES);
        return;
      }

      if(e.getSource() == buttonActualizarNecessidades)
      {
        pesquisarNecessidades();
        return;
      }

      if(e.getSource() == buttonMostrarTodasNecessidades)
      {
        carregarNecessidades();
        if(fieldPesquisaNecessidades != null)
          fieldPesquisaNecessidades.setText("");
        if(tabelaNecessidades != null)
          tabelaNecessidades.clearSelection();
        return;
      }

      if(e.getSource() == buttonRecarregarNecessidades)
      {
        recarregarUltimaPesquisaNecessidades();
        return;
      }

      if(e.getSource() == buttonInserirNecessidade)
      {
        inserirNovaNecessidade();
        return;
      }

      if(e.getSource() == buttonVoltarParaPontosNecessidades)
      {
        cardLayout.show(panelCards, CARD_SELECIONAR_PONTO);
        return;
      }

      if(e.getSource() == buttonContinuarParaDadosFamilia)
      {
        actualizarCampoNecessidadesSelecionadas();
        cardLayout.show(panelCards, CARD_DADOS_FAMILIA);
        return;
      }

      if(e.getSource() == buttonVoltar)
      {
        voltarAoDashboard();
        return;
      }

      if(e.getSource() == buttonVoltarParaPontos)
      {
        cardLayout.show(panelCards, CARD_NECESSIDADES);
        return;
      }

      if(e.getSource() == buttonRegistarFamilia)
      {
        if(!validarCamposFormulario())
          return;

        registarFamiliaNoBanco();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }

  // =========================== FIM DA LOGICA DA INTERFACE GRAFICA
  // ===========================
}
