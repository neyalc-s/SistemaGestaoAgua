package View_Interface_TransferenciasRecursosDB;

import Resources.MensagensInterface;

import Repository_SQL.TransferenciasRecursosDB.RegistarMedicaoQualidadeAguaSQL;
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
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class JPanel_RegistarMedicaoQualidadeAgua extends JPanel
{
  private static final String CARD_RECURSO = "recurso";
  private static final String CARD_ANALISTAS = "analistas";
  private static final String CARD_VALORES = "valores";

  private static final String[] PARAMETROS_OBRIGATORIOS =
    {
        "PH", "TURBIDEZ", "TEMPERATURA", "CLORO RESIDUAL", "OXIGENIO DISSOLVIDO"
    };

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final TratarButtons tratarButtons = new TratarButtons();
  private final Map<String, AnalistaSelecionado> analistasSelecionados = new LinkedHashMap<String, AnalistaSelecionado>();
  private final Map<String, JTextField> camposValores = new LinkedHashMap<String, JTextField>();

  private CardLayout cardLayout;
  private JPanel panelCards;

  private JTable tabelaRecursos;
  private DefaultTableModel modeloRecursos;
  private TableRowSorter<DefaultTableModel> sorterRecursos;
  private JTextField fieldPesquisaRecursos;
  private JComboBox<String> comboCriterioRecursos;
  private JButton buttonActualizarRecursos;
  private JButton buttonVoltarDashboard;
  private JButton buttonAvancarParaAnalistas;

  private JTable tabelaAnalistas;
  private DefaultTableModel modeloAnalistas;
  private TableRowSorter<DefaultTableModel> sorterAnalistas;
  private JTextField fieldPesquisaAnalistas;
  private JComboBox<String> comboCriterioAnalistas;
  private JTextField fieldRecursoSelecionadoAnalistas;
  private JButton buttonActualizarAnalistas;
  private JButton buttonVoltarParaRecurso;
  private JButton buttonAvancarParaValores;

  private JTextField fieldRecursoSelecionadoValores;
  private JPanel painelLinhasValores;
  private JButton buttonVoltarParaAnalistas;
  private JButton buttonRegistar;

  public JPanel_RegistarMedicaoQualidadeAgua(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_RegistarMedicaoQualidadeAgua(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_RegistarMedicaoQualidadeAgua(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_RegistarMedicaoQualidadeAgua(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
    panelCards.add(criarCardRecurso(), CARD_RECURSO);
    panelCards.add(criarCardAnalistas(), CARD_ANALISTAS);
    panelCards.add(criarCardValores(), CARD_VALORES);

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
      carregarAnalistas();
    }
  }

  private JPanel criarCardRecurso()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(InterfaceGraficaUtils.criarTopo("Registar Medição de Qualidade da Água",
        "<html>Seleccione o recurso hídrico onde serão registadas as 5 medições obrigatórias.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.NORTH);
    card.add(criarCentroRecursos(), BorderLayout.CENTER);
    card.add(criarRodapeRecurso(), BorderLayout.SOUTH);

    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardAnalistas()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(InterfaceGraficaUtils.criarTopo("Selecionar Analistas de Qualidade",
        "<html>Seleccione exactamente 5 analistas, um para cada parâmetro obrigatório.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.NORTH);
    card.add(criarCentroAnalistas(), BorderLayout.CENTER);
    card.add(criarRodapeAnalistas(), BorderLayout.SOUTH);

    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardValores()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(InterfaceGraficaUtils.criarTopo("Preencher Valores das Medições",
        "<html>Confirme os analistas e preencha apenas o valor medido para cada parâmetro.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.NORTH);
    card.add(criarCentroValores(), BorderLayout.CENTER);
    card.add(criarRodapeValores(), BorderLayout.SOUTH);

    painelExterno.add(card, criarGbcCard());
    return painelExterno;
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

  private JPanel criarCentroRecursos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    JLabel label = InterfaceGraficaUtils.criarLabel("Recursos Hídricos", InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(label, BorderLayout.NORTH);
    topoTabela.add(criarPainelPesquisaRecursos(), BorderLayout.SOUTH);

    painel.add(topoTabela, BorderLayout.NORTH);
    painel.add(criarScrollRecursos(), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarCentroAnalistas()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    fieldRecursoSelecionadoAnalistas = criarCampoResumo(260);
    JPanel painelResumo = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painelResumo.add(InterfaceGraficaUtils.criarLabel("Recurso seleccionado:", InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_TEXTO));
    painelResumo.add(fieldRecursoSelecionadoAnalistas);

    JLabel label = InterfaceGraficaUtils.criarLabel("Analistas de Qualidade",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(painelResumo, BorderLayout.NORTH);
    topoTabela.add(label, BorderLayout.CENTER);
    topoTabela.add(criarPainelPesquisaAnalistas(), BorderLayout.SOUTH);

    painel.add(topoTabela, BorderLayout.NORTH);
    painel.add(criarScrollAnalistas(), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarCentroValores()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 14));

    fieldRecursoSelecionadoValores = criarCampoResumo(260);
    JPanel resumo = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    resumo.add(InterfaceGraficaUtils.criarLabel("Recurso seleccionado:", InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_TEXTO));
    resumo.add(fieldRecursoSelecionadoValores);
    resumo.add(InterfaceGraficaUtils.criarLabel("Data da medição: agora (SYSDATE)",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO));

    painelLinhasValores = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    painel.add(resumo, BorderLayout.NORTH);
    painel.add(new JScrollPane(painelLinhasValores), BorderLayout.CENTER);
    return painel;
  }

  private JTextField criarCampoResumo(int largura)
  {
    JTextField campo = new JTextField();
    campo.setEditable(false);
    campo.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    campo.setPreferredSize(new Dimension(largura, 36));
    return campo;
  }

  private JPanel criarPainelPesquisaRecursos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    fieldPesquisaRecursos = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboCriterioRecursos = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Recurso", "Tipo", "Localização", "Sazonalidade", "Nível Exploração"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(210, 36));
    buttonActualizarRecursos = criarBotao("Mostrar Todos");

    fieldPesquisaRecursos.getDocument().addDocumentListener(new TratarPesquisaRecursos());
    comboCriterioRecursos.addActionListener(tratarButtons);
    buttonActualizarRecursos.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisaRecursos);
    painel.add(comboCriterioRecursos);
    painel.add(buttonActualizarRecursos);
    return painel;
  }

  private JPanel criarPainelPesquisaAnalistas()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    fieldPesquisaAnalistas = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboCriterioAnalistas = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Equipe", "Nome", "Código Parâmetro", "Parâmetro", "Especialidade"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(210, 36));
    buttonActualizarAnalistas = criarBotao("Mostrar Todos");

    fieldPesquisaAnalistas.getDocument().addDocumentListener(new TratarPesquisaAnalistas());
    comboCriterioAnalistas.addActionListener(tratarButtons);
    buttonActualizarAnalistas.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisaAnalistas);
    painel.add(comboCriterioAnalistas);
    painel.add(buttonActualizarAnalistas);
    return painel;
  }

  private JScrollPane criarScrollRecursos()
  {
    modeloRecursos = criarModeloNaoEditavel(new String[]
      {
          "Código Recurso", "Tipo", "Localização", "Vazão/Volume", "Sazonalidade", "Nível Exploração"
      });
    tabelaRecursos = criarTabela(modeloRecursos);
    tabelaRecursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterRecursos = new TableRowSorter<DefaultTableModel>(modeloRecursos);
    tabelaRecursos.setRowSorter(sorterRecursos);
    return InterfaceGraficaUtils.criarScrollTabela(tabelaRecursos, InterfaceGraficaUtils.COR_BORDA_TABELA);
  }

  private JScrollPane criarScrollAnalistas()
  {
    modeloAnalistas = criarModeloNaoEditavel(new String[]
      {
          "Código Equipe", "Nome", "Código Parâmetro", "Parâmetro", "Unidade", "Especialidade", "Frequência"
      });
    tabelaAnalistas = criarTabela(modeloAnalistas);
    tabelaAnalistas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sorterAnalistas = new TableRowSorter<DefaultTableModel>(modeloAnalistas);
    tabelaAnalistas.setRowSorter(sorterAnalistas);
    return InterfaceGraficaUtils.criarScrollTabela(tabelaAnalistas, InterfaceGraficaUtils.COR_BORDA_TABELA);
  }

  private JPanel criarRodapeRecurso()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Seleccione apenas um recurso hídrico.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar");
    buttonAvancarParaAnalistas = criarBotao("Proceder");
    buttonVoltarDashboard.addActionListener(tratarButtons);
    buttonAvancarParaAnalistas.addActionListener(tratarButtons);
    botoes.add(buttonVoltarDashboard);
    botoes.add(buttonAvancarParaAnalistas);
    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarRodapeAnalistas()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Use Ctrl ou Shift e selecione um analista por parâmetro.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarParaRecurso = criarBotao("Voltar");
    buttonAvancarParaValores = criarBotao("Proceder");
    buttonVoltarParaRecurso.addActionListener(tratarButtons);
    buttonAvancarParaValores.addActionListener(tratarButtons);
    botoes.add(buttonVoltarParaRecurso);
    botoes.add(buttonAvancarParaValores);
    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarRodapeValores()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("A BD fará a pré-validação antes de registar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarParaAnalistas = criarBotao("Voltar");
    buttonRegistar = criarBotao("Registar");
    buttonVoltarParaAnalistas.addActionListener(tratarButtons);
    buttonRegistar.addActionListener(tratarButtons);
    botoes.add(buttonVoltarParaAnalistas);
    botoes.add(buttonRegistar);
    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
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
    return botao;
  }

  private void carregarRecursos()
  {
    try
    {
      RegistarMedicaoQualidadeAguaSQL.carregarRecursos(connection, modeloRecursos);
      aplicarFiltroRecursos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os recursos hídricos.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarAnalistas()
  {
    try
    {
      RegistarMedicaoQualidadeAguaSQL.carregarAnalistas(connection, modeloAnalistas);
      aplicarFiltroAnalistas();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os analistas.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltroRecursos()
  {
    aplicarFiltro(sorterRecursos, fieldPesquisaRecursos, getIndiceColunaRecurso());
  }

  private void aplicarFiltroAnalistas()
  {
    aplicarFiltro(sorterAnalistas, fieldPesquisaAnalistas, getIndiceColunaAnalista());
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

  private int getIndiceColunaRecurso()
  {
    String criterio = comboCriterioRecursos.getSelectedItem().toString();
    if("Código Recurso".equals(criterio))
      return 0;
    if("Tipo".equals(criterio))
      return 1;
    if("Localização".equals(criterio))
      return 2;
    if("Sazonalidade".equals(criterio))
      return 4;
    if("Nível Exploração".equals(criterio))
      return 5;
    return 0;
  }

  private int getIndiceColunaAnalista()
  {
    String criterio = comboCriterioAnalistas.getSelectedItem().toString();
    if("Código Equipe".equals(criterio))
      return 0;
    if("Nome".equals(criterio))
      return 1;
    if("Código Parâmetro".equals(criterio))
      return 2;
    if("Parâmetro".equals(criterio))
      return 3;
    if("Especialidade".equals(criterio))
      return 5;
    return 0;
  }

  private Integer getCodigoRecursoSelecionado()
  {
    int linhaView = tabelaRecursos.getSelectedRow();
    if(linhaView == -1)
      return null;
    int linhaModel = tabelaRecursos.convertRowIndexToModel(linhaView);
    return Integer.valueOf(modeloRecursos.getValueAt(linhaModel, 0).toString());
  }

  private boolean prepararAnalistasSelecionados()
  {
    int[] linhas = tabelaAnalistas.getSelectedRows();
    if(linhas.length != 5)
    {
      JOptionPane.showMessageDialog(this, "Seleccione exactamente 5 analistas de qualidade.");
      return false;
    }

    analistasSelecionados.clear();

    for(int linhaView : linhas)
    {
      int linhaModel = tabelaAnalistas.convertRowIndexToModel(linhaView);
      String parametro = normalizar(modeloAnalistas.getValueAt(linhaModel, 3).toString());
      AnalistaSelecionado analista = new AnalistaSelecionado(
          Integer.parseInt(modeloAnalistas.getValueAt(linhaModel, 0).toString()),
          modeloAnalistas.getValueAt(linhaModel, 1).toString(),
          Integer.parseInt(modeloAnalistas.getValueAt(linhaModel, 2).toString()),
          modeloAnalistas.getValueAt(linhaModel, 3).toString(),
          modeloAnalistas.getValueAt(linhaModel, 4).toString());

      if(analistasSelecionados.containsKey(parametro))
      {
        JOptionPane.showMessageDialog(this, "Existe mais de um analista seleccionado para o parâmetro "
            + analista.nomeParametro + ".");
        return false;
      }

      analistasSelecionados.put(parametro, analista);
    }

    StringBuilder faltam = new StringBuilder();
    for(String parametro : PARAMETROS_OBRIGATORIOS)
    {
      if(!analistasSelecionados.containsKey(parametro))
      {
        if(faltam.length() > 0)
          faltam.append(", ");
        faltam.append(parametro);
      }
    }

    if(faltam.length() > 0)
    {
      JOptionPane.showMessageDialog(this, "Faltam analistas para os parâmetros: " + faltam + ".");
      return false;
    }

    return true;
  }

  private void montarFormularioValores()
  {
    camposValores.clear();
    painelLinhasValores.removeAll();

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 8, 6, 8);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    String[] cabecalhos =
      {
          "Parâmetro", "Unidade", "Analista", "Valor"
      };
    for(int i = 0; i < cabecalhos.length; i++)
    {
      gbc.gridx = i;
      gbc.gridy = 0;
      gbc.weightx = i == 3 ? 0.2 : 0.3;
      painelLinhasValores.add(InterfaceGraficaUtils.criarLabel(cabecalhos[i], InterfaceGraficaUtils.FONT_LABEL_SECAO,
          InterfaceGraficaUtils.COR_AZUL), gbc);
    }

    int linha = 1;
    for(String parametro : PARAMETROS_OBRIGATORIOS)
    {
      AnalistaSelecionado analista = analistasSelecionados.get(parametro);
      JTextField valor = InterfaceGraficaUtils.criarCampoTexto(10, InterfaceGraficaUtils.FONT_CAMPO,
          new Dimension(140, 36));
      camposValores.put(parametro, valor);

      adicionarTextoValor(linha, 0, analista.nomeParametro);
      adicionarTextoValor(linha, 1, analista.unidadePadrao);
      adicionarTextoValor(linha, 2, analista.equipeId + " - " + analista.nomeAnalista);

      gbc.gridx = 3;
      gbc.gridy = linha;
      gbc.weightx = 0.2;
      painelLinhasValores.add(valor, gbc);
      linha++;
    }

    painelLinhasValores.revalidate();
    painelLinhasValores.repaint();
  }

  private void adicionarTextoValor(int linha, int coluna, String texto)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = coluna;
    gbc.gridy = linha;
    gbc.weightx = coluna == 2 ? 0.4 : 0.2;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(6, 8, 6, 8);

    JTextField campo = criarCampoResumo(coluna == 2 ? 360 : 190);
    campo.setText(texto);
    painelLinhasValores.add(campo, gbc);
  }

  private RegistarMedicaoQualidadeAguaSQL.DadosMedicoes criarDadosMedicoes()
  {
    int codigoRh = getCodigoRecursoSelecionado().intValue();
    return new RegistarMedicaoQualidadeAguaSQL.DadosMedicoes(codigoRh, getAnalista("PH").equipeId,
        lerValor("PH"), getAnalista("TURBIDEZ").equipeId, lerValor("TURBIDEZ"),
        getAnalista("TEMPERATURA").equipeId, lerValor("TEMPERATURA"), getAnalista("CLORO RESIDUAL").equipeId,
        lerValor("CLORO RESIDUAL"), getAnalista("OXIGENIO DISSOLVIDO").equipeId,
        lerValor("OXIGENIO DISSOLVIDO"));
  }

  private AnalistaSelecionado getAnalista(String parametro)
  {
    return analistasSelecionados.get(parametro);
  }

  private double lerValor(String parametro)
  {
    String texto = camposValores.get(parametro).getText().trim().replace(',', '.');
    if(texto.isEmpty())
      throw new IllegalArgumentException("Preencha o valor de " + parametroVisual(parametro) + ".");
    return Double.parseDouble(texto);
  }

  private String parametroVisual(String parametro)
  {
    if("OXIGENIO DISSOLVIDO".equals(parametro))
      return "OXIGÉNIO DISSOLVIDO";
    return parametro;
  }

  private void registarMedicoes()
  {
    try
    {
      RegistarMedicaoQualidadeAguaSQL.DadosMedicoes dados = criarDadosMedicoes();
      RegistarMedicaoQualidadeAguaSQL.ResultadoPreValidacao pre =
          RegistarMedicaoQualidadeAguaSQL.preValidarRegisto(connection, dados);

      if(!pre.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Registo bloqueado:\n" + MensagensInterface.formatarMensagem(pre.mensagem));
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Recurso hídrico: " + dados.codigoRh + "\nMedições a registar: " + pre.totalMedicoes
              + "\nData da medição: agora\n\nConfirmar registo?",
          "Confirmar Registo", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      RegistarMedicaoQualidadeAguaSQL.ResultadoRegisto resultado =
          RegistarMedicaoQualidadeAguaSQL.registarMedicoes(connection, dados);

      if(!resultado.registado)
      {
        JOptionPane.showMessageDialog(this, "Registo não concluído:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this, resultado.totalRegistadas + " medições registadas com sucesso.");
      tabelaRecursos.clearSelection();
      tabelaAnalistas.clearSelection();
      analistasSelecionados.clear();
      camposValores.clear();
      cardLayout.show(panelCards, CARD_RECURSO);
    } catch(NumberFormatException ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Os valores das medições devem ser numéricos.");
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível registar medições:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private String normalizar(String texto)
  {
    String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    return semAcentos.trim().toUpperCase();
  }

  private class TratarPesquisaRecursos implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) { aplicarFiltroRecursos(); }
    public void removeUpdate(DocumentEvent e) { aplicarFiltroRecursos(); }
    public void changedUpdate(DocumentEvent e) { aplicarFiltroRecursos(); }
  }

  private class TratarPesquisaAnalistas implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) { aplicarFiltroAnalistas(); }
    public void removeUpdate(DocumentEvent e) { aplicarFiltroAnalistas(); }
    public void changedUpdate(DocumentEvent e) { aplicarFiltroAnalistas(); }
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

      if(e.getSource() == comboCriterioAnalistas)
      {
        aplicarFiltroAnalistas();
        return;
      }

      if(e.getSource() == buttonActualizarRecursos)
      {
        carregarRecursos();
        return;
      }

      if(e.getSource() == buttonActualizarAnalistas)
      {
        carregarAnalistas();
        return;
      }

      if(e.getSource() == buttonAvancarParaAnalistas)
      {
        Integer codigoRh = getCodigoRecursoSelecionado();
        if(codigoRh == null)
        {
          JOptionPane.showMessageDialog(JPanel_RegistarMedicaoQualidadeAgua.this, "Seleccione um recurso hídrico.");
          return;
        }
        fieldRecursoSelecionadoAnalistas.setText(codigoRh.toString());
        cardLayout.show(panelCards, CARD_ANALISTAS);
        return;
      }

      if(e.getSource() == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }

      if(e.getSource() == buttonVoltarParaRecurso)
      {
        cardLayout.show(panelCards, CARD_RECURSO);
        return;
      }

      if(e.getSource() == buttonAvancarParaValores)
      {
        if(!prepararAnalistasSelecionados())
          return;
        fieldRecursoSelecionadoValores.setText(getCodigoRecursoSelecionado().toString());
        montarFormularioValores();
        cardLayout.show(panelCards, CARD_VALORES);
        return;
      }

      if(e.getSource() == buttonVoltarParaAnalistas)
      {
        cardLayout.show(panelCards, CARD_ANALISTAS);
        return;
      }

      if(e.getSource() == buttonRegistar)
      {
        registarMedicoes();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }

  private static final class AnalistaSelecionado
  {
    final int equipeId;
    final String nomeAnalista;
    final int codigoParametro;
    final String nomeParametro;
    final String unidadePadrao;

    AnalistaSelecionado(int equipeId, String nomeAnalista, int codigoParametro, String nomeParametro,
        String unidadePadrao)
    {
      this.equipeId = equipeId;
      this.nomeAnalista = nomeAnalista;
      this.codigoParametro = codigoParametro;
      this.nomeParametro = nomeParametro;
      this.unidadePadrao = unidadePadrao;
    }
  }
}
