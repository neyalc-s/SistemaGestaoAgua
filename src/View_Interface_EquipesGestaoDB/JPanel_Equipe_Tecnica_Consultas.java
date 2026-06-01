package View_Interface_EquipesGestaoDB;

import Repository_SQL.EquipesGestaoDB.EquipeTecnicaConsultasSQL;
import Resources.IconesInterface;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.ConsultaRemotaUtils;
import Resources.VerificadorConexaoRemota;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

public class JPanel_Equipe_Tecnica_Consultas extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaTecnicos;
  private JTable tabelaAnalistas;
  private JTable tabelaEducadores;
  private JTable tabelaEquipes;
  private JTable tabelaPds;
  private JTable tabelaHistorico;

  private DefaultTableModel modeloTecnicos;
  private DefaultTableModel modeloAnalistas;
  private DefaultTableModel modeloEducadores;
  private DefaultTableModel modeloEquipes;
  private DefaultTableModel modeloPds;
  private DefaultTableModel modeloHistorico;

  private TableRowSorter<DefaultTableModel> sorterTecnicos;
  private TableRowSorter<DefaultTableModel> sorterAnalistas;
  private TableRowSorter<DefaultTableModel> sorterEducadores;
  private TableRowSorter<DefaultTableModel> sorterEquipes;
  private TableRowSorter<DefaultTableModel> sorterPds;
  private TableRowSorter<DefaultTableModel> sorterHistorico;

  private JTextField fieldFiltroTecnicos;
  private JTextField fieldFiltroAnalistas;
  private JTextField fieldFiltroEducadores;
  private JTextField fieldFiltroEquipes;
  private JTextField fieldFiltroPds;
  private JTextField fieldFiltroHistorico;

  private JButton buttonActualizarTecnicos;
  private JButton buttonLimparTecnicos;
  private JButton buttonActualizarAnalistas;
  private JButton buttonLimparAnalistas;
  private JButton buttonActualizarEducadores;
  private JButton buttonLimparEducadores;
  private JButton buttonActualizarEquipes;
  private JButton buttonLimparEquipes;
  private JButton buttonActualizarPds;
  private JButton buttonLimparPds;
  private JButton buttonActualizarHistorico;
  private JButton buttonLimparHistorico;
  private JButton buttonAjudaTecnicos;
  private JButton buttonAjudaAnalistas;
  private JButton buttonAjudaEducadores;
  private JButton buttonAjudaEquipes;
  private JButton buttonAjudaPds;
  private JButton buttonAjudaHistorico;

  public JPanel_Equipe_Tecnica_Consultas(Connection connection)
  {
    this(connection, false);
  }

  public JPanel_Equipe_Tecnica_Consultas(Connection connection, boolean carregarTabelasAutomaticamente)
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
      carregarTodasTabelas();
  }

  // ========================= INICIO DO CODIGO DE INTERFACE GRAFICA
  // =========================

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Consultas de Equipe Técnica",
        "<html>As consultas foram agrupadas por tipo de equipa e pelas pesquisas gerais do menu antigo. "
            + "Em todas as abas a pesquisa específica é feita por <b>equipe_id</b>.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JComponent criarCentro()
  {
    JTabbedPane tabs = new JTabbedPane();
    tabs.setFont(InterfaceGraficaUtils.FONT_ABA);

    tabs.addTab("Técnicos", criarTabTecnicos());
    tabs.addTab("Analistas", criarTabAnalistas());
    tabs.addTab("Educadores", criarTabEducadores());
    tabs.addTab("Equipes", criarTabEquipes());
    tabs.addTab("PDs Geridos", criarTabPds());
    tabs.addTab("Histórico", criarTabHistorico());

    return tabs;
  }

  private JPanel criarTabTecnicos()
  {
    return criarTabTabela("Técnicos de Manutenção", OrigemFiltro.TECNICOS);
  }

  private JPanel criarTabAnalistas()
  {
    return criarTabTabela("Analistas de Qualidade", OrigemFiltro.ANALISTAS);
  }

  private JPanel criarTabEducadores()
  {
    return criarTabTabela("Educadores Comunitários", OrigemFiltro.EDUCADORES);
  }

  private JPanel criarTabEquipes()
  {
    return criarTabTabela("Equipe Técnica Base", OrigemFiltro.EQUIPES);
  }

  private JPanel criarTabPds()
  {
    return criarTabTabela("Pontos de Distribuição Geridos", OrigemFiltro.PDS);
  }

  private JPanel criarTabHistorico()
  {
    return criarTabTabela("Histórico de Manutenção por Equipe", OrigemFiltro.HISTORICO);
  }

  private JPanel criarTabTabela(String titulo, OrigemFiltro origem)
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    JLabel label = InterfaceGraficaUtils.criarLabel(titulo, InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_AZUL);
    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));

    topo.add(label, BorderLayout.NORTH);
    topo.add(criarPainelPesquisa(origem), BorderLayout.SOUTH);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(origem), BorderLayout.CENTER);

    return panel;
  }

  private JPanel criarPainelPesquisa(OrigemFiltro origem)
  {
    JPanel panel = InterfaceGraficaUtils
        .criarPainelTransparente(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 0));

    JLabel label = InterfaceGraficaUtils.criarLabel("Pesquisar por equipe_id:", InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_TEXTO);
    JTextField fieldFiltro = InterfaceGraficaUtils.criarCampoTexto(18, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(240, 36));
    JButton buttonActualizar = criarBotao("Mostrar Todos", new Dimension(160, 36));
    JButton buttonLimpar = criarBotao("Limpar", new Dimension(120, 36));
    JButton buttonAjuda = criarBotaoAjuda();

    fieldFiltro.getDocument().addDocumentListener(new TratarPesquisa(origem));
    buttonActualizar.addActionListener(tratarButtons);
    buttonLimpar.addActionListener(tratarButtons);
    buttonAjuda.addActionListener(tratarButtons);

    guardarComponentesPesquisa(origem, fieldFiltro, buttonActualizar, buttonLimpar, buttonAjuda);

    panel.add(label);
    panel.add(fieldFiltro);
    panel.add(buttonActualizar);
    panel.add(buttonLimpar);
    panel.add(buttonAjuda);

    return panel;
  }

  private JScrollPane criarScrollTabela(OrigemFiltro origem)
  {
    JTable tabela = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabela, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1100, 560));

    guardarTabela(origem, tabela);

    return scroll;
  }

  private JButton criarBotao(String texto, Dimension tamanho)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, tamanho);
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  private JButton criarBotaoAjuda()
  {
    JButton botao = InterfaceGraficaUtils.criarBotao("?", InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(44, 36));
    IconesInterface.aplicarIconeBotao(botao);
    botao.setToolTipText("Ajuda da pesquisa");
    return botao;
  }

  // ========================== FIM DO CODIGO DE INTERFACE GRAFICA
  // ==========================

  // ========================== INICIO DA LOGICA DA INTERFACE GRAFICA
  // ==========================

  private void guardarComponentesPesquisa(OrigemFiltro origem, JTextField fieldFiltro, JButton buttonActualizar,
      JButton buttonLimpar, JButton buttonAjuda)
  {
    switch(origem)
    {
      case TECNICOS:
        fieldFiltroTecnicos = fieldFiltro;
        buttonActualizarTecnicos = buttonActualizar;
        buttonLimparTecnicos = buttonLimpar;
        buttonAjudaTecnicos = buttonAjuda;
        break;
      case ANALISTAS:
        fieldFiltroAnalistas = fieldFiltro;
        buttonActualizarAnalistas = buttonActualizar;
        buttonLimparAnalistas = buttonLimpar;
        buttonAjudaAnalistas = buttonAjuda;
        break;
      case EDUCADORES:
        fieldFiltroEducadores = fieldFiltro;
        buttonActualizarEducadores = buttonActualizar;
        buttonLimparEducadores = buttonLimpar;
        buttonAjudaEducadores = buttonAjuda;
        break;
      case EQUIPES:
        fieldFiltroEquipes = fieldFiltro;
        buttonActualizarEquipes = buttonActualizar;
        buttonLimparEquipes = buttonLimpar;
        buttonAjudaEquipes = buttonAjuda;
        break;
      case PDS:
        fieldFiltroPds = fieldFiltro;
        buttonActualizarPds = buttonActualizar;
        buttonLimparPds = buttonLimpar;
        buttonAjudaPds = buttonAjuda;
        break;
      case HISTORICO:
        fieldFiltroHistorico = fieldFiltro;
        buttonActualizarHistorico = buttonActualizar;
        buttonLimparHistorico = buttonLimpar;
        buttonAjudaHistorico = buttonAjuda;
        break;
    }
  }

  private void guardarTabela(OrigemFiltro origem, JTable tabela)
  {
    switch(origem)
    {
      case TECNICOS:
        tabelaTecnicos = tabela;
        break;
      case ANALISTAS:
        tabelaAnalistas = tabela;
        break;
      case EDUCADORES:
        tabelaEducadores = tabela;
        break;
      case EQUIPES:
        tabelaEquipes = tabela;
        break;
      case PDS:
        tabelaPds = tabela;
        break;
      case HISTORICO:
        tabelaHistorico = tabela;
        break;
    }
  }

  private void carregarTodasTabelas()
  {
    carregarTabelaTecnicos();
    carregarTabelaAnalistas();
    carregarTabelaEducadores();
    carregarTabelaEquipes();
    carregarTabelaPds();
    carregarTabelaHistorico();
  }

  private void carregarTabelaTecnicos()
  {
    carregarTabela(OrigemFiltro.TECNICOS, EquipeTecnicaConsultasSQL.VISUALIZAR_TECNICOS_MANUTENCAO);
  }

  private void carregarTabelaAnalistas()
  {
    carregarTabela(OrigemFiltro.ANALISTAS, EquipeTecnicaConsultasSQL.VISUALIZAR_ANALISTAS_QUALIDADE);
  }

  private void carregarTabelaEducadores()
  {
    carregarTabela(OrigemFiltro.EDUCADORES, EquipeTecnicaConsultasSQL.VISUALIZAR_EDUCADORES_COMUNITARIOS);
  }

  private void carregarTabelaEquipes()
  {
    carregarTabela(OrigemFiltro.EQUIPES, EquipeTecnicaConsultasSQL.VISUALIZAR_EQUIPE_TECNICA);
  }

  private void carregarTabelaPds()
  {
    if(!ConsultaRemotaUtils.prepararConsulta(connection, new VerificadorConexaoRemota.NoRemoto[]
      {
          VerificadorConexaoRemota.NoRemoto.DISTRIBUICAO_CONSUMO
      }, tabelaPds, new JComponent[]
        {
            fieldFiltroPds, buttonActualizarPds, buttonLimparPds, buttonAjudaPds
        }))
      return;

    carregarTabela(OrigemFiltro.PDS, EquipeTecnicaConsultasSQL.VISUALIZAR_EQUIPE_COM_PDS);
  }

  private void carregarTabelaHistorico()
  {
    if(!ConsultaRemotaUtils.prepararConsulta(connection, new VerificadorConexaoRemota.NoRemoto[]
      {
          VerificadorConexaoRemota.NoRemoto.DISTRIBUICAO_CONSUMO
      }, tabelaHistorico, new JComponent[]
        {
            fieldFiltroHistorico, buttonActualizarHistorico, buttonLimparHistorico, buttonAjudaHistorico
        }))
      return;

    carregarTabela(OrigemFiltro.HISTORICO, EquipeTecnicaConsultasSQL.VISUALIZAR_HISTORICO_EQUIPE);
  }

  private void carregarTabela(OrigemFiltro origem, String sql)
  {
    try
    {
      DefaultTableModel modelo = EquipeTecnicaConsultasSQL.carregarModeloTabela(connection, sql);
      JTable tabela = getTabela(origem);
      tabela.setModel(modelo);

      TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(modelo);
      tabela.setRowSorter(sorter);

      guardarModeloESorter(origem, modelo, sorter);
      InterfaceGraficaUtils.ajustarLarguraColunas(tabela, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER);
      aplicarFiltro(origem);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar dados de equipe técnica:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void guardarModeloESorter(OrigemFiltro origem, DefaultTableModel modelo,
      TableRowSorter<DefaultTableModel> sorter)
  {
    switch(origem)
    {
      case TECNICOS:
        modeloTecnicos = modelo;
        sorterTecnicos = sorter;
        break;
      case ANALISTAS:
        modeloAnalistas = modelo;
        sorterAnalistas = sorter;
        break;
      case EDUCADORES:
        modeloEducadores = modelo;
        sorterEducadores = sorter;
        break;
      case EQUIPES:
        modeloEquipes = modelo;
        sorterEquipes = sorter;
        break;
      case PDS:
        modeloPds = modelo;
        sorterPds = sorter;
        break;
      case HISTORICO:
        modeloHistorico = modelo;
        sorterHistorico = sorter;
        break;
    }
  }

  private JTable getTabela(OrigemFiltro origem)
  {
    switch(origem)
    {
      case TECNICOS:
        return tabelaTecnicos;
      case ANALISTAS:
        return tabelaAnalistas;
      case EDUCADORES:
        return tabelaEducadores;
      case EQUIPES:
        return tabelaEquipes;
      case PDS:
        return tabelaPds;
      case HISTORICO:
        return tabelaHistorico;
      default:
        return tabelaEquipes;
    }
  }

  private DefaultTableModel getModelo(OrigemFiltro origem)
  {
    switch(origem)
    {
      case TECNICOS:
        return modeloTecnicos;
      case ANALISTAS:
        return modeloAnalistas;
      case EDUCADORES:
        return modeloEducadores;
      case EQUIPES:
        return modeloEquipes;
      case PDS:
        return modeloPds;
      case HISTORICO:
        return modeloHistorico;
      default:
        return modeloEquipes;
    }
  }

  private TableRowSorter<DefaultTableModel> getSorter(OrigemFiltro origem)
  {
    switch(origem)
    {
      case TECNICOS:
        return sorterTecnicos;
      case ANALISTAS:
        return sorterAnalistas;
      case EDUCADORES:
        return sorterEducadores;
      case EQUIPES:
        return sorterEquipes;
      case PDS:
        return sorterPds;
      case HISTORICO:
        return sorterHistorico;
      default:
        return sorterEquipes;
    }
  }

  private JTextField getFieldFiltro(OrigemFiltro origem)
  {
    switch(origem)
    {
      case TECNICOS:
        return fieldFiltroTecnicos;
      case ANALISTAS:
        return fieldFiltroAnalistas;
      case EDUCADORES:
        return fieldFiltroEducadores;
      case EQUIPES:
        return fieldFiltroEquipes;
      case PDS:
        return fieldFiltroPds;
      case HISTORICO:
        return fieldFiltroHistorico;
      default:
        return fieldFiltroEquipes;
    }
  }

  private void aplicarFiltro(OrigemFiltro origem)
  {
    TableRowSorter<DefaultTableModel> sorter = getSorter(origem);
    DefaultTableModel modelo = getModelo(origem);
    JTextField fieldFiltro = getFieldFiltro(origem);

    if(sorter == null || modelo == null || fieldFiltro == null)
      return;

    String texto = fieldFiltro.getText().trim();
    if(texto.isEmpty())
    {
      sorter.setRowFilter(null);
      return;
    }

    int coluna = InterfaceGraficaUtils.encontrarIndiceColuna(modelo, "equipe_id");
    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private void limparFiltro(OrigemFiltro origem)
  {
    JTextField fieldFiltro = getFieldFiltro(origem);
    if(fieldFiltro != null)
      fieldFiltro.setText("");

    aplicarFiltro(origem);
  }

  private void actualizarTabela(OrigemFiltro origem)
  {
    switch(origem)
    {
      case TECNICOS:
        carregarTabelaTecnicos();
        break;
      case ANALISTAS:
        carregarTabelaAnalistas();
        break;
      case EDUCADORES:
        carregarTabelaEducadores();
        break;
      case EQUIPES:
        carregarTabelaEquipes();
        break;
      case PDS:
        carregarTabelaPds();
        break;
      case HISTORICO:
        carregarTabelaHistorico();
        break;
    }
  }

  private void mostrarAjuda(OrigemFiltro origem)
  {
    JOptionPane.showMessageDialog(this, getMensagemAjuda(origem), "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private String getMensagemAjuda(OrigemFiltro origem)
  {
    switch(origem)
    {
      case TECNICOS:
        return "Esta pesquisa filtra pelo equipe_id.\n\n"
            + "Se escrever o código de uma equipe, a tabela mostra o técnico de manutenção dessa equipe.\n"
            + "A pesquisa aceita parte do código.";
      case ANALISTAS:
        return "Esta pesquisa filtra pelo equipe_id.\n\n"
            + "Se escrever o código de uma equipe, a tabela mostra o analista de qualidade dessa equipe.\n"
            + "A pesquisa aceita parte do código.";
      case EDUCADORES:
        return "Esta pesquisa filtra pelo equipe_id.\n\n"
            + "Se escrever o código de uma equipe, a tabela mostra o educador comunitario dessa equipe.\n"
            + "A pesquisa aceita parte do código.";
      case EQUIPES:
        return "Esta pesquisa filtra pelo equipe_id.\n\n"
            + "Se escrever o código de uma equipe, a tabela mostra os dados base dessa equipe técnica.\n"
            + "A pesquisa aceita parte do código.";
      case PDS:
        return "Esta pesquisa filtra pelo equipe_id.\n\n"
            + "Se escrever o código de uma equipe, a tabela mostra os pontos de distribuição geridos por essa equipe.\n"
            + "A pesquisa aceita parte do código.";
      case HISTORICO:
        return "Esta pesquisa filtra pelo equipe_id.\n\n"
            + "Se escrever o código de uma equipe, a tabela mostra o histórico de manutenção associado a essa equipe.\n"
            + "A pesquisa aceita parte do código.";
      default:
        return "Esta pesquisa filtra pelo equipe_id.";
    }
  }

  private OrigemFiltro getOrigemDoBotao(Object source)
  {
    if(source == buttonActualizarTecnicos || source == buttonLimparTecnicos || source == buttonAjudaTecnicos)
      return OrigemFiltro.TECNICOS;
    if(source == buttonActualizarAnalistas || source == buttonLimparAnalistas || source == buttonAjudaAnalistas)
      return OrigemFiltro.ANALISTAS;
    if(source == buttonActualizarEducadores || source == buttonLimparEducadores || source == buttonAjudaEducadores)
      return OrigemFiltro.EDUCADORES;
    if(source == buttonActualizarEquipes || source == buttonLimparEquipes || source == buttonAjudaEquipes)
      return OrigemFiltro.EQUIPES;
    if(source == buttonActualizarPds || source == buttonLimparPds || source == buttonAjudaPds)
      return OrigemFiltro.PDS;
    if(source == buttonActualizarHistorico || source == buttonLimparHistorico || source == buttonAjudaHistorico)
      return OrigemFiltro.HISTORICO;

    return null;
  }

  private boolean isBotaoAjuda(Object source)
  {
    return source == buttonAjudaTecnicos || source == buttonAjudaAnalistas || source == buttonAjudaEducadores
        || source == buttonAjudaEquipes || source == buttonAjudaPds || source == buttonAjudaHistorico;
  }

  private boolean isBotaoLimpar(Object source)
  {
    return source == buttonLimparTecnicos || source == buttonLimparAnalistas || source == buttonLimparEducadores
        || source == buttonLimparEquipes || source == buttonLimparPds || source == buttonLimparHistorico;
  }

  private enum OrigemFiltro
  {
    TECNICOS, ANALISTAS, EDUCADORES, EQUIPES, PDS, HISTORICO
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
      aplicarFiltro(origem);
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltro(origem);
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltro(origem);
    }
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent event)
    {
      OrigemFiltro origem = getOrigemDoBotao(event.getSource());
      if(origem == null)
        return;

      if(isBotaoLimpar(event.getSource()))
        limparFiltro(origem);
      else if(isBotaoAjuda(event.getSource()))
        mostrarAjuda(origem);
      else
        actualizarTabela(origem);
    }
  }

  // =========================== FIM DA LOGICA DA INTERFACE GRAFICA ===========================
}
