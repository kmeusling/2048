package games;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Runs a bot for a fixed number of games, collecting statistics on its performance.
 *
 * @author lpalm
 */
public class BotEvaluator {

  private static final int NUM_GAMES = 5;

  public static void main(String[] args) {

    Bot bot = MonteCarloBot.makeRandomBased();

    float totalScore = 0;
    int maxScore = Integer.MIN_VALUE;
    int minScore = Integer.MAX_VALUE;
    int[] gamesByHighestCell = new int[16];
    long startMillis = System.currentTimeMillis();

    for (int i = 0; i < NUM_GAMES; i++) {
      GameModel model = new GameModel(4);
      model.addNumber();
      model.addNumber();

      int numMoves = 0;
      while (model.isThereAValidMove()) {
        Direction nextMove = bot.getNextMove(GameModel.copyOf(model));
        model.executeMove(nextMove);
        model.addNumber();
        if (++numMoves % 250 == 0) {
          System.out.println("Game: " + i + " Moves: " + numMoves);
        }
      }
      int score = model.getScore();
      totalScore += score;
      minScore = min(minScore, score);
      maxScore = max(maxScore, score);

      gamesByHighestCell[model.getHighestCell()]++;
    }
    long timeMillis = System.currentTimeMillis() - startMillis;
    long timePerGame = (long) (timeMillis / (float) NUM_GAMES + 0.5);
    int averageScore = (int) (totalScore / NUM_GAMES + 0.5);
    System.out.println();

    System.out.println("Time: " + timePerGame + "ms Avg Score: " + averageScore + " Max Score: " + maxScore
            + " Min Score: " + minScore);
    for (int i = 0; i < gamesByHighestCell.length; i++) {
      int numGames = gamesByHighestCell[i];
      if (numGames > 0) {
        int humanReadableScore = 1 << i;
        System.out.println(humanReadableScore + "'s:\t" + numGames);
      }
    }

  }

}
