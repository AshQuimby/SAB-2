package sab.game.stage;

import com.badlogic.gdx.math.Vector2;

public class Warzone extends StageType {
    private sab.game.stage.Platform platform;

    @Override
    public void init(Stage stage) {
        stage.name = "Warzone";
        platform = new Platform(-512 / 2, -128, 512, 56, "warzone.png", stage);
        stage.background = "warzone_background.png";
        stage.music = "invasion.mp3";
        stage.id = "warzone";
        stage.addStageObject(platform);
        stage.addLedge(new sab.game.stage.Ledge(platform, new Vector2(-512 / 2 - 24, -32), 24, 52, 1));
        stage.addLedge(new Ledge(platform, new Vector2(512 / 2, -32), 24, 52, -1));
        
        stage.addStageObject(new sab.game.stage.PassablePlatform(-512 / 2, 0, 124, 20, "warzone_platform.png", stage));
        stage.addStageObject(new sab.game.stage.PassablePlatform(-124 / 2, 64, 124, 20, "warzone_platform.png", stage));
        stage.addStageObject(new PassablePlatform(512 / 2 - 124, 0, 124, 20, "warzone_platform.png", stage));
    }
}