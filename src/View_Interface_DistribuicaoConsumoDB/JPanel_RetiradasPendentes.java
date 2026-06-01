package View_Interface_DistribuicaoConsumoDB;

import Repository_SQL.DistribuicaoConsumoDB.RetiradasPendentesSQL;
import Resources.IconesInterface;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class JPanel_RetiradasPendentes extends JPanel
{
  private final Connection connection;
  private final Runnable voltarDashboard;
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaPendencias;
  private DefaultTableModel modeloPendencias;
  private JTextField fieldPesquisa;
  private JComboBox<String> comboCriterio;
  private JButton buttonPesquisar;
  private JButton buttonMostrarTodos;
  private JButton buttonProcessarPendentes;
  private JButton buttonVoltar;

  public JPanel_RetiradasPendentes(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_RetiradasPendentes(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    JPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 20),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1280, 760));

    card.add(criarTopo(), BorderLayout.NORTH);
    card.add(criarCentro(), BorderLayout.CENTER);
    card.add(criarRodape(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(18, 18, 18, 18);

    add(card, gbc);

    if(carregarTabelasAutomaticamente)
      mostrarTodos();
  }

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Retiradas Pendentes",
        "<html>Consulte e processe retiradas de água registadas como pendentes.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JComponent criarCentro()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Operações Pendentes",
        InterfaceGraficaUtils.FONT_LABEL, InterfaceGraficaUtils.COR_AZUL);

    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topo.add(label, BorderLayout.NORTH);
    topo.add(criarLinhaPesquisa(), BorderLayout.CENTER);

    tabelaPendencias = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    modeloPendencias = RetiradasPendentesSQL.criarModeloVazio();
    tabelaPendencias.setModel(modeloPendencias);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(InterfaceGraficaUtils.criarScrollTabela(tabelaPendencias, InterfaceGraficaUtils.COR_BORDA_TABELA),
        BorderLayout.CENTER);

    return panel;
  }

  private JPanel criarLinhaPesquisa()
  {
    JPanel linha = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    comboCriterio = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Pendência", "Família", "Cota", "Ponto", "Estado", "Pessoa de Coleta"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(220, 36));

    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(280, 36));

    buttonPesquisar = criarBotao("Pesquisar", 145);
    buttonMostrarTodos = criarBotao("Mostrar Todos", 160);
    buttonPesquisar.addActionListener(tratarButtons);
    buttonMostrarTodos.addActionListener(tratarButtons);

    linha.add(new JLabel("Pesquisar por:"));
    linha.add(comboCriterio);
    linha.add(fieldPesquisa);
    linha.add(buttonPesquisar);
    linha.add(buttonMostrarTodos);

    return linha;
  }

  private JPanel criarRodape()
  {
    JPanel rodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    JLabel ajuda = InterfaceGraficaUtils.criarLabel("Processar Pendentes tenta aprovar ou rejeitar os pedidos em aberto.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltar = criarBotao("Voltar", 130);
    buttonProcessarPendentes = criarBotao("Processar Pendentes", 210);
    buttonVoltar.addActionListener(tratarButtons);
    buttonProcessarPendentes.addActionListener(tratarButtons);

    botoes.add(buttonVoltar);
    botoes.add(buttonProcessarPendentes);
    rodape.add(ajuda, BorderLayout.WEST);
    rodape.add(botoes, BorderLayout.EAST);

    return rodape;
  }

  private JButton criarBotao(String texto, int largura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, 40));
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  private void pesquisar()
  {
    String valor = fieldPesquisa.getText().trim();
    if(valor.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Informe um valor para pesquisar.");
      return;
    }

    carregarModelo(() -> RetiradasPendentesSQL.pesquisar(connection, comboCriterio.getSelectedItem().toString(),
        valor));
  }

  private void mostrarTodos()
  {
    carregarModelo(() -> RetiradasPendentesSQL.consultarTodos(connection));
  }

  private void processarPendentes()
  {
    int opcao = JOptionPane.showConfirmDialog(this, "Processar retiradas pendentes agora?",
        "Processar Pendentes", JOptionPane.YES_NO_OPTION);
    if(opcao != JOptionPane.YES_OPTION)
      return;

    try
    {
      RetiradasPendentesSQL.processarPendentes(connection);
      JOptionPane.showMessageDialog(this, "Processamento concluído.");
      mostrarTodos();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível processar pendências:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarModelo(ConsultaModelo consulta)
  {
    try
    {
      modeloPendencias = consulta.executar();
      tabelaPendencias.setModel(modeloPendencias);
      InterfaceGraficaUtils.configurarTabela(tabelaPendencias, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
          InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
      SwingUtilities.invokeLater(() -> InterfaceGraficaUtils.ajustarLarguraColunas(tabelaPendencias,
          InterfaceGraficaUtils.FONT_TABELA, InterfaceGraficaUtils.FONT_HEADER));
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível consultar retiradas pendentes:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private interface ConsultaModelo
  {
    DefaultTableModel executar() throws Exception;
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      Object source = e.getSource();

      if(source == buttonPesquisar)
      {
        pesquisar();
        return;
      }

      if(source == buttonMostrarTodos)
      {
        mostrarTodos();
        return;
      }

      if(source == buttonProcessarPendentes)
      {
        processarPendentes();
        return;
      }

      if(source == buttonVoltar && voltarDashboard != null)
        voltarDashboard.run();
    }
  }
}
