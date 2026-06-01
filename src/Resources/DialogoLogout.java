package Resources;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class DialogoLogout
{
  private DialogoLogout()
  {}

  public static boolean confirmar(Component parent, String mensagem)
  {
    Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
    final JDialog dialog = new JDialog(owner, "Logout", Dialog.ModalityType.APPLICATION_MODAL);
    final boolean[] resposta =
      {
          false
      };

    JPanel conteudo = new JPanel(new BorderLayout(12, 18));
    conteudo.setBackground(Color.WHITE);
    conteudo.setBorder(new EmptyBorder(22, 24, 18, 24));

    JLabel labelMensagem = new JLabel(mensagem == null ? "Deseja terminar a sessão actual?" : mensagem);
    labelMensagem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    labelMensagem.setForeground(new Color(40, 50, 60));
    labelMensagem.setHorizontalAlignment(SwingConstants.CENTER);

    JButton buttonSim = InterfaceGraficaUtils.criarBotao("Sim", InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(96, 36));
    JButton buttonNao = InterfaceGraficaUtils.criarBotao("Não", InterfaceGraficaUtils.FONT_BOTAO,
        InterfaceGraficaUtils.COR_AZUL, InterfaceGraficaUtils.COR_BRANCO, new Dimension(96, 36));

    aplicarIconeHover(buttonSim, new IconeLogout(true));
    aplicarIconeHover(buttonNao, new IconeLogout(false));

    buttonSim.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          resposta[0] = true;
          dialog.dispose();
        }
      });

    buttonNao.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          resposta[0] = false;
          dialog.dispose();
        }
      });

    JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    painelBotoes.setOpaque(false);
    painelBotoes.add(buttonSim);
    painelBotoes.add(buttonNao);

    conteudo.add(labelMensagem, BorderLayout.CENTER);
    conteudo.add(painelBotoes, BorderLayout.SOUTH);

    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    dialog.setContentPane(conteudo);
    dialog.setResizable(false);
    dialog.pack();
    dialog.setSize(new Dimension(370, 150));
    dialog.setLocationRelativeTo(parent);
    dialog.setVisible(true);

    return resposta[0];
  }

  private static void aplicarIconeHover(final JButton button, final IconeLogout icone)
  {
    button.setIcon(icone);
    button.setIconTextGap(8);

    button.addMouseListener(new MouseAdapter()
      {
        public void mouseEntered(MouseEvent e)
        {
          if(button.isEnabled())
          {
            icone.setHover(true);
            button.repaint();
          }
        }

        public void mouseExited(MouseEvent e)
        {
          icone.setHover(false);
          button.repaint();
        }
      });
  }

  private static final class IconeLogout implements Icon
  {
    private final boolean sair;
    private boolean hover;

    private IconeLogout(boolean sair)
    {
      this.sair = sair;
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
      g2.setColor(Color.WHITE);

      int size = hover ? 18 : 15;
      int left = x + ((getIconWidth() - size) / 2);
      int top = y + ((getIconHeight() - size) / 2);

      if(sair)
        desenharSair(g2, left, top);
      else
        desenharFicar(g2, left, top);

      g2.dispose();
    }

    private void desenharSair(Graphics2D g2, int left, int top)
    {
      if(hover)
      {
        g2.fillRoundRect(left + 1, top + 2, 7, 13, 2, 2);

        int[] setaX =
          {
              left + 8, left + 16, left + 8, left + 8, left + 4, left + 4, left + 8
          };
        int[] setaY =
          {
              top + 2, top + 8, top + 14, top + 11, top + 11, top + 5, top + 5
          };
        g2.fillPolygon(setaX, setaY, setaX.length);
      }
      else
      {
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawRoundRect(left + 1, top + 2, 7, 12, 2, 2);
        g2.drawLine(left + 5, top + 8, left + 14, top + 8);
        g2.drawLine(left + 11, top + 5, left + 14, top + 8);
        g2.drawLine(left + 11, top + 11, left + 14, top + 8);
      }
    }

    private void desenharFicar(Graphics2D g2, int left, int top)
    {
      if(hover)
      {
        int[] setaX =
          {
              left + 6, left + 1, left + 6, left + 6, left + 13, left + 13, left + 6
          };
        int[] setaY =
          {
              top + 1, top + 7, top + 13, top + 10, top + 10, top + 4, top + 4
          };
        g2.fillPolygon(setaX, setaY, setaX.length);
        g2.fillRoundRect(left + 11, top + 3, 5, 11, 2, 2);
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(left + 11, top + 6, 2, 5);
      }
      else
      {
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(left + 14, top + 8, left + 5, top + 8);
        g2.drawLine(left + 8, top + 5, left + 5, top + 8);
        g2.drawLine(left + 8, top + 11, left + 5, top + 8);

        g2.drawLine(left + 2, top + 3, left + 5, top + 3);
        g2.drawLine(left + 2, top + 3, left + 2, top + 13);
        g2.drawLine(left + 2, top + 13, left + 5, top + 13);
      }
    }
  }
}
