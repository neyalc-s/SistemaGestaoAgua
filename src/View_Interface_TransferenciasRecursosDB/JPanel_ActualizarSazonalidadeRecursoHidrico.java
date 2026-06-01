package View_Interface_TransferenciasRecursosDB;

import Repository_SQL.TransferenciasRecursosDB.ActualizarSazonalidadeRecursoHidricoSQL;
import Repository_SQL.TransferenciasRecursosDB.ActualizarSazonalidadeRecursoHidricoSQL.DadosRecurso;
import Repository_SQL.TransferenciasRecursosDB.ActualizarSazonalidadeRecursoHidricoSQL.ResultadoOperacao;
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

public class JPanel_ActualizarSazonalidadeRecursoHidrico extends JPanel
{
  private static final String CARD_RECURSOS = "recursos";
  private static final String CARD_SAZONALIDADE = "sazonalidade";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final CardLayout cardLayout = new CardLayout();
  private final JPanel panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaRecursos;
  private DefaultTableModel modeloRecursos;
  private TableRowSorter<DefaultTableModel> sorterRecursos;
  private JTextField fieldPesquisa;
  private JComboBox<String> comboPesquisa;
  private JButton buttonPesquisar;
  private JButton buttonMostrarTodos;
  private JButton buttonAvancar;
  private JButton buttonVoltarDashboard;

  private JTextField fieldCodigoRh;
  private JTextField fieldTipoRh;
  private JTextField fieldLocalizacaoRh;
  private JTextField fieldVolumeRh;
  private JTextField fieldSazonalidadeActual;
  private JTextField fieldVulnerabilidadeRh;
  private JTextField fieldNivelExploracaoRh;
  private JComboBox<String> comboNovaSazonalidade;
  private JTextField fieldOutraSazonalidade;
  private JButton buttonVoltar;
  private JButton buttonActualizar;

  private String sazonalidadeOriginal = "";

  public JPanel_ActualizarSazonalidadeRecursoHidrico(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_ActualizarSazonalidadeRecursoHidrico(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_ActualizarSazonalidadeRecursoHidrico(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_ActualizarSazonalidadeRecursoHidrico(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));

    panelCards.add(criarCardRecursos(), CARD_RECURSOS);
    panelCards.add(criarCardSazonalidade(), CARD_SAZONALIDADE);
    add(panelCards, BorderLayout.CENTER);

    if(carregarTabelasAutomaticamente)
    {
      carregarRecursos();
    }
  }

  private JPanel criarCardRecursos()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Actualizar Sazonalidade do Recurso",
        "<html>Seleccione o recurso hídrico cuja sazonalidade pretende actualizar.</html>");
    card.add(criarCentroRecursos(), BorderLayout.CENTER);
    card.add(criarRodapeRecursos(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardSazonalidade()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Sazonalidade do Recurso Hídrico",
        "<html>Esta operação altera apenas a sazonalidade do recurso hídrico seleccionado.</html>");
    card.add(criarCentroSazonalidade(), BorderLayout.CENTER);
    card.add(criarRodapeSazonalidade(), BorderLayout.SOUTH);
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
    painel.add(criarPesquisa(), BorderLayout.NORTH);

    modeloRecursos = new DefaultTableModel(new String[]
      {
          "codigo_rh", "tipo_rh", "localizacao_rh", "volume_rh", "sazonalidade_rh", "vulnerabilidade_rh",
          "nivel_exploracao_rh"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };

    tabelaRecursos = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaRecursos.setModel(modeloRecursos);
    tabelaRecursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tabelaRecursos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    sorterRecursos = new TableRowSorter<DefaultTableModel>(modeloRecursos);
    tabelaRecursos.setRowSorter(sorterRecursos);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaRecursos,
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
          "codigo_rh", "tipo_rh", "localizacao_rh", "sazonalidade_rh", "nivel_exploracao_rh"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(190, 36));
    buttonPesquisar = criarBotao("Pesquisar", 135, 36);
    buttonMostrarTodos = criarBotao("Mostrar Todos", 150, 36);

    fieldPesquisa.getDocument().addDocumentListener(new TratarPesquisa());
    comboPesquisa.addActionListener(tratarButtons);
    buttonPesquisar.addActionListener(tratarButtons);
    buttonMostrarTodos.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisa);
    painel.add(comboPesquisa);
    painel.add(buttonPesquisar);
    painel.add(buttonMostrarTodos);
    return painel;
  }

  private JPanel criarRodapeRecursos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Seleccione um recurso hídrico para continuar.",
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

  private JPanel criarCentroSazonalidade()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldCodigoRh = criarCampo(false);
    fieldTipoRh = criarCampo(false);
    fieldLocalizacaoRh = criarCampo(false);
    fieldVolumeRh = criarCampo(false);
    fieldSazonalidadeActual = criarCampo(false);
    fieldVulnerabilidadeRh = criarCampo(false);
    fieldNivelExploracaoRh = criarCampo(false);
    comboNovaSazonalidade = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Permanente", "Chuvosa", "Seca", "Intermitente", "Outros"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(220, 36));
    fieldOutraSazonalidade = criarCampo(true);
    fieldOutraSazonalidade.setEnabled(false);
    comboNovaSazonalidade.addActionListener(tratarButtons);

    int linha = 0;
    adicionarLinha(painel, linha++, "Código do recurso:", fieldCodigoRh);
    adicionarLinha(painel, linha++, "Tipo:", fieldTipoRh);
    adicionarLinha(painel, linha++, "Localizacao:", fieldLocalizacaoRh);
    adicionarLinha(painel, linha++, "Vazao/volume:", fieldVolumeRh);
    adicionarLinha(painel, linha++, "Sazonalidade actual:", fieldSazonalidadeActual);
    adicionarLinha(painel, linha++, "Vulnerabilidade:", fieldVulnerabilidadeRh);
    adicionarLinha(painel, linha++, "Nivel de exploracao:", fieldNivelExploracaoRh);
    adicionarLinha(painel, linha++, "Nova sazonalidade:", comboNovaSazonalidade);
    adicionarLinha(painel, linha++, "Outra sazonalidade:", fieldOutraSazonalidade);

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = linha;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);
    return painel;
  }

  private JPanel criarRodapeSazonalidade()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Apenas o campo sazonalidade_rh será actualizado.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltar = criarBotao("Voltar", 130, 42);
    buttonActualizar = criarBotao("Actualizar", 145, 42);
    buttonVoltar.addActionListener(tratarButtons);
    buttonActualizar.addActionListener(tratarButtons);
    botoes.add(buttonVoltar);
    botoes.add(buttonActualizar);
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

  private void carregarRecursos()
  {
    try
    {
      ActualizarSazonalidadeRecursoHidricoSQL.carregarRecursos(connection, modeloRecursos);
      aplicarFiltro();
      InterfaceGraficaUtils.ajustarLarguraColunas(tabelaRecursos, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os recursos hídricos.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private Integer getCodigoSelecionado()
  {
    int linhaView = tabelaRecursos.getSelectedRow();
    if(linhaView == -1)
      return null;
    int linhaModel = tabelaRecursos.convertRowIndexToModel(linhaView);
    return Integer.valueOf(modeloRecursos.getValueAt(linhaModel, 0).toString());
  }

  private void carregarRecursoSelecionado()
  {
    Integer codigo = getCodigoSelecionado();
    if(codigo == null)
    {
      JOptionPane.showMessageDialog(this, "Seleccione um recurso hídrico.");
      return;
    }

    try
    {
      DadosRecurso dados = ActualizarSazonalidadeRecursoHidricoSQL.preCarregarRecurso(connection, codigo.intValue());
      if(!dados.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(dados.mensagem));
        return;
      }

      sazonalidadeOriginal = valor(dados.sazonalidadeRh);
      fieldCodigoRh.setText(String.valueOf(dados.codigoRh));
      fieldTipoRh.setText(valor(dados.tipoRh));
      fieldLocalizacaoRh.setText(valor(dados.localizacaoRh));
      fieldVolumeRh.setText(dados.volumeRh == null ? "" : String.valueOf(dados.volumeRh));
      fieldSazonalidadeActual.setText(sazonalidadeOriginal);
      fieldVulnerabilidadeRh.setText(valor(dados.vulnerabilidadeRh));
      fieldNivelExploracaoRh.setText(valor(dados.nivelExploracaoRh));
      comboNovaSazonalidade.setSelectedIndex(0);
      fieldOutraSazonalidade.setText("");
      fieldOutraSazonalidade.setEnabled(false);
      cardLayout.show(panelCards, CARD_SAZONALIDADE);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar recurso hídrico:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private String valor(String texto)
  {
    return texto == null || "null".equalsIgnoreCase(texto) ? "" : texto;
  }

  private String obterNovaSazonalidade()
  {
    if("Outros".equals(comboNovaSazonalidade.getSelectedItem().toString()))
      return fieldOutraSazonalidade.getText().trim();
    return comboNovaSazonalidade.getSelectedItem().toString();
  }

  private void actualizarSazonalidade()
  {
    int codigo = Integer.parseInt(fieldCodigoRh.getText().trim());
    String novaSazonalidade = obterNovaSazonalidade();

    try
    {
      int opcao = JOptionPane.showConfirmDialog(this,
          "Confirma a actualização da sazonalidade deste recurso hídrico?\n\nApenas sazonalidade_rh será alterada.",
          "Confirmar actualização", JOptionPane.YES_NO_OPTION);
      if(opcao != JOptionPane.YES_OPTION)
        return;

      ResultadoOperacao resultado = ActualizarSazonalidadeRecursoHidricoSQL.actualizarSazonalidade(connection, codigo,
          sazonalidadeOriginal, novaSazonalidade);
      JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(resultado.mensagem));

      if(resultado.sucesso)
      {
        carregarRecursos();
        sazonalidadeOriginal = novaSazonalidade;
        cardLayout.show(panelCards, CARD_RECURSOS);
      }
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível actualizar sazonalidade:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltro()
  {
    if(sorterRecursos == null || modeloRecursos == null || comboPesquisa == null)
      return;

    String texto = fieldPesquisa.getText().trim();
    if(texto.isEmpty())
    {
      sorterRecursos.setRowFilter(null);
      return;
    }

    String colunaFiltro = comboPesquisa.getSelectedItem().toString();
    int coluna = InterfaceGraficaUtils.encontrarIndiceColuna(modeloRecursos, colunaFiltro);
    if("codigo_rh".equals(colunaFiltro))
      sorterRecursos.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(texto) + "$", coluna));
    else
      sorterRecursos.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private void mostrarTodos()
  {
    fieldPesquisa.setText("");
    carregarRecursos();
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
      if(event.getSource() == comboPesquisa || event.getSource() == buttonPesquisar)
      {
        aplicarFiltro();
        return;
      }

      if(event.getSource() == buttonMostrarTodos)
      {
        mostrarTodos();
        return;
      }

      if(event.getSource() == buttonAvancar)
      {
        carregarRecursoSelecionado();
        return;
      }

      if(event.getSource() == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }

      if(event.getSource() == comboNovaSazonalidade)
      {
        boolean outros = "Outros".equals(comboNovaSazonalidade.getSelectedItem().toString());
        fieldOutraSazonalidade.setEnabled(outros);
        if(!outros)
          fieldOutraSazonalidade.setText("");
        return;
      }

      if(event.getSource() == buttonVoltar)
      {
        cardLayout.show(panelCards, CARD_RECURSOS);
        return;
      }

      if(event.getSource() == buttonActualizar)
        actualizarSazonalidade();
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }
}
