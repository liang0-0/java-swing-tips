// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.Border;

/**
 * JLayeredPane1.
 * @author Taka
 */
public final class MainPanel extends JPanel {
  private static final int BACK_LAYER = 1;
  // private static final int FORE_LAYER = 2;
  private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);
  private static final int[] COLORS = {
      0xDD_DD_DD, 0xAA_AA_FF, 0xFF_AA_AA, 0xAA_FF_AA, 0xFF_FF_AA, 0xFF_AA_FF, 0xAA_FF_FF
  };
  private final JLayeredPane layerPane;

  private MainPanel() {
    super(new BorderLayout());

    layerPane = new BackImageLayeredPane(new ImageIcon(getClass().getResource("tokeidai.jpg")).getImage());
    for (int i = 0; i < 7; i++) {
      JPanel p = createPanel(i);
      p.setLocation(i * 70 + 20, i * 50 + 15);
      layerPane.add(p, BACK_LAYER);
    }
    add(layerPane);
    setPreferredSize(new Dimension(320, 240));
  }

  private static Color getColor(int i, float f) {
    int r = (int) ((i >> 16 & 0xFF) * f);
    int g = (int) ((i >> 8 & 0xFF) * f);
    int b = (int) ((i & 0xFF) * f);
    return new Color(r, g, b);
  }

  private JPanel createPanel(int i) {
    String s = "<html><font color=#333333>ヘッダーだよん:" + i + "</font></html>";
    JLabel label = new JLabel(s);
    label.setFont(FONT);
    label.setOpaque(true);
    label.setHorizontalAlignment(SwingConstants.CENTER);
    label.setBackground(getColor(COLORS[i], .85f));
    Border border1 = BorderFactory.createEmptyBorder(4, 4, 4, 4);
    label.setBorder(border1);

    JTextArea text = new JTextArea();
    text.setMargin(new Insets(4, 4, 4, 4));
    text.setLineWrap(true);
    text.setOpaque(false);

    JPanel p = new JPanel();
    p.setOpaque(true);
    p.setBackground(new Color(COLORS[i]));

    Color col = getColor(COLORS[i], .5f);
    Border border = BorderFactory.createLineBorder(col, 1);
    p.setBorder(border);

    // ウインド移動用の処理
    DragMouseListener li = new DragMouseListener(layerPane);
    p.addMouseListener(li);
    p.addMouseMotionListener(li);

    p.setLayout(new BorderLayout());
    p.add(label, BorderLayout.NORTH);
    p.add(text);
    p.setSize(new Dimension(120, 100));
    return p;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

// タイトル部分のマウスクリックでパネルを最上位にもってくる。ドラッグで移動。
class DragMouseListener extends MouseAdapter {
  private final JLayeredPane parent;
  private final Point origin = new Point();

  protected DragMouseListener(JLayeredPane parent) {
    super();
    this.parent = parent;
  }

  @Override public void mousePressed(MouseEvent e) {
    JComponent panel = (JComponent) e.getComponent();
    origin.setLocation(e.getPoint());
    // 選択された部品を上へ
    parent.moveToFront(panel);
  }

  @Override public void mouseDragged(MouseEvent e) {
    JComponent panel = (JComponent) e.getComponent();
    // ずれた分だけ JPanel を移動させる
    int dx = e.getX() - origin.x;
    int dy = e.getY() - origin.y;
    Point pt = panel.getLocation();
    panel.setLocation(pt.x + dx, pt.y + dy);
  }
}

// 背景画像を描画する JLayeredPane
class BackImageLayeredPane extends JLayeredPane {
  private final Image bgImage;

  protected BackImageLayeredPane(Image img) {
    super();
    this.bgImage = img;
  }

  @Override public boolean isOptimizedDrawingEnabled() {
    return false;
  }

  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (Objects.nonNull(bgImage)) {
      int iw = bgImage.getWidth(this);
      int ih = bgImage.getHeight(this);
      Dimension d = getSize();
      for (int h = 0; h < d.getHeight(); h += ih) {
        for (int w = 0; w < d.getWidth(); w += iw) {
          g.drawImage(bgImage, w, h, this);
        }
      }
    }
  }
}
