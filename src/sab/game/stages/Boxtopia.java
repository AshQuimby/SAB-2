package sab.game.stages;

import com.badlogic.gdx.math.Vector2;

public class Boxtopia extends StageType {

    @Override
    public void init(Stage stage) {
        stage.name = "Boxtopia";
        stage.music = "genetically_engineered_bad.mp3";
        stage.addStageObject(new Platform(-512 / 2, -128, 512, 56, "last_location.png"));
        stage.addStageObject(new Platform(-512 / 2, -128 + 56, 56, 256 - 56, "last_location.png"));
        stage.addStageObject(new Platform(512 / 2 - 56, -128 + 56, 56, 256 - 56, "last_location.png"));
        stage.addStageObject(new Platform(-512 / 2, 128, 512, 56, "last_location.png"));
    }
}