package sab.game.stage;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sab.game.Battle;
import sab.game.Game;
import sab.game.Player;
import sab.game.settings.Settings;
import sab.game.attack.Attack;
import sab.game.attack.marvin.Fireball;
import sab.game.attack.marvin.Frostball;
import sab.game.attack.unnamed_duck.DuckItem;
import sab.game.fighter.Fighter;
import sab.game.fighter.Marvin;
import sab.util.SabRandom;

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
            platform = new Platform(-512 + 64 * i, -32 + i * 32, 64 * + 16 * (i / 8f), 32 + i, "fight_button.png", stage);
            stage.addStageObject(platform);
        }

        platform = new Platform(-1680 / 2, 512, 640, 256, "ice.png", stage);
        platform.createLedges(16, 24, 16, stage);
        stage.addStageObject(platform);

        if (Settings.localSettings.stageHazards.value) {

            StageObject marvinBox = new PassablePlatform(-512, 960, 128, 128, "marvin_box.png", new StageObjectBehaviour() {
                @Override
                public void update(StageObject stageObject, Battle battle) {
                    stageObject.velocity.x = MathUtils.sinDeg(Game.getTick() / 256f) * 8f;
                    if (Game.getTick() % 180 == 0) {
                        Player player = new Player(new Fighter(new Marvin()), 0, -1, 1, battle);
                        player.setAI(player.fighter.getAI(player, 1));
                        player.hitbox.setCenter(stageObject.getCenter());
                        battle.addGameObject(player);
                    }
                }
            }, stage);
            stage.addStageObject(marvinBox);

            Platform wobbler = new Platform(-512, 960, 64, 64, "barrel.png", stage, new StageObjectBehaviour() {
                Player target;

                @Override
                public void update(StageObject stageObject, Battle battle) {
                    float bestDist = 1000000000f;
                    for (Player player : battle.getPlayers()) {
                        if (player.getLives() <= 0) continue;
                        float dist = player.getCenter().dst2(stageObject.getCenter());
                        if (dist < bestDist || target == null) {
                            target = player;
                            bestDist = dist;
                        }
                    }
                    stageObject.velocity = target.getCenter().sub(stageObject.getCenter()).nor().scl(3.5f);
                    player.hitbox.setCenter(stageObject.getCenter());
                    battle.addAttack(new Attack(new DuckItem(), player), new int[]{ 0, 0, (int) stageObject.hitbox.width + 4, (int) stageObject.hitbox.height + 4, 8, 12, 8, 1, 8 });
                    Vector2 oldCenter = stageObject.getCenter();
                    stageObject.hitbox.width += MathUtils.sin(Game.getTick() / 2) * 80;
                    stageObject.hitbox.height += MathUtils.cos(Game.getTick() / 2) * 80;
                    stageObject.hitbox.setCenter(oldCenter);
                    stageObject.drawRect.set(stageObject.hitbox);
                }
            });
            stage.addStageObject(wobbler);
        }
    }

    @Override
    public void update(Battle battle, Stage stage) {
        if (Game.game.window.getTick() % 10 == 0) {
            player.hitbox.x = SabRandom.random(stage.safeBlastZone.width - stage.safeBlastZone.width / 2 + 128);
            if (Game.game.window.getTick() % 360 == 0) {
                battle.addAttack(new Attack(new Frostball(), player), new int[]{ 320 });
            }
            battle.addAttack(new Attack(new Fireball(), player), new int[0]);
        }
    }
}
