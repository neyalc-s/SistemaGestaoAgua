package View_Interface_FamiliasCotasDB;

import Resources.MensagensInterface;

import Repository_SQL.FamiliasCotasDB.CotaAguaConsultasSQL;
import Repository_SQL.FamiliasCotasDB.ActualizarAjusteSazonalSQL;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JPanel_Actualizar_AjusteSazonal extends JPanel
{
  private static final String CARD_SELECIONAR_COTAS = "selecionar_cotas";
  private static final String CARD_ACTUALIZAR_AJUSTE = "actualizar_ajuste";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final TratarButtons tratarButtons = new TratarButtons();

  private CardLayout cardLayout;
  private JPanel panelCards;

  private JTable tabelaCotas;
  private DefaultTableModel modeloCotas;
  private TableRowSorter<DefaultTableModel> sorterCotas;

  private JTextField fieldPesquisa;
  private JComboBox<String> comboCriterioPesquisa;
  private JButton buttonActualizarTabela;
  private JButton buttonLimparFiltro;
  private JButton buttonAvancar;
  private JButton buttonVoltarDashboard;

  private JTextField fieldFamiliasSelecionadas;
  private JTextField fieldCotasSelecionadas;
  private JComboBox<String> comboNovoAjusteSazonal;
  private JButton buttonVoltarParaCotas;
  private JButton buttonActualizarAjuste;

  public JPanel_Actualizar_AjusteSazonal(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_Actualizar_AjusteSazonal(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_Actualizar_AjusteSazonal(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_Actualizar_AjusteSazonal(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    cardLayout = new CardLayout();
    panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
    panelCards.add(criarCardSelecionarCotas(), CARD_SELECIONAR_COTAS);
    panelCards.add(criarCardActualizarAjuste(), CARD_ACTUALIZAR_AJUSTE);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(18, 18, 18, 18);

    add(panelCards, gbc);

    if(carregarTabelasAutomaticamente)
    {
      carregarCotas();
    }
  }

  private JPanel criarCardSelecionarCotas()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1250, 760));

    card.add(criarTopoSelecao(), BorderLayout.NORTH);
    card.add(criarCentroSelecao(), BorderLayout.CENTER);
    card.add(criarRodapeSelecao(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarCardActualizarAjuste()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(1180, 720));

    card.add(criarTopoActualizacao(), BorderLayout.NORTH);
    card.add(criarCentroActualizacao(), BorderLayout.CENTER);
    card.add(criarRodapeActualizacao(), BorderLayout.SOUTH);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    painelExterno.add(card, gbc);

    return painelExterno;
  }

  private JPanel criarTopoSelecao()
  {
    return InterfaceGraficaUtils.criarTopo("Actualizar Ajuste Sazonal",
        "<html>Seleccione uma ou várias cotas válidas. Cotas expiradas aparecem na tabela, mas não podem avançar.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarCentroSelecao()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel labelTabela = InterfaceGraficaUtils.criarLabel("Cotas de Água com as Respectivas Famílias",
        InterfaceGraficaUtils.FONT_LABEL_SECAO, InterfaceGraficaUtils.COR_AZUL);

    JPanel topoTabela = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 10));
    topoTabela.add(labelTabela, BorderLayout.NORTH);
    topoTabela.add(criarPainelPesquisa(), BorderLayout.SOUTH);

    painel.add(topoTabela, BorderLayout.NORTH);
    painel.add(criarScrollTabelaCotas(), BorderLayout.CENTER);

    return painel;
  }

  private JPanel criarRodapeSelecao()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("Use Ctrl ou Shift para selecionar várias cotas.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarDashboard = criarBotao("Voltar");
    buttonAvancar = criarBotao("Proceder");
    buttonVoltarDashboard.addActionListener(tratarButtons);
    buttonAvancar.addActionListener(tratarButtons);
    painelBotoes.add(buttonVoltarDashboard);
    painelBotoes.add(buttonAvancar);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarTopoActualizacao()
  {
    return InterfaceGraficaUtils.criarTopo("Novo Ajuste Sazonal",
        "<html>Confirme as cotas seleccionadas e escolha o novo ajuste sazonal.</html>", InterfaceGraficaUtils.FONT_TITULO,
        InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO, InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarCentroActualizacao()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldFamiliasSelecionadas = new JTextField(20);
    fieldFamiliasSelecionadas.setEditable(false);
    fieldCotasSelecionadas = new JTextField(20);
    fieldCotasSelecionadas.setEditable(false);

    comboNovoAjusteSazonal = new JComboBox<>(new String[]
      {
          "Verão", "Inverno", "Chuvoso", "Seco", "Transição Seca", "Pico Seco", "Início Chuvoso", "Alta Demanda",
          "Fim de Ano", "Normal"
      });

    estilizarCampo(fieldFamiliasSelecionadas);
    estilizarCampo(fieldCotasSelecionadas);
    estilizarCombo(comboNovoAjusteSazonal);

    int linha = 0;
    adicionarLinhaFormulario(painel, linha++, "Famílias seleccionadas:", fieldFamiliasSelecionadas);
    adicionarLinhaFormulario(painel, linha++, "Cotas seleccionadas:", fieldCotasSelecionadas);
    adicionarLinhaFormulario(painel, linha++, "Novo ajuste sazonal:", comboNovoAjusteSazonal);

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = linha;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);

    return painel;
  }

  private JPanel criarRodapeActualizacao()
  {
    JPanel painelRodape = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    JLabel labelAjuda = InterfaceGraficaUtils.criarLabel("As cotas seleccionadas serão pré-validadas antes da actualização.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel painelBotoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonVoltarParaCotas = criarBotao("Voltar");
    buttonActualizarAjuste = criarBotao("Actualizar");
    buttonVoltarParaCotas.addActionListener(tratarButtons);
    buttonActualizarAjuste.addActionListener(tratarButtons);

    painelBotoes.add(buttonVoltarParaCotas);
    painelBotoes.add(buttonActualizarAjuste);

    painelRodape.add(labelAjuda, BorderLayout.WEST);
    painelRodape.add(painelBotoes, BorderLayout.EAST);

    return painelRodape;
  }

  private JPanel criarPainelPesquisa()
  {
    JPanel painelPesquisa = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));

    JLabel labelPesquisar = InterfaceGraficaUtils.criarLabel("Pesquisar:", InterfaceGraficaUtils.FONT_LABEL_PESQUISA,
        InterfaceGraficaUtils.COR_TEXTO);

    fieldPesquisa = InterfaceGraficaUtils.criarCampoTexto(22, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(250, 36));
    comboCriterioPesquisa = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Código Família", "Nome Família", "Contacto", "Código Cota", "Ajuste Sazonal", "Status Validade"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(190, 36));

    buttonActualizarTabela = criarBotao("Mostrar Todos");
    buttonActualizarTabela.setPreferredSize(new Dimension(145, 36));
    buttonLimparFiltro = criarBotao("Limpar");
    buttonLimparFiltro.setPreferredSize(new Dimension(115, 36));

    fieldPesquisa.getDocument().addDocumentListener(new TratarPesquisa());
    comboCriterioPesquisa.addActionListener(tratarButtons);
    buttonActualizarTabela.addActionListener(tratarButtons);
    buttonLimparFiltro.addActionListener(tratarButtons);

    painelPesquisa.add(labelPesquisar);
    painelPesquisa.add(fieldPesquisa);
    painelPesquisa.add(comboCriterioPesquisa);
    painelPesquisa.add(buttonActualizarTabela);
    painelPesquisa.add(buttonLimparFiltro);

    return painelPesquisa;
  }

  private JScrollPane criarScrollTabelaCotas()
  {
    tabelaCotas = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabelaCotas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    modeloCotas = new DefaultTableModel();
    tabelaCotas.setModel(modeloCotas);
    sorterCotas = new TableRowSorter<>(modeloCotas);
    tabelaCotas.setRowSorter(sorterCotas);

    JScrollPane scroll = InterfaceGraficaUtils.criarScrollTabela(tabelaCotas, InterfaceGraficaUtils.COR_BORDA_TABELA);
    scroll.setPreferredSize(new Dimension(1180, 520));
    return scroll;
  }

  private JButton criarBotao(String texto)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(145, 42));
    IconesInterface.aplicarIconeBotao(botao);
    Resources.IconesInterface.aplicarEfeitoHoverBotaoFamilias(botao);
    return botao;
  }

  private void estilizarCampo(JTextField campo)
  {
    campo.setPreferredSize(new Dimension(420, 36));
    campo.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    InterfaceGraficaUtils.aplicarCantosArredondados(campo);
  }

  private void estilizarCombo(JComboBox<String> combo)
  {
    combo.setPreferredSize(new Dimension(420, 36));
    combo.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    InterfaceGraficaUtils.aplicarCantosArredondados(combo);
  }

  private void adicionarLinhaFormulario(JPanel painel, int linha, String textoLabel, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();

    JLabel label = new JLabel(textoLabel);
    label.setFont(InterfaceGraficaUtils.FONT_LABEL_FORM);
    label.setForeground(InterfaceGraficaUtils.COR_TEXTO);

    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(8, 0, 8, 16);
    painel.add(label, gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 0, 8, 0);
    painel.add(campo, gbc);
  }

  private void carregarCotas()
  {
    try
    {
      modeloCotas = CotaAguaConsultasSQL.carregarModeloTabela(connection,
          CotaAguaConsultasSQL.VISUALIZAR_FAMILIA_COTA_SEMANAL);
      tabelaCotas.setModel(modeloCotas);
      sorterCotas = new TableRowSorter<>(modeloCotas);
      tabelaCotas.setRowSorter(sorterCotas);
      aplicarFiltro();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar as cotas de água.\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void aplicarFiltro()
  {
    if(sorterCotas == null || fieldPesquisa == null || comboCriterioPesquisa == null)
      return;

    String texto = fieldPesquisa.getText().trim();
    if(texto.isEmpty())
    {
      sorterCotas.setRowFilter(null);
      return;
    }

    sorterCotas.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto), getIndiceColunaSelecionada()));
  }

  private int getIndiceColunaSelecionada()
  {
    String criterio = comboCriterioPesquisa.getSelectedItem().toString();

    switch(criterio)
    {
      case "Código Família":
        return getIndiceColuna("CODIGO_FB", 0);
      case "Nome Família":
        return getIndiceColuna("NOME_RESPONSAVEL_FB", 1);
      case "Contacto":
        return getIndiceColuna("CONTACTO_FB", 3);
      case "Código Cota":
        return getIndiceColuna("CODIGO_COTA", 5);
      case "Ajuste Sazonal":
        return getIndiceColuna("AJUSTE_SAZONAL_CA", 8);
      case "Status Validade":
        return getIndiceColuna("STATUS_VALIDADE_COTA", 13);
      default:
        return 0;
    }
  }

  private int getIndiceColuna(String nomeColuna, int fallback)
  {
    for(int i = 0; i < modeloCotas.getColumnCount(); i++)
    {
      if(nomeColuna.equalsIgnoreCase(modeloCotas.getColumnName(i)))
        return i;
    }

    if(fallback >= 0 && fallback < modeloCotas.getColumnCount())
      return fallback;

    return 0;
  }

  private int getIndiceColunaOpcional(String nomeColuna)
  {
    if(modeloCotas == null)
      return -1;

    for(int i = 0; i < modeloCotas.getColumnCount(); i++)
    {
      if(nomeColuna.equalsIgnoreCase(modeloCotas.getColumnName(i)))
        return i;
    }

    return -1;
  }

  private boolean validarSelecaoParaAvancar()
  {
    int[] linhasSelecionadas = tabelaCotas.getSelectedRows();

    if(linhasSelecionadas.length == 0)
    {
      JOptionPane.showMessageDialog(this, "Seleccione pelo menos uma cota de água.");
      return false;
    }

    int colunaStatus = getIndiceColunaOpcional("STATUS_VALIDADE_COTA");

    if(colunaStatus >= 0)
    {
      for(int linhaView : linhasSelecionadas)
      {
        int linhaModel = tabelaCotas.convertRowIndexToModel(linhaView);
        Object status = modeloCotas.getValueAt(linhaModel, colunaStatus);

        if(status != null && "EXPIRADA".equalsIgnoreCase(status.toString().trim()))
        {
          JOptionPane.showMessageDialog(this,
              "A seleção contém uma cota expirada. Remova as cotas expiradas antes de avançar.");
          return false;
        }
      }
    }

    return true;
  }

  private void prepararCardActualizacao()
  {
    fieldFamiliasSelecionadas.setText(formatarValoresSelecionados("CODIGO_FB", 0));
    fieldCotasSelecionadas.setText(formatarValoresSelecionados("CODIGO_COTA", 5));
  }

  private List<CotaSelecionada> getCotasSelecionadas()
  {
    int colunaFamilia = getIndiceColuna("CODIGO_FB", 0);
    int colunaCota = getIndiceColuna("CODIGO_COTA", 5);
    int[] linhasSelecionadas = tabelaCotas.getSelectedRows();
    List<CotaSelecionada> cotas = new ArrayList<CotaSelecionada>();

    for(int linhaView : linhasSelecionadas)
    {
      int linhaModel = tabelaCotas.convertRowIndexToModel(linhaView);
      int codigoFamilia = Integer.parseInt(modeloCotas.getValueAt(linhaModel, colunaFamilia).toString());
      int codigoCota = Integer.parseInt(modeloCotas.getValueAt(linhaModel, colunaCota).toString());
      cotas.add(new CotaSelecionada(codigoFamilia, codigoCota));
    }

    return cotas;
  }

  private void actualizarAjusteSazonal()
  {
    List<CotaSelecionada> cotas = getCotasSelecionadas();

    if(cotas.isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Nenhuma cota foi seleccionada.");
      cardLayout.show(panelCards, CARD_SELECIONAR_COTAS);
      return;
    }

    String novoAjuste = comboNovoAjusteSazonal.getSelectedItem().toString();

    try
    {
      for(CotaSelecionada cota : cotas)
      {
        ActualizarAjusteSazonalSQL.ResultadoPreValidacao preValidacao =
            ActualizarAjusteSazonalSQL.preValidarActualizacao(connection, cota.codigoFamilia, cota.codigoCota,
                novoAjuste);

        if(!preValidacao.podeContinuar)
        {
          JOptionPane.showMessageDialog(this,
              "Actualização bloqueada para família " + cota.codigoFamilia + ", cota " + cota.codigoCota + ":\n"
                  + MensagensInterface.formatarMensagem(preValidacao.mensagem));
          carregarCotas();
          cardLayout.show(panelCards, CARD_SELECIONAR_COTAS);
          return;
        }
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Deseja actualizar " + cotas.size() + " cota(s) para o ajuste sazonal \"" + novoAjuste + "\"?",
          "Confirmar Actualização", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      int totalActualizado = 0;
      StringBuilder mensagens = new StringBuilder();

      for(CotaSelecionada cota : cotas)
      {
        ActualizarAjusteSazonalSQL.ResultadoActualizacao resultado =
            ActualizarAjusteSazonalSQL.actualizarAjuste(connection, cota.codigoFamilia, cota.codigoCota, novoAjuste);

        if(resultado.actualizado)
          totalActualizado++;
        else
          mensagens.append("Família ").append(cota.codigoFamilia).append(", cota ").append(cota.codigoCota)
              .append(": ").append(MensagensInterface.formatarMensagem(resultado.mensagem)).append("\n");
      }

      carregarCotas();
      tabelaCotas.clearSelection();
      cardLayout.show(panelCards, CARD_SELECIONAR_COTAS);

      if(mensagens.length() > 0)
      {
        JOptionPane.showMessageDialog(this,
            totalActualizado + " cota(s) actualizada(s).\nOcorreram avisos:\n" + mensagens.toString());
      }
      else
      {
        JOptionPane.showMessageDialog(this, totalActualizado + " cota(s) actualizada(s) com sucesso.");
      }
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível actualizar ajuste sazonal:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private String formatarValoresSelecionados(String nomeColuna, int fallback)
  {
    int coluna = getIndiceColuna(nomeColuna, fallback);
    int[] linhasSelecionadas = tabelaCotas.getSelectedRows();
    List<String> valores = new ArrayList<String>();

    for(int linhaView : linhasSelecionadas)
    {
      int linhaModel = tabelaCotas.convertRowIndexToModel(linhaView);
      Object valor = modeloCotas.getValueAt(linhaModel, coluna);
      String texto = valor == null ? "" : valor.toString();

      if(!texto.isEmpty() && !valores.contains(texto))
        valores.add(texto);
    }

    return valores.isEmpty() ? "Nenhum" : String.join(", ", valores);
  }

  private class TratarPesquisa implements DocumentListener
  {
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
  }

  private class TratarButtons implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e)
    {
      if(e.getSource() == comboCriterioPesquisa)
      {
        aplicarFiltro();
        return;
      }

      if(e.getSource() == buttonActualizarTabela)
      {
        carregarCotas();
        return;
      }

      if(e.getSource() == buttonLimparFiltro)
      {
        fieldPesquisa.setText("");
        aplicarFiltro();
        return;
      }

      if(e.getSource() == buttonAvancar)
      {
        try
        {
          if(!validarSelecaoParaAvancar())
            return;

          prepararCardActualizacao();
          cardLayout.show(panelCards, CARD_ACTUALIZAR_AJUSTE);
        } catch(Exception ex)
        {
          if(Resources.TratadorConexaoFechada.tratar(null, ex))
            return;

          JOptionPane.showMessageDialog(JPanel_Actualizar_AjusteSazonal.this,
              "Não foi possível avançar para a actualização:\n" + MensagensInterface.formatarErro(ex));
        }
        return;
      }

      if(e.getSource() == buttonVoltarDashboard)
      {
        voltarAoDashboard();
        return;
      }

      if(e.getSource() == buttonVoltarParaCotas)
      {
        cardLayout.show(panelCards, CARD_SELECIONAR_COTAS);
        return;
      }

      if(e.getSource() == buttonActualizarAjuste)
      {
        actualizarAjusteSazonal();
      }
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }

  private static final class CotaSelecionada
  {
    private final int codigoFamilia;
    private final int codigoCota;

    private CotaSelecionada(int codigoFamilia, int codigoCota)
    {
      this.codigoFamilia = codigoFamilia;
      this.codigoCota = codigoCota;
    }
  }
}
