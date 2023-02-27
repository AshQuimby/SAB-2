package sab.game.stage;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import sab.game.Battle;
import sab.game.Game;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.marvin.Fireball;
import sab.game.attack.marvin.Frostball;
import sab.game.fighter.Fighter;
import sab.game.fighter.Marvin;

public class HellTwoBoogaloo extends StageType {

    private Player player;
    public static int i = 0;
    @Override
    public void init(Stage stage) {
        stage.name = "HELL 2 BABY (It's back in business)";
        stage.music = "genetically_engineered_bad.mp3";
        stage.background = "no_ducks.png";
        stage.maxZoomOut = 3f;
        stage.safeBlastZone = new Rectangle(-6969 / 2 - 64, -4200 / 2 - 64, 6969 + 128, 4200 + 128);
        stage.unsafeBlastZone = new Rectangle(-6969 / 2 - 128, -4200 / 2 - 128, 6969 + 256, 4200 + 256);
        player = new Player(new Fighter(new Marvin()), 0, 0, 100000, stage.getBattle());
        player.direction = -1;
        Platform platform = new PassablePlatform(-630 / 2, 0, 1000, 128, "character_selector_background_layer_2.png", stage);
        platform.createLedges(16, 24, 16, stage);
        stage.addStageObject(platform);

        for (i = -4; i < 5; i++) {
            platform = new PassablePlatform(128, 32, 256, 64, "the_beans.png", new StageObjectBehaviour() {
                int offset = i * 30;
                @Override
                public void update(StageObject stageObject, Battle battle) {
                    stageObject.velocity.x = MathUtils.cosDeg(((Game.getTick() + offset) / 256f) * 128);
                    stageObject.velocity.y = MathUtils.sinDeg(((Game.getTick() + offset) / 256f) * 128);
                    stageObject.hitbox.x += stageObject.velocity.x;
                    stageObject.hitbox.y += stageObject.velocity.y;
                }
            }, stage);
            stage.addStageObject(platform);
        }

        for (i = 0; i < 24; i++) {
            platform = new Platform(-512 + 64 * i, -32 + i * 32, 64 * + 64 * (i / 8), 32 + i, "fight_button.png", stage);
            stage.addStageObject(platform);
        }

        platform = new Platform(-1680 / 2, 512, 640, 256, "ice.png", stage);
        platform.createLedges(16, 24, 16, stage);
        stage.addStageObject(platform);

        StageObject marvinBox = new PassablePlatform(-512, 960, 128, 128, "marvin_box.png", new StageObjectBehaviour() {
            @Override
            public void update(StageObject stageObject, Battle battle) {
                stageObject.velocity.x = MathUtils.sinDeg(Game.getTick() / 256f) * 8f;
                if (Game.getTick() % 180 == 0) {
                    Player player = new Player(new Fighter(new Marvin()), 0, 0, 1, battle);
                    player.setAI(player.fighter.getAI(player, 1));
                    player.hitbox.setCenter(stageObject.getCenter());
                    battle.addGameObject(player);
                }
            }
        }, stage);
        stage.addStageObject(marvinBox);
    }

    @Override
    public void update(Battle battle, Stage stage) {
        if (Game.game.window.getTick() % 10 == 0) {
            player.hitbox.x = MathUtils.random(stage.safeBlastZone.width - stage.safeBlastZone.width / 2 + 128);
            if (Game.game.window.getTick() % 360 == 0) {
                battle.addAttack(new Attack(new Frostball(), player), new int[]{ 320 });
            }
            battle.addAttack(new Attack(new Fireball(), player), new int[0]);
        }
    }
}
