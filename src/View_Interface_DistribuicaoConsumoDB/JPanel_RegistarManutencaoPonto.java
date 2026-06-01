package View_Interface_DistribuicaoConsumoDB;

import Resources.MensagensInterface;

import Repository_SQL.DistribuicaoConsumoDB.RegistarManutencaoPontoSQL;
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

public class JPanel_RegistarManutencaoPonto extends JPanel
{
  private static final String CARD_SELECIONAR_PONTO = "selecionar_ponto";
  private static final String CARD_DADOS_MANUTENCAO = "dados_manutencao";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final TratarButtons tratarButtons = new TratarButtons();

  private CardLayout cardLayout;
  private JPanel panelCards;

  private JTable tabelaPontos;
  private DefaultTableModel modeloPontos;
  private TableRowSorter<DefaultTableModel> sorterPontos;

  private JTextField fieldPesquisa;
  private JComboBox<String> comboCriterioPesquisa;
  private JButton buttonActualizarPontos;
  private JButton buttonAvancar;
  private JButton buttonVoltarDashboard;

  private JTextField fieldCodigoPonto;
  private JTextField fieldEquipeId;
  private JTextField fieldNomeEquipe;
  private JTextField fieldAreaEquipe;
  private JTextField fieldFormacaoEquipe;
  private JTextField fieldContactoEquipe;
  private JTextField fieldSupervisorEquipe;
  private JTextArea areaTipoManutencao;
  private JSpinner spinnerDiaManutencao;
  private JSpinner spinnerMesManutencao;
  private JSpinner spinnerAnoManutencao;
  private JRadioButton radioAlterarEstadoSim;
  private JRadioButton radioAlterarEstadoNao;
  private JComboBox<String> comboNovoEstadoPonto;
  private JButton buttonVoltarParaPontos;
  private JButton buttonRegistarManutencao;

  public JPanel_RegistarManutencaoPonto(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_RegistarManutencaoPonto(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_RegistarManutencaoPonto(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_RegistarManutencaoPonto(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
    panelCards.add(criarCardSelecionarPonto(), CARD_SELECIONAR_PONTO);
    panelCards.add(criarCardDadosManutencao(), CARD_DADOS_MANUTENCAO);

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
      carregarPontos();
    }
  }

  private JPanel criarCardSelecionarPonto()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(InterfaceGraficaUtils.criarTopo("Registar Manutenção",
        "<html>Seleccione o ponto de distribuição onde a manutenção foi realizada. A equipe técnica será obtida automaticamente.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.NORTH);
    card.add(criarCentroSelecao(), BorderLayout.CENTER);
    card.add(criarRodapeSelecao(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCardDadosManutencao()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1180, 720));

    card.add(InterfaceGraficaUtils.criarTopo("Dados da Manutenção",
        "<html>Informe a data e a descrição da manutenção realizada no ponto seleccionado.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.NORTH);
    card.add(criarCentroFormulario(), BorderLayout.CENTER);
    card.add(criarRodapeFormulario(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCentroSelecao()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Pontos de Distribuição",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(label, BorderLayout.NORTH);
    topoTabela.add(criarPainelPesquisa(), BorderLayout.SOUTH);

    painel.add(topoTabela, BorderLayout.NORTH);
    painel.add(criarScrollPontos(), BorderLayout.CENTER);

    return painel;
  }

  private JPanel criarRodapeSelecao()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Seleccione um ponto para continuar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar");
    buttonAvancar = criarBotao("Proceder");
    buttonVoltarDashboard.addActionListener(tratarButtons);
    buttonAvancar.addActionListener(tratarButtons);
    botoes.add(buttonVoltarDashboard);
    botoes.add(buttonAvancar);

    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarCentroFormulario()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldCodigoPonto = criarCampoReadonly();
    fieldEquipeId = criarCampoReadonly();
    fieldNomeEquipe = criarCampoReadonly();
    fieldAreaEquipe = criarCampoReadonly();
    fieldFormacaoEquipe = criarCampoReadonly();
    fieldContactoEquipe = criarCampoReadonly();
    fieldSupervisorEquipe = criarCampoReadonly();

    LocalDate hoje = LocalDate.now();
    spinnerDiaManutencao = criarSpinnerData(hoje.getDayOfMonth(), 1, hoje.lengthOfMonth());
    spinnerMesManutencao = criarSpinnerData(hoje.getMonthValue(), 1, 12);
    spinnerAnoManutencao = criarSpinnerData(hoje.getYear(), 1900, hoje.getYear());
    InterfaceGraficaUtils.removerSeparadorMilhares(spinnerAnoManutencao);
    spinnerDiaManutencao.addChangeListener(new TratarAlteracaoData());
    spinnerMesManutencao.addChangeListener(new TratarAlteracaoData());
    spinnerAnoManutencao.addChangeListener(new TratarAlteracaoData());
    criarControlosAlteracaoEstado();

    areaTipoManutencao = new JTextArea(5, 24);
    areaTipoManutencao.setLineWrap(true);
    areaTipoManutencao.setWrapStyleWord(true);
    areaTipoManutencao.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    JScrollPane scrollDescricao = new JScrollPane(areaTipoManutencao);
    scrollDescricao.setPreferredSize(new Dimension(420, 120));
    scrollDescricao.setMinimumSize(new Dimension(420, 120));

    int linha = 0;
    adicionarLinhaFormulario(painel, linha++, "Código do ponto:", fieldCodigoPonto);
    adicionarLinhaFormulario(painel, linha++, "Código da equipe:", fieldEquipeId);
    adicionarLinhaFormulario(painel, linha++, "Nome da equipe:", fieldNomeEquipe);
    adicionarLinhaFormulario(painel, linha++, "Área de actuação:", fieldAreaEquipe);
    adicionarLinhaFormulario(painel, linha++, "Nível de formação:", fieldFormacaoEquipe);
    adicionarLinhaFormulario(painel, linha++, "Contacto:", fieldContactoEquipe);
    adicionarLinhaFormulario(painel, linha++, "Supervisor:", fieldSupervisorEquipe);
    adicionarLinhaFormulario(painel, linha++, "Data da manutenção:", criarPainelDataManutencao());
    adicionarLinhaFormulario(painel, linha++, "Alterar estado do ponto?", criarPainelAlteracaoEstado());
    adicionarLinhaFormulario(painel, linha++, "Tipo/descrição da manutenção:", scrollDescricao);

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = linha;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);

    return painel;
  }

  private JPanel criarRodapeFormulario()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Confira os dados antes de registar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarParaPontos = criarBotao("Voltar");
    buttonRegistarManutencao = criarBotao("Registar");
    buttonVoltarParaPontos.addActionListener(tratarButtons);
    buttonRegistarManutencao.addActionListener(tratarButtons);
    botoes.add(buttonVoltarParaPontos);
    botoes.add(buttonRegistarManutencao);

    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarPainelPesquisa()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(250, 36));
    comboCriterioPesquisa = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Ponto", "Equipe Técnica", "Localização", "Infraestrutura", "Estado"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(190, 36));
    buttonActualizarPontos = criarBotaoPequeno("Mostrar Todos");

    fieldPesquisa.getDocument().addDocumentListener(new TratarPesquisa());
    comboCriterioPesquisa.addActionListener(tratarButtons);
    buttonActualizarPontos.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisa);
    painel.add(comboCriterioPesquisa);
    painel.add(buttonActualizarPontos);
    return painel;
  }

  private JScrollPane criarScrollPontos()
  {
    modeloPontos = new DefaultTableModel(new String[]
      {
          "Código Ponto", "Equipe Técnica", "Localização", "Infraestrutura", "Capacidade", "Volume Actual", "Estado",
          "Nome Equipe", "Área", "Formação", "Contacto", "Supervisor"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };

    tabelaPontos = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaPontos.setModel(modeloPontos);
    tabelaPontos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterPontos = new TableRowSorter<DefaultTableModel>(modeloPontos);
    tabelaPontos.setRowSorter(sorterPontos);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaPontos, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1180, 520));
    return scroll;
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

  private JTextField criarCampoReadonly()
  {
    JTextField campo = new JTextField(20);
    campo.setEditable(false);
    campo.setPreferredSize(new Dimension(420, 36));
    campo.setMinimumSize(new Dimension(420, 36));
    campo.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    InterfaceGraficaUtils.aplicarCantosArredondados(campo);
    return campo;
  }

  private JSpinner criarSpinnerData(int valorInicial, int minimo, int maximo)
  {
    JSpinner spinner = InterfaceGraficaUtils.criarSpinnerNumero(valorInicial, minimo, maximo, 1,
        InterfaceGraficaUtils.FONT_CAMPO, new Dimension(92, 36));
    spinner.setMinimumSize(new Dimension(92, 36));
    return spinner;
  }

  private JPanel criarPainelDataManutencao()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    painel.add(spinnerDiaManutencao);
    painel.add(spinnerMesManutencao);
    painel.add(spinnerAnoManutencao);
    return painel;
  }

  private void criarControlosAlteracaoEstado()
  {
    radioAlterarEstadoSim = new JRadioButton("SIM");
    radioAlterarEstadoNao = new JRadioButton("NAO", true);
    radioAlterarEstadoSim.setOpaque(false);
    radioAlterarEstadoNao.setOpaque(false);
    radioAlterarEstadoSim.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    radioAlterarEstadoNao.setFont(InterfaceGraficaUtils.FONT_CAMPO);

    ButtonGroup grupo = new ButtonGroup();
    grupo.add(radioAlterarEstadoSim);
    grupo.add(radioAlterarEstadoNao);

    comboNovoEstadoPonto = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Inactivo", "Em Manutencao"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(190, 36));
    comboNovoEstadoPonto.setEnabled(false);

    radioAlterarEstadoSim.addActionListener(tratarButtons);
    radioAlterarEstadoNao.addActionListener(tratarButtons);
    actualizarControlosAlteracaoEstado();
  }

  private JPanel criarPainelAlteracaoEstado()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    painel.add(radioAlterarEstadoNao);
    painel.add(radioAlterarEstadoSim);
    painel.add(comboNovoEstadoPonto);
    return painel;
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

  private void carregarPontos()
  {
    try
    {
      RegistarManutencaoPontoSQL.carregarPontos(connection, modeloPontos);
      aplicarFiltro();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os pontos de distribuição.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltro()
  {
    if(sorterPontos == null || fieldPesquisa == null || comboCriterioPesquisa == null)
      return;

    String texto = fieldPesquisa.getText().trim();
    if(texto.isEmpty())
    {
      sorterPontos.setRowFilter(null);
      return;
    }

    sorterPontos.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), getIndiceFiltro()));
  }

  private int getIndiceFiltro()
  {
    String criterio = comboCriterioPesquisa.getSelectedItem().toString();
    if("Equipe Técnica".equals(criterio))
      return 1;
    if("Localização".equals(criterio))
      return 2;
    if("Infraestrutura".equals(criterio))
      return 3;
    if("Estado".equals(criterio))
      return 6;
    return 0;
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

  private void prepararFormulario()
  {
    int linhaView = tabelaPontos.getSelectedRow();
    int linhaModel = tabelaPontos.convertRowIndexToModel(linhaView);
    fieldCodigoPonto.setText(modeloPontos.getValueAt(linhaModel, 0).toString());
    fieldEquipeId.setText(modeloPontos.getValueAt(linhaModel, 1).toString());
    fieldNomeEquipe.setText(valorTabela(linhaModel, 7));
    fieldAreaEquipe.setText(valorTabela(linhaModel, 8));
    fieldFormacaoEquipe.setText(valorTabela(linhaModel, 9));
    fieldContactoEquipe.setText(valorTabela(linhaModel, 10));
    fieldSupervisorEquipe.setText(valorTabela(linhaModel, 11));
  }

  private String valorTabela(int linhaModel, int coluna)
  {
    Object valor = modeloPontos.getValueAt(linhaModel, coluna);
    return valor == null ? "" : valor.toString();
  }

  private boolean validarFormulario()
  {
    ajustarDataManutencao();

    if(getDataManutencao().isAfter(LocalDate.now()))
    {
      JOptionPane.showMessageDialog(this, "A data da manutenção não pode ser futura.");
      return false;
    }

    if(areaTipoManutencao.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Informe o tipo ou descrição da manutenção.");
      return false;
    }

    return true;
  }

  private String getDataManutencaoFormatada()
  {
    LocalDate data = getDataManutencao();
    return String.format("%04d-%02d-%02d", data.getYear(), data.getMonthValue(), data.getDayOfMonth());
  }

  private LocalDate getDataManutencao()
  {
    int dia = ((Number) spinnerDiaManutencao.getValue()).intValue();
    int mes = ((Number) spinnerMesManutencao.getValue()).intValue();
    int ano = ((Number) spinnerAnoManutencao.getValue()).intValue();
    return LocalDate.of(ano, mes, dia);
  }

  private void ajustarDataManutencao()
  {
    int dia = ((Number) spinnerDiaManutencao.getValue()).intValue();
    int mes = ((Number) spinnerMesManutencao.getValue()).intValue();
    int ano = ((Number) spinnerAnoManutencao.getValue()).intValue();
    LocalDate hoje = LocalDate.now();
    int maximoDia = YearMonth.of(ano, mes).lengthOfMonth();

    if(ano == hoje.getYear())
    {
      SpinnerNumberModel modeloMes = (SpinnerNumberModel) spinnerMesManutencao.getModel();
      modeloMes.setMaximum(Integer.valueOf(hoje.getMonthValue()));
      if(mes > hoje.getMonthValue())
      {
        mes = hoje.getMonthValue();
        spinnerMesManutencao.setValue(Integer.valueOf(mes));
      }
    }
    else
    {
      ((SpinnerNumberModel) spinnerMesManutencao.getModel()).setMaximum(Integer.valueOf(12));
    }

    if(ano == hoje.getYear() && mes == hoje.getMonthValue())
      maximoDia = Math.min(maximoDia, hoje.getDayOfMonth());

    SpinnerNumberModel modeloDia = (SpinnerNumberModel) spinnerDiaManutencao.getModel();
    modeloDia.setMaximum(Integer.valueOf(maximoDia));

    if(dia > maximoDia)
      spinnerDiaManutencao.setValue(Integer.valueOf(maximoDia));

    actualizarControlosAlteracaoEstado();
  }

  private void actualizarControlosAlteracaoEstado()
  {
    if(radioAlterarEstadoSim == null || radioAlterarEstadoNao == null || comboNovoEstadoPonto == null)
      return;

    boolean dataHoje = getDataManutencao().equals(LocalDate.now());
    radioAlterarEstadoSim.setEnabled(dataHoje);
    radioAlterarEstadoNao.setEnabled(dataHoje);

    if(!dataHoje)
      radioAlterarEstadoNao.setSelected(true);

    comboNovoEstadoPonto.setEnabled(dataHoje && radioAlterarEstadoSim.isSelected());
  }

  private void registarManutencao()
  {
    if(!validarFormulario())
      return;

    RegistarManutencaoPontoSQL.DadosManutencao dados = new RegistarManutencaoPontoSQL.DadosManutencao(
        Integer.parseInt(fieldCodigoPonto.getText().trim()), Integer.parseInt(fieldEquipeId.getText().trim()),
        getDataManutencaoFormatada(), areaTipoManutencao.getText().trim(), radioAlterarEstadoSim.isSelected(),
        radioAlterarEstadoSim.isSelected() ? comboNovoEstadoPonto.getSelectedItem().toString() : null);

    try
    {
      RegistarManutencaoPontoSQL.ResultadoPreValidacao preValidacao =
          RegistarManutencaoPontoSQL.preValidarManutencao(connection, dados);

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Manutenção não pode ser registada:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Ponto de distribuição: " + dados.codigoPonto + "\nEquipe técnica: " + preValidacao.equipeId
              + "\nData da manutenção: " + dados.dataManutencao
              + (dados.alterarEstado ? "\nNovo estado do ponto: " + dados.novoEstado : "")
              + "\n\nDeseja registar esta manutenção?",
          "Confirmar Manutenção", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      RegistarManutencaoPontoSQL.ResultadoRegisto resultado =
          RegistarManutencaoPontoSQL.registarManutencao(connection, dados);

      if(!resultado.registado)
      {
        JOptionPane.showMessageDialog(this, "Manutenção não registada:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          "Manutenção registada com sucesso.\nCódigo do histórico: " + resultado.codigoHistorico + "\nMensagem: "
              + MensagensInterface.formatarMensagem(resultado.mensagem));
      areaTipoManutencao.setText("");
      radioAlterarEstadoNao.setSelected(true);
      actualizarControlosAlteracaoEstado();
      tabelaPontos.clearSelection();
      carregarPontos();
      cardLayout.show(panelCards, CARD_SELECIONAR_PONTO);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível registar manutenção:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private class TratarPesquisa implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }
  }

  private class TratarAlteracaoData implements ChangeListener
  {
    public void stateChanged(ChangeEvent e)
    {
      ajustarDataManutencao();
    }
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      if(e.getSource() == comboCriterioPesquisa)
      {
        aplicarFiltro();
        return;
      }
      if(e.getSource() == buttonActualizarPontos)
      {
        carregarPontos();
        return;
      }
      if(e.getSource() == buttonAvancar)
      {
        if(!validarPontoSelecionado())
          return;
        prepararFormulario();
        cardLayout.show(panelCards, CARD_DADOS_MANUTENCAO);
        return;
      }
      if(e.getSource() == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }
      if(e.getSource() == buttonVoltarParaPontos)
      {
        cardLayout.show(panelCards, CARD_SELECIONAR_PONTO);
        return;
      }
      if(e.getSource() == buttonRegistarManutencao)
      {
        registarManutencao();
      }
      if(e.getSource() == radioAlterarEstadoSim || e.getSource() == radioAlterarEstadoNao)
      {
        actualizarControlosAlteracaoEstado();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }
}
