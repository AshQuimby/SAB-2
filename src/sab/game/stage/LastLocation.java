package sab.game.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.graphics.ParallaxBackground;

public class LastLocation extends StageType {
    sab.game.stage.Platform platform;

    @Override
    public void init(Stage stage) {
        stage.name = "Last Location";
        stage.parallaxBackground = new ParallaxBackground(Gdx.files.internal("assets/backgrounds/stage/last_location"));

        platform = new Platform(-512 / 2, -128, 512, 56, "last_location.png", stage);
        stage.addStageObject(platform);
        platform.createLedges(32, 24, 52, stage);
    }
}