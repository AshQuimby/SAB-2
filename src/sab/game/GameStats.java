package sab.game;

public class GameStats {
    private int damageDealt;
    private int damageTaken;
    private int kills;
    private int deaths;
    private int parries;
    private String type;
    private int playerId;
    private int style;
    private int combo;
    private boolean flawless;
    private boolean flawlessLife;

    public GameStats(String type, int playerId) {
        damageDealt = 0;
        damageTaken = 0;
        kills = 0;
        deaths = 0;
        style = 0;
        combo = 0;
        parries = 0;
        flawless = true;
        this.type = type;
        this.playerId = playerId + 1;
    }

    public void setType(String type, int playerId) {
        this.type = type;
        this.playerId = playerId + 1;
    }

    public void dealtDamage(int amount) {
        style += amount / 2 * Math.max(combo / 2, 1);
        combo += 1;
        damageDealt += amount;
    }

    public void tookDamage(int amount) {
        damageTaken += amount;
        loseCombo();
    }

    public void gotKill() {
        if (flawless)
            style += 100;
        else if (flawlessLife)
            style += 50;
        else
            style += 25;
        kills++;
    }

    public void died() {
        if (flawlessLife)
            style -= 25;
        else
            style -= 50;
        loseCombo();
        deaths++;
        flawlessLife = true;
    }

    private void loseCombo() {
        combo = 0;
        flawless = false;
        flawlessLife = false;
    }

    public int getStyle() {
        return style;
    }

    public void landedParry() {
        parries++;
        if (flawless)
            style += 60;
        else
            style += 30;
    }

    public boolean wasFlawless() {
        return flawless;
    }

    @Override
    public String toString() {
        return "    PLAYER " + playerId + "\n" + type + "\nDamage dealt: " + damageDealt + "%\nDamage taken: " +
                damageTaken + "%\nKills: " + kills + "\nDeaths: " + deaths + "\nParries: " + parries + "\nStyle: " + style + "\nRank: ";
    }
}
