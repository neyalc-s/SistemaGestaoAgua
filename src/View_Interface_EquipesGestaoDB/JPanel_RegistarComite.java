package View_Interface_EquipesGestaoDB;

import Repository_SQL.EquipesGestaoDB.RegistarComiteSQL;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;

public class JPanel_RegistarComite extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTextField fieldNomeComite;
  private JSpinner spinnerDia;
  private JSpinner spinnerMes;
  private JSpinner spinnerAno;
  private JButton buttonRegistar;
  private JButton buttonLimpar;

  public JPanel_RegistarComite(Connection connection)
  {
    this.connection = connection;

    setLayout(new GridBagLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);

    add(criarCard(), criarGbcPrincipal());
  }

  private GridBagConstraints criarGbcPrincipal()
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(18, 18, 18, 18);
    return gbc;
  }

  private JPanel criarCard()
  {
    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32, new BorderLayout(0, 22),
        InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30), new Dimension(780, 460));

    card.add(criarTopo(), BorderLayout.NORTH);
    card.add(criarFormulario(), BorderLayout.CENTER);
    card.add(criarRodape(), BorderLayout.SOUTH);
    return card;
  }

  private JPanel criarTopo()
  {
    return InterfaceGraficaUtils.criarTopo("Registar Comité",
        "<html>Preencha os dados do comité local responsável pelos pontos de distribuição.</html>",
        InterfaceGraficaUtils.FONT_TITULO, InterfaceGraficaUtils.FONT_SUBTITULO, InterfaceGraficaUtils.COR_TEXTO,
        InterfaceGraficaUtils.COR_SUBTEXTO);
  }

  private JPanel criarFormulario()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());

    fieldNomeComite = InterfaceGraficaUtils.criarCampoTexto(24, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(420, 36));

    LocalDate hoje = LocalDate.now();
    spinnerDia = InterfaceGraficaUtils.criarSpinnerNumero(hoje.getDayOfMonth(), 1, 31, 1,
        InterfaceGraficaUtils.FONT_CAMPO, new Dimension(82, 36));
    spinnerMes = InterfaceGraficaUtils.criarSpinnerNumero(hoje.getMonthValue(), 1, 12, 1,
        InterfaceGraficaUtils.FONT_CAMPO, new Dimension(82, 36));
    spinnerAno = InterfaceGraficaUtils.criarSpinnerNumero(hoje.getYear(), 1900, hoje.getYear(), 1,
        InterfaceGraficaUtils.FONT_CAMPO, new Dimension(105, 36));
    InterfaceGraficaUtils.removerSeparadorMilhares(spinnerAno);

    adicionarLinha(painel, 0, "Nome do comité:", fieldNomeComite);
    adicionarLinha(painel, 1, "Data de criação:", criarPainelData());

    GridBagConstraints gbcFim = new GridBagConstraints();
    gbcFim.gridx = 0;
    gbcFim.gridy = 2;
    gbcFim.weighty = 1.0;
    gbcFim.fill = GridBagConstraints.VERTICAL;
    painel.add(Box.createVerticalGlue(), gbcFim);

    return painel;
  }

  private JPanel criarPainelData()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 8, 0));
    painel.add(spinnerDia);
    painel.add(InterfaceGraficaUtils.criarLabel("/", InterfaceGraficaUtils.FONT_LABEL, InterfaceGraficaUtils.COR_TEXTO));
    painel.add(spinnerMes);
    painel.add(InterfaceGraficaUtils.criarLabel("/", InterfaceGraficaUtils.FONT_LABEL, InterfaceGraficaUtils.COR_TEXTO));
    painel.add(spinnerAno);
    return painel;
  }

  private void adicionarLinha(JPanel painel, int linha, String label, JComponent campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(10, 0, 10, 18);
    painel.add(InterfaceGraficaUtils.criarLabel(label, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(10, 0, 10, 0);
    painel.add(campo, gbc);
  }

  private JPanel criarRodape()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());

    JLabel ajuda = InterfaceGraficaUtils.criarLabel("O código do comité será gerado automaticamente.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonLimpar = criarBotao("Limpar", 130);
    buttonRegistar = criarBotao("Registar", 145);

    buttonLimpar.addActionListener(tratarButtons);
    buttonRegistar.addActionListener(tratarButtons);

    botoes.add(buttonLimpar);
    botoes.add(buttonRegistar);

    painel.add(ajuda, BorderLayout.WEST);
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

  private void registarComite()
  {
    try
    {
      RegistarComiteSQL.DadosComite dados = criarDadosComite();

      RegistarComiteSQL.ResultadoPreValidacao preValidacao =
          RegistarComiteSQL.preValidarRegisto(connection, dados);

      if(!preValidacao.podeContinuar)
      {
        JOptionPane.showMessageDialog(this, "Comité não pode ser registado:\n" + MensagensInterface.formatarMensagem(preValidacao.mensagem));
        return;
      }

      int resposta = JOptionPane.showConfirmDialog(this,
          "Nome do comité: " + dados.nomeComite + "\nData de criação: " + dados.dataCriacao
              + "\n\nDeseja registar este comité?",
          "Confirmar Comité", JOptionPane.YES_NO_OPTION);

      if(resposta != JOptionPane.YES_OPTION)
        return;

      RegistarComiteSQL.ResultadoRegisto resultado = RegistarComiteSQL.registarComite(connection, dados);

      if(!resultado.registado)
      {
        JOptionPane.showMessageDialog(this, "Comité não registado:\n" + MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this,
          "Comité registado com sucesso.\nCódigo: " + resultado.codigoComite + "\nMensagem: "
              + MensagensInterface.formatarMensagem(resultado.mensagem));
      limparFormulario();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, MensagensInterface.formatarErro(ex));
    }
  }

  private RegistarComiteSQL.DadosComite criarDadosComite()
  {
    String nome = fieldNomeComite.getText().trim();

    if(nome.isEmpty())
      throw new IllegalArgumentException("Informe o nome do comité.");

    if(nome.length() > 60)
      throw new IllegalArgumentException("Nome do comité não pode exceder 60 caracteres.");

    LocalDate data = criarDataSelecionada();
    if(data.isAfter(LocalDate.now()))
      throw new IllegalArgumentException("Data de criação não pode ser futura.");

    return new RegistarComiteSQL.DadosComite(nome, Date.valueOf(data));
  }

  private LocalDate criarDataSelecionada()
  {
    int dia = ((Integer) spinnerDia.getValue()).intValue();
    int mes = ((Integer) spinnerMes.getValue()).intValue();
    int ano = ((Integer) spinnerAno.getValue()).intValue();

    try
    {
      return LocalDate.of(ano, mes, dia);
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        throw new IllegalArgumentException("Data de criação inválida para o mês/ano escolhido.");

      throw new IllegalArgumentException("Data de criação inválida para o mês/ano escolhido.");
    }
  }

  private void limparFormulario()
  {
    fieldNomeComite.setText("");
    LocalDate hoje = LocalDate.now();
    spinnerDia.setValue(Integer.valueOf(hoje.getDayOfMonth()));
    spinnerMes.setValue(Integer.valueOf(hoje.getMonthValue()));
    spinnerAno.setValue(Integer.valueOf(hoje.getYear()));
  }

  private final class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == buttonLimpar)
      {
        limparFormulario();
        return;
      }

      if(event.getSource() == buttonRegistar)
        registarComite();
    }
  }
}
