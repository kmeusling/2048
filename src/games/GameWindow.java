package games;

import static games.Constants.*;

import javax.swing.*;
import java.awt.*;

/**
 * A display that renders a 2048 game window.
 */
public class GameWindow extends JFrame {

  private static final int WINDOW_WIDTH = 600;
  private static final int WINDOW_HEIGHT = 600;
  private static final int GRID_WIDTH = 500;
  private static final int GRID_HEIGHT = 500;

  public static final int GRID_MIN_X = (WINDOW_WIDTH - GRID_WIDTH) / 2;
  public static final int GRID_MIN_Y = (WINDOW_HEIGHT - GRID_HEIGHT) / 2;
  public static final float GRID_SPACING_X = (float) GRID_WIDTH / GRID_SIZE;
  public static final float GRID_SPACING_Y = (float) GRID_WIDTH / GRID_SIZE;

  GamePanel gamePanel;
  Game gameManager;

  public GameWindow(Game gameManager) throws HeadlessException {
    super("2048");
    this.gameManager = gameManager;
    gamePanel = new GamePanel();
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

  public class GamePanel extends JPanel {

    @Override
    public Dimension getPreferredSize() {
      return new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawGrid(g);
      drawNumbers(gameManager.getGameModel().getGrid(), g);
      drawWinLoseLabels(gameManager.getGameState(), g);
    }

    private void drawGrid(Graphics g) {
      int gridMaxX = GRID_MIN_X + GRID_WIDTH;
      int gridMaxY = GRID_MIN_Y + GRID_HEIGHT;

      g.setFont(new Font("SansSerif", Font.BOLD, 28));

      int numVerticalLines = GRID_SIZE + 1;
      for (int i = 0; i < numVerticalLines; i++) {
        int xCoord = (int) (GRID_MIN_X + i * GRID_SPACING_X);
        g.drawLine(xCoord, GRID_MIN_Y, xCoord, gridMaxY);
      }

      int numHorizontalLines = GRID_SIZE + 1;
      for (int i = 0; i < numHorizontalLines; i++) {
        int yCoord = (int) (GRID_MIN_Y + i * GRID_SPACING_Y);
        g.drawLine(GRID_MIN_X, yCoord, gridMaxX, yCoord);
      }
    }

    private void drawNumbers(int[] grid, Graphics g) {
      for (int i = 0; i < grid.length; i++) {
        int logNumber = grid[i];
        if (logNumber >= 0) {
          drawNumberInGrid(logNumber, i % GRID_SIZE, i / GRID_SIZE, g);
        }
      }
    }

    private void drawNumberInGrid(int logNumber, int gridX, int gridY, Graphics g) {
      FontMetrics fm = g.getFontMetrics();
      String s = String.valueOf(1 << logNumber);

      int minX = (int) (GRID_MIN_X + gridX * GRID_SPACING_X);
      int x = (int) (minX + (GRID_SPACING_X - fm.stringWidth(s)) / 2);
      int minY = (int) (GRID_MIN_Y + gridY * GRID_SPACING_Y);
      int y = (int) (minY + fm.getAscent() + (GRID_SPACING_Y - fm.getHeight()) / 2);
      g.setColor(getColorForNumber(logNumber));
      g.drawString(s, x, y);
      g.setColor(Color.BLACK);
    }
  }

  private Color getColorForNumber(int n) {
    if (n <= 11 ) {
      int gComponent = 250 - (20 * n);
      return new Color(255, gComponent, 0);
    } else {
      return Color.BLACK;
    }
  }

  private void drawWinLoseLabels(Game.GameState state, Graphics g) {
    if (state == Game.GameState.WON) {
      g.drawString("Yay! You won :D Press space to retry", 20, 200);
    } else if (state == Game.GameState.LOST) {
      g.drawString("Boo! You lost :( Press space to retry", 20, 200);
    }
  }
}
