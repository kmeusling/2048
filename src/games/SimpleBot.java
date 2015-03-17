package games;

/**
 * A very basic bot that chooses a direction to move randomly.
 */
public class SimpleBot implements Bot {

  @Override
  public Direction getNextMove(GameModel model) {
    return Direction.values()[(int) (Math.random() * 4)];
  }
}
