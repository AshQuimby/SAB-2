package sab.game.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.graphics.ParallaxBackground;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.fighter.Fighter;
import sab.game.fighter.Marvin;
import sab.util.SabRandom;

public class LittleHLand extends StageType {
    private Player player;
    @Override
    public void init(Stage stage) {
        stage.name = "Little h Land";
        stage.background = "little_h_land_background.png";
        stage.music = "fresh_beginnings.mp3";
        stage.parallaxBackground = new ParallaxBackground(Gdx.files.internal("assets/backgrounds/stage/little_h_land"));

        player = new Player(new Fighter(new Marvin()), 0, 0, 100000, stage.getBattle());

        for (int i = -1; i <= 1; i++) {
            placeRandomPlatform(stage, new Vector2(384 * i, -128 + SabRandom.random(-128, 64)));
        }

        stage.player2SpawnX = stage.getStageObjects().get(stage.getStageObjects().size() - 1).getCenter().x;
        stage.player1SpawnX = stage.getStageObjects().get(0).getCenter().x;
    }

    public void placeRandomPlatform(Stage stage, Vector2 position) {
        switch (SabRandom.random(0, 6)) {
            case 0 :
                Platform icePlatform = new Platform(position.x - 192 / 2, position.y, 192, 64, "ice_platform.png", stage);
                icePlatform.friction = 1f;
                icePlatform.createLedges(stage);
                stage.addStageObject(icePlatform);
                break;
            case 1 :
                PassablePlatform oneWayPlatform = new PassablePlatform(position.x - 160 / 2, position.y + 128, 160, 32, "one_way_platform.png", stage);
                stage.addStageObject(oneWayPlatform);
                break;
            case 2 :
                Platform tundraPlatform = new Platform(position.x - 96 / 2 - 96, position.y + 32, 96, 64, "tundra_platform.png", stage);
                tundraPlatform.createLedges(stage);
                stage.addStageObject(tundraPlatform);
                tundraPlatform = new Platform(position.x - 96 / 2 + 96, position.y - 32, 96, 64, "tundra_platform.png", stage);
                tundraPlatform.createLedges(stage);
                stage.addStageObject(tundraPlatform);
                break;
            case 3 :
                Platform rockPlatform = new Platform(position.x - 96, position.y, 96, 64, "rock_platform_part_1.png", stage);
                stage.addStageObject(rockPlatform);
                rockPlatform = new Platform(position.x - 64, position.y - 32, 96, 32, "rock_platform_part_2.png", stage);
                stage.addStageObject(rockPlatform);
                rockPlatform = new Platform(position.x, position.y, 64, 96, "rock_platform_part_3.png", stage);
                stage.addStageObject(rockPlatform);
                break;
            case 4 :
                StageObject infectedPlatform = new Platform(position.x - 128, position.y, 64, 64, "infected_platform_edge.png", stage);
                stage.addStageObject(infectedPlatform);
                infectedPlatform = new StageObject(position.x - 64, position.y, 128, 64, "infection.png", stage);
                stage.addStageObject(infectedPlatform);
                stage.battle.addAttack(new Attack(new InfectionHitbox(), player), new int[] { (int) position.x - 64, (int) position.y });
                infectedPlatform = new Platform(position.x + 64, position.y, 64, 64, "infected_platform_edge.png", stage);
                infectedPlatform.direction = 1;
                stage.addStageObject(infectedPlatform);
                break;
            default :
                Platform grassPlatform = new Platform(position.x- 224 / 2, position.y, 352, 64, "grass_platform.png", stage);
                grassPlatform.createLedges(stage);
                stage.addStageObject(grassPlatform);
                break;
        }
    }

    private class InfectionHitbox extends AttackType {
        @Override
        public void onSpawn(Attack attack, int[] data) {
            attack.hitbox.width = 128;
            attack.hitbox.height = 48;
            attack.hitbox.x = data[0];
            attack.hitbox.y = data[1] + 8;
            attack.life = -1;
            attack.hitCooldown = 15;
            attack.damage = 8;
            attack.reflectable = false;
            attack.parryable = false;
        }

        @Override
        public void successfulHit(Attack attack, GameObject hit) {
            attack.knockback = hit.getCenter().sub(attack.getCenter()).nor().scl(6);
            if (hit instanceof Player) {
                if (((Player) hit).damage > 200) {
                    ((Player) hit).kill(1);
                    attack.knockback.scl(0);
                }
            }
        }
    }
}