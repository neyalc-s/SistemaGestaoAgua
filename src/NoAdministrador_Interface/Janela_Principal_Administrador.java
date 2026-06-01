package NoAdministrador_Interface;

import Connection.OracleConnection;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import javax.swing.*;

public class Janela_Principal_Administrador extends JFrame
{
  private final Connection connection;

  public Janela_Principal_Administrador(Connection connection)
  {
    super("No Administrador");
    this.connection = connection;

    setContentPane(new JPanel_DashboardAdministrador());
    setSize(1320, 820);
    setMinimumSize(new Dimension(980, 640));
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    addWindowListener(new WindowAdapter()
      {
        public void windowClosed(WindowEvent event)
        {
          OracleConnection.closeConnection(connection);
        }
      });
    setVisible(true);
  }

  public static void main(String[] args)
  {
    configurarLookAndFeel();
    SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          new Janela_Principal_Administrador(null);
        }
      });
  }

  private static void configurarLookAndFeel()
  {
    try
    {
      FlatLightLaf.setup();
      UIManager.put("Component.hideMnemonics", Boolean.FALSE);
    } catch(Exception ex)
    {
      try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("Component.hideMnemonics", Boolean.FALSE);
      } catch(Exception ignored)
      {}
    }
  }
}
