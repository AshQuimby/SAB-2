package sab.game.settings;

public class PercentageSetting extends Setting<Integer> {
    private int increment;
    public PercentageSetting(String id, String name, int defaultValue) {
        this(id, name, defaultValue, 1);
    }

    public PercentageSetting(String id, String name, int defaultValue, int increment) {
        super(id, name, defaultValue);
        this.increment = increment;
    }

    @Override
    public boolean isValid(String rawValue) {
        try {
            int v = Integer.parseInt(rawValue);
            return v >= 0 && v <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public String asRawValue() {
        return Integer.toString(value);
    }

    @Override
    public boolean isDiscrete() {
        return false;
    }

    @Override
    public void set(String rawValue) {
        value = Integer.parseInt(rawValue);
    }

    @Override
    public void next() {
        if (value < 100) {
            value = Math.min(100, value + increment);
        }
    }

    @Override
    public void previous() {
        if (value > 0) {
            value = Math.max(0, value - increment);
        }
    }

    @Override
    public String display() {
        return value + "%";
    }

    public float asFloat() {
        return value / 100f;
    }
}
