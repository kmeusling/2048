package games;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A very basic bot that chooses a direction to move randomly.
 */
public class RandomBot implements Bot {

  @Override
  public Direction getNextMove(GameModel model) {
    ThreadLocalRandom rng = ThreadLocalRandom.current();
    return Direction.values()[rng.nextInt(4)];
  }
}
