package View_Interface_FamiliasCotasDB;

import Resources.DashboardResumoSQL;
import Repository_SQL.FamiliasCotasDB.DashboardFamiliasCotasSQL;
import Resources.DashboardUIUtils;
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

public class JPanel_DashboardFamiliasCotas extends JPanel
{
  private final Connection connection;
  private final String funcionario;
  private final String mensagemSaudacaoHtml;
  private final PainelNavegador navegador;
  private JPanel painelCartoes;
  private JPanel painelListas;

  public JPanel_DashboardFamiliasCotas(Connection connection, String funcionario, PainelNavegador navegador)
  {
    this(connection, funcionario, SaudacaoDashboardUtils.criar(funcionario, funcionario), navegador);
  }

  public JPanel_DashboardFamiliasCotas(Connection connection, String funcionario, String mensagemSaudacaoHtml,
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
    topo.add(DashboardUIUtils.criarTopoComSaudacao(FontAwesomeSolid.USERS, "Dashboard de Famílias e Cotas", mensagemSaudacaoHtml), BorderLayout.CENTER);
    JButton buttonActualizar = DashboardUIUtils.criarBotao("", FontAwesomeSolid.SYNC_ALT, 44, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          carregarDashboard();
        }
      });
    Resources.IconesInterface.aplicarEfeitoHoverBotaoFamilias(buttonActualizar);
    topo.add(DashboardUIUtils.criarPainelBotaoTopo(buttonActualizar), BorderLayout.EAST);
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
          atalho("Registar Família", FontAwesomeSolid.PLUS, 170, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_RegistarFamilia(connection);
              }
            }),
          atalho("Consultar Famílias", FontAwesomeSolid.SEARCH, 180, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_Familia_Beneficiaria_Consultas(connection);
              }
            }),
          atalho("Consultar Cotas", FontAwesomeSolid.TABLE, 165, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_Cota_Agua_Consultas(connection);
              }
            }),
          atalho("Actualizar Necessidades", FontAwesomeSolid.SYNC_ALT, 220, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_ActualizarNecessidadeFamilias(connection);
              }
            }),
          atalho("Associar a Ponto", FontAwesomeSolid.MAP_MARKED_ALT, 175, new PainelFactory()
            {
              public JPanel criar()
              {
                return new JPanel_AssociarFamiliasPontoDistribuicao(connection);
              }
            })
      });
  }

  private JButton atalho(String texto, FontAwesomeSolid icone, int largura, final PainelFactory factory)
  {
    JButton botao = DashboardUIUtils.criarBotao(texto, icone, largura, new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          if(navegador != null)
            navegador.abrirPainel(factory.criar());
        }
      });
    Resources.IconesInterface.aplicarEfeitoHoverBotaoFamilias(botao);
    return botao;
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
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.USERS, "Famílias",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardFamiliasCotasSQL.CONTAR_FAMILIAS)),
          "registadas no nó"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.MAP_MARKED_ALT, "Com ponto",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardFamiliasCotasSQL.CONTAR_FAMILIAS_COM_PONTO)),
          "associadas a pontos"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.CALENDAR_CHECK, "Cotas válidas",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardFamiliasCotasSQL.CONTAR_COTAS_VALIDAS)),
          "dentro da validade"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.EXCLAMATION_TRIANGLE, "Cotas expiradas",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardFamiliasCotasSQL.CONTAR_COTAS_EXPIRADAS)),
          "fora da validade"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TINT, "Saldo baixo",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardFamiliasCotasSQL.CONTAR_COTAS_SALDO_BAIXO)),
          "até 20% da cota"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TABLE, "Necessidades",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardFamiliasCotasSQL.CONTAR_NECESSIDADES)),
          "tipos registados"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.TABLE, "Associações",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardFamiliasCotasSQL.CONTAR_ASSOCIACOES_NECESSIDADE)),
          "família/necessidade"));
      painelCartoes.add(DashboardUIUtils.criarCardResumo(FontAwesomeSolid.DATABASE, "Localizações",
          String.valueOf(DashboardResumoSQL.contar(connection, DashboardFamiliasCotasSQL.CONTAR_LOCALIZACOES)),
          "aldeias/localizações"));

      painelListas.removeAll();
      painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.USERS, "Últimas famílias",
          DashboardResumoSQL.carregarLinhas(connection, DashboardFamiliasCotasSQL.LISTAR_ULTIMAS_FAMILIAS)));
      painelListas.add(DashboardUIUtils.criarLista(FontAwesomeSolid.TINT, "Cotas com menor saldo",
          DashboardResumoSQL.carregarLinhas(connection, DashboardFamiliasCotasSQL.LISTAR_COTAS_MENOR_SALDO)));
      revalidar();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar dashboard de famílias e cotas:\n" + MensagensInterface.formatarErro(ex));
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
