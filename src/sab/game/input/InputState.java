package sab.game.input;

public class InputState {
    private final int inputs;
    private final boolean[] pressed;
    private final boolean[] justPressed;
    private final boolean[] justReleased;

    public InputState(int inputs) {
        this.inputs = inputs;

        pressed = new boolean[inputs];
        justPressed = new boolean[inputs];
        justReleased = new boolean[inputs];
    }

    public boolean isValidInput(int input) {
        return (input >= 0 && input < inputs);
    }

    public void press(int input) {
        if (!isValidInput(input)) {
            throw new IllegalArgumentException("Invalid input: " + input + " (must be in range [0, " + inputs + "))");
        }

        pressed[input] = true;
        justPressed[input] = true;
    }

    public void release(int input) {
        if (!isValidInput(input)) {
            throw new IllegalArgumentException("Invalid input: " + input + " (must be in range [0, " + inputs + "))");
        }

        pressed[input] = false;
        justReleased[input] = true;
    }

    public boolean isPressed(int input) {
        if (!isValidInput(input)) {
            throw new IllegalArgumentException("Invalid input: " + input + " (must be in range [0, " + inputs + "))");
        }

        return pressed[input];
    }

    public boolean isJustPressed(int input) {
        if (!isValidInput(input)) {
            throw new IllegalArgumentException("Invalid input: " + input + " (must be in range [0, " + inputs + "))");
        }

        return justPressed[input];
    }

    public boolean isJustReleased(int input) {
        if (!isValidInput(input)) {
            throw new IllegalArgumentException("Invalid input: " + input + " (must be in range [0, " + inputs + "))");
        }

        return justReleased[input];
    }

    public void update() {
        for (int i = 0; i < inputs; i++) {
            justPressed[i] = false;
            justReleased[i] = false;
        }
    }

    public void reset() {
        for (int i = 0; i < inputs; i++) {
            pressed[i] = false;
            justPressed[i] = false;
            justReleased[i] = false;
        }
    }
}