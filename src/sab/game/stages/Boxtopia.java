package sab.game.stages;

public class Boxtopia extends StageType {
    @Override
    public void init(Stage stage) {
        stage.name = "Boxtopia";
        stage.background = "box_location.png";
        stage.music = "box_location.mp3";

        stage.addStageObject(new StageObject(-512 / 2, -256, 512, 512, "box_back.png", stage));
        stage.addStageObject(new Platform(-512 / 2, -256, 512, 32, "box_bottom.png", stage));
        stage.addStageObject(new Platform(-512 / 2, -256 + 32, 32, 448, "box_left_wall.png", stage));
        stage.addStageObject(new Platform(512 / 2 - 32, -256 + 32, 32, 448, "box_right_wall.png", stage));
        stage.addStageObject(new PassablePlatform(-512 / 2, -256 + 448 + 32, 512, 32, "box_top.png", stage));
    }
}