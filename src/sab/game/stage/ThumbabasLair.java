package sab.game.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.seagull_engine.graphics.ParallaxBackground;
import sab.game.Game;

public class ThumbabasLair extends StageType {
    @Override
    public void init(Stage stage) {
        stage.name = "Thumbaba's Lair";
        stage.background = "thumbabas_lair_background.png";
        stage.parallaxBackground = new ParallaxBackground(Gdx.files.internal("assets/backgrounds/stage/thumbabas_lair"));
        stage.parallaxBackground.ambientSpeedMultiplier = 0.01f;
        stage.music = "thumbabas_lair.mp3";
    
        Rectangle platform1 = new Rectangle(0, 0, 136, 20);
        Rectangle platform2 = new Rectangle(platform1);
        Rectangle platform3 = new Rectangle(platform1);
        Rectangle platform4 = new Rectangle(platform1);

        stage.addStageObject(new StageObject(-Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2 - 64, Game.game.window.resolutionX, Game.game.window.resolutionY, "thumbabas_fingers.png", stage));

        platform1.setCenter(200, -38 - 64);
        platform2.setCenter(408, -82 - 64);
        platform3.setCenter(-112, -98 - 64);
        platform4.setCenter(-416, -130 - 64);

        Platform platform = new Platform(platform1.x, platform1.y, 136, 20, "finger_platform.png", stage);
        platform.createLedges(32, 16, 32, stage);
        stage.addStageObject(platform);
        platform = new Platform(platform2.x, platform2.y, 136, 20, "finger_platform.png", stage);
        platform.createLedges(32, 16, 32, stage);
        stage.addStageObject(platform);
        platform = new Platform(platform3.x, platform3.y, 136, 20, "finger_platform.png", stage);
        platform.createLedges(32, 16, 32, stage);
        stage.addStageObject(platform);
        platform = new Platform(platform4.x, platform4.y, 136, 20, "finger_platform.png", stage);
        platform.createLedges(32, 16, 32, stage);
        stage.addStageObject(platform);
    }
}
