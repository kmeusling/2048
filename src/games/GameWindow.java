package games;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A display that renders a 2048 game window.
 */
@SuppressWarnings( "serial" )
public class GameWindow extends JFrame {

  public static final int WINDOW_WIDTH = 600;
  public static final int WINDOW_HEIGHT = 600;


  final GamePanel gamePanel;
  final Game gameManager;

  public GameWindow(Game gameManager) throws HeadlessException {
    super("2048");
    this.gameManager = gameManager;
    gamePanel = new GamePanel( this );
    initWindow();
  }

  private void initWindow() {
    setSize(WINDOW_WIDTH, WINDOW_HEIGHT + 50);
    setMaximumSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT + 50));
    setResizable(false);
    setLocationRelativeTo(null);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    JLabel instructionLabel = new JLabel(
        "Welcome to 2048! Use the arrow keys, or press b to toggle bot mode on/off");
    JPanel instructionPanel = new JPanel();
    instructionPanel.setLayout(new GridBagLayout());
    instructionPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, 50));
    instructionPanel.add(instructionLabel);
    add(instructionPanel);
    add(gamePanel);

    pack();
  }

}
