package View_Interface_AdministradorDB;

import Repository_SQL.AdministradorDB.AuditoriaSessoesFuncionariosSQL;
import Repository_SQL.AdministradorDB.AuditoriaSessoesFuncionariosSQL.FiltroAuditoria;
import Repository_SQL.AdministradorDB.AuditoriaSessoesFuncionariosSQL.ResultadoRefresh;
import Repository_SQL.AdministradorDB.AuditoriaSessoesFuncionariosSQL.SessaoAuditoria;
import Resources.IconesInterface;
import Resources.InterfaceGraficaUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JPanel_AuditoriaSessoesFuncionarios extends JPanel
{
  private static final String[] NOS =
    {
        "AdministradorDB", "FamiliasCotasDB", "DistribuicaoConsumoDB", "TransferenciasRecursosDB", "EquipesGestaoDB"
    };

  private static final String CRITERIO_CODIGO_FUNCIONARIO = "Código Funcionário";
  private static final String CRITERIO_NOME_FUNCIONARIO = "Nome Funcionário";
  private static final String CRITERIO_USERNAME_ORACLE = "Username Oracle";

  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();
  private final SimpleDateFormat formatoDataHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private final Map<String, JTable> tabelasPorNo = new HashMap<String, JTable>();
  private final javax.swing.Timer timerPesquisa;

  private JTextField fieldPesquisa;
  private JComboBox<String> comboCriterioPesquisa;
  private JComboBox<String> comboEstadoSessao;
  private JTabbedPane tabsNos;
  private JLabel labelResumo;
  private JLabel labelEstadoPesquisa;
  private JButton buttonPesquisar;
  private JButton buttonMostrarTodos;
  private JButton buttonLimpar;
  private JButton buttonActualizarAuditoria;
  private boolean limparEmCurso = false;

  public JPanel_AuditoriaSessoesFuncionarios(Connection connection)
  {
    this(connection, false);
  }

  public JPanel_AuditoriaSessoesFuncionarios(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this.connection = connection;
    this.timerPesquisa = new javax.swing.Timer(350, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          pesquisarAutomaticamente();
        }
      });
    timerPesquisa.setRepeats(false);

    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(22, 22, 22, 22));

    add(criarConteudo(), BorderLayout.CENTER);
    configurarPesquisaAutomatica();

    if(carregarTabelasAutomaticamente)
      carregarSessoesDoTabActivo(false);
    else
      mostrarEstadoPesquisa("Pesquise sessões ou clique Mostrar Todos para carregar dados.");
  }

  private JPanel criarConteudo()
  {
    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(28, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(26, 26, 26, 26), new Dimension(1320, 780));

    card.add(criarTopo(), BorderLayout.NORTH);
    card.add(criarCentro(), BorderLayout.CENTER);
    card.add(criarRodape(), BorderLayout.SOUTH);
    return card;
  }

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Auditoria de Sessões dos Funcionários",
        "<html>Consulta consolidada das sessões locais por nó.</html>", InterfaceGraficaUtils.FONT_TITULO,
        InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO, InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarCentro()
  {
    JPanel centro = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 14));
    centro.add(criarPainelPesquisa(), BorderLayout.NORTH);
    centro.add(criarTabs(), BorderLayout.CENTER);
    return centro;
  }

  private JPanel criarPainelPesquisa()
  {
    JPanel painelPesquisa = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 6));
    JPanel linhaPesquisa = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    JLabel labelPesquisar = InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO);

    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(220, 36));

    comboCriterioPesquisa = InterfaceGraficaUtils.criarCombo(new String[]
      {
          CRITERIO_CODIGO_FUNCIONARIO, CRITERIO_NOME_FUNCIONARIO, CRITERIO_USERNAME_ORACLE
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(190, 36));

    comboEstadoSessao = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Todos", "ABERTA", "FECHADA", "EXPIRADA"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(135, 36));

    buttonPesquisar = criarBotao("Pesquisar", 130, 36);
    buttonMostrarTodos = criarBotao("Mostrar Todos", 160, 36);
    buttonLimpar = criarBotao("Limpar", 115, 36);

    labelEstadoPesquisa = InterfaceGraficaUtils.criarLabel("", InterfaceGraficaUtils.FONT_AJUDA,
        InterfaceGraficaUtils.COR_SUBTEXTO);

    linhaPesquisa.add(labelPesquisar);
    linhaPesquisa.add(fieldPesquisa);
    linhaPesquisa.add(comboCriterioPesquisa);
    linhaPesquisa.add(comboEstadoSessao);
    linhaPesquisa.add(buttonPesquisar);
    linhaPesquisa.add(buttonMostrarTodos);
    linhaPesquisa.add(buttonLimpar);

    painelPesquisa.add(linhaPesquisa, BorderLayout.NORTH);
    painelPesquisa.add(labelEstadoPesquisa, BorderLayout.SOUTH);
    return painelPesquisa;
  }

  private JPanel criarTabs()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    labelResumo = InterfaceGraficaUtils.criarLabel("Sessões encontradas: 0", InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_TEXTO);
    tabsNos = new JTabbedPane();

    for(int i = 0; i < NOS.length; i++)
    {
      JTable tabela = criarTabelaComMensagemVazia("Pesquise sessões ou clique Mostrar Todos para carregar dados.",
          "Nenhuma sessão encontrada no filtro actual. Altere o texto ou clique Pesquisar.");
      tabela.setModel(criarModeloVazio());
      tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      tabelasPorNo.put(NOS[i], tabela);
      tabsNos.addTab(NOS[i], InterfaceGraficaUtils.criarScrollTabela(tabela, InterfaceGraficaUtils.COR_BORDA_TABELA));
    }

    tabsNos.addChangeListener(new ChangeListener()
      {
        public void stateChanged(ChangeEvent e)
        {
          carregarSessoesDoTabActivo(true);
        }
      });

    painel.add(labelResumo, BorderLayout.NORTH);
    painel.add(tabsNos, BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarRodape()
  {
    JPanel rodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));

    buttonActualizarAuditoria = criarBotao("Actualizar Auditoria", 185, 40);

    botoes.add(buttonActualizarAuditoria);

    rodape.add(botoes, BorderLayout.EAST);
    return rodape;
  }

  private JButton criarBotao(String texto, int largura, int altura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, altura));
    IconesInterface.aplicarIconeBotao(botao);
    botao.addActionListener(tratarButtons);
    return botao;
  }

  private DefaultTableModel criarModeloVazio()
  {
    return new DefaultTableModel(new Object[]
      {
          "Código Sessão", "Código Funcionário", "Nome Funcionário", "Username Oracle", "Data Início", "Data Fim",
          "Estado Sessão", "Modo Login"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
  }

  private JTable criarTabelaComMensagemVazia(final String mensagemSemDados, final String mensagemSemFiltro)
  {
    JTable tabela = new JTable()
      {
        protected void paintComponent(Graphics g)
        {
          super.paintComponent(g);

          if(getRowCount() > 0)
            return;

          boolean temDadosCarregados = getModel() != null && getModel().getRowCount() > 0;
          String mensagem = temDadosCarregados ? mensagemSemFiltro : mensagemSemDados;

          Graphics2D g2 = (Graphics2D) g.create();
          g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g2.setFont(InterfaceGraficaUtils.FONT_AJUDA.deriveFont(Font.BOLD, 15f));
          g2.setColor(InterfaceGraficaUtils.COR_SUBTEXTO);

          FontMetrics metrics = g2.getFontMetrics();
          int x = Math.max(16, (getWidth() - metrics.stringWidth(mensagem)) / 2);
          int y = Math.max(metrics.getHeight(), (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent());

          g2.drawString(mensagem, x, y);
          g2.dispose();
        }
      };

    InterfaceGraficaUtils.configurarTabela(tabela, InterfaceGraficaUtils.FONT_TABELA, InterfaceGraficaUtils.FONT_HEADER,
        InterfaceGraficaUtils.COR_GRID_TABELA, InterfaceGraficaUtils.COR_SELECAO_TABELA,
        InterfaceGraficaUtils.COR_TEXTO);
    tabela.setFillsViewportHeight(true);
    return tabela;
  }

  private void configurarPesquisaAutomatica()
  {
    DocumentListener listener = new DocumentListener()
      {
        public void insertUpdate(DocumentEvent e)
        {
          agendarPesquisa();
        }

        public void removeUpdate(DocumentEvent e)
        {
          agendarPesquisa();
        }

        public void changedUpdate(DocumentEvent e)
        {
          agendarPesquisa();
        }
      };

    fieldPesquisa.getDocument().addDocumentListener(listener);
    comboCriterioPesquisa.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          agendarPesquisa();
        }
      });
    comboEstadoSessao.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          agendarPesquisa();
        }
      });
  }

  private void agendarPesquisa()
  {
    if(limparEmCurso)
      return;
    timerPesquisa.restart();
  }

  private void pesquisarAutomaticamente()
  {
    carregarSessoesDoTabActivo(true);
  }

  private void carregarSessoesDoTabActivo(boolean manterMensagemInicial)
  {
    carregarSessoes(lerFiltro(), manterMensagemInicial);
  }

  private void carregarSessoes(FiltroAuditoria filtro, boolean manterMensagemInicial)
  {
    String nomeNo = getNoActivo();
    JTable tabela = tabelasPorNo.get(nomeNo);

    try
    {
      List<SessaoAuditoria> sessoes = AuditoriaSessoesFuncionariosSQL.pesquisarPorNo(connection, filtro, nomeNo);
      DefaultTableModel model = criarModeloVazio();
      for(SessaoAuditoria sessao : sessoes)
        model.addRow(new Object[]
          {
              sessao.codigoSessao, sessao.codigoFuncionario, sessao.nomeFuncionario, sessao.usernameOracle,
              formatarData(sessao.dataInicio), formatarData(sessao.dataFim), sessao.estadoSessao, sessao.modoLogin
          });

      tabela.setModel(model);
      tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      InterfaceGraficaUtils.ajustarLarguraColunas(tabela, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER);
      labelResumo.setText("Sessões encontradas: " + sessoes.size());

      if(!manterMensagemInicial || sessoes.size() > 0)
        mostrarEstadoPesquisa("Sessões encontradas: " + sessoes.size());
      else
        mostrarEstadoPesquisa("Nenhuma sessão encontrada no filtro actual.");
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      mostrarEstadoPesquisa("Não foi possível carregar a auditoria deste nó.");
    }
  }

  private String getNoActivo()
  {
    int index = tabsNos.getSelectedIndex();
    if(index < 0)
      return NOS[0];
    return tabsNos.getTitleAt(index);
  }

  private FiltroAuditoria lerFiltro()
  {
    return new FiltroAuditoria(fieldPesquisa.getText(), comboCriterioPesquisa.getSelectedItem().toString(),
        getNoActivo(), comboEstadoSessao.getSelectedItem().toString());
  }

  private void actualizarAuditoria()
  {
    try
    {
      ResultadoRefresh resultado = AuditoriaSessoesFuncionariosSQL.actualizarAuditoria(connection);
      mostrarEstadoPesquisa(normalizarMensagemRefresh(resultado));
      carregarSessoes(lerFiltro(), true);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      mostrarEstadoPesquisa("Não foi possível actualizar auditoria.");
    }
  }

  private String normalizarMensagemRefresh(ResultadoRefresh resultado)
  {
    String mensagem = resultado.mensagem == null ? "" : resultado.mensagem.toLowerCase();
    if(mensagem.indexOf("parcial") >= 0 || mensagem.indexOf("indispon") >= 0)
      return "Auditoria actualizada parcialmente. Alguns nós estão temporariamente indisponíveis.";
    if(resultado.sucesso)
      return "Auditoria actualizada com sucesso.";
    return "Não foi possível actualizar auditoria.";
  }

  private void mostrarTodos()
  {
    limparCamposSemMensagem();
    carregarSessoes(new FiltroAuditoria(null, CRITERIO_CODIGO_FUNCIONARIO, getNoActivo(), "Todos"), false);
  }

  private void limparCampos()
  {
    limparCamposSemMensagem();
    mostrarEstadoPesquisa("Filtros limpos. Clique Mostrar Todos ou faça uma nova pesquisa.");
  }

  private void limparCamposSemMensagem()
  {
    limparEmCurso = true;
    fieldPesquisa.setText("");
    comboCriterioPesquisa.setSelectedItem(CRITERIO_CODIGO_FUNCIONARIO);
    comboEstadoSessao.setSelectedItem("Todos");
    limparEmCurso = false;
  }

  private void mostrarEstadoPesquisa(String mensagem)
  {
    if(labelEstadoPesquisa != null)
      labelEstadoPesquisa.setText(mensagem == null ? "" : mensagem);
  }

  private String formatarData(java.util.Date data)
  {
    if(data == null)
      return "";
    return formatoDataHora.format(data);
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == buttonActualizarAuditoria)
        actualizarAuditoria();
      else if(event.getSource() == buttonPesquisar)
        carregarSessoes(lerFiltro(), false);
      else if(event.getSource() == buttonMostrarTodos)
        mostrarTodos();
      else if(event.getSource() == buttonLimpar)
        limparCampos();
    }
  }
}
