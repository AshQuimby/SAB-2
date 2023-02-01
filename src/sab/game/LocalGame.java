package sab.game;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.SeagullEngine;
import com.seagull_engine.SeagullManager;

public class LocalGame {

    // Launch the application
    public static void main(String[] args) {
        // SeagullEngine so we do less work
        SeagullManager manager = SeagullEngine.hatch("Super Ass Brothers: Remasstered", "assets", "assets/images/ui/icon.png", false, new Vector2(1280, 720), Game.game);
        Game.game.manager = manager;
    }
}
