package View_Interface_TransferenciasRecursosDB;

import Repository_SQL.TransferenciasRecursosDB.ActualizarMotivoTransferenciaSQL;
import Repository_SQL.TransferenciasRecursosDB.ActualizarMotivoTransferenciaSQL.DadosTransferencia;
import Repository_SQL.TransferenciasRecursosDB.ActualizarMotivoTransferenciaSQL.ResultadoOperacao;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;

public class JPanel_ActualizarMotivoTransferencia extends JPanel
{
  private static final String CARD_TRANSFERENCIAS = "transferências";
  private static final String CARD_MOTIVO = "motivo";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final CardLayout cardLayout = new CardLayout();
  private final JPanel panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaTransferencias;
  private DefaultTableModel modeloTransferencias;
  private TableRowSorter<DefaultTableModel> sorterTransferencias;
  private JTextField fieldPesquisa;
  private JComboBox<String> comboPesquisa;
  private JButton buttonActualizarTabela;
  private JButton buttonAvancar;
  private JButton buttonVoltarDashboard;

  private JTextField fieldCodigoTransferencia;
  private JTextField fieldFamiliaDoadora;
  private JTextField fieldFamiliaReceptora;
  private JTextField fieldVolumeCedido;
  private JTextField fieldDataAprovacao;
  private JTextField fieldValidade;
  private JTextArea areaMotivo;
  private JButton buttonVoltar;
  private JButton buttonActualizarMotivo;

  private String motivoOriginal = "";

  public JPanel_ActualizarMotivoTransferencia(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_ActualizarMotivoTransferencia(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_ActualizarMotivoTransferencia(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_ActualizarMotivoTransferencia(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));

    panelCards.add(criarCardTransferencias(), CARD_TRANSFERENCIAS);
    panelCards.add(criarCardMotivo(), CARD_MOTIVO);
    add(panelCards, BorderLayout.CENTER);

    if(carregarTabelasAutomaticamente)
    {
      carregarTransferencias();
    }
  }

  private JPanel criarCardTransferencias()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Actualizar Motivo da Transferência",
        "<html>Seleccione a transferência cujo motivo pretende actualizar.</html>");
    card.add(criarCentroTransferencias(), BorderLayout.CENTER);
    card.add(criarRodapeTransferencias(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardMotivo()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Motivo da Transferência",
        "<html>Esta operação altera apenas o motivo. Os saldos das cotas não serão modificados.</html>");
    card.add(criarCentroMotivo(), BorderLayout.CENTER);
    card.add(criarRodapeMotivo(), BorderLayout.SOUTH);
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

  private JPanel criarCentroTransferencias()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    painel.add(criarPesquisa(), BorderLayout.NORTH);

    modeloTransferencias = new DefaultTableModel(new String[]
      {
          "Código", "Cod. Doadora", "Família Doadora", "Cod. Receptora", "Família Receptora", "Volume", "Motivo",
          "Data Aprovação", "Validade"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };

    tabelaTransferencias = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaTransferencias.setModel(modeloTransferencias);
    tabelaTransferencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterTransferencias = new TableRowSorter<DefaultTableModel>(modeloTransferencias);
    tabelaTransferencias.setRowSorter(sorterTransferencias);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaTransferencias,
        InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1120, 500));
    painel.add(scroll, BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarPesquisa()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboPesquisa = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código", "Família Doadora", "Família Receptora", "Motivo"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(190, 36));
    buttonActualizarTabela = criarBotao("Mostrar Todos", 160, 36);

    fieldPesquisa.getDocument().addDocumentListener(new TratarPesquisa());
    comboPesquisa.addActionListener(tratarButtons);
    buttonActualizarTabela.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisa);
    painel.add(comboPesquisa);
    painel.add(buttonActualizarTabela);
    return painel;
  }

  private JPanel criarRodapeTransferencias()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Seleccione uma transferência para continuar.",
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

  private JPanel criarCentroMotivo()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldCodigoTransferencia = criarCampo(false);
    fieldFamiliaDoadora = criarCampo(false);
    fieldFamiliaReceptora = criarCampo(false);
    fieldVolumeCedido = criarCampo(false);
    fieldDataAprovacao = criarCampo(false);
    fieldValidade = criarCampo(false);

    areaMotivo = new JTextArea(5, 20);
    areaMotivo.setLineWrap(true);
    areaMotivo.setWrapStyleWord(true);
    areaMotivo.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    JScrollPane scrollMotivo = new JScrollPane(areaMotivo);
    scrollMotivo.setPreferredSize(new Dimension(360, 120));

    int linha = 0;
    adicionarLinha(painel, linha++, "Código da transferência:", fieldCodigoTransferencia);
    adicionarLinha(painel, linha++, "Família doadora:", fieldFamiliaDoadora);
    adicionarLinha(painel, linha++, "Família receptora:", fieldFamiliaReceptora);
    adicionarLinha(painel, linha++, "Volume cedido:", fieldVolumeCedido);
    adicionarLinha(painel, linha++, "Data de aprovação:", fieldDataAprovacao);
    adicionarLinha(painel, linha++, "Validade:", fieldValidade);
    adicionarLinha(painel, linha++, "Motivo:", scrollMotivo);

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = linha;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);
    return painel;
  }

  private JPanel criarRodapeMotivo()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Actualizar o motivo não altera saldos nem datas da transferência.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltar = criarBotao("Voltar", 130, 42);
    buttonActualizarMotivo = criarBotao("Actualizar", 145, 42);
    buttonVoltar.addActionListener(tratarButtons);
    buttonActualizarMotivo.addActionListener(tratarButtons);
    botoes.add(buttonVoltar);
    botoes.add(buttonActualizarMotivo);
    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JTextField criarCampo(boolean editavel)
  {
    JTextField campo = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(360, 36));
    campo.setEditable(editavel);
    return campo;
  }

  private JButton criarBotao(String texto, int largura, int altura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, altura));
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  private void adicionarLinha(JPanel painel, int linha, String textoLabel, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(9, 0, 9, 18);
    painel.add(InterfaceGraficaUtils.criarLabel(textoLabel, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(9, 0, 9, 0);
    painel.add(campo, gbc);
  }

  private void carregarTransferencias()
  {
    try
    {
      ActualizarMotivoTransferenciaSQL.carregarTransferencias(connection, modeloTransferencias);
      aplicarFiltro();
      InterfaceGraficaUtils.ajustarLarguraColunas(tabelaTransferencias, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar transferências:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private Integer getCodigoSelecionado()
  {
    int linhaView = tabelaTransferencias.getSelectedRow();
    if(linhaView == -1)
      return null;
    int linhaModel = tabelaTransferencias.convertRowIndexToModel(linhaView);
    return Integer.valueOf(modeloTransferencias.getValueAt(linhaModel, 0).toString());
  }

  private void carregarTransferenciaSelecionada()
  {
    Integer codigo = getCodigoSelecionado();
    if(codigo == null)
    {
      JOptionPane.showMessageDialog(this, "Seleccione uma transferência.");
      return;
    }

    try
    {
      DadosTransferencia dados = ActualizarMotivoTransferenciaSQL.carregarTransferencia(connection, codigo.intValue());
      motivoOriginal = valor(dados.motivo);
      fieldCodigoTransferencia.setText(String.valueOf(dados.codigoTransferencia));
      fieldFamiliaDoadora.setText(dados.codigoFamiliaDoadora + " - " + valor(dados.nomeDoadora));
      fieldFamiliaReceptora.setText(dados.codigoFamiliaReceptora + " - " + valor(dados.nomeReceptora));
      fieldVolumeCedido.setText(String.valueOf(dados.volumeCedido));
      fieldDataAprovacao.setText(valor(dados.dataAprovacao));
      fieldValidade.setText(valor(dados.validadeTransferencia));
      areaMotivo.setText(motivoOriginal);
      cardLayout.show(panelCards, CARD_MOTIVO);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar transferência:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private String valor(String texto)
  {
    return texto == null || "null".equalsIgnoreCase(texto) ? "" : texto;
  }

  private void actualizarMotivo()
  {
    String novoMotivo = areaMotivo.getText().trim();
    int codigo = Integer.parseInt(fieldCodigoTransferencia.getText().trim());

    try
    {
      ResultadoOperacao preValidacao = ActualizarMotivoTransferenciaSQL.preValidarActualizacao(connection, codigo,
          motivoOriginal, novoMotivo);
      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(preValidacao.mensagem));
        return;
      }

      int opcao = JOptionPane.showConfirmDialog(this,
          "Confirma a actualização do motivo desta transferência?\n\nOs saldos das cotas não serão modificados.",
          "Confirmar actualização", JOptionPane.YES_NO_OPTION);
      if(opcao != JOptionPane.YES_OPTION)
        return;

      ResultadoOperacao resultado = ActualizarMotivoTransferenciaSQL.actualizarMotivo(connection, codigo, motivoOriginal,
          novoMotivo);
      JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(resultado.mensagem));

      if(resultado.podeContinuar)
      {
        carregarTransferencias();
        motivoOriginal = novoMotivo;
        cardLayout.show(panelCards, CARD_TRANSFERENCIAS);
      }
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível actualizar motivo:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltro()
  {
    if(sorterTransferencias == null || fieldPesquisa == null)
      return;

    String texto = fieldPesquisa.getText().trim();
    if(texto.isEmpty())
    {
      sorterTransferencias.setRowFilter(null);
      return;
    }

    sorterTransferencias.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), getIndiceFiltro()));
  }

  private int getIndiceFiltro()
  {
    String criterio = comboPesquisa.getSelectedItem().toString();
    if("Família Doadora".equals(criterio))
      return 2;
    if("Família Receptora".equals(criterio))
      return 4;
    if("Motivo".equals(criterio))
      return 6;
    return 0;
  }

  private class TratarPesquisa implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
    public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
    public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == comboPesquisa)
      {
        aplicarFiltro();
        return;
      }

      if(event.getSource() == buttonActualizarTabela)
      {
        carregarTransferencias();
        return;
      }

      if(event.getSource() == buttonAvancar)
      {
        carregarTransferenciaSelecionada();
        return;
      }

      if(event.getSource() == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }

      if(event.getSource() == buttonVoltar)
      {
        cardLayout.show(panelCards, CARD_TRANSFERENCIAS);
        return;
      }

      if(event.getSource() == buttonActualizarMotivo)
        actualizarMotivo();
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }
}
