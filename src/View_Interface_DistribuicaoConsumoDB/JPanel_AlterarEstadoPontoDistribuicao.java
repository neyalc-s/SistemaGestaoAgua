package View_Interface_DistribuicaoConsumoDB;

import Repository_SQL.DistribuicaoConsumoDB.PontoDistribuicaoSQL;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

public class JPanel_AlterarEstadoPontoDistribuicao extends JPanel
{
  private static final String CARD_SELECCIONAR = "seleccionar";
  private static final String CARD_REVISAO = "revisao";

  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  private CardLayout cardLayout;
  private JPanel panelCards;

  private JTable tabelaPontos;
  private DefaultTableModel modeloPontos;
  private TableRowSorter<DefaultTableModel> sorterPontos;
  private JTextField fieldPesquisa;
  private JComboBox<String> comboCriterio;
  private JButton buttonActualizar;
  private JButton buttonLimparFiltro;
  private JButton buttonProceder;
  private JButton buttonVoltar;
  private JLabel labelQuantidadeSelecionada;

  private JTable tabelaSelecionados;
  private DefaultTableModel modeloSelecionados;
  private JButton buttonRemoverSelecionados;
  private JButton buttonLimparLista;
  private JButton buttonVoltarSelecao;
  private JButton buttonConfirmar;
  private JComboBox<String> comboEstadoNovo;
  private JLabel labelResumoSelecao;
  private final Runnable voltarDashboard;

  public JPanel_AlterarEstadoPontoDistribuicao(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_AlterarEstadoPontoDistribuicao(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_AlterarEstadoPontoDistribuicao(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_AlterarEstadoPontoDistribuicao(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
    panelCards.add(criarCardSeleccionar(), CARD_SELECCIONAR);
    panelCards.add(criarCardRevisao(), CARD_REVISAO);

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
      carregarPontosDistribuicao();
    }
  }

  private JPanel criarCardSeleccionar()
  {
    JPanel painelExterno = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(
      32,
      new BorderLayout(0, 22),
      InterfaceGraficaUtils.COR_CARD,
      new EmptyBorder(30, 30, 30, 30),
      new Dimension(1180, 720)
    );

    card.add(
      InterfaceGraficaUtils.criarTopo(
        "Alterar Estado Operacional",
        "<html>Seleccione um ou vários pontos de distribuição na tabela. Use Ctrl ou Shift para selecionar em lote.</html>",
        InterfaceGraficaUtils.FONT_TITULO,
        InterfaceGraficaUtils.FONT_SUBTITULO,
        InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO
      ),
      BorderLayout.NORTH
    );

    card.add(criarCentroSeleccionar(), BorderLayout.CENTER);
    card.add(criarRodapeSeleccionar(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCardRevisao()
  {
    JPanel painelExterno = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(
      32,
      new BorderLayout(0, 18),
      InterfaceGraficaUtils.COR_CARD,
      new EmptyBorder(30, 30, 30, 30),
      new Dimension(1180, 720)
    );

    card.add(
      InterfaceGraficaUtils.criarTopo(
        "Revisão da Mudança",
        "<html>Confirme a lista de pontos seleccionados e defina um novo estado operacional para todos.</html>",
        InterfaceGraficaUtils.FONT_TITULO,
        InterfaceGraficaUtils.FONT_SUBTITULO,
        InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO
      ),
      BorderLayout.NORTH
    );

    card.add(criarCentroRevisao(), BorderLayout.CENTER);
    card.add(criarRodapeRevisao(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCentroSeleccionar()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel labelTabela = InterfaceGraficaUtils.criarLabel(
      "Pontos de Distribuição",
      InterfaceGraficaUtils.FONT_LABEL_SECAO,
      InterfaceGraficaUtils.COR_AZUL
    );

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(labelTabela, BorderLayout.NORTH);
    topoTabela.add(criarPainelPesquisa(), BorderLayout.SOUTH);

    painel.add(topoTabela, BorderLayout.NORTH);
    painel.add(criarScrollTabelaPontos(), BorderLayout.CENTER);

    return painel;
  }

  private JPanel criarCentroRevisao()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 14));

    labelResumoSelecao = InterfaceGraficaUtils.criarLabel(
      "Pontos seleccionados: 0",
      InterfaceGraficaUtils.FONT_LABEL_SECAO,
      InterfaceGraficaUtils.COR_AZUL
    );

    JPanel painelResumo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    painelResumo.add(labelResumoSelecao, BorderLayout.NORTH);
    painelResumo.add(criarScrollTabelaSelecionados(), BorderLayout.CENTER);

    painel.add(painelResumo, BorderLayout.CENTER);
    painel.add(criarFormularioMudanca(), BorderLayout.SOUTH);

    return painel;
  }

  private JPanel criarPainelPesquisa()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    JLabel label = InterfaceGraficaUtils.criarLabel(
      "Pesquisar:",
      InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
      InterfaceGraficaUtils.COR_TEXTO
    );

    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(250, 36));
    comboCriterio = InterfaceGraficaUtils.criarCombo(
      new String[]
      {
        "Código", "Localização", "Tipo de Infraestrutura", "Fonte de Abastecimento", "Estado Operacional"
      },
      InterfaceGraficaUtils.FONT_CAMPO,
      new Dimension(230, 36)
    );

    buttonActualizar = criarBotao("Mostrar Todos");
    buttonLimparFiltro = criarBotao("Limpar Filtro");

    fieldPesquisa.getDocument().addDocumentListener(new TratarPesquisa());
    comboCriterio.addActionListener(tratarButtons);
    buttonActualizar.addActionListener(tratarButtons);
    buttonLimparFiltro.addActionListener(tratarButtons);

    painel.add(label);
    painel.add(fieldPesquisa);
    painel.add(comboCriterio);
    painel.add(buttonActualizar);
    painel.add(buttonLimparFiltro);

    return painel;
  }

  private JScrollPane criarScrollTabelaPontos()
  {
    String[] colunas =
      {
        "Código", "Localização", "Tipo de Infraestrutura", "Volume Atual", "Fonte de Abastecimento",
        "Estado Operacional"
      };

    modeloPontos = new DefaultTableModel(colunas, 0)
    {
      @Override
      public boolean isCellEditable(int row, int column)
      {
        return false;
      }
    };

    tabelaPontos = InterfaceGraficaUtils.criarTabelaBase(
      InterfaceGraficaUtils.FONT_TABELA,
      InterfaceGraficaUtils.FONT_HEADER,
      InterfaceGraficaUtils.COR_GRID_TABELA,
      InterfaceGraficaUtils.COR_SELECAO_TABELA,
      InterfaceGraficaUtils.COR_TEXTO
    );
    tabelaPontos.setModel(modeloPontos);
    tabelaPontos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    tabelaPontos.getSelectionModel().addListSelectionListener(new TratarSelecaoPontos());

    sorterPontos = new TableRowSorter<>(modeloPontos);
    tabelaPontos.setRowSorter(sorterPontos);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaPontos, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1100, 470));
    return scroll;
  }

  private JPanel criarRodapeSeleccionar()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    labelQuantidadeSelecionada = InterfaceGraficaUtils.criarLabel(
      "0 ponto(s) seleccionado(s)",
      InterfaceGraficaUtils.FONT_AJUDA,
      InterfaceGraficaUtils.COR_SUBTEXTO
    );

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));

    buttonVoltar = criarBotao("Voltar");
    buttonProceder = criarBotao("Proceder");
    buttonProceder.setEnabled(false);

    buttonVoltar.addActionListener(tratarButtons);
    buttonProceder.addActionListener(tratarButtons);

    painelBotoes.add(buttonVoltar);
    painelBotoes.add(buttonProceder);

    painelRodape.add(labelQuantidadeSelecionada, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JScrollPane criarScrollTabelaSelecionados()
  {
    String[] colunas =
      {
        "Código", "Localização", "Tipo de Infraestrutura", "Volume Atual", "Fonte de Abastecimento",
        "Estado Actual"
      };

    modeloSelecionados = new DefaultTableModel(colunas, 0)
    {
      @Override
      public boolean isCellEditable(int row, int column)
      {
        return false;
      }
    };

    tabelaSelecionados = InterfaceGraficaUtils.criarTabelaBase(
      InterfaceGraficaUtils.FONT_TABELA,
      InterfaceGraficaUtils.FONT_HEADER,
      InterfaceGraficaUtils.COR_GRID_TABELA,
      InterfaceGraficaUtils.COR_SELECAO_TABELA,
      InterfaceGraficaUtils.COR_TEXTO
    );
    tabelaSelecionados.setModel(modeloSelecionados);
    tabelaSelecionados.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaSelecionados, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1100, 330));
    return scroll;
  }

  private JPanel criarFormularioMudanca()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    comboEstadoNovo = InterfaceGraficaUtils.criarCombo(
      new String[]
      {
        "Activo",
        "Inactivo",
        "Em Manutencao"
      },
      InterfaceGraficaUtils.FONT_CAMPO,
      new Dimension(250, 36)
    );

    int linha = 0;
    adicionarLinhaFormulario(painel, linha++, "Novo estado operacional:", comboEstadoNovo);

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = linha;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);

    return painel;
  }

  private JPanel criarRodapeRevisao()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel(
      "Revise os pontos e confirme a mudança para todos os seleccionados.",
      InterfaceGraficaUtils.FONT_AJUDA,
      InterfaceGraficaUtils.COR_SUBTEXTO
    );

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarSelecao = criarBotao("Voltar");
    buttonRemoverSelecionados = criarBotao("Remover da lista");
    buttonLimparLista = criarBotao("Limpar lista");
    buttonConfirmar = criarBotao("Confirmar");

    buttonVoltarSelecao.addActionListener(tratarButtons);
    buttonRemoverSelecionados.addActionListener(tratarButtons);
    buttonLimparLista.addActionListener(tratarButtons);
    buttonConfirmar.addActionListener(tratarButtons);

    painelBotoes.add(buttonVoltarSelecao);
    painelBotoes.add(buttonRemoverSelecionados);
    painelBotoes.add(buttonLimparLista);
    painelBotoes.add(buttonConfirmar);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JButton criarBotao(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(
      texto,
      InterfaceGraficaUtils.FONT_BOTAO,
      InterfaceGraficaUtils.COR_AZUL,
      InterfaceGraficaUtils.COR_BRANCO,
      new Dimension(150, 40)
    );
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
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

  private void carregarPontosDistribuicao()
  {
    try
    {
      DefaultTableModel modelo = InterfaceGraficaUtils.carregarModeloTabela(
        connection,
        PontoDistribuicaoSQL.CONSULTAR_PONTOS_DISTRIBUICAO
      );

      modeloPontos = modelo;
      if(tabelaPontos != null)
        tabelaPontos.setModel(modeloPontos);
      sorterPontos = new TableRowSorter<>(modeloPontos);
      if(tabelaPontos != null)
      {
        tabelaPontos.setRowSorter(sorterPontos);
        InterfaceGraficaUtils.ajustarLarguraColunas(
          tabelaPontos,
          InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER
        );
      }
      aplicarFiltro();
      atualizarQuantidadeSelecionada();
    }
    catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os pontos de distribuição.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltro()
  {
    if(sorterPontos == null || fieldPesquisa == null || comboCriterio == null || modeloPontos == null)
      return;

    String texto = fieldPesquisa.getText().trim();
    if(texto.isEmpty())
    {
      sorterPontos.setRowFilter(null);
      return;
    }

    int coluna = getIndiceColunaSelecionada();
    sorterPontos.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private int getIndiceColunaSelecionada()
  {
    String criterio = comboCriterio.getSelectedItem().toString();

    switch(criterio)
    {
      case "Código":
        return InterfaceGraficaUtils.encontrarIndiceColuna(modeloPontos, "codigo_pd");
      case "Localização":
        return InterfaceGraficaUtils.encontrarIndiceColuna(modeloPontos, "localizacao_pd");
      case "Tipo de Infraestrutura":
        return InterfaceGraficaUtils.encontrarIndiceColuna(modeloPontos, "tipo_infraestrutura_pd");
      case "Fonte de Abastecimento":
        return InterfaceGraficaUtils.encontrarIndiceColuna(modeloPontos, "fonte_abastecimento_pd");
      case "Estado Operacional":
        return InterfaceGraficaUtils.encontrarIndiceColuna(modeloPontos, "estado_operacional_pd");
      default:
        return 0;
    }
  }

  private void actualizarSeleccaoParaRevisao()
  {
    Set<String> codigosExistentes = new LinkedHashSet<String>();

    for(int i = 0; i < modeloSelecionados.getRowCount(); i++)
      codigosExistentes.add(String.valueOf(modeloSelecionados.getValueAt(i, 0)));

    int[] linhas = tabelaPontos.getSelectedRows();
    if(linhas == null || linhas.length == 0)
    {
      atualizarQuantidadeSelecionada();
      return;
    }

    for(int linhaView : linhas)
    {
      int linhaModel = tabelaPontos.convertRowIndexToModel(linhaView);
      String codigo = String.valueOf(modeloPontos.getValueAt(linhaModel, 0));
      if(codigosExistentes.contains(codigo))
        continue;

      Vector<Object> linha = new Vector<Object>();
      linha.add(codigo);
      linha.add(modeloPontos.getValueAt(linhaModel, 1));
      linha.add(modeloPontos.getValueAt(linhaModel, 2));
      linha.add(modeloPontos.getValueAt(linhaModel, 3));
      linha.add(modeloPontos.getValueAt(linhaModel, 4));
      linha.add(modeloPontos.getValueAt(linhaModel, 5));
      modeloSelecionados.addRow(linha);
    }

    tabelaPontos.clearSelection();
    atualizarQuantidadeSelecionada();
  }

  private void removerDaListaSelecionados()
  {
    int[] linhas = tabelaSelecionados.getSelectedRows();
    if(linhas == null || linhas.length == 0)
    {
      JOptionPane.showMessageDialog(this, "Seleccione pelo menos um ponto na lista de revisão.");
      return;
    }

    for(int i = linhas.length - 1; i >= 0; i--)
      modeloSelecionados.removeRow(tabelaSelecionados.convertRowIndexToModel(linhas[i]));

    atualizarQuantidadeSelecionada();
  }

  private void limparListaSelecionados()
  {
    modeloSelecionados.setRowCount(0);
    atualizarQuantidadeSelecionada();
  }

  private void actualizarMudanca()
  {
    if(modeloSelecionados == null || modeloSelecionados.getRowCount() == 0)
    {
      JOptionPane.showMessageDialog(this, "Não há pontos seleccionados para alterar.");
      return;
    }

    String novoEstado = comboEstadoNovo.getSelectedItem().toString();

    List<String> codigos = new ArrayList<String>();
    for(int i = 0; i < modeloSelecionados.getRowCount(); i++)
      codigos.add(String.valueOf(modeloSelecionados.getValueAt(i, 0)));

    String erroValidacao = validarPontosSeleccionados();
    if(erroValidacao != null)
    {
      JOptionPane.showMessageDialog(this, erroValidacao);
      return;
    }

    int resposta = JOptionPane.showConfirmDialog(
      this,
      criarMensagemConfirmacao(novoEstado, codigos),
      "Confirmar Alteração",
      JOptionPane.YES_NO_OPTION
    );

    if(resposta != JOptionPane.YES_OPTION)
      return;

    aplicarMudancaEmLote(novoEstado);

    limparListaSelecionados();
    carregarPontosDistribuicao();
    cardLayout.show(panelCards, CARD_SELECCIONAR);
  }

  private String criarMensagemConfirmacao(String novoEstado, List<String> codigos)
  {
    int emCurso = 0;

    try
    {
      for(int i = 0; i < modeloSelecionados.getRowCount(); i++)
      {
        int codigoPd = Integer.parseInt(String.valueOf(modeloSelecionados.getValueAt(i, 0)));
        emCurso += PontoDistribuicaoSQL.contarAbastecimentosEstado(connection, codigoPd, "Em curso");
      }
    }
    catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return null;

      return "Pontos seleccionados: " + codigos.size() + "\n"
        + "Códigos: " + String.join(", ", codigos) + "\n"
        + "Novo estado: " + novoEstado + "\n\n"
        + "Não foi possível verificar abastecimentos associados antes da confirmação:\n"
        + MensagensInterface.formatarErro(ex) + "\n\n"
        + "Deseja confirmar esta alteração em lote?";
    }

    StringBuilder mensagem = new StringBuilder();
    mensagem.append("Pontos seleccionados: ").append(codigos.size()).append("\n");
    mensagem.append("Códigos: ").append(String.join(", ", codigos)).append("\n");
    mensagem.append("Novo estado: ").append(novoEstado).append("\n\n");

    if(("Inactivo".equalsIgnoreCase(novoEstado) || "Em Manutencao".equalsIgnoreCase(novoEstado)) && emCurso > 0)
    {
      mensagem.append("Existem ").append(emCurso).append(" abastecimento(s) em curso associado(s) aos pontos seleccionados.\n");
      mensagem.append("Ao prosseguir, esses abastecimentos serão cancelados.\n\n");
    }

    mensagem.append("Deseja confirmar esta alteração em lote?");
    return mensagem.toString();
  }

  private String validarPontosSeleccionados()
  {
    try
    {
      for(int i = 0; i < modeloSelecionados.getRowCount(); i++)
      {
        int codigoPd = Integer.parseInt(String.valueOf(modeloSelecionados.getValueAt(i, 0)));
        String estadoEsperado = String.valueOf(modeloSelecionados.getValueAt(i, 5));

        PontoDistribuicaoSQL.PreValidacaoAlteracaoEstado preValidacao =
          PontoDistribuicaoSQL.preValidarAlteracaoEstado(connection, codigoPd, estadoEsperado);

        if(!preValidacao.podeContinuar)
          return "O ponto " + codigoPd + " já foi alterado.\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem);

        if(preValidacao.estadoActualBanco == null)
          return "O ponto " + codigoPd + " retornou estado nulo na pré-validação.";

        if(!estadoEsperado.equalsIgnoreCase(preValidacao.estadoActualBanco.trim()))
          return "O ponto " + codigoPd + " já não está no estado esperado.\n"
            + "Esperado: " + estadoEsperado + "\n"
            + "Actual: " + preValidacao.estadoActualBanco;
      }
    }
    catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return null;

      return "Não foi possível validar os pontos seleccionados:\n" + MensagensInterface.formatarErro(ex);
    }

    return null;
  }

  private void aplicarMudancaEmLote(String novoEstado)
  {
    StringBuilder resumo = new StringBuilder();
    int sucesso = 0;
    int falhas = 0;

    for(int i = 0; i < modeloSelecionados.getRowCount(); i++)
    {
      try
      {
        int codigoPd = Integer.parseInt(String.valueOf(modeloSelecionados.getValueAt(i, 0)));
        String estadoEsperado = String.valueOf(modeloSelecionados.getValueAt(i, 5));

        PontoDistribuicaoSQL.ResultadoAlteracaoEstado resultado =
          PontoDistribuicaoSQL.alterarEstadoPonto(connection, codigoPd, estadoEsperado, novoEstado);

        if(resultado.falhou)
        {
          falhas++;
          resumo.append("Ponto ").append(codigoPd).append(": ").append(MensagensInterface.formatarMensagem(resultado.mensagem)).append("\n");
        }
        else
        {
          sucesso++;
          resumo.append("Ponto ").append(codigoPd).append(": ").append(MensagensInterface.formatarMensagem(resultado.mensagem)).append("\n");
        }
      }
      catch(Exception ex)
      {
        if(Resources.TratadorConexaoFechada.tratar(null, ex))
          return;

        falhas++;
        resumo.append("Ponto ")
          .append(String.valueOf(modeloSelecionados.getValueAt(i, 0)))
          .append(": ERRO - ")
          .append(MensagensInterface.formatarErro(ex))
          .append("\n");
      }
    }

    JOptionPane.showMessageDialog(
      this,
      "Alteração em lote concluída.\n"
        + "Sucesso: " + sucesso + "\n"
        + "Falhas: " + falhas + "\n\n"
        + resumo.toString()
    );
  }

  private void atualizarQuantidadeSelecionada()
  {
    int total = modeloSelecionados == null ? 0 : modeloSelecionados.getRowCount();
    if(labelQuantidadeSelecionada != null)
      labelQuantidadeSelecionada.setText(total + " ponto(s) seleccionado(s)");
    if(labelResumoSelecao != null)
      labelResumoSelecao.setText("Pontos seleccionados: " + total);
    if(buttonProceder != null)
      buttonProceder.setEnabled(total > 0);
    if(buttonConfirmar != null)
      buttonConfirmar.setEnabled(total > 0);
  }

  private class TratarPesquisa implements DocumentListener
  {
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
  }

  private class TratarSelecaoPontos implements ListSelectionListener
  {
    @Override
    public void valueChanged(ListSelectionEvent e)
    {
      if(!e.getValueIsAdjusting())
      {
        int seleccionados = tabelaPontos.getSelectedRowCount();
        if(labelQuantidadeSelecionada != null)
          labelQuantidadeSelecionada.setText(seleccionados + " ponto(s) seleccionado(s)");
        if(buttonProceder != null)
          buttonProceder.setEnabled(seleccionados > 0);
      }
    }
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      Object source = e.getSource();

      if(source == comboCriterio)
      {
        aplicarFiltro();
        return;
      }

      if(source == buttonActualizar)
      {
        carregarPontosDistribuicao();
        return;
      }

      if(source == buttonLimparFiltro)
      {
        fieldPesquisa.setText("");
        aplicarFiltro();
        return;
      }

      if(source == buttonVoltar)
      {
        voltarAoDashboard();
        return;
      }

      if(source == buttonProceder)
      {
        if(tabelaPontos.getSelectedRowCount() == 0)
        {
          JOptionPane.showMessageDialog(JPanel_AlterarEstadoPontoDistribuicao.this,
            "Seleccione pelo menos um ponto de distribuição.");
          return;
        }

        actualizarSeleccaoParaRevisao();
        cardLayout.show(panelCards, CARD_REVISAO);
        return;
      }

      if(source == buttonVoltarSelecao)
      {
        cardLayout.show(panelCards, CARD_SELECCIONAR);
        return;
      }

      if(source == buttonRemoverSelecionados)
      {
        removerDaListaSelecionados();
        return;
      }

      if(source == buttonLimparLista)
      {
        limparListaSelecionados();
        return;
      }

      if(source == buttonConfirmar)
      {
        actualizarMudanca();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }
}
