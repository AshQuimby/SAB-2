package sab.util;


import com.badlogic.gdx.math.RandomXS128;

public class SABRandom {
    static {
        gameRandom = new RandomXS128(System.currentTimeMillis());
    }
    private static RandomXS128 gameRandom;
    private static RandomXS128 battleRandom;

    public static void createNewBattleRandom() {
        createNewBattleRandom(System.currentTimeMillis());
    }

    public static void createNewBattleRandom(long seed) {
        battleRandom = new RandomXS128(seed);
    }

    public static void disposeBattleRandom() {
        battleRandom = null;
    }

    public static double randomDouble() {
        if (battleRandom != null) return battleRandom.nextDouble();
        return gameRandom.nextDouble();
    }

    public static float random() {
        if (battleRandom != null) return battleRandom.nextFloat();
        return gameRandom.nextFloat();
    }

    public static float random(float from, float to) {
        if (from == to) return from;
        if (battleRandom != null) return battleRandom.nextFloat(from, to);
        return gameRandom.nextFloat(from, to);
    }

    public static int random(int from, int to) {
        if (from == to) return from;
        if (battleRandom != null) return battleRandom.nextInt(from, to);
        return gameRandom.nextInt(from, to);
    }

    public static float random(float max) {
        if (battleRandom != null) return battleRandom.nextFloat(max);
        return gameRandom.nextFloat(max);
    }

    public static int random(int max) {
        if (battleRandom != null) return battleRandom.nextInt(max);
        return gameRandom.nextInt(max);
    }

    public static boolean randomBoolean(float chance) {
        return random() < chance;
    }
}
