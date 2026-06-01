package View_Interface_DistribuicaoConsumoDB;

import Resources.MensagensInterface;

import Repository_SQL.DistribuicaoConsumoDB.AssociarPontosRecursoSQL;
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

public class JPanel_AssociarPontosRecursoHidrico extends JPanel
{
  private static final String CARD_SELECIONAR_PONTOS = "selecionar_pontos";
  private static final String CARD_SELECIONAR_RECURSO = "selecionar_recurso";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final TratarButtons tratarButtons = new TratarButtons();

  private CardLayout cardLayout;
  private JPanel panelCards;

  private JTable tabelaPontos;
  private DefaultTableModel modeloPontos;
  private TableRowSorter<DefaultTableModel> sorterPontos;
  private JTextField fieldPesquisaPontos;
  private JComboBox<String> comboCriterioPontos;
  private JButton buttonActualizarPontos;
  private JButton buttonAvancarParaRecursos;
  private JButton buttonVoltarDashboard;

  private JTable tabelaRecursos;
  private DefaultTableModel modeloRecursos;
  private TableRowSorter<DefaultTableModel> sorterRecursos;
  private JTextField fieldPesquisaRecursos;
  private JComboBox<String> comboCriterioRecursos;
  private JTextField fieldPontosSelecionados;
  private JButton buttonActualizarRecursos;
  private JButton buttonVoltarParaPontos;
  private JButton buttonAssociar;

  public JPanel_AssociarPontosRecursoHidrico(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_AssociarPontosRecursoHidrico(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_AssociarPontosRecursoHidrico(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_AssociarPontosRecursoHidrico(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
    panelCards.add(criarCardSelecionarPontos(), CARD_SELECIONAR_PONTOS);
    panelCards.add(criarCardSelecionarRecurso(), CARD_SELECIONAR_RECURSO);

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
      carregarRecursos();
    }
  }

  private JPanel criarCardSelecionarPontos()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
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

  private JPanel criarCardSelecionarRecurso()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(criarTopoRecursos(), BorderLayout.NORTH);
    card.add(criarCentroRecursos(), BorderLayout.CENTER);
    card.add(criarRodapeRecursos(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarTopoPontos()
  {
    return InterfaceGraficaUtils.criarTopo("Associar Pontos a Recurso Hídrico",
        "<html>Seleccione um ou vários pontos de distribuição que passarão a ser abastecidos pelo mesmo recurso hídrico.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarTopoRecursos()
  {
    return InterfaceGraficaUtils.criarTopo("Selecionar Recurso Hídrico",
        "<html>Escolha o recurso hídrico que ficará associado aos pontos de distribuição seleccionados.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarCentroPontos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Pontos de Distribuição",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(label, BorderLayout.NORTH);
    topoTabela.add(criarPainelPesquisaPontos(), BorderLayout.SOUTH);

    painel.add(topoTabela, BorderLayout.NORTH);
    painel.add(criarScrollPontos(), BorderLayout.CENTER);

    return painel;
  }

  private JPanel criarCentroRecursos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    fieldPontosSelecionados = new JTextField(20);
    fieldPontosSelecionados.setEditable(false);
    fieldPontosSelecionados.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    fieldPontosSelecionados.setPreferredSize(new Dimension(650, 36));
    fieldPontosSelecionados.setMinimumSize(new Dimension(650, 36));

    JPanel painelResumo = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painelResumo.add(InterfaceGraficaUtils.criarLabel("Pontos escolhidos:", InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_TEXTO));
    painelResumo.add(fieldPontosSelecionados);

    JLabel label = InterfaceGraficaUtils.criarLabel("Recursos Hídricos",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(painelResumo, BorderLayout.NORTH);
    topoTabela.add(label, BorderLayout.CENTER);
    topoTabela.add(criarPainelPesquisaRecursos(), BorderLayout.SOUTH);

    painel.add(topoTabela, BorderLayout.NORTH);
    painel.add(criarScrollRecursos(), BorderLayout.CENTER);

    return painel;
  }

  private JPanel criarRodapePontos()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("Use Ctrl ou Shift para selecionar vários pontos.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar");
    buttonAvancarParaRecursos = criarBotao("Proceder");
    buttonVoltarDashboard.addActionListener(tratarButtons);
    buttonAvancarParaRecursos.addActionListener(tratarButtons);
    painelBotoes.add(buttonVoltarDashboard);
    painelBotoes.add(buttonAvancarParaRecursos);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarRodapeRecursos()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("A associação será pré-validada antes da confirmação.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarParaPontos = criarBotao("Voltar");
    buttonAssociar = criarBotao("Associar");

    buttonVoltarParaPontos.addActionListener(tratarButtons);
    buttonAssociar.addActionListener(tratarButtons);

    painelBotoes.add(buttonVoltarParaPontos);
    painelBotoes.add(buttonAssociar);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarPainelPesquisaPontos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    fieldPesquisaPontos = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboCriterioPontos = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Ponto", "Localização Ponto", "Infraestrutura", "Estado", "Código Recurso Actual", "Tipo Recurso"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(230, 36));
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
    buttonActualizarRecursos.setPreferredSize(new Dimension(145, 36));

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

  private JScrollPane criarScrollPontos()
  {
    String[] colunas =
      {
          "Código Ponto", "Localização Ponto", "Infraestrutura", "Capacidade", "Volume Actual", "Estado",
          "Código Recurso Actual", "Tipo Recurso", "Localização Recurso"
      };

    modeloPontos = criarModeloNaoEditavel(colunas);
    tabelaPontos = criarTabela(modeloPontos);
    tabelaPontos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sorterPontos = new TableRowSorter<DefaultTableModel>(modeloPontos);
    tabelaPontos.setRowSorter(sorterPontos);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaPontos, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1180, 520));
    return scroll;
  }

  private JScrollPane criarScrollRecursos()
  {
    String[] colunas =
      {
          "Código Recurso", "Tipo", "Localização", "Vazão/Volume", "Sazonalidade", "Nível Exploração"
      };

    modeloRecursos = criarModeloNaoEditavel(colunas);
    tabelaRecursos = criarTabela(modeloRecursos);
    tabelaRecursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterRecursos = new TableRowSorter<DefaultTableModel>(modeloRecursos);
    tabelaRecursos.setRowSorter(sorterRecursos);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaRecursos, InterfaceGraficaUtils.COR_BORDA_TABELA);
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
    return botao;
  }

  private void aplicarFiltroPontos()
  {
    aplicarFiltro(sorterPontos, fieldPesquisaPontos, getIndiceColunaPontos());
  }

  private void aplicarFiltroRecursos()
  {
    aplicarFiltro(sorterRecursos, fieldPesquisaRecursos, getIndiceColunaRecursos());
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

  private int getIndiceColunaPontos()
  {
    String criterio = comboCriterioPontos.getSelectedItem().toString();

    switch(criterio)
    {
      case "Código Ponto":
        return 0;
      case "Localização Ponto":
        return 1;
      case "Infraestrutura":
        return 2;
      case "Estado":
        return 5;
      case "Código Recurso Actual":
        return 6;
      case "Tipo Recurso":
        return 7;
      default:
        return 0;
    }
  }

  private int getIndiceColunaRecursos()
  {
    String criterio = comboCriterioRecursos.getSelectedItem().toString();

    switch(criterio)
    {
      case "Código Recurso":
        return 0;
      case "Tipo":
        return 1;
      case "Localização":
        return 2;
      case "Sazonalidade":
        return 4;
      case "Nível Exploração":
        return 5;
      default:
        return 0;
    }
  }

  private boolean validarPontosSelecionados()
  {
    if(tabelaPontos.getSelectedRows().length == 0)
    {
      JOptionPane.showMessageDialog(this, "Seleccione pelo menos um ponto de distribuição.");
      return false;
    }

    return true;
  }

  private boolean validarRecursoSelecionado()
  {
    if(tabelaRecursos.getSelectedRow() == -1)
    {
      JOptionPane.showMessageDialog(this, "Seleccione um recurso hídrico.");
      return false;
    }

    return true;
  }

  private void carregarPontos()
  {
    try
    {
      AssociarPontosRecursoSQL.carregarPontos(connection, modeloPontos);
      aplicarFiltroPontos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os pontos de distribuição.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarRecursos()
  {
    try
    {
      AssociarPontosRecursoSQL.carregarRecursos(connection, modeloRecursos);
      aplicarFiltroRecursos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os recursos hídricos.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void prepararCardRecursos()
  {
    fieldPontosSelecionados.setText(formatarPontosSelecionados());
  }

  private String formatarPontosSelecionados()
  {
    int[] linhasSelecionadas = tabelaPontos.getSelectedRows();
    List<String> codigos = new ArrayList<String>();

    for(int linhaView : linhasSelecionadas)
    {
      int linhaModel = tabelaPontos.convertRowIndexToModel(linhaView);
      Object valor = modeloPontos.getValueAt(linhaModel, 0);

      if(valor != null)
        codigos.add(valor.toString());
    }

    return codigos.isEmpty() ? "Nenhum ponto seleccionado" : String.join(", ", codigos);
  }

  private List<Integer> getCodigosPontosSelecionados()
  {
    int[] linhasSelecionadas = tabelaPontos.getSelectedRows();
    List<Integer> codigos = new ArrayList<Integer>();

    for(int linhaView : linhasSelecionadas)
    {
      int linhaModel = tabelaPontos.convertRowIndexToModel(linhaView);
      Object valor = modeloPontos.getValueAt(linhaModel, 0);

      if(valor != null)
        codigos.add(Integer.valueOf(valor.toString()));
    }

    return codigos;
  }

  private Integer getCodigoRecursoSelecionado()
  {
    int linhaView = tabelaRecursos.getSelectedRow();

    if(linhaView == -1)
      return null;

    int linhaModel = tabelaRecursos.convertRowIndexToModel(linhaView);
    Object valor = modeloRecursos.getValueAt(linhaModel, 0);

    if(valor == null)
      return null;

    return Integer.valueOf(valor.toString());
  }

  private void associarPontosAoRecurso()
  {
    if(!validarRecursoSelecionado())
      return;

    List<Integer> codigosPontos = getCodigosPontosSelecionados();
    Integer codigoRh = getCodigoRecursoSelecionado();

    if(codigosPontos.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Nenhum ponto de distribuição foi seleccionado.");
      cardLayout.show(panelCards, CARD_SELECIONAR_PONTOS);
      return;
    }

    try
    {
      AssociarPontosRecursoSQL.ResultadoPreValidacao preValidacao =
          AssociarPontosRecursoSQL.preValidarAssociacao(connection, codigosPontos, codigoRh.intValue());

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Associação bloqueada:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        carregarPontos();
        carregarRecursos();
        cardLayout.show(panelCards, CARD_SELECIONAR_PONTOS);
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Pontos seleccionados: " + preValidacao.totalPontos + "\nRecurso hídrico destino: " + codigoRh
              + "\n\nEsta operação irá alterar o recurso hídrico associado aos pontos seleccionados."
              + "\nConfirmar associação?",
          "Confirmar Associação", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      AssociarPontosRecursoSQL.ResultadoAssociacao resultado =
          AssociarPontosRecursoSQL.associarPontos(connection, codigosPontos, codigoRh.intValue());

      if(!resultado.associou)
      {
        JOptionPane.showMessageDialog(this, "Associação não concluída:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          resultado.totalAssociados + " ponto(s) associado(s) ao recurso hídrico " + codigoRh + ".");

      carregarPontos();
      carregarRecursos();
      tabelaPontos.clearSelection();
      tabelaRecursos.clearSelection();
      fieldPontosSelecionados.setText("");
      cardLayout.show(panelCards, CARD_SELECIONAR_PONTOS);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível associar pontos ao recurso hídrico:\n" + MensagensInterface.formatarErro(ex));
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

  private class TratarPesquisaRecursos implements DocumentListener
  {
    @Override
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroRecursos();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroRecursos();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroRecursos();
    }
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      if(e.getSource() == comboCriterioPontos)
      {
        aplicarFiltroPontos();
        return;
      }

      if(e.getSource() == comboCriterioRecursos)
      {
        aplicarFiltroRecursos();
        return;
      }

      if(e.getSource() == buttonActualizarPontos)
      {
        carregarPontos();
        return;
      }

      if(e.getSource() == buttonActualizarRecursos)
      {
        carregarRecursos();
        return;
      }

      if(e.getSource() == buttonAvancarParaRecursos)
      {
        if(!validarPontosSelecionados())
          return;

        prepararCardRecursos();
        cardLayout.show(panelCards, CARD_SELECIONAR_RECURSO);
        return;
      }

      if(e.getSource() == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }

      if(e.getSource() == buttonVoltarParaPontos)
      {
        cardLayout.show(panelCards, CARD_SELECIONAR_PONTOS);
        return;
      }

      if(e.getSource() == buttonAssociar)
      {
        associarPontosAoRecurso();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }
}
