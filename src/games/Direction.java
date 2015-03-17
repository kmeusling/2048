package games;

public enum Direction {
  UP,
  DOWN,
  LEFT,
  RIGHT;

  // Java makes a defensive copy when calling values().
  // Cache it for performance and promise not to modify it.
  public static final Direction[] VALUES = values();
}
