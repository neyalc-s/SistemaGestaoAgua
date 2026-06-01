package View_Interface_DistribuicaoConsumoDB;

import Resources.MensagensInterface;
import Repository_SQL.DistribuicaoConsumoDB.DashboardDistribuicaoConsumoSQL;

import Resources.*;
import Resources.SaudacaoDashboardUtils;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

public class JPanel_DashboardDistribuicaoConsumo extends JPanel
{
  private final Connection connection;
  private final String funcionario;
  private final String mensagemSaudacaoHtml;
  private final PainelNavegador navegador;
  private JPanel painelCartoes;
  private JPanel painelListas;

  public JPanel_DashboardDistribuicaoConsumo(Connection connection, String funcionario, PainelNavegador navegador)
  {
    this(connection, funcionario, SaudacaoDashboardUtils.criar(funcionario, funcionario), navegador);
  }

  public JPanel_DashboardDistribuicaoConsumo(Connection connection, String funcionario, String mensagemSaudacaoHtml,
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
    topo.add(DashboardUIUtils.criarTopoComSaudacao(FontAwesomeSolid.TINT, "Dashboard de Distribuição e Consumo", mensagemSaudacaoHtml), BorderLayout.CENTER);
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
          atalho("Retirar Água", FontAwesomeSolid.TINT, 155, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_RetirarAgua(connection);
              }
            }),
          atalho("Consumos", FontAwesomeSolid.SEARCH, 135, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_Registro_Consumo_Consultas(connection);
              }
            }),
          atalho("Registar Abastecimento", FontAwesomeSolid.PLUS, 220, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_RegistarAbastecimento(connection);
              }
            }),
          atalho("Registar Manutenção", FontAwesomeSolid.PLUS, 210, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_RegistarManutencaoPonto(connection);
              }
            }),
          atalho("Pontos", FontAwesomeSolid.MAP_MARKED_ALT, 120, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_Ponto_Distribuicao_Consultas(connection);
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

  private interface PainelFactory
  {
    JPanel criar();
  }

  private void carregarDashboard()
  {
    try
    {
      painelCartoes.removeAll();
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.MAP_MARKED_ALT, "Pontos",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardDistribuicaoConsumoSQL.CONTAR_PONTOS)),
          "registados"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.CALENDAR_CHECK, "Operacionais",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardDistribuicaoConsumoSQL.CONTAR_PONTOS_OPERACIONAIS)),
          "activos"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.EXCLAMATION_TRIANGLE, "Em manutenção",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardDistribuicaoConsumoSQL.CONTAR_PONTOS_MANUTENCAO)),
          "pontos indisponíveis"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TINT, "Retiradas hoje",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardDistribuicaoConsumoSQL.CONTAR_RETIRADAS_HOJE)),
          "registros de consumo"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.SYNC_ALT, "Abastecimentos",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardDistribuicaoConsumoSQL.CONTAR_ABASTECIMENTOS_EM_CURSO)),
          "em curso"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TABLE, "Hist. Abastecimento",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardDistribuicaoConsumoSQL.CONTAR_HISTORICO_ABASTECIMENTO)),
          "registos"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TABLE, "Manutenções",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardDistribuicaoConsumoSQL.CONTAR_MANUTENCOES)),
          "registos"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.DATABASE, "Consumos",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardDistribuicaoConsumoSQL.CONTAR_CONSUMOS)),
          "histórico total"));

      painelListas.removeAll();
      painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.TINT, "Últimos consumos",
          DashboardResumoSQL.carregarLinhas(connection, DashboardDistribuicaoConsumoSQL.LISTAR_ULTIMOS_CONSUMOS)));
      painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.SYNC_ALT, "Manutenções recentes",
          DashboardResumoSQL.carregarLinhas(connection, DashboardDistribuicaoConsumoSQL.LISTAR_MANUTENCOES_RECENTES)));
      revalidar();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar dashboard de distribuição:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void revalidar()
  {
    painelCartoes.revalidate();
    painelCartoes.repaint();
    painelListas.revalidate();
    painelListas.repaint();
  }
}
