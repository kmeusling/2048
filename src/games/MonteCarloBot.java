package games;

import static java.lang.Math.max;

/**
 * Plays the game with another bot until completion for a fixed number of simulations for each possible move.
 * Picks the move in which the core bot performed the best on average.
 *
 * @author lpalm
 */
public class MonteCarloBot implements Bot {

  /**
   * For each potential next move, simulate this many games after the move is made.
   */
  private static final int NUM_SIMULATIONS = 3000;

  /**
   * Fraction of the score bonus for each empty cell on a board.
   */
  private static final float EMPTY_CELL_SCORE_BONUS = 0.5f;

  /**
   * Cut simulations short and score the grid after this many moves
   */
  private static final int MAX_MOVE_LOOKAHEAD = 8;

  /**
   * Score gradient array for the gradient scoring method.
   * see https://codemyroad.wordpress.com/2014/05/14/2048-ai-the-intelligent-bot/
   */
  private static final int[][] gradients = {
          {3, 2, 1, 0,
                  2, 1, 0, -1,
                  1, 0, -1, -2,
                  0, -1, -2, 3},
          {0, 1, 2, 3,
                  -1, 0, 1, 2,
                  -2, -1, 0, 1,
                  -3, -2, -1, 0},
          {0, -1, -2, -3,
                  1, 0, -1, -2,
                  2, 1, 0, -1,
                  3, 2, 1, 0},
          {-3, -2, -1, 0,
                  -2, -1, 0, 1,
                  -1, 0, 1, 2,
                  0, 1, 2, 3}
  };

  private final Bot coreBot;

  public static MonteCarloBot makeRandomBased() {

    return new MonteCarloBot(new RandomBot());
  }

  public MonteCarloBot(Bot coreBot) {
    this.coreBot = coreBot;
  }

  @Override
  public Direction getNextMove(GameModel model) {

    float bestScore = Float.NEGATIVE_INFINITY;
    Direction bestDirection = null;

    for (Direction direction : Direction.VALUES) {

      GameModel movedModel = GameModel.copyOf(model);
      if (!movedModel.executeMove(direction)) {
        continue;
      }
      movedModel.addNumber();

      float score = getAverageScore(movedModel);
//      float score = getBestScore(movedModel);
      if (score > bestScore) {
        bestScore = score;
        bestDirection = direction;
      }
    }
    return bestDirection;
  }

  /**
   * Runs the core bot for a number of simulations and returns the average score of the ending grids.
   */
  public float getAverageScore(GameModel startingState) {

    float totalScore = 0;
    for (int i = 0; i < NUM_SIMULATIONS; i++) {
      totalScore += simulateAndGetScore(GameModel.copyOf(startingState));
    }

    return totalScore / NUM_SIMULATIONS;
  }

  /**
   * Runs the core bot for a number of simulations and returns the average score of the ending grids.
   */
  public float getBestScore(GameModel startingState) {

    float bestScore = 0;
    for (int i = 0; i < NUM_SIMULATIONS; i++) {
      bestScore = max(bestScore, simulateAndGetScore(GameModel.copyOf(startingState)));
    }

    return bestScore;
  }

  /**
   * Runs the core bot until game completion and returns the score achived.
   * Modifies the input {@link GameModel).
   */
  private float simulateAndGetScore(GameModel model) {

    int numMoves = 0;
    while (model.isThereAValidMove() && numMoves < MAX_MOVE_LOOKAHEAD) {
      model.executeMove(coreBot.getNextMove(model));
      model.addNumber();
      numMoves++;
    }

//    return computeScore(model);
//    return computeGradientScore(model);
    return computeEmptyCellScore(model);
  }


  /**
   * Compute a grid score based on the sum of the values on each cell.
   */
  private static float computeScore(GameModel model) {

    float score = 0;
    int numEmptyCells = 0;
    for (byte b : model.getGrid()) {
      if (b < 0) {
        numEmptyCells++;
      } else {
        score += 1 << b;
      }
    }
    return score * (1 + numEmptyCells * EMPTY_CELL_SCORE_BONUS);
  }

  /**
   * Compute a grid score based on the number of empty cells on the board.
   */
  private static float computeEmptyCellScore(GameModel model) {

    int numEmptyCells = 0;
    for (byte b : model.getGrid()) {
      if (b < 0) {
        numEmptyCells++;
      }
    }
    return 1 << numEmptyCells;
  }

  /**
   * Compute a grid score based on the gradient method, a human heuristic.
   * NOTE: This seems to suck.
   */
  private static float computeGradientScore(GameModel model) {

    float bestScore = Float.NEGATIVE_INFINITY;
    int numEmptyCells = 0;
    for (int i = 0; i < 4; i++) {
      float score = 0;
      int[] gradientArray = gradients[i];
      for (int j = 0; j < model.getGrid().length; j++) {
        byte b = model.getGrid()[j];
        if (b < 0) {
          numEmptyCells++;
        } else {
          int gradient = gradientArray[j];
          score += (1 << b) * gradient;
        }
      }
      score *= (1 + numEmptyCells * EMPTY_CELL_SCORE_BONUS);
      bestScore = max(bestScore, score);
    }
    return bestScore;
  }
}
