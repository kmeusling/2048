package games;

/**
 * An interface for a Bot to play this game.
 */
public interface Bot {

  /**
   * Returns the desired direction for the next move based on the given model,
   * which should be a copy of the game state.
   */
  public Direction getNextMove(GameModel model);
}
