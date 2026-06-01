package View_Interface_EquipesGestaoDB;

import Repository_SQL.EquipesGestaoDB.ComiteConsultasSQL;
import Repository_SQL.EquipesGestaoDB.ComiteConsultasSQL.Comite;
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
import java.text.SimpleDateFormat;
import java.util.List;

public class JPanel_Comite_Consultas extends JPanel
{
  private static final String[] CRITERIOS = { "Código", "Nome", "Data de criação" };

  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();
  private final SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");

  private JComboBox<String> comboCriterio;
  private JTextField fieldPesquisa;
  private JTable tabelaComites;
  private JLabel labelResumo;
  private JTextArea areaMensagem;
  private JButton buttonPesquisar;
  private JButton buttonMostrarTodos;
  private JButton buttonLimpar;

  public JPanel_Comite_Consultas(Connection connection)
  {
    this(connection, false);
  }

  public JPanel_Comite_Consultas(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this.connection = connection;

    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(22, 22, 22, 22));

    add(criarConteudo(), BorderLayout.CENTER);
    if(carregarTabelasAutomaticamente)
      mostrarTodos();
    else
      areaMensagem.setText("Pesquise ou clique em Mostrar Todos.");
  }

  private JPanel criarConteudo()
  {
    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(28, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(26, 26, 26, 26), new Dimension(1120, 720));

    card.add(criarTopo(), BorderLayout.NORTH);
    card.add(criarCentro(), BorderLayout.CENTER);
    card.add(criarRodape(), BorderLayout.SOUTH);
    return card;
  }

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Consulta de Comités Locais",
        "<html>Visualização dos comités registados no EquipesGestaoDB.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarCentro()
  {
    JPanel centro = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 14));
    centro.add(criarFiltros(), BorderLayout.NORTH);
    centro.add(criarTabela(), BorderLayout.CENTER);
    return centro;
  }

  private JPanel criarFiltros()
  {
    JPanel filtros = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 12, 0));

    JPanel grupoCriterio = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 5));
    comboCriterio = InterfaceGraficaUtils.criarCombo(CRITERIOS, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(190, 36));
    grupoCriterio.add(InterfaceGraficaUtils.criarLabel("Critério:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO), BorderLayout.NORTH);
    grupoCriterio.add(comboCriterio, BorderLayout.CENTER);

    JPanel grupoPesquisa = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 5));
    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(32, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(360, 36));
    grupoPesquisa.add(InterfaceGraficaUtils.criarLabel("Pesquisa:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO), BorderLayout.NORTH);
    grupoPesquisa.add(fieldPesquisa, BorderLayout.CENTER);

    filtros.add(grupoCriterio);
    filtros.add(grupoPesquisa);
    return filtros;
  }

  private JPanel criarTabela()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    labelResumo = InterfaceGraficaUtils.criarLabel("Comités encontrados: 0", InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_TEXTO);

    tabelaComites = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaComites.setModel(criarModeloVazio());

    painel.add(labelResumo, BorderLayout.NORTH);
    painel.add(InterfaceGraficaUtils.criarScrollTabela(tabelaComites, InterfaceGraficaUtils.COR_BORDA_TABELA),
        BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarRodape()
  {
    JPanel rodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(12, 0));
    areaMensagem = new JTextArea(2, 45);
    areaMensagem.setEditable(false);
    areaMensagem.setLineWrap(true);
    areaMensagem.setWrapStyleWord(true);
    areaMensagem.setFont(InterfaceGraficaUtils.FONT_AJUDA);
    areaMensagem.setText("Consulta apenas. Esta tela não regista, actualiza ou remove comités.");

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonPesquisar = criarBotao("Pesquisar", 125);
    buttonMostrarTodos = criarBotao("Mostrar Todos", 145);
    buttonLimpar = criarBotao("Limpar", 110);

    botoes.add(buttonPesquisar);
    botoes.add(buttonMostrarTodos);
    botoes.add(buttonLimpar);

    rodape.add(new JScrollPane(areaMensagem), BorderLayout.CENTER);
    rodape.add(botoes, BorderLayout.EAST);
    return rodape;
  }

  private JButton criarBotao(String texto, int largura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, 40));
    IconesInterface.aplicarIconeBotao(botao);
    botao.addActionListener(tratarButtons);
    return botao;
  }

  private DefaultTableModel criarModeloVazio()
  {
    return new DefaultTableModel(new Object[] { "cod_comite_responsavel", "nome_comite", "data_criacao" }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
  }

  private void carregarComites(String criterio, String pesquisa)
  {
    try
    {
      List<Comite> comites = ComiteConsultasSQL.pesquisar(connection, criterio, pesquisa);
      DefaultTableModel model = criarModeloVazio();
      for(Comite comite : comites)
        model.addRow(new Object[] { Integer.valueOf(comite.codComiteResponsavel), comite.nomeComite,
            formatarData(comite.dataCriacao) });

      tabelaComites.setModel(model);
      InterfaceGraficaUtils.ajustarLarguraColunas(tabelaComites, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER);
      labelResumo.setText("Comités encontrados: " + comites.size());
      areaMensagem.setText("");
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      areaMensagem.setText("Não foi possível consultar comités: " + MensagensInterface.formatarErro(ex));
    }
  }

  private void pesquisar()
  {
    carregarComites((String) comboCriterio.getSelectedItem(), fieldPesquisa.getText());
  }

  private void mostrarTodos()
  {
    if(fieldPesquisa != null)
      fieldPesquisa.setText("");

    carregarComites(null, null);
  }

  private void limparCampos()
  {
    fieldPesquisa.setText("");
    comboCriterio.setSelectedIndex(0);
    tabelaComites.setModel(criarModeloVazio());
    labelResumo.setText("Comités encontrados: 0");
    areaMensagem.setText("");
  }

  private String formatarData(java.util.Date data)
  {
    if(data == null)
      return "";
    return formatoData.format(data);
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == buttonPesquisar)
        pesquisar();
      else if(event.getSource() == buttonMostrarTodos)
        mostrarTodos();
      else if(event.getSource() == buttonLimpar)
        limparCampos();
    }
  }
}
