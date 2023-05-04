package sab.game;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    NONE;

    public boolean isHorizontal() {
        switch (this) {
            case LEFT, RIGHT -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isVertical() {
        switch (this) {
            case UP, DOWN -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean isNotNone() {
        return this != NONE;
    }

    public boolean isNone() {
        return this == NONE;
    }
}