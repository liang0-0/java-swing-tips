// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.net.URL;
import javax.accessibility.Accessible;
import javax.swing.*;
import javax.swing.plaf.basic.ComboPopup;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    URL url1 = getClass().getResource("favicon.png");
    URL url2 = getClass().getResource("animated.gif");

    JComboBox<Icon> combo = new JComboBox<>();
    combo.setModel(new DefaultComboBoxModel<>(new Icon[] {new ImageIcon(url1), makeImageIcon(url2, combo, 1)}));

    JPanel p = new JPanel(new GridLayout(4, 1, 5, 5));
    setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
    p.add(new JLabel("Default ImageIcon"));
    p.add(new JComboBox<>(new Icon[] {new ImageIcon(url1), new ImageIcon(url2)}));
    p.add(new JLabel("ImageIcon#setImageObserver(ImageObserver)"));
    p.add(combo);
    add(p, BorderLayout.NORTH);
    setPreferredSize(new Dimension(320, 240));
  }

  private static Icon makeImageIcon(URL url, JComboBox<?> combo, int row) {
    ImageIcon icon = new ImageIcon(url);
    // Wastefulness: icon.setImageObserver(combo);
    icon.setImageObserver((img, infoflags, x, y, w, h) -> {
      // @see http://www2.gol.com/users/tame/swing/examples/SwingExamples.html
      if (combo.isShowing() && (infoflags & (FRAMEBITS | ALLBITS)) != 0) {
        repaintComboBox(combo, row);
      }
      return (infoflags & (ALLBITS | ABORT)) == 0;
    });
    return icon;
  }

  protected static void repaintComboBox(JComboBox<?> combo, int row) {
    if (combo.getSelectedIndex() == row) {
      combo.repaint();
    }
    Accessible a = combo.getAccessibleContext().getAccessibleChild(0);
    if (a instanceof ComboPopup) {
      JList<?> list = ((ComboPopup) a).getList();
      if (list.isShowing()) {
        list.repaint(list.getCellBounds(row, row));
      }
    }
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
