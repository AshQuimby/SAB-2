package sab.game.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.graphics.ParallaxBackground;
import sab.game.Battle;
import sab.game.Game;

public class Hyperspace extends StageType {
    @Override
    public void init(Stage stage) {
        stage.name = "Hyperspace";
        stage.background = "hyperspace_background.png";
        stage.music = "wavezone.mp3";
        stage.parallaxBackground = new ParallaxBackground(Gdx.files.internal("assets/backgrounds/stage/hyperspace"));

        stage.safeBlastZone = new Rectangle(-1360 / 2, -800 / 2, 1360, 800);
        stage.unsafeBlastZone = new Rectangle(-1440 / 2, -860 / 2, 1440, 860);

        Platform platform = new sab.game.stage.Platform(-272 / 2 - 272 - 196, 20, 272, 88, "neon_platform.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(sab.game.stage.StageObject stageObject, Battle battle) {
                stageObject.velocity.y = (float) Math.sin(Game.game.window.getTick() / 32f) / 8f;
            }
        });
        platform.createLedges(48, 16, 80, stage);
        stage.addStageObject(platform);

        platform = new sab.game.stage.Platform(-488 / 2, -200, 488, 88, "synth_platform.png", stage);

        platform.createLedges(48, 16, 80, stage);
        stage.addStageObject(platform);

        platform = new sab.game.stage.Platform(-272 / 2 + 272 + 196, 20, 272, 88, "wave_platform.png", stage, new StageObjectBehaviour() {
            @Override
            public void update(sab.game.stage.StageObject stageObject, Battle battle) {
                stageObject.velocity.y = (float) Math.sin(Game.game.window.getTick() / 32f) / 8f;
            }
        });
        platform.createLedges(48, 16, 80, stage);
        stage.addStageObject(platform);
    }
}
