package View_Interface_AdministradorDB;

import Login.LoginSistema;
import Connection.OracleConnection;
import Resources.DialogoLogout;
import Resources.PainelNavegador;
import Resources.SessaoFuncional;
import Resources.IconesInterface;
import Resources.TratadorConexaoFechada;
import Resources.SaudacaoDashboardUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Janela_Principal extends JFrame
{
  private String name_items[] =
    {
        "Dashboard", "Registar Funcionário", "Auditoria de Sessões"
    };
  private JMenuItem items[];
  private JMenu menuOutros, menuSessao;
  private JMenuBar menubar;
  private JPopupMenu popupMenu;
  private JCheckBoxMenuItem itemCarregarTabelasAutomaticamente;
  private JMenuItem itemLogout;
  private boolean carregarTabelasAutomaticamente = false;
  private int indicePainelActual = 0;
  private boolean painelRetiradasPendentesActual = false;
  private boolean recursosEncerrados = false;
  private TratarButtons tb = new TratarButtons();
  private final Connection connection;
  private final int codigoSessao;
  private final String mensagemDashboard;

  public Janela_Principal(final Connection connection)
  {
    this(connection, 0);
  }

  public Janela_Principal(final Connection connection, int codigoSessao)
  {
    this(connection, codigoSessao, null);
  }

  public Janela_Principal(final Connection connection, int codigoSessao, String nomeFuncionario)
  {
    super("AdministradorDB");
    this.connection = connection;
    this.codigoSessao = codigoSessao;
    this.mensagemDashboard = SaudacaoDashboardUtils.criar(nomeFuncionario, "Funcionário");

    items = new JMenuItem[name_items.length];
    menuOutros = new JMenu("Administração");

    for(int i = 0; i < items.length; i++)
    {
      items[i] = new JMenuItem(name_items[i]);
      IconesInterface.aplicarIconeMenu(items[i]);
      items[i].addActionListener(tb);
    }

    menuOutros.add(items[0]);
    menuOutros.add(items[1]);
    menuOutros.add(items[2]);

    menuSessao = new JMenu("Sessão");
    itemLogout = new JMenuItem("Logout");
    IconesInterface.aplicarIconeMenu(itemLogout);
    itemLogout.addActionListener(tb);
    menuSessao.add(itemLogout);
    aplicarMnemonics();

    menubar = new JMenuBar();
    menubar.add(menuOutros);
    menubar.add(menuSessao);
    setJMenuBar(menubar);

    criarPopupMenu();
    abrirPainelMenu(0);

    setSize(1450, 900);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          fecharJanelaPrincipal();
        }

        public void windowClosed(WindowEvent e)
        {
          encerrarSessaoEConexao();
        }
      });
    setVisible(true);
  }

  private void aplicarMnemonics()
  {
    menuOutros.setMnemonic(KeyEvent.VK_A);
    menuSessao.setMnemonic(KeyEvent.VK_S);

    items[0].setMnemonic(KeyEvent.VK_D);
    items[1].setMnemonic(KeyEvent.VK_R);
    items[2].setMnemonic(KeyEvent.VK_A);
    itemLogout.setMnemonic(KeyEvent.VK_L);
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(acaoBloqueadaPorConexaoFechada())
        return;

      if(event.getSource() == itemLogout)
        executarLogout();
      else if(event.getSource() == items[0])
        abrirPainelMenu(0);
      else if(event.getSource() == items[1])
        abrirPainelMenu(1);
      else if(event.getSource() == items[2])
        abrirPainelMenu(2);
    }
  }

  private void abrirPainelMenu(int indice)
  {
    if(acaoBloqueadaPorConexaoFechada())
      return;

    indicePainelActual = indice;
    painelRetiradasPendentesActual = false;

    if(indice == 0)
      mostrarPainel(new JPanel_DashboardSistema(connection, null, mensagemDashboard, criarNavegador()));
    else if(indice == 1)
      mostrarPainel(new JPanel_RegistarFuncionario(connection, carregarTabelasAutomaticamente));
    else if(indice == 2)
      mostrarPainel(new JPanel_AuditoriaSessoesFuncionarios(connection, carregarTabelasAutomaticamente));
  }

  private void reabrirPainelActual()
  {
    abrirPainelMenu(indicePainelActual);
  }

  private PainelNavegador criarNavegador()
  {
    return new PainelNavegador()
      {
        public void abrirPainel(JPanel painel)
        {
          abrirPainelNavegador(painel);
        }
      };
  }

  private void abrirPainelNavegador(JPanel painel)
  {
    if(painel instanceof JPanel_RegistarFuncionario)
    {
      abrirPainelMenu(1);
      return;
    }

    painelRetiradasPendentesActual = false;
    mostrarPainel(painel);
  }

  private void criarPopupMenu()
  {
    popupMenu = new JPopupMenu();
    itemCarregarTabelasAutomaticamente = new JCheckBoxMenuItem("Carregar tabelas automaticamente");
    itemCarregarTabelasAutomaticamente.setSelected(carregarTabelasAutomaticamente);
    itemCarregarTabelasAutomaticamente.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          if(acaoBloqueadaPorConexaoFechada())
            return;

          carregarTabelasAutomaticamente = itemCarregarTabelasAutomaticamente.isSelected();
          reabrirPainelActual();
        }
      });
    popupMenu.add(itemCarregarTabelasAutomaticamente);
    popupMenu.addSeparator();
    JMenuItem popupLogout = new JMenuItem("Logout");
    IconesInterface.aplicarIconeMenu(popupLogout);
    popupLogout.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          executarLogout();
        }
      });
    popupMenu.add(popupLogout);
    getRootPane().setComponentPopupMenu(popupMenu);
    ((JComponent) getContentPane()).setComponentPopupMenu(popupMenu);
  }

  private void mostrarPainel(JPanel painel)
  {
    Resources.InterfaceGraficaUtils.padronizarPainelComTabelas(painel);
    setContentPane(painel);
    aplicarPopupRecursivo(painel);
    revalidate();
    repaint();
  }

  private void executarLogout()
  {
    if(!DialogoLogout.confirmar(this, "Deseja terminar a sessão actual?"))
      return;

    encerrarSessaoEConexao(true);
    dispose();
    new LoginSistema();
  }

  private void fecharJanelaPrincipal()
  {
    encerrarSessaoEConexao(false);
    dispose();
    System.exit(0);
  }

  private boolean acaoBloqueadaPorConexaoFechada()
  {
    if(!conexaoEstaFechada())
      return false;

    forcarVoltarLoginPorConexaoFechada();
    return true;
  }

  private boolean conexaoEstaFechada()
  {
    try
    {
      return connection == null || connection.isClosed();
    } catch(Exception ex)
    {
      return true;
    }
  }

  public void forcarVoltarLoginPorConexaoFechada()
  {
    if(recursosEncerrados)
      return;

    recursosEncerrados = true;

    try
    {
      OracleConnection.closeConnection(connection);
    } catch(Exception ignored)
    {}

    mostrarErroGraveLogout();
    dispose();
    new LoginSistema();
  }

  private void encerrarSessaoEConexao()
  {
    encerrarSessaoEConexao(false);
  }

  private boolean encerrarSessaoEConexao(boolean mostrarErro)
  {
    if(recursosEncerrados)
      return true;

    recursosEncerrados = true;
    boolean sessaoFechada = true;

    try
    {
      sessaoFechada = SessaoFuncional.tentarFecharSessaoAdministrador(connection, Janela_Principal.this.codigoSessao);
    } catch(Exception ex)
    {
      sessaoFechada = false;
    }

    try
    {
      OracleConnection.closeConnection(connection);
    } catch(Exception ignored)
    {}

    if(!sessaoFechada && mostrarErro)
      mostrarErroGraveLogout();

    return sessaoFechada;
  }

  private void mostrarErroGraveLogout()
  {
    JOptionPane.showMessageDialog(this, TratadorConexaoFechada.mensagemConexaoFechada(), "Erro",
        JOptionPane.ERROR_MESSAGE);
  }

  private void aplicarPopupRecursivo(Component component)
  {
    if(component instanceof JComponent)
    {
      ((JComponent) component).setComponentPopupMenu(popupMenu);
      ((JComponent) component).setInheritsPopupMenu(true);
    }

    if(component instanceof Container)
    {
      Component[] filhos = ((Container) component).getComponents();
      for(int i = 0; i < filhos.length; i++)
        aplicarPopupRecursivo(filhos[i]);
    }
  }
}
