package sab.game.fighter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.Player;
import sab.game.PlayerAction;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.marvin.Wrench;
import sab.game.attack.marvin.Fireball;
import sab.game.attack.marvin.Frostball;
import sab.game.attack.marvin.Toilet;
import sab.game.particle.Particle;
import sab.game.screen.VictoryScreen;

public class John extends FighterType {
    private Animation swingAnimation;
    private Animation squatAnimation;
    private Animation chargeAnimation;
    private Animation throwAnimation;
    private boolean justWon;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "john";
        fighter.name = "John Joseph";
        fighter.hitboxWidth = 60;
        fighter.hitboxHeight = 68;
        fighter.renderWidth = 80;
        fighter.renderHeight = 76;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 4;
        fighter.frames = 14;
        fighter.jumpHeight = 160;
        fighter.friction = .2f;
        fighter.mass = 5f;
        fighter.jumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 8, true);
        fighter.description = "American attorney John Joseph has a spotless legal record. Despite this, he has been finding it tough to get work after being cursed to be a pink sphere for all eternity. In this state he worked exclusively his arms so much that his legs withered away to the point of being vistigial.";
        fighter.debut = "John Joseph's Nightmare World";

        swingAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
        squatAnimation = new Animation(new int[] {6}, 4, true);
        chargeAnimation = new Animation(new int[] {9}, 4, true);
        throwAnimation = new Animation(new int[] {10, 11}, 6, true);
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 4;
        justWon = true;
    }

    @Override
    public void update(Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startAttack(new Attack(new Fireball(), player), swingAnimation, 6, 10, false);
            player.velocity.y /= 3;
            player.velocity.x *= 0.9f;
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startAttack(new Attack(new Wrench(), player), swingAnimation, 4, 18, false);
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            squatAnimation.reset();
            player.startAttack(new Attack(new Toilet(), player), squatAnimation, 4, 30, false);
            player.removeJumps();
            player.usedRecovery = true;
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startChargeAttack(new PlayerAction(5, true, 0), 5, 60);
        }
    }

    @Override
    public void chargeAttack(Fighter fighter, Player player, int charge) {
        throwAnimation.reset();
        player.startAttack(new Attack(new Frostball(), player), throwAnimation, 7, 10, false, new int[]{charge});
        player.velocity.y /= 3;
        player.velocity.x *= 0.9f;
    }

    @Override
    public void charging(Fighter fighter, Player player, int charge) {
        if (Game.game.window.getTick() % 5 == 0) {
            for (int i = 0; i < 4; i++)
            player.battle.addParticle(new Particle(player.hitbox.getCenter(new Vector2()).add(new Vector2(charge / 4f + 36 + MathUtils.sin(Game.game.window.getTick() / 8f) * 8f, 0).rotateDeg(i * 90 + Game.game.window.getTick() * 4)).add(0, 48), new Vector2(-1, 0).rotateDeg(i * 90 + Game.game.window.getTick() * 2), 32, 32, "frostfire.png"));
        }
        player.velocity.y *= 0.9f;
        player.frame = chargeAnimation.stepLooping();
    }

    @Override
    public String getVictorySongId(Fighter fighter, Player player) {
        if (player.costume == 2) return "gigachad_john_victory.mp3";
        if (player.costume == 3) return "slippery_john_victory.mp3";
        return super.getVictorySongId(fighter, player);
    }

    @Override
    public void renderVictoryScreen(Fighter fighter, Player player, Player opponent, VictoryScreen screen, Seagraphics g) {
        if (player.costume == 3) {
            if (justWon) {
                screen.setupTimer = 0;
                screen.update();
                SABSounds.stopMusic();
                SABSounds.playMusic(getVictorySongId(fighter, player), false);
            }
            if (screen.setupTimer < 252) {
                g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY, 0, 1, 0, false, false, Color.BLACK);
            } else {
                g.scalableDraw(g.imageProvider.getImage("the_beans.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
            }
        }
        justWon = false;
    }
}
