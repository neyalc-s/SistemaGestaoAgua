package App;

import Login.LoginSistema;
import Resources.InterfaceGraficaUtils;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Executable
{
  public static void main(String[] args)
  {
    configurarLookAndFeel();
    SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          new LoginSistema();
        }
      });
  }

  private static void configurarLookAndFeel()
  {
    try
    {
      FlatLightLaf.setup();
      UIManager.put("Component.hideMnemonics", Boolean.FALSE);
      InterfaceGraficaUtils.configurarTemaAplicacao();
    } catch(Exception ex)
    {
      try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("Component.hideMnemonics", Boolean.FALSE);
        InterfaceGraficaUtils.configurarTemaAplicacao();
      } catch(Exception e)
      {}
    }
  }
}
