package View_Interface_TransferenciasRecursosDB;

import Resources.MensagensInterface;

import Repository_SQL.TransferenciasRecursosDB.RecursoHidricoConsultasSQL;
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

public class JPanel_Recurso_Hidrico_Consultas extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  // TAB 1 - Recursos + medidas + responsáveis
  private JTable tabelaRecursosMedidas;
  private DefaultTableModel modeloRecursosMedidas;
  private TableRowSorter<DefaultTableModel> sorterRecursosMedidas;
  private JTextField fieldFiltroRecursosMedidas;
  private JComboBox<String> comboFiltroRecursosMedidas;
  private JButton buttonActualizarRecursosMedidas;
  private JButton buttonLimparRecursosMedidas;
  private JButton buttonAjudaRecursosMedidas;

  // TAB 2 - Recursos + pontos de distribuição
  private JTable tabelaRecursosPontos;
  private DefaultTableModel modeloRecursosPontos;
  private TableRowSorter<DefaultTableModel> sorterRecursosPontos;
  private JTextField fieldFiltroRecursosPontos;
  private JComboBox<String> comboFiltroRecursosPontos;
  private JButton buttonActualizarRecursosPontos;
  private JButton buttonLimparRecursosPontos;
  private JButton buttonAjudaRecursosPontos;

  // TAB 3 - Qualidade da água
  private JTable tabelaQualidadeAgua;
  private DefaultTableModel modeloQualidadeAgua;
  private TableRowSorter<DefaultTableModel> sorterQualidadeAgua;
  private JTextField fieldFiltroQualidadeAgua;
  private JComboBox<String> comboFiltroQualidadeAgua;
  private JButton buttonActualizarQualidadeAgua;
  private JButton buttonLimparQualidadeAgua;
  private JButton buttonAjudaQualidadeAgua;

  // TAB 4 - Historico de abastecimento
  private JTable tabelaHistoricoAbastecimento;
  private DefaultTableModel modeloHistoricoAbastecimento;
  private TableRowSorter<DefaultTableModel> sorterHistoricoAbastecimento;
  private JTextField fieldFiltroHistoricoAbastecimento;
  private JComboBox<String> comboFiltroHistoricoAbastecimento;
  private JButton buttonActualizarHistoricoAbastecimento;
  private JButton buttonLimparHistoricoAbastecimento;
  private JButton buttonAjudaHistoricoAbastecimento;

  public JPanel_Recurso_Hidrico_Consultas(Connection connection)
  {
    this(connection, false);
  }

  public JPanel_Recurso_Hidrico_Consultas(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this.connection = connection;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    JPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 20),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

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
      carregarTabelaRecursosMedidas();
      carregarTabelaRecursosPontos();
      carregarTabelaQualidadeAgua();
      carregarTabelaHistoricoAbastecimento();
    }
  }

  // ========================= INICIO DO CODIGO DE INTERFACE GRAFICA
  // =========================

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Pesquisas de Recurso Hídrico",
        "<html>As pesquisas foram agrupadas por medidas de protecção, pontos de distribuição, qualidade da água "
            + "e histórico de abastecimento.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JComponent criarCentro()
  {
    JTabbedPane tabs = new JTabbedPane();
    tabs.setFont(InterfaceGraficaUtils.FONT_ABA);

    tabs.addTab("Recursos, Medidas e Responsáveis", criarTabRecursosMedidas());
    tabs.addTab("Recursos e Pontos de Distribuição", criarTabRecursosPontos());
    tabs.addTab("Qualidade da Água", criarTabQualidadeAgua());
    tabs.addTab("Histórico de Abastecimento", criarTabHistoricoAbastecimento());

    return tabs;
  }

  private JPanel criarTabRecursosMedidas()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    JLabel label = InterfaceGraficaUtils.criarLabel(
        "Todos os Recursos Hídricos com suas Medidas de Protecção e Responsáveis", InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_AZUL);

    fieldFiltroRecursosMedidas = criarCampoFiltro(OrigemFiltro.RECURSOS_MEDIDAS);
    comboFiltroRecursosMedidas = criarComboFiltro(false);
    buttonActualizarRecursosMedidas = criarBotao("Mostrar Todos");
    buttonActualizarRecursosMedidas.addActionListener(tratarButtons);
    buttonLimparRecursosMedidas = criarBotao("Limpar Filtro");
    buttonLimparRecursosMedidas.addActionListener(tratarButtons);
    buttonAjudaRecursosMedidas = criarBotaoAjuda();
    buttonAjudaRecursosMedidas.addActionListener(tratarButtons);

    JPanel topo = criarTopoTabela(label, fieldFiltroRecursosMedidas, comboFiltroRecursosMedidas, buttonActualizarRecursosMedidas,
        buttonLimparRecursosMedidas, buttonAjudaRecursosMedidas);

    tabelaRecursosMedidas = criarTabelaBase();
    modeloRecursosMedidas = new DefaultTableModel();
    tabelaRecursosMedidas.setModel(modeloRecursosMedidas);
    sorterRecursosMedidas = new TableRowSorter<>(modeloRecursosMedidas);
    tabelaRecursosMedidas.setRowSorter(sorterRecursosMedidas);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabelaRecursosMedidas), BorderLayout.CENTER);

    return panel;
  }

  private JPanel criarTabRecursosPontos()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    JLabel label = InterfaceGraficaUtils.criarLabel(
        "Todos os Recursos Hídricos com os Pontos de Distribuição que Abastecem", InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_AZUL);

    fieldFiltroRecursosPontos = criarCampoFiltro(OrigemFiltro.RECURSOS_PONTOS);
    comboFiltroRecursosPontos = criarComboFiltro(false);
    buttonActualizarRecursosPontos = criarBotao("Mostrar Todos");
    buttonActualizarRecursosPontos.addActionListener(tratarButtons);
    buttonLimparRecursosPontos = criarBotao("Limpar Filtro");
    buttonLimparRecursosPontos.addActionListener(tratarButtons);
    buttonAjudaRecursosPontos = criarBotaoAjuda();
    buttonAjudaRecursosPontos.addActionListener(tratarButtons);

    JPanel topo = criarTopoTabela(label, fieldFiltroRecursosPontos, comboFiltroRecursosPontos, buttonActualizarRecursosPontos,
        buttonLimparRecursosPontos, buttonAjudaRecursosPontos);

    tabelaRecursosPontos = criarTabelaBase();
    modeloRecursosPontos = new DefaultTableModel();
    tabelaRecursosPontos.setModel(modeloRecursosPontos);
    sorterRecursosPontos = new TableRowSorter<>(modeloRecursosPontos);
    tabelaRecursosPontos.setRowSorter(sorterRecursosPontos);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabelaRecursosPontos), BorderLayout.CENTER);

    return panel;
  }

  private JPanel criarTabQualidadeAgua()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    JLabel label = InterfaceGraficaUtils.criarLabel(
        "Todos os Testes de Qualidade da Água de Todos os Recursos Hídricos", InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_AZUL);

    fieldFiltroQualidadeAgua = criarCampoFiltro(OrigemFiltro.QUALIDADE_AGUA);
    comboFiltroQualidadeAgua = criarComboFiltro(false);
    buttonActualizarQualidadeAgua = criarBotao("Mostrar Todos");
    buttonActualizarQualidadeAgua.addActionListener(tratarButtons);
    buttonLimparQualidadeAgua = criarBotao("Limpar Filtro");
    buttonLimparQualidadeAgua.addActionListener(tratarButtons);
    buttonAjudaQualidadeAgua = criarBotaoAjuda();
    buttonAjudaQualidadeAgua.addActionListener(tratarButtons);

    JPanel topo = criarTopoTabela(label, fieldFiltroQualidadeAgua, comboFiltroQualidadeAgua, buttonActualizarQualidadeAgua,
        buttonLimparQualidadeAgua, buttonAjudaQualidadeAgua);

    tabelaQualidadeAgua = criarTabelaBase();
    modeloQualidadeAgua = new DefaultTableModel();
    tabelaQualidadeAgua.setModel(modeloQualidadeAgua);
    sorterQualidadeAgua = new TableRowSorter<>(modeloQualidadeAgua);
    tabelaQualidadeAgua.setRowSorter(sorterQualidadeAgua);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabelaQualidadeAgua), BorderLayout.CENTER);

    return panel;
  }

  private JPanel criarTabHistoricoAbastecimento()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    JLabel label = InterfaceGraficaUtils.criarLabel(
        "Todos os Históricos de Abastecimento dos Recursos Hídricos", InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_AZUL);

    fieldFiltroHistoricoAbastecimento = criarCampoFiltro(OrigemFiltro.HISTORICO_ABASTECIMENTO);
    comboFiltroHistoricoAbastecimento = criarComboFiltro(true);
    buttonActualizarHistoricoAbastecimento = criarBotao("Mostrar Todos");
    buttonActualizarHistoricoAbastecimento.addActionListener(tratarButtons);
    buttonLimparHistoricoAbastecimento = criarBotao("Limpar Filtro");
    buttonLimparHistoricoAbastecimento.addActionListener(tratarButtons);
    buttonAjudaHistoricoAbastecimento = criarBotaoAjuda();
    buttonAjudaHistoricoAbastecimento.addActionListener(tratarButtons);

    JPanel topo = criarTopoTabela(label, fieldFiltroHistoricoAbastecimento, comboFiltroHistoricoAbastecimento,
        buttonActualizarHistoricoAbastecimento, buttonLimparHistoricoAbastecimento,
        buttonAjudaHistoricoAbastecimento);

    tabelaHistoricoAbastecimento = criarTabelaBase();
    modeloHistoricoAbastecimento = new DefaultTableModel();
    tabelaHistoricoAbastecimento.setModel(modeloHistoricoAbastecimento);
    sorterHistoricoAbastecimento = new TableRowSorter<>(modeloHistoricoAbastecimento);
    tabelaHistoricoAbastecimento.setRowSorter(sorterHistoricoAbastecimento);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabelaHistoricoAbastecimento), BorderLayout.CENTER);

    return panel;
  }

  private JTextField criarCampoFiltro(OrigemFiltro origemFiltro)
  {
    JTextField fieldFiltro = InterfaceGraficaUtils.criarCampoTexto(18, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(260, 36));
    fieldFiltro.getDocument().addDocumentListener(new TratarPesquisa(origemFiltro));
    return fieldFiltro;
  }

  private JComboBox<String> criarComboFiltro(boolean incluirEstadoAbastecimento)
  {
    String[] criterios = incluirEstadoAbastecimento
        ? new String[]
          {
              "codigo_rh", "tipo_rh", "localizacao_rh", "sazonalidade_rh", "nivel_exploracao_rh",
              "estado_abastecimento"
          }
        : new String[]
          {
              "codigo_rh", "tipo_rh", "localizacao_rh", "sazonalidade_rh", "nivel_exploracao_rh"
          };
    JComboBox<String> combo = InterfaceGraficaUtils.criarCombo(criterios, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(incluirEstadoAbastecimento ? 220 : 190, 36));
    combo.addActionListener(tratarButtons);
    return combo;
  }

  private JPanel criarTopoTabela(JLabel label, JTextField fieldFiltro, JComboBox<String> comboFiltro,
      JButton buttonActualizar, JButton buttonLimpar, JButton buttonAjuda)
  {
    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    JPanel linhaControles = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaControles.add(new JLabel("Pesquisar:"));
    linhaControles.add(fieldFiltro);
    linhaControles.add(comboFiltro);
    linhaControles.add(buttonActualizar);
    linhaControles.add(buttonLimpar);
    linhaControles.add(buttonAjuda);

    topo.add(label, BorderLayout.NORTH);
    topo.add(linhaControles, BorderLayout.CENTER);

    return topo;
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

  private void carregarTabelaRecursosMedidas()
  {
    carregarTabela(tabelaRecursosMedidas, RecursoHidricoConsultasSQL.VISUALIZAR_RECURSOS_MEDIDAS);
    aplicarFiltroRecursosMedidas();
  }

  private void carregarTabelaRecursosPontos()
  {
    if(!ConsultaRemotaUtils.prepararConsulta(connection, new VerificadorConexaoRemota.NoRemoto[]
      {
          VerificadorConexaoRemota.NoRemoto.DISTRIBUICAO_CONSUMO
      }, tabelaRecursosPontos, new JComponent[]
        {
            fieldFiltroRecursosPontos, comboFiltroRecursosPontos, buttonActualizarRecursosPontos,
            buttonLimparRecursosPontos, buttonAjudaRecursosPontos
        }))
      return;

    carregarTabela(tabelaRecursosPontos, RecursoHidricoConsultasSQL.VISUALIZAR_RECURSOS_PONTOS);
    aplicarFiltroRecursosPontos();
  }

  private void carregarTabelaQualidadeAgua()
  {
    if(!ConsultaRemotaUtils.prepararConsulta(connection, new VerificadorConexaoRemota.NoRemoto[]
      {
          VerificadorConexaoRemota.NoRemoto.EQUIPES_GESTAO
      }, tabelaQualidadeAgua, new JComponent[]
        {
            fieldFiltroQualidadeAgua, comboFiltroQualidadeAgua, buttonActualizarQualidadeAgua,
            buttonLimparQualidadeAgua, buttonAjudaQualidadeAgua
        }))
      return;

    carregarTabela(tabelaQualidadeAgua, RecursoHidricoConsultasSQL.VISUALIZAR_QUALIDADE_AGUA);
    aplicarFiltroQualidadeAgua();
  }

  private void carregarTabelaHistoricoAbastecimento()
  {
    if(!ConsultaRemotaUtils.prepararConsulta(connection, new VerificadorConexaoRemota.NoRemoto[]
      {
          VerificadorConexaoRemota.NoRemoto.DISTRIBUICAO_CONSUMO
      }, tabelaHistoricoAbastecimento, new JComponent[]
        {
            fieldFiltroHistoricoAbastecimento, comboFiltroHistoricoAbastecimento,
            buttonActualizarHistoricoAbastecimento, buttonLimparHistoricoAbastecimento,
            buttonAjudaHistoricoAbastecimento
        }))
      return;

    carregarTabela(tabelaHistoricoAbastecimento, RecursoHidricoConsultasSQL.VISUALIZAR_HISTORICO_ABASTECIMENTO);
    aplicarFiltroHistoricoAbastecimento();
  }

  private void carregarTabela(JTable tabela, String sql)
  {
    try
    {
      DefaultTableModel modelo = RecursoHidricoConsultasSQL.carregarModeloTabela(connection, sql);
      tabela.setModel(modelo);
      InterfaceGraficaUtils.configurarTabela(tabela, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
          InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);

      if(tabela == tabelaRecursosMedidas)
      {
        modeloRecursosMedidas = modelo;
        sorterRecursosMedidas = new TableRowSorter<>(modeloRecursosMedidas);
        tabelaRecursosMedidas.setRowSorter(sorterRecursosMedidas);
      }
      else if(tabela == tabelaRecursosPontos)
      {
        modeloRecursosPontos = modelo;
        sorterRecursosPontos = new TableRowSorter<>(modeloRecursosPontos);
        tabelaRecursosPontos.setRowSorter(sorterRecursosPontos);
      }
      else if(tabela == tabelaQualidadeAgua)
      {
        modeloQualidadeAgua = modelo;
        sorterQualidadeAgua = new TableRowSorter<>(modeloQualidadeAgua);
        tabelaQualidadeAgua.setRowSorter(sorterQualidadeAgua);
      }
      else if(tabela == tabelaHistoricoAbastecimento)
      {
        modeloHistoricoAbastecimento = modelo;
        sorterHistoricoAbastecimento = new TableRowSorter<>(modeloHistoricoAbastecimento);
        tabelaHistoricoAbastecimento.setRowSorter(sorterHistoricoAbastecimento);
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

  private void aplicarFiltroRecursosMedidas()
  {
    aplicarFiltroTabela(sorterRecursosMedidas, fieldFiltroRecursosMedidas, comboFiltroRecursosMedidas,
        modeloRecursosMedidas);
  }

  private void aplicarFiltroRecursosPontos()
  {
    aplicarFiltroTabela(sorterRecursosPontos, fieldFiltroRecursosPontos, comboFiltroRecursosPontos,
        modeloRecursosPontos);
  }

  private void aplicarFiltroQualidadeAgua()
  {
    aplicarFiltroTabela(sorterQualidadeAgua, fieldFiltroQualidadeAgua, comboFiltroQualidadeAgua,
        modeloQualidadeAgua);
  }

  private void aplicarFiltroHistoricoAbastecimento()
  {
    aplicarFiltroTabela(sorterHistoricoAbastecimento, fieldFiltroHistoricoAbastecimento,
        comboFiltroHistoricoAbastecimento, modeloHistoricoAbastecimento);
  }

  private void aplicarFiltroTabela(TableRowSorter<DefaultTableModel> sorter, JTextField fieldFiltro,
      JComboBox<String> comboFiltro, DefaultTableModel modelo)
  {
    if(sorter == null || fieldFiltro == null || comboFiltro == null || modelo == null)
      return;

    String texto = fieldFiltro.getText().trim();

    if(texto.isEmpty())
    {
      sorter.setRowFilter(null);
      return;
    }

    String colunaFiltro = comboFiltro.getSelectedItem().toString();
    int coluna = InterfaceGraficaUtils.encontrarIndiceColuna(modelo, colunaFiltro);
    if(coluna < 0)
    {
      sorter.setRowFilter(null);
      return;
    }

    if("codigo_rh".equals(colunaFiltro))
      sorter.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(texto) + "$", coluna));
    else
      sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private void mostrarAjudaRecursosMedidas()
  {
    JOptionPane.showMessageDialog(this,
        "Esta pesquisa filtra pelo codigo_rh.\n\n"
            + "Tambem pode pesquisar por tipo_rh, localizacao_rh, sazonalidade_rh e nivel_exploracao_rh.\n\n"
            + "A pesquisa por codigo_rh usa correspondencia exacta; os outros criterios aceitam texto parcial.",
        "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private void mostrarAjudaRecursosPontos()
  {
    JOptionPane.showMessageDialog(this,
        "Esta pesquisa filtra pelo codigo_rh.\n\n"
            + "Tambem pode pesquisar por tipo_rh, localizacao_rh, sazonalidade_rh e nivel_exploracao_rh.\n\n"
            + "A pesquisa por codigo_rh usa correspondencia exacta; os outros criterios aceitam texto parcial.",
        "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private void mostrarAjudaQualidadeAgua()
  {
    JOptionPane.showMessageDialog(this,
        "Esta pesquisa filtra pelo codigo_rh.\n\n"
            + "Tambem pode pesquisar por tipo_rh, localizacao_rh, sazonalidade_rh e nivel_exploracao_rh.\n\n"
            + "A pesquisa por codigo_rh usa correspondencia exacta; os outros criterios aceitam texto parcial.",
        "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private void mostrarAjudaHistoricoAbastecimento()
  {
    JOptionPane.showMessageDialog(this,
        "Esta pesquisa permite filtrar historicos de abastecimento por codigo_rh, tipo_rh, localizacao_rh, "
            + "sazonalidade_rh, nivel_exploracao_rh ou estado_abastecimento.\n\n"
            + "A pesquisa por codigo_rh usa correspondencia exacta; os outros criterios aceitam texto parcial.",
        "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private enum OrigemFiltro
  {
    RECURSOS_MEDIDAS, RECURSOS_PONTOS, QUALIDADE_AGUA, HISTORICO_ABASTECIMENTO
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
        case RECURSOS_MEDIDAS:
          aplicarFiltroRecursosMedidas();
          break;
        case RECURSOS_PONTOS:
          aplicarFiltroRecursosPontos();
          break;
        case QUALIDADE_AGUA:
          aplicarFiltroQualidadeAgua();
          break;
        case HISTORICO_ABASTECIMENTO:
          aplicarFiltroHistoricoAbastecimento();
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

      if(source == buttonActualizarRecursosMedidas)
      {
        carregarTabelaRecursosMedidas();
        return;
      }

      if(source == buttonLimparRecursosMedidas)
      {
        fieldFiltroRecursosMedidas.setText("");
        aplicarFiltroRecursosMedidas();
        return;
      }

      if(source == buttonAjudaRecursosMedidas)
      {
        mostrarAjudaRecursosMedidas();
        return;
      }

      if(source == comboFiltroRecursosMedidas)
      {
        aplicarFiltroRecursosMedidas();
        return;
      }

      if(source == buttonActualizarRecursosPontos)
      {
        carregarTabelaRecursosPontos();
        return;
      }

      if(source == buttonLimparRecursosPontos)
      {
        fieldFiltroRecursosPontos.setText("");
        aplicarFiltroRecursosPontos();
        return;
      }

      if(source == buttonAjudaRecursosPontos)
      {
        mostrarAjudaRecursosPontos();
        return;
      }

      if(source == comboFiltroRecursosPontos)
      {
        aplicarFiltroRecursosPontos();
        return;
      }

      if(source == buttonActualizarQualidadeAgua)
      {
        carregarTabelaQualidadeAgua();
        return;
      }

      if(source == buttonLimparQualidadeAgua)
      {
        fieldFiltroQualidadeAgua.setText("");
        aplicarFiltroQualidadeAgua();
        return;
      }

      if(source == buttonAjudaQualidadeAgua)
      {
        mostrarAjudaQualidadeAgua();
        return;
      }

      if(source == comboFiltroQualidadeAgua)
      {
        aplicarFiltroQualidadeAgua();
        return;
      }

      if(source == buttonActualizarHistoricoAbastecimento)
      {
        carregarTabelaHistoricoAbastecimento();
        return;
      }

      if(source == buttonLimparHistoricoAbastecimento)
      {
        fieldFiltroHistoricoAbastecimento.setText("");
        aplicarFiltroHistoricoAbastecimento();
        return;
      }

      if(source == buttonAjudaHistoricoAbastecimento)
      {
        mostrarAjudaHistoricoAbastecimento();
        return;
      }

      if(source == comboFiltroHistoricoAbastecimento)
      {
        aplicarFiltroHistoricoAbastecimento();
      }
    }
  }

  // =========================== FIM DA LOGICA DA INTERFACE GRAFICA
  // ===========================
}
