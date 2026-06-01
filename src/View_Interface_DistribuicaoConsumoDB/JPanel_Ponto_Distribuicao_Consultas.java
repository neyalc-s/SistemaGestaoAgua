package View_Interface_DistribuicaoConsumoDB;

import Resources.MensagensInterface;

import Repository_SQL.DistribuicaoConsumoDB.PontoDistribuicaoConsultasSQL;
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
import java.util.regex.Pattern;

public class JPanel_Ponto_Distribuicao_Consultas extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  // TAB 1
  private JTable tabelaFamiliasPorPonto;
  private DefaultTableModel modeloFamiliasPorPonto;
  private TableRowSorter<DefaultTableModel> sorterFamiliasPorPonto;
  private JTextField fieldFiltroFamiliasPorPonto;
  private JComboBox<String> comboFiltroFamiliasPorPonto;
  private JButton buttonActualizarFamiliasPorPonto;
  private JButton buttonLimparFamiliasPorPonto;
  private JButton buttonAjudaFamiliasPorPonto;

  // TAB 2
  private JTable tabelaPontoRegistros;
  private DefaultTableModel modeloPontoRegistros;
  private TableRowSorter<DefaultTableModel> sorterPontoRegistros;
  private JTextField fieldFiltroPontoRegistros;
  private JButton buttonActualizarPontoRegistros;
  private JButton buttonLimparPontoRegistros;
  private JButton buttonAjudaPontoRegistros;

  // TAB 3
  private JTable tabelaPontoHistorico;
  private DefaultTableModel modeloPontoHistorico;
  private TableRowSorter<DefaultTableModel> sorterPontoHistorico;
  private JTextField fieldFiltroPontoHistorico;
  private JButton buttonActualizarPontoHistorico;
  private JButton buttonLimparPontoHistorico;
  private JButton buttonAjudaPontoHistorico;

  public JPanel_Ponto_Distribuicao_Consultas(Connection connection)
  {
    this(connection, false);
  }

  public JPanel_Ponto_Distribuicao_Consultas(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this.connection = connection;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    JPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 20),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1280, 760));

    card.add(criarTopo(), BorderLayout.NORTH);
    card.add(criarCentro(), BorderLayout.CENTER);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(18, 18, 18, 18);

    add(card, gbc);

    if(carregarTabelasAutomaticamente)
    {
      carregarTabelaFamiliasPorPonto();
      carregarTabelaPontoRegistros();
      carregarTabelaPontoHistorico();
    }
  }

  // ========================= INICIO DO CODIGO DE INTERFACE GRAFICA
  // =========================

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Consultas de Ponto de Distribuição",
        "<html>As consultas foram agrupadas por famílias associadas, registos de consumo e histórico de manutenção. "
            + "Na primeira aba pode filtrar por <b>codigo_pd</b> ou <b>codigo_fb</b>; "
            + "nas restantes, a pesquisa é feita por <b>codigo_pd</b>.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JComponent criarCentro()
  {
    JTabbedPane tabs = new JTabbedPane();
    tabs.setFont(InterfaceGraficaUtils.FONT_ABA);

    tabs.addTab("Famílias por Ponto", criarTabFamiliasPorPonto());
    tabs.addTab("Registos de Consumo por Ponto", criarTabPontoRegistros());
    tabs.addTab("Histórico de Manutenção", criarTabPontoHistorico());

    return tabs;
  }

  private JPanel criarTabFamiliasPorPonto()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Todas as Famílias Associadas a Todos os Pontos",
        InterfaceGraficaUtils.FONT_LABEL, InterfaceGraficaUtils.COR_AZUL);

    fieldFiltroFamiliasPorPonto = InterfaceGraficaUtils.criarCampoTexto(18, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(220, 36));

    comboFiltroFamiliasPorPonto = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código do Ponto", "Código da Família"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(220, 36));

    fieldFiltroFamiliasPorPonto.getDocument().addDocumentListener(new TratarPesquisa(OrigemFiltro.FAMILIAS_POR_PONTO));
    comboFiltroFamiliasPorPonto.addActionListener(tratarButtons);

    buttonActualizarFamiliasPorPonto = criarBotao("Mostrar Todos");
    buttonActualizarFamiliasPorPonto.addActionListener(tratarButtons);

    buttonLimparFamiliasPorPonto = criarBotao("Limpar Filtro");
    buttonLimparFamiliasPorPonto.addActionListener(tratarButtons);

    buttonAjudaFamiliasPorPonto = criarBotaoAjuda();
    buttonAjudaFamiliasPorPonto.addActionListener(tratarButtons);

    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));

    JPanel linhaControles = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaControles.add(new JLabel("Filtrar por:"));
    linhaControles.add(comboFiltroFamiliasPorPonto);
    linhaControles.add(fieldFiltroFamiliasPorPonto);
    linhaControles.add(buttonActualizarFamiliasPorPonto);
    linhaControles.add(buttonLimparFamiliasPorPonto);
    linhaControles.add(buttonAjudaFamiliasPorPonto);

    topo.add(label, BorderLayout.NORTH);
    topo.add(linhaControles, BorderLayout.CENTER);

    tabelaFamiliasPorPonto = criarTabelaBase();
    modeloFamiliasPorPonto = new DefaultTableModel();
    tabelaFamiliasPorPonto.setModel(modeloFamiliasPorPonto);
    sorterFamiliasPorPonto = new TableRowSorter<>(modeloFamiliasPorPonto);
    tabelaFamiliasPorPonto.setRowSorter(sorterFamiliasPorPonto);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabelaFamiliasPorPonto), BorderLayout.CENTER);

    return panel;
  }

  private JPanel criarTabPontoRegistros()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Todos os Pontos com os Seus Registos de Consumo",
        InterfaceGraficaUtils.FONT_LABEL, InterfaceGraficaUtils.COR_AZUL);

    fieldFiltroPontoRegistros = InterfaceGraficaUtils.criarCampoTexto(18, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(260, 36));

    fieldFiltroPontoRegistros.getDocument().addDocumentListener(new TratarPesquisa(OrigemFiltro.PONTO_REGISTROS));

    buttonActualizarPontoRegistros = criarBotao("Mostrar Todos");
    buttonActualizarPontoRegistros.addActionListener(tratarButtons);

    buttonLimparPontoRegistros = criarBotao("Limpar Filtro");
    buttonLimparPontoRegistros.addActionListener(tratarButtons);

    buttonAjudaPontoRegistros = criarBotaoAjuda();
    buttonAjudaPontoRegistros.addActionListener(tratarButtons);

    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));

    JPanel linhaControles = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaControles.add(new JLabel("Pesquisar por codigo_pd:"));
    linhaControles.add(fieldFiltroPontoRegistros);
    linhaControles.add(buttonActualizarPontoRegistros);
    linhaControles.add(buttonLimparPontoRegistros);
    linhaControles.add(buttonAjudaPontoRegistros);

    topo.add(label, BorderLayout.NORTH);
    topo.add(linhaControles, BorderLayout.CENTER);

    tabelaPontoRegistros = criarTabelaBase();
    modeloPontoRegistros = new DefaultTableModel();
    tabelaPontoRegistros.setModel(modeloPontoRegistros);
    sorterPontoRegistros = new TableRowSorter<>(modeloPontoRegistros);
    tabelaPontoRegistros.setRowSorter(sorterPontoRegistros);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabelaPontoRegistros), BorderLayout.CENTER);

    return panel;
  }

  private JPanel criarTabPontoHistorico()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Histórico de Manutenção de Todos os Pontos",
        InterfaceGraficaUtils.FONT_LABEL, InterfaceGraficaUtils.COR_AZUL);

    fieldFiltroPontoHistorico = InterfaceGraficaUtils.criarCampoTexto(18, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(260, 36));

    fieldFiltroPontoHistorico.getDocument().addDocumentListener(new TratarPesquisa(OrigemFiltro.PONTO_HISTORICO));

    buttonActualizarPontoHistorico = criarBotao("Mostrar Todos");
    buttonActualizarPontoHistorico.addActionListener(tratarButtons);

    buttonLimparPontoHistorico = criarBotao("Limpar Filtro");
    buttonLimparPontoHistorico.addActionListener(tratarButtons);

    buttonAjudaPontoHistorico = criarBotaoAjuda();
    buttonAjudaPontoHistorico.addActionListener(tratarButtons);

    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));

    JPanel linhaControles = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaControles.add(new JLabel("Pesquisar por codigo_pd:"));
    linhaControles.add(fieldFiltroPontoHistorico);
    linhaControles.add(buttonActualizarPontoHistorico);
    linhaControles.add(buttonLimparPontoHistorico);
    linhaControles.add(buttonAjudaPontoHistorico);

    topo.add(label, BorderLayout.NORTH);
    topo.add(linhaControles, BorderLayout.CENTER);

    tabelaPontoHistorico = criarTabelaBase();
    modeloPontoHistorico = new DefaultTableModel();
    tabelaPontoHistorico.setModel(modeloPontoHistorico);
    sorterPontoHistorico = new TableRowSorter<>(modeloPontoHistorico);
    tabelaPontoHistorico.setRowSorter(sorterPontoHistorico);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabelaPontoHistorico), BorderLayout.CENTER);

    return panel;
  }

  private JTable criarTabelaBase()
  {
    return InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA, InterfaceGraficaUtils.FONT_HEADER,
        InterfaceGraficaUtils.COR_GRID_TABELA, InterfaceGraficaUtils.COR_SELECAO_TABELA,
        InterfaceGraficaUtils.COR_TEXTO);
  }

  private JScrollPane criarScrollTabela(JTable tabela)
  {
    return InterfaceGraficaUtils.criarScrollTabela(tabela, InterfaceGraficaUtils.COR_BORDA_TABELA);
  }

  private JButton criarBotao(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(150, 40));
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  private JButton criarBotaoAjuda()
  {
    JButton botao = InterfaceGraficaUtils.criarBotao("?", InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(44, 40));
    IconesInterface.aplicarIconeBotao(botao);
    botao.setToolTipText("Ajuda da pesquisa");
    return botao;
  }

  // ========================== FIM DO CODIGO DE INTERFACE GRAFICA
  // ==========================

  // ========================== INICIO DA LOGICA DA INTERFACE GRAFICA
  // ==========================

  private void carregarTabelaFamiliasPorPonto()
  {
    if(!ConsultaRemotaUtils.prepararConsulta(connection, new VerificadorConexaoRemota.NoRemoto[]
      {
          VerificadorConexaoRemota.NoRemoto.FAMILIAS_COTAS
      }, tabelaFamiliasPorPonto, new JComponent[]
        {
            comboFiltroFamiliasPorPonto, fieldFiltroFamiliasPorPonto, buttonActualizarFamiliasPorPonto,
            buttonLimparFamiliasPorPonto, buttonAjudaFamiliasPorPonto
        }))
      return;

    carregarTabela(tabelaFamiliasPorPonto, PontoDistribuicaoConsultasSQL.VISUALIZAR_FAMILIA_TODAS);
    aplicarFiltroFamiliasPorPonto();
  }

  private void carregarTabelaPontoRegistros()
  {
    carregarTabela(tabelaPontoRegistros, PontoDistribuicaoConsultasSQL.VISUALIZAR_PONTO_REGISTRO);
    aplicarFiltroPontoRegistros();
  }

  private void carregarTabelaPontoHistorico()
  {
    carregarTabela(tabelaPontoHistorico, PontoDistribuicaoConsultasSQL.VISUALIZAR_PONTO_HISTORICO);
    aplicarFiltroPontoHistorico();
  }

  private void carregarTabela(JTable tabela, String sql)
  {
    try
    {
      DefaultTableModel modelo = InterfaceGraficaUtils.carregarModeloTabela(connection, sql);
      tabela.setModel(modelo);
      InterfaceGraficaUtils.configurarTabela(tabela, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
          InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);

      if(tabela == tabelaFamiliasPorPonto)
      {
        modeloFamiliasPorPonto = modelo;
        sorterFamiliasPorPonto = new TableRowSorter<>(modeloFamiliasPorPonto);
        tabelaFamiliasPorPonto.setRowSorter(sorterFamiliasPorPonto);
      }
      else if(tabela == tabelaPontoRegistros)
      {
        modeloPontoRegistros = modelo;
        sorterPontoRegistros = new TableRowSorter<>(modeloPontoRegistros);
        tabelaPontoRegistros.setRowSorter(sorterPontoRegistros);
      }
      else if(tabela == tabelaPontoHistorico)
      {
        modeloPontoHistorico = modelo;
        sorterPontoHistorico = new TableRowSorter<>(modeloPontoHistorico);
        tabelaPontoHistorico.setRowSorter(sorterPontoHistorico);
      }

      SwingUtilities.invokeLater(() -> InterfaceGraficaUtils.ajustarLarguraColunas(tabela,
          InterfaceGraficaUtils.FONT_TABELA, InterfaceGraficaUtils.FONT_HEADER));
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os dados.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltroFamiliasPorPonto()
  {
    if(sorterFamiliasPorPonto == null || fieldFiltroFamiliasPorPonto == null || comboFiltroFamiliasPorPonto == null)
      return;

    String texto = fieldFiltroFamiliasPorPonto.getText().trim();

    if(texto.isEmpty())
    {
      sorterFamiliasPorPonto.setRowFilter(null);
      return;
    }

    int coluna = getIndiceColunaFamiliasPorPonto();
    sorterFamiliasPorPonto.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(texto) + "$", coluna));
  }

  private void aplicarFiltroPontoRegistros()
  {
    if(sorterPontoRegistros == null || fieldFiltroPontoRegistros == null)
      return;

    String texto = fieldFiltroPontoRegistros.getText().trim();

    if(texto.isEmpty())
    {
      sorterPontoRegistros.setRowFilter(null);
      return;
    }

    int coluna = InterfaceGraficaUtils.encontrarIndiceColuna(modeloPontoRegistros, "codigo_pd");
    sorterPontoRegistros.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(texto) + "$", coluna));
  }

  private void aplicarFiltroPontoHistorico()
  {
    if(sorterPontoHistorico == null || fieldFiltroPontoHistorico == null)
      return;

    String texto = fieldFiltroPontoHistorico.getText().trim();

    if(texto.isEmpty())
    {
      sorterPontoHistorico.setRowFilter(null);
      return;
    }

    int coluna = InterfaceGraficaUtils.encontrarIndiceColuna(modeloPontoHistorico, "codigo_pd");
    sorterPontoHistorico.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(texto) + "$", coluna));
  }

  private int getIndiceColunaFamiliasPorPonto()
  {
    String criterio = comboFiltroFamiliasPorPonto.getSelectedItem().toString();

    switch(criterio)
    {
      case "Código do Ponto":
        return InterfaceGraficaUtils.encontrarIndiceColuna(modeloFamiliasPorPonto, "codigo_pd");
      case "Código da Família":
        return InterfaceGraficaUtils.encontrarIndiceColuna(modeloFamiliasPorPonto, "codigo_fb");
      default:
        return 0;
    }
  }

  private void mostrarAjudaFamiliasPorPonto()
  {
    JOptionPane.showMessageDialog(this,
        "Famílias por Ponto\n\n" + "Use este separador para ver famílias associadas aos pontos de distribuição.\n\n"
            + "Filtro por Código do Ponto:\n"
            + "Mostra todas as famílias associadas ao ponto de distribuição informado.\n\n"
            + "Filtro por Código da Família:\n"
            + "Mostra a família informada e o ponto de distribuição ao qual ela está associada.\n\n" + "Sem filtro:\n"
            + "Mostra todas as famílias de todos os pontos de distribuição.",
        "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private void mostrarAjudaPontoRegistros()
  {
    JOptionPane.showMessageDialog(this,
        "Registos de Consumo por Ponto\n\n"
            + "Use este separador para ver retiradas/registos de consumo ligados aos pontos.\n\n"
            + "Pesquisa por codigo_pd:\n"
            + "Mostra todos os registos de consumo feitos num ponto de distribuição específico.\n\n" + "Sem filtro:\n"
            + "Mostra os registos de consumo de todos os pontos de distribuição.",
        "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private void mostrarAjudaPontoHistorico()
  {
    JOptionPane.showMessageDialog(this,
        "Histórico de Manutenção\n\n"
            + "Use este separador para ver o histórico de manutenção dos pontos de distribuição.\n\n"
            + "Pesquisa por codigo_pd:\n"
            + "Mostra todas as manutenções registadas para o ponto de distribuição informado.\n\n" + "Sem filtro:\n"
            + "Mostra o histórico de manutenção de todos os pontos.",
        "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private enum OrigemFiltro
  {
    FAMILIAS_POR_PONTO, PONTO_REGISTROS, PONTO_HISTORICO
  }

  private class TratarPesquisa implements DocumentListener
  {
    private final OrigemFiltro origem;

    public TratarPesquisa(OrigemFiltro origem)
    {
      this.origem = origem;
    }

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

    private void aplicarFiltro()
    {
      switch(origem)
      {
        case FAMILIAS_POR_PONTO:
          aplicarFiltroFamiliasPorPonto();
          break;
        case PONTO_REGISTROS:
          aplicarFiltroPontoRegistros();
          break;
        case PONTO_HISTORICO:
          aplicarFiltroPontoHistorico();
          break;
      }
    }
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      Object source = e.getSource();

      if(source == comboFiltroFamiliasPorPonto)
      {
        aplicarFiltroFamiliasPorPonto();
        return;
      }

      if(source == buttonAjudaFamiliasPorPonto)
      {
        mostrarAjudaFamiliasPorPonto();
        return;
      }

      if(source == buttonAjudaPontoRegistros)
      {
        mostrarAjudaPontoRegistros();
        return;
      }

      if(source == buttonAjudaPontoHistorico)
      {
        mostrarAjudaPontoHistorico();
        return;
      }

      if(source == buttonActualizarFamiliasPorPonto)
      {
        carregarTabelaFamiliasPorPonto();
        return;
      }

      if(source == buttonLimparFamiliasPorPonto)
      {
        fieldFiltroFamiliasPorPonto.setText("");
        comboFiltroFamiliasPorPonto.setSelectedIndex(0);
        aplicarFiltroFamiliasPorPonto();
        return;
      }

      if(source == buttonActualizarPontoRegistros)
      {
        carregarTabelaPontoRegistros();
        return;
      }

      if(source == buttonLimparPontoRegistros)
      {
        fieldFiltroPontoRegistros.setText("");
        aplicarFiltroPontoRegistros();
        return;
      }

      if(source == buttonActualizarPontoHistorico)
      {
        carregarTabelaPontoHistorico();
        return;
      }

      if(source == buttonLimparPontoHistorico)
      {
        fieldFiltroPontoHistorico.setText("");
        aplicarFiltroPontoHistorico();
      }
    }
  }

  // =========================== FIM DA LOGICA DA INTERFACE GRAFICA
  // ===========================
}
