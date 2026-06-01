package View_Interface_DistribuicaoConsumoDB;

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
        "Dashboard", "Registar Ponto de Distribuição", "Alterar Estado do Ponto", "Associar Pontos a Recurso",
        "Associar Pontos a Comité", "Registar Manutenção", "Registar Abastecimento",
        "Cancelar Abastecimento", "Retirar Água", "Actualizar Observação Registro Consumo",
        "Pontos de Distribuição", "Registros de Consumo", "Histórico de Manutenções"
    };
  private JMenuItem items[];
  private JMenu menuOperacoes, menuConsultas, menuPendentes, menuSessao;
  private JMenuBar menubar;
  private JPopupMenu popupMenu;
  private JCheckBoxMenuItem itemCarregarTabelasAutomaticamente;
  private JMenuItem itemRetiradasPendentes;
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
    super("DistribuicaoConsumoDB");
    this.connection = connection;
    this.codigoSessaoLocal = codigoSessaoLocal;
    this.usernameOracle = usernameOracle;
    this.codigoNo = codigoNo;
    this.mensagemDashboard = SaudacaoDashboardUtils.criar(nomeFuncionario, usernameOracle);

    items = new JMenuItem[name_items.length];
    menuOperacoes = new JMenu("Operações");
    menuConsultas = new JMenu("Consultas");
    menuPendentes = new JMenu("Operações Pendentes");

    for(int i = 0; i < items.length; i++)
    {
      items[i] = new JMenuItem(name_items[i]);
      IconesInterface.aplicarIconeMenu(items[i]);
      items[i].addActionListener(tb);
    }

    menuOperacoes.add(items[2]);
    menuOperacoes.add(items[9]);
    menuOperacoes.addSeparator();
    menuOperacoes.add(items[1]);
    menuOperacoes.add(items[3]);
    menuOperacoes.add(items[4]);
    menuOperacoes.add(items[5]);
    menuOperacoes.add(items[6]);
    menuOperacoes.add(items[7]);
    menuOperacoes.add(items[8]);

    menuConsultas.add(items[0]);
    menuConsultas.add(items[10]);
    menuConsultas.add(items[11]);
    menuConsultas.add(items[12]);

    itemRetiradasPendentes = new JMenuItem("Retiradas Pendentes");
    IconesInterface.aplicarIconeMenu(itemRetiradasPendentes);
    itemRetiradasPendentes.addActionListener(tb);
    menuPendentes.add(itemRetiradasPendentes);

    menuSessao = new JMenu("Sessão");
    itemLogout = new JMenuItem("Logout");
    IconesInterface.aplicarIconeMenu(itemLogout);
    itemLogout.addActionListener(tb);
    menuSessao.add(itemLogout);
    aplicarMnemonics();

    menubar = new JMenuBar();
    menubar.add(menuOperacoes);
    menubar.add(menuPendentes);
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
    menuPendentes.setMnemonic(KeyEvent.VK_P);
    menuConsultas.setMnemonic(KeyEvent.VK_C);
    menuSessao.setMnemonic(KeyEvent.VK_S);

    items[0].setMnemonic(KeyEvent.VK_D);
    items[1].setMnemonic(KeyEvent.VK_P);
    items[2].setMnemonic(KeyEvent.VK_E);
    items[3].setMnemonic(KeyEvent.VK_R);
    items[4].setMnemonic(KeyEvent.VK_C);
    items[5].setMnemonic(KeyEvent.VK_M);
    items[6].setMnemonic(KeyEvent.VK_A);
    items[7].setMnemonic(KeyEvent.VK_B);
    items[8].setMnemonic(KeyEvent.VK_T);
    items[9].setMnemonic(KeyEvent.VK_O);
    items[10].setMnemonic(KeyEvent.VK_P);
    items[11].setMnemonic(KeyEvent.VK_R);
    items[12].setMnemonic(KeyEvent.VK_H);
    itemRetiradasPendentes.setMnemonic(KeyEvent.VK_R);
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
      else if(event.getSource() == itemRetiradasPendentes)
        abrirPainelRetiradasPendentes();
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
      else if(event.getSource() == items[6])
        abrirPainelMenu(6);
      else if(event.getSource() == items[7])
        abrirPainelMenu(7);
      else if(event.getSource() == items[8])
        abrirPainelMenu(8);
      else if(event.getSource() == items[9])
        abrirPainelMenu(9);
      else if(event.getSource() == items[10])
        abrirPainelMenu(10);
      else if(event.getSource() == items[11])
        abrirPainelMenu(11);
      else if(event.getSource() == items[12])
        abrirPainelMenu(12);
    }
  }

  private void abrirPainelMenu(int indice)
  {
    if(acaoBloqueadaPorConexaoFechada())
      return;

    indicePainelActual = indice;
    painelRetiradasPendentesActual = false;

    if(indice == 0)
      mostrarPainel(new JPanel_DashboardDistribuicaoConsumo(connection, usernameOracle, mensagemDashboard, criarNavegador()));
    else if(indice == 1)
      abrirPainelOperacaoRemota("Registar Ponto de Distribuição", new VerificadorConexaoRemota.NoRemoto[]
        {
            VerificadorConexaoRemota.NoRemoto.TRANSFERENCIAS_RECURSOS,
            VerificadorConexaoRemota.NoRemoto.EQUIPES_GESTAO
        }, new PainelFactory()
          {
            public JPanel criar(boolean carregarTabelasAutomaticamente)
            {
              return new JPanel_RegistarPontoDistribuicao(connection, carregarTabelasAutomaticamente,
                  criarVoltarDashboard());
            }
          });
    else if(indice == 2)
      mostrarPainel(new JPanel_AlterarEstadoPontoDistribuicao(connection, carregarTabelasAutomaticamente,
          criarVoltarDashboard()));
    else if(indice == 3)
      abrirPainelOperacaoRemota("Associar Pontos a Recurso", new VerificadorConexaoRemota.NoRemoto[]
        {
            VerificadorConexaoRemota.NoRemoto.TRANSFERENCIAS_RECURSOS
        }, new PainelFactory()
          {
            public JPanel criar(boolean carregarTabelasAutomaticamente)
            {
              return new JPanel_AssociarPontosRecursoHidrico(connection, carregarTabelasAutomaticamente,
                  criarVoltarDashboard());
            }
          });
    else if(indice == 4)
      abrirPainelOperacaoRemota("Associar Pontos a Comité", new VerificadorConexaoRemota.NoRemoto[]
        {
            VerificadorConexaoRemota.NoRemoto.EQUIPES_GESTAO
        }, new PainelFactory()
          {
            public JPanel criar(boolean carregarTabelasAutomaticamente)
            {
              return new JPanel_AssociarPontosComite(connection, carregarTabelasAutomaticamente,
                  criarVoltarDashboard());
            }
          });
    else if(indice == 5)
      abrirPainelOperacaoRemota("Registar Manutenção", new VerificadorConexaoRemota.NoRemoto[]
        {
            VerificadorConexaoRemota.NoRemoto.EQUIPES_GESTAO
        }, new PainelFactory()
          {
            public JPanel criar(boolean carregarTabelasAutomaticamente)
            {
              return new JPanel_RegistarManutencaoPonto(connection, carregarTabelasAutomaticamente,
                  criarVoltarDashboard());
            }
          });
    else if(indice == 6)
      abrirPainelOperacaoRemota("Registar Abastecimento", new VerificadorConexaoRemota.NoRemoto[]
        {
            VerificadorConexaoRemota.NoRemoto.TRANSFERENCIAS_RECURSOS
        }, new PainelFactory()
          {
            public JPanel criar(boolean carregarTabelasAutomaticamente)
            {
              return new JPanel_RegistarAbastecimento(connection, carregarTabelasAutomaticamente,
                  criarVoltarDashboard());
            }
          });
    else if(indice == 7)
      abrirPainelOperacaoRemota("Cancelar Abastecimento", new VerificadorConexaoRemota.NoRemoto[]
        {
            VerificadorConexaoRemota.NoRemoto.TRANSFERENCIAS_RECURSOS
        }, new PainelFactory()
          {
            public JPanel criar(boolean carregarTabelasAutomaticamente)
            {
              return new JPanel_CancelarAbastecimento(connection, carregarTabelasAutomaticamente,
                  criarVoltarDashboard());
            }
          });
    else if(indice == 8)
      mostrarPainel(new JPanel_RetirarAgua(connection, carregarTabelasAutomaticamente, criarVoltarDashboard()));
    else if(indice == 9)
      mostrarPainel(new JPanel_Actualizar_Observacao_Registro_Consumo(connection));
    else if(indice == 10)
      mostrarPainel(new JPanel_Ponto_Distribuicao_Consultas(connection, carregarTabelasAutomaticamente));
    else if(indice == 11)
      mostrarPainel(new JPanel_Registro_Consumo_Consultas(connection, carregarTabelasAutomaticamente));
    else if(indice == 12)
      mostrarPainel(new JPanel_Historico_Manutencao_Consultas(connection, carregarTabelasAutomaticamente));
  }

  private void abrirPainelRetiradasPendentes()
  {
    painelRetiradasPendentesActual = true;
    mostrarPainel(new JPanel_RetiradasPendentes(connection, carregarTabelasAutomaticamente, criarVoltarDashboard()));
  }

  private void reabrirPainelActual()
  {
    if(painelRetiradasPendentesActual)
      abrirPainelRetiradasPendentes();
    else
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

  private Runnable criarVoltarDashboard()
  {
    return new Runnable()
      {
        public void run()
        {
          abrirPainelMenu(0);
        }
      };
  }

  private void abrirPainelNavegador(JPanel painel)
  {
    if(painel instanceof JPanel_RegistarManutencaoPonto)
    {
      abrirPainelMenu(5);
      return;
    }

    if(painel instanceof JPanel_RegistarAbastecimento)
    {
      abrirPainelMenu(6);
      return;
    }

    if(painel instanceof JPanel_RetirarAgua)
    {
      abrirPainelMenu(8);
      return;
    }

    if(painel instanceof JPanel_Ponto_Distribuicao_Consultas)
    {
      abrirPainelMenu(10);
      return;
    }

    if(painel instanceof JPanel_Registro_Consumo_Consultas)
    {
      abrirPainelMenu(11);
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
