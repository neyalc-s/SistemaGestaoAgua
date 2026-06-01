package View_Interface_FamiliasCotasDB;

import Repository_SQL.FamiliasCotasDB.ActualizarDadosFamiliaSQL;
import Repository_SQL.FamiliasCotasDB.ActualizarDadosFamiliaSQL.DadosActualizacao;
import Repository_SQL.FamiliasCotasDB.ActualizarDadosFamiliaSQL.DadosFamilia;
import Repository_SQL.FamiliasCotasDB.ActualizarDadosFamiliaSQL.ResultadoOperacao;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

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

public class JPanel_ActualizarDadosFamilia extends JPanel
{
  private static final String CARD_FAMILIAS = "famílias";
  private static final String CARD_DADOS = "dados";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final CardLayout cardLayout = new CardLayout();
  private final JPanel panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaFamilias;
  private DefaultTableModel modeloFamilias;
  private TableRowSorter<DefaultTableModel> sorterFamilias;
  private JTextField fieldPesquisaFamilias;
  private JComboBox<String> comboPesquisaFamilias;
  private JButton buttonActualizarFamilias;
  private JButton buttonAvancar;
  private JButton buttonVoltarDashboard;

  private JTextField fieldCodigoFamilia;
  private JTextField fieldNomeResponsavel;
  private JTextField fieldNumeroMembros;
  private JComboBox<String> comboPerfilSocioeconomico;
  private JTextField fieldContacto;
  private JComboBox<String> comboEstadoFamilia;
  private JTextField fieldAldeia;
  private JTextField fieldCoordenadasGps;

  private JCheckBox checkNomeResponsavel;
  private JCheckBox checkNumeroMembros;
  private JCheckBox checkPerfilSocioeconomico;
  private JCheckBox checkContacto;
  private JCheckBox checkEstadoFamilia;
  private JCheckBox checkAldeia;
  private JCheckBox checkCoordenadasGps;

  private String nomeResponsavelOriginal = "";
  private String numeroMembrosOriginal = "";
  private String perfilSocioeconomicoOriginal = "";
  private String contactoOriginal = "";
  private String estadoFamiliaOriginal = "";
  private String aldeiaOriginal = "";
  private String coordenadasGpsOriginal = "";

  private JButton buttonVoltar;
  private JButton buttonActualizarDados;

  public JPanel_ActualizarDadosFamilia(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_ActualizarDadosFamilia(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_ActualizarDadosFamilia(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_ActualizarDadosFamilia(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));

    panelCards.add(criarCardFamilias(), CARD_FAMILIAS);
    panelCards.add(criarCardDados(), CARD_DADOS);
    add(panelCards, BorderLayout.CENTER);

    if(carregarTabelasAutomaticamente)
    {
      carregarFamilias();
    }
  }

  private JPanel criarCardFamilias()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Actualizar Dados da Família",
        "<html>Seleccione a família cujos dados principais pretende actualizar.</html>");
    card.add(criarCentroFamilias(), BorderLayout.CENTER);
    card.add(criarRodapeFamilias(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardDados()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Dados Principais da Família",
        "<html>Active os campos que pretende modificar. Campos não seleccionados permanecem bloqueados.</html>");
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

  private JPanel criarCentroFamilias()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    painel.add(criarPesquisaFamilias(), BorderLayout.NORTH);

    modeloFamilias = new DefaultTableModel(new String[]
      {
          "Código Família", "Responsável", "Contacto", "Membros", "Estado", "Aldeia", "Coordenadas GPS"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
    tabelaFamilias = criarTabela(modeloFamilias);
    tabelaFamilias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterFamilias = new TableRowSorter<DefaultTableModel>(modeloFamilias);
    tabelaFamilias.setRowSorter(sorterFamilias);
    painel.add(criarScroll(tabelaFamilias, new Dimension(1120, 500)), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarCentroDados()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldCodigoFamilia = criarCampoReadonly();
    fieldNomeResponsavel = criarCampoEditavelBloqueado();
    fieldNumeroMembros = criarCampoEditavelBloqueado();
    comboPerfilSocioeconomico = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Baixa renda", "Media renda", "Alta renda"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(360, 36));
    comboPerfilSocioeconomico.setEnabled(false);
    fieldContacto = criarCampoEditavelBloqueado();
    comboEstadoFamilia = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Activo", "Inactivo"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(360, 36));
    comboEstadoFamilia.setEnabled(false);
    fieldAldeia = criarCampoEditavelBloqueado();
    fieldCoordenadasGps = criarCampoEditavelBloqueado();

    checkNomeResponsavel = criarCheck("Actualizar");
    checkNumeroMembros = criarCheck("Actualizar");
    checkPerfilSocioeconomico = criarCheck("Actualizar");
    checkContacto = criarCheck("Actualizar");
    checkEstadoFamilia = criarCheck("Actualizar");
    checkAldeia = criarCheck("Actualizar");
    checkCoordenadasGps = criarCheck("Actualizar");

    checkNomeResponsavel.addActionListener(tratarButtons);
    checkNumeroMembros.addActionListener(tratarButtons);
    checkPerfilSocioeconomico.addActionListener(tratarButtons);
    checkContacto.addActionListener(tratarButtons);
    checkEstadoFamilia.addActionListener(tratarButtons);
    checkAldeia.addActionListener(tratarButtons);
    checkCoordenadasGps.addActionListener(tratarButtons);

    int linha = 0;
    adicionarLinhaDados(painel, linha++, "Código da família:", fieldCodigoFamilia, null);
    adicionarLinhaDados(painel, linha++, "Aldeia:", fieldAldeia, checkAldeia);
    adicionarLinhaDados(painel, linha++, "Coordenadas GPS:", fieldCoordenadasGps, checkCoordenadasGps);
    adicionarLinhaDados(painel, linha++, "Nome do responsável:", fieldNomeResponsavel, checkNomeResponsavel);
    adicionarLinhaDados(painel, linha++, "Número de membros:", fieldNumeroMembros, checkNumeroMembros);
    adicionarLinhaDados(painel, linha++, "Perfil socioeconómico:", comboPerfilSocioeconomico,
        checkPerfilSocioeconomico);
    adicionarLinhaDados(painel, linha++, "Contacto:", fieldContacto, checkContacto);
    adicionarLinhaDados(painel, linha++, "Estado da família:", comboEstadoFamilia, checkEstadoFamilia);

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = linha;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);
    return painel;
  }

  private JPanel criarPesquisaFamilias()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    fieldPesquisaFamilias = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboPesquisaFamilias = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Família", "Responsável", "Contacto", "Estado", "Aldeia"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(180, 36));
    buttonActualizarFamilias = criarBotao("Mostrar Todos", 160, 36);

    fieldPesquisaFamilias.getDocument().addDocumentListener(new TratarPesquisaFamilias());
    comboPesquisaFamilias.addActionListener(tratarButtons);
    buttonActualizarFamilias.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisaFamilias);
    painel.add(comboPesquisaFamilias);
    painel.add(buttonActualizarFamilias);
    return painel;
  }

  private JPanel criarRodapeFamilias()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Seleccione uma família para continuar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar", 130, 42);
    buttonAvancar = criarBotao("Proceder", 145, 42);
    buttonVoltarDashboard.addActionListener(tratarButtons);
    buttonAvancar.addActionListener(tratarButtons);
    botoes.add(buttonVoltarDashboard);
    botoes.add(buttonAvancar);

    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarRodapeDados()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Ao alterar membros, a cota semanal actual não muda.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltar = criarBotao("Voltar", 130, 42);
    buttonActualizarDados = criarBotao("Actualizar", 145, 42);
    buttonVoltar.addActionListener(tratarButtons);
    buttonActualizarDados.addActionListener(tratarButtons);
    botoes.add(buttonVoltar);
    botoes.add(buttonActualizarDados);

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

  private JTextField criarCampoReadonly()
  {
    JTextField campo = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(360, 36));
    campo.setEditable(false);
    return campo;
  }

  private JTextField criarCampoEditavelBloqueado()
  {
    JTextField campo = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(360, 36));
    campo.setEditable(false);
    return campo;
  }

  private JCheckBox criarCheck(String texto)
  {
    JCheckBox check = new JCheckBox(texto);
    check.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    check.setForeground(InterfaceGraficaUtils.COR_TEXTO);
    check.setOpaque(false);
    check.setFocusPainted(false);
    return check;
  }

  private JButton criarBotao(String texto, int largura, int altura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, altura));
    IconesInterface.aplicarIconeBotao(botao);
    Resources.IconesInterface.aplicarEfeitoHoverBotaoFamilias(botao);
    return botao;
  }

  private void adicionarLinhaDados(JPanel painel, int linha, String textoLabel, JComponent campo, JCheckBox check)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(10, 0, 10, 18);
    painel.add(InterfaceGraficaUtils.criarLabel(textoLabel, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 0, 10, 12);
    painel.add(campo, gbc);

    if(check != null)
    {
      gbc = new GridBagConstraints();
      gbc.gridx = 2;
      gbc.gridy = linha;
      gbc.anchor = GridBagConstraints.WEST;
      gbc.insets = new Insets(10, 0, 10, 0);
      painel.add(check, gbc);
    }
  }

  private void carregarFamilias()
  {
    try
    {
      ActualizarDadosFamiliaSQL.carregarFamilias(connection, modeloFamilias);
      aplicarFiltroFamilias();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar as famílias.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarDadosFamiliaSelecionada()
  {
    Integer codigo = getCodigoFamiliaSelecionada();
    if(codigo == null)
    {
      JOptionPane.showMessageDialog(this, "Seleccione uma família.");
      return;
    }

    try
    {
      DadosFamilia dados = ActualizarDadosFamiliaSQL.carregarDadosFamilia(connection, codigo.intValue());
      guardarValoresOriginais(dados);
      fieldCodigoFamilia.setText(String.valueOf(dados.codigoFamilia));
      reporValoresOriginais();
      bloquearCamposEdicao();
      cardLayout.show(panelCards, CARD_DADOS);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar dados da família:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private String valor(String texto)
  {
    return texto == null ? "" : texto;
  }

  private Integer getCodigoFamiliaSelecionada()
  {
    int linhaView = tabelaFamilias.getSelectedRow();
    if(linhaView == -1)
      return null;

    int linhaModel = tabelaFamilias.convertRowIndexToModel(linhaView);
    return Integer.valueOf(modeloFamilias.getValueAt(linhaModel, 0).toString());
  }

  private void bloquearCamposEdicao()
  {
    checkNomeResponsavel.setSelected(false);
    checkNumeroMembros.setSelected(false);
    checkPerfilSocioeconomico.setSelected(false);
    checkContacto.setSelected(false);
    checkEstadoFamilia.setSelected(false);
    checkAldeia.setSelected(false);
    checkCoordenadasGps.setSelected(false);
    reporValoresOriginais();
    actualizarEstadoCampos();
  }

  private void actualizarEstadoCampos()
  {
    if(!checkNomeResponsavel.isSelected())
      fieldNomeResponsavel.setText(nomeResponsavelOriginal);
    if(!checkNumeroMembros.isSelected())
      fieldNumeroMembros.setText(numeroMembrosOriginal);
    if(!checkPerfilSocioeconomico.isSelected())
      seleccionarPerfilSocioeconomico(perfilSocioeconomicoOriginal);
    if(!checkContacto.isSelected())
      fieldContacto.setText(contactoOriginal);
    if(!checkEstadoFamilia.isSelected())
      seleccionarEstadoFamilia(estadoFamiliaOriginal);
    if(!checkAldeia.isSelected())
      fieldAldeia.setText(aldeiaOriginal);
    if(!checkCoordenadasGps.isSelected())
      fieldCoordenadasGps.setText(coordenadasGpsOriginal);

    fieldNomeResponsavel.setEditable(checkNomeResponsavel.isSelected());
    fieldNumeroMembros.setEditable(checkNumeroMembros.isSelected());
    comboPerfilSocioeconomico.setEnabled(checkPerfilSocioeconomico.isSelected());
    fieldContacto.setEditable(checkContacto.isSelected());
    comboEstadoFamilia.setEnabled(checkEstadoFamilia.isSelected());
    fieldAldeia.setEditable(checkAldeia.isSelected());
    fieldCoordenadasGps.setEditable(checkCoordenadasGps.isSelected());
  }

  private void guardarValoresOriginais(DadosFamilia dados)
  {
    nomeResponsavelOriginal = valor(dados.nomeResponsavel);
    numeroMembrosOriginal = String.valueOf(dados.numeroMembros);
    perfilSocioeconomicoOriginal = valor(dados.perfilSocioeconomico);
    contactoOriginal = valor(dados.contacto);
    estadoFamiliaOriginal = valor(dados.estado);
    aldeiaOriginal = valor(dados.aldeia);
    coordenadasGpsOriginal = valor(dados.coordenadasGps);
  }

  private void reporValoresOriginais()
  {
    fieldNomeResponsavel.setText(nomeResponsavelOriginal);
    fieldNumeroMembros.setText(numeroMembrosOriginal);
    seleccionarPerfilSocioeconomico(perfilSocioeconomicoOriginal);
    fieldContacto.setText(contactoOriginal);
    seleccionarEstadoFamilia(estadoFamiliaOriginal);
    fieldAldeia.setText(aldeiaOriginal);
    fieldCoordenadasGps.setText(coordenadasGpsOriginal);
  }

  private void seleccionarPerfilSocioeconomico(String perfil)
  {
    if(perfil == null)
    {
      comboPerfilSocioeconomico.setSelectedIndex(0);
      return;
    }

    ComboBoxModel<String> modelo = comboPerfilSocioeconomico.getModel();
    for(int i = 0; i < modelo.getSize(); i++)
    {
      if(modelo.getElementAt(i).equalsIgnoreCase(perfil.trim()))
      {
        comboPerfilSocioeconomico.setSelectedIndex(i);
        return;
      }
    }

    comboPerfilSocioeconomico.setSelectedIndex(0);
  }

  private void seleccionarEstadoFamilia(String estado)
  {
    if(estado == null)
    {
      comboEstadoFamilia.setSelectedIndex(0);
      return;
    }

    ComboBoxModel<String> modelo = comboEstadoFamilia.getModel();
    for(int i = 0; i < modelo.getSize(); i++)
    {
      if(modelo.getElementAt(i).equalsIgnoreCase(estado.trim()))
      {
        comboEstadoFamilia.setSelectedIndex(i);
        return;
      }
    }

    comboEstadoFamilia.setSelectedIndex(0);
  }

  private void aplicarFiltroFamilias()
  {
    if(sorterFamilias == null || fieldPesquisaFamilias == null)
      return;

    String texto = fieldPesquisaFamilias.getText().trim();
    if(texto.isEmpty())
    {
      sorterFamilias.setRowFilter(null);
      return;
    }

    sorterFamilias.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), getIndiceFiltroFamilias()));
  }

  private int getIndiceFiltroFamilias()
  {
    String criterio = comboPesquisaFamilias.getSelectedItem().toString();
    if("Responsável".equals(criterio))
      return 1;
    if("Contacto".equals(criterio))
      return 2;
    if("Estado".equals(criterio))
      return 4;
    if("Aldeia".equals(criterio))
      return 5;
    return 0;
  }

  private void mostrarResumoActualizacao()
  {
    if(!checkNomeResponsavel.isSelected() && !checkNumeroMembros.isSelected() && !checkPerfilSocioeconomico.isSelected()
        && !checkContacto.isSelected() && !checkEstadoFamilia.isSelected() && !checkAldeia.isSelected()
        && !checkCoordenadasGps.isSelected())
    {
      JOptionPane.showMessageDialog(this, "Seleccione pelo menos um campo para actualizar.");
      return;
    }

    try
    {
      DadosActualizacao dados = criarDadosActualizacao();
      ResultadoOperacao preValidacao = ActualizarDadosFamiliaSQL.preValidarActualizacao(connection, dados);

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(preValidacao.mensagem));
        return;
      }

      String mensagemConfirmacao = "Confirma a actualização dos dados desta família?";
      if(checkNumeroMembros.isSelected())
        mensagemConfirmacao += "\n\nNota: a cota semanal actualmente valida não será alterada.";

      int opcao = JOptionPane.showConfirmDialog(this, mensagemConfirmacao, "Confirmar actualização",
          JOptionPane.YES_NO_OPTION);
      if(opcao != JOptionPane.YES_OPTION)
        return;

      ResultadoOperacao resultado = ActualizarDadosFamiliaSQL.actualizarDados(connection, dados);
      JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(resultado.mensagem));

      if(resultado.podeContinuar)
      {
        carregarFamilias();
        carregarDadosActualizados(Integer.parseInt(fieldCodigoFamilia.getText().trim()));
      }
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível actualizar dados da família:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private DadosActualizacao criarDadosActualizacao()
  {
    int codigoFamilia = Integer.parseInt(fieldCodigoFamilia.getText().trim());
    int numeroOriginal = parseNumeroMembros(numeroMembrosOriginal, "Número de membros original inválido.");
    int numeroNovo = parseNumeroMembros(fieldNumeroMembros.getText().trim(),
        "Número de membros deve ser um valor inteiro.");

    return new DadosActualizacao(
        codigoFamilia,
        nomeResponsavelOriginal,
        numeroOriginal,
        perfilSocioeconomicoOriginal,
        contactoOriginal,
        estadoFamiliaOriginal,
        aldeiaOriginal,
        coordenadasGpsOriginal,
        fieldNomeResponsavel.getText().trim(),
        numeroNovo,
        comboPerfilSocioeconomico.getSelectedItem().toString(),
        fieldContacto.getText().trim(),
        comboEstadoFamilia.getSelectedItem().toString(),
        fieldAldeia.getText().trim(),
        fieldCoordenadasGps.getText().trim(),
        checkNomeResponsavel.isSelected(),
        checkNumeroMembros.isSelected(),
        checkPerfilSocioeconomico.isSelected(),
        checkContacto.isSelected(),
        checkEstadoFamilia.isSelected(),
        checkAldeia.isSelected(),
        checkCoordenadasGps.isSelected());
  }

  private int parseNumeroMembros(String texto, String mensagemErro)
  {
    try
    {
      return Integer.parseInt(texto);
    } catch(NumberFormatException ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        throw new IllegalArgumentException(mensagemErro);

      throw new IllegalArgumentException(mensagemErro);
    }
  }

  private void carregarDadosActualizados(int codigoFamilia)
  {
    try
    {
      DadosFamilia dados = ActualizarDadosFamiliaSQL.carregarDadosFamilia(connection, codigoFamilia);
      guardarValoresOriginais(dados);
      reporValoresOriginais();
      bloquearCamposEdicao();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível recarregar dados da família:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private class TratarPesquisaFamilias implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) { aplicarFiltroFamilias(); }
    public void removeUpdate(DocumentEvent e) { aplicarFiltroFamilias(); }
    public void changedUpdate(DocumentEvent e) { aplicarFiltroFamilias(); }
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == comboPesquisaFamilias)
      {
        aplicarFiltroFamilias();
        return;
      }

      if(event.getSource() == buttonActualizarFamilias)
      {
        carregarFamilias();
        return;
      }

      if(event.getSource() == buttonAvancar)
      {
        carregarDadosFamiliaSelecionada();
        return;
      }

      if(event.getSource() == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }

      if(event.getSource() == buttonVoltar)
      {
        cardLayout.show(panelCards, CARD_FAMILIAS);
        return;
      }

      if(event.getSource() == buttonActualizarDados)
      {
        mostrarResumoActualizacao();
        return;
      }

      actualizarEstadoCampos();
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }
}
