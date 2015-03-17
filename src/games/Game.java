package games;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static games.Constants.BOT_DELAY_MS;
import static games.Constants.GRID_SIZE;

import javax.swing.Timer;

/**
 * A game manager that starts a game and responds to player actions.
 */
public class Game implements KeyListener {

  // The current running state of the game
  private GameState state = GameState.RUNNING;

  // The game display
  private GameWindow gameWindow;

  // The current state of the game
  private GameModel model;

  // Whether the game is currently running in bot mode or not
  private boolean botMode;

  // TODO: does this belong in the model as well?
  public enum GameState {
    RUNNING,
    WON,
    LOST,
  }

  public GameModel getGameModel() {
    return model;
  }

  public GameState getGameState() {
    return state;
  }

  public void runGame() {
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        gameWindow = new GameWindow(Game.this);
        gameWindow.addKeyListener(Game.this);
        gameWindow.setVisible(true);

        startNewGame();
      }
    });
  }

  private void startNewGame() {
    state = GameState.RUNNING;
    model = new GameModel(GRID_SIZE);
    model.addNumber();
    model.addNumber();
    gameWindow.repaint();

    if (botMode) {
      startBot();
    }
  }

  private void startBot() {
    final Bot bot = new SimpleBot();

    final Timer timer = new Timer(BOT_DELAY_MS, null);
    timer.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent actionEvent) {
        if (state == GameState.RUNNING && botMode) {
          executeAction(bot.getNextMove(GameModel.copyOf(model)));
        } else {
          timer.stop();
        }

      }
    });

    timer.start();
  }

  private void executeAction(Direction direction) {

    model.executeMove(direction);

    if (model.hasWon()) {
      state = GameState.WON;
      gameWindow.repaint();
    } else {
      gameWindow.repaint();
      model.addNumber();
      if (!model.isThereAValidMove()){
        state = GameState.LOST;
      }
      gameWindow.repaint();
    }
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    if (keyEvent.getKeyCode() == KeyEvent.VK_B) {
      botMode = !botMode;
      System.out.println("Bot mode: " + botMode);
      startBot();
    }
    if (state == GameState.RUNNING && !botMode) {
      switch (keyEvent.getKeyCode()) {
        case KeyEvent.VK_UP:
          executeAction(Direction.UP);
          break;
        case KeyEvent.VK_DOWN:
          executeAction(Direction.DOWN);
          break;
        case KeyEvent.VK_LEFT:
          executeAction(Direction.LEFT);
          break;
        case KeyEvent.VK_RIGHT:
          executeAction(Direction.RIGHT);
          break;
        default:
          break;
      }
    }

    if (state == GameState.WON || state == GameState.LOST) {
      switch (keyEvent.getKeyCode()) {
        case KeyEvent.VK_SPACE:
          startNewGame();
          break;
        default:
          break;
      }
    }
  }

  @Override
  public void keyTyped(KeyEvent keyEvent) {
    // Don't care
  }

  @Override
  public void keyReleased(KeyEvent keyEvent) {
    // Don't care
  }

  public static void main(String[] args) {
    Game game = new Game();
    game.runGame();
  }
}
