package Resources;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.lang.reflect.Method;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class TratadorConexaoFechada
{
  private static boolean emTratamento = false;

  private TratadorConexaoFechada()
  {}

  public static boolean tratar(Component parent, Throwable erro)
  {
    if(!ehErroConexaoFechada(erro))
      return false;

    if(emTratamento)
      return true;

    emTratamento = true;

    Window janela = localizarJanela(parent);

    if(janela != null)
    {
      try
      {
        Method metodo = janela.getClass().getMethod("forcarVoltarLoginPorConexaoFechada");
        metodo.invoke(janela);
        return true;
      } catch(Exception ignored)
      {}
    }

    JOptionPane.showMessageDialog(parent, mensagemConexaoFechada(), "Erro", JOptionPane.ERROR_MESSAGE);
    return true;
  }

  public static boolean ehErroConexaoFechada(Throwable erro)
  {
    if(erro == null)
      return false;

    Throwable actual = erro;
    while(actual != null)
    {
      if(ehTextoConexaoFechada(actual.getMessage()))
        return true;

      if(actual instanceof SQLException && ehTextoConexaoFechada(actual.toString()))
        return true;

      actual = actual.getCause();
    }

    return false;
  }

  public static boolean ehTextoConexaoFechada(String texto)
  {
    if(texto == null)
      return false;

    String t = texto.toLowerCase();

    return t.contains("closed connection") || t.contains("connection closed") || t.contains("connection is closed")
        || t.contains("conexão fechada") || t.contains("ligacao fechada");
  }

  public static String mensagemConexaoFechada()
  {
    return "A ligacao com a base de dados foi perdida. O sistema vai voltar ao login para restabelecer a sessão.";
  }

  private static Window localizarJanela(Component parent)
  {
    if(parent != null)
    {
      Window janela = SwingUtilities.getWindowAncestor(parent);
      if(janela != null)
        return janela;
    }

    Window activa = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
    if(activa != null)
      return activa;

    Window[] janelas = Window.getWindows();
    for(int i = 0; i < janelas.length; i++)
    {
      if(janelas[i] != null && janelas[i].isVisible())
        return janelas[i];
    }

    return null;
  }
}
