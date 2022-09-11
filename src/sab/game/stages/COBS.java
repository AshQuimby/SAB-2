package sab.game.stages;

import com.badlogic.gdx.math.Vector2;

import sab.game.Battle;
import sab.game.Game;

public class COBS extends StageType {

    @Override
    public void init(Stage stage) {
        stage.name = "Church of Big Seagull";
        stage.background = "cobs_background.png";
        stage.id = "cobs";
        stage.music = "seagull_ultima.mp3";
        Platform platform = new Platform(-280 / 2 - 280 - 100, -128, 280, 72, "ruined_platform_1.png", new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                stageObject.velocity.y = (float) Math.sin(Game.game.window.getTick() / 32f) / 4f;
            }
        }, stage);
        stage.addStageObject(platform);
        stage.addLedge(new Ledge(platform, new Vector2(-280 / 2 - 24, -48), 24, 64, 1));
        stage.addLedge(new Ledge(platform, new Vector2(280 / 2, -48), 24, 64, -1));

        platform = new Platform(-280 / 2, -128, 280, 72, "ruined_platform_2.png", new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                stageObject.velocity.y = (float) Math.sin(Game.game.window.getTick() / 32f + Math.PI / 2f) / 4f;
            }
        }, stage);

        stage.addStageObject(platform);
        stage.addLedge(new Ledge(platform, new Vector2(-280 / 2 - 24, -48), 24, 64, 1));
        stage.addLedge(new Ledge(platform, new Vector2(280 / 2, -48), 24, 64, -1));

        platform = new Platform(-280 / 2 + 280 + 100, -128, 280, 72, "ruined_platform_3.png", new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                stageObject.velocity.y = (float) Math.sin(Game.game.window.getTick() / 32f + Math.PI) / 4f;
            }
        }, stage);

        stage.addStageObject(platform);
        stage.addLedge(new Ledge(platform, new Vector2(-280 / 2 - 24, -48), 24, 64, 1));
        stage.addLedge(new Ledge(platform, new Vector2(280 / 2, -48), 24, 64, -1));
    }
}
