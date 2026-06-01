package View_Interface_TransferenciasRecursosDB;

import Repository_SQL.TransferenciasRecursosDB.RegistarMedidaProteccaoSQL;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class JPanel_RegistarMedidaProteccao extends JPanel
{
  private static final String CARD_RESPONSAVEL = "responsavel";
  private static final String CARD_MEDIDA = "medida";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final java.awt.CardLayout cardLayout = new java.awt.CardLayout();
  private final JPanel panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaResponsaveis;
  private DefaultTableModel modeloResponsaveis;
  private TableRowSorter<DefaultTableModel> sorterResponsaveis;
  private JTextField fieldPesquisaResponsavel;
  private JComboBox<String> comboPesquisaResponsavel;
  private JTextField fieldCodResponsavel;
  private JTextField fieldNomeResponsavel;
  private JTextArea areaDescricaoMedida;
  private JButton buttonActualizar;
  private JButton buttonNovoResponsavel;
  private JButton buttonAvancar;
  private JButton buttonVoltarDashboard;
  private JButton buttonVoltar;
  private JButton buttonRegistar;

  public JPanel_RegistarMedidaProteccao(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_RegistarMedidaProteccao(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_RegistarMedidaProteccao(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_RegistarMedidaProteccao(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));

    panelCards.add(criarCardResponsavel(), CARD_RESPONSAVEL);
    panelCards.add(criarCardMedida(), CARD_MEDIDA);
    add(panelCards, BorderLayout.CENTER);

    if(carregarTabelasAutomaticamente)
    {
      carregarResponsaveis();
    }
  }

  private JPanel criarCardResponsavel()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Registar Medida de Protecção",
        "<html>Seleccione o responsável que coordena a nova medida de protecção.</html>");
    card.add(criarCentroResponsavel(), BorderLayout.CENTER);
    card.add(criarRodapeResponsavel(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardMedida()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Dados da Medida",
        "<html>Confirme o responsável escolhido e descreva a medida de protecção.</html>");
    card.add(criarCentroMedida(), BorderLayout.CENTER);
    card.add(criarRodapeMedida(), BorderLayout.SOUTH);
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

  private JPanel criarCentroResponsavel()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(InterfaceGraficaUtils.criarLabel("Responsáveis por Medidas de Protecção",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL), BorderLayout.NORTH);
    topoTabela.add(criarPesquisaResponsavel(), BorderLayout.SOUTH);
    painel.add(topoTabela, BorderLayout.NORTH);

    modeloResponsaveis = new DefaultTableModel(new String[]
      {
          "Código", "Nome do Responsável"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
    tabelaResponsaveis = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaResponsaveis.setModel(modeloResponsaveis);
    tabelaResponsaveis.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterResponsaveis = new TableRowSorter<DefaultTableModel>(modeloResponsaveis);
    tabelaResponsaveis.setRowSorter(sorterResponsaveis);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaResponsaveis,
        InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1120, 500));
    painel.add(scroll, BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarCentroMedida()
  {
    JPanel painelExterno = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 14));
    painelExterno.add(InterfaceGraficaUtils.criarLabel("Medida de Protecção",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL), BorderLayout.NORTH);

    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    fieldCodResponsavel = criarCampoReadonly();
    fieldNomeResponsavel = criarCampoReadonly();
    areaDescricaoMedida = new JTextArea(5, 42);
    areaDescricaoMedida.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    areaDescricaoMedida.setLineWrap(true);
    areaDescricaoMedida.setWrapStyleWord(true);

    adicionarLinha(painel, 0, "Código do responsável:", fieldCodResponsavel);
    adicionarLinha(painel, 1, "Nome do responsável:", fieldNomeResponsavel);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.insets = new Insets(10, 0, 5, 14);
    painel.add(InterfaceGraficaUtils.criarLabel("Descrição da medida:", InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 0, 5, 0);
    painel.add(new JScrollPane(areaDescricaoMedida), gbc);

    painelExterno.add(painel, BorderLayout.NORTH);
    return painelExterno;
  }

  private JPanel criarPesquisaResponsavel()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    fieldPesquisaResponsavel = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboPesquisaResponsavel = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código", "Nome"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(150, 36));
    buttonActualizar = criarBotao("Mostrar Todos", 150);
    buttonNovoResponsavel = criarBotao("Novo Responsável", 175);

    fieldPesquisaResponsavel.getDocument().addDocumentListener(new TratarPesquisaResponsavel());
    comboPesquisaResponsavel.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisaResponsavel);
    painel.add(comboPesquisaResponsavel);
    painel.add(buttonActualizar);
    painel.add(buttonNovoResponsavel);
    return painel;
  }

  private JPanel criarRodapeResponsavel()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Cada medida deve ter exactamente um responsável.",
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

  private JPanel criarRodapeMedida()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("A medida ficará disponível para associar aos recursos hídricos.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltar = criarBotao("Voltar", 120);
    buttonRegistar = criarBotao("Registar", 135);
    botoes.add(buttonVoltar);
    botoes.add(buttonRegistar);
    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JTextField criarCampoReadonly()
  {
    JTextField campo = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(420, 36));
    campo.setEditable(false);
    return campo;
  }

  private JButton criarBotao(String texto, int largura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, 40));
    IconesInterface.aplicarIconeBotao(botao);
    botao.addActionListener(tratarButtons);
    return botao;
  }

  private void adicionarLinha(JPanel painel, int linha, String label, JTextField campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, 0, 6, 14);
    painel.add(InterfaceGraficaUtils.criarLabel(label, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(6, 0, 6, 0);
    painel.add(campo, gbc);
  }

  private void carregarResponsaveis()
  {
    try
    {
      RegistarMedidaProteccaoSQL.carregarResponsaveis(connection, modeloResponsaveis);
      aplicarFiltroResponsaveis();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar responsáveis:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void inserirResponsavel()
  {
    String nome = JOptionPane.showInputDialog(this, "Nome do responsável:", "Novo Responsável",
        JOptionPane.PLAIN_MESSAGE);
    if(nome == null)
      return;

    nome = nome.trim();
    if(nome.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "O nome do responsável é obrigatório.");
      return;
    }

    try
    {
      RegistarMedidaProteccaoSQL.ResultadoOperacao resultado =
          RegistarMedidaProteccaoSQL.inserirResponsavel(connection, nome);
      if(resultado.sucesso != 1)
      {
        JOptionPane.showMessageDialog(this, "Responsável não inserido:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      carregarResponsaveis();
      JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(resultado.mensagem));
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível inserir responsável:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private boolean prepararResponsavelSelecionado()
  {
    if(tabelaResponsaveis.getSelectedRow() == -1)
    {
      JOptionPane.showMessageDialog(this, "Seleccione um responsável.");
      return false;
    }

    int linhaModel = tabelaResponsaveis.convertRowIndexToModel(tabelaResponsaveis.getSelectedRow());
    fieldCodResponsavel.setText(valorTabela(linhaModel, 0));
    fieldNomeResponsavel.setText(valorTabela(linhaModel, 1));
    return true;
  }

  private void registarMedida()
  {
    String descricao = areaDescricaoMedida.getText().trim();
    int codResponsavel = Integer.parseInt(fieldCodResponsavel.getText().trim());

    try
    {
      RegistarMedidaProteccaoSQL.ResultadoValidacao preValidacao =
          RegistarMedidaProteccaoSQL.preValidarRegisto(connection, codResponsavel, descricao);
      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Registo não permitido:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Responsável: " + codResponsavel + " - " + fieldNomeResponsavel.getText() + "\nDescrição: " + descricao
              + "\n\nDeseja registar esta medida de protecção?",
          "Confirmar Registo", JOptionPane.YES_NO_OPTION);
      if(resposta != JOptionPane.YES_OPTION)
        return;

      RegistarMedidaProteccaoSQL.ResultadoRegisto resultado =
          RegistarMedidaProteccaoSQL.registarMedida(connection, codResponsavel, descricao);
      if(!resultado.registado)
      {
        JOptionPane.showMessageDialog(this, "Medida não registada:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          MensagensInterface.formatarMensagem(resultado.mensagem) + "\nCódigo da medida: " + resultado.codigoMedida);
      areaDescricaoMedida.setText("");
      carregarResponsaveis();
      cardLayout.show(panelCards, CARD_RESPONSAVEL);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível registar medida:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private String valorTabela(int linha, int coluna)
  {
    Object valor = modeloResponsaveis.getValueAt(linha, coluna);
    return valor == null ? "" : valor.toString();
  }

  private void aplicarFiltroResponsaveis()
  {
    if(sorterResponsaveis == null || fieldPesquisaResponsavel == null || comboPesquisaResponsavel == null)
      return;

    String texto = fieldPesquisaResponsavel.getText().trim();
    if(texto.isEmpty())
    {
      sorterResponsaveis.setRowFilter(null);
      return;
    }

    int coluna = "Nome".equals(comboPesquisaResponsavel.getSelectedItem().toString()) ? 1 : 0;
    sorterResponsaveis.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private final class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      Object source = event.getSource();

      if(source == buttonActualizar)
        carregarResponsaveis();
      else if(source == buttonNovoResponsavel)
        inserirResponsavel();
      else if(source == comboPesquisaResponsavel)
        aplicarFiltroResponsaveis();
      else if(source == buttonAvancar)
      {
        if(prepararResponsavelSelecionado())
          cardLayout.show(panelCards, CARD_MEDIDA);
      }
      else if(source == buttonVoltarDashboard)
        voltarAoDashboard();
      else if(source == buttonVoltar)
        cardLayout.show(panelCards, CARD_RESPONSAVEL);
      else if(source == buttonRegistar)
        registarMedida();
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }

  private final class TratarPesquisaResponsavel implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroResponsaveis();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroResponsaveis();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroResponsaveis();
    }
  }
}
