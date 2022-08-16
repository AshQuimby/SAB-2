package sab.game;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.SeagullEngine;

public class LocalGame {

    public static void main(String[] args) {
        SeagullEngine.hatch("SAB 2", "assets", "assets/images/ui/icon.png", false, new Vector2(1152, 704), Game.game);
    }
}