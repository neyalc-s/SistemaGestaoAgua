package View_Interface_FamiliasCotasDB;

import Resources.MensagensInterface;

import Repository_SQL.FamiliasCotasDB.CotaAguaConsultasSQL;
import Resources.*;

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

public class JPanel_Cota_Agua_Consultas extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  // TAB 1
  private JTable tabelaCotasFamilias;
  private DefaultTableModel modeloCotasFamilias;
  private TableRowSorter<DefaultTableModel> sorterCotasFamilias;
  private JTextField fieldFiltroCotasFamilias;
  private JButton buttonActualizarCotasFamilias;
  private JButton buttonLimparCotasFamilias;
  private JButton buttonGerarCotasSemanais;
  private JButton buttonAjudaCotasFamilias;

  // TAB 2
  private JTable tabelaRegistosConsumo;
  private DefaultTableModel modeloRegistosConsumo;
  private TableRowSorter<DefaultTableModel> sorterRegistosConsumo;
  private JTextField fieldFiltroRegistosConsumo;
  private JButton buttonActualizarRegistosConsumo;
  private JButton buttonLimparRegistosConsumo;
  private JButton buttonAjudaRegistosConsumo;

  public JPanel_Cota_Agua_Consultas(Connection connection)
  {
    this(connection, false);
  }

  public JPanel_Cota_Agua_Consultas(Connection connection, boolean carregarTabelasAutomaticamente)
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
    {
      carregarTabelaCotasFamilias();
      carregarTabelaRegistosConsumo();
    }
  }

  // ========================= INICIO DO CODIGO DE INTERFACE GRAFICA
  // =========================

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Consultas de Cota de Água",
        "<html>As consultas foram agrupadas em dois separadores: cotas das famílias e registos de consumo. "
            + "Na primeira aba a pesquisa é apenas por código_fb, e na segunda aba apenas por código de cota.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JComponent criarCentro()
  {
    JTabbedPane tabs = new JTabbedPane();
    tabs.setFont(InterfaceGraficaUtils.FONT_ABA);

    tabs.addTab("Cotas de Todas as Famílias", criarTabCotasFamilias());
    tabs.addTab("Registos de Consumo por Cota", criarTabRegistosConsumo());

    return tabs;
  }

  private JPanel criarTabCotasFamilias()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Todas as Cotas de Água de Todas as Famílias",
        InterfaceGraficaUtils.FONT_LABEL, InterfaceGraficaUtils.COR_AZUL);

    fieldFiltroCotasFamilias = InterfaceGraficaUtils.criarCampoTexto(18, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(260, 36));
    fieldFiltroCotasFamilias.getDocument().addDocumentListener(new TratarPesquisa(OrigemFiltro.COTAS_FAMILIAS));

    buttonActualizarCotasFamilias = criarBotao("Mostrar Todos");
    buttonActualizarCotasFamilias.addActionListener(tratarButtons);

    buttonLimparCotasFamilias = criarBotao("Limpar Filtro");
    buttonLimparCotasFamilias.addActionListener(tratarButtons);

    buttonGerarCotasSemanais = criarBotao("Gerar Cotas");
    buttonGerarCotasSemanais.addActionListener(tratarButtons);

    buttonAjudaCotasFamilias = criarBotaoAjuda();
    buttonAjudaCotasFamilias.addActionListener(tratarButtons);

    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    JPanel linhaControles = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaControles.add(new JLabel("Pesquisar por código_fb:"));
    linhaControles.add(fieldFiltroCotasFamilias);
    linhaControles.add(buttonActualizarCotasFamilias);
    linhaControles.add(buttonLimparCotasFamilias);
    linhaControles.add(buttonGerarCotasSemanais);
    linhaControles.add(buttonAjudaCotasFamilias);

    topo.add(label, BorderLayout.NORTH);
    topo.add(linhaControles, BorderLayout.CENTER);

    tabelaCotasFamilias = criarTabelaBase();
    modeloCotasFamilias = new DefaultTableModel();
    tabelaCotasFamilias.setModel(modeloCotasFamilias);
    sorterCotasFamilias = new TableRowSorter<>(modeloCotasFamilias);
    tabelaCotasFamilias.setRowSorter(sorterCotasFamilias);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabelaCotasFamilias), BorderLayout.CENTER);

    return panel;
  }

  private JPanel criarTabRegistosConsumo()
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel label = InterfaceGraficaUtils.criarLabel("Todos os Registos de Consumo com as Respetivas Cotas",
        InterfaceGraficaUtils.FONT_LABEL, InterfaceGraficaUtils.COR_AZUL);

    fieldFiltroRegistosConsumo = InterfaceGraficaUtils.criarCampoTexto(18, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(260, 36));
    fieldFiltroRegistosConsumo.getDocument().addDocumentListener(new TratarPesquisa(OrigemFiltro.REGISTOS_CONSUMO));

    buttonActualizarRegistosConsumo = criarBotao("Mostrar Todos");
    buttonActualizarRegistosConsumo.addActionListener(tratarButtons);

    buttonLimparRegistosConsumo = criarBotao("Limpar Filtro");
    buttonLimparRegistosConsumo.addActionListener(tratarButtons);

    buttonAjudaRegistosConsumo = criarBotaoAjuda();
    buttonAjudaRegistosConsumo.addActionListener(tratarButtons);

    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    JPanel linhaControles = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaControles.add(new JLabel("Pesquisar por código de cota:"));
    linhaControles.add(fieldFiltroRegistosConsumo);
    linhaControles.add(buttonActualizarRegistosConsumo);
    linhaControles.add(buttonLimparRegistosConsumo);
    linhaControles.add(buttonAjudaRegistosConsumo);

    topo.add(label, BorderLayout.NORTH);
    topo.add(linhaControles, BorderLayout.CENTER);

    tabelaRegistosConsumo = criarTabelaBase();
    modeloRegistosConsumo = new DefaultTableModel();
    tabelaRegistosConsumo.setModel(modeloRegistosConsumo);
    sorterRegistosConsumo = new TableRowSorter<>(modeloRegistosConsumo);
    tabelaRegistosConsumo.setRowSorter(sorterRegistosConsumo);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabelaRegistosConsumo), BorderLayout.CENTER);

    return panel;
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
    Resources.IconesInterface.aplicarEfeitoHoverBotaoFamilias(botao);
    return botao;
  }

  private JButton criarBotaoAjuda()
  {
    JButton botao = InterfaceGraficaUtils.criarBotao("?", InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(44, 40));
    IconesInterface.aplicarIconeBotao(botao);
    Resources.IconesInterface.aplicarEfeitoHoverBotaoFamilias(botao);
    botao.setToolTipText("Ajuda da pesquisa");
    return botao;
  }

  // ========================== FIM DO CODIGO DE INTERFACE GRAFICA
  // ==========================

  // ========================== INICIO DA LOGICA DA INTERFACE GRAFICA
  // ==========================

  private void carregarTabelaCotasFamilias()
  {
    carregarTabela(tabelaCotasFamilias, CotaAguaConsultasSQL.VISUALIZAR_FAMILIA_COTA_SEMANAL);
    aplicarFiltroCotasFamilias();
  }

  private void carregarTabelaRegistosConsumo()
  {
    if(!ConsultaRemotaUtils.prepararConsulta(connection, new VerificadorConexaoRemota.NoRemoto[]
      {
          VerificadorConexaoRemota.NoRemoto.DISTRIBUICAO_CONSUMO
      }, tabelaRegistosConsumo, new JComponent[]
        {
            fieldFiltroRegistosConsumo, buttonActualizarRegistosConsumo, buttonLimparRegistosConsumo,
            buttonAjudaRegistosConsumo
        }))
      return;

    carregarTabela(tabelaRegistosConsumo, CotaAguaConsultasSQL.VISUALIZAR_COTA_REGISTROCONSUMO);
    aplicarFiltroRegistosConsumo();
  }

  private void carregarTabela(JTable tabela, String sql)
  {
    try
    {
      DefaultTableModel modelo = CotaAguaConsultasSQL.carregarModeloTabela(connection, sql);
      tabela.setModel(modelo);
      InterfaceGraficaUtils.configurarTabela(tabela, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
          InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);

      if(tabela == tabelaCotasFamilias)
      {
        modeloCotasFamilias = modelo;
        sorterCotasFamilias = new TableRowSorter<>(modeloCotasFamilias);
        tabelaCotasFamilias.setRowSorter(sorterCotasFamilias);
      }
      else if(tabela == tabelaRegistosConsumo)
      {
        modeloRegistosConsumo = modelo;
        sorterRegistosConsumo = new TableRowSorter<>(modeloRegistosConsumo);
        tabelaRegistosConsumo.setRowSorter(sorterRegistosConsumo);
      }

      SwingUtilities.invokeLater(() -> InterfaceGraficaUtils.ajustarLarguraColunas(tabela,
          InterfaceGraficaUtils.FONT_TABELA, InterfaceGraficaUtils.FONT_HEADER));
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar os dados.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltroCotasFamilias()
  {
    if(sorterCotasFamilias == null || fieldFiltroCotasFamilias == null)
      return;

    String texto = fieldFiltroCotasFamilias.getText().trim();

    if(texto.isEmpty())
    {
      sorterCotasFamilias.setRowFilter(null);
      return;
    }

    int coluna = InterfaceGraficaUtils.encontrarIndiceColuna(modeloCotasFamilias, "codigo_fb");
    sorterCotasFamilias.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(texto) + "$", coluna));
  }

  private void aplicarFiltroRegistosConsumo()
  {
    if(sorterRegistosConsumo == null || fieldFiltroRegistosConsumo == null)
      return;

    String texto = fieldFiltroRegistosConsumo.getText().trim();

    if(texto.isEmpty())
    {
      sorterRegistosConsumo.setRowFilter(null);
      return;
    }

    int coluna = InterfaceGraficaUtils.encontrarIndiceColuna(modeloRegistosConsumo, "codigo_cota");
    sorterRegistosConsumo.setRowFilter(RowFilter.regexFilter("^" + Pattern.quote(texto) + "$", coluna));
  }

  private void mostrarAjudaCotasFamilias()
  {
    JOptionPane.showMessageDialog(this,
        "Esta pesquisa filtra pelo codigo_fb.\n\n"
            + "Se escrever o código de uma família, a tabela mostra apenas as cotas de água dessa família.\n"
            + "A pesquisa usa correspondencia exacta: o código escrito deve ser igual ao codigo_fb.",
        "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private void gerarCotasSemanais()
  {
    int opcao = JOptionPane.showConfirmDialog(this,
        "Gerar cotas semanais em falta para as famílias activas?",
        "Gerar Cotas Semanais", JOptionPane.YES_NO_OPTION);

    if(opcao != JOptionPane.YES_OPTION)
      return;

    try
    {
      CotaAguaConsultasSQL.ResultadoGeracao resultado = CotaAguaConsultasSQL.gerarCotasSemanais(connection);
      JOptionPane.showMessageDialog(this,
          "Cotas criadas: " + resultado.cotasCriadas + "\n"
              + "Famílias ignoradas: " + resultado.familiasIgnoradas + "\n"
              + "Erros: " + resultado.erros + "\n\n"
              + MensagensInterface.formatarMensagem(resultado.mensagem),
          "Geracao de Cotas Semanais", JOptionPane.INFORMATION_MESSAGE);
      carregarTabelaCotasFamilias();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível gerar cotas semanais:\n" + MensagensInterface.formatarErro(ex),
          "Erro", JOptionPane.ERROR_MESSAGE);
    }
  }

  private void mostrarAjudaRegistosConsumo()
  {
    JOptionPane.showMessageDialog(this,
        "Esta pesquisa filtra pelo codigo_cota.\n\n"
            + "Se escrever o código de uma cota, a tabela mostra apenas os registos de consumo dessa cota.\n"
            + "A pesquisa usa correspondencia exacta: o código escrito deve ser igual ao codigo_cota.",
        "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private enum OrigemFiltro
  {
    COTAS_FAMILIAS, REGISTOS_CONSUMO
  }

  private class TratarPesquisa implements DocumentListener
  {
    private final OrigemFiltro origem;

    public TratarPesquisa(OrigemFiltro origem)
    {
      this.origem = origem;
    }

    @Override
    public void insertUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }

    @Override
    public void removeUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }

    @Override
    public void changedUpdate(DocumentEvent e)
    {
      aplicarFiltro();
    }

    private void aplicarFiltro()
    {
      switch(origem)
      {
        case COTAS_FAMILIAS:
          aplicarFiltroCotasFamilias();
          break;
        case REGISTOS_CONSUMO:
          aplicarFiltroRegistosConsumo();
          break;
      }
    }
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      Object source = e.getSource();

      if(source == buttonActualizarCotasFamilias)
      {
        carregarTabelaCotasFamilias();
        return;
      }

      if(source == buttonLimparCotasFamilias)
      {
        fieldFiltroCotasFamilias.setText("");
        aplicarFiltroCotasFamilias();
        return;
      }

      if(source == buttonGerarCotasSemanais)
      {
        gerarCotasSemanais();
        return;
      }

      if(source == buttonAjudaCotasFamilias)
      {
        mostrarAjudaCotasFamilias();
        return;
      }

      if(source == buttonActualizarRegistosConsumo)
      {
        carregarTabelaRegistosConsumo();
        return;
      }

      if(source == buttonLimparRegistosConsumo)
      {
        fieldFiltroRegistosConsumo.setText("");
        aplicarFiltroRegistosConsumo();
        return;
      }

      if(source == buttonAjudaRegistosConsumo)
      { mostrarAjudaRegistosConsumo(); }
    }
  }

  // =========================== FIM DA LOGICA DA INTERFACE GRAFICA
  // ===========================
}
