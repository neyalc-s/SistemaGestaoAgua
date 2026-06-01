package View_Interface_AdministradorDB;

import Resources.InterfaceGraficaUtils;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class JPanel_NotificacoesBD extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaNotificacoes;
  private DefaultTableModel modeloNotificacoes;
  private TableRowSorter<DefaultTableModel> sorterNotificacoes;
  private JTextField fieldPesquisa;
  private JComboBox<String> comboFiltroSeveridade;
  private JButton buttonActualizar;
  private JButton buttonLimpar;
  private JLabel labelCriticos;
  private JLabel labelAvisos;
  private JLabel labelPendentes;

  public JPanel_NotificacoesBD(Connection connection)
  {
    this.connection = connection;
    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(criarTopo(), BorderLayout.NORTH);
    card.add(criarCentro(), BorderLayout.CENTER);
    card.add(criarRodape(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(18, 18, 18, 18);
    add(card, gbc);

    carregarNotificacoesIniciais();
  }

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Notificações da Base de Dados",
        "<html>Alertas críticos, avisos e eventos recebidos dos nós remotos da base de dados distribuída.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarCentro()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 14));
    painel.add(criarResumo(), BorderLayout.NORTH);
    painel.add(criarTabelaComControles(), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarResumo()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 12, 0));
    labelCriticos = criarResumoLabel("Críticos: 0");
    labelAvisos = criarResumoLabel("Avisos: 0");
    labelPendentes = criarResumoLabel("Pendentes: 0");
    painel.add(labelCriticos);
    painel.add(labelAvisos);
    painel.add(labelPendentes);
    return painel;
  }

  private JLabel criarResumoLabel(String texto)
  {
    JLabel label = new JLabel(texto, SwingConstants.CENTER);
    label.setFont(InterfaceGraficaUtils.FONT_LABEL);
    label.setForeground(InterfaceGraficaUtils.COR_TEXTO);
    label.setPreferredSize(new Dimension(170, 34));
    label.setBorder(javax.swing.BorderFactory.createLineBorder(InterfaceGraficaUtils.COR_BORDA_TABELA));
    return label;
  }

  private JPanel criarTabelaComControles()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    painel.add(criarControles(), BorderLayout.NORTH);

    modeloNotificacoes = new DefaultTableModel(new String[]
      {
          "Data/Hora", "Nó Origem", "BD Origem", "Severidade", "Categoria", "Mensagem", "Estado"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };

    tabelaNotificacoes = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaNotificacoes.setModel(modeloNotificacoes);
    sorterNotificacoes = new TableRowSorter<DefaultTableModel>(modeloNotificacoes);
    tabelaNotificacoes.setRowSorter(sorterNotificacoes);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaNotificacoes,
        InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1180, 500));
    painel.add(scroll, BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarControles()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(270, 36));
    fieldPesquisa.getDocument().addDocumentListener(new TratarPesquisa());

    comboFiltroSeveridade = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Todas", "Critico", "Aviso", "Informativo"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(150, 36));
    comboFiltroSeveridade.addActionListener(tratarButtons);

    buttonActualizar = criarBotao("Mostrar Todos");
    buttonLimpar = criarBotao("Limpar");

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisa);
    painel.add(InterfaceGraficaUtils.criarLabel("Severidade:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(comboFiltroSeveridade);
    painel.add(buttonActualizar);
    painel.add(buttonLimpar);
    return painel;
  }

  private JPanel criarRodape()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel nota = InterfaceGraficaUtils.criarLabel(
        "Painel preparado para receber notificações dinamicamente a partir da aplicação ou de integrações futuras.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);
    painel.add(nota, BorderLayout.WEST);
    return painel;
  }

  private JButton criarBotao(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(130, 36));
    IconesInterface.aplicarIconeBotao(botao);
    botao.addActionListener(tratarButtons);
    return botao;
  }

  private void carregarNotificacoesIniciais()
  {
    modeloNotificacoes.setRowCount(0);
    adicionarNotificacao("2026-04-29 08:15", "NO_CENTRAL", "BD_CENTRAL", "Critico", "Sincronizacao",
        "Falha ao sincronizar retiradas pendentes do no local.", "Pendente");
    adicionarNotificacao("2026-04-29 08:22", "NO_DISTRIB_01", "BD_DISTRIB_01", "Aviso", "Replicacao",
        "Replica de cotas com atraso superior a 10 minutos.", "Em analise");
    adicionarNotificacao("2026-04-29 09:05", "NO_QUALIDADE", "BD_QUALIDADE", "Critico", "Qualidade da Água",
        "Medição de turbidez acima do limite no recurso hídrico 2001.", "Pendente");
    adicionarNotificacao("2026-04-29 09:40", "NO_MANUT", "BD_MANUTENCAO", "Aviso", "Ponto de Distribuição",
        "Ponto de distribuição marcado como Em Manutenção por equipe técnica remota.", "Resolvido");
    adicionarNotificacao("2026-04-29 10:10", "NO_CENTRAL", "BD_CENTRAL", "Informativo", "Auditoria",
        "Novo recurso hídrico registado com 5 medicoes iniciais.", "Registado");
    adicionarNotificacao("2026-04-29 10:35", "NO_DISTRIB_02", "BD_DISTRIB_02", "Critico", "Disponibilidade",
        "Nó remoto indisponível durante validação de associação de famílias.", "Pendente");
    adicionarNotificacao("2026-04-29 11:00", "NO_CENTRAL", "BD_CENTRAL", "Aviso", "Capacidade",
        "Volume actual de ponto de distribuição abaixo do limite operacional.", "Em analise");
    adicionarNotificacao("2026-04-29 11:25", "NO_QUALIDADE", "BD_QUALIDADE", "Informativo", "Analista",
        "Analista de qualidade validou medições do parâmetro pH.", "Registado");
    actualizarResumo();
    aplicarFiltro();
  }

  public void receberNotificacao(String dataHora, String noOrigem, String bdOrigem, String severidade,
      String categoria, String mensagem, String estado)
  {
    adicionarNotificacao(dataHora, noOrigem, bdOrigem, severidade, categoria, mensagem, estado);
    actualizarResumo();
    aplicarFiltro();
  }

  private void adicionarNotificacao(String dataHora, String noOrigem, String bdOrigem, String severidade,
      String categoria, String mensagem, String estado)
  {
    modeloNotificacoes.addRow(new Object[]
      {
          dataHora, noOrigem, bdOrigem, severidade, categoria, mensagem, estado
      });
  }

  private void actualizarResumo()
  {
    int criticos = 0;
    int avisos = 0;
    int pendentes = 0;

    for(int i = 0; i < modeloNotificacoes.getRowCount(); i++)
    {
      String severidade = modeloNotificacoes.getValueAt(i, 3).toString();
      String estado = modeloNotificacoes.getValueAt(i, 6).toString();
      if("Critico".equalsIgnoreCase(severidade))
        criticos++;
      if("Aviso".equalsIgnoreCase(severidade))
        avisos++;
      if("Pendente".equalsIgnoreCase(estado))
        pendentes++;
    }

    labelCriticos.setText("Críticos: " + criticos);
    labelAvisos.setText("Avisos: " + avisos);
    labelPendentes.setText("Pendentes: " + pendentes);
  }

  private void aplicarFiltro()
  {
    if(sorterNotificacoes == null)
      return;

    String texto = fieldPesquisa == null ? "" : fieldPesquisa.getText().trim();
    String severidade = comboFiltroSeveridade == null ? "Todas" : comboFiltroSeveridade.getSelectedItem().toString();

    java.util.List<RowFilter<Object, Object>> filtros = new java.util.ArrayList<RowFilter<Object, Object>>();
    if(!texto.isEmpty())
      filtros.add(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));

    if(!"Todas".equals(severidade))
      filtros.add(RowFilter.regexFilter("^" + Pattern.quote(severidade) + "$", 3));

    if(filtros.isEmpty())
      sorterNotificacoes.setRowFilter(null);
    else
      sorterNotificacoes.setRowFilter(RowFilter.andFilter(filtros));
  }

  private final class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == buttonActualizar)
      {
        carregarNotificacoesIniciais();
      }
      else if(event.getSource() == buttonLimpar)
      {
        fieldPesquisa.setText("");
        comboFiltroSeveridade.setSelectedIndex(0);
        aplicarFiltro();
      }
      else if(event.getSource() == comboFiltroSeveridade)
      {
        aplicarFiltro();
      }
    }
  }

  private final class TratarPesquisa implements DocumentListener
  {
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }

    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }

    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }
  }
}
