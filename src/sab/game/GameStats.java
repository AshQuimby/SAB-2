package sab.game;

public class GameStats {
    
    private int damageDealt;
    private int damageTaken;
    private int kills;
    private int deaths;
    private boolean winner;
    private String type;
    private int playerId;

    public GameStats(String type, int playerId) {
        damageDealt = 0;
        damageTaken = 0;
        kills = 0;
        deaths = 0;
        winner = false;
        this.type = type;
        this.playerId = playerId + 1;
    }

    public void setType(String type, int playerId) {
        this.type = type;
        this.playerId = playerId + 1;
    }

    public void dealtDamage(int amount) {
        damageDealt += amount;
    }

    public void tookDamage(int amount) {
        damageTaken += amount;
    }

    public void gotKill() {
        kills++;
    }

    public void died() {
        deaths++;
    }

    public void won() {
        winner = true;;
    }

    @Override
    public String toString() {
        return "    PLAYER " + playerId + "\n" + type + "\nDamage dealt: " + damageDealt + "%\nDamage taken: " + damageTaken + "%\nKills: " + kills + "\nDeaths: " + deaths; 
    }
}
