package View_Interface_TransferenciasRecursosDB;

import Repository_SQL.TransferenciasRecursosDB.RegistarRecursoHidricoSQL;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class JPanel_RegistarRecursoHidrico extends JPanel
{
  private static final String CARD_MEDIDAS = "medidas";
  private static final String CARD_ANALISTAS = "analistas";
  private static final String CARD_DADOS = "dados";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final CardLayout cardLayout = new CardLayout();
  private final JPanel panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaMedidas;
  private JTable tabelaAnalistas;
  private DefaultTableModel modeloMedidas;
  private DefaultTableModel modeloAnalistas;
  private TableRowSorter<DefaultTableModel> sorterMedidas;
  private TableRowSorter<DefaultTableModel> sorterAnalistas;

  private JTextField fieldPesquisaMedidas;
  private JTextField fieldPesquisaAnalistas;
  private JComboBox<String> comboPesquisaMedidas;
  private JComboBox<String> comboPesquisaAnalistas;
  private JButton buttonActualizarMedidas;
  private JButton buttonActualizarAnalistas;
  private JButton buttonVoltarDashboard;

  private JTextField fieldValorPh;
  private JTextField fieldValorTurbidez;
  private JTextField fieldValorTemperatura;
  private JTextField fieldValorCloro;
  private JTextField fieldValorOxigenio;

  private JComboBox<String> comboTipoRh;
  private JTextField fieldLocalizacaoRh;
  private JTextField fieldVolumeRh;
  private JComboBox<String> comboSazonalidadeRh;
  private JTextField fieldOutraSazonalidadeRh;
  private JTextField fieldVulnerabilidadeRh;
  private JComboBox<String> comboNivelExploracaoRh;

  public JPanel_RegistarRecursoHidrico(final Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_RegistarRecursoHidrico(final Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_RegistarRecursoHidrico(final Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_RegistarRecursoHidrico(final Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));

    panelCards.add(criarCardMedidas(), CARD_MEDIDAS);
    panelCards.add(criarCardAnalistas(), CARD_ANALISTAS);
    panelCards.add(criarCardDados(), CARD_DADOS);
    add(panelCards, BorderLayout.CENTER);

    if(carregarTabelasAutomaticamente)
    {
      carregarMedidas();
      carregarAnalistas();
    }
  }

  private JPanel criarCardMedidas()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Medidas de Protecção",
        "<html>Seleccione zero ou várias medidas para associar ao novo recurso hídrico.</html>");
    card.add(criarCentroMedidas(), BorderLayout.CENTER);
    card.add(criarRodapeMedidas(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardAnalistas()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Analistas e Medições",
        "<html>Seleccione exactamente 5 analistas diferentes, cobrindo os 5 parâmetros obrigatórios.</html>");
    card.add(criarCentroAnalistas(), BorderLayout.CENTER);
    card.add(criarRodapeAnalistas(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardDados()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Dados do Recurso Hídrico",
        "<html>Preencha os dados do recurso. A vazão/volume estimado não será diminuída nas retiradas.</html>");
    card.add(criarCentroDados(), BorderLayout.CENTER);
    card.add(criarRodapeDados(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarPainelExterno()
  {
    JPanel painel = new JPanel(new GridBagLayout());
    painel.setOpaque(false);
    return painel;
  }

  private GridBagConstraints criarGbcCard()
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    return gbc;
  }

  private InterfaceGraficaUtils.RoundedPanel criarCardBase(String titulo, String subtitulo)
  {
    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1180, 720));
    card.add(InterfaceGraficaUtils.criarTopo(titulo, subtitulo, InterfaceGraficaUtils.FONT_TITULO,
        InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO, InterfaceGraficaUtils.COR_SUBTEXTO),
        BorderLayout.NORTH);
    return card;
  }

  private JPanel criarCentroMedidas()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    painel.add(criarPainelPesquisaMedidas(), BorderLayout.NORTH);

    modeloMedidas = new DefaultTableModel(new String[]
      {
          "Código Medida", "Código Responsável", "Nome Responsável", "Descrição"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
    tabelaMedidas = criarTabela(modeloMedidas, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sorterMedidas = new TableRowSorter<DefaultTableModel>(modeloMedidas);
    tabelaMedidas.setRowSorter(sorterMedidas);
    painel.add(criarScroll(tabelaMedidas), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarCentroAnalistas()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(20, 0));
    JPanel painelTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    painelTabela.add(criarPainelPesquisaAnalistas(), BorderLayout.NORTH);

    modeloAnalistas = new DefaultTableModel(new String[]
      {
          "Equipe", "Nome", "Código Parâmetro", "Parâmetro", "Unidade", "Especialidade", "Frequência"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
    tabelaAnalistas = criarTabela(modeloAnalistas, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sorterAnalistas = new TableRowSorter<DefaultTableModel>(modeloAnalistas);
    tabelaAnalistas.setRowSorter(sorterAnalistas);
    painelTabela.add(criarScroll(tabelaAnalistas), BorderLayout.CENTER);

    painel.add(painelTabela, BorderLayout.CENTER);
    painel.add(criarPainelValoresMedicao(), BorderLayout.EAST);
    return painel;
  }

  private JPanel criarCentroDados()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    comboTipoRh = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Subterranea", "Superficial", "Pluvial"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(260, 36));
    fieldLocalizacaoRh = criarCampoTexto();
    fieldVolumeRh = criarCampoTexto();
    comboSazonalidadeRh = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Permanente", "Chuvosa", "Seca", "Intermitente", "Outros"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(180, 36));
    fieldOutraSazonalidadeRh = criarCampoTexto();
    fieldOutraSazonalidadeRh.setEnabled(false);
    fieldVulnerabilidadeRh = criarCampoTexto();
    comboNivelExploracaoRh = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Baixo", "Medio", "Alto", "Critico"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(180, 36));
    comboSazonalidadeRh.addActionListener(tratarButtons);

    int linha = 0;
    adicionarLinhaFormulario(painel, linha++, "Tipo do recurso:", comboTipoRh);
    adicionarLinhaFormulario(painel, linha++, "Localização:", fieldLocalizacaoRh);
    adicionarLinhaFormulario(painel, linha++, "Vazão/volume estimado (L/dia):", fieldVolumeRh);
    adicionarLinhaFormulario(painel, linha++, "Sazonalidade:", criarPainelSazonalidade());
    adicionarLinhaFormulario(painel, linha++, "Vulnerabilidades:", fieldVulnerabilidadeRh);
    adicionarLinhaFormulario(painel, linha++, "Nível de exploração:", comboNivelExploracaoRh);

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = linha;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);

    return painel;
  }

  private JPanel criarPainelValoresMedicao()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    painel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(InterfaceGraficaUtils.COR_BORDA_TABELA),
        new EmptyBorder(16, 16, 16, 16)));
    painel.setPreferredSize(new Dimension(330, 430));

    JLabel titulo = InterfaceGraficaUtils.criarLabel("Valores das medições", InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_AZUL);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 14, 0);
    painel.add(titulo, gbc);

    fieldValorPh = criarCampoValor();
    fieldValorTurbidez = criarCampoValor();
    fieldValorTemperatura = criarCampoValor();
    fieldValorCloro = criarCampoValor();
    fieldValorOxigenio = criarCampoValor();

    int linha = 1;
    adicionarLinhaValor(painel, linha++, "pH:", fieldValorPh);
    adicionarLinhaValor(painel, linha++, "Turbidez:", fieldValorTurbidez);
    adicionarLinhaValor(painel, linha++, "Temperatura:", fieldValorTemperatura);
    adicionarLinhaValor(painel, linha++, "Cloro Residual:", fieldValorCloro);
    adicionarLinhaValor(painel, linha++, "Oxigénio Dissolvido:", fieldValorOxigenio);
    return painel;
  }

  private JPanel criarPainelPesquisaMedidas()
  {
    fieldPesquisaMedidas = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboPesquisaMedidas = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Medida", "Nome Responsável", "Descrição"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(180, 36));
    buttonActualizarMedidas = criarBotaoPequeno("Mostrar Todos");
    return criarPainelPesquisa(fieldPesquisaMedidas, comboPesquisaMedidas, buttonActualizarMedidas,
        new TratarPesquisaMedidas());
  }

  private JPanel criarPainelPesquisaAnalistas()
  {
    fieldPesquisaAnalistas = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboPesquisaAnalistas = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Equipe", "Nome", "Parâmetro"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(160, 36));
    buttonActualizarAnalistas = criarBotaoPequeno("Mostrar Todos");
    return criarPainelPesquisa(fieldPesquisaAnalistas, comboPesquisaAnalistas, buttonActualizarAnalistas,
        new TratarPesquisaAnalistas());
  }

  private JPanel criarPainelPesquisa(JTextField campo, JComboBox<String> combo, JButton buttonActualizar,
      DocumentListener listener)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    campo.getDocument().addDocumentListener(listener);
    combo.addActionListener(tratarButtons);
    buttonActualizar.addActionListener(tratarButtons);
    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(campo);
    painel.add(combo);
    painel.add(buttonActualizar);
    return painel;
  }

  private JPanel criarPainelSazonalidade()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    painel.add(comboSazonalidadeRh);
    painel.add(fieldOutraSazonalidadeRh);
    return painel;
  }

  private JPanel criarRodapeMedidas()
  {
    buttonVoltarDashboard = criarBotao("Voltar");
    return criarRodape(buttonVoltarDashboard, criarBotao("Proceder"));
  }

  private JPanel criarRodapeAnalistas()
  {
    return criarRodape(criarBotao("Voltar"), criarBotao("Proceder"));
  }

  private JPanel criarRodapeDados()
  {
    return criarRodape(criarBotao("Voltar"), criarBotao("Registar"));
  }

  private JPanel criarRodape(JButton voltar, JButton principal)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Confira os dados antes de continuar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    if(voltar != null)
      botoes.add(voltar);
    botoes.add(principal);
    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JTable criarTabela(DefaultTableModel modelo, int modoSelecao)
  {
    JTable tabela = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabela.setModel(modelo);
    tabela.setSelectionMode(modoSelecao);
    return tabela;
  }

  private JScrollPane criarScroll(JTable tabela)
  {
    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabela, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(780, 500));
    return scroll;
  }

  private JButton criarBotao(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(145, 42));
    IconesInterface.aplicarIconeBotao(botao);
    botao.addActionListener(tratarButtons);
    return botao;
  }

  private JButton criarBotaoPequeno(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(145, 36));
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  private JTextField criarCampoTexto()
  {
    return InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(420, 36));
  }

  private JTextField criarCampoValor()
  {
    return InterfaceGraficaUtils.criarCampoTexto(8, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(130, 36));
  }

  private void adicionarLinhaFormulario(JPanel painel, int linha, String textoLabel, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(8, 0, 8, 16);
    painel.add(InterfaceGraficaUtils.criarLabel(textoLabel, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 0, 8, 0);
    painel.add(campo, gbc);
  }

  private void adicionarLinhaValor(JPanel painel, int linha, String label, JTextField campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(8, 0, 8, 12);
    painel.add(InterfaceGraficaUtils.criarLabel(label, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.insets = new Insets(8, 0, 8, 0);
    painel.add(campo, gbc);
  }

  private void carregarMedidas()
  {
    try
    {
      RegistarRecursoHidricoSQL.carregarMedidas(connection, modeloMedidas);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar medidas de protecção:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarAnalistas()
  {
    try
    {
      RegistarRecursoHidricoSQL.carregarAnalistas(connection, modeloAnalistas);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os analistas de qualidade.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltroMedidas()
  {
    aplicarFiltro(sorterMedidas, fieldPesquisaMedidas, getIndiceFiltroMedidas());
  }

  private void aplicarFiltroAnalistas()
  {
    aplicarFiltro(sorterAnalistas, fieldPesquisaAnalistas, getIndiceFiltroAnalistas());
  }

  private void aplicarFiltro(TableRowSorter<DefaultTableModel> sorter, JTextField campo, int indice)
  {
    if(sorter == null || campo == null)
      return;

    String texto = campo.getText().trim();
    if(texto.isEmpty())
      sorter.setRowFilter(null);
    else
      sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), indice));
  }

  private int getIndiceFiltroMedidas()
  {
    String criterio = comboPesquisaMedidas.getSelectedItem().toString();
    if("Nome Responsável".equals(criterio))
      return 2;
    if("Descrição".equals(criterio))
      return 3;
    return 0;
  }

  private int getIndiceFiltroAnalistas()
  {
    String criterio = comboPesquisaAnalistas.getSelectedItem().toString();
    if("Nome".equals(criterio))
      return 1;
    if("Parâmetro".equals(criterio))
      return 3;
    return 0;
  }

  private boolean validarAnalistasSelecionados()
  {
    Map<String, Integer> analistas = obterAnalistasSelecionados();
    if(analistas.size() != 5)
    {
      JOptionPane.showMessageDialog(this,
          "Seleccione exactamente 5 analistas, um para cada parâmetro obrigatório.");
      return false;
    }
    return true;
  }

  private Map<String, Integer> obterAnalistasSelecionados()
  {
    Map<String, Integer> analistas = new LinkedHashMap<String, Integer>();
    int[] linhas = tabelaAnalistas.getSelectedRows();
    for(int linhaView : linhas)
    {
      int linhaModel = tabelaAnalistas.convertRowIndexToModel(linhaView);
      String parametro = normalizarParametro(valorTabela(modeloAnalistas, linhaModel, 3));
      int equipeId = Integer.parseInt(valorTabela(modeloAnalistas, linhaModel, 0));
      analistas.put(parametro, Integer.valueOf(equipeId));
    }
    return analistas;
  }

  private String obterCodigosMedidasSelecionadas()
  {
    int[] linhas = tabelaMedidas.getSelectedRows();
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < linhas.length; i++)
    {
      int linhaModel = tabelaMedidas.convertRowIndexToModel(linhas[i]);
      if(sb.length() > 0)
        sb.append(',');
      sb.append(valorTabela(modeloMedidas, linhaModel, 0));
    }
    return sb.toString();
  }

  private String valorTabela(DefaultTableModel modelo, int linha, int coluna)
  {
    Object valor = modelo.getValueAt(linha, coluna);
    return valor == null ? "" : valor.toString();
  }

  private String normalizarParametro(String parametro)
  {
    String valor = parametro == null ? "" : parametro.trim().toUpperCase();
    if(valor.equals("PH"))
      return "PH";
    if(valor.equals("TURBIDEZ"))
      return "TURBIDEZ";
    if(valor.equals("TEMPERATURA"))
      return "TEMPERATURA";
    if(valor.equals("CLORO RESIDUAL"))
      return "CLORO";
    if(valor.startsWith("OXIG"))
      return "OXIGENIO";
    return valor;
  }

  private boolean validarDadosRecurso()
  {
    if(fieldLocalizacaoRh.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Informe a localização do recurso hídrico.");
      return false;
    }

    if(fieldVulnerabilidadeRh.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Informe as vulnerabilidades do recurso hídrico.");
      return false;
    }

    if(obterSazonalidade().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Informe a sazonalidade do recurso hídrico.");
      return false;
    }

    try
    {
      if(parseDouble(fieldVolumeRh.getText()) <= 0)
      {
        JOptionPane.showMessageDialog(this, "A vazão/volume estimado deve ser maior que zero.");
        return false;
      }
      parseDouble(fieldValorPh.getText());
      parseDouble(fieldValorTurbidez.getText());
      parseDouble(fieldValorTemperatura.getText());
      parseDouble(fieldValorCloro.getText());
      parseDouble(fieldValorOxigenio.getText());
    } catch(NumberFormatException ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return false;

      JOptionPane.showMessageDialog(this, "Volume e valores das medições devem ser numéricos.");
      return false;
    }

    return true;
  }

  private double parseDouble(String valor)
  {
    return Double.parseDouble(valor.trim().replace(',', '.'));
  }

  private String obterSazonalidade()
  {
    if("Outros".equals(comboSazonalidadeRh.getSelectedItem().toString()))
      return fieldOutraSazonalidadeRh.getText().trim();
    return comboSazonalidadeRh.getSelectedItem().toString();
  }

  private RegistarRecursoHidricoSQL.DadosRecurso criarDadosRecurso()
  {
    Map<String, Integer> analistas = obterAnalistasSelecionados();
    return new RegistarRecursoHidricoSQL.DadosRecurso(comboTipoRh.getSelectedItem().toString(),
        fieldLocalizacaoRh.getText().trim(), parseDouble(fieldVolumeRh.getText()), obterSazonalidade(),
        fieldVulnerabilidadeRh.getText().trim(), comboNivelExploracaoRh.getSelectedItem().toString(),
        obterCodigosMedidasSelecionadas(), analistas.get("PH").intValue(), parseDouble(fieldValorPh.getText()),
        analistas.get("TURBIDEZ").intValue(), parseDouble(fieldValorTurbidez.getText()),
        analistas.get("TEMPERATURA").intValue(), parseDouble(fieldValorTemperatura.getText()),
        analistas.get("CLORO").intValue(), parseDouble(fieldValorCloro.getText()),
        analistas.get("OXIGENIO").intValue(), parseDouble(fieldValorOxigenio.getText()));
  }

  private void registarRecurso()
  {
    if(!validarAnalistasSelecionados() || !validarDadosRecurso())
      return;

    RegistarRecursoHidricoSQL.DadosRecurso dados = criarDadosRecurso();

    try
    {
      RegistarRecursoHidricoSQL.ResultadoPreValidacao preValidacao =
          RegistarRecursoHidricoSQL.preValidarRegisto(connection, dados);

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Recurso hídrico não pode ser registado:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Tipo: " + dados.tipoRh + "\nLocalização: " + dados.localizacaoRh + "\nVazão/volume: " + dados.volumeRh
              + " L/dia\nSazonalidade: " + dados.sazonalidadeRh + "\nMedidas seleccionadas: "
              + (dados.codMedidas == null || dados.codMedidas.isEmpty() ? "Nenhuma" : dados.codMedidas)
              + "\n\nDeseja registar este recurso hídrico?",
          "Confirmar Recurso Hídrico", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      RegistarRecursoHidricoSQL.ResultadoRegisto resultado =
          RegistarRecursoHidricoSQL.registarRecurso(connection, dados);

      if(!resultado.registado)
      {
        JOptionPane.showMessageDialog(this, "Recurso hídrico não registado:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          "Recurso hídrico registado com sucesso.\nCódigo: " + resultado.codigoRecurso + "\nMensagem: "
              + MensagensInterface.formatarMensagem(resultado.mensagem));
      limparFormulario();
      carregarMedidas();
      carregarAnalistas();
      cardLayout.show(panelCards, CARD_MEDIDAS);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível registar recurso hídrico:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void limparFormulario()
  {
    tabelaMedidas.clearSelection();
    tabelaAnalistas.clearSelection();
    fieldValorPh.setText("");
    fieldValorTurbidez.setText("");
    fieldValorTemperatura.setText("");
    fieldValorCloro.setText("");
    fieldValorOxigenio.setText("");
    fieldLocalizacaoRh.setText("");
    fieldVolumeRh.setText("");
    fieldVulnerabilidadeRh.setText("");
    comboTipoRh.setSelectedIndex(0);
    comboSazonalidadeRh.setSelectedIndex(0);
    comboNivelExploracaoRh.setSelectedIndex(0);
  }

  private final class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      String texto = event.getSource() instanceof JButton ? ((JButton) event.getSource()).getText() : "";

      if(event.getSource() == comboPesquisaMedidas)
        aplicarFiltroMedidas();
      else if(event.getSource() == comboPesquisaAnalistas)
        aplicarFiltroAnalistas();
      else if(event.getSource() == buttonActualizarMedidas)
      {
        carregarMedidas();
        aplicarFiltroMedidas();
      }
      else if(event.getSource() == buttonActualizarAnalistas)
      {
        carregarAnalistas();
        aplicarFiltroAnalistas();
      }
      else if(event.getSource() == comboSazonalidadeRh)
      {
        boolean outros = "Outros".equals(comboSazonalidadeRh.getSelectedItem().toString());
        fieldOutraSazonalidadeRh.setEnabled(outros);
        if(!outros)
          fieldOutraSazonalidadeRh.setText("");
      }
      else if("Proceder".equals(texto))
      {
        if(tabelaAnalistas != null && tabelaAnalistas.isShowing())
        {
          if(validarAnalistasSelecionados())
            cardLayout.show(panelCards, CARD_DADOS);
        }
        else
          cardLayout.show(panelCards, CARD_ANALISTAS);
      }
      else if("Voltar".equals(texto))
      {
        if(event.getSource() == buttonVoltarDashboard)
          voltarAoDashboard();
        else if(comboTipoRh != null && comboTipoRh.isShowing())
          cardLayout.show(panelCards, CARD_ANALISTAS);
        else
          cardLayout.show(panelCards, CARD_MEDIDAS);
      }
      else if("Registar".equals(texto))
        registarRecurso();
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }

  private final class TratarPesquisaMedidas implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroMedidas();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroMedidas();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroMedidas();
    }
  }

  private final class TratarPesquisaAnalistas implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroAnalistas();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroAnalistas();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroAnalistas();
    }
  }
}
