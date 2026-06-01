package View_Interface_EquipesGestaoDB;

import Login.LoginSistema;
import Connection.OracleConnection;
import Resources.DialogoLogout;
import Resources.PainelNavegador;
import Resources.SessaoFuncional;
import Resources.IconesInterface;
import Resources.TratadorConexaoFechada;
import Resources.VerificadorConexaoRemota;
import Resources.BloqueadorOperacaoRemota;
import Resources.SaudacaoDashboardUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Janela_Principal extends JFrame
{
  private String name_items[] =
    {
        "Dashboard", "Registar Comité", "Registar Equipe Técnica", "Equipes Técnicas", "Alertas Qualidade Água",
        "Comités Locais"
    };
  private JMenuItem items[];
  private JMenu menuOperacoes, menuConsultas, menuSessao;
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
  private final int codigoSessaoLocal;
  private final String usernameOracle;
  private final int codigoNo;
  private final String mensagemDashboard;

  public Janela_Principal(final Connection connection)
  {
    this(connection, 0, null, 0);
  }

  public Janela_Principal(final Connection connection, int codigoSessaoLocal, String usernameOracle, int codigoNo)
  {
    this(connection, codigoSessaoLocal, usernameOracle, codigoNo, usernameOracle);
  }

  public Janela_Principal(final Connection connection, int codigoSessaoLocal, String usernameOracle, int codigoNo,
      String nomeFuncionario)
  {
    super("EquipesGestaoDB");
    this.connection = connection;
    this.codigoSessaoLocal = codigoSessaoLocal;
    this.usernameOracle = usernameOracle;
    this.codigoNo = codigoNo;
    this.mensagemDashboard = SaudacaoDashboardUtils.criar(nomeFuncionario, usernameOracle);

    items = new JMenuItem[name_items.length];
    menuOperacoes = new JMenu("Operações");
    menuConsultas = new JMenu("Consultas");

    for(int i = 0; i < items.length; i++)
    {
      items[i] = new JMenuItem(name_items[i]);
      IconesInterface.aplicarIconeMenu(items[i]);
      items[i].addActionListener(tb);
    }

    menuOperacoes.add(items[1]);
    menuOperacoes.addSeparator();
    menuOperacoes.add(items[2]);
    menuConsultas.add(items[0]);
    menuConsultas.add(items[3]);
    menuConsultas.add(items[4]);
    menuConsultas.add(items[5]);

    menuSessao = new JMenu("Sessão");
    itemLogout = new JMenuItem("Logout");
    IconesInterface.aplicarIconeMenu(itemLogout);
    itemLogout.addActionListener(tb);
    menuSessao.add(itemLogout);
    aplicarMnemonics();

    menubar = new JMenuBar();
    menubar.add(menuOperacoes);
    menubar.add(menuConsultas);
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
    menuOperacoes.setMnemonic(KeyEvent.VK_O);
    menuConsultas.setMnemonic(KeyEvent.VK_C);
    menuSessao.setMnemonic(KeyEvent.VK_S);

    items[0].setMnemonic(KeyEvent.VK_D);
    items[1].setMnemonic(KeyEvent.VK_C);
    items[2].setMnemonic(KeyEvent.VK_E);
    items[3].setMnemonic(KeyEvent.VK_T);
    items[4].setMnemonic(KeyEvent.VK_A);
    items[5].setMnemonic(KeyEvent.VK_L);
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
      else if(event.getSource() == items[3])
        abrirPainelMenu(3);
      else if(event.getSource() == items[4])
        abrirPainelMenu(4);
      else if(event.getSource() == items[5])
        abrirPainelMenu(5);
    }
  }

  private void abrirPainelMenu(int indice)
  {
    if(acaoBloqueadaPorConexaoFechada())
      return;

    indicePainelActual = indice;
    painelRetiradasPendentesActual = false;

    if(indice == 0)
      mostrarPainel(new JPanel_DashboardEquipesGestao(connection, usernameOracle, mensagemDashboard, criarNavegador()));
    else if(indice == 1)
      mostrarPainel(new JPanel_RegistarComite(connection));
    else if(indice == 2)
      abrirPainelOperacaoRemota("Registar Equipe Técnica", new VerificadorConexaoRemota.NoRemoto[]
        {
            VerificadorConexaoRemota.NoRemoto.TRANSFERENCIAS_RECURSOS
        }, new PainelFactory()
          {
            public JPanel criar(boolean carregarTabelasAutomaticamente)
            {
              return new JPanel_RegistarEquipeTecnica(connection, carregarTabelasAutomaticamente);
            }
          });
    else if(indice == 3)
      mostrarPainel(new JPanel_Equipe_Tecnica_Consultas(connection, carregarTabelasAutomaticamente));
    else if(indice == 4)
      mostrarPainel(new JPanel_AlertasQualidadeAgua(connection, carregarTabelasAutomaticamente));
    else if(indice == 5)
      mostrarPainel(new JPanel_Comite_Consultas(connection, carregarTabelasAutomaticamente));
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
    if(painel instanceof JPanel_RegistarComite)
    {
      abrirPainelMenu(1);
      return;
    }

    if(painel instanceof JPanel_RegistarEquipeTecnica)
    {
      abrirPainelMenu(2);
      return;
    }

    if(painel instanceof JPanel_Equipe_Tecnica_Consultas)
    {
      abrirPainelMenu(3);
      return;
    }

    if(painel instanceof JPanel_Comite_Consultas)
    {
      abrirPainelMenu(5);
      return;
    }

    painelRetiradasPendentesActual = false;
    mostrarPainel(painel);
  }


  private interface PainelFactory
  {
    JPanel criar(boolean carregarTabelasAutomaticamente);
  }

  private void abrirPainelOperacaoRemota(String tituloOperacao, VerificadorConexaoRemota.NoRemoto[] nosRemotos,
      PainelFactory factory)
  {
    VerificadorConexaoRemota.ResultadoVerificacao resultado = VerificadorConexaoRemota.verificar(connection,
        nosRemotos);
    JPanel painel = factory.criar(resultado.isDisponivel() && carregarTabelasAutomaticamente);

    mostrarPainel(painel);

    if(!resultado.isDisponivel())
      BloqueadorOperacaoRemota.bloquear(painel, resultado.getMensagem());
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
      sessaoFechada = SessaoFuncional.tentarFecharSessaoLocal(connection, Janela_Principal.this.codigoSessaoLocal);
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
