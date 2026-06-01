package View_Interface_DistribuicaoConsumoDB;

import Resources.MensagensInterface;

import Repository_SQL.DistribuicaoConsumoDB.RegistarPontoDistribuicaoSQL;
import Resources.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.regex.Pattern;

public class JPanel_RegistarPontoDistribuicao extends JPanel
{
  private static final String CARD_RECURSO = "recurso";
  private static final String CARD_COMITE = "comite";
  private static final String CARD_EQUIPE = "equipe";
  private static final String CARD_DADOS_PONTO = "dados_ponto";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final TratarButtons tratarButtons = new TratarButtons();

  private CardLayout cardLayout;
  private JPanel panelCards;

  private JTable tabelaRecursos;
  private JTable tabelaComites;
  private JTable tabelaEquipes;
  private DefaultTableModel modeloRecursos;
  private DefaultTableModel modeloComites;
  private DefaultTableModel modeloEquipes;
  private TableRowSorter<DefaultTableModel> sorterRecursos;
  private TableRowSorter<DefaultTableModel> sorterComites;
  private TableRowSorter<DefaultTableModel> sorterEquipes;

  private JTextField fieldPesquisaRecursos;
  private JTextField fieldPesquisaComites;
  private JTextField fieldPesquisaEquipes;
  private JComboBox<String> comboCriterioRecursos;
  private JComboBox<String> comboCriterioComites;
  private JComboBox<String> comboCriterioEquipes;

  private JButton buttonActualizarRecursos;
  private JButton buttonActualizarComites;
  private JButton buttonActualizarEquipes;
  private JButton buttonAvancarComite;
  private JButton buttonAvancarEquipe;
  private JButton buttonAvancarDados;
  private JButton buttonVoltarDashboard;
  private JButton buttonVoltarRecurso;
  private JButton buttonVoltarComite;
  private JButton buttonVoltarEquipe;
  private JButton buttonRegistarPonto;

  private JTextField fieldCodigoRecurso;
  private JTextField fieldCodigoComite;
  private JTextField fieldEquipeId;
  private JTextField fieldLocalizacao;
  private JTextField fieldTipoInfraestrutura;
  private JTextField fieldCapacidade;
  private JTextField fieldVolumeActual;
  private JTextField fieldFonteAbastecimento;
  private JTextField fieldTecnologiaTratamento;
  private JSpinner spinnerDiaInstalacao;
  private JSpinner spinnerMesInstalacao;
  private JSpinner spinnerAnoInstalacao;
  private JComboBox<String> comboEstadoOperacional;

  public JPanel_RegistarPontoDistribuicao(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_RegistarPontoDistribuicao(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_RegistarPontoDistribuicao(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_RegistarPontoDistribuicao(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
    panelCards.add(criarCardRecurso(), CARD_RECURSO);
    panelCards.add(criarCardComite(), CARD_COMITE);
    panelCards.add(criarCardEquipe(), CARD_EQUIPE);
    panelCards.add(criarCardDadosPonto(), CARD_DADOS_PONTO);

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
      carregarRecursos();
      carregarComites();
      carregarEquipes();
    }
  }

  private JPanel criarCardRecurso()
  {
    return criarCardSelecao("Registar Ponto de Distribuição",
        "<html>Primeiro, selecione o recurso hídrico que abastecerá obrigatoriamente o novo ponto.</html>",
        "Recursos Hídricos", criarPainelPesquisaRecursos(), criarScrollRecursos(), criarRodapeRecurso());
  }

  private JPanel criarCardComite()
  {
    return criarCardSelecao("Selecionar Comité Responsável",
        "<html>Escolha o comité que ficará obrigatoriamente responsável pelo novo ponto.</html>", "Comités Locais",
        criarPainelPesquisaComites(), criarScrollComites(), criarRodapeComite());
  }

  private JPanel criarCardEquipe()
  {
    return criarCardSelecao("Selecionar Técnico de Manutenção",
        "<html>Escolha o técnico de manutenção que gerirá obrigatoriamente o novo ponto de distribuição.</html>",
        "Técnicos de Manutenção", criarPainelPesquisaEquipes(), criarScrollEquipes(), criarRodapeEquipe());
  }

  private JPanel criarCardSelecao(String titulo, String subtitulo, String tituloTabela, JPanel pesquisa,
      JScrollPane tabela, JPanel rodape)
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    JPanel centro = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    JLabel labelTabela = InterfaceGraficaUtils.criarLabel(tituloTabela, InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_AZUL);
    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(labelTabela, BorderLayout.NORTH);
    topoTabela.add(pesquisa, BorderLayout.SOUTH);
    centro.add(topoTabela, BorderLayout.NORTH);
    centro.add(tabela, BorderLayout.CENTER);

    card.add(InterfaceGraficaUtils.criarTopo(titulo, subtitulo, InterfaceGraficaUtils.FONT_TITULO,
        InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO, InterfaceGraficaUtils.COR_SUBTEXTO),
        BorderLayout.NORTH);
    card.add(centro, BorderLayout.CENTER);
    card.add(rodape, BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCardDadosPonto()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1180, 720));

    card.add(InterfaceGraficaUtils.criarTopo("Dados do Ponto de Distribuição",
        "<html>Preencha os dados próprios do novo ponto e confirme o registo.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.NORTH);
    card.add(criarCentroDadosPonto(), BorderLayout.CENTER);
    card.add(criarRodapeDadosPonto(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarPainelPesquisaRecursos()
  {
    fieldPesquisaRecursos = criarCampoPesquisa();
    comboCriterioRecursos = criarComboPesquisa(new String[]
      {
          "Código", "Tipo", "Localização", "Sazonalidade"
      });
    buttonActualizarRecursos = criarBotaoPequeno("Mostrar Todos");
    buttonActualizarRecursos.addActionListener(tratarButtons);
    fieldPesquisaRecursos.getDocument().addDocumentListener(new TratarPesquisa(OrigemFiltro.RECURSOS));
    comboCriterioRecursos.addActionListener(tratarButtons);
    return criarLinhaPesquisa(fieldPesquisaRecursos, comboCriterioRecursos, buttonActualizarRecursos);
  }

  private JPanel criarPainelPesquisaComites()
  {
    fieldPesquisaComites = criarCampoPesquisa();
    comboCriterioComites = criarComboPesquisa(new String[]
      {
          "Código", "Nome"
      });
    buttonActualizarComites = criarBotaoPequeno("Mostrar Todos");
    buttonActualizarComites.addActionListener(tratarButtons);
    fieldPesquisaComites.getDocument().addDocumentListener(new TratarPesquisa(OrigemFiltro.COMITES));
    comboCriterioComites.addActionListener(tratarButtons);
    return criarLinhaPesquisa(fieldPesquisaComites, comboCriterioComites, buttonActualizarComites);
  }

  private JPanel criarPainelPesquisaEquipes()
  {
    fieldPesquisaEquipes = criarCampoPesquisa();
    comboCriterioEquipes = criarComboPesquisa(new String[]
      {
          "Código", "Nome", "Tipo", "Área", "Supervisor"
      });
    buttonActualizarEquipes = criarBotaoPequeno("Mostrar Todos");
    buttonActualizarEquipes.addActionListener(tratarButtons);
    fieldPesquisaEquipes.getDocument().addDocumentListener(new TratarPesquisa(OrigemFiltro.EQUIPES));
    comboCriterioEquipes.addActionListener(tratarButtons);
    return criarLinhaPesquisa(fieldPesquisaEquipes, comboCriterioEquipes, buttonActualizarEquipes);
  }

  private JPanel criarLinhaPesquisa(JTextField campo, JComboBox<String> combo, JButton botao)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(campo);
    painel.add(combo);
    painel.add(botao);
    return painel;
  }

  private JScrollPane criarScrollRecursos()
  {
    modeloRecursos = criarModelo(new String[]
      {
          "Código", "Tipo", "Localização", "Volume (L)", "Sazonalidade", "Nível Exploração"
      });
    tabelaRecursos = criarTabela(modeloRecursos);
    tabelaRecursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterRecursos = new TableRowSorter<DefaultTableModel>(modeloRecursos);
    tabelaRecursos.setRowSorter(sorterRecursos);
    return criarScrollTabela(tabelaRecursos);
  }

  private JScrollPane criarScrollComites()
  {
    modeloComites = criarModelo(new String[]
      {
          "Código", "Nome", "Data Criação"
      });
    tabelaComites = criarTabela(modeloComites);
    tabelaComites.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterComites = new TableRowSorter<DefaultTableModel>(modeloComites);
    tabelaComites.setRowSorter(sorterComites);
    return criarScrollTabela(tabelaComites);
  }

  private JScrollPane criarScrollEquipes()
  {
    modeloEquipes = criarModelo(new String[]
      {
          "Código", "Nome", "Tipo", "Área", "Formação", "Contacto", "Supervisor"
      });
    tabelaEquipes = criarTabela(modeloEquipes);
    tabelaEquipes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterEquipes = new TableRowSorter<DefaultTableModel>(modeloEquipes);
    tabelaEquipes.setRowSorter(sorterEquipes);
    return criarScrollTabela(tabelaEquipes);
  }

  private JPanel criarRodapeRecurso()
  {
    buttonVoltarDashboard = criarBotao("Voltar");
    buttonAvancarComite = criarBotao("Proceder");
    buttonVoltarDashboard.addActionListener(tratarButtons);
    buttonAvancarComite.addActionListener(tratarButtons);
    return criarRodape(buttonVoltarDashboard, buttonAvancarComite, "Seleccione um recurso hídrico para continuar.");
  }

  private JPanel criarRodapeComite()
  {
    buttonVoltarRecurso = criarBotao("Voltar");
    buttonAvancarEquipe = criarBotao("Proceder");
    buttonVoltarRecurso.addActionListener(tratarButtons);
    buttonAvancarEquipe.addActionListener(tratarButtons);
    return criarRodape(buttonVoltarRecurso, buttonAvancarEquipe, "Seleccione um comité responsável.");
  }

  private JPanel criarRodapeEquipe()
  {
    buttonVoltarComite = criarBotao("Voltar");
    buttonAvancarDados = criarBotao("Proceder");
    buttonVoltarComite.addActionListener(tratarButtons);
    buttonAvancarDados.addActionListener(tratarButtons);
    return criarRodape(buttonVoltarComite, buttonAvancarDados, "Seleccione um técnico de manutenção.");
  }

  private JPanel criarRodapeDadosPonto()
  {
    buttonVoltarEquipe = criarBotao("Voltar");
    buttonRegistarPonto = criarBotao("Registar");
    buttonVoltarEquipe.addActionListener(tratarButtons);
    buttonRegistarPonto.addActionListener(tratarButtons);
    return criarRodape(buttonVoltarEquipe, buttonRegistarPonto, "Confira os dados antes de registar.");
  }

  private JPanel criarRodape(JButton voltar, JButton avancar, String ajuda)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel(ajuda, InterfaceGraficaUtils.FONT_AJUDA,
        InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    if(voltar != null)
      botoes.add(voltar);
    botoes.add(avancar);
    painel.add(labelAjuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarCentroDadosPonto()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldCodigoRecurso = criarCampoReadonly();
    fieldCodigoComite = criarCampoReadonly();
    fieldEquipeId = criarCampoReadonly();
    fieldLocalizacao = criarCampoFormulario();
    fieldTipoInfraestrutura = criarCampoFormulario();
    fieldCapacidade = criarCampoFormulario();
    fieldVolumeActual = criarCampoFormulario();
    fieldFonteAbastecimento = criarCampoFormulario();
    fieldTecnologiaTratamento = criarCampoFormulario();
    LocalDate hoje = LocalDate.now();
    spinnerDiaInstalacao = criarSpinnerData(hoje.getDayOfMonth(), 1, hoje.lengthOfMonth());
    spinnerMesInstalacao = criarSpinnerData(hoje.getMonthValue(), 1, 12);
    spinnerAnoInstalacao = criarSpinnerData(hoje.getYear(), 1900, 2100);
    InterfaceGraficaUtils.removerSeparadorMilhares(spinnerAnoInstalacao);
    spinnerMesInstalacao.addChangeListener(new TratarAlteracaoData());
    spinnerAnoInstalacao.addChangeListener(new TratarAlteracaoData());
    comboEstadoOperacional = new JComboBox<String>(new String[]
      {
          "Activo", "Inactivo", "Em Manutencao"
      });
    estilizarCombo(comboEstadoOperacional);

    int linha = 0;
    adicionarLinhaFormulario(painel, linha++, "Código do recurso hídrico:", fieldCodigoRecurso);
    adicionarLinhaFormulario(painel, linha++, "Código do comité:", fieldCodigoComite);
    adicionarLinhaFormulario(painel, linha++, "Código da equipe técnica:", fieldEquipeId);
    adicionarLinhaFormulario(painel, linha++, "Localização:", fieldLocalizacao);
    adicionarLinhaFormulario(painel, linha++, "Tipo de infraestrutura:", fieldTipoInfraestrutura);
    adicionarLinhaFormulario(painel, linha++, "Capacidade de armazenamento (L):", fieldCapacidade);
    adicionarLinhaFormulario(painel, linha++, "Volume actual (L):", fieldVolumeActual);
    adicionarLinhaFormulario(painel, linha++, "Fonte de abastecimento:", fieldFonteAbastecimento);
    adicionarLinhaFormulario(painel, linha++, "Tecnologia de tratamento:", fieldTecnologiaTratamento);
    adicionarLinhaFormulario(painel, linha++, "Data de instalação:", criarPainelDataInstalacao());
    adicionarLinhaFormulario(painel, linha++, "Estado operacional:", comboEstadoOperacional);

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = linha;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);

    return painel;
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
    gbc.insets = new Insets(7, 0, 7, 16);
    painel.add(label, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(7, 0, 7, 0);
    painel.add(campo, gbc);
  }

  private DefaultTableModel criarModelo(String[] colunas)
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

  private JScrollPane criarScrollTabela(JTable tabela)
  {
    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabela, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1180, 520));
    return scroll;
  }

  private JTextField criarCampoPesquisa()
  {
    return InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(250, 36));
  }

  private JComboBox<String> criarComboPesquisa(String[] opcoes)
  {
    return InterfaceGraficaUtils.criarCombo(opcoes, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(190, 36));
  }

  private JTextField criarCampoFormulario()
  {
    JTextField campo = new JTextField(20);
    estilizarCampo(campo);
    return campo;
  }

  private JTextField criarCampoReadonly()
  {
    JTextField campo = criarCampoFormulario();
    campo.setEditable(false);
    return campo;
  }

  private JSpinner criarSpinnerData(int valorInicial, int minimo, int maximo)
  {
    JSpinner spinner = InterfaceGraficaUtils.criarSpinnerNumero(valorInicial, minimo, maximo, 1,
        InterfaceGraficaUtils.FONT_CAMPO, new Dimension(92, 36));
    spinner.setMinimumSize(new Dimension(92, 36));
    return spinner;
  }

  private JPanel criarPainelDataInstalacao()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    painel.add(spinnerDiaInstalacao);
    painel.add(spinnerMesInstalacao);
    painel.add(spinnerAnoInstalacao);
    return painel;
  }

  private void estilizarCampo(JTextField campo)
  {
    Dimension tamanho = new Dimension(340, 36);
    campo.setPreferredSize(tamanho);
    campo.setMinimumSize(tamanho);
    campo.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    InterfaceGraficaUtils.aplicarCantosArredondados(campo);
  }

  private void estilizarCombo(JComboBox<String> combo)
  {
    Dimension tamanho = new Dimension(340, 36);
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
    return botao;
  }

  private JButton criarBotaoPequeno(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(145, 36));
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  private void carregarRecursos()
  {
    try
    {
      RegistarPontoDistribuicaoSQL.carregarRecursos(connection, modeloRecursos);
      aplicarFiltroRecursos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os recursos hídricos.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarComites()
  {
    try
    {
      RegistarPontoDistribuicaoSQL.carregarComites(connection, modeloComites);
      aplicarFiltroComites();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os comités.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarEquipes()
  {
    try
    {
      RegistarPontoDistribuicaoSQL.carregarEquipes(connection, modeloEquipes);
      aplicarFiltroEquipes();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os técnicos de manutenção.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltroRecursos()
  {
    aplicarFiltro(sorterRecursos, fieldPesquisaRecursos, getIndiceRecurso());
  }

  private void aplicarFiltroComites()
  {
    aplicarFiltro(sorterComites, fieldPesquisaComites, getIndiceComite());
  }

  private void aplicarFiltroEquipes()
  {
    aplicarFiltro(sorterEquipes, fieldPesquisaEquipes, getIndiceEquipe());
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

  private int getIndiceRecurso()
  {
    String criterio = comboCriterioRecursos.getSelectedItem().toString();
    if("Tipo".equals(criterio))
      return 1;
    if("Localização".equals(criterio))
      return 2;
    if("Sazonalidade".equals(criterio))
      return 4;
    return 0;
  }

  private int getIndiceComite()
  {
    return "Nome".equals(comboCriterioComites.getSelectedItem().toString()) ? 1 : 0;
  }

  private int getIndiceEquipe()
  {
    String criterio = comboCriterioEquipes.getSelectedItem().toString();
    if("Nome".equals(criterio))
      return 1;
    if("Tipo".equals(criterio))
      return 2;
    if("Área".equals(criterio))
      return 3;
    if("Supervisor".equals(criterio))
      return 6;
    return 0;
  }

  private Integer getCodigoSelecionado(JTable tabela, DefaultTableModel modelo)
  {
    int linhaView = tabela.getSelectedRow();
    if(linhaView == -1)
      return null;
    int linhaModel = tabela.convertRowIndexToModel(linhaView);
    Object valor = modelo.getValueAt(linhaModel, 0);
    return valor == null ? null : Integer.valueOf(valor.toString());
  }

  private boolean validarSelecao(JTable tabela, String mensagem)
  {
    if(tabela.getSelectedRow() == -1)
    {
      JOptionPane.showMessageDialog(this, mensagem);
      return false;
    }
    return true;
  }

  private void preencherCodigosSelecionados()
  {
    fieldCodigoRecurso.setText(getCodigoSelecionado(tabelaRecursos, modeloRecursos).toString());
    fieldCodigoComite.setText(getCodigoSelecionado(tabelaComites, modeloComites).toString());
    fieldEquipeId.setText(getCodigoSelecionado(tabelaEquipes, modeloEquipes).toString());
  }

  private boolean validarCamposFormulario()
  {
    if(fieldLocalizacao.getText().trim().isEmpty() || fieldTipoInfraestrutura.getText().trim().isEmpty()
        || fieldCapacidade.getText().trim().isEmpty() || fieldVolumeActual.getText().trim().isEmpty()
        || fieldFonteAbastecimento.getText().trim().isEmpty() || fieldTecnologiaTratamento.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios antes de continuar.");
      return false;
    }

    try
    {
      double capacidade = Double.parseDouble(fieldCapacidade.getText().trim());
      double volumeActual = Double.parseDouble(fieldVolumeActual.getText().trim());
      if(capacidade <= 0 || volumeActual < 0)
      {
        JOptionPane.showMessageDialog(this, "Capacidade deve ser maior que zero e volume actual não pode ser negativo.");
        return false;
      }
      if(volumeActual > capacidade)
      {
        JOptionPane.showMessageDialog(this, "Volume actual não pode ser maior que a capacidade.");
        return false;
      }
    } catch(NumberFormatException ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return false;

      JOptionPane.showMessageDialog(this, "Capacidade e volume actual devem ser números válidos.");
      return false;
    }

    ajustarDiaMaximoInstalacao();

    return true;
  }

  private RegistarPontoDistribuicaoSQL.DadosPonto criarDadosPonto()
  {
    return new RegistarPontoDistribuicaoSQL.DadosPonto(Integer.parseInt(fieldCodigoComite.getText().trim()),
        Integer.parseInt(fieldEquipeId.getText().trim()), Integer.parseInt(fieldCodigoRecurso.getText().trim()),
        Double.parseDouble(fieldVolumeActual.getText().trim()), fieldLocalizacao.getText().trim(),
        fieldTipoInfraestrutura.getText().trim(), Double.parseDouble(fieldCapacidade.getText().trim()),
        fieldFonteAbastecimento.getText().trim(), fieldTecnologiaTratamento.getText().trim(),
        getDataInstalacaoFormatada(), comboEstadoOperacional.getSelectedItem().toString());
  }

  private String getDataInstalacaoFormatada()
  {
    int dia = ((Number) spinnerDiaInstalacao.getValue()).intValue();
    int mes = ((Number) spinnerMesInstalacao.getValue()).intValue();
    int ano = ((Number) spinnerAnoInstalacao.getValue()).intValue();
    return String.format("%04d-%02d-%02d", ano, mes, dia);
  }

  private void ajustarDiaMaximoInstalacao()
  {
    int dia = ((Number) spinnerDiaInstalacao.getValue()).intValue();
    int mes = ((Number) spinnerMesInstalacao.getValue()).intValue();
    int ano = ((Number) spinnerAnoInstalacao.getValue()).intValue();
    int maximoDia = YearMonth.of(ano, mes).lengthOfMonth();

    SpinnerNumberModel modeloDia = (SpinnerNumberModel) spinnerDiaInstalacao.getModel();
    modeloDia.setMaximum(Integer.valueOf(maximoDia));

    if(dia > maximoDia)
      spinnerDiaInstalacao.setValue(Integer.valueOf(maximoDia));
  }

  private void registarPonto()
  {
    if(!validarCamposFormulario())
      return;

    try
    {
      RegistarPontoDistribuicaoSQL.DadosPonto dados = criarDadosPonto();
      RegistarPontoDistribuicaoSQL.ResultadoPreValidacao preValidacao =
          RegistarPontoDistribuicaoSQL.preValidarRegisto(connection, dados);

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Ponto não pode ser registado:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Recurso hídrico: " + dados.codigoRecursoHidrico + "\nComité: " + dados.codigoComite
              + "\nTécnico de manutenção: " + dados.equipeId + "\nLocalização: " + dados.localizacao + "\nCapacidade (L): " + dados.capacidade
              + "\nVolume actual (L): " + dados.volumeActual + "\n\nDeseja registar o ponto de distribuição?",
          "Confirmar Registo de Ponto", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      RegistarPontoDistribuicaoSQL.ResultadoRegisto resultado =
          RegistarPontoDistribuicaoSQL.registarPonto(connection, dados);

      if(!resultado.registado)
      {
        JOptionPane.showMessageDialog(this, "Ponto não registado:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          "Ponto de distribuição registado com sucesso.\nCódigo do ponto: " + resultado.codigoPonto);
      limparFormulario();
      cardLayout.show(panelCards, CARD_RECURSO);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível registar ponto de distribuição:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void limparFormulario()
  {
    fieldCodigoRecurso.setText("");
    fieldCodigoComite.setText("");
    fieldEquipeId.setText("");
    fieldLocalizacao.setText("");
    fieldTipoInfraestrutura.setText("");
    fieldCapacidade.setText("");
    fieldVolumeActual.setText("");
    fieldFonteAbastecimento.setText("");
    fieldTecnologiaTratamento.setText("");
    LocalDate hoje = LocalDate.now();
    spinnerAnoInstalacao.setValue(Integer.valueOf(hoje.getYear()));
    spinnerMesInstalacao.setValue(Integer.valueOf(hoje.getMonthValue()));
    spinnerDiaInstalacao.setValue(Integer.valueOf(hoje.getDayOfMonth()));
    ajustarDiaMaximoInstalacao();
    comboEstadoOperacional.setSelectedIndex(0);
    tabelaRecursos.clearSelection();
    tabelaComites.clearSelection();
    tabelaEquipes.clearSelection();
  }

  private enum OrigemFiltro
  {
    RECURSOS, COMITES, EQUIPES
  }

  private class TratarPesquisa implements DocumentListener
  {
    private final OrigemFiltro origem;

    private TratarPesquisa(OrigemFiltro origem)
    {
      this.origem = origem;
    }

    public void insertUpdate(DocumentEvent e)
    {
      filtrar();
    }

    public void removeUpdate(DocumentEvent e)
    {
      filtrar();
    }

    public void changedUpdate(DocumentEvent e)
    {
      filtrar();
    }

    private void filtrar()
    {
      if(origem == OrigemFiltro.RECURSOS)
        aplicarFiltroRecursos();
      else if(origem == OrigemFiltro.COMITES)
        aplicarFiltroComites();
      else
        aplicarFiltroEquipes();
    }
  }

  private class TratarAlteracaoData implements ChangeListener
  {
    public void stateChanged(ChangeEvent e)
    {
      ajustarDiaMaximoInstalacao();
    }
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      if(e.getSource() == comboCriterioRecursos)
      {
        aplicarFiltroRecursos();
        return;
      }
      if(e.getSource() == comboCriterioComites)
      {
        aplicarFiltroComites();
        return;
      }
      if(e.getSource() == comboCriterioEquipes)
      {
        aplicarFiltroEquipes();
        return;
      }
      if(e.getSource() == buttonActualizarRecursos)
      {
        carregarRecursos();
        return;
      }
      if(e.getSource() == buttonActualizarComites)
      {
        carregarComites();
        return;
      }
      if(e.getSource() == buttonActualizarEquipes)
      {
        carregarEquipes();
        return;
      }
      if(e.getSource() == buttonAvancarComite)
      {
        if(validarSelecao(tabelaRecursos, "Seleccione um recurso hídrico."))
          cardLayout.show(panelCards, CARD_COMITE);
        return;
      }
      if(e.getSource() == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }
      if(e.getSource() == buttonAvancarEquipe)
      {
        if(validarSelecao(tabelaComites, "Seleccione um comité."))
          cardLayout.show(panelCards, CARD_EQUIPE);
        return;
      }
      if(e.getSource() == buttonAvancarDados)
      {
        if(validarSelecao(tabelaEquipes, "Seleccione um técnico de manutenção."))
        {
          preencherCodigosSelecionados();
          cardLayout.show(panelCards, CARD_DADOS_PONTO);
        }
        return;
      }
      if(e.getSource() == buttonVoltarRecurso)
      {
        cardLayout.show(panelCards, CARD_RECURSO);
        return;
      }
      if(e.getSource() == buttonVoltarComite)
      {
        cardLayout.show(panelCards, CARD_COMITE);
        return;
      }
      if(e.getSource() == buttonVoltarEquipe)
      {
        cardLayout.show(panelCards, CARD_EQUIPE);
        return;
      }
      if(e.getSource() == buttonRegistarPonto)
      {
        registarPonto();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }
}
