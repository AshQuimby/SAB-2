package sab.screen;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.seagull_engine.Seagraphics;

public interface Screen {
    Screen keyPressed(int keyCode);
    Screen keyReleased(int keyCode);
    Screen update();
    Screen controllerAxisMoved(Controller controller, int axis, float value, float deltaValue);

    void render(Seagraphics g);
    void debugRender(ShapeRenderer s);
    void close();
}