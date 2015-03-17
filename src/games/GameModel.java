package games;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

import static games.Constants.LIKELIHOOD_OF_4;
import static games.Constants.WINNING_POWER_OF_2;

/**
 * Representation of the state of a game.
 */
public class GameModel {

  // The width and height of the game grid
  private final int gridSize;

  /*
     * A 1D representation of the log values of a corresponding 2D grid.
     * grid[n] gives the log value of co-ordinate (n%gridSize, n/gridSize)
     *
     * e.g. this array
     * [012030111]
     *
     * represents this 2D grid
     * 1 2 4
     * 1 8 1
     * 2 2 2
     */
  private byte[] grid;

  /**
   * Create a new game model with the specified gridSize.
   */
  public GameModel(int gridSize) {
    this(gridSize, new byte[gridSize * gridSize]);
    Arrays.fill(grid, (byte) -1);
  }

  private GameModel(int gridSize, byte[] grid) {
    this.gridSize = gridSize;
    this.grid = grid;
  }

  /**
   * Returns a copy of the given model.
   */
  public static GameModel copyOf(GameModel model) {
    byte[] gridCopy = Arrays.copyOf(model.getGrid(), model.getGrid().length);
    return new GameModel(model.gridSize, gridCopy);
  }

  /**
   * Returns the current state of the grid.
   */
  public byte[] getGrid() {
    return grid;
  }

  /**
   * Executes a move in the requested direction, updating the game state as necessary.
   *
   * @return true if a move was made.
   */
  public boolean executeMove(Direction direction) {
    switch (direction) {
      case UP:
        return moveUp(true);
      case DOWN:
        return moveDown(true);
      case LEFT:
        return moveLeft(true);
      case RIGHT:
        return moveRight(true);
    }
    return false;
  }

  private boolean moveRight(boolean doUpdate) {
    boolean anyUpdates = false;
    int step = 1;
    for (int i = grid.length - 1; i >= 0; i--) {
      byte currNumber = grid[i];
      int yCoord = i / gridSize;
      int nextIndexToCheck = i + step;
      // try to move all the way to the right side of the grid
      while (nextIndexToCheck / gridSize == yCoord
              && (grid[nextIndexToCheck] == -1 || grid[nextIndexToCheck] == currNumber)) {
        if (!doUpdate) {
          return true;
        }
        anyUpdates = true;
        grid[nextIndexToCheck - step] = -1;
        if (grid[nextIndexToCheck] == -1) {
          // empty, so just move the number
          grid[nextIndexToCheck] = currNumber;
        } else {
          // otherwise combine the numbers, and keep going up
          grid[nextIndexToCheck] += 1;
          currNumber += 1;
        }
        nextIndexToCheck += step;
      }
    }
    return anyUpdates;
  }

  private boolean moveLeft(boolean doUpdate) {
    boolean anyUpdates = false;
    int step = -1;
    for (int i = 0; i < grid.length; i++) {
      byte currNumber = grid[i];
      int yCoord = i / gridSize;
      int nextIndexToCheck = i + step;
      // try to move all the way to the left side of the grid
      while (nextIndexToCheck / gridSize == yCoord && nextIndexToCheck >= 0
              && (grid[nextIndexToCheck] == -1 || grid[nextIndexToCheck] == currNumber)) {
        if (!doUpdate) {
          return true;
        }
        anyUpdates = true;
        grid[nextIndexToCheck - step] = -1;
        if (grid[nextIndexToCheck] == -1) {
          // empty, so just move the number
          grid[nextIndexToCheck] = currNumber;
        } else {
          // otherwise combine the numbers, and keep going up
          grid[nextIndexToCheck] += 1;
          currNumber += 1;
        }
        nextIndexToCheck += step;
      }
    }
    return anyUpdates;
  }

  private boolean moveDown(boolean doUpdate) {
    boolean anyUpdates = false;
    int step = gridSize;
    for (int i = grid.length - 1; i >= 0; i--) {
      byte currNumber = grid[i];
      int nextIndexToCheck = i + step;
      // try to move all the way to the bottom of the grid
      while (nextIndexToCheck < grid.length
              && (grid[nextIndexToCheck] == -1 || grid[nextIndexToCheck] == currNumber)) {
        if (!doUpdate) {
          return true;
        }
        anyUpdates = true;
        grid[nextIndexToCheck - step] = -1;
        if (grid[nextIndexToCheck] == -1) {
          // empty, so just move the number
          grid[nextIndexToCheck] = currNumber;
        } else {
          // otherwise combine the numbers, and keep going up
          grid[nextIndexToCheck] += 1;
          currNumber += 1;
        }
        nextIndexToCheck += step;
      }
    }
    return anyUpdates;
  }

  private boolean moveUp(boolean doUpdate) {
    boolean anyUpdates = false;
    int step = -gridSize;
    for (int i = 0; i < grid.length; i++) {
      byte currNumber = grid[i];
      int nextIndexToCheck = i + step;
      // try to move all the way to the top of the grid
      while (nextIndexToCheck >= 0
              && (grid[nextIndexToCheck] == -1 || grid[nextIndexToCheck] == currNumber)) {
        if (!doUpdate) {
          return true;
        }
        anyUpdates = true;
        grid[nextIndexToCheck - step] = -1;
        if (grid[nextIndexToCheck] == -1) {
          // empty, so just move the number
          grid[nextIndexToCheck] = currNumber;
        } else {
          // otherwise combine the numbers, and keep going up
          grid[nextIndexToCheck] += 1;
          currNumber += 1;
        }
        nextIndexToCheck += step;
      }
    }
    return anyUpdates;
  }

  /**
   * Tries to add a new number to the grid according to the game rules.
   * Returns true if a number was added, i.e. the board was not full.
   */
  public boolean addNumber() {
    List<Integer> emptySpaces = Lists.newArrayList();
    for (int i = 0; i < grid.length; i++) {
      if (grid[i] == -1) {
        emptySpaces.add(Integer.valueOf(i));
      }
    }

    if (emptySpaces.isEmpty()) return false;

    int index = (int) (Math.random() * emptySpaces.size());
    byte numberToAdd = (byte) (Math.random() < LIKELIHOOD_OF_4 ? 2 : 1);
    grid[emptySpaces.get(index).intValue()] = numberToAdd;
    return true;
  }

  /**
   * Returns true if the winning number has been reached.
   */
  public boolean hasWon() {

    for (int aGrid : grid) {
      if (aGrid == WINNING_POWER_OF_2) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if there is still a move remaining.
   */
  public boolean isThereAValidMove() {
    return !isBoardFull() || moveUp(false) || moveDown(false)
            || moveLeft(false) || moveRight(false);
  }


  private boolean isBoardFull() {

    for (int aGrid : grid) {
      if (aGrid == -1) {
        return false;
      }
    }
    return true;
  }
}
