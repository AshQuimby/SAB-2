package sab.game.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import com.seagull_engine.graphics.ParallaxBackground;
import sab.game.Battle;
import sab.game.Game;

public class COBS extends StageType {

    @Override
    public void init(Stage stage) {
        stage.name = "Church of Big Seagull";
        stage.background = "cobs_background.png";
        stage.music = "seagull_ultima.mp3";
        stage.parallaxBackground = new ParallaxBackground(Gdx.files.internal("assets/backgrounds/stage/cobs"));
        stage.parallaxBackground.ambientSpeedMultiplier = 0.25f;
        sab.game.stage.Platform platform = new sab.game.stage.Platform(-280 / 2 - 280 - 100, -128, 280, 72, "ruined_platform_1.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(sab.game.stage.StageObject stageObject, Battle battle) {
                stageObject.velocity.y = (float) Math.sin(Game.game.window.getTick() / 32f) / 8f;
            }
        });
        stage.addStageObject(platform);
        stage.addLedge(new Ledge(platform, new Vector2(-280 / 2 - 24, -48), 24, 64, 1));
        stage.addLedge(new Ledge(platform, new Vector2(280 / 2, -48), 24, 64, -1));

        platform = new sab.game.stage.Platform(-280 / 2, -128, 280, 72, "ruined_platform_2.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(sab.game.stage.StageObject stageObject, Battle battle) {
                stageObject.velocity.y = (float) Math.sin(Game.game.window.getTick() / 32f + Math.PI / 2f) / 8f;
            }
        });

        stage.addStageObject(platform);
        stage.addLedge(new Ledge(platform, new Vector2(-280 / 2 - 24, -48), 24, 64, 1));
        stage.addLedge(new Ledge(platform, new Vector2(280 / 2, -48), 24, 64, -1));

        platform = new Platform(-280 / 2 + 280 + 100, -128, 280, 72, "ruined_platform_3.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                stageObject.velocity.y = (float) Math.sin(Game.game.window.getTick() / 32f + Math.PI) / 8f;
            }
        });

        stage.addStageObject(platform);
        stage.addLedge(new Ledge(platform, new Vector2(-280 / 2 - 24, -48), 24, 64, 1));
        stage.addLedge(new Ledge(platform, new Vector2(280 / 2, -48), 24, 64, -1));
    }
}
