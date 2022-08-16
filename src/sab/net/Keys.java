package sab.net;

public class Keys {
    public static final byte LEFT = 0x00;
    public static final byte RIGHT = 0x01;
    public static final byte UP = 0x02;
    public static final byte DOWN = 0x03;
    public static final byte ATTACK = 0x04;
    public static final byte PARRY = 0x05;

    public static boolean isValidKey(byte key) {
        return key >= 0 && key < 6;
    }
}