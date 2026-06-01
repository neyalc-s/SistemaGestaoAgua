package View_Interface_DistribuicaoConsumoDB;

import Repository_SQL.DistribuicaoConsumoDB.CancelarAbastecimentoSQL;
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
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class JPanel_CancelarAbastecimento extends JPanel
{
  private static final String CARD_RECURSOS = "recursos";
  private static final String CARD_ABASTECIMENTOS = "abastecimentos";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final java.awt.CardLayout cardLayout = new java.awt.CardLayout();
  private final JPanel panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaRecursos;
  private JTable tabelaAbastecimentos;
  private DefaultTableModel modeloRecursos;
  private DefaultTableModel modeloAbastecimentos;
  private TableRowSorter<DefaultTableModel> sorterRecursos;
  private TableRowSorter<DefaultTableModel> sorterAbastecimentos;
  private JTextField fieldPesquisaRecursos;
  private JTextField fieldPesquisaAbastecimentos;
  private JComboBox<String> comboPesquisaRecursos;
  private JComboBox<String> comboPesquisaAbastecimentos;
  private JTextField fieldCodigoRh;
  private JTextField fieldResumoRecurso;
  private JButton buttonActualizarRecursos;
  private JButton buttonActualizarAbastecimentos;
  private JButton buttonAvancar;
  private JButton buttonVoltarDashboard;
  private JButton buttonVoltar;
  private JButton buttonCancelarAbastecimento;

  public JPanel_CancelarAbastecimento(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_CancelarAbastecimento(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_CancelarAbastecimento(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_CancelarAbastecimento(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));

    panelCards.add(criarCardRecursos(), CARD_RECURSOS);
    panelCards.add(criarCardAbastecimentos(), CARD_ABASTECIMENTOS);
    add(panelCards, BorderLayout.CENTER);

    if(carregarTabelasAutomaticamente)
    {
      carregarRecursos();
    }
  }

  private JPanel criarCardRecursos()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Cancelar Abastecimento",
        "<html>Seleccione o recurso hídrico que possui um abastecimento em curso.</html>");
    card.add(criarCentroRecursos(), BorderLayout.CENTER);
    card.add(criarRodapeRecursos(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardAbastecimentos()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Abastecimentos em Curso",
        "<html>Escolha o abastecimento a cancelar. O sistema irá guardar apenas o volume realmente entregue.</html>");
    card.add(criarCentroAbastecimentos(), BorderLayout.CENTER);
    card.add(criarRodapeAbastecimentos(), BorderLayout.SOUTH);
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
    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topo.add(InterfaceGraficaUtils.criarLabel("Recursos Hídricos", InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_AZUL), BorderLayout.NORTH);
    topo.add(criarPesquisaRecursos(), BorderLayout.SOUTH);
    painel.add(topo, BorderLayout.NORTH);

    modeloRecursos = criarModelo(new String[]
      {
          "Código RH", "Tipo", "Localização", "Vazão L/h", "Sazonalidade", "Nível Exploração"
      });
    tabelaRecursos = criarTabela(modeloRecursos);
    tabelaRecursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterRecursos = new TableRowSorter<DefaultTableModel>(modeloRecursos);
    tabelaRecursos.setRowSorter(sorterRecursos);
    painel.add(criarScroll(tabelaRecursos, new Dimension(1120, 500)), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarCentroAbastecimentos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    fieldCodigoRh = criarCampoReadonly();
    fieldResumoRecurso = criarCampoReadonly();

    JPanel resumo = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    adicionarLinhaResumo(resumo, 0, "Código do recurso:", fieldCodigoRh);
    adicionarLinhaResumo(resumo, 1, "Recurso seleccionado:", fieldResumoRecurso);
    painel.add(resumo, BorderLayout.NORTH);

    JPanel centro = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(InterfaceGraficaUtils.criarLabel("Abastecimentos em Curso",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL), BorderLayout.NORTH);
    topoTabela.add(criarPesquisaAbastecimentos(), BorderLayout.SOUTH);
    centro.add(topoTabela, BorderLayout.NORTH);

    modeloAbastecimentos = criarModelo(new String[]
      {
          "Código", "Ponto", "Localização Ponto", "Volume Previsto", "Data Início", "Data Fim", "Duração",
          "Estado"
      });
    tabelaAbastecimentos = criarTabela(modeloAbastecimentos);
    tabelaAbastecimentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterAbastecimentos = new TableRowSorter<DefaultTableModel>(modeloAbastecimentos);
    tabelaAbastecimentos.setRowSorter(sorterAbastecimentos);
    centro.add(criarScroll(tabelaAbastecimentos, new Dimension(1120, 380)), BorderLayout.CENTER);
    painel.add(centro, BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarPesquisaRecursos()
  {
    fieldPesquisaRecursos = criarCampoPesquisa(new TratarPesquisaRecursos());
    comboPesquisaRecursos = criarComboPesquisa(new String[]
      {
          "Código RH", "Tipo", "Localização", "Sazonalidade", "Nível Exploração"
      });
    buttonActualizarRecursos = criarBotao("Mostrar Todos", 150);
    return criarLinhaPesquisa(fieldPesquisaRecursos, comboPesquisaRecursos, buttonActualizarRecursos);
  }

  private JPanel criarPesquisaAbastecimentos()
  {
    fieldPesquisaAbastecimentos = criarCampoPesquisa(new TratarPesquisaAbastecimentos());
    comboPesquisaAbastecimentos = criarComboPesquisa(new String[]
      {
          "Código", "Ponto", "Localização Ponto"
      });
    buttonActualizarAbastecimentos = criarBotao("Mostrar Todos", 150);
    return criarLinhaPesquisa(fieldPesquisaAbastecimentos, comboPesquisaAbastecimentos, buttonActualizarAbastecimentos);
  }

  private JPanel criarLinhaPesquisa(JTextField field, JComboBox<String> combo, JButton botao)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(field);
    painel.add(combo);
    painel.add(botao);
    return painel;
  }

  private JPanel criarRodapeRecursos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    painel.add(InterfaceGraficaUtils.criarLabel("Seleccione um recurso para consultar abastecimentos em curso.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.WEST);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar", 120);
    buttonAvancar = criarBotao("Proceder", 145);
    botoes.add(buttonVoltarDashboard);
    botoes.add(buttonAvancar);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarRodapeAbastecimentos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    painel.add(InterfaceGraficaUtils.criarLabel("Ao cancelar, o volume entregue até agora será somado ao ponto.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.WEST);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltar = criarBotao("Voltar", 120);
    buttonCancelarAbastecimento = criarBotao("Cancelar Abast.", 165);
    botoes.add(buttonVoltar);
    botoes.add(buttonCancelarAbastecimento);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private DefaultTableModel criarModelo(String[] colunas)
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

  private JScrollPane criarScroll(JTable tabela, Dimension tamanho)
  {
    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabela, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(tamanho);
    return scroll;
  }

  private JTextField criarCampoPesquisa(DocumentListener listener)
  {
    JTextField field = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    field.getDocument().addDocumentListener(listener);
    return field;
  }

  private JComboBox<String> criarComboPesquisa(String[] opcoes)
  {
    JComboBox<String> combo = InterfaceGraficaUtils.criarCombo(opcoes, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(190, 36));
    combo.addActionListener(tratarButtons);
    return combo;
  }

  private JTextField criarCampoReadonly()
  {
    JTextField field = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(420, 36));
    field.setEditable(false);
    return field;
  }

  private JButton criarBotao(String texto, int largura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, 40));
    IconesInterface.aplicarIconeBotao(botao);
    botao.addActionListener(tratarButtons);
    return botao;
  }

  private void adicionarLinhaResumo(JPanel painel, int linha, String label, JTextField campo)
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
      CancelarAbastecimentoSQL.carregarRecursos(connection, modeloRecursos);
      aplicarFiltroRecursos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar recursos:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarAbastecimentos()
  {
    try
    {
      CancelarAbastecimentoSQL.carregarAbastecimentos(connection, modeloAbastecimentos, getCodigoRh());
      aplicarFiltroAbastecimentos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar abastecimentos:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private boolean prepararRecurso()
  {
    if(tabelaRecursos.getSelectedRow() == -1)
    {
      JOptionPane.showMessageDialog(this, "Seleccione um recurso hídrico.");
      return false;
    }

    int linha = tabelaRecursos.convertRowIndexToModel(tabelaRecursos.getSelectedRow());
    fieldCodigoRh.setText(valorTabela(modeloRecursos, linha, 0));
    fieldResumoRecurso.setText(valorTabela(modeloRecursos, linha, 1) + " - "
        + valorTabela(modeloRecursos, linha, 2));
    carregarAbastecimentos();
    return true;
  }

  private void cancelarAbastecimento()
  {
    if(tabelaAbastecimentos.getSelectedRow() == -1)
    {
      JOptionPane.showMessageDialog(this, "Seleccione um abastecimento em curso.");
      return;
    }

    int linha = tabelaAbastecimentos.convertRowIndexToModel(tabelaAbastecimentos.getSelectedRow());
    int codigoAbastecimento = Integer.parseInt(valorTabela(modeloAbastecimentos, linha, 0));
    String codigoPonto = valorTabela(modeloAbastecimentos, linha, 1);
    String volumePrevisto = valorTabela(modeloAbastecimentos, linha, 3);

    try
    {
      CancelarAbastecimentoSQL.ResultadoPreValidacao pre =
          CancelarAbastecimentoSQL.preValidarCancelamento(connection, codigoAbastecimento);
      if(!pre.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Cancelamento não permitido:\n" + MensagensInterface.formatarMensagem(pre.mensagem));
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Abastecimento: " + codigoAbastecimento + "\nPonto: " + codigoPonto + "\nVolume previsto: "
              + volumePrevisto + "\n\nAo confirmar, o sistema irá calcular o volume realmente entregue até agora, "
              + "somar esse volume ao ponto e marcar o abastecimento como Cancelado.\n\nDeseja continuar?",
          "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION);
      if(resposta != JOptionPane.YES_OPTION)
        return;

      CancelarAbastecimentoSQL.ResultadoCancelamento resultado =
          CancelarAbastecimentoSQL.cancelarAbastecimento(connection, codigoAbastecimento);
      if(!resultado.cancelado)
      {
        JOptionPane.showMessageDialog(this, "Abastecimento não cancelado:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          MensagensInterface.formatarMensagem(resultado.mensagem) + "\nVolume entregue: "
              + String.format(java.util.Locale.US, "%.2f", resultado.volumeEntregue));
      carregarAbastecimentos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível cancelar abastecimento:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private int getCodigoRh()
  {
    return Integer.parseInt(fieldCodigoRh.getText().trim());
  }

  private String valorTabela(DefaultTableModel modelo, int linha, int coluna)
  {
    Object valor = modelo.getValueAt(linha, coluna);
    return valor == null ? "" : valor.toString();
  }

  private void aplicarFiltroRecursos()
  {
    aplicarFiltro(sorterRecursos, fieldPesquisaRecursos, comboPesquisaRecursos);
  }

  private void aplicarFiltroAbastecimentos()
  {
    aplicarFiltro(sorterAbastecimentos, fieldPesquisaAbastecimentos, comboPesquisaAbastecimentos);
  }

  private void aplicarFiltro(TableRowSorter<DefaultTableModel> sorter, JTextField field, JComboBox<String> combo)
  {
    if(sorter == null || field == null || combo == null)
      return;

    String texto = field.getText().trim();
    if(texto.isEmpty())
    {
      sorter.setRowFilter(null);
      return;
    }

    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), combo.getSelectedIndex()));
  }

  private final class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      Object source = event.getSource();

      if(source == buttonActualizarRecursos)
        carregarRecursos();
      else if(source == buttonActualizarAbastecimentos)
        carregarAbastecimentos();
      else if(source == comboPesquisaRecursos)
        aplicarFiltroRecursos();
      else if(source == comboPesquisaAbastecimentos)
        aplicarFiltroAbastecimentos();
      else if(source == buttonAvancar)
      {
        if(prepararRecurso())
          cardLayout.show(panelCards, CARD_ABASTECIMENTOS);
      }
      else if(source == buttonVoltarDashboard)
        voltarAoDashboard();
      else if(source == buttonVoltar)
        cardLayout.show(panelCards, CARD_RECURSOS);
      else if(source == buttonCancelarAbastecimento)
        cancelarAbastecimento();
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }

  private final class TratarPesquisaRecursos implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) { aplicarFiltroRecursos(); }
    public void removeUpdate(DocumentEvent e) { aplicarFiltroRecursos(); }
    public void changedUpdate(DocumentEvent e) { aplicarFiltroRecursos(); }
  }

  private final class TratarPesquisaAbastecimentos implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) { aplicarFiltroAbastecimentos(); }
    public void removeUpdate(DocumentEvent e) { aplicarFiltroAbastecimentos(); }
    public void changedUpdate(DocumentEvent e) { aplicarFiltroAbastecimentos(); }
  }
}
