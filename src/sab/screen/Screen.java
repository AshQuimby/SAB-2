package sab.screen;

import com.seagull_engine.Seagraphics;

public interface Screen {
    Screen keyPressed(int keyCode);
    Screen keyReleased(int keyCode);
    Screen update();
    void render(Seagraphics g);
    void close();
}