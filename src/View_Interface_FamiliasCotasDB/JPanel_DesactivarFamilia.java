package View_Interface_FamiliasCotasDB;

import Repository_SQL.FamiliasCotasDB.DesactivarFamiliaSQL;
import Repository_SQL.FamiliasCotasDB.DesactivarFamiliaSQL.ResultadoOperacao;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.regex.Pattern;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

public class JPanel_DesactivarFamilia extends JPanel
{
  private final Connection connection;
  private final Runnable voltarAoDashboard;
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTable tabelaFamilias;
  private DefaultTableModel modeloFamilias;
  private TableRowSorter<DefaultTableModel> sorterFamilias;
  private JTextField fieldPesquisaFamilias;
  private JComboBox<String> comboPesquisaFamilias;
  private JButton buttonActualizar;
  private JButton buttonDesactivar;
  private JButton buttonVoltar;

  public JPanel_DesactivarFamilia(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_DesactivarFamilia(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_DesactivarFamilia(Connection connection, boolean carregarTabelasAutomaticamente,
      Runnable voltarAoDashboard)
  {
    this.connection = connection;
    this.voltarAoDashboard = voltarAoDashboard;

    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));
    add(criarConteudo(), BorderLayout.CENTER);

    if(carregarTabelasAutomaticamente)
    {
      carregarFamilias();
    }
  }

  private JPanel criarConteudo()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1180, 720));
    card.add(InterfaceGraficaUtils.criarTopo("Apagar/Desactivar Família",
        "<html>Famílias sem consumo nem transferência são apagadas. Com histórico, ficam apenas inactivas.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.NORTH);
    card.add(criarCentro(), BorderLayout.CENTER);
    card.add(criarRodape(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);
    return painelExterno;
  }

  private JPanel criarCentro()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    painel.add(criarPesquisa(), BorderLayout.NORTH);

    modeloFamilias = new DefaultTableModel(new String[]
      {
          "Código Família", "Responsável", "Contacto", "Membros", "Estado", "Aldeia", "Coordenadas GPS"
      }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };

    tabelaFamilias = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaFamilias.setModel(modeloFamilias);
    tabelaFamilias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sorterFamilias = new TableRowSorter<DefaultTableModel>(modeloFamilias);
    tabelaFamilias.setRowSorter(sorterFamilias);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaFamilias, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1120, 500));
    painel.add(scroll, BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarPesquisa()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    fieldPesquisaFamilias = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(250, 36));
    comboPesquisaFamilias = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Família", "Responsável", "Contacto", "Estado", "Aldeia"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(180, 36));
    buttonActualizar = criarBotao("Mostrar Todos", 160, 36);

    fieldPesquisaFamilias.getDocument().addDocumentListener(new TratarPesquisa());
    comboPesquisaFamilias.addActionListener(tratarButtons);
    buttonActualizar.addActionListener(tratarButtons);

    painel.add(InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO));
    painel.add(fieldPesquisaFamilias);
    painel.add(comboPesquisaFamilias);
    painel.add(buttonActualizar);
    return painel;
  }

  private JPanel criarRodape()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel ajuda = InterfaceGraficaUtils.criarLabel("A família só é apagada se não existir histórico de consumo ou transferência.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltar = criarBotao("Voltar", 130, 42);
    buttonVoltar.addActionListener(tratarButtons);

    buttonDesactivar = criarBotao("Executar", 150, 42);
    buttonDesactivar.setIcon(FontIcon.of(FontAwesomeSolid.TRASH_ALT, 14, InterfaceGraficaUtils.COR_BRANCO));
    buttonDesactivar.setIconTextGap(8);
    buttonDesactivar.addActionListener(tratarButtons);

    botoes.add(buttonVoltar);
    botoes.add(buttonDesactivar);

    painel.add(ajuda, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JButton criarBotao(String texto, int largura, int altura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, altura));
    IconesInterface.aplicarIconeBotao(botao);
    Resources.IconesInterface.aplicarEfeitoHoverBotaoFamilias(botao);
    return botao;
  }

  private void carregarFamilias()
  {
    try
    {
      DesactivarFamiliaSQL.carregarFamilias(connection, modeloFamilias);
      aplicarFiltro();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar as famílias.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void desactivarFamiliaSelecionada()
  {
    Integer codigo = getCodigoFamiliaSelecionada();
    if(codigo == null)
    {
      JOptionPane.showMessageDialog(this, "Seleccione uma família.");
      return;
    }

    String responsavel = getValorSelecionado(1);
    int escolha = JOptionPane.showConfirmDialog(this,
        "Pretende apagar/desactivar a família " + codigo + " - " + responsavel + "?",
        "Confirmar operação", JOptionPane.YES_NO_OPTION);
    if(escolha != JOptionPane.YES_OPTION)
      return;

    try
    {
      ResultadoOperacao preValidacao = DesactivarFamiliaSQL.preValidarDesactivacao(connection, codigo.intValue());
      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(preValidacao.mensagem));
        return;
      }

      int confirmarAccao = JOptionPane.showConfirmDialog(this,
          MensagensInterface.formatarMensagem(preValidacao.mensagem) + "\n\nAcção prevista: " + preValidacao.accao + ". Pretende continuar?",
          "Confirmar " + preValidacao.accao, JOptionPane.YES_NO_OPTION);
      if(confirmarAccao != JOptionPane.YES_OPTION)
        return;

      ResultadoOperacao resultado = DesactivarFamiliaSQL.desactivarFamilia(connection, codigo.intValue());
      JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(resultado.mensagem));

      if(resultado.podeContinuar)
        carregarFamilias();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível desactivar família:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private Integer getCodigoFamiliaSelecionada()
  {
    int linhaView = tabelaFamilias.getSelectedRow();
    if(linhaView == -1)
      return null;

    int linhaModel = tabelaFamilias.convertRowIndexToModel(linhaView);
    return Integer.valueOf(modeloFamilias.getValueAt(linhaModel, 0).toString());
  }

  private String getValorSelecionado(int coluna)
  {
    int linhaView = tabelaFamilias.getSelectedRow();
    if(linhaView == -1)
      return "";

    int linhaModel = tabelaFamilias.convertRowIndexToModel(linhaView);
    Object valor = modeloFamilias.getValueAt(linhaModel, coluna);
    return valor == null ? "" : valor.toString();
  }

  private void aplicarFiltro()
  {
    if(sorterFamilias == null || fieldPesquisaFamilias == null)
      return;

    String texto = fieldPesquisaFamilias.getText().trim();
    if(texto.isEmpty())
    {
      sorterFamilias.setRowFilter(null);
      return;
    }

    sorterFamilias.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), getIndiceFiltro()));
  }

  private int getIndiceFiltro()
  {
    String criterio = comboPesquisaFamilias.getSelectedItem().toString();
    if("Responsável".equals(criterio))
      return 1;
    if("Contacto".equals(criterio))
      return 2;
    if("Estado".equals(criterio))
      return 4;
    if("Aldeia".equals(criterio))
      return 5;
    return 0;
  }

  private class TratarPesquisa implements DocumentListener
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
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == buttonVoltar)
      {
        if(voltarAoDashboard != null)
          voltarAoDashboard.run();
        return;
      }

      if(event.getSource() == buttonActualizar)
        carregarFamilias();
      else if(event.getSource() == comboPesquisaFamilias)
        aplicarFiltro();
      else if(event.getSource() == buttonDesactivar)
        desactivarFamiliaSelecionada();
    }
  }
}
