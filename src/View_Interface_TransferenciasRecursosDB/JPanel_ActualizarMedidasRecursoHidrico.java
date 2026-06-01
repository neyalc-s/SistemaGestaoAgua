package View_Interface_TransferenciasRecursosDB;

import Repository_SQL.TransferenciasRecursosDB.ActualizarMedidasRecursoHidricoSQL;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class JPanel_ActualizarMedidasRecursoHidrico extends JPanel
{
  private static final String CARD_RECURSOS = "recursos";
  private static final String CARD_MEDIDAS = "medidas";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final java.awt.CardLayout cardLayout = new java.awt.CardLayout();
  private final JPanel panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
  private final TratarButtons tratarButtons = new TratarButtons();
  private boolean ajustandoData;

  private JTable tabelaRecursos;
  private JTable tabelaAplicadas;
  private JTable tabelaDisponiveis;
  private DefaultTableModel modeloRecursos;
  private DefaultTableModel modeloAplicadas;
  private DefaultTableModel modeloDisponiveis;
  private TableRowSorter<DefaultTableModel> sorterRecursos;
  private TableRowSorter<DefaultTableModel> sorterAplicadas;
  private TableRowSorter<DefaultTableModel> sorterDisponiveis;
  private JTextField fieldPesquisaRecursos;
  private JTextField fieldPesquisaAplicadas;
  private JTextField fieldPesquisaDisponiveis;
  private JComboBox<String> comboPesquisaRecursos;
  private JTextField fieldCodigoRh;
  private JTextField fieldTipoRh;
  private JTextField fieldResumoMedidas;
  private JSpinner spinnerDiaImpl;
  private JSpinner spinnerMesImpl;
  private JSpinner spinnerAnoImpl;
  private JButton buttonAvancar;
  private JButton buttonVoltarDashboard;
  private JButton buttonVoltar;
  private JButton buttonAdicionar;
  private JButton buttonRemover;
  private JButton buttonActualizar;
  private JButton buttonActualizarMedidas;

  public JPanel_ActualizarMedidasRecursoHidrico(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_ActualizarMedidasRecursoHidrico(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_ActualizarMedidasRecursoHidrico(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_ActualizarMedidasRecursoHidrico(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));

    panelCards.add(criarCardRecursos(), CARD_RECURSOS);
    panelCards.add(criarCardMedidas(), CARD_MEDIDAS);
    add(panelCards, BorderLayout.CENTER);

    if(carregarTabelasAutomaticamente)
    {
      carregarRecursos();
    }
  }

  private JPanel criarCardRecursos()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Actualizar Medidas do Recurso Hídrico",
        "<html>Seleccione um recurso hídrico para adicionar ou remover medidas de protecção aplicadas.</html>");
    card.add(criarCentroRecursos(), BorderLayout.CENTER);
    card.add(criarRodapeRecursos(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardMedidas()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Gerir Medidas de Protecção",
        "<html>Adicione medidas disponíveis com data de implementação ou remova medidas já aplicadas ao recurso.</html>");
    card.add(criarCentroMedidas(), BorderLayout.CENTER);
    card.add(criarRodapeMedidas(), BorderLayout.SOUTH);
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

  private JPanel criarCentroRecursos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    painel.add(criarPesquisaRecursos(), BorderLayout.NORTH);

    modeloRecursos = new DefaultTableModel(new String[]
      {
          "Código RH", "Tipo", "Localização", "Vazão/Volume", "Sazonalidade", "Nível Exploração"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
    tabelaRecursos = criarTabela(modeloRecursos);
    tabelaRecursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterRecursos = new TableRowSorter<DefaultTableModel>(modeloRecursos);
    tabelaRecursos.setRowSorter(sorterRecursos);
    painel.add(criarScroll(tabelaRecursos, new Dimension(1120, 500)), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarCentroMedidas()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 14));
    painel.add(criarResumoRecurso(), BorderLayout.NORTH);

    JPanel tabelas = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    adicionarPainelTabela(tabelas, 0, "Medidas Aplicadas ao Recurso", criarPainelAplicadas());
    adicionarPainelTabela(tabelas, 1, "Medidas Disponíveis", criarPainelDisponiveis());
    painel.add(tabelas, BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarResumoRecurso()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    fieldCodigoRh = criarCampoReadonly();
    fieldTipoRh = criarCampoReadonly();
    fieldResumoMedidas = criarCampoReadonly();

    adicionarLinhaResumo(painel, 0, "Código do recurso:", fieldCodigoRh);
    adicionarLinhaResumo(painel, 1, "Tipo:", fieldTipoRh);
    adicionarLinhaResumo(painel, 2, "Medidas aplicadas:", fieldResumoMedidas);
    adicionarLinhaResumo(painel, 3, "Data de implementação:", criarPainelData());
    return painel;
  }

  private JPanel criarPainelData()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    LocalDate hoje = LocalDate.now();
    spinnerDiaImpl = criarSpinnerData(hoje.getDayOfMonth(), 1, hoje.lengthOfMonth());
    spinnerMesImpl = criarSpinnerData(hoje.getMonthValue(), 1, 12);
    spinnerAnoImpl = criarSpinnerData(hoje.getYear(), 1900, hoje.getYear());
    InterfaceGraficaUtils.removerSeparadorMilhares(spinnerAnoImpl);

    ChangeListener listener = new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          ajustarDataImplementacao();
        }
      };
    spinnerDiaImpl.addChangeListener(listener);
    spinnerMesImpl.addChangeListener(listener);
    spinnerAnoImpl.addChangeListener(listener);

    painel.add(InterfaceGraficaUtils.criarLabel("Dia", InterfaceGraficaUtils.FONT_AJUDA,
        InterfaceGraficaUtils.COR_SUBTEXTO));
    painel.add(spinnerDiaImpl);
    painel.add(InterfaceGraficaUtils.criarLabel("Mês", InterfaceGraficaUtils.FONT_AJUDA,
        InterfaceGraficaUtils.COR_SUBTEXTO));
    painel.add(spinnerMesImpl);
    painel.add(InterfaceGraficaUtils.criarLabel("Ano", InterfaceGraficaUtils.FONT_AJUDA,
        InterfaceGraficaUtils.COR_SUBTEXTO));
    painel.add(spinnerAnoImpl);
    return painel;
  }

  private JPanel criarPainelAplicadas()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    fieldPesquisaAplicadas = criarCampoPesquisa();
    fieldPesquisaAplicadas.getDocument().addDocumentListener(new TratarPesquisaAplicadas());
    painel.add(criarLinhaPesquisa(fieldPesquisaAplicadas), BorderLayout.NORTH);

    modeloAplicadas = new DefaultTableModel(new String[]
      {
          "Código", "Descrição", "Responsável", "Data Impl."
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
    tabelaAplicadas = criarTabela(modeloAplicadas);
    tabelaAplicadas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sorterAplicadas = new TableRowSorter<DefaultTableModel>(modeloAplicadas);
    tabelaAplicadas.setRowSorter(sorterAplicadas);
    painel.add(criarScroll(tabelaAplicadas, new Dimension(520, 345)), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarPainelDisponiveis()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    fieldPesquisaDisponiveis = criarCampoPesquisa();
    fieldPesquisaDisponiveis.getDocument().addDocumentListener(new TratarPesquisaDisponiveis());
    painel.add(criarLinhaPesquisa(fieldPesquisaDisponiveis), BorderLayout.NORTH);

    modeloDisponiveis = new DefaultTableModel(new String[]
      {
          "Código", "Descrição", "Responsável"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
    tabelaDisponiveis = criarTabela(modeloDisponiveis);
    tabelaDisponiveis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sorterDisponiveis = new TableRowSorter<DefaultTableModel>(modeloDisponiveis);
    tabelaDisponiveis.setRowSorter(sorterDisponiveis);
    painel.add(criarScroll(tabelaDisponiveis, new Dimension(520, 345)), BorderLayout.CENTER);
    return painel;
  }

  private void adicionarPainelTabela(JPanel painel, int coluna, String titulo, JPanel tabelaPanel)
  {
    JPanel wrapper = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    wrapper.add(InterfaceGraficaUtils.criarLabel(titulo, InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_AZUL), BorderLayout.NORTH);
    wrapper.add(tabelaPanel, BorderLayout.CENTER);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = coluna;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, coluna == 0 ? 0 : 12, 0, coluna == 0 ? 12 : 0);
    painel.add(wrapper, gbc);
  }

  private JPanel criarPesquisaRecursos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    fieldPesquisaRecursos = criarCampoPesquisa();
    fieldPesquisaRecursos.getDocument().addDocumentListener(new TratarPesquisaRecursos());
    comboPesquisaRecursos = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código RH", "Tipo", "Localização"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(180, 36));
    comboPesquisaRecursos.addActionListener(tratarButtons);
    buttonActualizar = criarBotao("Mostrar Todos", 150);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisaRecursos);
    painel.add(comboPesquisaRecursos);
    painel.add(buttonActualizar);
    return painel;
  }

  private JPanel criarLinhaPesquisa(JTextField campo)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(campo);
    return painel;
  }

  private JPanel criarRodapeRecursos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Seleccione um recurso hídrico para continuar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar", 120);
    buttonAvancar = criarBotao("Proceder", 145);
    botoes.add(buttonVoltarDashboard);
    botoes.add(buttonAvancar);
    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarRodapeMedidas()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("A data é usada apenas ao adicionar novas medidas.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltar = criarBotao("Voltar", 120);
    buttonRemover = criarBotao("Remover", 130);
    buttonAdicionar = criarBotao("Adicionar", 130);
    buttonActualizarMedidas = criarBotao("Actualizar", 135);
    botoes.add(buttonVoltar);
    botoes.add(buttonActualizarMedidas);
    botoes.add(buttonRemover);
    botoes.add(buttonAdicionar);
    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JTable criarTabela(DefaultTableModel modelo)
  {
    JTable tabela = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabela.setModel(modelo);
    return tabela;
  }

  private JScrollPane criarScroll(JTable tabela, Dimension tamanho)
  {
    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabela, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(tamanho);
    return scroll;
  }

  private JTextField criarCampoPesquisa()
  {
    return InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(250, 36));
  }

  private JTextField criarCampoReadonly()
  {
    JTextField campo = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(420, 36));
    campo.setEditable(false);
    return campo;
  }

  private JSpinner criarSpinnerData(int valorInicial, int minimo, int maximo)
  {
    return InterfaceGraficaUtils.criarSpinnerNumero(valorInicial, minimo, maximo, 1, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(76, 36));
  }

  private JButton criarBotao(String texto, int largura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, 40));
    IconesInterface.aplicarIconeBotao(botao);
    botao.addActionListener(tratarButtons);
    return botao;
  }

  private void adicionarLinhaResumo(JPanel painel, int linha, String label, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(5, 0, 5, 14);
    painel.add(InterfaceGraficaUtils.criarLabel(label, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 0, 5, 0);
    painel.add(campo, gbc);
  }

  private void carregarRecursos()
  {
    try
    {
      ActualizarMedidasRecursoHidricoSQL.carregarRecursos(connection, modeloRecursos);
      aplicarFiltroRecursos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os recursos hídricos.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarMedidasRecurso()
  {
    int codigoRh = getCodigoRecursoSelecionado();
    try
    {
      ActualizarMedidasRecursoHidricoSQL.carregarMedidasAplicadas(connection, modeloAplicadas, codigoRh);
      ActualizarMedidasRecursoHidricoSQL.carregarMedidasDisponiveis(connection, modeloDisponiveis, codigoRh);
      aplicarFiltroAplicadas();
      aplicarFiltroDisponiveis();
      actualizarResumoMedidas();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar medidas de protecção:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private boolean prepararRecursoSelecionado()
  {
    if(tabelaRecursos.getSelectedRow() == -1)
    {
      JOptionPane.showMessageDialog(this, "Seleccione um recurso hídrico.");
      return false;
    }

    int linhaModel = tabelaRecursos.convertRowIndexToModel(tabelaRecursos.getSelectedRow());
    fieldCodigoRh.setText(valorTabela(modeloRecursos, linhaModel, 0));
    fieldTipoRh.setText(valorTabela(modeloRecursos, linhaModel, 1) + " - "
        + valorTabela(modeloRecursos, linhaModel, 2));
    carregarMedidasRecurso();
    return true;
  }

  private int getCodigoRecursoSelecionado()
  {
    return Integer.parseInt(fieldCodigoRh.getText().trim());
  }

  private void actualizarResumoMedidas()
  {
    if(modeloAplicadas.getRowCount() == 0)
    {
      fieldResumoMedidas.setText("Nenhuma medida aplicada");
      return;
    }

    StringBuilder texto = new StringBuilder();
    for(int i = 0; i < modeloAplicadas.getRowCount(); i++)
    {
      if(i > 0)
        texto.append(", ");
      texto.append(modeloAplicadas.getValueAt(i, 0));
    }
    fieldResumoMedidas.setText(texto.toString());
  }

  private void adicionarMedidas()
  {
    LocalDate data = getDataImplementacao();
    if(data.isAfter(LocalDate.now()))
    {
      JOptionPane.showMessageDialog(this, "A data de implementação não pode ser futura.");
      return;
    }
    actualizarMedidas("ADICIONAR", tabelaDisponiveis, modeloDisponiveis, "adicionar", data);
  }

  private void removerMedidas()
  {
    actualizarMedidas("REMOVER", tabelaAplicadas, modeloAplicadas, "remover", null);
  }

  private void actualizarMedidas(String operacao, JTable tabela, DefaultTableModel modelo, String textoOperacao,
      LocalDate dataImplementacao)
  {
    List<Integer> codigos = getCodigosSelecionados(tabela, modelo);
    if(codigos.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Seleccione pelo menos uma medida para " + textoOperacao + ".");
      return;
    }

    int codigoRh = getCodigoRecursoSelecionado();

    try
    {
      ActualizarMedidasRecursoHidricoSQL.ResultadoPreValidacao preValidacao =
          ActualizarMedidasRecursoHidricoSQL.preValidarActualizacao(connection, codigoRh, codigos, operacao,
              dataImplementacao);

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Operação não permitida:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        return;
      }

      String dataTexto = dataImplementacao == null ? "" : "\nData de implementação: " + dataImplementacao;
      int resposta = JOptionPane.showConfirmDialog(this,
          "Recurso hídrico: " + codigoRh + "\nMedidas: " + codigos + dataTexto + "\n\nDeseja " + textoOperacao
              + " estas medidas?",
          "Confirmar Actualização", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      ActualizarMedidasRecursoHidricoSQL.ResultadoActualizacao resultado =
          ActualizarMedidasRecursoHidricoSQL.actualizarMedidas(connection, codigoRh, codigos, operacao,
              dataImplementacao);

      if(!resultado.actualizado)
      {
        JOptionPane.showMessageDialog(this, "Medidas não actualizadas:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          "Medidas actualizadas com sucesso.\nLinhas afectadas: " + resultado.afectadas);
      carregarMedidasRecurso();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível actualizar medidas:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private List<Integer> getCodigosSelecionados(JTable tabela, DefaultTableModel modelo)
  {
    List<Integer> codigos = new ArrayList<Integer>();
    int[] linhas = tabela.getSelectedRows();

    for(int linhaView : linhas)
    {
      int linhaModel = tabela.convertRowIndexToModel(linhaView);
      codigos.add(Integer.valueOf(modelo.getValueAt(linhaModel, 0).toString()));
    }
    return codigos;
  }

  private LocalDate getDataImplementacao()
  {
    int dia = ((Integer) spinnerDiaImpl.getValue()).intValue();
    int mes = ((Integer) spinnerMesImpl.getValue()).intValue();
    int ano = ((Integer) spinnerAnoImpl.getValue()).intValue();
    return LocalDate.of(ano, mes, dia);
  }

  private void ajustarDataImplementacao()
  {
    if(ajustandoData)
      return;

    ajustandoData = true;
    try
    {
      LocalDate hoje = LocalDate.now();
      int ano = ((Integer) spinnerAnoImpl.getValue()).intValue();
      int mes = ((Integer) spinnerMesImpl.getValue()).intValue();

      SpinnerNumberModel modeloMes = (SpinnerNumberModel) spinnerMesImpl.getModel();
      if(ano == hoje.getYear())
      {
        modeloMes.setMaximum(Integer.valueOf(hoje.getMonthValue()));
        if(mes > hoje.getMonthValue())
        {
          mes = hoje.getMonthValue();
          spinnerMesImpl.setValue(Integer.valueOf(mes));
        }
      }
      else
      {
        modeloMes.setMaximum(Integer.valueOf(12));
      }

      int maximoDia = YearMonth.of(ano, mes).lengthOfMonth();
      if(ano == hoje.getYear() && mes == hoje.getMonthValue())
        maximoDia = hoje.getDayOfMonth();

      SpinnerNumberModel modeloDia = (SpinnerNumberModel) spinnerDiaImpl.getModel();
      modeloDia.setMaximum(Integer.valueOf(maximoDia));
      int dia = ((Integer) spinnerDiaImpl.getValue()).intValue();
      if(dia > maximoDia)
        spinnerDiaImpl.setValue(Integer.valueOf(maximoDia));
    } finally
    {
      ajustandoData = false;
    }
  }

  private String valorTabela(DefaultTableModel modelo, int linha, int coluna)
  {
    Object valor = modelo.getValueAt(linha, coluna);
    return valor == null ? "" : valor.toString();
  }

  private void aplicarFiltroRecursos()
  {
    if(sorterRecursos == null || fieldPesquisaRecursos == null || comboPesquisaRecursos == null)
      return;

    String texto = fieldPesquisaRecursos.getText().trim();
    if(texto.isEmpty())
    {
      sorterRecursos.setRowFilter(null);
      return;
    }

    int coluna = 0;
    String criterio = comboPesquisaRecursos.getSelectedItem().toString();
    if("Tipo".equals(criterio))
      coluna = 1;
    else if("Localização".equals(criterio))
      coluna = 2;

    sorterRecursos.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private void aplicarFiltroAplicadas()
  {
    aplicarFiltroMedidas(sorterAplicadas, fieldPesquisaAplicadas);
  }

  private void aplicarFiltroDisponiveis()
  {
    aplicarFiltroMedidas(sorterDisponiveis, fieldPesquisaDisponiveis);
  }

  private void aplicarFiltroMedidas(TableRowSorter<DefaultTableModel> sorter, JTextField field)
  {
    if(sorter == null || field == null)
      return;

    String texto = field.getText().trim();
    if(texto.isEmpty())
      sorter.setRowFilter(null);
    else
      sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
  }

  private final class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      Object source = event.getSource();

      if(source == buttonActualizar)
      {
        carregarRecursos();
      }
      else if(source == comboPesquisaRecursos)
      {
        aplicarFiltroRecursos();
      }
      else if(source == buttonAvancar)
      {
        if(prepararRecursoSelecionado())
          cardLayout.show(panelCards, CARD_MEDIDAS);
      }
      else if(source == buttonVoltarDashboard)
      {
        voltarAoDashboard();
      }
      else if(source == buttonVoltar)
      {
        cardLayout.show(panelCards, CARD_RECURSOS);
      }
      else if(source == buttonActualizarMedidas)
      {
        carregarMedidasRecurso();
      }
      else if(source == buttonAdicionar)
      {
        adicionarMedidas();
      }
      else if(source == buttonRemover)
      {
        removerMedidas();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }

  private final class TratarPesquisaRecursos implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroRecursos();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroRecursos();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroRecursos();
    }
  }

  private final class TratarPesquisaAplicadas implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroAplicadas();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroAplicadas();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroAplicadas();
    }
  }

  private final class TratarPesquisaDisponiveis implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroDisponiveis();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroDisponiveis();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroDisponiveis();
    }
  }
}
