package View_Interface_EquipesGestaoDB;

import Repository_SQL.EquipesGestaoDB.AlertasQualidadeAguaSQL;
import Repository_SQL.EquipesGestaoDB.AlertasQualidadeAguaSQL.AlertaQualidade;
import Repository_SQL.EquipesGestaoDB.AlertasQualidadeAguaSQL.ResultadoRefresh;
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

public class JPanel_AlertasQualidadeAgua extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();
  private final SimpleDateFormat formatoDataHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private JTextField fieldPesquisa;
  private JTable tabelaAlertas;
  private JLabel labelResumo;
  private JTextArea areaMensagem;
  private JButton buttonActualizarAlertas;
  private JButton buttonPesquisar;
  private JButton buttonMostrarTodos;
  private JButton buttonLimpar;

  public JPanel_AlertasQualidadeAgua(Connection connection)
  {
    this(connection, false);
  }

  public JPanel_AlertasQualidadeAgua(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this.connection = connection;
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(22, 22, 22, 22));

    add(criarConteudo(), BorderLayout.CENTER);
    if(carregarTabelasAutomaticamente)
      carregarAlertas(null);
    else
      areaMensagem.setText("Pesquise ou clique em Mostrar Todos.");
  }

  private JPanel criarConteudo()
  {
    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(28, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(26, 26, 26, 26), new Dimension(1320, 780));

    card.add(criarTopo(), BorderLayout.NORTH);
    card.add(criarCentro(), BorderLayout.CENTER);
    card.add(criarRodape(), BorderLayout.SOUTH);
    return card;
  }

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Alertas de Qualidade da Água",
        "<html>Consulta dos alertas de qualidade sincronizados do TransferenciasRecursosDB.</html>",
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
    JPanel grupoPesquisa = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 5));

    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(35, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(360, 36));
    grupoPesquisa.add(InterfaceGraficaUtils.criarLabel("Pesquisa:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO), BorderLayout.NORTH);
    grupoPesquisa.add(fieldPesquisa, BorderLayout.CENTER);

    filtros.add(grupoPesquisa);
    return filtros;
  }

  private JPanel criarTabela()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    labelResumo = InterfaceGraficaUtils.criarLabel("Alertas encontrados: 0", InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_TEXTO);

    tabelaAlertas = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaAlertas.setModel(criarModeloVazio());

    painel.add(labelResumo, BorderLayout.NORTH);
    painel.add(InterfaceGraficaUtils.criarScrollTabela(tabelaAlertas, InterfaceGraficaUtils.COR_BORDA_TABELA),
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
    areaMensagem.setText("Pesquise ou clique em Mostrar Todos.");

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonActualizarAlertas = criarBotao("Actualizar Alertas", 180);
    buttonPesquisar = criarBotao("Pesquisar", 125);
    buttonMostrarTodos = criarBotao("Mostrar Todos", 145);
    buttonLimpar = criarBotao("Limpar", 110);

    botoes.add(buttonActualizarAlertas);
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
    return new DefaultTableModel(new Object[] { "Código Alerta", "Código Medição", "Código RH", "Mensagem",
        "Data Alerta" }, 0)
      {
        public boolean isCellEditable(int row, int column)
        {
          return false;
        }
      };
  }

  private void carregarAlertas(String pesquisa)
  {
    try
    {
      List<AlertaQualidade> alertas = AlertasQualidadeAguaSQL.pesquisar(connection, pesquisa);
      DefaultTableModel model = criarModeloVazio();
      for(AlertaQualidade alerta : alertas)
        model.addRow(new Object[] { alerta.codigoAlerta, alerta.codigoMedicao, alerta.codigoRh,
            alerta.mensagemAlerta, formatarData(alerta.dataAlerta) });

      tabelaAlertas.setModel(model);
      InterfaceGraficaUtils.ajustarLarguraColunas(tabelaAlertas, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER);
      labelResumo.setText("Alertas encontrados: " + alertas.size());
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      areaMensagem.setText("Não foi possível carregar alertas de qualidade: " + MensagensInterface.formatarErro(ex));
    }
  }

  private void actualizarAlertas()
  {
    try
    {
      ResultadoRefresh resultado = AlertasQualidadeAguaSQL.actualizarAlertas(connection);
      areaMensagem.setText(resultado.mensagem == null ? "" : resultado.mensagem);
      carregarAlertas(fieldPesquisa.getText());
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      areaMensagem.setText("Não foi possível actualizar alertas: " + MensagensInterface.formatarErro(ex));
    }
  }

  private void mostrarTodos()
  {
    fieldPesquisa.setText("");
    carregarAlertas(null);
  }

  private void limparCampos()
  {
    fieldPesquisa.setText("");
    areaMensagem.setText("");
  }

  private String formatarData(java.util.Date data)
  {
    if(data == null)
      return "";
    return formatoDataHora.format(data);
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == buttonActualizarAlertas)
        actualizarAlertas();
      else if(event.getSource() == buttonPesquisar)
        carregarAlertas(fieldPesquisa.getText());
      else if(event.getSource() == buttonMostrarTodos)
        mostrarTodos();
      else if(event.getSource() == buttonLimpar)
        limparCampos();
    }
  }
}
