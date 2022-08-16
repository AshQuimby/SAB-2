package sab.util;

public class Utils {
    public static int loop(int old, int increment, int max, int min) {
        if (old + increment >= max) {
            return min + (max - (old + increment));
        }
        if (old + increment < min) {
            return max + min + old + increment;
        }
        return old + increment;
    }
}
