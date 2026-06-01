package Login;

import Resources.InterfaceGraficaUtils;
import Connection.OracleConnection;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.concurrent.ExecutionException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

public class LoginSistema extends JFrame
{
  private static final Color COR_FUNDO_EXTERNO = new Color(8, 12, 16);
  private static final Color COR_ILHA = Color.WHITE;
  private static final Color COR_TITULO = new Color(25, 36, 48);
  private static final Color COR_AZUL = new Color(18, 142, 215);
  private static final Color COR_BORDA = new Color(214, 222, 230);
  private static final Color COR_ERRO = new Color(196, 45, 45);
  private static final Color COR_ESTADO = new Color(95, 108, 122);

  private JTextField campoUsername;
  private JPasswordField campoSenha;
  private JButton buttonLogin;
  private LoginButtonIcon iconLogin;
  private JLabel labelErro;
  private JLabel labelEstado;
  private JToggleButton toggleMostrarSenha;
  private final LoginService loginService = new LoginService();

  public static void main(String[] args)
  {
    configurarLookAndFeel();
    SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          new LoginSistema();
        }
      });
  }

  public LoginSistema()
  {
    super("Login");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setContentPane(criarConteudo());
    pack();
    setMinimumSize(new Dimension(980, 800));
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private JPanel criarConteudo()
  {
    JPanel painel = new JPanel(new GridBagLayout());
    painel.setOpaque(true);
    painel.setBackground(COR_FUNDO_EXTERNO);
    painel.setBorder(BorderFactory.createEmptyBorder(34, 42, 44, 42));
    painel.setPreferredSize(new Dimension(1000, 830));

    JPanel blocoCentral = new JPanel();
    blocoCentral.setOpaque(false);
    blocoCentral.setLayout(new BoxLayout(blocoCentral, BoxLayout.Y_AXIS));
    blocoCentral.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel labelLogo = new JLabel(carregarLogoEscalado(690, 197));
    labelLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

    RoundedPanel ilhaFormulario = new RoundedPanel(30, COR_ILHA);
    ilhaFormulario.setLayout(new GridBagLayout());
    ilhaFormulario.setBorder(new EmptyBorder(34, 48, 34, 48));
    ilhaFormulario.setAlignmentX(Component.CENTER_ALIGNMENT);
    ilhaFormulario.setPreferredSize(new Dimension(560, 430));
    ilhaFormulario.setMinimumSize(new Dimension(560, 430));
    ilhaFormulario.setMaximumSize(new Dimension(560, 430));

    JPanel formularioCompleto = criarFormularioCompleto();
    GridBagConstraints ilhaGbc = new GridBagConstraints();
    ilhaGbc.gridx = 0;
    ilhaGbc.gridy = 0;
    ilhaGbc.anchor = GridBagConstraints.CENTER;
    ilhaFormulario.add(formularioCompleto, ilhaGbc);

    blocoCentral.add(labelLogo);
    blocoCentral.add(Box.createVerticalStrut(26));
    blocoCentral.add(ilhaFormulario);

    painel.add(blocoCentral);
    return painel;
  }

  private JPanel criarFormularioCompleto()
  {
    JPanel painel = new JPanel();
    painel.setOpaque(false);
    painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
    painel.setPreferredSize(new Dimension(470, 350));
    painel.setMaximumSize(new Dimension(470, 350));

    JLabel titulo = new JLabel("Bem-vindo de volta!");
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
    titulo.setForeground(COR_TITULO);
    titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
    titulo.setHorizontalAlignment(SwingConstants.CENTER);

    JPanel formulario = criarFormulario();
    formulario.setAlignmentX(Component.CENTER_ALIGNMENT);

    buttonLogin = new JButton("Login");
    buttonLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
    buttonLogin.setBackground(COR_AZUL);
    buttonLogin.setForeground(Color.WHITE);
    buttonLogin.setFocusPainted(false);
    buttonLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
    buttonLogin.setPreferredSize(new Dimension(180, 48));
    buttonLogin.setMinimumSize(new Dimension(180, 48));
    InterfaceGraficaUtils.aplicarCantosArredondados(buttonLogin);

    iconLogin = new LoginButtonIcon(false);
    buttonLogin.setIcon(iconLogin);
    buttonLogin.setIconTextGap(8);
    buttonLogin.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseEntered(MouseEvent event)
      {
        if(buttonLogin.isEnabled())
        {
          iconLogin.setHover(true);
          buttonLogin.repaint();
        }
      }

      @Override
      public void mouseExited(MouseEvent event)
      {
        iconLogin.setHover(false);
        buttonLogin.repaint();
      }
    });
    buttonLogin.addActionListener(event -> autenticar());

    JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
    painelBotao.setOpaque(false);
    painelBotao.setAlignmentX(Component.CENTER_ALIGNMENT);
    painelBotao.setPreferredSize(new Dimension(430, 54));
    painelBotao.setMaximumSize(new Dimension(430, 54));
    painelBotao.add(buttonLogin);

    labelEstado = new JLabel(" ");
    labelEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    labelEstado.setForeground(COR_ESTADO);
    labelEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
    labelEstado.setHorizontalAlignment(SwingConstants.CENTER);
    labelEstado.setPreferredSize(new Dimension(430, 22));
    labelEstado.setMaximumSize(new Dimension(430, 22));

    painel.add(titulo);
    painel.add(Box.createVerticalStrut(28));
    painel.add(formulario);
    painel.add(Box.createVerticalStrut(12));
    painel.add(painelBotao);
    painel.add(Box.createVerticalStrut(10));
    painel.add(labelEstado);

    return painel;
  }

  private JPanel criarFormulario()
  {
    JPanel formulario = new JPanel(new GridBagLayout());
    formulario.setOpaque(false);
    formulario.setPreferredSize(new Dimension(430, 214));
    formulario.setMinimumSize(new Dimension(430, 214));
    formulario.setMaximumSize(new Dimension(430, 214));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(0, 0, 8, 0);

    formulario.add(criarLabel("Username"), gbc);

    campoUsername = new JTextField();
    configurarCampo(campoUsername);
    gbc.gridy++;
    gbc.insets = new Insets(0, 0, 16, 0);
    formulario.add(campoUsername, gbc);

    gbc.gridy++;
    gbc.insets = new Insets(0, 0, 8, 0);
    formulario.add(criarLabel("Password"), gbc);

    JPanel painelSenha = new JPanel(new BorderLayout(8, 0));
    painelSenha.setOpaque(false);
    campoSenha = new JPasswordField();
    configurarCampo(campoSenha);
    campoSenha.setEchoChar('•');
    painelSenha.add(campoSenha, BorderLayout.CENTER);

    toggleMostrarSenha = criarToggleSenha();
    painelSenha.add(toggleMostrarSenha, BorderLayout.EAST);

    gbc.gridy++;
    gbc.insets = new Insets(0, 0, 8, 0);
    formulario.add(painelSenha, gbc);

    labelErro = new JLabel(" ");
    labelErro.setFont(new Font("Segoe UI", Font.BOLD, 12));
    labelErro.setForeground(COR_ERRO);
    labelErro.setHorizontalAlignment(SwingConstants.LEFT);
    labelErro.setPreferredSize(new Dimension(430, 30));
    labelErro.setMinimumSize(new Dimension(430, 30));

    gbc.gridy++;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.insets = new Insets(0, 0, 0, 0);
    formulario.add(labelErro, gbc);

    return formulario;
  }

  private void configurarCampo(JTextField campo)
  {
    campo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
    campo.setPreferredSize(new Dimension(398, 46));
    campo.setMinimumSize(new Dimension(398, 46));
    campo.setBorder(new RoundedLineBorder(COR_BORDA, 12, 12));
  }

  private JToggleButton criarToggleSenha()
  {
    JToggleButton botao = new JToggleButton();
    botao.setSelected(false);
    botao.setFocusable(false);
    botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    botao.setPreferredSize(new Dimension(48, 46));
    botao.setMinimumSize(new Dimension(48, 46));
    botao.setBackground(COR_ILHA);
    botao.setBorder(new RoundedLineBorder(COR_BORDA, 12, 12));
    botao.setIcon(FontIcon.of(FontAwesomeSolid.EYE, 16, COR_AZUL));
    botao.setSelectedIcon(FontIcon.of(FontAwesomeSolid.EYE_SLASH, 16, COR_AZUL));
    botao.addActionListener(event -> alternarVisibilidadeSenha());
    return botao;
  }

  private void alternarVisibilidadeSenha()
  {
    if(toggleMostrarSenha.isSelected())
      campoSenha.setEchoChar((char) 0);
    else
      campoSenha.setEchoChar('•');
  }

  private JLabel criarLabel(String texto)
  {
    JLabel label = new JLabel(texto);
    label.setFont(new Font("Segoe UI", Font.BOLD, 13));
    label.setForeground(COR_AZUL);
    return label;
  }

  private ImageIcon carregarLogoEscalado(int larguraMaxima, int alturaMaxima)
  {
    try
    {
      File ficheiro = new File("logo.png");
      if(!ficheiro.exists())
        ficheiro = new File("./ThisProject/logo.png");

      BufferedImage imagem = ImageIO.read(ficheiro);
      if(imagem == null)
        throw new IllegalStateException("Logo não encontrada.");

      double escala = Math.min((double) larguraMaxima / imagem.getWidth(), (double) alturaMaxima / imagem.getHeight());
      int largura = Math.max(1, (int) Math.round(imagem.getWidth() * escala));
      int altura = Math.max(1, (int) Math.round(imagem.getHeight() * escala));

      return criarImagemArredondada(imagem, largura, altura, 40);
    } catch(Exception ex)
    {
      BufferedImage fallback = new BufferedImage(larguraMaxima, alturaMaxima, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = fallback.createGraphics();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(COR_FUNDO_EXTERNO);
      g2.fillRect(0, 0, larguraMaxima, alturaMaxima);
      g2.setColor(Color.WHITE);
      g2.setFont(new Font("Segoe UI", Font.BOLD, 34));
      FontMetrics fm = g2.getFontMetrics();
      String texto = "SISTEMA DISTRIBUIDO DE AGUA";
      int x = (larguraMaxima - fm.stringWidth(texto)) / 2;
      int y = (alturaMaxima + fm.getAscent()) / 2 - 8;
      g2.drawString(texto, Math.max(x, 12), Math.max(y, 40));
      g2.dispose();
      return new ImageIcon(fallback);
    }
  }

  private ImageIcon criarImagemArredondada(BufferedImage imagemOriginal, int largura, int altura, int raio)
  {
    BufferedImage imagemArredondada = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = imagemArredondada.createGraphics();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setClip(new RoundRectangle2D.Float(0, 0, largura, altura, raio, raio));
    g2.drawImage(imagemOriginal, 0, 0, largura, altura, null);
    g2.dispose();

    return new ImageIcon(imagemArredondada);
  }

  private void autenticar()
  {
    limparMensagemErro();

    String username = campoUsername.getText().trim();
    String senha = new String(campoSenha.getPassword());

    if(username.length() == 0 || senha.length() == 0)
    {
      mostrarMensagemErro("Informe username e password.");
      return;
    }

    setAutenticacaoEmCurso(true);

    SwingWorker<ResultadoLogin, Void> worker = new SwingWorker<ResultadoLogin, Void>()
    {
      @Override
      protected ResultadoLogin doInBackground() throws Exception
      {
        return loginService.autenticar(username, senha);
      }

      @Override
      protected void done()
      {
        try
        {
          ResultadoLogin resultado = get();
          abrirJanelaPrincipal(resultado);
          dispose();
        } catch(Exception ex)
        {
          mostrarMensagemErro(mensagemErro(ex));
          setAutenticacaoEmCurso(false);
        }
      }
    };
    worker.execute();
  }

  private void mostrarMensagemErro(String mensagem)
  {
    if(mensagem == null || mensagem.trim().length() == 0)
      mensagem = "Não foi possível efectuar login.";

    labelErro.setText("<html><div style='text-align:center; width:350px;'>" + escaparHtml(mensagem) + "</div></html>");
    labelEstado.setText(" ");
  }

  private void limparMensagemErro()
  {
    labelErro.setText(" ");
  }

  private String escaparHtml(String texto)
  {
    return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
  }

  private void setAutenticacaoEmCurso(boolean emCurso)
  {
    buttonLogin.setEnabled(!emCurso);
    campoUsername.setEnabled(!emCurso);
    campoSenha.setEnabled(!emCurso);
    toggleMostrarSenha.setEnabled(!emCurso);
    setCursor(Cursor.getPredefinedCursor(emCurso ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
    labelEstado.setText(emCurso ? "A autenticar..." : " ");
    if(emCurso)
      limparMensagemErro();
  }

  private String mensagemErro(Exception ex)
  {
    if(ex instanceof InterruptedException)
    {
      Thread.currentThread().interrupt();
      return "A autenticacao foi interrompida. Tente novamente.";
    }

    Throwable causa = ex;
    if(ex instanceof ExecutionException && ex.getCause() != null)
      causa = ex.getCause();

    String mensagem = causa.getMessage();
    if(mensagem == null || mensagem.trim().length() == 0)
      return "Não foi possível conectar ao sistema. Tente novamente.";
    return mensagem;
  }

  private void abrirJanelaPrincipal(ResultadoLogin resultado)
  {
    if(resultado.getNo() == OracleConnection.NoBD.ADMINISTRADOR)
    {
      new View_Interface_AdministradorDB.Janela_Principal(resultado.getConnection(), resultado.getCodigoSessaoLocal());
      return;
    }

    if(resultado.getNo() == OracleConnection.NoBD.FAMILIAS_COTAS)
    {
      new View_Interface_FamiliasCotasDB.Janela_Principal(resultado.getConnection(), resultado.getCodigoSessaoLocal(),
          resultado.getUsernameOracle(), resultado.getCodigoNo());
      return;
    }

    if(resultado.getNo() == OracleConnection.NoBD.DISTRIBUICAO_CONSUMO)
    {
      new View_Interface_DistribuicaoConsumoDB.Janela_Principal(resultado.getConnection(),
          resultado.getCodigoSessaoLocal(), resultado.getUsernameOracle(), resultado.getCodigoNo());
      return;
    }

    if(resultado.getNo() == OracleConnection.NoBD.TRANSFERENCIAS_RECURSOS)
    {
      new View_Interface_TransferenciasRecursosDB.Janela_Principal(resultado.getConnection(),
          resultado.getCodigoSessaoLocal(), resultado.getUsernameOracle(), resultado.getCodigoNo());
      return;
    }

    if(resultado.getNo() == OracleConnection.NoBD.EQUIPES_GESTAO)
    {
      new View_Interface_EquipesGestaoDB.Janela_Principal(resultado.getConnection(), resultado.getCodigoSessaoLocal(),
          resultado.getUsernameOracle(), resultado.getCodigoNo());
      return;
    }

    OracleConnection.closeConnection(resultado.getConnection());
    throw new IllegalArgumentException("No desconhecido: " + resultado.getNo());
  }

  private static void configurarLookAndFeel()
  {
    try
    {
      FlatLightLaf.setup();
      UIManager.put("Component.hideMnemonics", Boolean.FALSE);
      InterfaceGraficaUtils.configurarTemaAplicacao();
    } catch(Exception ex)
    {
      try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("Component.hideMnemonics", Boolean.FALSE);
        InterfaceGraficaUtils.configurarTemaAplicacao();
      } catch(Exception ignored)
      {}
    }
  }

  private static final class LoginButtonIcon implements Icon
  {
    private boolean hover;

    private LoginButtonIcon(boolean hover)
    {
      this.hover = hover;
    }

    private void setHover(boolean hover)
    {
      this.hover = hover;
    }

    public int getIconWidth()
    {
      return hover ? 19 : 16;
    }

    public int getIconHeight()
    {
      return hover ? 19 : 16;
    }

    public void paintIcon(Component c, Graphics g, int x, int y)
    {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

      int size = hover ? 18 : 15;
      int left = x + ((getIconWidth() - size) / 2);
      int top = y + ((getIconHeight() - size) / 2);

      g2.setColor(Color.WHITE);

      if(hover)
      {
        int[] arrowX = {
            left + 6, left + 12, left + 6, left + 6, left + 0, left + 0, left + 6
        };
        int[] arrowY = {
            top + 1, top + 8, top + 15, top + 11, top + 11, top + 5, top + 5
        };
        g2.fillPolygon(arrowX, arrowY, arrowX.length);

        g2.fillRoundRect(left + 12, top + 3, 5, 11, 2, 2);
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(left + 12, top + 6, 2, 5);
      }
      else
      {
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2.drawLine(left + 1, top + 8, left + 10, top + 8);
        g2.drawLine(left + 7, top + 4, left + 11, top + 8);
        g2.drawLine(left + 7, top + 12, left + 11, top + 8);

        g2.drawLine(left + 12, top + 3, left + 15, top + 3);
        g2.drawLine(left + 15, top + 3, left + 15, top + 13);
        g2.drawLine(left + 15, top + 13, left + 12, top + 13);
      }

      g2.dispose();
    }
  }

  private static final class RoundedPanel extends JPanel
  {
    private final int arc;
    private final Color corFundo;

    private RoundedPanel(int arc, Color corFundo)
    {
      this.arc = arc;
      this.corFundo = corFundo;
      setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(corFundo);
      g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
      g2.dispose();
      super.paintComponent(g);
    }
  }

  private static final class RoundedLineBorder extends AbstractBorder
  {
    private final Color cor;
    private final int espessura;
    private final int arc;

    private RoundedLineBorder(Color cor, int espessura, int arc)
    {
      this.cor = cor;
      this.espessura = espessura;
      this.arc = arc;
    }

    @Override
    public Insets getBorderInsets(Component c)
    {
      return new Insets(espessura + 1, 12, espessura + 1, 12);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets)
    {
      insets.left = 12;
      insets.right = 12;
      insets.top = espessura + 1;
      insets.bottom = espessura + 1;
      return insets;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
      Graphics2D g2 = (Graphics2D) g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(cor);
      g2.setStroke(new BasicStroke(1f));
      g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
      g2.dispose();
    }
  }
}
