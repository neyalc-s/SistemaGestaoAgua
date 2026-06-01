package Resources;

import java.awt.Component;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public final class MensagensInterface
{
  private MensagensInterface()
  {}

  public static String formatarMensagem(String mensagem)
  {
    if(mensagem == null)
      return "Operação não concluída. Tente novamente.";

    String texto = mensagem.trim();
    if(texto.length() == 0)
      return "Operação não concluída. Tente novamente.";

    texto = texto.replace("ERRO:", "Operação não concluída.");
    texto = texto.replace("SUCESSO:", "Operação concluída.");

    texto = texto.replaceAll("(?is)ORA-06512:.*", "");
    texto = texto.replaceAll("(?is)PL/SQL:\\s*Statement ignored", "");
    texto = texto.replaceAll("(?is)java\\.sql\\.SQLException:\\s*", "");
    texto = texto.replaceAll("(?is)Exception:\\s*", "");

    if(texto.contains("ORA-00001"))
      return "Operação não concluída: já existe um registo com estes dados.";

    if(texto.contains("ORA-02291"))
      return "Operação não concluída: existe uma referência obrigatória que não foi encontrada.";

    if(texto.contains("ORA-02292"))
      return "Operação não concluída: este registo ainda está associado a outros dados e não pode ser removido.";

    if(texto.contains("ORA-01017"))
      return "Não foi possível autenticar. Verifique o utilizador e a palavra-passe.";

    if(texto.contains("ORA-01013"))
      return "A operação foi cancelada antes de terminar.";

    if(TratadorConexaoFechada.ehTextoConexaoFechada(texto))
      return TratadorConexaoFechada.mensagemConexaoFechada();

    if(texto.contains("ORA-02064"))
      return "Operação não concluída: a base de dados remota não permitiu esta operação distribuída.";

    if(texto.contains("ORA-02019") || texto.contains("ORA-12154") || texto.contains("ORA-12514")
        || texto.contains("ORA-12541") || texto.contains("ORA-03113") || texto.contains("ORA-03114"))
      return "Não foi possível comunicar com a base de dados remota. Verifique se o nó está online e tente novamente.";

    if(texto.contains("ORA-04062"))
      return "Operação não concluída: uma procedure remota foi recompilada. Actualize os scripts desse nó e tente novamente.";

    if(texto.contains("ORA-04063"))
      return "Não foi possível carregar estes dados porque uma view ou procedure está inválida. Recompile os scripts do nó correspondente.";

    if(texto.contains("ORA-04042"))
      return "Operação não concluída: a procedure necessária não foi encontrada no nó esperado.";

    texto = texto.replaceAll("ORA-\\d{5}:\\s*", "");
    texto = texto.replaceAll("\\s+", " ").trim();

    if(texto.length() == 0)
      return "Operação não concluída. Tente novamente.";

    return texto;
  }

  public static String formatarErro(Throwable erro)
  {
    if(erro == null)
      return "Operação não concluída. Tente novamente.";

    Throwable causa = erro;
    while(causa.getCause() != null)
      causa = causa.getCause();

    if(TratadorConexaoFechada.ehErroConexaoFechada(causa))
      return TratadorConexaoFechada.mensagemConexaoFechada();

    if(causa instanceof SQLException)
      return formatarMensagem(causa.getMessage());

    String mensagem = causa.getMessage();
    if(mensagem == null || mensagem.trim().length() == 0)
      mensagem = erro.toString();

    return formatarMensagem(mensagem);
  }

  public static void mostrarErro(Component parent, String titulo, String mensagem)
  {
    JOptionPane.showMessageDialog(parent, formatarMensagem(mensagem), titulo, JOptionPane.ERROR_MESSAGE);
  }

  public static void mostrarErro(Component parent, String titulo, Throwable erro)
  {
    JOptionPane.showMessageDialog(parent, formatarErro(erro), titulo, JOptionPane.ERROR_MESSAGE);
  }

  public static void mostrarInformacao(Component parent, String titulo, String mensagem)
  {
    JOptionPane.showMessageDialog(parent, formatarMensagem(mensagem), titulo, JOptionPane.INFORMATION_MESSAGE);
  }

  public static void mostrarAviso(Component parent, String titulo, String mensagem)
  {
    JOptionPane.showMessageDialog(parent, formatarMensagem(mensagem), titulo, JOptionPane.WARNING_MESSAGE);
  }
}
