package NoAdministrador_Interface;

import Resources.InterfaceGraficaUtils;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

public class JPanel_DashboardAdministrador extends JPanel
{
  public JPanel_DashboardAdministrador()
  {
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(26, 26, 26, 26));
    add(criarConteudo(), BorderLayout.CENTER);
  }

  private JPanel criarConteudo()
  {
    InterfaceGraficaUtils.RoundedPanel island = InterfaceGraficaUtils.criarCardArredondado(30,
        new BorderLayout(0, 22), InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 34, 34, 34),
        new Dimension(1180, 720));

    island.add(criarTopo(), BorderLayout.NORTH);
    island.add(criarCentro(), BorderLayout.CENTER);
    return criarPainelCentralizado(island);
  }

  private JPanel criarPainelCentralizado(JPanel conteudo)
  {
    JPanel wrapper = new JPanel(new GridBagLayout());
    wrapper.setOpaque(false);
    wrapper.add(conteudo);
    return wrapper;
  }

  private JPanel criarTopo()
  {
    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(18, 0));

    JLabel icone = new JLabel(criarIcone(FontAwesomeSolid.USER_SHIELD, 32, InterfaceGraficaUtils.COR_AZUL));
    topo.add(icone, BorderLayout.WEST);

    JPanel textos = InterfaceGraficaUtils.criarPainelTransparente(new GridLayout(2, 1, 0, 4));
    JLabel titulo = InterfaceGraficaUtils.criarLabel("Dashboard do Administrador",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.COR_TEXTO);
    JLabel subtitulo = InterfaceGraficaUtils.criarLabel("Gestão de nos, funcionários e sessões do sistema",
        InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_SUBTEXTO);
    textos.add(titulo);
    textos.add(subtitulo);
    topo.add(textos, BorderLayout.CENTER);

    return topo;
  }

  private JPanel criarCentro()
  {
    JPanel centro = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 22));
    centro.add(criarCardsResumo(), BorderLayout.NORTH);
    centro.add(criarAreaPrincipal(), BorderLayout.CENTER);
    return centro;
  }

  private JPanel criarCardsResumo()
  {
    JPanel cards = InterfaceGraficaUtils.criarPainelTransparente(new GridLayout(1, 4, 14, 0));
    cards.add(criarCardResumo(FontAwesomeSolid.SERVER, "Nos do Sistema", "A configurar", "estado geral dos nos"));
    cards.add(criarCardResumo(FontAwesomeSolid.USERS, "Funcionários", "A configurar", "utilizadores registados"));
    cards.add(criarCardResumo(FontAwesomeSolid.SIGN_IN_ALT, "Sessões", "A configurar", "acessos abertos/fechados"));
    cards.add(criarCardResumo(FontAwesomeSolid.CLIPBOARD_LIST, "Auditoria", "A configurar", "eventos administrativos"));
    return cards;
  }

  private JPanel criarCardResumo(FontAwesomeSolid icone, String titulo, String valor, String detalhe)
  {
    JPanel card = new JPanel(new BorderLayout(0, 8));
    card.setBackground(new Color(248, 250, 252));
    card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(224, 229, 235)),
        new EmptyBorder(16, 16, 16, 16)));

    JLabel labelTitulo = InterfaceGraficaUtils.criarLabel(titulo, InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_SUBTEXTO);
    labelTitulo.setIcon(criarIcone(icone, 15, InterfaceGraficaUtils.COR_AZUL));
    labelTitulo.setIconTextGap(9);

    JLabel labelValor = InterfaceGraficaUtils.criarLabel(valor, new Font("Segoe UI", Font.BOLD, 22),
        InterfaceGraficaUtils.COR_AZUL);
    JLabel labelDetalhe = InterfaceGraficaUtils.criarLabel(detalhe, InterfaceGraficaUtils.FONT_AJUDA,
        InterfaceGraficaUtils.COR_SUBTEXTO);

    card.add(labelTitulo, BorderLayout.NORTH);
    card.add(labelValor, BorderLayout.CENTER);
    card.add(labelDetalhe, BorderLayout.SOUTH);
    return card;
  }

  private JPanel criarAreaPrincipal()
  {
    JPanel area = InterfaceGraficaUtils.criarPainelTransparente(new GridLayout(1, 2, 18, 0));
    area.add(criarPainelLista("Nos Administrados", new String[] {
        "AdministradorDB",
        "FamiliasCotasDB",
        "DistribuicaoConsumoDB",
        "TransferenciasRecursosDB",
        "EquipesGestaoDB"
    }));
    area.add(criarPainelLista("Tarefas do Administrador", new String[] {
        "Validar funcionário e no de trabalho",
        "Abrir e fechar sessões funcionais",
        "Consultar sessões abertas",
        "Controlar estado operacional dos nos",
        "Apoiar o login distribuido da aplicacao"
    }));
    return area;
  }

  private JPanel criarPainelLista(String titulo, String[] linhas)
  {
    JPanel painel = new JPanel(new BorderLayout(0, 14));
    painel.setBackground(new Color(248, 250, 252));
    painel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(224, 229, 235)),
        new EmptyBorder(20, 22, 20, 22)));

    JLabel labelTitulo = InterfaceGraficaUtils.criarLabel(titulo, InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_TEXTO);
    painel.add(labelTitulo, BorderLayout.NORTH);

    JPanel lista = InterfaceGraficaUtils.criarPainelTransparente(new GridLayout(linhas.length, 1, 0, 10));
    for(String linha : linhas)
      lista.add(criarLinhaLista(linha));

    painel.add(lista, BorderLayout.CENTER);
    return painel;
  }

  private JLabel criarLinhaLista(String texto)
  {
    JLabel label = InterfaceGraficaUtils.criarLabel(texto, InterfaceGraficaUtils.FONT_CAMPO,
        InterfaceGraficaUtils.COR_TEXTO);
    label.setIcon(criarIcone(FontAwesomeSolid.CIRCLE, 8, InterfaceGraficaUtils.COR_AZUL));
    label.setIconTextGap(10);
    return label;
  }

  private Icon criarIcone(FontAwesomeSolid icone, int tamanho, Color cor)
  {
    FontIcon fontIcon = FontIcon.of(icone, tamanho);
    fontIcon.setIconColor(cor);
    return fontIcon;
  }
}
