package games;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

import static games.Constants.*;

/**
 * Representation of the state of a game.
 */
public class GameModel {

  // The width and height of the game grid
  private int gridSize;

  /*
     * A 1D representation of the log values of a corresponding 2D grid.
     * grid[n] gives the log value of co-ordinate (n%gridSize, n/gridSize)
     *
     * e.g. this array
     * [012000111]
     *
     * represents this 2D grid
     * 1 2 4
     * 1 1 1
     * 2 2 2
     */
  private int[] grid;

  /**
   * Create a new game model with the specified gridSize.
   */
  public GameModel(int gridSize) {
    this.gridSize = gridSize;
    this.grid = new int[gridSize * gridSize];
    Arrays.fill(grid, -1);
  }

  /**
   * Returns a copy of the given model.
   */
  public static GameModel copyOf(GameModel model) {
    GameModel clone = new GameModel(model.gridSize);
    clone.grid = Arrays.copyOf(model.getGrid(), model.getGrid().length);
    return clone;
  }

  /**
   * Returns the current state of the grid.
   */
  public int[] getGrid() {
    return grid;
  }

  /**
   * Executes a move in the requested direction, updating the game state as necessary.
   */
  public void executeMove(Direction direction) {
    switch (direction) {
      case UP:
        moveUp(false);
        break;
      case DOWN:
        moveDown(false);
        break;
      case LEFT:
        moveLeft(false);
        break;
      case RIGHT:
        moveRight(false);
        break;
    }
  }

  private boolean moveRight(boolean noUpdates) {
    boolean anyUpdates = false;
    for (int i = grid.length - 1; i >= 0; i--) {
      int currNumber = grid[i];
      int yCoord = i / gridSize;
      int nextIndexToCheck = i + 1;
      // try to move all the way to the right side of the grid
      while (nextIndexToCheck / gridSize == yCoord
          && (grid[nextIndexToCheck] == -1 || grid[nextIndexToCheck] == currNumber)) {
        if (noUpdates) {
          return true;
        }
        anyUpdates = true;
        grid[nextIndexToCheck - 1] = -1;
        if (grid[nextIndexToCheck] == -1) {
          // empty, so just move the number
          grid[nextIndexToCheck] = currNumber;
        } else {
          // otherwise combine the numbers, and keep going up
          grid[nextIndexToCheck] += 1;
          currNumber += 1;
        }
        nextIndexToCheck += 1;
      }
    }
    return anyUpdates;
  }

  private boolean moveLeft(boolean noUpdates) {
    boolean anyUpdates = false;
    for (int i = 0; i < grid.length; i++) {
      int currNumber = grid[i];
      int yCoord = i / gridSize;
      int nextIndexToCheck = i - 1;
      // try to move all the way to the left side of the grid
      while (nextIndexToCheck / gridSize == yCoord && nextIndexToCheck >= 0
          && (grid[nextIndexToCheck] == -1 || grid[nextIndexToCheck] == currNumber)) {
        if (noUpdates) {
          return true;
        }
        anyUpdates = true;
        grid[nextIndexToCheck + 1] = -1;
        if (grid[nextIndexToCheck] == -1) {
          // empty, so just move the number
          grid[nextIndexToCheck] = currNumber;
        } else {
          // otherwise combine the numbers, and keep going up
          grid[nextIndexToCheck] += 1;
          currNumber += 1;
        }
        nextIndexToCheck -= 1;
      }
    }
    return anyUpdates;
  }

  private boolean moveDown(boolean noUpdates) {
    boolean anyUpdates = false;
    for (int i = grid.length - 1; i >= 0; i--) {
      int currNumber = grid[i];
      int nextIndexToCheck = i + gridSize;
      // try to move all the way to the bottom of the grid
      while (nextIndexToCheck < grid.length
          && (grid[nextIndexToCheck] == -1 || grid[nextIndexToCheck] == currNumber)) {
        if (noUpdates) {
          return true;
        }
        anyUpdates = true;
        grid[nextIndexToCheck - gridSize] = -1;
        if (grid[nextIndexToCheck] == -1) {
          // empty, so just move the number
          grid[nextIndexToCheck] = currNumber;
        } else {
          // otherwise combine the numbers, and keep going up
          grid[nextIndexToCheck] += 1;
          currNumber += 1;
        }
        nextIndexToCheck += gridSize;
      }
    }
    return anyUpdates;
  }

  private boolean moveUp(boolean noUpdates) {
    boolean anyUpdates = false;
    for (int i = 0; i < grid.length; i++) {
      int currNumber = grid[i];
      int nextIndexToCheck = i - gridSize;
      // try to move all the way to the top of the grid
      while (nextIndexToCheck >= 0
          && (grid[nextIndexToCheck] == -1 || grid[nextIndexToCheck] == currNumber)) {
        if (noUpdates) {
          return true;
        }
        anyUpdates = true;
        grid[nextIndexToCheck + gridSize] = -1;
        if (grid[nextIndexToCheck] == -1) {
          // empty, so just move the number
          grid[nextIndexToCheck] = currNumber;
        } else {
          // otherwise combine the numbers, and keep going up
          grid[nextIndexToCheck] += 1;
          currNumber += 1;
        }
        nextIndexToCheck -= gridSize;
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
        emptySpaces.add(i);
      }
    }

    if (emptySpaces.isEmpty()) {
      return false;
    } else {
      int index = (int) (Math.random() * emptySpaces.size());
      int numberToAdd = Math.random() < LIKELIHOOD_OF_2 ? 1 : 0;
      grid[emptySpaces.get(index)] = numberToAdd;
      return true;
    }
  }

  /**
   * Returns true if the winning number has been reached.
   */
  public boolean hasWon() {
    for (int i = 0; i < grid.length; i++) {
      if (grid[i] == WINNING_POWER_OF_2) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns true if there is still a move remaining.
   */
  public boolean isThereAValidMove() {
    return !isBoardFull() || moveUp(true) || moveDown(true)
        || moveLeft(true) || moveRight(true);
  }

  private boolean isBoardFull() {
    for (int i = 0; i < grid.length; i++) {
      if (grid[i] == -1) {
        return false;
      }
    }
    return true;
  }
}
