package sab.game.stage;

public class BattleConfig {
    public enum GameMode {
        DAMAGE,
        HEALTH
    }

    public GameMode gameMode;

    public int player1Index;
    public int player1Costume;
    public int player1Type;

    public int player2Index;
    public int player2Costume;
    public int player2Type;

    public int stageIndex;

    public int lives;

    public boolean spawnAssBalls;
    public boolean stageHazards;

    public BattleConfig() {
        gameMode = GameMode.DAMAGE;
    }

    public void setPlayer1(int index, int costume, int type) {
        player1Index = index;
        player1Costume = costume;
        player1Type = type;
    }

    public void setPlayer2(int index, int costume, int type) {
        player2Index = index;
        player2Costume = costume;
        player2Type = type;
    }
}
