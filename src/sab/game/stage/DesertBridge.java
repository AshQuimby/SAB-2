package sab.game.stage;

import com.badlogic.gdx.math.Rectangle;

public class DesertBridge extends StageType {

    @Override
    public void init(Stage stage) {
        stage.name = "Desert Bridge";
        Platform platform = new Platform(-1600 / 2, -856 / 2 - 64, 1600, 456, "desert_bridge.png", stage);
        stage.addStageObject(platform);
        stage.background = "desert_background.png";
        stage.music = "desert_bridge.mp3";
        stage.maxZoomOut = 1.15f;
        stage.safeBlastZone = new Rectangle(-1184 / 2 - 64, -856 / 2 - 64, 1184 + 128, 856 + 128);
        stage.unsafeBlastZone = new Rectangle(-1184 / 2 - 128, -856 / 2 - 128, 1184 + 256, 856 + 256);
    }
}
