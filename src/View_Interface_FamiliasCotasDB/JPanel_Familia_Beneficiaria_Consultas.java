package View_Interface_FamiliasCotasDB;

import Resources.MensagensInterface;

import Repository_SQL.FamiliasCotasDB.FamiliaBeneficiariaConsultasSQL;
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

public class JPanel_Familia_Beneficiaria_Consultas extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  // TAB 1 - Localizações
  private JTable tabelaLocalizacoes;
  private DefaultTableModel modeloLocalizacoes;
  private TableRowSorter<DefaultTableModel> sorterLocalizacoes;
  private JTextField fieldFiltroLocalizacoes;
  private JComboBox<String> comboFiltroLocalizacoes;
  private JButton buttonActualizarLocalizacoes;
  private JButton buttonLimparLocalizacoes;
  private JButton buttonAjudaLocalizacoes;

  // TAB 2 - Transferências
  private JTable tabelaTransferencias;
  private DefaultTableModel modeloTransferencias;
  private TableRowSorter<DefaultTableModel> sorterTransferencias;
  private JTextField fieldFiltroTransferencias;
  private JComboBox<String> comboFiltroTransferencias;
  private JButton buttonActualizarTransferencias;
  private JButton buttonLimparTransferencias;
  private JButton buttonAjudaTransferencias;

  // TAB 3 - Cotas
  private JTable tabelaCotas;
  private DefaultTableModel modeloCotas;
  private TableRowSorter<DefaultTableModel> sorterCotas;
  private JTextField fieldFiltroCotas;
  private JComboBox<String> comboFiltroCotas;
  private JButton buttonActualizarCotas;
  private JButton buttonLimparCotas;
  private JButton buttonAjudaCotas;

  // TAB 4 - Necessidades
  private JTable tabelaNecessidades;
  private DefaultTableModel modeloNecessidades;
  private TableRowSorter<DefaultTableModel> sorterNecessidades;
  private JTextField fieldFiltroNecessidades;
  private JComboBox<String> comboFiltroNecessidades;
  private JButton buttonActualizarNecessidades;
  private JButton buttonLimparNecessidades;
  private JButton buttonAjudaNecessidades;

  public JPanel_Familia_Beneficiaria_Consultas(Connection connection)
  {
    this(connection, false);
  }

  public JPanel_Familia_Beneficiaria_Consultas(Connection connection, boolean carregarTabelasAutomaticamente)
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
      carregarTabelaLocalizacoes();
      carregarTabelaTransferencias();
      carregarTabelaCotas();
      carregarTabelaNecessidades();
    }
  }

  // ========================= INICIO DO CODIGO DE INTERFACE GRAFICA
  // =========================

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Pesquisas sobre Família Beneficiária",
        "<html>As pesquisas foram agrupadas em quatro separadores: localizações, transferências, cotas de água e necessidades. "
            + "Em todas as abas pode pesquisar por <b>codigo_fb</b>, <b>nome_responsavel_fb</b> ou <b>contacto_fb</b>.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JComponent criarCentro()
  {
    JTabbedPane tabs = new JTabbedPane();
    tabs.setFont(InterfaceGraficaUtils.FONT_ABA);

    tabs.addTab("Localizações", criarTabLocalizacoes());
    tabs.addTab("Transferências", criarTabTransferencias());
    tabs.addTab("Cotas de Água", criarTabCotas());
    tabs.addTab("Necessidades", criarTabNecessidades());

    return tabs;
  }

  private JPanel criarTabLocalizacoes()
  {
    fieldFiltroLocalizacoes = criarCampoFiltro(OrigemFiltro.LOCALIZACOES);
    comboFiltroLocalizacoes = criarComboFiltro();
    comboFiltroLocalizacoes.addActionListener(tratarButtons);
    buttonActualizarLocalizacoes = criarBotao("Mostrar Todos");
    buttonActualizarLocalizacoes.addActionListener(tratarButtons);
    buttonLimparLocalizacoes = criarBotao("Limpar Filtro");
    buttonLimparLocalizacoes.addActionListener(tratarButtons);
    buttonAjudaLocalizacoes = criarBotaoAjuda();
    buttonAjudaLocalizacoes.addActionListener(tratarButtons);

    tabelaLocalizacoes = criarTabelaBase();
    modeloLocalizacoes = new DefaultTableModel();
    tabelaLocalizacoes.setModel(modeloLocalizacoes);
    sorterLocalizacoes = new TableRowSorter<>(modeloLocalizacoes);
    tabelaLocalizacoes.setRowSorter(sorterLocalizacoes);

    return criarTabTabela("Localização de Todas as Famílias", comboFiltroLocalizacoes, fieldFiltroLocalizacoes,
        buttonActualizarLocalizacoes, buttonLimparLocalizacoes, buttonAjudaLocalizacoes, tabelaLocalizacoes);
  }

  private JPanel criarTabTransferencias()
  {
    fieldFiltroTransferencias = criarCampoFiltro(OrigemFiltro.TRANSFERENCIAS);
    comboFiltroTransferencias = criarComboFiltro();
    comboFiltroTransferencias.addActionListener(tratarButtons);
    buttonActualizarTransferencias = criarBotao("Mostrar Todos");
    buttonActualizarTransferencias.addActionListener(tratarButtons);
    buttonLimparTransferencias = criarBotao("Limpar Filtro");
    buttonLimparTransferencias.addActionListener(tratarButtons);
    buttonAjudaTransferencias = criarBotaoAjuda();
    buttonAjudaTransferencias.addActionListener(tratarButtons);

    tabelaTransferencias = criarTabelaBase();
    modeloTransferencias = new DefaultTableModel();
    tabelaTransferencias.setModel(modeloTransferencias);
    sorterTransferencias = new TableRowSorter<>(modeloTransferencias);
    tabelaTransferencias.setRowSorter(sorterTransferencias);

    return criarTabTabela("Transferências Associadas às Famílias", comboFiltroTransferencias, fieldFiltroTransferencias,
        buttonActualizarTransferencias, buttonLimparTransferencias, buttonAjudaTransferencias, tabelaTransferencias);
  }

  private JPanel criarTabCotas()
  {
    fieldFiltroCotas = criarCampoFiltro(OrigemFiltro.COTAS);
    comboFiltroCotas = criarComboFiltro();
    comboFiltroCotas.addActionListener(tratarButtons);
    buttonActualizarCotas = criarBotao("Mostrar Todos");
    buttonActualizarCotas.addActionListener(tratarButtons);
    buttonLimparCotas = criarBotao("Limpar Filtro");
    buttonLimparCotas.addActionListener(tratarButtons);
    buttonAjudaCotas = criarBotaoAjuda();
    buttonAjudaCotas.addActionListener(tratarButtons);

    tabelaCotas = criarTabelaBase();
    modeloCotas = new DefaultTableModel();
    tabelaCotas.setModel(modeloCotas);
    sorterCotas = new TableRowSorter<>(modeloCotas);
    tabelaCotas.setRowSorter(sorterCotas);

    return criarTabTabela("Cotas de Água de Todas as Famílias", comboFiltroCotas, fieldFiltroCotas, buttonActualizarCotas,
        buttonLimparCotas, buttonAjudaCotas, tabelaCotas);
  }

  private JPanel criarTabNecessidades()
  {
    fieldFiltroNecessidades = criarCampoFiltro(OrigemFiltro.NECESSIDADES);
    comboFiltroNecessidades = criarComboFiltro();
    comboFiltroNecessidades.addActionListener(tratarButtons);
    buttonActualizarNecessidades = criarBotao("Mostrar Todos");
    buttonActualizarNecessidades.addActionListener(tratarButtons);
    buttonLimparNecessidades = criarBotao("Limpar Filtro");
    buttonLimparNecessidades.addActionListener(tratarButtons);
    buttonAjudaNecessidades = criarBotaoAjuda();
    buttonAjudaNecessidades.addActionListener(tratarButtons);

    tabelaNecessidades = criarTabelaBase();
    modeloNecessidades = new DefaultTableModel();
    tabelaNecessidades.setModel(modeloNecessidades);
    sorterNecessidades = new TableRowSorter<>(modeloNecessidades);
    tabelaNecessidades.setRowSorter(sorterNecessidades);

    return criarTabTabela("Necessidades de Todas as Famílias", comboFiltroNecessidades, fieldFiltroNecessidades,
        buttonActualizarNecessidades, buttonLimparNecessidades, buttonAjudaNecessidades, tabelaNecessidades);
  }

  private JPanel criarTabTabela(String titulo, JComboBox<String> comboFiltro, JTextField fieldFiltro,
      JButton buttonActualizar, JButton buttonLimpar, JButton buttonAjuda, JTable tabela)
  {
    JPanel panel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));
    JLabel label = InterfaceGraficaUtils.criarLabel(titulo, InterfaceGraficaUtils.FONT_LABEL,
        InterfaceGraficaUtils.COR_AZUL);

    JPanel topo = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    JPanel linhaControles = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    linhaControles.add(new JLabel("Pesquisar por:"));
    linhaControles.add(comboFiltro);
    linhaControles.add(fieldFiltro);
    linhaControles.add(buttonActualizar);
    linhaControles.add(buttonLimpar);
    linhaControles.add(buttonAjuda);

    topo.add(label, BorderLayout.NORTH);
    topo.add(linhaControles, BorderLayout.CENTER);

    panel.add(topo, BorderLayout.NORTH);
    panel.add(criarScrollTabela(tabela), BorderLayout.CENTER);

    return panel;
  }

  private JTextField criarCampoFiltro(OrigemFiltro origemFiltro)
  {
    JTextField fieldFiltro = InterfaceGraficaUtils.criarCampoTexto(18, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(260, 36));
    fieldFiltro.getDocument().addDocumentListener(new TratarPesquisa(origemFiltro));
    return fieldFiltro;
  }

  private JComboBox<String> criarComboFiltro()
  {
    return InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código da Família", "Nome do Responsável", "Telefone"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(220, 36));
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

  private void carregarTabelaLocalizacoes()
  {
    carregarTabela(tabelaLocalizacoes, FamiliaBeneficiariaConsultasSQL.VISUALIZAR_LOCALIZACAO_TODAS);
    aplicarFiltroLocalizacoes();
  }

  private void carregarTabelaTransferencias()
  {
    if(!ConsultaRemotaUtils.prepararConsulta(connection, new VerificadorConexaoRemota.NoRemoto[]
      {
          VerificadorConexaoRemota.NoRemoto.TRANSFERENCIAS_RECURSOS
      }, tabelaTransferencias, new JComponent[]
        {
            comboFiltroTransferencias, fieldFiltroTransferencias, buttonActualizarTransferencias,
            buttonLimparTransferencias, buttonAjudaTransferencias
        }))
      return;

    carregarTabela(tabelaTransferencias, FamiliaBeneficiariaConsultasSQL.VISUALIZAR_TRANSFERENCIAS_TODAS);
    aplicarFiltroTransferencias();
  }

  private void carregarTabelaCotas()
  {
    carregarTabela(tabelaCotas, FamiliaBeneficiariaConsultasSQL.VISUALIZAR_COTAS_TODAS);
    aplicarFiltroCotas();
  }

  private void carregarTabelaNecessidades()
  {
    carregarTabela(tabelaNecessidades, FamiliaBeneficiariaConsultasSQL.VISUALIZAR_NECESSIDADES_TODAS);
    aplicarFiltroNecessidades();
  }

  private void carregarTabela(JTable tabela, String sql)
  {
    try
    {
      DefaultTableModel modelo = FamiliaBeneficiariaConsultasSQL.carregarModeloTabela(connection, sql);
      tabela.setModel(modelo);
      InterfaceGraficaUtils.configurarTabela(tabela, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
          InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);

      if(tabela == tabelaLocalizacoes)
      {
        modeloLocalizacoes = modelo;
        sorterLocalizacoes = new TableRowSorter<>(modeloLocalizacoes);
        tabelaLocalizacoes.setRowSorter(sorterLocalizacoes);
      }
      else if(tabela == tabelaTransferencias)
      {
        modeloTransferencias = modelo;
        sorterTransferencias = new TableRowSorter<>(modeloTransferencias);
        tabelaTransferencias.setRowSorter(sorterTransferencias);
      }
      else if(tabela == tabelaCotas)
      {
        modeloCotas = modelo;
        sorterCotas = new TableRowSorter<>(modeloCotas);
        tabelaCotas.setRowSorter(sorterCotas);
      }
      else if(tabela == tabelaNecessidades)
      {
        modeloNecessidades = modelo;
        sorterNecessidades = new TableRowSorter<>(modeloNecessidades);
        tabelaNecessidades.setRowSorter(sorterNecessidades);
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

  private void aplicarFiltroLocalizacoes()
  {
    aplicarFiltroFamilia(sorterLocalizacoes, fieldFiltroLocalizacoes, comboFiltroLocalizacoes, modeloLocalizacoes);
  }

  private void aplicarFiltroTransferencias()
  {
    aplicarFiltroFamilia(sorterTransferencias, fieldFiltroTransferencias, comboFiltroTransferencias,
        modeloTransferencias);
  }

  private void aplicarFiltroCotas()
  {
    aplicarFiltroFamilia(sorterCotas, fieldFiltroCotas, comboFiltroCotas, modeloCotas);
  }

  private void aplicarFiltroNecessidades()
  {
    aplicarFiltroFamilia(sorterNecessidades, fieldFiltroNecessidades, comboFiltroNecessidades, modeloNecessidades);
  }

  private void aplicarFiltroFamilia(TableRowSorter<DefaultTableModel> sorter, JTextField fieldFiltro,
      JComboBox<String> comboFiltro, DefaultTableModel modelo)
  {
    if(sorter == null || fieldFiltro == null || comboFiltro == null || modelo == null)
      return;

    String texto = fieldFiltro.getText().trim();

    if(texto.isEmpty())
    {
      sorter.setRowFilter(null);
      return;
    }

    String criterio = comboFiltro.getSelectedItem().toString();
    int coluna = getIndiceColunaFiltro(modelo, criterio);
    String expressao = "Código da Família".equals(criterio) ? "^" + Pattern.quote(texto) + "$"
        : "(?i)" + Pattern.quote(texto);
    sorter.setRowFilter(RowFilter.regexFilter(expressao, coluna));
  }

  private int getIndiceColunaFiltro(DefaultTableModel modelo, String criterio)
  {
    if("Nome do Responsável".equals(criterio))
      return InterfaceGraficaUtils.encontrarIndiceColuna(modelo, "nome_responsavel_fb");

    if("Telefone".equals(criterio))
      return InterfaceGraficaUtils.encontrarIndiceColuna(modelo, "contacto_fb");

    return InterfaceGraficaUtils.encontrarIndiceColuna(modelo, "codigo_fb");
  }

  private void mostrarAjudaLocalizacoes()
  {
    mostrarAjudaPesquisa("Localizações", "a localização associada à família", comboFiltroLocalizacoes);
  }

  private void mostrarAjudaTransferencias()
  {
    mostrarAjudaPesquisa("Transferências", "as transferências associadas à família", comboFiltroTransferencias);
  }

  private void mostrarAjudaCotas()
  {
    mostrarAjudaPesquisa("Cotas de Água", "as cotas de água da família", comboFiltroCotas);
  }

  private void mostrarAjudaNecessidades()
  {
    mostrarAjudaPesquisa("Necessidades", "as necessidades calculadas para a família", comboFiltroNecessidades);
  }

  private void mostrarAjudaPesquisa(String titulo, String resultado, JComboBox<String> comboFiltro)
  {
    String criterio = comboFiltro.getSelectedItem().toString();
    if("Nome do Responsável".equals(criterio))
    {
      JOptionPane.showMessageDialog(this,
          titulo + "\n\nEsta pesquisa filtra por nome_responsavel_fb.\n\n"
              + "Se escrever parte do nome do responsável, a tabela mostra " + resultado + ".\n"
              + "A pesquisa não exige correspondência exacta.",
          "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    if("Telefone".equals(criterio))
    {
      JOptionPane.showMessageDialog(this,
          titulo + "\n\nEsta pesquisa filtra por contacto_fb.\n\n"
              + "Se escrever parte do telefone, a tabela mostra " + resultado + ".\n"
              + "A pesquisa não exige correspondência exacta.",
          "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
      return;
    }

    JOptionPane.showMessageDialog(this,
        titulo + "\n\nEsta pesquisa filtra pelo codigo_fb.\n\n"
            + "Se escrever o código de uma família, a tabela mostra " + resultado + ".\n"
            + "A pesquisa usa correspondencia exacta.",
        "Ajuda da Pesquisa", JOptionPane.INFORMATION_MESSAGE);
  }

  private enum OrigemFiltro
  {
    LOCALIZACOES, TRANSFERENCIAS, COTAS, NECESSIDADES
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
        case LOCALIZACOES:
          aplicarFiltroLocalizacoes();
          break;
        case TRANSFERENCIAS:
          aplicarFiltroTransferencias();
          break;
        case COTAS:
          aplicarFiltroCotas();
          break;
        case NECESSIDADES:
          aplicarFiltroNecessidades();
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

      if(source == comboFiltroLocalizacoes)
      {
        aplicarFiltroLocalizacoes();
        return;
      }

      if(source == buttonActualizarLocalizacoes)
      {
        carregarTabelaLocalizacoes();
        return;
      }

      if(source == buttonLimparLocalizacoes)
      {
        fieldFiltroLocalizacoes.setText("");
        aplicarFiltroLocalizacoes();
        return;
      }

      if(source == buttonAjudaLocalizacoes)
      {
        mostrarAjudaLocalizacoes();
        return;
      }

      if(source == comboFiltroTransferencias)
      {
        aplicarFiltroTransferencias();
        return;
      }

      if(source == buttonActualizarTransferencias)
      {
        carregarTabelaTransferencias();
        return;
      }

      if(source == buttonLimparTransferencias)
      {
        fieldFiltroTransferencias.setText("");
        aplicarFiltroTransferencias();
        return;
      }

      if(source == buttonAjudaTransferencias)
      {
        mostrarAjudaTransferencias();
        return;
      }

      if(source == comboFiltroCotas)
      {
        aplicarFiltroCotas();
        return;
      }

      if(source == buttonActualizarCotas)
      {
        carregarTabelaCotas();
        return;
      }

      if(source == buttonLimparCotas)
      {
        fieldFiltroCotas.setText("");
        aplicarFiltroCotas();
        return;
      }

      if(source == buttonAjudaCotas)
      {
        mostrarAjudaCotas();
        return;
      }

      if(source == comboFiltroNecessidades)
      {
        aplicarFiltroNecessidades();
        return;
      }

      if(source == buttonActualizarNecessidades)
      {
        carregarTabelaNecessidades();
        return;
      }

      if(source == buttonLimparNecessidades)
      {
        fieldFiltroNecessidades.setText("");
        aplicarFiltroNecessidades();
        return;
      }

      if(source == buttonAjudaNecessidades)
      { mostrarAjudaNecessidades(); }
    }
  }

  // =========================== FIM DA LOGICA DA INTERFACE GRAFICA
  // ===========================
}
