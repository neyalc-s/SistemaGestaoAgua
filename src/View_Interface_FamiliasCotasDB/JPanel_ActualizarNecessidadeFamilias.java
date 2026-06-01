package View_Interface_FamiliasCotasDB;

import Repository_SQL.FamiliasCotasDB.ActualizarNecessidadeFamiliasSQL;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class JPanel_ActualizarNecessidadeFamilias extends JPanel
{
  private static final String CARD_FAMILIAS = "famílias";
  private static final String CARD_NECESSIDADES = "necessidades";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final java.awt.CardLayout cardLayout = new java.awt.CardLayout();
  private final JPanel panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaFamilias;
  private JTable tabelaAssociadas;
  private JTable tabelaDisponiveis;
  private DefaultTableModel modeloFamilias;
  private DefaultTableModel modeloAssociadas;
  private DefaultTableModel modeloDisponiveis;
  private TableRowSorter<DefaultTableModel> sorterFamilias;
  private TableRowSorter<DefaultTableModel> sorterAssociadas;
  private TableRowSorter<DefaultTableModel> sorterDisponiveis;
  private JTextField fieldPesquisaFamilias;
  private JTextField fieldPesquisaAssociadas;
  private JTextField fieldPesquisaDisponiveis;
  private JComboBox<String> comboPesquisaFamilias;
  private JTextField fieldCodigoFamilia;
  private JTextField fieldNomeFamilia;
  private JTextField fieldResumoNecessidades;
  private JButton buttonAvancar;
  private JButton buttonVoltarDashboard;
  private JButton buttonVoltar;
  private JButton buttonAdicionar;
  private JButton buttonRemover;
  private JButton buttonActualizar;
  private JButton buttonActualizarNecessidades;
  private JButton buttonInserirNecessidade;

  public JPanel_ActualizarNecessidadeFamilias(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_ActualizarNecessidadeFamilias(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_ActualizarNecessidadeFamilias(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_ActualizarNecessidadeFamilias(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));

    panelCards.add(criarCardFamilias(), CARD_FAMILIAS);
    panelCards.add(criarCardNecessidades(), CARD_NECESSIDADES);
    add(panelCards, BorderLayout.CENTER);

    if(carregarTabelasAutomaticamente)
    {
      carregarFamilias();
    }
  }

  private JPanel criarCardFamilias()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Actualizar Necessidades da Família",
        "<html>Seleccione uma família para adicionar ou remover necessidades associadas.</html>");
    card.add(criarCentroFamilias(), BorderLayout.CENTER);
    card.add(criarRodapeFamilias(), BorderLayout.SOUTH);
    painelExterno.add(card, criarGbcCard());
    return painelExterno;
  }

  private JPanel criarCardNecessidades()
  {
    JPanel painelExterno = criarPainelExterno();
    InterfaceGraficaUtils.RoundedPanel card = criarCardBase("Gerir Necessidades",
        "<html>Adicione necessidades disponíveis ou remova necessidades já associadas à família seleccionada.</html>");
    card.add(criarCentroNecessidades(), BorderLayout.CENTER);
    card.add(criarRodapeNecessidades(), BorderLayout.SOUTH);
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
          "Código Família", "Responsável", "Contacto", "Membros", "Estado", "Ponto", "Aldeia"
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

  private JPanel criarCentroNecessidades()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 14));
    painel.add(criarResumoFamilia(), BorderLayout.NORTH);

    JPanel tabelas = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    adicionarPainelTabela(tabelas, 0, "Necessidades da Família", criarPainelAssociadas());
    adicionarPainelTabela(tabelas, 1, "Necessidades Disponíveis", criarPainelDisponiveis());
    painel.add(tabelas, BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarResumoFamilia()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    fieldCodigoFamilia = criarCampoReadonly();
    fieldNomeFamilia = criarCampoReadonly();
    fieldResumoNecessidades = criarCampoReadonly();

    adicionarLinhaResumo(painel, 0, "Código da família:", fieldCodigoFamilia);
    adicionarLinhaResumo(painel, 1, "Responsável:", fieldNomeFamilia);
    adicionarLinhaResumo(painel, 2, "Necessidades associadas:", fieldResumoNecessidades);
    return painel;
  }

  private JPanel criarPainelAssociadas()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    fieldPesquisaAssociadas = criarCampoPesquisa();
    fieldPesquisaAssociadas.getDocument().addDocumentListener(new TratarPesquisaAssociadas());
    painel.add(criarLinhaPesquisa(fieldPesquisaAssociadas), BorderLayout.NORTH);

    modeloAssociadas = criarModeloNecessidades();
    tabelaAssociadas = criarTabela(modeloAssociadas);
    tabelaAssociadas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sorterAssociadas = new TableRowSorter<DefaultTableModel>(modeloAssociadas);
    tabelaAssociadas.setRowSorter(sorterAssociadas);
    painel.add(criarScroll(tabelaAssociadas, new Dimension(520, 360)), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarPainelDisponiveis()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    fieldPesquisaDisponiveis = criarCampoPesquisa();
    fieldPesquisaDisponiveis.getDocument().addDocumentListener(new TratarPesquisaDisponiveis());
    painel.add(criarLinhaPesquisa(fieldPesquisaDisponiveis), BorderLayout.NORTH);

    modeloDisponiveis = criarModeloNecessidades();
    tabelaDisponiveis = criarTabela(modeloDisponiveis);
    tabelaDisponiveis.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    sorterDisponiveis = new TableRowSorter<DefaultTableModel>(modeloDisponiveis);
    tabelaDisponiveis.setRowSorter(sorterDisponiveis);
    painel.add(criarScroll(tabelaDisponiveis, new Dimension(520, 360)), BorderLayout.CENTER);
    return painel;
  }

  private DefaultTableModel criarModeloNecessidades()
  {
    return new DefaultTableModel(new String[]
      {
          "Código", "Descrição"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
  }

  private void adicionarPainelTabela(JPanel painel, int coluna, String titulo, JPanel tabelaPanel)
  {
    JPanel wrapper = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    wrapper.add(InterfaceGraficaUtils.criarLabel(titulo, InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_AZUL), BorderLayout.NORTH);
    wrapper.add(tabelaPanel, BorderLayout.CENTER);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = coluna;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(0, coluna == 0 ? 0 : 12, 0, coluna == 0 ? 12 : 0);
    painel.add(wrapper, gbc);
  }

  private JPanel criarPesquisaFamilias()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    fieldPesquisaFamilias = criarCampoPesquisa();
    fieldPesquisaFamilias.getDocument().addDocumentListener(new TratarPesquisaFamilias());
    comboPesquisaFamilias = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Família", "Responsável", "Estado", "Aldeia"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(180, 36));
    comboPesquisaFamilias.addActionListener(tratarButtons);
    buttonActualizar = criarBotao("Mostrar Todos", 150);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisaFamilias);
    painel.add(comboPesquisaFamilias);
    painel.add(buttonActualizar);
    return painel;
  }

  private JPanel criarLinhaPesquisa(JTextField campo)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(campo);
    return painel;
  }

  private JPanel criarRodapeFamilias()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Seleccione uma família para continuar.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar", 120);
    buttonAvancar = criarBotao("Proceder", 145);
    buttonVoltarDashboard.addActionListener(tratarButtons);
    botoes.add(buttonVoltarDashboard);
    botoes.add(buttonAvancar);
    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JPanel criarRodapeNecessidades()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("As tabelas são actualizadas após cada operação.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltar = criarBotao("Voltar", 120);
    buttonRemover = criarBotao("Remover", 130);
    buttonAdicionar = criarBotao("Adicionar", 130);
    buttonActualizarNecessidades = criarBotao("Mostrar Todos", 150);
    buttonInserirNecessidade = criarBotao("Nova Necess.", 145);
    botoes.add(buttonVoltar);
    botoes.add(buttonActualizarNecessidades);
    botoes.add(buttonInserirNecessidade);
    botoes.add(buttonRemover);
    botoes.add(buttonAdicionar);
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

  private JTextField criarCampoPesquisa()
  {
    return InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(250, 36));
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
    Resources.IconesInterface.aplicarEfeitoHoverBotaoFamilias(botao);
    botao.addActionListener(tratarButtons);
    return botao;
  }

  private void adicionarLinhaResumo(JPanel painel, int linha, String label, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(5, 0, 5, 14);
    painel.add(InterfaceGraficaUtils.criarLabel(label, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 0, 5, 0);
    painel.add(campo, gbc);
  }

  private void carregarFamilias()
  {
    try
    {
      ActualizarNecessidadeFamiliasSQL.carregarFamilias(connection, modeloFamilias);
      aplicarFiltroFamilias();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar as famílias.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarNecessidadesFamilia()
  {
    int codigoFamilia = getCodigoFamiliaSelecionada();
    try
    {
      ActualizarNecessidadeFamiliasSQL.carregarNecessidadesFamilia(connection, modeloAssociadas, codigoFamilia);
      ActualizarNecessidadeFamiliasSQL.carregarNecessidadesDisponiveis(connection, modeloDisponiveis, codigoFamilia);
      aplicarFiltroAssociadas();
      aplicarFiltroDisponiveis();
      actualizarResumoNecessidades();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar necessidades:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private boolean prepararFamiliaSelecionada()
  {
    if(tabelaFamilias.getSelectedRow() == -1)
    {
      JOptionPane.showMessageDialog(this, "Seleccione uma família.");
      return false;
    }

    int linhaModel = tabelaFamilias.convertRowIndexToModel(tabelaFamilias.getSelectedRow());
    fieldCodigoFamilia.setText(valorTabela(modeloFamilias, linhaModel, 0));
    fieldNomeFamilia.setText(valorTabela(modeloFamilias, linhaModel, 1));
    carregarNecessidadesFamilia();
    return true;
  }

  private int getCodigoFamiliaSelecionada()
  {
    return Integer.parseInt(fieldCodigoFamilia.getText().trim());
  }

  private void actualizarResumoNecessidades()
  {
    if(modeloAssociadas.getRowCount() == 0)
    {
      fieldResumoNecessidades.setText("Nenhuma necessidade associada");
      return;
    }

    StringBuilder texto = new StringBuilder();
    for(int i = 0; i < modeloAssociadas.getRowCount(); i++)
    {
      if(i > 0)
        texto.append(", ");
      texto.append(modeloAssociadas.getValueAt(i, 0));
    }
    fieldResumoNecessidades.setText(texto.toString());
  }

  private void adicionarNecessidades()
  {
    actualizarNecessidades("ADICIONAR", tabelaDisponiveis, modeloDisponiveis, "adicionar");
  }

  private void removerNecessidades()
  {
    actualizarNecessidades("REMOVER", tabelaAssociadas, modeloAssociadas, "remover");
  }

  private void inserirNovaNecessidade()
  {
    String descricao = JOptionPane.showInputDialog(this, "Descrição da nova necessidade:", "Nova Necessidade",
        JOptionPane.PLAIN_MESSAGE);

    if(descricao == null)
      return;

    descricao = descricao.trim();
    if(descricao.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "A descrição da necessidade é obrigatória.");
      return;
    }

    try
    {
      ActualizarNecessidadeFamiliasSQL.inserirNecessidade(connection, descricao);
      carregarNecessidadesFamilia();
      JOptionPane.showMessageDialog(this, "Necessidade inserida com sucesso.");
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível inserir necessidade:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void actualizarNecessidades(String operacao, JTable tabela, DefaultTableModel modelo, String textoOperacao)
  {
    List<Integer> codigos = getCodigosSelecionados(tabela, modelo);
    if(codigos.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Seleccione pelo menos uma necessidade para " + textoOperacao + ".");
      return;
    }

    int codigoFamilia = getCodigoFamiliaSelecionada();

    try
    {
      ActualizarNecessidadeFamiliasSQL.ResultadoPreValidacao preValidacao =
          ActualizarNecessidadeFamiliasSQL.preValidarActualizacao(connection, codigoFamilia, codigos, operacao);

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Operação não permitida:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Família: " + codigoFamilia + "\nNecessidades: " + codigos + "\n\nDeseja " + textoOperacao
              + " estas necessidades?",
          "Confirmar Actualização", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      ActualizarNecessidadeFamiliasSQL.ResultadoActualizacao resultado =
          ActualizarNecessidadeFamiliasSQL.actualizarNecessidades(connection, codigoFamilia, codigos, operacao);

      if(!resultado.actualizado)
      {
        JOptionPane.showMessageDialog(this, "Necessidades não actualizadas:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          "Necessidades actualizadas com sucesso.\nLinhas afectadas: " + resultado.afectadas);
      carregarNecessidadesFamilia();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível actualizar necessidades:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private List<Integer> getCodigosSelecionados(JTable tabela, DefaultTableModel modelo)
  {
    List<Integer> codigos = new ArrayList<Integer>();
    int[] linhas = tabela.getSelectedRows();

    for(int linhaView : linhas)
    {
      int linhaModel = tabela.convertRowIndexToModel(linhaView);
      codigos.add(Integer.valueOf(modelo.getValueAt(linhaModel, 0).toString()));
    }
    return codigos;
  }

  private String valorTabela(DefaultTableModel modelo, int linha, int coluna)
  {
    Object valor = modelo.getValueAt(linha, coluna);
    return valor == null ? "" : valor.toString();
  }

  private void aplicarFiltroFamilias()
  {
    if(sorterFamilias == null || fieldPesquisaFamilias == null || comboPesquisaFamilias == null)
      return;

    String texto = fieldPesquisaFamilias.getText().trim();
    if(texto.isEmpty())
    {
      sorterFamilias.setRowFilter(null);
      return;
    }

    int coluna = 0;
    String criterio = comboPesquisaFamilias.getSelectedItem().toString();
    if("Responsável".equals(criterio))
      coluna = 1;
    else if("Estado".equals(criterio))
      coluna = 4;
    else if("Aldeia".equals(criterio))
      coluna = 6;

    sorterFamilias.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private void aplicarFiltroAssociadas()
  {
    aplicarFiltroNecessidades(sorterAssociadas, fieldPesquisaAssociadas);
  }

  private void aplicarFiltroDisponiveis()
  {
    aplicarFiltroNecessidades(sorterDisponiveis, fieldPesquisaDisponiveis);
  }

  private void aplicarFiltroNecessidades(TableRowSorter<DefaultTableModel> sorter, JTextField field)
  {
    if(sorter == null || field == null)
      return;

    String texto = field.getText().trim();
    if(texto.isEmpty())
      sorter.setRowFilter(null);
    else
      sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
  }

  private final class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      Object source = event.getSource();

      if(source == buttonActualizar)
      {
        carregarFamilias();
      }
      else if(source == comboPesquisaFamilias)
      {
        aplicarFiltroFamilias();
      }
      else if(source == buttonAvancar)
      {
        if(prepararFamiliaSelecionada())
          cardLayout.show(panelCards, CARD_NECESSIDADES);
      }
      else if(source == buttonVoltarDashboard)
      {
        voltarAoDashboard();
      }
      else if(source == buttonVoltar)
      {
        cardLayout.show(panelCards, CARD_FAMILIAS);
      }
      else if(source == buttonActualizarNecessidades)
      {
        carregarNecessidadesFamilia();
      }
      else if(source == buttonInserirNecessidade)
      {
        inserirNovaNecessidade();
      }
      else if(source == buttonAdicionar)
      {
        adicionarNecessidades();
      }
      else if(source == buttonRemover)
      {
        removerNecessidades();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }

  private final class TratarPesquisaFamilias implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroFamilias();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroFamilias();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroFamilias();
    }
  }

  private final class TratarPesquisaAssociadas implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroAssociadas();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroAssociadas();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroAssociadas();
    }
  }

  private final class TratarPesquisaDisponiveis implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltroDisponiveis();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltroDisponiveis();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltroDisponiveis();
    }
  }
}
