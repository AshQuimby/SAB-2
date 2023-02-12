package sab.game.fighter;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
import sab.game.PlayerAction;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.marvin.Wrench;
import sab.game.attack.marvin.Fireball;
import sab.game.attack.marvin.Frostball;
import sab.game.attack.marvin.Toilet;
import sab.game.particle.Particle;

public class Marvin extends FighterType {
    private Animation swingAnimation;
    private Animation squatAnimation;
    private Animation chargeAnimation;
    private Animation throwAnimation;

    @Override
    public void setDefaults(sab.game.fighter.Fighter fighter) {
        fighter.id = "marvin";
        fighter.name = "Marvin";
        fighter.hitboxWidth = 40;
        fighter.hitboxHeight = 60;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.frames = 12;
        fighter.jumpHeight = 160;
        fighter.friction = .2f;
        fighter.mass = 5f;
        fighter.jumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        fighter.description = "Retired Albany plumber now princess saving daydreamer, Marvin is a troubled man who is always distracted by his dreams about the things he could be. He can't seem to escape from his brother's sucess as a musician.";
        fighter.debut = "Super Marvin Plumber";

        swingAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
        squatAnimation = new Animation(new int[] {6}, 4, true);
        chargeAnimation = new Animation(new int[] {9}, 4, true);
        throwAnimation = new Animation(new int[] {10, 11}, 6, true);
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 3;
    }

    @Override
    public void update(sab.game.fighter.Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startAttack(new Attack(new Fireball(), player), swingAnimation, 6, 10, false);
            player.velocity.y /= 3;
            player.velocity.x *= 0.9f;
        }
    }

    @Override
    public void sideAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startAttack(new Attack(new Wrench(), player), swingAnimation, 4, 18, false);
        }
    }

    @Override
    public void upAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            squatAnimation.reset();
            player.startAttack(new Attack(new Toilet(), player), squatAnimation, 4, 30, false);
            player.removeJumps();
            player.usedRecovery = true;
        }
    }

    @Override
    public void downAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startChargeAttack(new PlayerAction(5, true, 0), 5, 60);
        }
    }

    @Override
    public void chargeAttack(sab.game.fighter.Fighter fighter, Player player, int charge) {
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
}
