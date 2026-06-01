package Resources;

public final class SaudacaoDashboardUtils
{
  private static final String[] MENSAGENS =
    {
        "Que tenha um dia de trabalho tranquilo e produtivo.",
        "Bom trabalho e uma excelente continuidade nas suas actividades.",
        "Desejamos-lhe um dia produtivo e organizado.",
        "Bom trabalho e sucesso nas suas actividades de hoje."
    };

  private SaudacaoDashboardUtils()
  {}

  public static String criar(String nomeFuncionario, String fallback)
  {
    String nome = normalizar(nomeFuncionario, fallback);
    int indice = (int)(Math.random() * MENSAGENS.length);
    return "Saudações, <b>" + escaparHtml(nome) + "</b>! " + MENSAGENS[indice];
  }

  private static String normalizar(String valor, String fallback)
  {
    if(valor != null && valor.trim().length() > 0)
      return valor.trim();

    if(fallback != null && fallback.trim().length() > 0)
      return fallback.trim();

    return "Funcionário";
  }

  private static String escaparHtml(String texto)
  {
    if(texto == null)
      return "";
    return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }
}
