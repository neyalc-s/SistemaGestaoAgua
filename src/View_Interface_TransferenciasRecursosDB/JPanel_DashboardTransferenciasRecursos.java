package View_Interface_TransferenciasRecursosDB;

import Resources.MensagensInterface;
import Repository_SQL.TransferenciasRecursosDB.DashboardTransferenciasRecursosSQL;

import Resources.*;
import Resources.SaudacaoDashboardUtils;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class JPanel_DashboardTransferenciasRecursos extends JPanel
{
  private final Connection connection;
  private final String funcionario;
  private final String mensagemSaudacaoHtml;
  private final PainelNavegador navegador;
  private JPanel painelCartoes;
  private JPanel painelListas;

  public JPanel_DashboardTransferenciasRecursos(Connection connection, String funcionario, PainelNavegador navegador)
  {
    this(connection, funcionario, SaudacaoDashboardUtils.criar(funcionario, funcionario), navegador);
  }

  public JPanel_DashboardTransferenciasRecursos(Connection connection, String funcionario, String mensagemSaudacaoHtml,
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
    topo.add(DashboardUIUtils.criarTopoComSaudacao(FontAwesomeSolid.WATER, "Dashboard de Transferências e Recursos", mensagemSaudacaoHtml), BorderLayout.CENTER);
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
          atalho("Registar Recurso", FontAwesomeSolid.PLUS, 180, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_RegistarRecursoHidrico(connection);
              }
            }),
          atalho("Registar Medição", FontAwesomeSolid.FLASK, 180, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_RegistarMedicaoQualidadeAgua(connection);
              }
            }),
          atalho("Doar Cota", FontAwesomeSolid.TINT, 135, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_Doar_Cota(connection);
              }
            }),
          atalho("Transferências", FontAwesomeSolid.SEARCH, 160, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_Transferencia_Cota_Consultas(connection);
              }
            }),
          atalho("Recursos", FontAwesomeSolid.WATER, 130, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_Recurso_Hidrico_Consultas(connection);
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
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.WATER, "Recursos",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardTransferenciasRecursosSQL.CONTAR_RECURSOS)),
          "fontes hídricas"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.FLASK, "Medições",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardTransferenciasRecursosSQL.CONTAR_MEDICOES)),
          "qualidade da água"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.EXCLAMATION_TRIANGLE, "Alertas",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardTransferenciasRecursosSQL.CONTAR_ALERTAS)),
          "qualidade"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TINT, "Transferências",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardTransferenciasRecursosSQL.CONTAR_TRANSFERENCIAS)),
          "cotas transferidas"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TABLE, "Medidas",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardTransferenciasRecursosSQL.CONTAR_MEDIDAS)),
          "protecção"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.CALENDAR_CHECK, "Recursos protegidos",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardTransferenciasRecursosSQL.CONTAR_RECURSOS_PROTEGIDOS)),
          "com medidas"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.USERS, "Responsáveis",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardTransferenciasRecursosSQL.CONTAR_RESPONSAVEIS)),
          "medidas de protecção"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.DATABASE, "Parâmetros",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardTransferenciasRecursosSQL.CONTAR_PARAMETROS)),
          "qualidade"));

      painelListas.removeAll();
      painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.FLASK, "Últimas medições",
          DashboardResumoSQL.carregarLinhas(connection, DashboardTransferenciasRecursosSQL.LISTAR_ULTIMAS_MEDICOES)));
      painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.TINT, "Transferências recentes",
          DashboardResumoSQL.carregarLinhas(connection, DashboardTransferenciasRecursosSQL.LISTAR_TRANSFERENCIAS_RECENTES)));
      revalidar();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar dashboard de transferências e recursos:\n" + MensagensInterface.formatarErro(ex));
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
