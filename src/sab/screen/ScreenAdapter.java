package sab.screen;

import com.badlogic.gdx.controllers.Controller;
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
    public Screen controllerAxisMoved(Controller controller, int axis, float value, float deltaValue) {
        return this;
    }

    @Override
    public void render(Seagraphics g) {

    }

    @Override
    public void close() {

    }
}