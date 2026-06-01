package View_Interface_TransferenciasRecursosDB;

import Repository_SQL.TransferenciasRecursosDB.TransferenciaCotaSQL;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;

public class JPanel_Doar_Cota extends JPanel
{
  private static final String CARD_FAMILIA_DOADORA = "familia_doadora";
  private static final String CARD_COTA_DOADORA = "cota_doadora";
  private static final String CARD_FAMILIA_RECEPTORA = "familia_receptora";
  private static final String CARD_COTA_RECEPTORA = "cota_receptora";
  private static final String CARD_FORMULARIO = "formulario";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final TratarButtons tratarButtons = new TratarButtons();

  private CardLayout cardLayout;
  private JPanel panelCards;

  private JTable tabelaFamiliasDoadoras;
  private JTable tabelaCotasDoadoras;
  private JTable tabelaFamiliasReceptoras;
  private JTable tabelaCotasReceptoras;

  private DefaultTableModel modeloFamiliasDoadoras;
  private DefaultTableModel modeloCotasDoadoras;
  private DefaultTableModel modeloFamiliasReceptoras;
  private DefaultTableModel modeloCotasReceptoras;

  private TableRowSorter<DefaultTableModel> sorterFamiliasDoadoras;
  private TableRowSorter<DefaultTableModel> sorterCotasDoadoras;
  private TableRowSorter<DefaultTableModel> sorterFamiliasReceptoras;
  private TableRowSorter<DefaultTableModel> sorterCotasReceptoras;

  private JTextField fieldPesquisaFamiliasDoadoras;
  private JTextField fieldPesquisaCotasDoadoras;
  private JTextField fieldPesquisaFamiliasReceptoras;
  private JTextField fieldPesquisaCotasReceptoras;

  private JComboBox<String> comboCriterioFamiliasDoadoras;
  private JComboBox<String> comboCriterioFamiliasReceptoras;

  private JButton buttonActualizarFamiliasDoadoras;
  private JButton buttonLimparFamiliasDoadoras;
  private JButton buttonVoltarDashboard;
  private JButton buttonProcederFamiliaDoadora;

  private JButton buttonVoltarCotasDoadoras;
  private JButton buttonActualizarCotasDoadoras;
  private JButton buttonLimparCotasDoadoras;
  private JButton buttonProcederCotaDoadora;

  private JButton buttonVoltarFamiliasReceptoras;
  private JButton buttonActualizarFamiliasReceptoras;
  private JButton buttonLimparFamiliasReceptoras;
  private JButton buttonProcederFamiliaReceptora;

  private JButton buttonVoltarCotasReceptoras;
  private JButton buttonActualizarCotasReceptoras;
  private JButton buttonLimparCotasReceptoras;
  private JButton buttonProcederCotaReceptora;

  private JButton buttonVoltarFormulario;
  private JButton buttonExecutarTransferencia;

  private JTextField fieldCodigoFamiliaDoadora;
  private JTextField fieldCodigoCotaDoadora;
  private JTextField fieldCodigoFamiliaReceptora;
  private JTextField fieldCodigoCotaReceptora;
  private JTextField fieldVolumeTransferido;
  private JTextField fieldValidadeTransferencia;
  private JTextArea areaMotivo;

  public JPanel_Doar_Cota(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_Doar_Cota(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_Doar_Cota(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_Doar_Cota(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);

    panelCards.add(criarCardFamiliaDoadora(), CARD_FAMILIA_DOADORA);
    panelCards.add(criarCardCotaDoadora(), CARD_COTA_DOADORA);
    panelCards.add(criarCardFamiliaReceptora(), CARD_FAMILIA_RECEPTORA);
    panelCards.add(criarCardCotaReceptora(), CARD_COTA_RECEPTORA);
    panelCards.add(criarCardFormulario(), CARD_FORMULARIO);

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
      carregarFamiliasDoadoras();
    }
  }

  // ========================= INICIO DO CODIGO DE INTERFACE GRAFICA
  // =========================

  private JPanel criarCardFamiliaDoadora()
  {
    return criarCardTabela("Transferir Cota", "Primeiro, selecione a família doadora.", "Famílias Doadoras",
        OrigemTabela.FAMILIAS_DOADORAS, null, buttonProcederFamiliaDoadora);
  }

  private JPanel criarCardCotaDoadora()
  {
    return criarCardTabela("Cota da Família Doadora", "Escolha a cota da família doadora.", "Cotas da Família Doadora",
        OrigemTabela.COTAS_DOADORAS, CARD_FAMILIA_DOADORA, buttonProcederCotaDoadora);
  }

  private JPanel criarCardFamiliaReceptora()
  {
    return criarCardTabela("Família Receptora", "Agora, selecione a família que vai receber a transferência.",
        "Famílias Receptoras", OrigemTabela.FAMILIAS_RECEPTORAS, CARD_COTA_DOADORA, buttonProcederFamiliaReceptora);
  }

  private JPanel criarCardCotaReceptora()
  {
    return criarCardTabela("Cota da Família Receptora", "Escolha a cota que vai receber o volume transferido.",
        "Cotas da Família Receptora", OrigemTabela.COTAS_RECEPTORAS, CARD_FAMILIA_RECEPTORA,
        buttonProcederCotaReceptora);
  }

  private JPanel criarCardTabela(String titulo, String subtitulo, String tituloTabela, OrigemTabela origem,
      String cardVoltar, JButton ignored)
  {
    JPanel painelExterno = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1180, 720));

    card.add(criarTopo(titulo, subtitulo), BorderLayout.NORTH);
    card.add(criarCentroTabela(tituloTabela, origem), BorderLayout.CENTER);
    card.add(criarRodapeTabela(origem, cardVoltar), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;

    painelExterno.add(card, gbc);
    return painelExterno;
  }

  private JPanel criarCentroTabela(String tituloTabela, OrigemTabela origem)
  {
    JPanel centro = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    JLabel labelTabela = InterfaceGraficaUtils.criarLabel(tituloTabela, InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_AZUL);
    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));

    topoTabela.add(labelTabela, BorderLayout.NORTH);
    topoTabela.add(criarPainelPesquisa(origem), BorderLayout.SOUTH);

    centro.add(topoTabela, BorderLayout.NORTH);
    centro.add(criarScrollTabela(origem), BorderLayout.CENTER);

    return centro;
  }

  private JPanel criarTopo(String titulo, String subtitulo)
  {
    return InterfaceGraficaUtils.criarTopo(titulo, "<html>" + subtitulo + "</html>", InterfaceGraficaUtils.FONT_TITULO,
        InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO, InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarPainelPesquisa(OrigemTabela origem)
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    boolean pesquisaFamilia = isPesquisaFamilia(origem);
    JLabel label = InterfaceGraficaUtils.criarLabel(pesquisaFamilia ? "Pesquisar por:" : "Pesquisar por código:",
        InterfaceGraficaUtils.FONT_LABEL_FORM, InterfaceGraficaUtils.COR_TEXTO);
    JComboBox<String> comboCriterio = null;
    JTextField fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(18, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(240, 36));
    JButton buttonActualizar = criarBotao("Mostrar Todos", new Dimension(160, 36));
    JButton buttonLimpar = criarBotao("Limpar", new Dimension(120, 36));

    if(pesquisaFamilia)
    {
      comboCriterio = InterfaceGraficaUtils.criarCombo(new String[]
        {
            "Código da Família", "Nome do Responsável", "Telefone"
        }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(210, 36));
      comboCriterio.addActionListener(tratarButtons);
    }

    fieldPesquisa.getDocument().addDocumentListener(new TratarPesquisa(origem));
    buttonActualizar.addActionListener(tratarButtons);
    buttonLimpar.addActionListener(tratarButtons);

    guardarPesquisa(origem, fieldPesquisa, comboCriterio, buttonActualizar, buttonLimpar);

    panel.add(label);
    if(comboCriterio != null)
      panel.add(comboCriterio);
    panel.add(fieldPesquisa);
    panel.add(buttonActualizar);
    panel.add(buttonLimpar);

    return panel;
  }

  private JScrollPane criarScrollTabela(OrigemTabela origem)
  {
    JTable tabela = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tabela.getSelectionModel().addListSelectionListener(new TratarSelecaoTabela(origem));

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabela, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1100, 470));

    guardarTabela(origem, tabela);
    return scroll;
  }

  private JPanel criarRodapeTabela(OrigemTabela origem, String cardVoltar)
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("Seleccione uma linha para continuar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));

    JButton buttonVoltar = criarBotao("Voltar", new Dimension(145, 42));
    JButton buttonProceder = criarBotao("Proceder", new Dimension(145, 42));
    buttonProceder.setEnabled(false);

    buttonVoltar.addActionListener(tratarButtons);
    buttonProceder.addActionListener(tratarButtons);

    guardarBotoesRodape(origem, buttonVoltar, buttonProceder);

    if(cardVoltar != null || origem == OrigemTabela.FAMILIAS_DOADORAS)
      painelBotoes.add(buttonVoltar);
    painelBotoes.add(buttonProceder);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarCardFormulario()
  {
    JPanel painelExterno = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1180, 720));

    card.add(criarTopo("Dados da Transferência", "Preencha os dados finais e execute a transferência."),
        BorderLayout.NORTH);
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

  private JPanel criarCentroFormulario()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldCodigoFamiliaDoadora = criarCampoFormulario(false);
    fieldCodigoCotaDoadora = criarCampoFormulario(false);
    fieldCodigoFamiliaReceptora = criarCampoFormulario(false);
    fieldCodigoCotaReceptora = criarCampoFormulario(false);
    fieldVolumeTransferido = criarCampoFormulario(true);
    fieldValidadeTransferencia = criarCampoFormulario(false);
    fieldValidadeTransferencia.setText("Automatica - fim da validade da cota semanal");

    areaMotivo = new JTextArea(5, 20);
    areaMotivo.setLineWrap(true);
    areaMotivo.setWrapStyleWord(true);
    areaMotivo.setFont(InterfaceGraficaUtils.FONT_CAMPO);

    JScrollPane scrollMotivo = new JScrollPane(areaMotivo);
    scrollMotivo.setPreferredSize(new Dimension(320, 110));

    int linha = 0;
    adicionarLinhaFormulario(painel, linha++, "Família doadora:", fieldCodigoFamiliaDoadora);
    adicionarLinhaFormulario(painel, linha++, "Cota doadora:", fieldCodigoCotaDoadora);
    adicionarLinhaFormulario(painel, linha++, "Família receptora:", fieldCodigoFamiliaReceptora);
    adicionarLinhaFormulario(painel, linha++, "Cota receptora:", fieldCodigoCotaReceptora);
    adicionarLinhaFormulario(painel, linha++, "Volume transferido:", fieldVolumeTransferido);
    adicionarLinhaFormulario(painel, linha++, "Validade:", fieldValidadeTransferencia);
    adicionarLinhaFormulario(painel, linha++, "Motivo:", scrollMotivo);

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
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("Confira os dados antes de executar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));

    buttonVoltarFormulario = criarBotao("Voltar", new Dimension(145, 42));
    buttonExecutarTransferencia = criarBotao("Executar", new Dimension(145, 42));

    buttonVoltarFormulario.addActionListener(tratarButtons);
    buttonExecutarTransferencia.addActionListener(tratarButtons);

    painelBotoes.add(buttonVoltarFormulario);
    painelBotoes.add(buttonExecutarTransferencia);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JTextField criarCampoFormulario(boolean editavel)
  {
    JTextField campo = InterfaceGraficaUtils.criarCampoTexto(20, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(320, 36));
    campo.setEditable(editavel);
    return campo;
  }

  private void adicionarLinhaFormulario(JPanel painel, int linha, String textoLabel, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    JLabel label = InterfaceGraficaUtils.criarLabel(textoLabel, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO);

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

  private JButton criarBotao(String texto, Dimension tamanho)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, tamanho);
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  // ========================== FIM DO CODIGO DE INTERFACE GRAFICA
  // ==========================

  // ========================== INICIO DA LOGICA DA INTERFACE GRAFICA
  // ==========================

  private void guardarPesquisa(OrigemTabela origem, JTextField fieldPesquisa, JComboBox<String> comboCriterio,
      JButton buttonActualizar, JButton buttonLimpar)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        fieldPesquisaFamiliasDoadoras = fieldPesquisa;
        comboCriterioFamiliasDoadoras = comboCriterio;
        buttonActualizarFamiliasDoadoras = buttonActualizar;
        buttonLimparFamiliasDoadoras = buttonLimpar;
        break;
      case COTAS_DOADORAS:
        fieldPesquisaCotasDoadoras = fieldPesquisa;
        buttonActualizarCotasDoadoras = buttonActualizar;
        buttonLimparCotasDoadoras = buttonLimpar;
        break;
      case FAMILIAS_RECEPTORAS:
        fieldPesquisaFamiliasReceptoras = fieldPesquisa;
        comboCriterioFamiliasReceptoras = comboCriterio;
        buttonActualizarFamiliasReceptoras = buttonActualizar;
        buttonLimparFamiliasReceptoras = buttonLimpar;
        break;
      case COTAS_RECEPTORAS:
        fieldPesquisaCotasReceptoras = fieldPesquisa;
        buttonActualizarCotasReceptoras = buttonActualizar;
        buttonLimparCotasReceptoras = buttonLimpar;
        break;
    }
  }

  private boolean isPesquisaFamilia(OrigemTabela origem)
  {
    return origem == OrigemTabela.FAMILIAS_DOADORAS || origem == OrigemTabela.FAMILIAS_RECEPTORAS;
  }

  private void guardarTabela(OrigemTabela origem, JTable tabela)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        tabelaFamiliasDoadoras = tabela;
        break;
      case COTAS_DOADORAS:
        tabelaCotasDoadoras = tabela;
        break;
      case FAMILIAS_RECEPTORAS:
        tabelaFamiliasReceptoras = tabela;
        break;
      case COTAS_RECEPTORAS:
        tabelaCotasReceptoras = tabela;
        break;
    }
  }

  private void guardarBotoesRodape(OrigemTabela origem, JButton buttonVoltar, JButton buttonProceder)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        buttonVoltarDashboard = buttonVoltar;
        buttonProcederFamiliaDoadora = buttonProceder;
        break;
      case COTAS_DOADORAS:
        buttonVoltarCotasDoadoras = buttonVoltar;
        buttonProcederCotaDoadora = buttonProceder;
        break;
      case FAMILIAS_RECEPTORAS:
        buttonVoltarFamiliasReceptoras = buttonVoltar;
        buttonProcederFamiliaReceptora = buttonProceder;
        break;
      case COTAS_RECEPTORAS:
        buttonVoltarCotasReceptoras = buttonVoltar;
        buttonProcederCotaReceptora = buttonProceder;
        break;
    }
  }

  private void carregarFamiliasDoadoras()
  {
    carregarTabela(OrigemTabela.FAMILIAS_DOADORAS, TransferenciaCotaSQL.VISUALIZAR_FAMILIAS, null);
  }

  private void carregarFamiliasReceptoras()
  {
    carregarTabela(OrigemTabela.FAMILIAS_RECEPTORAS, TransferenciaCotaSQL.VISUALIZAR_FAMILIAS, null);
  }

  private void carregarCotasDoadoras()
  {
    carregarTabela(OrigemTabela.COTAS_DOADORAS, TransferenciaCotaSQL.VISUALIZAR_COTAS_FAMILIA,
        Integer.parseInt(fieldCodigoFamiliaDoadora.getText().trim()));
  }

  private void carregarCotasReceptoras()
  {
    carregarTabela(OrigemTabela.COTAS_RECEPTORAS, TransferenciaCotaSQL.VISUALIZAR_COTAS_FAMILIA,
        Integer.parseInt(fieldCodigoFamiliaReceptora.getText().trim()));
  }

  private void carregarTabela(OrigemTabela origem, String sql, Integer parametro)
  {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try
    {
      ps = connection.prepareStatement(sql);
      if(parametro != null)
        ps.setInt(1, parametro.intValue());

      rs = ps.executeQuery();
      DefaultTableModel modelo = InterfaceGraficaUtils.criarModeloTabela(rs);
      JTable tabela = getTabela(origem);
      tabela.setModel(modelo);

      TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(modelo);
      tabela.setRowSorter(sorter);

      guardarModeloESorter(origem, modelo, sorter);
      InterfaceGraficaUtils.ajustarLarguraColunas(tabela, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER);
      aplicarFiltro(origem);
      limparSelecao(origem);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os dados.\n" + MensagensInterface.formatarErro(ex));
    } finally
    {
      fechar(rs, ps);
    }
  }

  private void guardarModeloESorter(OrigemTabela origem, DefaultTableModel modelo,
      TableRowSorter<DefaultTableModel> sorter)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        modeloFamiliasDoadoras = modelo;
        sorterFamiliasDoadoras = sorter;
        break;
      case COTAS_DOADORAS:
        modeloCotasDoadoras = modelo;
        sorterCotasDoadoras = sorter;
        break;
      case FAMILIAS_RECEPTORAS:
        modeloFamiliasReceptoras = modelo;
        sorterFamiliasReceptoras = sorter;
        break;
      case COTAS_RECEPTORAS:
        modeloCotasReceptoras = modelo;
        sorterCotasReceptoras = sorter;
        break;
    }
  }

  private JTable getTabela(OrigemTabela origem)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        return tabelaFamiliasDoadoras;
      case COTAS_DOADORAS:
        return tabelaCotasDoadoras;
      case FAMILIAS_RECEPTORAS:
        return tabelaFamiliasReceptoras;
      case COTAS_RECEPTORAS:
        return tabelaCotasReceptoras;
      default:
        return tabelaFamiliasDoadoras;
    }
  }

  private DefaultTableModel getModelo(OrigemTabela origem)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        return modeloFamiliasDoadoras;
      case COTAS_DOADORAS:
        return modeloCotasDoadoras;
      case FAMILIAS_RECEPTORAS:
        return modeloFamiliasReceptoras;
      case COTAS_RECEPTORAS:
        return modeloCotasReceptoras;
      default:
        return modeloFamiliasDoadoras;
    }
  }

  private TableRowSorter<DefaultTableModel> getSorter(OrigemTabela origem)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        return sorterFamiliasDoadoras;
      case COTAS_DOADORAS:
        return sorterCotasDoadoras;
      case FAMILIAS_RECEPTORAS:
        return sorterFamiliasReceptoras;
      case COTAS_RECEPTORAS:
        return sorterCotasReceptoras;
      default:
        return sorterFamiliasDoadoras;
    }
  }

  private JTextField getFieldPesquisa(OrigemTabela origem)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        return fieldPesquisaFamiliasDoadoras;
      case COTAS_DOADORAS:
        return fieldPesquisaCotasDoadoras;
      case FAMILIAS_RECEPTORAS:
        return fieldPesquisaFamiliasReceptoras;
      case COTAS_RECEPTORAS:
        return fieldPesquisaCotasReceptoras;
      default:
        return fieldPesquisaFamiliasDoadoras;
    }
  }

  private JComboBox<String> getComboCriterioFamilia(OrigemTabela origem)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        return comboCriterioFamiliasDoadoras;
      case FAMILIAS_RECEPTORAS:
        return comboCriterioFamiliasReceptoras;
      default:
        return null;
    }
  }

  private void aplicarFiltro(OrigemTabela origem)
  {
    TableRowSorter<DefaultTableModel> sorter = getSorter(origem);
    DefaultTableModel modelo = getModelo(origem);
    JTextField fieldPesquisa = getFieldPesquisa(origem);

    if(sorter == null || modelo == null || fieldPesquisa == null)
      return;

    String texto = fieldPesquisa.getText().trim();
    if(texto.isEmpty())
    {
      sorter.setRowFilter(null);
      return;
    }

    int coluna = InterfaceGraficaUtils.encontrarIndiceColuna(modelo, getNomeColunaPesquisa(origem));
    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private String getNomeColunaPesquisa(OrigemTabela origem)
  {
    if(isPesquisaFamilia(origem))
    {
      JComboBox<String> comboCriterio = getComboCriterioFamilia(origem);
      if(comboCriterio != null)
      {
        String criterio = comboCriterio.getSelectedItem().toString();
        if("Nome do Responsável".equals(criterio))
          return "nome_responsavel_fb";
        if("Telefone".equals(criterio))
          return "contacto_fb";
      }

      return "codigo_fb";
    }

    switch(origem)
    {
      case COTAS_DOADORAS:
      case COTAS_RECEPTORAS:
        return "codigo_cota";
      default:
        return "codigo_fb";
    }
  }

  private void limparFiltro(OrigemTabela origem)
  {
    JTextField fieldPesquisa = getFieldPesquisa(origem);
    if(fieldPesquisa != null)
      fieldPesquisa.setText("");

    JComboBox<String> comboCriterio = getComboCriterioFamilia(origem);
    if(comboCriterio != null)
      comboCriterio.setSelectedIndex(0);

    aplicarFiltro(origem);
  }

  private void limparSelecao(OrigemTabela origem)
  {
    JTable tabela = getTabela(origem);
    if(tabela != null)
      tabela.clearSelection();

    JButton buttonProceder = getButtonProceder(origem);
    if(buttonProceder != null)
      buttonProceder.setEnabled(false);
  }

  private JButton getButtonProceder(OrigemTabela origem)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        return buttonProcederFamiliaDoadora;
      case COTAS_DOADORAS:
        return buttonProcederCotaDoadora;
      case FAMILIAS_RECEPTORAS:
        return buttonProcederFamiliaReceptora;
      case COTAS_RECEPTORAS:
        return buttonProcederCotaReceptora;
      default:
        return null;
    }
  }

  private String getCodigoSelecionado(OrigemTabela origem, String coluna)
  {
    JTable tabela = getTabela(origem);
    DefaultTableModel modelo = getModelo(origem);

    if(tabela == null || modelo == null || tabela.getSelectedRow() == -1)
      return null;

    int linhaModel = tabela.convertRowIndexToModel(tabela.getSelectedRow());
    int indiceColuna = InterfaceGraficaUtils.encontrarIndiceColuna(modelo, coluna);
    Object valor = modelo.getValueAt(linhaModel, indiceColuna);

    return valor == null ? null : valor.toString();
  }

  private void procederFamiliaDoadora()
  {
    String codigo = getCodigoSelecionado(OrigemTabela.FAMILIAS_DOADORAS, "codigo_fb");
    if(codigo == null)
    {
      JOptionPane.showMessageDialog(this, "Seleccione uma família doadora.");
      return;
    }

    fieldCodigoFamiliaDoadora.setText(codigo);
    carregarCotasDoadoras();
    cardLayout.show(panelCards, CARD_COTA_DOADORA);
  }

  private void procederCotaDoadora()
  {
    String codigo = getCodigoSelecionado(OrigemTabela.COTAS_DOADORAS, "codigo_cota");
    if(codigo == null)
    {
      JOptionPane.showMessageDialog(this, "Seleccione uma cota doadora.");
      return;
    }

    fieldCodigoCotaDoadora.setText(codigo);
    carregarFamiliasReceptoras();
    cardLayout.show(panelCards, CARD_FAMILIA_RECEPTORA);
  }

  private void procederFamiliaReceptora()
  {
    String codigo = getCodigoSelecionado(OrigemTabela.FAMILIAS_RECEPTORAS, "codigo_fb");
    if(codigo == null)
    {
      JOptionPane.showMessageDialog(this, "Seleccione uma família receptora.");
      return;
    }

    if(codigo.equals(fieldCodigoFamiliaDoadora.getText().trim()))
    {
      JOptionPane.showMessageDialog(this, "A família receptora deve ser diferente da família doadora.");
      return;
    }

    fieldCodigoFamiliaReceptora.setText(codigo);
    carregarCotasReceptoras();
    cardLayout.show(panelCards, CARD_COTA_RECEPTORA);
  }

  private void procederCotaReceptora()
  {
    String codigo = getCodigoSelecionado(OrigemTabela.COTAS_RECEPTORAS, "codigo_cota");
    if(codigo == null)
    {
      JOptionPane.showMessageDialog(this, "Seleccione uma cota receptora.");
      return;
    }

    fieldCodigoCotaReceptora.setText(codigo);
    fieldValidadeTransferencia.setText("Automatica - fim da validade da cota semanal seleccionada");
    cardLayout.show(panelCards, CARD_FORMULARIO);
  }

  private boolean validarFormulario()
  {
    if(fieldCodigoFamiliaDoadora.getText().trim().isEmpty() || fieldCodigoCotaDoadora.getText().trim().isEmpty()
        || fieldCodigoFamiliaReceptora.getText().trim().isEmpty()
        || fieldCodigoCotaReceptora.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Faltam dados seleccionados para a transferência.");
      return false;
    }

    if(fieldVolumeTransferido.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Informe o volume transferido.");
      return false;
    }

    try
    {
      double volume = Double.parseDouble(fieldVolumeTransferido.getText().trim());
      if(volume <= 0)
      {
        JOptionPane.showMessageDialog(this, "O volume transferido deve ser maior que zero.");
        return false;
      }
    } catch(NumberFormatException ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return false;

      JOptionPane.showMessageDialog(this, "Informe um volume transferido válido.");
      return false;
    }

    if(areaMotivo.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Informe o motivo da transferência.");
      return false;
    }

    return true;
  }

  private void executarTransferencia()
  {
    CallableStatement cs = null;

    try
    {
      cs = connection.prepareCall(TransferenciaCotaSQL.TRANSFERIR_COTA);

      cs.setInt(1, Integer.parseInt(fieldCodigoFamiliaDoadora.getText().trim()));
      cs.setInt(2, Integer.parseInt(fieldCodigoCotaDoadora.getText().trim()));
      cs.setInt(3, Integer.parseInt(fieldCodigoFamiliaReceptora.getText().trim()));
      cs.setInt(4, Integer.parseInt(fieldCodigoCotaReceptora.getText().trim()));
      cs.setDouble(5, Double.parseDouble(fieldVolumeTransferido.getText().trim()));
      cs.setString(6, areaMotivo.getText().trim());
      cs.registerOutParameter(7, Types.NUMERIC);
      cs.registerOutParameter(8, Types.VARCHAR);

      cs.execute();

      int codigoTransferencia = cs.getInt(7);
      boolean transferenciaFalhou = cs.wasNull();
      String mensagem = cs.getString(8);

      if(transferenciaFalhou)
      {
        JOptionPane.showMessageDialog(this, "Transferência não executada.\nMensagem: " + mensagem);
        return;
      }

      JOptionPane.showMessageDialog(this,
          "Transferência executada." + "\nCódigo da transferência: " + codigoTransferencia + "\nMensagem: " + mensagem);

      limparTudo();
      carregarFamiliasDoadoras();
      cardLayout.show(panelCards, CARD_FAMILIA_DOADORA);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível executar transferência:\n" + MensagensInterface.formatarErro(ex));
    } finally
    {
      try
      {
        if(cs != null)
          cs.close();
      } catch(Exception ignored)
      {}
    }
  }

  private void limparTudo()
  {
    fieldCodigoFamiliaDoadora.setText("");
    fieldCodigoCotaDoadora.setText("");
    fieldCodigoFamiliaReceptora.setText("");
    fieldCodigoCotaReceptora.setText("");
    fieldVolumeTransferido.setText("");
    fieldValidadeTransferencia.setText("Automatica - fim da validade da cota semanal");
    areaMotivo.setText("");

    limparFiltro(OrigemTabela.FAMILIAS_DOADORAS);
    limparFiltro(OrigemTabela.COTAS_DOADORAS);
    limparFiltro(OrigemTabela.FAMILIAS_RECEPTORAS);
    limparFiltro(OrigemTabela.COTAS_RECEPTORAS);
  }

  private void actualizarTabela(OrigemTabela origem)
  {
    switch(origem)
    {
      case FAMILIAS_DOADORAS:
        carregarFamiliasDoadoras();
        break;
      case COTAS_DOADORAS:
        if(!fieldCodigoFamiliaDoadora.getText().trim().isEmpty())
          carregarCotasDoadoras();
        break;
      case FAMILIAS_RECEPTORAS:
        carregarFamiliasReceptoras();
        break;
      case COTAS_RECEPTORAS:
        if(!fieldCodigoFamiliaReceptora.getText().trim().isEmpty())
          carregarCotasReceptoras();
        break;
    }
  }

  private void fechar(ResultSet rs, PreparedStatement ps)
  {
    try
    {
      if(rs != null)
        rs.close();
    } catch(Exception ignored)
    {}

    try
    {
      if(ps != null)
        ps.close();
    } catch(Exception ignored)
    {}
  }

  private enum OrigemTabela
  {
    FAMILIAS_DOADORAS, COTAS_DOADORAS, FAMILIAS_RECEPTORAS, COTAS_RECEPTORAS
  }

  private class TratarSelecaoTabela implements ListSelectionListener
  {
    private final OrigemTabela origem;

    public TratarSelecaoTabela(OrigemTabela origem)
    {
      this.origem = origem;
    }

    @Override
    public void valueChanged(ListSelectionEvent event)
    {
      if(!event.getValueIsAdjusting())
      {
        JButton buttonProceder = getButtonProceder(origem);
        JTable tabela = getTabela(origem);
        if(buttonProceder != null && tabela != null)
          buttonProceder.setEnabled(tabela.getSelectedRow() != -1);
      }
    }
  }

  private class TratarPesquisa implements DocumentListener
  {
    private final OrigemTabela origem;

    public TratarPesquisa(OrigemTabela origem)
    {
      this.origem = origem;
    }

    @Override
    public void insertUpdate(DocumentEvent event)
    {
      aplicarFiltro(origem);
    }

    @Override
    public void removeUpdate(DocumentEvent event)
    {
      aplicarFiltro(origem);
    }

    @Override
    public void changedUpdate(DocumentEvent event)
    {
      aplicarFiltro(origem);
    }
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent event)
    {
      Object source = event.getSource();

      if(source == buttonProcederFamiliaDoadora)
      {
        procederFamiliaDoadora();
        return;
      }

      if(source == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }

      if(source == buttonProcederCotaDoadora)
      {
        procederCotaDoadora();
        return;
      }

      if(source == buttonProcederFamiliaReceptora)
      {
        procederFamiliaReceptora();
        return;
      }

      if(source == buttonProcederCotaReceptora)
      {
        procederCotaReceptora();
        return;
      }

      if(source == buttonVoltarCotasDoadoras)
      {
        cardLayout.show(panelCards, CARD_FAMILIA_DOADORA);
        return;
      }

      if(source == buttonVoltarFamiliasReceptoras)
      {
        cardLayout.show(panelCards, CARD_COTA_DOADORA);
        return;
      }

      if(source == buttonVoltarCotasReceptoras)
      {
        cardLayout.show(panelCards, CARD_FAMILIA_RECEPTORA);
        return;
      }

      if(source == buttonVoltarFormulario)
      {
        cardLayout.show(panelCards, CARD_COTA_RECEPTORA);
        return;
      }

      if(source == buttonExecutarTransferencia)
      {
        if(validarFormulario())
          executarTransferencia();
        return;
      }

      tratarBotoesTabela(source);
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }

  private void tratarBotoesTabela(Object source)
  {
    if(source == buttonActualizarFamiliasDoadoras)
      actualizarTabela(OrigemTabela.FAMILIAS_DOADORAS);
    else if(source == buttonLimparFamiliasDoadoras)
      limparFiltro(OrigemTabela.FAMILIAS_DOADORAS);
    else if(source == buttonActualizarCotasDoadoras)
      actualizarTabela(OrigemTabela.COTAS_DOADORAS);
    else if(source == buttonLimparCotasDoadoras)
      limparFiltro(OrigemTabela.COTAS_DOADORAS);
    else if(source == buttonActualizarFamiliasReceptoras)
      actualizarTabela(OrigemTabela.FAMILIAS_RECEPTORAS);
    else if(source == buttonLimparFamiliasReceptoras)
      limparFiltro(OrigemTabela.FAMILIAS_RECEPTORAS);
    else if(source == buttonActualizarCotasReceptoras)
      actualizarTabela(OrigemTabela.COTAS_RECEPTORAS);
    else if(source == buttonLimparCotasReceptoras)
      limparFiltro(OrigemTabela.COTAS_RECEPTORAS);
    else if(source == comboCriterioFamiliasDoadoras)
      aplicarFiltro(OrigemTabela.FAMILIAS_DOADORAS);
    else if(source == comboCriterioFamiliasReceptoras)
      aplicarFiltro(OrigemTabela.FAMILIAS_RECEPTORAS);
  }

  // =========================== FIM DA LOGICA DA INTERFACE GRAFICA
  // ===========================
}
