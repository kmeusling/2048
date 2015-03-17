package games;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import static games.Constants.GRID_SIZE;
import static games.GameWindow.WINDOW_HEIGHT;
import static games.GameWindow.WINDOW_WIDTH;
import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;


/**
 * {@link Graphics} based renderer for the game grid.
 *
 * @author lpalm
 */
@SuppressWarnings("serial")
public class GamePanel
        extends JPanel {

  private static final int GRID_WIDTH = 500;

  private static final int GRID_HEIGHT = 500;

  private static final int GRID_MIN_X = (WINDOW_WIDTH - GRID_WIDTH) / 2;

  private static final int GRID_MIN_Y = (WINDOW_HEIGHT - GRID_HEIGHT) / 2;

  // The actual distance between cells will be twice the padding.
  private static final float CELL_PADDING_X = 6;

  private static final float CELL_PADDING_Y = 6;

  private static final float CELL_MIN_X = GRID_MIN_X + CELL_PADDING_X;

  private static final float CELL_MIN_Y = GRID_MIN_Y + CELL_PADDING_Y;

  private static final float CELL_CHAMFER_X = 20;

  private static final float CELL_CHAMFER_Y = 20;

  private static final float GRID_SPACING_X = (GRID_WIDTH - 2 * CELL_PADDING_X) / GRID_SIZE;

  private static final float GRID_SPACING_Y = (GRID_WIDTH - 2 * CELL_PADDING_Y) / GRID_SIZE;

  private static final Color EMPTY_CELL_COLOR = new Color(204, 192, 179);

  private static final Color GRID_COLOR = new Color(187, 173, 160);

  private final GameWindow gameWindow;


  public GamePanel(GameWindow gameWindow) {

    this.gameWindow = gameWindow;
  }


  private static void drawNumberInGrid(byte logNumber, int gridX, int gridY, Graphics g) {

    if (logNumber < 0) return;

    g.setFont(new Font("Helvetica Neue", Font.BOLD, getFontSize(logNumber)));

    FontMetrics fm = g.getFontMetrics();
    String s = String.valueOf(1 << logNumber);

    int minX = (int) (CELL_MIN_X + gridX * GRID_SPACING_X);
    int x = (int) (minX + (GRID_SPACING_X - fm.stringWidth(s)) / 2);
    int minY = (int) (CELL_MIN_Y + gridY * GRID_SPACING_Y);
    int y = (int) (minY + fm.getAscent() + (GRID_SPACING_Y - fm.getHeight()) / 2);
    g.setColor(getColorForNumber(logNumber));
    g.drawString(s, x, y);
    g.setColor(BLACK);
  }


  private static int getFontSize(byte logNumber) {

    if (logNumber < 7) return 48; // 1-64
    if (logNumber < 11) return 36; // 128-512
    if (logNumber < 14) return 28; // 4 digits
    return 22; // 5 digits+
  }


  private static Color getColorForNumber(byte logNumber) {

    return logNumber <= 5 ? BLACK : WHITE;
  }


  private static Color getColorForCell(byte logNumber) {

    if (logNumber < 0) {
      return EMPTY_CELL_COLOR;
    }

    if (logNumber <= 11) {
      int gComponent = 250 - (25 * logNumber);
      return new Color(255, gComponent, 0);
    } else {
      return BLACK;
    }
  }


  private static void drawWinLoseLabels(Game.GameState state, Graphics g) {

    g.setFont(new Font("Helvetica Neue", Font.BOLD, 18));

    if (state == Game.GameState.WON) {
      g.drawString("Yay! You won :D Press space to retry", 150, 20);
    } else if (state == Game.GameState.LOST) {
      g.drawString("Boo! You lost :( Press space to retry", 150, 20);
    }
  }


  private static void drawCells(byte[] grid, Graphics g) {

    for (int i = 0; i < grid.length; i++) {
      byte logNumber = grid[i];
      drawCell(logNumber, i % GRID_SIZE, i / GRID_SIZE, g);
    }
  }


  private static void drawCell(byte logNumber, int x, int y, Graphics g) {

    Graphics2D g2 = (Graphics2D) g;
    g2.setPaint(getColorForCell(logNumber));

    float cellX = CELL_MIN_X + CELL_PADDING_X + x * GRID_SPACING_X;
    float cellY = CELL_MIN_Y + CELL_PADDING_Y + y * GRID_SPACING_Y;
    float cellWidth = GRID_SPACING_X - 2 * CELL_PADDING_X;
    float cellHeight = GRID_SPACING_Y - 2 * CELL_PADDING_Y;
    g2.fill(new RoundRectangle2D.Double(cellX, cellY, cellWidth, cellHeight, CELL_CHAMFER_X, CELL_CHAMFER_Y));

    drawNumberInGrid(logNumber, x, y, g);
  }


  @Override
  public Dimension getPreferredSize() {

    return new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT);
  }


  @Override
  public void paintComponent(Graphics g) {

    super.paintComponent(g);
    Game gameManager = gameWindow.gameManager;
    drawBackground(g);
    drawCells(gameManager.getGameModel().getGrid(), g);
    drawWinLoseLabels(gameManager.getGameState(), g);
  }


  private static void drawBackground(Graphics g) {

    Graphics2D g2 = (Graphics2D) g;
    g2.setPaint(GRID_COLOR);
    g2.fill(new RoundRectangle2D.Double((float) GRID_MIN_X, (float) GRID_MIN_Y, (float) GRID_WIDTH, (float) GRID_HEIGHT,
            CELL_CHAMFER_X, CELL_CHAMFER_Y));
  }
}
