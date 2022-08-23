package sab.game.stages;

import com.badlogic.gdx.math.Vector2;

public class LastLocation extends StageType {
    Platform platform;

    @Override
    public void init(Stage stage) {
        platform = new Platform(-512 / 2, -128, 512, 56, "last_location.png");
        stage.addStageObject(platform);
        stage.addLedge(new Ledge(platform, new Vector2(-512 / 2 - 24, -52), 24, 64, 1));
        stage.addLedge(new Ledge(platform, new Vector2(512 / 2, -52), 24, 64, -1));
    }
}