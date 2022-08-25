package sab.game.stages;

import com.badlogic.gdx.math.Vector2;

import sab.game.stages.Ledge;
import sab.game.stages.PassablePlatform;
import sab.game.stages.Platform;
import sab.game.stages.Stage;
import sab.game.stages.StageType;

public class Warzone extends StageType {
    private Platform platform;

    @Override
    public void init(Stage stage) {
        stage.name = "Warzone";
        platform = new Platform(-512 / 2, -128, 512, 56, "warzone.png");
        stage.background = "warzone_background.png";
        stage.music = "overgrown_armada.png";
        stage.addStageObject(platform);
        stage.addLedge(new Ledge(platform, new Vector2(-512 / 2 - 24, -32), 24, 52, 1));
        stage.addLedge(new Ledge(platform, new Vector2(512 / 2, -32), 24, 52, -1));
        
        stage.addStageObject(new PassablePlatform(-512 / 2, 0, 124, 20, "warzone_platform.png"));
        stage.addStageObject(new PassablePlatform(-124 / 2, 64, 124, 20, "warzone_platform.png"));
        stage.addStageObject(new PassablePlatform(512 / 2 - 124, 0, 124, 20, "warzone_platform.png"));
    }
}