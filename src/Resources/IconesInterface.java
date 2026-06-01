package Resources;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Normalizer;
import javax.swing.JMenuItem;
import javax.swing.JButton;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

public final class IconesInterface
{
  private static final String PROP_LISTENER = "efeitoHoverIconeBotaoListener";
  private static final String PROP_SOLID = "efeitoHoverIconeBotaoSolid";
  private static final String PROP_OUTLINE = "efeitoHoverIconeBotaoOutline";
  private static final String PROP_COLOR = "efeitoHoverIconeBotaoColor";
  private static final String PROP_SIZE_NORMAL = "efeitoHoverIconeBotaoSizeNormal";
  private static final String PROP_SIZE_HOVER = "efeitoHoverIconeBotaoSizeHover";

  private IconesInterface()
  {}

  public static void aplicarIconeMenu(JMenuItem item)
  {
    if(item == null)
      return;

    FontAwesomeSolid icone = escolherIcone(item.getText());
    if(icone == null)
      return;

    item.setIcon(FontIcon.of(icone, 13, InterfaceGraficaUtils.COR_AZUL));
    item.setIconTextGap(8);
  }

  public static void aplicarIconeBotao(JButton botao)
  {
    if(botao == null)
      return;

    FontAwesomeSolid icone = escolherIconeBotao(botao.getText());
    if(icone == null)
      return;

    botao.setIcon(FontIcon.of(icone, 14, InterfaceGraficaUtils.COR_BRANCO));
    botao.setIconTextGap(8);
    aplicarEfeitoHoverBotao(botao);
  }

  public static void aplicarEfeitoHoverBotaoFamilias(final JButton botao)
  {
    aplicarEfeitoHoverBotao(botao);
  }

  public static void aplicarEfeitoHoverBotao(final JButton botao)
  {
    if(botao == null)
      return;

    if(!(botao.getIcon() instanceof FontIcon))
      return;

    FontIcon iconBase = (FontIcon) botao.getIcon();
    Ikon iconeSolido = procurarIconeSolido(iconBase.getIkon());
    if(iconeSolido == null)
      return;

    Ikon iconeOutline = procurarIconeRegular(iconeSolido);
    Color cor = iconBase.getIconColor() != null ? iconBase.getIconColor() : InterfaceGraficaUtils.COR_BRANCO;
    int tamanhoNormal = Math.max(14, iconBase.getIconSize());
    int tamanhoHover = tamanhoNormal + 3;

    botao.putClientProperty(PROP_SOLID, iconeSolido);
    botao.putClientProperty(PROP_OUTLINE, iconeOutline);
    botao.putClientProperty(PROP_COLOR, cor);
    botao.putClientProperty(PROP_SIZE_NORMAL, Integer.valueOf(tamanhoNormal));
    botao.putClientProperty(PROP_SIZE_HOVER, Integer.valueOf(tamanhoHover));

    botao.setIcon(FontIcon.of(iconeOutline != null ? iconeOutline : iconeSolido, tamanhoNormal, cor));
    botao.setIconTextGap(8);

    if(Boolean.TRUE.equals(botao.getClientProperty(PROP_LISTENER)))
      return;

    botao.putClientProperty(PROP_LISTENER, Boolean.TRUE);

    botao.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseEntered(MouseEvent event)
      {
        if(!botao.isEnabled())
          return;

        Ikon iconeSolido = (Ikon) botao.getClientProperty(PROP_SOLID);
        Color cor = (Color) botao.getClientProperty(PROP_COLOR);
        Integer tamanhoHover = (Integer) botao.getClientProperty(PROP_SIZE_HOVER);

        if(iconeSolido != null && cor != null && tamanhoHover != null)
        {
          botao.setIcon(FontIcon.of(iconeSolido, tamanhoHover.intValue(), cor));
          botao.repaint();
        }
      }

      @Override
      public void mouseExited(MouseEvent event)
      {
        Ikon iconeSolido = (Ikon) botao.getClientProperty(PROP_SOLID);
        Ikon iconeOutline = (Ikon) botao.getClientProperty(PROP_OUTLINE);
        Color cor = (Color) botao.getClientProperty(PROP_COLOR);
        Integer tamanhoNormal = (Integer) botao.getClientProperty(PROP_SIZE_NORMAL);

        if(iconeSolido != null && cor != null && tamanhoNormal != null)
        {
          botao.setIcon(FontIcon.of(iconeOutline != null ? iconeOutline : iconeSolido, tamanhoNormal.intValue(), cor));
          botao.repaint();
        }
      }
    });
  }

  private static Ikon procurarIconeSolido(Ikon icone)
  {
    if(icone instanceof FontAwesomeSolid)
      return icone;

    if(!(icone instanceof Enum<?>))
      return null;

    String nome = ((Enum<?>) icone).name();

    try
    {
      return FontAwesomeSolid.valueOf(nome);
    } catch(Exception ex)
    {
      return null;
    }
  }

  private static Ikon procurarIconeRegular(Ikon icone)
  {
    if(!(icone instanceof Enum<?>))
      return null;

    String nome = ((Enum<?>) icone).name();

    try
    {
      return FontAwesomeRegular.valueOf(nome);
    } catch(Exception ex)
    {
      return null;
    }
  }

  private static FontAwesomeSolid escolherIcone(String texto)
  {
    if(texto == null)
      return null;

    String t = normalizar(texto);
    if(t.contains("dashboard"))
      return FontAwesomeSolid.TACHOMETER_ALT;
    if(t.contains("logout") || t.contains("sair") || t.contains("terminar sessão"))
      return FontAwesomeSolid.SIGN_OUT_ALT;
    if(t.contains("auditoria") || t.contains("sessão"))
      return FontAwesomeSolid.TABLE;
    if(t.contains("registar"))
      return FontAwesomeSolid.PLUS;
    if(t.contains("pesquis") || t.contains("consult") || t.contains("hist"))
      return FontAwesomeSolid.SEARCH;
    if(t.contains("actualizar") || t.contains("alterar"))
      return FontAwesomeSolid.SYNC_ALT;
    if(t.contains("apagar") || t.contains("desactivar") || t.contains("cancelar"))
      return FontAwesomeSolid.EXCLAMATION_TRIANGLE;
    if(t.contains("família") || t.contains("funcionário") || t.contains("equipe") || t.contains("comit"))
      return FontAwesomeSolid.USERS;
    if(t.contains("água") || t.contains("consumo") || t.contains("recurso") || t.contains("ponto"))
      return FontAwesomeSolid.TINT;
    if(t.contains("qualidade") || t.contains("medição"))
      return FontAwesomeSolid.FLASK;
    return FontAwesomeSolid.TABLE;
  }

  private static FontAwesomeSolid escolherIconeBotao(String texto)
  {
    if(texto == null)
      return null;

    String t = normalizar(texto);
    if(t.contains("dashboard"))
      return FontAwesomeSolid.TACHOMETER_ALT;
    if(t.equals("?") || t.contains("ajuda") || t.contains("help"))
      return FontAwesomeSolid.QUESTION_CIRCLE;
    if(t.contains("voltar"))
      return FontAwesomeSolid.ARROW_LEFT;
    if(t.contains("proceder") || t.contains("avancar") || t.contains("continuar"))
      return FontAwesomeSolid.ARROW_RIGHT;
    if(t.contains("pesquis") || t.contains("consult") || t.contains("ver detalhe"))
      return FontAwesomeSolid.SEARCH;
    if(t.contains("mostrar todos") || t.contains("lista") || t.contains("tabela"))
      return FontAwesomeSolid.TABLE;
    if(t.contains("guardar") || t.contains("registar") || t.contains("confirmar"))
      return FontAwesomeSolid.SAVE;
    if(t.contains("actualizar") || t.contains("alterar") || t.contains("recarregar"))
      return FontAwesomeSolid.SYNC_ALT;
    if(t.contains("limpar"))
      return FontAwesomeSolid.ERASER;
    if(t.contains("logout") || t.contains("sair") || t.contains("terminar sessão"))
      return FontAwesomeSolid.SIGN_OUT_ALT;
    if(t.contains("cancelar") || t.contains("fechar"))
      return FontAwesomeSolid.EXCLAMATION_TRIANGLE;
    if(t.contains("eliminar") || t.contains("apagar") || t.contains("desactivar") || t.contains("remover"))
      return FontAwesomeSolid.EXCLAMATION_TRIANGLE;
    if(t.contains("adicionar") || t.contains("novo") || t.contains("nova") || t.contains("gerar"))
      return FontAwesomeSolid.PLUS;
    return null;
  }

  private static String normalizar(String texto)
  {
    return Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toLowerCase();
  }
}
