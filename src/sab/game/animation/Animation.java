package sab.game.animation;

public class Animation {
    public final boolean interruptable;

    public final int[] frames;
    public final int frameLength;

    public int frame;
    public int ticksUntilNextFrame;

    public static Animation createBasic(int frameLength, int... frames) {
        return new Animation(frameLength, false, frames);
    }

    public Animation(int frameLength, boolean interruptable, int... frames) {
        this(frames, frameLength, interruptable);
    }

    public Animation(int firstFrame, int lastFrame, int frameLength, boolean interruptable) {
        if (firstFrame >= lastFrame) {
            throw new IllegalArgumentException("First frame must come after last frame");
        }
        if (frameLength <= 0) {
            throw new IllegalArgumentException("Frame length must be greater than 0");
        }

        frames = new int[lastFrame + 1 - firstFrame];
        for (int i = 0; i < frames.length; i++) {
            frames[i] = firstFrame + i;
        }

        this.frameLength = frameLength;
        this.interruptable = interruptable;

        frame = 0;
        ticksUntilNextFrame = frameLength;
    }

    public Animation(int[] frames, int frameLength, boolean interruptable) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException("Frame length must be greater than 0");
        }

        this.frames = frames.clone();
        this.frameLength = frameLength;
        this.interruptable = interruptable;

        frame = 0;
        ticksUntilNextFrame = frameLength;
    }

    public int step() {
        if (isDone()) return frames[frames.length - 1];

        if (ticksUntilNextFrame <= 0) {
            ticksUntilNextFrame = frameLength;
            frame++;
            onFrameChange(frame);
        }

        ticksUntilNextFrame--;
        return frames[frame];
    }

    public int stepLooping() {
        if (isDone()) {
            reset();
            onFrameChange(frame);
        }

        if (ticksUntilNextFrame <= 0) {
            ticksUntilNextFrame = frameLength;
            frame++;
            onFrameChange(frame);
        }

        ticksUntilNextFrame--;

        if (isDone()) {
            reset();
            onFrameChange(frame);
            return frames[frames.length - 1];
        }

        return frames[frame];
    }

    public int getFrame() {
        return frames[frame];
    }

    public void reset() {
        frame = 0;
        ticksUntilNextFrame = frameLength;
    }

    public boolean isDone() {
        return frame == frames.length - 1 && ticksUntilNextFrame <= 0;
    }

    // Methods for overriding
    public void onFrameChange(int newFrame) {

    }
}