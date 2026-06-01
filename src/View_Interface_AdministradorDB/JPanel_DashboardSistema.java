package View_Interface_AdministradorDB;

import Resources.DashboardResumoSQL;
import Resources.DashboardUIUtils;
import Repository_SQL.AdministradorDB.DashboardAdministradorSQL;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.PainelNavegador;
import Resources.SaudacaoDashboardUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class JPanel_DashboardSistema extends JPanel
{
  private final Connection connection;
  private final PainelNavegador navegador;
  private final String funcionario;
  private final String mensagemSaudacaoHtml;
  private JPanel painelCartoes;
  private JPanel painelListas;

  public JPanel_DashboardSistema(Connection connection)
  {
    this(connection, null, SaudacaoDashboardUtils.criar(null, null), null);
  }

  public JPanel_DashboardSistema(Connection connection, String funcionario, PainelNavegador navegador)
  {
    this(connection, funcionario, SaudacaoDashboardUtils.criar(funcionario, funcionario), navegador);
  }

  public JPanel_DashboardSistema(Connection connection, String funcionario, String mensagemSaudacaoHtml,
      PainelNavegador navegador)
  {
    this.connection = connection;
    this.navegador = navegador;
    this.funcionario = DashboardResumoSQL.usuarioLigado(connection, funcionario);
    this.mensagemSaudacaoHtml = mensagemSaudacaoHtml;

    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(22, 22, 22, 22));
    add(criarConteudo(), BorderLayout.CENTER);
    carregarDashboard();
  }

  private JPanel criarConteudo()
  {
    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(28, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(26, 26, 26, 26), new Dimension(1320, 780));

    card.add(criarTopo(), BorderLayout.NORTH);
    card.add(criarCentro(), BorderLayout.CENTER);
    return card;
  }

  private JPanel criarTopo()
  {
    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(12, 0));
    topo.add(DashboardUIUtils.criarTopoComSaudacao(FontAwesomeSolid.TACHOMETER_ALT, "Dashboard Administrativo",
        mensagemSaudacaoHtml),
        BorderLayout.CENTER);
    topo.add(DashboardUIUtils.criarPainelBotaoTopo(DashboardUIUtils.criarBotao("", FontAwesomeSolid.SYNC_ALT, 44, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          carregarDashboard();
        }
      })), BorderLayout.EAST);
    return topo;
  }

  private JPanel criarCentro()
  {
    JPanel centro = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 18));
    painelCartoes = DashboardUIUtils.criarCartoes();
    painelListas = InterfaceGraficaUtils.criarPainelTransparente(new GridLayout(1, 3, 14, 0));
    centro.add(painelCartoes, BorderLayout.NORTH);
    centro.add(DashboardUIUtils.scroll(painelListas), BorderLayout.CENTER);
    return centro;
  }

  private JPanel criarRodape()
  {
    JButton registar = DashboardUIUtils.criarBotao("Registar Funcionário", FontAwesomeSolid.PLUS, 190,
        new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              if(navegador != null)
                navegador.abrirPainel(new JPanel_RegistarFuncionario(connection));
            }
          });
    JButton dashboard = DashboardUIUtils.criarBotao("Dashboard", FontAwesomeSolid.TACHOMETER_ALT, 145,
        new ActionListener()
          {
            public void actionPerformed(ActionEvent e)
            {
              carregarDashboard();
            }
          });
    return DashboardUIUtils.criarAtalhos(new JButton[]
      {
          dashboard, registar
      });
  }

  private void carregarDashboard()
  {
    try
    {
      int funcionarios = DashboardResumoSQL.contar(connection, DashboardAdministradorSQL.CONTAR_FUNCIONARIOS);
      int funcionariosActivos = DashboardResumoSQL.contar(connection,
          DashboardAdministradorSQL.CONTAR_FUNCIONARIOS_ACTIVOS);
      int sessoesAbertas = DashboardResumoSQL.contar(connection,
          DashboardAdministradorSQL.CONTAR_SESSOES_ABERTAS);
      int sessoesFechadasHoje = DashboardResumoSQL.contar(connection,
          DashboardAdministradorSQL.CONTAR_SESSOES_FECHADAS_HOJE);
      int sessoesExpiradas = DashboardResumoSQL.contar(connection,
          DashboardAdministradorSQL.CONTAR_SESSOES_EXPIRADAS);

      preencherCartoes(funcionarios, funcionariosActivos, sessoesAbertas, sessoesFechadasHoje, sessoesExpiradas);
      preencherListas();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar dashboard administrativo:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void preencherCartoes(int funcionarios, int funcionariosActivos, int sessoesAbertas, int sessoesFechadasHoje,
      int sessoesExpiradas)
  {
    painelCartoes.removeAll();
    painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.USERS, "Funcionários",
        String.valueOf(funcionarios), funcionariosActivos + " activos"));
    painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.CALENDAR_CHECK, "Sessões Abertas",
        String.valueOf(sessoesAbertas), "activas neste momento"));
    painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.CALENDAR_CHECK, "Fechadas Hoje",
        String.valueOf(sessoesFechadasHoje), "sessões encerradas"));
    painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.EXCLAMATION_TRIANGLE, "Expiradas",
        String.valueOf(sessoesExpiradas), "sessões abandonadas"));
    painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.DATABASE, "FamiliasCotasDB",
        estadoNo(DashboardAdministradorSQL.TESTAR_FAMILIAS_COTAS), "ligação"));
    painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.DATABASE, "DistribuicaoConsumoDB",
        estadoNo(DashboardAdministradorSQL.TESTAR_DISTRIBUICAO_CONSUMO), "ligação"));
    painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.DATABASE, "TransferenciasRecursosDB",
        estadoNo(DashboardAdministradorSQL.TESTAR_TRANSFERENCIAS_RECURSOS), "ligação"));
    painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.DATABASE, "EquipesGestaoDB",
        estadoNo(DashboardAdministradorSQL.TESTAR_EQUIPES_GESTAO), "ligação"));
    painelCartoes.revalidate();
    painelCartoes.repaint();
  }

  private void preencherListas() throws Exception
  {
    painelListas.removeAll();
    painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.USERS, "Funcionários por nó",
        DashboardResumoSQL.carregarLinhas(connection, DashboardAdministradorSQL.LISTAR_FUNCIONARIOS_POR_NO)));
    painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.CALENDAR_CHECK, "Sessões por estado",
        DashboardResumoSQL.carregarLinhas(connection, DashboardAdministradorSQL.LISTAR_SESSOES_POR_ESTADO)));
    painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.DATABASE, "Nós registados",
        DashboardResumoSQL.carregarLinhas(connection, DashboardAdministradorSQL.LISTAR_NOS_REGISTADOS)));
    painelListas.revalidate();
    painelListas.repaint();
  }

  private String estadoNo(String sql)
  {
    return DashboardResumoSQL.responde(connection, sql) ? "Online" : "Offline";
  }
}
