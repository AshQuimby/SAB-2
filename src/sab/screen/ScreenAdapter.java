package sab.screen;

import com.seagull_engine.Seagraphics;

public class ScreenAdapter implements Screen {
    @Override
    public Screen keyPressed(int keyCode) {
        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        return this;
    }

    @Override
    public Screen update() {
        return this;
    }

    @Override
    public void render(Seagraphics g) {

    }

    @Override
    public void close() {

    }
}