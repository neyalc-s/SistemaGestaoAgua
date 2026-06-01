package View_Interface_EquipesGestaoDB;

import Resources.MensagensInterface;
import Repository_SQL.EquipesGestaoDB.DashboardEquipesGestaoSQL;

import Resources.*;
import Resources.SaudacaoDashboardUtils;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class JPanel_DashboardEquipesGestao extends JPanel
{
  private final Connection connection;
  private final String funcionario;
  private final String mensagemSaudacaoHtml;
  private final PainelNavegador navegador;
  private JPanel painelCartoes;
  private JPanel painelListas;

  public JPanel_DashboardEquipesGestao(Connection connection, String funcionario, PainelNavegador navegador)
  {
    this(connection, funcionario, SaudacaoDashboardUtils.criar(funcionario, funcionario), navegador);
  }

  public JPanel_DashboardEquipesGestao(Connection connection, String funcionario, String mensagemSaudacaoHtml,
      PainelNavegador navegador)
  {
    this.connection = connection;
    this.funcionario = DashboardResumoSQL.usuarioLigado(connection, funcionario);
    this.mensagemSaudacaoHtml = mensagemSaudacaoHtml;
    this.navegador = navegador;
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
    topo.add(DashboardUIUtils.criarTopoComSaudacao(FontAwesomeSolid.USERS, "Dashboard de Equipes e Gestão", mensagemSaudacaoHtml), BorderLayout.CENTER);
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
    painelListas = InterfaceGraficaUtils.criarPainelTransparente(new GridLayout(1, 2, 14, 0));
    centro.add(painelCartoes, BorderLayout.NORTH);
    centro.add(DashboardUIUtils.scroll(painelListas), BorderLayout.CENTER);
    return centro;
  }

  private JPanel criarRodape()
  {
    return DashboardUIUtils.criarAtalhos(new JButton[]
      {
          atalho("Registar Equipe", FontAwesomeSolid.PLUS, 170, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_RegistarEquipeTecnica(connection);
              }
            }),
          atalho("Consultar Equipes", FontAwesomeSolid.SEARCH, 180, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_Equipe_Tecnica_Consultas(connection);
              }
            }),
          atalho("Registar Comité", FontAwesomeSolid.PLUS, 165, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_RegistarComite(connection);
              }
            }),
          atalho("Consultar Comités", FontAwesomeSolid.TABLE, 180, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_Comite_Consultas(connection);
              }
            })
      });
  }

  private JButton atalho(String texto, FontAwesomeSolid icone, int largura, final PainelFactory factory)
  {
    return DashboardUIUtils.criarBotao(texto, icone, largura, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          if(navegador != null)
            navegador.abrirPainel(factory.criar());
        }
      });
  }

  private void carregarDashboard()
  {
    try
    {
      painelCartoes.removeAll();
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.USERS, "Equipes",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardEquipesGestaoSQL.CONTAR_EQUIPES)),
          "técnicas registadas"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.USERS, "Manutenção",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardEquipesGestaoSQL.CONTAR_TECNICOS_MANUTENCAO)),
          "técnicos"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.FLASK, "Analistas",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardEquipesGestaoSQL.CONTAR_ANALISTAS)),
          "qualidade"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.USERS, "Educadores",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardEquipesGestaoSQL.CONTAR_EDUCADORES)),
          "comunitários"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TABLE, "Comités",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardEquipesGestaoSQL.CONTAR_COMITES)),
          "locais"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TABLE, "Ferramentas",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardEquipesGestaoSQL.CONTAR_FERRAMENTAS)),
          "manutenção"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TABLE, "Equipamentos",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardEquipesGestaoSQL.CONTAR_EQUIPAMENTOS)),
          "analistas"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TABLE, "Materiais",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardEquipesGestaoSQL.CONTAR_MATERIAIS)),
          "educadores"));

      painelListas.removeAll();
      painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.USERS, "Equipes recentes",
          DashboardResumoSQL.carregarLinhas(connection, DashboardEquipesGestaoSQL.LISTAR_EQUIPES_RECENTES)));
      painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.TABLE, "Comités recentes",
          DashboardResumoSQL.carregarLinhas(connection, DashboardEquipesGestaoSQL.LISTAR_COMITES_RECENTES)));
      revalidar();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar dashboard de equipes:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void revalidar()
  {
    painelCartoes.revalidate();
    painelCartoes.repaint();
    painelListas.revalidate();
    painelListas.repaint();
  }

  private interface PainelFactory
  {
    JPanel criar();
  }
}
