package View_Interface_DistribuicaoConsumoDB;

import Resources.MensagensInterface;

import Repository_SQL.DistribuicaoConsumoDB.AssociarPontosComiteSQL;
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

public class JPanel_AssociarPontosComite extends JPanel
{
  private static final String CARD_COMITE = "comite";
  private static final String CARD_PONTOS = "pontos";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final TratarButtons tratarButtons = new TratarButtons();

  private CardLayout cardLayout;
  private JPanel panelCards;

  private JTable tabelaComites;
  private DefaultTableModel modeloComites;
  private TableRowSorter<DefaultTableModel> sorterComites;
  private JTextField fieldPesquisaComites;
  private JComboBox<String> comboCriterioComites;
  private JButton buttonActualizarComites;
  private JButton buttonAvancarParaPontos;
  private JButton buttonVoltarDashboard;

  private JTable tabelaPontos;
  private DefaultTableModel modeloPontos;
  private TableRowSorter<DefaultTableModel> sorterPontos;
  private JTextField fieldPesquisaPontos;
  private JComboBox<String> comboCriterioPontos;
  private JTextField fieldComiteSelecionado;
  private JButton buttonActualizarPontos;
  private JButton buttonVoltarParaComites;
  private JButton buttonAssociar;

  public JPanel_AssociarPontosComite(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_AssociarPontosComite(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_AssociarPontosComite(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_AssociarPontosComite(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
    panelCards.add(criarCardComite(), CARD_COMITE);
    panelCards.add(criarCardPontos(), CARD_PONTOS);

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
      carregarComites();
      carregarPontos();
    }
  }

  private JPanel criarCardComite()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(criarTopoComites(), BorderLayout.NORTH);
    card.add(criarCentroComites(), BorderLayout.CENTER);
    card.add(criarRodapeComites(), BorderLayout.SOUTH);

    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardPontos()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(criarTopoPontos(), BorderLayout.NORTH);
    card.add(criarCentroPontos(), BorderLayout.CENTER);
    card.add(criarRodapePontos(), BorderLayout.SOUTH);

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

  private JPanel criarTopoComites()
  {
    return InterfaceGraficaUtils.criarTopo("Associar Pontos a Comité",
        "<html>Seleccione o comité que ficará responsável por um ou vários pontos de distribuição.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarTopoPontos()
  {
    return InterfaceGraficaUtils.criarTopo("Selecionar Pontos de Distribuição",
        "<html>Escolha os pontos de distribuição que passarão a ser geridos pelo comité seleccionado.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarCentroComites()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Comités", InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(label, BorderLayout.NORTH);
    topoTabela.add(criarPainelPesquisaComites(), BorderLayout.SOUTH);

    painel.add(topoTabela, BorderLayout.NORTH);
    painel.add(criarScrollComites(), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarCentroPontos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    fieldComiteSelecionado = new JTextField(20);
    fieldComiteSelecionado.setEditable(false);
    fieldComiteSelecionado.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    fieldComiteSelecionado.setPreferredSize(new Dimension(650, 36));

    JPanel painelResumo = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painelResumo.add(InterfaceGraficaUtils.criarLabel("Comité escolhido:", InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_TEXTO));
    painelResumo.add(fieldComiteSelecionado);

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

  private JPanel criarRodapeComites()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Seleccione apenas um comité.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar");
    buttonAvancarParaPontos = criarBotao("Proceder");
    buttonVoltarDashboard.addActionListener(tratarButtons);
    buttonAvancarParaPontos.addActionListener(tratarButtons);
    botoes.add(buttonVoltarDashboard);
    botoes.add(buttonAvancarParaPontos);

    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarRodapePontos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Use Ctrl ou Shift para selecionar vários pontos.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarParaComites = criarBotao("Voltar");
    buttonAssociar = criarBotao("Associar");
    buttonVoltarParaComites.addActionListener(tratarButtons);
    buttonAssociar.addActionListener(tratarButtons);
    botoes.add(buttonVoltarParaComites);
    botoes.add(buttonAssociar);

    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarPainelPesquisaComites()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    fieldPesquisaComites = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboCriterioComites = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Comité", "Nome"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(170, 36));
    buttonActualizarComites = criarBotao("Mostrar Todos");
    buttonActualizarComites.setPreferredSize(new Dimension(145, 36));

    fieldPesquisaComites.getDocument().addDocumentListener(new TratarPesquisaComites());
    comboCriterioComites.addActionListener(tratarButtons);
    buttonActualizarComites.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisaComites);
    painel.add(comboCriterioComites);
    painel.add(buttonActualizarComites);
    return painel;
  }

  private JPanel criarPainelPesquisaPontos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    fieldPesquisaPontos = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboCriterioPontos = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Ponto", "Localização", "Infraestrutura", "Estado", "Código Comité Actual", "Nome Comité"
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

  private JScrollPane criarScrollComites()
  {
    modeloComites = criarModeloNaoEditavel(new String[]
      {
          "Código Comité", "Nome", "Data Criação"
      });
    tabelaComites = criarTabela(modeloComites);
    tabelaComites.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterComites = new TableRowSorter<DefaultTableModel>(modeloComites);
    tabelaComites.setRowSorter(sorterComites);
    return InterfaceGraficaUtils.criarScrollTabela(tabelaComites, InterfaceGraficaUtils.COR_BORDA_TABELA);
  }

  private JScrollPane criarScrollPontos()
  {
    modeloPontos = criarModeloNaoEditavel(new String[]
      {
          "Código Ponto", "Localização", "Infraestrutura", "Capacidade", "Volume Actual", "Estado",
          "Código Comité Actual", "Nome Comité"
      });
    tabelaPontos = criarTabela(modeloPontos);
    tabelaPontos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sorterPontos = new TableRowSorter<DefaultTableModel>(modeloPontos);
    tabelaPontos.setRowSorter(sorterPontos);
    return InterfaceGraficaUtils.criarScrollTabela(tabelaPontos, InterfaceGraficaUtils.COR_BORDA_TABELA);
  }

  private DefaultTableModel criarModeloNaoEditavel(String[] colunas)
  {
    return new DefaultTableModel(colunas, 0)
      {
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

  private void carregarComites()
  {
    try
    {
      AssociarPontosComiteSQL.carregarComites(connection, modeloComites);
      aplicarFiltroComites();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os comités.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarPontos()
  {
    try
    {
      AssociarPontosComiteSQL.carregarPontos(connection, modeloPontos);
      aplicarFiltroPontos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os pontos de distribuição.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltroComites()
  {
    aplicarFiltro(sorterComites, fieldPesquisaComites, getIndiceComite());
  }

  private void aplicarFiltroPontos()
  {
    aplicarFiltro(sorterPontos, fieldPesquisaPontos, getIndicePonto());
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

  private int getIndiceComite()
  {
    return "Nome".equals(comboCriterioComites.getSelectedItem().toString()) ? 1 : 0;
  }

  private int getIndicePonto()
  {
    String criterio = comboCriterioPontos.getSelectedItem().toString();
    if("Localização".equals(criterio))
      return 1;
    if("Infraestrutura".equals(criterio))
      return 2;
    if("Estado".equals(criterio))
      return 5;
    if("Código Comité Actual".equals(criterio))
      return 6;
    if("Nome Comité".equals(criterio))
      return 7;
    return 0;
  }

  private Integer getCodigoComiteSelecionado()
  {
    int linhaView = tabelaComites.getSelectedRow();
    if(linhaView == -1)
      return null;
    int linhaModel = tabelaComites.convertRowIndexToModel(linhaView);
    return Integer.valueOf(modeloComites.getValueAt(linhaModel, 0).toString());
  }

  private String getResumoComiteSelecionado()
  {
    int linhaView = tabelaComites.getSelectedRow();
    if(linhaView == -1)
      return "";
    int linhaModel = tabelaComites.convertRowIndexToModel(linhaView);
    return modeloComites.getValueAt(linhaModel, 0) + " - " + modeloComites.getValueAt(linhaModel, 1);
  }

  private List<Integer> getCodigosPontosSelecionados()
  {
    int[] linhas = tabelaPontos.getSelectedRows();
    List<Integer> codigos = new ArrayList<Integer>();
    for(int linhaView : linhas)
    {
      int linhaModel = tabelaPontos.convertRowIndexToModel(linhaView);
      codigos.add(Integer.valueOf(modeloPontos.getValueAt(linhaModel, 0).toString()));
    }
    return codigos;
  }

  private void associarPontosAoComite()
  {
    Integer codigoComite = getCodigoComiteSelecionado();
    if(codigoComite == null)
    {
      JOptionPane.showMessageDialog(this, "Seleccione um comité.");
      cardLayout.show(panelCards, CARD_COMITE);
      return;
    }

    List<Integer> codigosPontos = getCodigosPontosSelecionados();
    if(codigosPontos.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Seleccione pelo menos um ponto de distribuição.");
      return;
    }

    try
    {
      AssociarPontosComiteSQL.ResultadoPreValidacao preValidacao =
          AssociarPontosComiteSQL.preValidarAssociacao(connection, codigosPontos, codigoComite.intValue());

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Associação bloqueada:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        carregarPontos();
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Comité destino: " + codigoComite + "\nPontos seleccionados: " + preValidacao.totalPontos
              + "\n\nEsta operação irá alterar o comité responsável pelos pontos seleccionados."
              + "\nConfirmar associação?",
          "Confirmar Associação", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      AssociarPontosComiteSQL.ResultadoAssociacao resultado =
          AssociarPontosComiteSQL.associarPontos(connection, codigosPontos, codigoComite.intValue());

      if(!resultado.associou)
      {
        JOptionPane.showMessageDialog(this, "Associação não concluída:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          resultado.totalAssociados + " ponto(s) associado(s) ao comité " + codigoComite + ".");

      carregarComites();
      carregarPontos();
      tabelaComites.clearSelection();
      tabelaPontos.clearSelection();
      fieldComiteSelecionado.setText("");
      cardLayout.show(panelCards, CARD_COMITE);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível associar pontos ao comité:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private class TratarPesquisaComites implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) { aplicarFiltroComites(); }
    public void removeUpdate(DocumentEvent e) { aplicarFiltroComites(); }
    public void changedUpdate(DocumentEvent e) { aplicarFiltroComites(); }
  }

  private class TratarPesquisaPontos implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) { aplicarFiltroPontos(); }
    public void removeUpdate(DocumentEvent e) { aplicarFiltroPontos(); }
    public void changedUpdate(DocumentEvent e) { aplicarFiltroPontos(); }
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      if(e.getSource() == comboCriterioComites)
      {
        aplicarFiltroComites();
        return;
      }

      if(e.getSource() == comboCriterioPontos)
      {
        aplicarFiltroPontos();
        return;
      }

      if(e.getSource() == buttonActualizarComites)
      {
        carregarComites();
        return;
      }

      if(e.getSource() == buttonActualizarPontos)
      {
        carregarPontos();
        return;
      }

      if(e.getSource() == buttonAvancarParaPontos)
      {
        if(getCodigoComiteSelecionado() == null)
        {
          JOptionPane.showMessageDialog(JPanel_AssociarPontosComite.this, "Seleccione um comité.");
          return;
        }
        fieldComiteSelecionado.setText(getResumoComiteSelecionado());
        cardLayout.show(panelCards, CARD_PONTOS);
        return;
      }

      if(e.getSource() == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }

      if(e.getSource() == buttonVoltarParaComites)
      {
        cardLayout.show(panelCards, CARD_COMITE);
        return;
      }

      if(e.getSource() == buttonAssociar)
        associarPontosAoComite();
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }
}
