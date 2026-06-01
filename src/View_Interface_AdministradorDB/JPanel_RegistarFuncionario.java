package View_Interface_AdministradorDB;

import Repository_SQL.AdministradorDB.RegistarFuncionarioSQL;
import Repository_SQL.AdministradorDB.RegistarFuncionarioSQL.FuncionarioResumo;
import Repository_SQL.AdministradorDB.RegistarFuncionarioSQL.NoSistema;
import Repository_SQL.AdministradorDB.RegistarFuncionarioSQL.ResultadoRegisto;
import Resources.IconesInterface;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.List;
import java.util.regex.Pattern;

public class JPanel_RegistarFuncionario extends JPanel
{
  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z][A-Za-z0-9_]{0,26}$");

  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTextField fieldNomeFuncionario;
  private JTextField fieldUsername;
  private JTextField fieldPasswordInicial;
  private JComboBox<NoSistema> comboNos;
  private JTable tabelaFuncionarios;
  private JLabel labelEstado;
  private JButton buttonRegistar;
  private JButton buttonLimpar;
  private JButton buttonActualizar;

  public JPanel_RegistarFuncionario(Connection connection)
  {
    this(connection, true);
  }

  public JPanel_RegistarFuncionario(Connection connection, boolean carregarTabelasAutomaticamente)
  {
    this.connection = connection;

    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(22, 22, 22, 22));

    add(criarConteudo(), BorderLayout.CENTER);
    carregarNos();
    carregarFuncionarios();
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
    return InterfaceGraficaUtils.criarTopo("Registar Funcionário",
        "<html>Registo de novo funcionário.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarCentro()
  {
    JPanel centro = InterfaceGraficaUtils.criarPainelTransparente(new GridLayout(1, 2, 18, 0));
    centro.add(criarFormulario());
    centro.add(criarListaFuncionarios());
    return centro;
  }

  private JPanel criarFormulario()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 14));
    JPanel campos = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldNomeFuncionario = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(360, 36));
    fieldUsername = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(360, 36));
    fieldPasswordInicial = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(360, 36));
    fieldPasswordInicial.setEditable(false);
    fieldPasswordInicial.setEnabled(false);
    fieldPasswordInicial.setDisabledTextColor(InterfaceGraficaUtils.COR_TEXTO);
    fieldPasswordInicial.setText("123");

    fieldUsername.getDocument().addDocumentListener(new DocumentListener()
    {
      public void insertUpdate(DocumentEvent event)
      {
        actualizarPasswordInicial();
      }

      public void removeUpdate(DocumentEvent event)
      {
        actualizarPasswordInicial();
      }

      public void changedUpdate(DocumentEvent event)
      {
        actualizarPasswordInicial();
      }
    });

    comboNos = new JComboBox<NoSistema>();
    comboNos.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    comboNos.setPreferredSize(new Dimension(360, 36));

    adicionarLinha(campos, 0, "Nome do funcionário:", fieldNomeFuncionario);
    adicionarLinha(campos, 1, "Username:", fieldUsername);
    adicionarLinha(campos, 2, "Password inicial:", fieldPasswordInicial);
    adicionarLinha(campos, 3, "No do sistema:", comboNos);
    JLabel nota = InterfaceGraficaUtils.criarLabel(
        "Password inicial apresentada apenas para informacao. O funcionário devera usa-la no primeiro login.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    painel.add(campos, BorderLayout.NORTH);
    painel.add(nota, BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarListaFuncionarios()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 12));

    JLabel titulo = InterfaceGraficaUtils.criarLabel("Funcionários registados", InterfaceGraficaUtils.FONT_LABEL_SECAO,
        InterfaceGraficaUtils.COR_TEXTO);
    tabelaFuncionarios = InterfaceGraficaUtils.criarTabelaBase(InterfaceGraficaUtils.FONT_TABELA,
        InterfaceGraficaUtils.FONT_HEADER, InterfaceGraficaUtils.COR_GRID_TABELA, InterfaceGraficaUtils.COR_SELECAO_TABELA,
        InterfaceGraficaUtils.COR_TEXTO);

    painel.add(titulo, BorderLayout.NORTH);
    painel.add(InterfaceGraficaUtils.criarScrollTabela(tabelaFuncionarios, InterfaceGraficaUtils.COR_BORDA_TABELA),
        BorderLayout.CENTER);
    return painel;
  }

  private void adicionarLinha(JPanel painel, int linha, String label, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(8, 0, 8, 16);
    painel.add(InterfaceGraficaUtils.criarLabel(label, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 0, 8, 0);
    painel.add(campo, gbc);
  }

  private JPanel criarRodape()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    labelEstado = InterfaceGraficaUtils.criarLabel("", InterfaceGraficaUtils.FONT_AJUDA,
        InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonLimpar = criarBotao("Limpar", 115);
    buttonActualizar = criarBotao("Mostrar Todos", 150);
    buttonRegistar = criarBotao("Registar", 135);

    buttonLimpar.addActionListener(tratarButtons);
    buttonActualizar.addActionListener(tratarButtons);
    buttonRegistar.addActionListener(tratarButtons);

    botoes.add(buttonLimpar);
    botoes.add(buttonActualizar);
    botoes.add(buttonRegistar);

    painel.add(labelEstado, BorderLayout.WEST);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private JButton criarBotao(String texto, int largura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO, InterfaceGraficaUtils.COR_AZUL,
        InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, 42));
    IconesInterface.aplicarIconeBotao(botao);
    return botao;
  }

  private void carregarNos()
  {
    try
    {
      comboNos.removeAllItems();
      List<NoSistema> nos = RegistarFuncionarioSQL.carregarNos(connection);
      for(NoSistema no : nos)
        comboNos.addItem(no);
      labelEstado.setText("");
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      labelEstado.setText("Não foi possível carregar nos.");
      JOptionPane.showMessageDialog(this, "Não foi possível carregar os nós do sistema: " + MensagensInterface.formatarErro(ex));
    }
  }

  private void carregarFuncionarios()
  {
    try
    {
      List<FuncionarioResumo> funcionarios = RegistarFuncionarioSQL.carregarFuncionarios(connection);
      DefaultTableModel model = new DefaultTableModel(
          new Object[] { "Código", "Nome", "Username", "No", "Estado" }, 0)
        {
          public boolean isCellEditable(int row, int column)
          {
            return false;
          }
        };

      for(FuncionarioResumo funcionario : funcionarios)
        model.addRow(new Object[] { funcionario.codigoFuncionario, funcionario.nomeFuncionario,
            funcionario.usernameOracle, funcionario.nomeNo, funcionario.estadoFuncionario });

      tabelaFuncionarios.setModel(model);
      InterfaceGraficaUtils.ajustarLarguraColunas(tabelaFuncionarios, InterfaceGraficaUtils.FONT_TABELA,
          InterfaceGraficaUtils.FONT_HEADER);
      labelEstado.setText("Funcionários carregados.");
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      labelEstado.setText("Não foi possível carregar funcionários.");
      JOptionPane.showMessageDialog(this, "Não foi possível carregar os funcionários: " + MensagensInterface.formatarErro(ex));
    }
  }

  private void actualizarPasswordInicial()
  {
    String username = fieldUsername.getText().trim();

    if(username.length() == 0)
      fieldPasswordInicial.setText("123");
    else
      fieldPasswordInicial.setText(username.toLowerCase() + "123");
  }

  private void registarFuncionario()
  {
    String nomeFuncionario = fieldNomeFuncionario.getText().trim();
    String username = fieldUsername.getText().trim();
    NoSistema noSistema = (NoSistema) comboNos.getSelectedItem();

    try
    {
      if(nomeFuncionario.length() == 0)
      {
        JOptionPane.showMessageDialog(this, "Nome do funcionário é obrigatório.");
        return;
      }

      if(!USERNAME_PATTERN.matcher(username).matches())
      {
        JOptionPane.showMessageDialog(this,
            "Username invalido. Comece por letra e use apenas letras, numeros ou underscore. Maximo: 27 caracteres.");
        return;
      }

      if(noSistema == null)
      {
        JOptionPane.showMessageDialog(this, "Seleccione o nó do sistema.");
        return;
      }

      ResultadoRegisto resultado = RegistarFuncionarioSQL.registarFuncionario(connection, nomeFuncionario, username,
          noSistema.codigoNo);

      labelEstado.setText(resultado.mensagem == null ? "Operação concluida." : resultado.mensagem);

      if(resultado.sucesso)
      {
        limparFormularioSemTabela();
        carregarFuncionarios();
        JOptionPane.showMessageDialog(this,
            MensagensInterface.formatarMensagem(resultado.mensagem) + "\nCódigo do funcionário: " + resultado.codigoFuncionario);
      }
      else
      {
        JOptionPane.showMessageDialog(this, resultado.mensagem == null ? "Não foi possível registar funcionário."
            : resultado.mensagem);
      }
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      labelEstado.setText("Não foi possível registar funcionário.");
      JOptionPane.showMessageDialog(this, "Não foi possível registar funcionário: " + MensagensInterface.formatarErro(ex));
    }
  }

  private void limparFormulario()
  {
    limparFormularioSemTabela();
    labelEstado.setText("Formulario limpo.");
  }

  private void limparFormularioSemTabela()
  {
    fieldNomeFuncionario.setText("");
    fieldUsername.setText("");
    actualizarPasswordInicial();
    if(comboNos.getItemCount() > 0)
      comboNos.setSelectedIndex(0);
  }

  private class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == buttonRegistar)
        registarFuncionario();
      else if(event.getSource() == buttonActualizar)
      {
        carregarNos();
        carregarFuncionarios();
      }
      else if(event.getSource() == buttonLimpar)
        limparFormulario();
    }
  }
}
