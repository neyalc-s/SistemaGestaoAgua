package View_Interface_TransferenciasRecursosDB;

import Repository_SQL.TransferenciasRecursosDB.TransferenciaCotaConsultasSQL;
import Resources.IconesInterface;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.ConsultaRemotaUtils;
import Resources.VerificadorConexaoRemota;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;

public class JPanel_Transferencia_Cota_Consultas extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaTransferencias;
  private DefaultTableModel modeloTransferencias;
  private TableRowSorter<DefaultTableModel> sorterTransferencias;
  private JTextField fieldPesquisa;
  private JComboBox<String> comboPesquisa;
  private JButton buttonPesquisar;
  private JButton buttonMostrarTodos;

  public JPanel_Transferencia_Cota_Consultas(Connection connection)
  {
    this(connection, false);
  }

  public JPanel_Transferencia_Cota_Consultas(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this.connection = connection;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    JPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 20),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

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
      carregarTransferencias();
  }

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Histórico de Transferências de Cota",
        "<html>Consulta própria das transferências registadas, separada da operação de actualizar motivo.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarCentro()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    painel.add(criarBarraPesquisa(), BorderLayout.NORTH);

    tabelaTransferencias = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaTransferencias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tabelaTransferencias.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    modeloTransferencias = new DefaultTableModel();
    tabelaTransferencias.setModel(modeloTransferencias);
    sorterTransferencias = new TableRowSorter<DefaultTableModel>(modeloTransferencias);
    tabelaTransferencias.setRowSorter(sorterTransferencias);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaTransferencias,
        InterfaceGraficaUtils.COR_BORDA_TABELA);
    painel.add(scroll, BorderLayout.CENTER);

    return painel;
  }

  private JPanel criarBarraPesquisa()
  {
    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    JLabel label = InterfaceGraficaUtils.criarLabel("Transferências de cota por família e participação",
        InterfaceGraficaUtils.FONT_LABEL, InterfaceGraficaUtils.COR_AZUL);

    JPanel linhaControles = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(20, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(260, 36));
    fieldPesquisa.getDocument().addDocumentListener(new DocumentListener()
      {
        public void insertUpdate(DocumentEvent e)
        {
          aplicarFiltro();
        }

        public void removeUpdate(DocumentEvent e)
        {
          aplicarFiltro();
        }

        public void changedUpdate(DocumentEvent e)
        {
          aplicarFiltro();
        }
      });
    comboPesquisa = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "codigo_tc", "cod_fam_doadora_tc", "familia_doadora", "cod_fam_receptora_tc", "familia_receptora",
          "motivo_solicitacao_tc", "papel_na_transferencia"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(230, 36));
    buttonPesquisar = criarBotao("Pesquisar");
    buttonMostrarTodos = criarBotao("Mostrar Todos");

    buttonPesquisar.addActionListener(tratarButtons);
    buttonMostrarTodos.addActionListener(tratarButtons);
    comboPesquisa.addActionListener(tratarButtons);

    linhaControles.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    linhaControles.add(fieldPesquisa);
    linhaControles.add(comboPesquisa);
    linhaControles.add(buttonPesquisar);
    linhaControles.add(buttonMostrarTodos);

    topo.add(label, BorderLayout.NORTH);
    topo.add(linhaControles, BorderLayout.CENTER);
    return topo;
  }

  private JButton criarBotao(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(150, 40));
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  private void carregarTransferencias()
  {
    if(!ConsultaRemotaUtils.prepararConsulta(connection, new VerificadorConexaoRemota.NoRemoto[]
      {
          VerificadorConexaoRemota.NoRemoto.FAMILIAS_COTAS
      }, tabelaTransferencias, new JComponent[]
        {
            fieldPesquisa, comboPesquisa, buttonPesquisar, buttonMostrarTodos
        }))
      return;

    try
    {
      modeloTransferencias = TransferenciaCotaConsultasSQL.carregarTransferencias(connection);
      tabelaTransferencias.setModel(modeloTransferencias);
      InterfaceGraficaUtils.configurarTabela(tabelaTransferencias, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
          InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
      tabelaTransferencias.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      sorterTransferencias = new TableRowSorter<DefaultTableModel>(modeloTransferencias);
      tabelaTransferencias.setRowSorter(sorterTransferencias);
      SwingUtilities.invokeLater(() -> InterfaceGraficaUtils.ajustarLarguraColunas(tabelaTransferencias,
          InterfaceGraficaUtils.FONT_TABELA, InterfaceGraficaUtils.FONT_HEADER));
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar transferências:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltro()
  {
    if(sorterTransferencias == null || modeloTransferencias == null || comboPesquisa == null)
      return;

    String texto = fieldPesquisa.getText().trim();
    if(texto.isEmpty())
    {
      sorterTransferencias.setRowFilter(null);
      return;
    }

    String colunaFiltro = comboPesquisa.getSelectedItem().toString();
    int coluna = InterfaceGraficaUtils.encontrarIndiceColuna(modeloTransferencias, colunaFiltro);
    if("codigo_tc".equals(colunaFiltro) || "cod_fam_doadora_tc".equals(colunaFiltro)
        || "cod_fam_receptora_tc".equals(colunaFiltro))
      sorterTransferencias.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(texto) + "$", coluna));
    else
      sorterTransferencias.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), coluna));
  }

  private void pesquisarTransferencias()
  {
    if(!ConsultaRemotaUtils.prepararConsulta(connection, new VerificadorConexaoRemota.NoRemoto[]
      {
          VerificadorConexaoRemota.NoRemoto.FAMILIAS_COTAS
      }, tabelaTransferencias, new JComponent[]
        {
            fieldPesquisa, comboPesquisa, buttonPesquisar, buttonMostrarTodos
        }))
      return;

    String texto = fieldPesquisa.getText().trim();
    if(texto.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Informe um valor para pesquisar ou clique em Mostrar Todos.");
      return;
    }

    try
    {
      modeloTransferencias = TransferenciaCotaConsultasSQL.pesquisarTransferencias(connection,
          comboPesquisa.getSelectedItem().toString(), texto);
      tabelaTransferencias.setModel(modeloTransferencias);
      InterfaceGraficaUtils.configurarTabela(tabelaTransferencias, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
          InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
      tabelaTransferencias.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
      sorterTransferencias = new TableRowSorter<DefaultTableModel>(modeloTransferencias);
      tabelaTransferencias.setRowSorter(sorterTransferencias);
      aplicarFiltro();
      SwingUtilities.invokeLater(() -> InterfaceGraficaUtils.ajustarLarguraColunas(tabelaTransferencias,
          InterfaceGraficaUtils.FONT_TABELA, InterfaceGraficaUtils.FONT_HEADER));
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível pesquisar transferências:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void mostrarTodos()
  {
    fieldPesquisa.setText("");
    carregarTransferencias();
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == buttonPesquisar || event.getSource() == comboPesquisa)
      {
        if(event.getSource() == buttonPesquisar)
          pesquisarTransferencias();
        else
          aplicarFiltro();
        return;
      }

      if(event.getSource() == buttonMostrarTodos)
        mostrarTodos();
    }
  }
}
