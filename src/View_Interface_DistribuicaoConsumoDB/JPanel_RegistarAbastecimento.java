package View_Interface_DistribuicaoConsumoDB;

import Repository_SQL.DistribuicaoConsumoDB.RegistarAbastecimentoSQL;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class JPanel_RegistarAbastecimento extends JPanel
{
  private static final String CARD_RECURSOS = "recursos";
  private static final String CARD_PONTOS = "pontos";
  private static final String CARD_DADOS = "dados";
  private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final java.awt.CardLayout cardLayout = new java.awt.CardLayout();
  private final JPanel panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaRecursos;
  private JTable tabelaPontos;
  private DefaultTableModel modeloRecursos;
  private DefaultTableModel modeloPontos;
  private TableRowSorter<DefaultTableModel> sorterRecursos;
  private TableRowSorter<DefaultTableModel> sorterPontos;

  private JTextField fieldPesquisaRecursos;
  private JTextField fieldPesquisaPontos;
  private JComboBox<String> comboPesquisaRecursos;
  private JComboBox<String> comboPesquisaPontos;

  private JTextField fieldCodigoRh;
  private JTextField fieldTipoRh;
  private JTextField fieldLocalizacaoRh;
  private JTextField fieldVazaoRh;
  private JTextField fieldCodigoPd;
  private JTextField fieldLocalizacaoPd;
  private JTextField fieldCapacidadePd;
  private JTextField fieldVolumeActualPd;
  private JTextField fieldEstadoPd;
  private JTextField fieldVolumeAbastecido;
  private JTextField fieldDataInicio;
  private JTextField fieldDuracaoHoras;
  private JTextField fieldDataFim;
  private JTextField fieldEstadoAbastecimento;
  private JTextField fieldVolumePrevisto;

  private JButton buttonActualizarRecursos;
  private JButton buttonActualizarPontos;
  private JButton buttonAvancarPontos;
  private JButton buttonVoltarDashboard;
  private JButton buttonVoltarRecursos;
  private JButton buttonAvancarDados;
  private JButton buttonVoltarPontos;
  private JButton buttonRegistar;
  private JButton buttonFinalizarPendentes;

  public JPanel_RegistarAbastecimento(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_RegistarAbastecimento(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_RegistarAbastecimento(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_RegistarAbastecimento(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));

    panelCards.add(criarCardRecursos(), CARD_RECURSOS);
    panelCards.add(criarCardPontos(), CARD_PONTOS);
    panelCards.add(criarCardDados(), CARD_DADOS);
    add(panelCards, BorderLayout.CENTER);

    finalizarPendentes(false);
    if(carregarTabelasAutomaticamente)
    {
      carregarRecursos();
    }
  }

  private JPanel criarCardRecursos()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Registar Abastecimento",
        "<html>Seleccione o recurso hídrico que será usado no abastecimento.</html>");
    card.add(criarCentroRecursos(), BorderLayout.CENTER);
    card.add(criarRodapeRecursos(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardPontos()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Selecionar Ponto de Distribuição",
        "<html>Escolha um ponto activo associado ao recurso hídrico seleccionado.</html>");
    card.add(criarCentroPontos(), BorderLayout.CENTER);
    card.add(criarRodapePontos(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardDados()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Dados do Abastecimento",
        "<html>Informe o volume. Os restantes campos são calculados automaticamente.</html>");
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

  private JPanel criarCentroRecursos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    painel.add(criarPesquisaRecursos(), BorderLayout.NORTH);

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

  private JPanel criarCentroPontos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    painel.add(criarPesquisaPontos(), BorderLayout.NORTH);

    modeloPontos = criarModelo(new String[]
      {
          "Código PD", "Localização", "Infraestrutura", "Capacidade", "Volume Actual", "Estado"
      });
    tabelaPontos = criarTabela(modeloPontos);
    tabelaPontos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterPontos = new TableRowSorter<DefaultTableModel>(modeloPontos);
    tabelaPontos.setRowSorter(sorterPontos);
    painel.add(criarScroll(tabelaPontos, new Dimension(1120, 500)), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarCentroDados()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    fieldCodigoRh = criarCampoReadonly();
    fieldTipoRh = criarCampoReadonly();
    fieldLocalizacaoRh = criarCampoReadonly();
    fieldVazaoRh = criarCampoReadonly();
    fieldCodigoPd = criarCampoReadonly();
    fieldLocalizacaoPd = criarCampoReadonly();
    fieldCapacidadePd = criarCampoReadonly();
    fieldVolumeActualPd = criarCampoReadonly();
    fieldEstadoPd = criarCampoReadonly();
    fieldVolumeAbastecido = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(420, 36));
    fieldDataInicio = criarCampoReadonly();
    fieldDuracaoHoras = criarCampoReadonly();
    fieldDataFim = criarCampoReadonly();
    fieldEstadoAbastecimento = criarCampoReadonly();
    fieldVolumePrevisto = criarCampoReadonly();

    fieldVolumeAbastecido.getDocument().addDocumentListener(new TratarVolume());

    int linha = 0;
    adicionarLinha(painel, linha++, "Código do recurso:", fieldCodigoRh);
    adicionarLinha(painel, linha++, "Tipo do recurso:", fieldTipoRh);
    adicionarLinha(painel, linha++, "Localização do recurso:", fieldLocalizacaoRh);
    adicionarLinha(painel, linha++, "Vazão do recurso (L/h):", fieldVazaoRh);
    adicionarLinha(painel, linha++, "Código do ponto:", fieldCodigoPd);
    adicionarLinha(painel, linha++, "Localização do ponto:", fieldLocalizacaoPd);
    adicionarLinha(painel, linha++, "Capacidade do ponto:", fieldCapacidadePd);
    adicionarLinha(painel, linha++, "Volume actual do ponto:", fieldVolumeActualPd);
    adicionarLinha(painel, linha++, "Estado do ponto:", fieldEstadoPd);
    adicionarLinha(painel, linha++, "Volume a abastecer:", fieldVolumeAbastecido);
    adicionarLinha(painel, linha++, "Data de início:", fieldDataInicio);
    adicionarLinha(painel, linha++, "Duração calculada:", fieldDuracaoHoras);
    adicionarLinha(painel, linha++, "Data prevista de fim:", fieldDataFim);
    adicionarLinha(painel, linha++, "Estado do abastecimento:", fieldEstadoAbastecimento);
    adicionarLinha(painel, linha++, "Volume previsto do ponto:", fieldVolumePrevisto);
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

  private JPanel criarPesquisaPontos()
  {
    fieldPesquisaPontos = criarCampoPesquisa(new TratarPesquisaPontos());
    comboPesquisaPontos = criarComboPesquisa(new String[]
      {
          "Código PD", "Localização", "Estado"
      });
    buttonActualizarPontos = criarBotao("Mostrar Todos", 150);
    return criarLinhaPesquisa(fieldPesquisaPontos, comboPesquisaPontos, buttonActualizarPontos);
  }

  private JPanel criarLinhaPesquisa(JTextField field, JComboBox<String> combo, JButton botaoActualizar)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(field);
    painel.add(combo);
    painel.add(botaoActualizar);
    return painel;
  }

  private JPanel criarRodapeRecursos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    painel.add(InterfaceGraficaUtils.criarLabel("Seleccione um recurso hídrico para continuar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.WEST);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar", 120);
    buttonFinalizarPendentes = criarBotao("Finalizar Pendentes", 190);
    buttonAvancarPontos = criarBotao("Proceder", 145);
    botoes.add(buttonVoltarDashboard);
    botoes.add(buttonFinalizarPendentes);
    botoes.add(buttonAvancarPontos);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarRodapePontos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    painel.add(InterfaceGraficaUtils.criarLabel("Apenas pontos activos podem receber novo abastecimento.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.WEST);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarRecursos = criarBotao("Voltar", 120);
    buttonAvancarDados = criarBotao("Proceder", 145);
    botoes.add(buttonVoltarRecursos);
    botoes.add(buttonAvancarDados);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarRodapeDados()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    painel.add(InterfaceGraficaUtils.criarLabel("O volume do ponto só aumenta quando o abastecimento concluir.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.WEST);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarPontos = criarBotao("Voltar", 120);
    buttonRegistar = criarBotao("Registar", 135);
    botoes.add(buttonVoltarPontos);
    botoes.add(buttonRegistar);
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
        new Dimension(180, 36));
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

  private void adicionarLinha(JPanel painel, int linha, String label, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(4, 0, 4, 14);
    painel.add(InterfaceGraficaUtils.criarLabel(label, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(4, 0, 4, 0);
    painel.add(campo, gbc);
  }

  private void carregarRecursos()
  {
    try
    {
      RegistarAbastecimentoSQL.carregarRecursos(connection, modeloRecursos);
      aplicarFiltroRecursos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar recursos:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarPontos()
  {
    try
    {
      RegistarAbastecimentoSQL.carregarPontosRecurso(connection, modeloPontos, getCodigoRh());
      aplicarFiltroPontos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar pontos:\n" + MensagensInterface.formatarErro(ex));
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
    fieldTipoRh.setText(valorTabela(modeloRecursos, linha, 1));
    fieldLocalizacaoRh.setText(valorTabela(modeloRecursos, linha, 2));
    fieldVazaoRh.setText(valorTabela(modeloRecursos, linha, 3));

    try
    {
      int abertos = RegistarAbastecimentoSQL.contarAbastecimentosAbertosRecurso(connection, getCodigoRh());
      if(abertos > 0)
      {
        JOptionPane.showMessageDialog(this,
            "Este recurso hídrico já possui " + abertos + " abastecimento(s) aberto(s).\n"
                + "Conclua, retome ou cancele o abastecimento actual antes de iniciar outro para este recurso.");
        limparRecursoPreparado();
        return false;
      }
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return false;

      JOptionPane.showMessageDialog(this, "Não foi possível validar abastecimentos abertos do recurso:\n" + MensagensInterface.formatarErro(ex));
      limparRecursoPreparado();
      return false;
    }

    carregarPontos();
    return true;
  }

  private void limparRecursoPreparado()
  {
    fieldCodigoRh.setText("");
    fieldTipoRh.setText("");
    fieldLocalizacaoRh.setText("");
    fieldVazaoRh.setText("");
  }

  private boolean prepararPonto()
  {
    if(tabelaPontos.getSelectedRow() == -1)
    {
      JOptionPane.showMessageDialog(this, "Seleccione um ponto de distribuição.");
      return false;
    }

    int linha = tabelaPontos.convertRowIndexToModel(tabelaPontos.getSelectedRow());
    fieldCodigoPd.setText(valorTabela(modeloPontos, linha, 0));
    fieldLocalizacaoPd.setText(valorTabela(modeloPontos, linha, 1));
    fieldCapacidadePd.setText(valorTabela(modeloPontos, linha, 3));
    fieldVolumeActualPd.setText(valorTabela(modeloPontos, linha, 4));
    fieldEstadoPd.setText(valorTabela(modeloPontos, linha, 5));
    fieldVolumeAbastecido.setText("");
    actualizarCalculos();
    return true;
  }

  private void actualizarCalculos()
  {
    fieldDataInicio.setText(FORMATO_DATA.format(LocalDateTime.now()));
    fieldEstadoAbastecimento.setText("Em curso");

    Double volume = lerDouble(fieldVolumeAbastecido);
    Double vazao = lerDouble(fieldVazaoRh);
    Double volumeActual = lerDouble(fieldVolumeActualPd);

    if(volume == null || vazao == null || vazao.doubleValue() <= 0)
    {
      fieldDuracaoHoras.setText("");
      fieldDataFim.setText("");
      fieldVolumePrevisto.setText("");
      return;
    }

    double duracao = volume.doubleValue() / vazao.doubleValue();
    LocalDateTime dataFim = LocalDateTime.now().plusSeconds(Math.round(duracao * 3600.0));
    fieldDuracaoHoras.setText(formatarDuracao(duracao));
    fieldDataFim.setText(FORMATO_DATA.format(dataFim));

    if(volumeActual != null)
      fieldVolumePrevisto.setText(String.format(java.util.Locale.US, "%.2f", volumeActual.doubleValue() + volume));
  }

  private void registarAbastecimento()
  {
    Double volume = lerDouble(fieldVolumeAbastecido);
    if(volume == null)
    {
      JOptionPane.showMessageDialog(this, "Informe um volume válido.");
      return;
    }

    int codigoRh = getCodigoRh();
    int codigoPd = getCodigoPd();

    try
    {
      RegistarAbastecimentoSQL.ResultadoPreValidacao pre =
          RegistarAbastecimentoSQL.preValidarRegisto(connection, codigoRh, codigoPd, volume.doubleValue());
      if(!pre.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Registo não permitido:\n" + MensagensInterface.formatarMensagem(pre.mensagem));
        return;
      }

      actualizarCalculos();
      int resposta = JOptionPane.showConfirmDialog(this,
          "Recurso hídrico: " + codigoRh + "\nPonto: " + codigoPd + "\nVolume: " + volume
              + "\nDuração prevista: " + fieldDuracaoHoras.getText() + "\nData prevista de fim: "
              + fieldDataFim.getText() + "\n\nDeseja registar este abastecimento?",
          "Confirmar Abastecimento", JOptionPane.YES_NO_OPTION);
      if(resposta != JOptionPane.YES_OPTION)
        return;

      RegistarAbastecimentoSQL.ResultadoRegisto resultado =
          RegistarAbastecimentoSQL.registarAbastecimento(connection, codigoRh, codigoPd, volume.doubleValue());
      if(!resultado.registado)
      {
        JOptionPane.showMessageDialog(this, "Abastecimento não registado:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          MensagensInterface.formatarMensagem(resultado.mensagem) + "\nCódigo: " + resultado.codigoAbastecimento + "\nInício: " + resultado.dataInicio
              + "\nFim previsto: " + resultado.dataFim);
      carregarPontos();
      cardLayout.show(panelCards, CARD_PONTOS);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível registar abastecimento:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void finalizarPendentes(boolean mostrarMensagem)
  {
    try
    {
      RegistarAbastecimentoSQL.ResultadoFinalizacao resultado =
          RegistarAbastecimentoSQL.finalizarAbastecimentos(connection);
      if(mostrarMensagem)
        JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(resultado.mensagem) + "\nFinalizados: " + resultado.finalizados);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      if(mostrarMensagem)
        JOptionPane.showMessageDialog(this, "Não foi possível finalizar abastecimentos:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private int getCodigoRh()
  {
    return Integer.parseInt(fieldCodigoRh.getText().trim());
  }

  private int getCodigoPd()
  {
    return Integer.parseInt(fieldCodigoPd.getText().trim());
  }

  private Double lerDouble(JTextField field)
  {
    try
    {
      String texto = field.getText().trim().replace(',', '.');
      if(texto.isEmpty())
        return null;
      return Double.valueOf(texto);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return null;

      return null;
    }
  }

  private String formatarDuracao(double duracaoHoras)
  {
    long totalSegundos = Math.max(1L, Math.round(duracaoHoras * 3600.0));
    long horas = totalSegundos / 3600;
    long resto = totalSegundos % 3600;
    long minutos = resto / 60;
    long segundos = resto % 60;

    if(horas > 0)
    {
      if(minutos > 0)
        return horas + " hora(s) e " + minutos + " minuto(s)";
      return horas + " hora(s)";
    }

    if(minutos > 0)
    {
      if(segundos > 0)
        return minutos + " minuto(s) e " + segundos + " segundo(s)";
      return minutos + " minuto(s)";
    }

    return segundos + " segundo(s)";
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

  private void aplicarFiltroPontos()
  {
    aplicarFiltro(sorterPontos, fieldPesquisaPontos, comboPesquisaPontos);
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

    int coluna = combo.getSelectedIndex();
    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private final class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      Object source = event.getSource();

      if(source == buttonActualizarRecursos)
        carregarRecursos();
      else if(source == buttonActualizarPontos)
        carregarPontos();
      else if(source == comboPesquisaRecursos)
        aplicarFiltroRecursos();
      else if(source == comboPesquisaPontos)
        aplicarFiltroPontos();
      else if(source == buttonFinalizarPendentes)
      {
        finalizarPendentes(true);
        carregarRecursos();
      }
      else if(source == buttonVoltarDashboard)
        voltarAoDashboard();
      else if(source == buttonAvancarPontos)
      {
        if(prepararRecurso())
          cardLayout.show(panelCards, CARD_PONTOS);
      }
      else if(source == buttonVoltarRecursos)
        cardLayout.show(panelCards, CARD_RECURSOS);
      else if(source == buttonAvancarDados)
      {
        if(prepararPonto())
          cardLayout.show(panelCards, CARD_DADOS);
      }
      else if(source == buttonVoltarPontos)
        cardLayout.show(panelCards, CARD_PONTOS);
      else if(source == buttonRegistar)
        registarAbastecimento();
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

  private final class TratarPesquisaPontos implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) { aplicarFiltroPontos(); }
    public void removeUpdate(DocumentEvent e) { aplicarFiltroPontos(); }
    public void changedUpdate(DocumentEvent e) { aplicarFiltroPontos(); }
  }

  private final class TratarVolume implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e) { actualizarCalculos(); }
    public void removeUpdate(DocumentEvent e) { actualizarCalculos(); }
    public void changedUpdate(DocumentEvent e) { actualizarCalculos(); }
  }
}
