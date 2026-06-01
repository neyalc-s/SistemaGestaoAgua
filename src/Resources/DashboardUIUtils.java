package Resources;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

public final class DashboardUIUtils
{
  private DashboardUIUtils()
  {}

  public static JPanel criarTopo(FontAwesomeSolid icone, String titulo, String subtituloHtml, String funcionario,
      String noActual)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(14, 0));
    JLabel labelIcone = new JLabel(icone(icone, 32, InterfaceGraficaUtils.COR_AZUL));
    labelIcone.setVerticalAlignment(SwingConstants.TOP);
    labelIcone.setBorder(new EmptyBorder(3, 0, 0, 0));

    String textoSessao = subtituloHtml + "<br><b>Funcionário:</b> " + escapar(funcionario) + " &nbsp; <b>Nó actual:</b> "
        + escapar(noActual);
    painel.add(labelIcone, BorderLayout.WEST);
    painel.add(InterfaceGraficaUtils.criarTopo(titulo, "<html>" + textoSessao + "</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.CENTER);
    return painel;
  }

  public static JPanel criarTopoComSaudacao(FontAwesomeSolid icone, String titulo, String saudacaoHtml)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(14, 0));
    JLabel labelIcone = new JLabel(icone(icone, 32, InterfaceGraficaUtils.COR_AZUL));
    labelIcone.setVerticalAlignment(SwingConstants.TOP);
    labelIcone.setBorder(new EmptyBorder(3, 0, 0, 0));

    painel.add(labelIcone, BorderLayout.WEST);
    painel.add(InterfaceGraficaUtils.criarTopo(titulo, "<html>" + saudacaoHtml + "</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO.deriveFont(18f),
        InterfaceGraficaUtils.COR_TEXTO, InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.CENTER);
    return painel;
  }

  public static JPanel criarCartoes()
  {
    return InterfaceGraficaUtils.criarPainelTransparente(new GridLayout(2, 4, 12, 12));
  }

  public static JPanel criarCardResumo(FontAwesomeSolid icone, String titulo, String valor, String detalhe)
  {
    JPanel card = new JPanel(new BorderLayout(0, 6));
    card.setBackground(new Color(248, 250, 252));
    card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(224, 229, 235)),
        new EmptyBorder(12, 14, 12, 14)));

    JLabel labelTitulo = InterfaceGraficaUtils.criarLabel(titulo, InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_SUBTEXTO);
    labelTitulo.setIcon(icone(icone, 14, InterfaceGraficaUtils.COR_AZUL));
    labelTitulo.setIconTextGap(8);

    JLabel labelValor = InterfaceGraficaUtils.criarLabel(valor, InterfaceGraficaUtils.FONT_TITULO,
        InterfaceGraficaUtils.COR_AZUL);
    JLabel labelDetalhe = InterfaceGraficaUtils.criarLabel(detalhe, InterfaceGraficaUtils.FONT_AJUDA,
        InterfaceGraficaUtils.COR_SUBTEXTO);

    card.add(labelTitulo, BorderLayout.NORTH);
    card.add(labelValor, BorderLayout.CENTER);
    card.add(labelDetalhe, BorderLayout.SOUTH);
    return card;
  }

  public static JPanel criarLista(FontAwesomeSolid icone, String titulo, List<DashboardResumoSQL.LinhaResumo> linhas)
  {
    JPanel painel = new JPanel();
    painel.setLayout(new javax.swing.BoxLayout(painel, javax.swing.BoxLayout.Y_AXIS));
    painel.setBackground(new Color(248, 250, 252));
    painel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(224, 229, 235)),
        new EmptyBorder(12, 14, 12, 14)));

    JLabel labelTitulo = InterfaceGraficaUtils.criarLabel(titulo, InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_TEXTO);
    labelTitulo.setIcon(icone(icone, 15, InterfaceGraficaUtils.COR_AZUL));
    labelTitulo.setIconTextGap(8);
    labelTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
    painel.add(labelTitulo);
    painel.add(javax.swing.Box.createVerticalStrut(8));

    if(linhas == null || linhas.isEmpty())
    {
      JLabel vazio = InterfaceGraficaUtils.criarLabel("Sem dados para apresentar.", InterfaceGraficaUtils.FONT_AJUDA,
          InterfaceGraficaUtils.COR_SUBTEXTO);
      vazio.setAlignmentX(Component.LEFT_ALIGNMENT);
      painel.add(vazio);
      return painel;
    }

    for(DashboardResumoSQL.LinhaResumo linha : linhas)
    {
      JLabel item = InterfaceGraficaUtils.criarLabel(
          "<html><b>" + escapar(linha.principal) + "</b><br>" + escapar(linha.detalhe) + " - "
              + escapar(linha.valor) + "</html>",
          InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_TEXTO);
      item.setAlignmentX(Component.LEFT_ALIGNMENT);
      painel.add(item);
      painel.add(javax.swing.Box.createVerticalStrut(6));
    }
    return painel;
  }

  public static JButton criarBotao(String texto, FontAwesomeSolid icone, int largura, ActionListener listener)
  {
    boolean somenteIcone = texto == null || texto.trim().length() == 0;
    int altura = somenteIcone ? 36 : 38;
    int larguraFinal = somenteIcone ? 36 : largura;

    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(larguraFinal, altura));
    botao.setPreferredSize(new Dimension(larguraFinal, altura));
    botao.setMinimumSize(new Dimension(larguraFinal, altura));
    botao.setMaximumSize(new Dimension(larguraFinal, altura));
    botao.setIcon(icone(icone, 15, InterfaceGraficaUtils.COR_BRANCO));
    botao.setIconTextGap(somenteIcone ? 0 : 8);
    IconesInterface.aplicarEfeitoHoverBotao(botao);
    if(listener != null)
      botao.addActionListener(listener);
    return botao;
  }

  public static JPanel criarPainelBotaoTopo(JButton botao)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new java.awt.GridBagLayout());
    painel.setPreferredSize(new Dimension(46, 46));
    painel.setMinimumSize(new Dimension(46, 46));
    painel.setMaximumSize(new Dimension(46, 46));
    painel.add(botao);
    return painel;
  }

  public static JPanel criarAtalhos(JButton[] botoes)
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    for(int i = 0; i < botoes.length; i++)
      painel.add(botoes[i]);
    return painel;
  }

  public static JScrollPane scroll(JComponent componente)
  {
    JScrollPane scroll = new JScrollPane(componente);
    scroll.setBorder(null);
    scroll.setOpaque(false);
    scroll.getViewport().setOpaque(false);
    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    scroll.getVerticalScrollBar().setUnitIncrement(14);
    return scroll;
  }

  public static FontIcon icone(FontAwesomeSolid icone, int tamanho, Color cor)
  {
    return FontIcon.of(icone, tamanho, cor);
  }

  private static String escapar(String texto)
  {
    if(texto == null)
      return "";
    return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }
}
