package sab.game.stage;

import com.badlogic.gdx.math.Rectangle;
import sab.game.Game;

public class DesertBridge extends StageType {

    @Override
    public void init(Stage stage) {
        stage.name = "Desert Bridge";
        Platform platform = new Platform(-1600 / 2, -820 / 2 - 64, 1600, 456, "desert_bridge.png", stage);
        stage.addStageObject(platform);
        stage.background = "desert_background.png";
        stage.music = "desert_bridge.mp3";
        stage.maxZoomOut = 1.1f;
        stage.safeBlastZone = new Rectangle(-1400 / 2 - 64, -820 / 2 - 64, 1400 + 128, 820 + 128);
        stage.unsafeBlastZone = new Rectangle(-1600 / 2 - 128, -900 / 2 - 128, 1400 + 256, 900 + 256);

        stage.addStageObject(new StageObject(
                -1600 / 2f,
                -40,
                1600,
                24,
                "desert_bridge_top.png", stage) {
            @Override
            public boolean inBackground() {
                return false;
            }
        });
    }
}
