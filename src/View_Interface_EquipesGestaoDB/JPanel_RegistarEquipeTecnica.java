package View_Interface_EquipesGestaoDB;

import Repository_SQL.EquipesGestaoDB.RegistarEquipeTecnicaSQL;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class JPanel_RegistarEquipeTecnica extends JPanel
{
  private static final String CARD_GERAL = "geral";
  private static final String CARD_TECNICO = "técnico";
  private static final String CARD_ANALISTA = "analista";
  private static final String CARD_EDUCADOR = "educador";

  private final Connection connection;
  private final Runnable voltarDashboard;
  private final CardLayout cardLayout = new CardLayout();
  private final JPanel panelCards = InterfaceGraficaUtils.criarPainelTransparente(cardLayout);
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTextField fieldNome;
  private JTextField fieldAreaActuacao;
  private JComboBox<String> comboNivelFormacao;
  private JTextField fieldContacto;
  private JTextField fieldSupervisorResponsavel;
  private JRadioButton radioTecnicoManutencao;
  private JRadioButton radioAnalistaQualidade;
  private JRadioButton radioEducadorComunitario;

  private JTextField fieldHabilidadeTecnica;
  private JSpinner spinnerTempoResposta;
  private JTable tabelaFerramentas;
  private DefaultTableModel modeloFerramentas;
  private JTextField fieldNovaFerramenta;
  private DefaultListModel<String> modeloNovasFerramentas;

  private JTable tabelaParametros;
  private DefaultTableModel modeloParametros;
  private JTextField fieldNovoParametro;
  private JTextField fieldUnidadeParametro;
  private JTextField fieldNovoEquipamento;
  private DefaultListModel<String> modeloEquipamentos;
  private JTextField fieldEspecialidadeAnalise;
  private JComboBox<String> comboFrequenciaAmostragem;

  private JTextField fieldNovoMaterial;
  private DefaultListModel<String> modeloMateriais;
  private JTextField fieldMetodologia;
  private JTextField fieldLinguaLocal;
  private JTextField fieldComunidade;

  private JButton buttonLimpar;
  private JButton buttonVoltarDashboard;
  private JButton buttonContinuar;
  private JButton buttonVoltarTecnico;
  private JButton buttonRegistarTecnico;
  private JButton buttonAddFerramenta;
  private JButton buttonVoltarAnalista;
  private JButton buttonRegistarAnalista;
  private JButton buttonAddEquipamento;
  private JButton buttonVoltarEducador;
  private JButton buttonRegistarEducador;
  private JButton buttonAddMaterial;

  public JPanel_RegistarEquipeTecnica(Connection connection)
  {
    this(connection, true, null);
  }

  public JPanel_RegistarEquipeTecnica(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this(connection, carregarTabelasAutomaticamente, null);
  }

  public JPanel_RegistarEquipeTecnica(Connection connection, Runnable voltarDashboard)
  {
    this(connection, true, voltarDashboard);
  }

  public JPanel_RegistarEquipeTecnica(Connection connection, boolean carregarTabelasAutomaticamente, Runnable voltarDashboard)
  {
    this.connection = connection;
    this.voltarDashboard = voltarDashboard;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    panelCards.add(criarCardGeral(), CARD_GERAL);
    panelCards.add(criarCardTecnico(), CARD_TECNICO);
    panelCards.add(criarCardAnalista(), CARD_ANALISTA);
    panelCards.add(criarCardEducador(), CARD_EDUCADOR);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(18, 18, 18, 18);
    add(panelCards, gbc);
  }

  private JPanel criarCardGeral()
  {
    JPanel conteudo = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldNome = criarCampoTexto();
    fieldAreaActuacao = criarCampoTexto();
    comboNivelFormacao = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Ensino medio", "Técnico profissional", "Licenciatura", "Mestrado", "Doutoramento", "Outro"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(360, 36));
    fieldContacto = criarCampoTexto();
    fieldSupervisorResponsavel = criarCampoTexto();

    radioTecnicoManutencao = criarRadio("Técnico de Manutenção");
    radioAnalistaQualidade = criarRadio("Analista de Qualidade");
    radioEducadorComunitario = criarRadio("Educador Comunitário");

    ButtonGroup grupo = new ButtonGroup();
    grupo.add(radioTecnicoManutencao);
    grupo.add(radioAnalistaQualidade);
    grupo.add(radioEducadorComunitario);
    radioTecnicoManutencao.setSelected(true);

    int linha = 0;
    adicionarLinha(conteudo, linha++, "Nome da equipe:", fieldNome);
    adicionarLinha(conteudo, linha++, "Area de actuacao:", fieldAreaActuacao);
    adicionarLinha(conteudo, linha++, "Nivel de formacao:", comboNivelFormacao);
    adicionarLinha(conteudo, linha++, "Contacto telefonico:", fieldContacto);
    adicionarLinha(conteudo, linha++, "Supervisor responsavel:", fieldSupervisorResponsavel);
    adicionarLinha(conteudo, linha++, "Tipo de equipe:", criarPainelTipos());

    JPanel rodape = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonLimpar = criarBotao("Limpar", 130);
    buttonVoltarDashboard = criarBotao("Voltar", 120);
    buttonContinuar = criarBotao("Proceder", 145);
    buttonLimpar.addActionListener(tratarButtons);
    buttonVoltarDashboard.addActionListener(tratarButtons);
    buttonContinuar.addActionListener(tratarButtons);
    rodape.add(buttonLimpar);
    rodape.add(buttonVoltarDashboard);
    rodape.add(buttonContinuar);

    return criarCard("Registar Equipe Técnica", "Preencha os dados gerais e escolha o tipo de equipe.", conteudo,
        rodape);
  }

  private JPanel criarCardTecnico()
  {
    JPanel conteudo = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    fieldHabilidadeTecnica = criarCampoTexto();
    spinnerTempoResposta = new JSpinner(new SpinnerNumberModel(24, 1, 999, 1));
    spinnerTempoResposta.setPreferredSize(new Dimension(120, 36));
    fieldNovaFerramenta = criarCampoTexto();
    modeloNovasFerramentas = new DefaultListModel<String>();

    modeloFerramentas = new DefaultTableModel();
    tabelaFerramentas = criarTabela(modeloFerramentas);
    JScrollPane scrollFerramentas = new JScrollPane(tabelaFerramentas);
    scrollFerramentas.setPreferredSize(new Dimension(520, 140));

    JPanel linhaNova = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    buttonAddFerramenta = criarBotao("Adicionar", 120);
    buttonAddFerramenta.addActionListener(tratarButtons);
    linhaNova.add(fieldNovaFerramenta);
    linhaNova.add(buttonAddFerramenta);

    int linha = 0;
    adicionarLinha(conteudo, linha++, "Ferramentas disponiveis:", scrollFerramentas);
    adicionarLinha(conteudo, linha++, "Nova ferramenta:", linhaNova);
    adicionarLinha(conteudo, linha++, "Ferramentas novas:", new JScrollPane(new JList<String>(modeloNovasFerramentas)));
    adicionarLinha(conteudo, linha++, "Habilidade técnica:", fieldHabilidadeTecnica);
    adicionarLinha(conteudo, linha++, "Tempo medio resposta:", spinnerTempoResposta);

    JPanel rodape = criarRodapeDetalhe();
    buttonVoltarTecnico = (JButton) rodape.getComponent(0);
    buttonRegistarTecnico = (JButton) rodape.getComponent(1);
    buttonVoltarTecnico.addActionListener(tratarButtons);
    buttonRegistarTecnico.addActionListener(tratarButtons);

    return criarCard("Técnico de Manutenção", "Associe ferramentas e preencha os dados técnicos.", conteudo, rodape);
  }

  private JPanel criarCardAnalista()
  {
    JPanel conteudo = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    modeloParametros = new DefaultTableModel();
    tabelaParametros = criarTabela(modeloParametros);
    JScrollPane scrollParametros = new JScrollPane(tabelaParametros);
    scrollParametros.setPreferredSize(new Dimension(560, 140));

    fieldNovoParametro = criarCampoTexto();
    fieldUnidadeParametro = criarCampoTexto();
    fieldNovoEquipamento = criarCampoTexto();
    modeloEquipamentos = new DefaultListModel<String>();
    fieldEspecialidadeAnalise = criarCampoTexto();
    comboFrequenciaAmostragem = InterfaceGraficaUtils.criarCombo(new String[]
      {
          "Semanal", "Quinzenal", "Mensal"
      }, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(360, 36));

    JPanel linhaEquipamento = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    buttonAddEquipamento = criarBotao("Adicionar", 120);
    buttonAddEquipamento.addActionListener(tratarButtons);
    linhaEquipamento.add(fieldNovoEquipamento);
    linhaEquipamento.add(buttonAddEquipamento);

    int linha = 0;
    adicionarLinha(conteudo, linha++, "Parametro existente:", scrollParametros);
    adicionarLinha(conteudo, linha++, "Novo parâmetro:", fieldNovoParametro);
    adicionarLinha(conteudo, linha++, "Unidade do parâmetro:", fieldUnidadeParametro);
    adicionarLinha(conteudo, linha++, "Novo equipamento:", linhaEquipamento);
    adicionarLinha(conteudo, linha++, "Equipamentos:", new JScrollPane(new JList<String>(modeloEquipamentos)));
    adicionarLinha(conteudo, linha++, "Especialidade:", fieldEspecialidadeAnalise);
    adicionarLinha(conteudo, linha++, "Frequencia:", comboFrequenciaAmostragem);

    JPanel rodape = criarRodapeDetalhe();
    buttonVoltarAnalista = (JButton) rodape.getComponent(0);
    buttonRegistarAnalista = (JButton) rodape.getComponent(1);
    buttonVoltarAnalista.addActionListener(tratarButtons);
    buttonRegistarAnalista.addActionListener(tratarButtons);

    return criarCard("Analista de Qualidade", "Escolha ou registe um parâmetro e associe equipamentos.", conteudo,
        rodape);
  }

  private JPanel criarCardEducador()
  {
    JPanel conteudo = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    fieldNovoMaterial = criarCampoTexto();
    modeloMateriais = new DefaultListModel<String>();
    fieldMetodologia = criarCampoTexto();
    fieldLinguaLocal = criarCampoTexto();
    fieldComunidade = criarCampoTexto();

    JPanel linhaMaterial = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    buttonAddMaterial = criarBotao("Adicionar", 120);
    buttonAddMaterial.addActionListener(tratarButtons);
    linhaMaterial.add(fieldNovoMaterial);
    linhaMaterial.add(buttonAddMaterial);

    int linha = 0;
    adicionarLinha(conteudo, linha++, "Novo material:", linhaMaterial);
    adicionarLinha(conteudo, linha++, "Materiais:", new JScrollPane(new JList<String>(modeloMateriais)));
    adicionarLinha(conteudo, linha++, "Metodologia:", fieldMetodologia);
    adicionarLinha(conteudo, linha++, "Lingua local:", fieldLinguaLocal);
    adicionarLinha(conteudo, linha++, "Comunidade atendida:", fieldComunidade);

    JPanel rodape = criarRodapeDetalhe();
    buttonVoltarEducador = (JButton) rodape.getComponent(0);
    buttonRegistarEducador = (JButton) rodape.getComponent(1);
    buttonVoltarEducador.addActionListener(tratarButtons);
    buttonRegistarEducador.addActionListener(tratarButtons);

    return criarCard("Educador Comunitário", "Registe materiais e preencha os dados comunitários.", conteudo, rodape);
  }

  private JPanel criarCard(String titulo, String subtitulo, JPanel conteudo, JPanel rodape)
  {
    JPanel externo = new JPanel(new GridBagLayout());
    externo.setOpaque(false);
    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 18),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(28, 28, 28, 28), new Dimension(1040, 720));
    card.add(InterfaceGraficaUtils.criarTopo(titulo, subtitulo, InterfaceGraficaUtils.FONT_TITULO,
        InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO, InterfaceGraficaUtils.COR_SUBTEXTO),
        BorderLayout.NORTH);
    card.add(new JScrollPane(conteudo), BorderLayout.CENTER);
    card.add(rodape, BorderLayout.SOUTH);
    externo.add(card, new GridBagConstraints());
    return externo;
  }

  private JPanel criarPainelTipos()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 14, 0));
    painel.add(radioTecnicoManutencao);
    painel.add(radioAnalistaQualidade);
    painel.add(radioEducadorComunitario);
    return painel;
  }

  private JPanel criarRodapeDetalhe()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    painel.add(criarBotao("Voltar", 120));
    painel.add(criarBotao("Registar", 145));
    return painel;
  }

  private JTextField criarCampoTexto()
  {
    return InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(360, 36));
  }

  private JRadioButton criarRadio(String texto)
  {
    JRadioButton radio = new JRadioButton(texto);
    radio.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    radio.setForeground(InterfaceGraficaUtils.COR_TEXTO);
    radio.setOpaque(false);
    radio.setFocusPainted(false);
    return radio;
  }

  private JTable criarTabela(DefaultTableModel modelo)
  {
    JTable tabela = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA,
        InterfaceGraficaUtils.COR_SELECAO_TABELA, InterfaceGraficaUtils.COR_TEXTO);
    tabela.setModel(modelo);
    tabela.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    tabela.setRowHeight(28);
    return tabela;
  }

  private JButton criarBotao(String texto, int largura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, 42));
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  private void adicionarLinha(JPanel painel, int linha, String textoLabel, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(9, 0, 9, 18);
    painel.add(InterfaceGraficaUtils.criarLabel(textoLabel, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(9, 0, 9, 0);
    painel.add(campo, gbc);
  }

  private boolean validarDadosGerais()
  {
    if(fieldNome.getText().trim().isEmpty() || fieldAreaActuacao.getText().trim().isEmpty()
        || fieldContacto.getText().trim().isEmpty() || fieldSupervisorResponsavel.getText().trim().isEmpty())
    {
      JOptionPane.showMessageDialog(this, "Preencha todos os dados gerais obrigatórios antes de continuar.");
      return false;
    }
    return true;
  }

  private void irParaDetalhe()
  {
    if(!validarDadosGerais())
      return;

    if(radioTecnicoManutencao.isSelected())
    {
      carregarFerramentas();
      cardLayout.show(panelCards, CARD_TECNICO);
    } else if(radioAnalistaQualidade.isSelected())
    {
      carregarParametros();
      cardLayout.show(panelCards, CARD_ANALISTA);
    } else
    {
      cardLayout.show(panelCards, CARD_EDUCADOR);
    }
  }

  private void carregarParametros()
  {
    try
    {
      modeloParametros = RegistarEquipeTecnicaSQL.carregarParametros(connection);
      tabelaParametros.setModel(modeloParametros);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar parâmetros:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarFerramentas()
  {
    try
    {
      modeloFerramentas = RegistarEquipeTecnicaSQL.carregarFerramentasDisponiveis(connection);
      tabelaFerramentas.setModel(modeloFerramentas);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível carregar ferramentas:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private String getTipoSelecionado()
  {
    if(radioAnalistaQualidade.isSelected())
      return "Analista de Qualidade";
    if(radioEducadorComunitario.isSelected())
      return "Educador Comunitario";
    return "Tecnico de Manutencao";
  }

  private Integer getParametroSelecionado()
  {
    int linha = tabelaParametros.getSelectedRow();
    if(linha < 0)
      return null;
    int linhaModelo = tabelaParametros.convertRowIndexToModel(linha);
    Object valor = modeloParametros.getValueAt(linhaModelo, 0);
    return Integer.valueOf(valor.toString());
  }

  private String getFerramentasSelecionadas()
  {
    int[] linhas = tabelaFerramentas.getSelectedRows();
    List<String> ids = new ArrayList<String>();
    for(int i = 0; i < linhas.length; i++)
    {
      int linhaModelo = tabelaFerramentas.convertRowIndexToModel(linhas[i]);
      ids.add(modeloFerramentas.getValueAt(linhaModelo, 0).toString());
    }
    return String.join(",", ids);
  }

  private String join(DefaultListModel<String> modelo)
  {
    List<String> valores = new ArrayList<String>();
    for(int i = 0; i < modelo.size(); i++)
      valores.add(modelo.get(i));
    return String.join("|", valores);
  }

  private void adicionarItem(DefaultListModel<String> modelo, JTextField campo)
  {
    String valor = campo.getText().trim();
    if(valor.isEmpty())
      return;
    modelo.addElement(valor);
    campo.setText("");
  }

  private Integer getTempoResposta()
  {
    return Integer.valueOf(((Number) spinnerTempoResposta.getValue()).intValue());
  }

  private RegistarEquipeTecnicaSQL.DadosEquipe recolherDados()
  {
    String tipo = getTipoSelecionado();
    return new RegistarEquipeTecnicaSQL.DadosEquipe(fieldNome.getText().trim(), fieldAreaActuacao.getText().trim(),
        String.valueOf(comboNivelFormacao.getSelectedItem()), fieldContacto.getText().trim(),
        fieldSupervisorResponsavel.getText().trim(), tipo, fieldHabilidadeTecnica.getText().trim(), getTempoResposta(),
        getFerramentasSelecionadas(), join(modeloNovasFerramentas), getParametroSelecionado(),
        fieldNovoParametro.getText().trim(), fieldUnidadeParametro.getText().trim(),
        fieldEspecialidadeAnalise.getText().trim(), String.valueOf(comboFrequenciaAmostragem.getSelectedItem()),
        join(modeloEquipamentos), fieldMetodologia.getText().trim(), fieldLinguaLocal.getText().trim(),
        fieldComunidade.getText().trim(), join(modeloMateriais));
  }

  private void registar()
  {
    try
    {
      RegistarEquipeTecnicaSQL.DadosEquipe dados = recolherDados();
      RegistarEquipeTecnicaSQL.ResultadoValidacao validacao = RegistarEquipeTecnicaSQL.preValidar(connection, dados);
      if(!validacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(validacao.mensagem));
        return;
      }

      RegistarEquipeTecnicaSQL.ResultadoRegisto resultado = RegistarEquipeTecnicaSQL.registar(connection, dados);
      JOptionPane.showMessageDialog(this,
          MensagensInterface.formatarMensagem(resultado.mensagem) + (resultado.registado ? "\nCódigo da equipe: " + resultado.equipeId : ""));
      if(resultado.registado)
        limparFormulario();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível registar equipe técnica:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void limparFormulario()
  {
    fieldNome.setText("");
    fieldAreaActuacao.setText("");
    fieldContacto.setText("");
    fieldSupervisorResponsavel.setText("");
    comboNivelFormacao.setSelectedIndex(0);
    radioTecnicoManutencao.setSelected(true);
    fieldHabilidadeTecnica.setText("");
    spinnerTempoResposta.setValue(Integer.valueOf(24));
    fieldNovaFerramenta.setText("");
    modeloNovasFerramentas.clear();
    fieldNovoParametro.setText("");
    fieldUnidadeParametro.setText("");
    fieldNovoEquipamento.setText("");
    modeloEquipamentos.clear();
    fieldEspecialidadeAnalise.setText("");
    comboFrequenciaAmostragem.setSelectedIndex(0);
    fieldNovoMaterial.setText("");
    modeloMateriais.clear();
    fieldMetodologia.setText("");
    fieldLinguaLocal.setText("");
    fieldComunidade.setText("");
    cardLayout.show(panelCards, CARD_GERAL);
  }

  private final class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      Object source = event.getSource();
      if(source == buttonLimpar)
        limparFormulario();
      else if(source == buttonVoltarDashboard)
        voltarAoDashboard();
      else if(source == buttonContinuar)
        irParaDetalhe();
      else if(source == buttonVoltarTecnico || source == buttonVoltarAnalista || source == buttonVoltarEducador)
        cardLayout.show(panelCards, CARD_GERAL);
      else if(source == buttonAddFerramenta)
        adicionarItem(modeloNovasFerramentas, fieldNovaFerramenta);
      else if(source == buttonAddEquipamento)
        adicionarItem(modeloEquipamentos, fieldNovoEquipamento);
      else if(source == buttonAddMaterial)
        adicionarItem(modeloMateriais, fieldNovoMaterial);
      else if(source == buttonRegistarTecnico || source == buttonRegistarAnalista || source == buttonRegistarEducador)
        registar();
    }
  }

  private void voltarAoDashboard()
  {
    if(voltarDashboard != null)
      voltarDashboard.run();
  }
}
