package View_Interface_DistribuicaoConsumoDB;

import Repository_SQL.DistribuicaoConsumoDB.ActualizarObservacaoRegistroConsumoSQL;
import Repository_SQL.DistribuicaoConsumoDB.ActualizarObservacaoRegistroConsumoSQL.RegistroConsumo;
import Repository_SQL.DistribuicaoConsumoDB.ActualizarObservacaoRegistroConsumoSQL.ResultadoActualizacao;
import Resources.InterfaceGraficaUtils;
import Resources.MensagensInterface;
import Resources.IconesInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class JPanel_Actualizar_Observacao_Registro_Consumo extends JPanel
{
  private final Connection connection;
  private final TratarButtons tratarButtons = new TratarButtons();

  private JTextField fieldCodigoRc;
  private JTextField fieldCodigoFb;
  private JTextField fieldCodigoPd;
  private JTextField fieldCodigoCota;
  private JTextField fieldDataHora;
  private JTextField fieldVolumeRetirado;
  private JTextField fieldPessoaColeta;
  private JTextField fieldMetodoAutenticacao;
  private JTextField fieldSaldoCota;
  private JTextField fieldValidadeCota;
  private JTextField fieldStatusCota;
  private JTextArea areaObservacaoActual;
  private JTextArea areaNovaObservacao;
  private JButton buttonCarregar;
  private JButton buttonActualizar;

  private RegistroConsumo registroCarregado;
  private String observacaoOriginal;

  public JPanel_Actualizar_Observacao_Registro_Consumo(Connection connection)
  {
    this.connection = connection;
    setLayout(new BorderLayout());
    setBackground(InterfaceGraficaUtils.COR_FUNDO);
    setBorder(new EmptyBorder(28, 28, 28, 28));
    add(criarPainelPrincipal(), BorderLayout.CENTER);
    limparDetalhes();
  }

  private JPanel criarPainelPrincipal()
  {
    JPanel painelExterno = new JPanel(new GridBagLayout());
    painelExterno.setOpaque(false);

    InterfaceGraficaUtils.RoundedPanel card = InterfaceGraficaUtils.criarCardArredondado(32,
        new BorderLayout(0, 18), InterfaceGraficaUtils.COR_CARD, new EmptyBorder(30, 30, 30, 30),
        new Dimension(1180, 720));

    card.add(InterfaceGraficaUtils.criarTopo("Actualizar Observação do Registro de Consumo",
        "<html>Carregue um registro existente e altere apenas a observação. O histórico principal da retirada permanece bloqueado.</html>",
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
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 18));
    painel.add(criarLinhaBusca(), BorderLayout.NORTH);
    painel.add(criarDetalhes(), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarLinhaBusca()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.LEFT, 10, 0));
    painel.add(InterfaceGraficaUtils.criarLabel("Código do registro:", InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO));

    fieldCodigoRc = criarCampoEditavel(12);
    painel.add(fieldCodigoRc);

    buttonCarregar = criarBotao("Carregar", 130);
    painel.add(buttonCarregar);
    return painel;
  }

  private JPanel criarDetalhes()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 16));

    JPanel grid = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    fieldCodigoFb = criarCampoReadonly(18);
    fieldCodigoPd = criarCampoReadonly(18);
    fieldCodigoCota = criarCampoReadonly(18);
    fieldDataHora = criarCampoReadonly(24);
    fieldVolumeRetirado = criarCampoReadonly(18);
    fieldPessoaColeta = criarCampoReadonly(24);
    fieldMetodoAutenticacao = criarCampoReadonly(18);
    fieldSaldoCota = criarCampoReadonly(18);
    fieldValidadeCota = criarCampoReadonly(18);
    fieldStatusCota = criarCampoReadonly(18);

    adicionarCampo(grid, 0, 0, "Família:", fieldCodigoFb);
    adicionarCampo(grid, 0, 2, "Ponto:", fieldCodigoPd);
    adicionarCampo(grid, 1, 0, "Cota:", fieldCodigoCota);
    adicionarCampo(grid, 1, 2, "Data da retirada:", fieldDataHora);
    adicionarCampo(grid, 2, 0, "Volume retirado:", fieldVolumeRetirado);
    adicionarCampo(grid, 2, 2, "Pessoa:", fieldPessoaColeta);
    adicionarCampo(grid, 3, 0, "Autenticação:", fieldMetodoAutenticacao);
    adicionarCampo(grid, 3, 2, "Saldo após retirada:", fieldSaldoCota);
    adicionarCampo(grid, 4, 0, "Validade da cota:", fieldValidadeCota);
    adicionarCampo(grid, 4, 2, "Estado da cota:", fieldStatusCota);

    painel.add(grid, BorderLayout.NORTH);
    painel.add(criarPainelObservacoes(), BorderLayout.CENTER);
    return painel;
  }

  private JPanel criarPainelObservacoes()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new GridBagLayout());
    areaObservacaoActual = criarAreaTexto(false);
    areaNovaObservacao = criarAreaTexto(true);

    adicionarArea(painel, 0, "Observação actual:", areaObservacaoActual);
    adicionarArea(painel, 1, "Nova observação:", areaNovaObservacao);
    return painel;
  }

  private JPanel criarRodape()
  {
    JPanel painel = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout());
    painel.add(InterfaceGraficaUtils.criarLabel("A actualização é permitida apenas enquanto a cota associada estiver válida.",
        InterfaceGraficaUtils.FONT_AJUDA, InterfaceGraficaUtils.COR_SUBTEXTO), BorderLayout.WEST);

    JPanel botoes = InterfaceGraficaUtils.criarPainelTransparente(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonActualizar = criarBotao("Actualizar Observação", 220);
    botoes.add(buttonActualizar);
    painel.add(botoes, BorderLayout.EAST);
    return painel;
  }

  private void adicionarCampo(JPanel painel, int linha, int coluna, String label, JTextField campo)
  {
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = coluna;
    gbc.gridy = linha;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(6, coluna == 0 ? 0 : 22, 6, 10);
    painel.add(InterfaceGraficaUtils.criarLabel(label, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = coluna + 1;
    gbc.gridy = linha;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(6, 0, 6, 0);
    painel.add(campo, gbc);
  }

  private void adicionarArea(JPanel painel, int coluna, String label, JTextArea area)
  {
    JPanel bloco = InterfaceGraficaUtils.criarPainelTransparente(new BorderLayout(0, 8));
    bloco.add(InterfaceGraficaUtils.criarLabel(label, InterfaceGraficaUtils.FONT_LABEL_FORM,
        InterfaceGraficaUtils.COR_TEXTO), BorderLayout.NORTH);
    bloco.add(criarScrollArea(area), BorderLayout.CENTER);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = coluna;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets(8, coluna == 0 ? 0 : 12, 0, coluna == 0 ? 12 : 0);
    painel.add(bloco, gbc);
  }

  private JTextField criarCampoEditavel(int colunas)
  {
    return InterfaceGraficaUtils.criarCampoTexto(colunas, InterfaceGraficaUtils.FONT_CAMPO, new Dimension(180, 36));
  }

  private JTextField criarCampoReadonly(int colunas)
  {
    JTextField field = InterfaceGraficaUtils.criarCampoTexto(colunas, InterfaceGraficaUtils.FONT_CAMPO,
        new Dimension(260, 36));
    field.setEditable(false);
    return field;
  }

  private JTextArea criarAreaTexto(boolean editavel)
  {
    JTextArea area = new JTextArea(7, 35);
    area.setFont(InterfaceGraficaUtils.FONT_CAMPO);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    area.setEditable(editavel);
    area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    return area;
  }

  private JScrollPane criarScrollArea(JTextArea area)
  {
    JScrollPane scroll = new JScrollPane(area);
    scroll.setPreferredSize(new Dimension(520, 160));
    scroll.setBorder(BorderFactory.createLineBorder(InterfaceGraficaUtils.COR_BORDA_TABELA));
    return scroll;
  }

  private JButton criarBotao(String texto, int largura)
  {
    JButton botao = InterfaceGraficaUtils.criarBotao(texto, InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(largura, 40));
    IconesInterface.aplicarIconeBotao(botao);
    botao.addActionListener(tratarButtons);
    return botao;
  }

  private void carregarRegistro()
  {
    int codigoRc;
    try
    {
      codigoRc = Integer.parseInt(fieldCodigoRc.getText().trim());
    } catch(NumberFormatException ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Informe um código de registro válido.");
      return;
    }

    try
    {
      RegistroConsumo registro = ActualizarObservacaoRegistroConsumoSQL.carregarRegistro(connection, codigoRc);
      registroCarregado = registro;
      observacaoOriginal = registro.observacaoRc;
      preencherDetalhes(registro);

      if(!registro.podeContinuar)
        JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(registro.mensagem));
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      limparDetalhes();
      JOptionPane.showMessageDialog(this, "Não foi possível carregar registro de consumo:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void actualizarObservacao()
  {
    if(registroCarregado == null)
    {
      JOptionPane.showMessageDialog(this, "Carregue um registro de consumo antes de actualizar.");
      return;
    }

    if(!registroCarregado.podeContinuar)
    {
      JOptionPane.showMessageDialog(this, "Actualização não permitida:\n" + MensagensInterface.formatarMensagem(registroCarregado.mensagem));
      return;
    }

    String novaObservacao = areaNovaObservacao.getText().trim();
    try
    {
      ResultadoActualizacao resultado = ActualizarObservacaoRegistroConsumoSQL.actualizarObservacao(connection,
          registroCarregado.codigoRc, observacaoOriginal, novaObservacao);

      if(!resultado.actualizado)
      {
        JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(resultado.mensagem));
        return;
      }

      JOptionPane.showMessageDialog(this, MensagensInterface.formatarMensagem(resultado.mensagem));
      carregarRegistro();
    } catch(Exception ex)
    {
      if(Resources.TratadorConexaoFechada.tratar(null, ex))
        return;

      JOptionPane.showMessageDialog(this, "Não foi possível actualizar observação:\n" + MensagensInterface.formatarErro(ex));
    }
  }

  private void preencherDetalhes(RegistroConsumo registro)
  {
    fieldCodigoFb.setText(valor(registro.codigoFb));
    fieldCodigoPd.setText(valor(registro.codigoPd));
    fieldCodigoCota.setText(valor(registro.codigoCota));
    fieldDataHora.setText(valor(registro.dataHoraRc));
    fieldVolumeRetirado.setText(valor(registro.volumeRetiradoRc));
    fieldPessoaColeta.setText(valor(registro.pessoaColetaRc));
    fieldMetodoAutenticacao.setText(valor(registro.metodoAutenticacaoRc));
    fieldSaldoCota.setText(valor(registro.saldoCotaRc));
    fieldValidadeCota.setText(valor(registro.periodoValidadeCa));
    fieldStatusCota.setText(valor(registro.statusValidade));
    areaObservacaoActual.setText(valor(registro.observacaoRc));
    areaNovaObservacao.setText(valor(registro.observacaoRc));
    areaNovaObservacao.setEnabled(registro.podeContinuar);
    buttonActualizar.setEnabled(registro.podeContinuar);
  }

  private void limparDetalhes()
  {
    registroCarregado = null;
    observacaoOriginal = null;

    JTextField[] campos = new JTextField[]
      {
          fieldCodigoFb, fieldCodigoPd, fieldCodigoCota, fieldDataHora, fieldVolumeRetirado, fieldPessoaColeta,
          fieldMetodoAutenticacao, fieldSaldoCota, fieldValidadeCota, fieldStatusCota
      };

    for(int i = 0; i < campos.length; i++)
      if(campos[i] != null)
        campos[i].setText("");

    if(areaObservacaoActual != null)
      areaObservacaoActual.setText("");
    if(areaNovaObservacao != null)
    {
      areaNovaObservacao.setText("");
      areaNovaObservacao.setEnabled(false);
    }
    if(buttonActualizar != null)
      buttonActualizar.setEnabled(false);
  }

  private String valor(Object valor)
  {
    return valor == null ? "" : valor.toString();
  }

  private final class TratarButtons implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      Object source = event.getSource();
      if(source == buttonCarregar)
        carregarRegistro();
      else if(source == buttonActualizar)
        actualizarObservacao();
    }
  }
}
