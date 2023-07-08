package sab.game.settings;

public class IntSetting extends Setting<Integer> {
    private int minValue;
    private int maxValue;
    public IntSetting(String id, String name, Integer defaultValue, int minValue, int maxValue) {
        super(id, name, defaultValue);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public boolean isValid(String rawValue) {
        if (rawValue == null) return false;
        try {
            int v = Integer.parseInt(rawValue);
            return v >= 0 && v <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String asRawValue() {
        return value.toString();
    }

    @Override
    public boolean isDiscrete() {
        return true;
    }

    @Override
    public void set(String rawValue) {
        value = Integer.parseInt(rawValue);
    }

    @Override
    public void next() {
        value = Math.min(maxValue, value + 1);
    }

    @Override
    public void previous() {
        value = Math.max(minValue, value - 1);
    }

    @Override
    public String display() {
        return value.toString();
    }
}
