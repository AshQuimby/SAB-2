package sab.game.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.seagull_engine.graphics.ParallaxBackground;
import sab.game.Game;

public class DesertBridge extends StageType {

    @Override
    public void init(Stage stage) {
        stage.name = "Desert Bridge";
        Platform platform = new Platform(-1800 / 2, -820 / 2 - 64, 1800, 456, "desert_bridge.png", stage);
        stage.addStageObject(platform);
        stage.background = "desert_background.png";
        stage.music = "desert_bridge.mp3";
        stage.parallaxBackground = new ParallaxBackground(Gdx.files.internal("assets/backgrounds/stage/desert_bridge"));
        stage.maxZoomOut = 1.1f;
        stage.safeBlastZone = new Rectangle(-1480 / 2, -1024 / 2, 1480, 1024);
        stage.unsafeBlastZone = new Rectangle(-1520 / 2, -1088 / 2, 1520, 1088);

        stage.addStageObject(new StageObject(
                -1600 / 2f,
                -18,
                1600,
                24,
                "desert_bridge_top.png", stage) {
            @Override
            public boolean inBackground() {
                return false;
            }
        });
    }
}