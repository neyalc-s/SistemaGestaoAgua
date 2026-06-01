package View_Interface_DistribuicaoConsumoDB;

import Repository_SQL.DistribuicaoConsumoDB.RegistroConsumoConsultasSQL;
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

public class JPanel_Registro_Consumo_Consultas extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaRegistros;
  private DefaultTableModel modeloRegistros;
  private JTextField fieldPesquisa;
  private JComboBox<String> comboCriterio;
  private JButton buttonPesquisar;
  private JButton buttonMostrarTodos;

  public JPanel_Registro_Consumo_Consultas(Connection connection)
  {
    this(connection, false);
  }

  public JPanel_Registro_Consumo_Consultas(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this.connection = connection;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    JPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 20),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1280, 760));

    card.add(criarTopo(), BorderLayout.NORTH);
    card.add(criarCentro(), BorderLayout.CENTER);

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
    return InterfaceGraficaUtils.criarTopo("Consultas de Registro de Consumo",
        "<html>Consulte o histórico de retiradas registadas em <b>Registro_Consumo</b>. "
            + "Use <b>Pesquisar</b> para filtrar ou <b>Mostrar Todos</b> para carregar todos os registos.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JComponent criarCentro()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Histórico de Registros de Consumo",
        InterfaceGraficaUtils.FONT_LABEL, InterfaceGraficaUtils.COR_AZUL);

    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topo.add(label, BorderLayout.NORTH);
    topo.add(criarLinhaPesquisa(), BorderLayout.CENTER);

    tabelaRegistros = criarTabelaBase();
    modeloRegistros = RegistroConsumoConsultasSQL.criarModeloVazio();
    tabelaRegistros.setModel(modeloRegistros);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabelaRegistros), BorderLayout.CENTER);

    return panel;
  }

  private JPanel criarLinhaPesquisa()
  {
    JPanel linha = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    comboCriterio = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código do Registo", "Código da Família", "Código do Ponto", "Código da Cota", "Pessoa de Coleta",
          "Método de Autenticação", "Data/Hora"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(230, 36));

    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(280, 36));

    buttonPesquisar = criarBotao("Pesquisar");
    buttonPesquisar.addActionListener(tratarButtons);

    buttonMostrarTodos = criarBotao("Mostrar Todos");
    buttonMostrarTodos.addActionListener(tratarButtons);

    linha.add(new JLabel("Pesquisar por:"));
    linha.add(comboCriterio);
    linha.add(fieldPesquisa);
    linha.add(buttonPesquisar);
    linha.add(buttonMostrarTodos);

    return linha;
  }

  private JTable criarTabelaBase()
  {
    return InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA, InterfaceGraficaUtils.FONT_HEADER,
        InterfaceGraficaUtils.COR_GRID_TABELA, InterfaceGraficaUtils.COR_SELECAO_TABELA,
        InterfaceGraficaUtils.COR_TEXTO);
  }

  private JScrollPane criarScrollTabela(JTable tabela)
  {
    return InterfaceGraficaUtils.criarScrollTabela(tabela, InterfaceGraficaUtils.COR_BORDA_TABELA);
  }

  private JButton criarBotao(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(150, 40));
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

    carregarModelo(() -> RegistroConsumoConsultasSQL.pesquisar(connection, comboCriterio.getSelectedItem().toString(),
        valor));
  }

  private void mostrarTodos()
  {
    carregarModelo(() -> RegistroConsumoConsultasSQL.consultarTodos(connection));
  }

  private void carregarModelo(ConsultaModelo consulta)
  {
    try
    {
      modeloRegistros = consulta.executar();
      tabelaRegistros.setModel(modeloRegistros);
      InterfaceGraficaUtils.configurarTabela(tabelaRegistros, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
          InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
      SwingUtilities.invokeLater(() -> InterfaceGraficaUtils.ajustarLarguraColunas(tabelaRegistros,
          InterfaceGraficaUtils.FONT_TABELA, InterfaceGraficaUtils.FONT_HEADER));
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível consultar registros de consumo:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private interface ConsultaModelo
  {
    DefaultTableModel executar() throws Exception;
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      Object source = e.getSource();

      if(source == buttonPesquisar)
      {
        pesquisar();
        return;
      }

      if(source == buttonMostrarTodos)
        mostrarTodos();
    }
  }
}
