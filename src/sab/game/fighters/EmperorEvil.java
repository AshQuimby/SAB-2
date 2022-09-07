package sab.game.fighters;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
import sab.game.PlayerAction;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.Chomp;
import sab.game.attacks.melees.ExplosiveBarrel;
import sab.game.attacks.melees.Wrench;
import sab.game.attacks.projectiles.Cannonball;
import sab.game.attacks.projectiles.Frostball;
import sab.game.attacks.projectiles.MagicBanana;
import sab.game.attacks.projectiles.Toilet;
import sab.game.particles.Particle;

public class EmperorEvil extends FighterType {
    private Animation shootAnimation;
    private Animation chompAnimation;
    private Animation barrel;
    private Animation chargeAnimation;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "emperor_evil";
        fighter.name = "Emperor E. Vile";
        fighter.hitboxWidth = 64;
        fighter.hitboxHeight = 80;
        fighter.renderWidth = 80;
        fighter.renderHeight = 88;
        fighter.imageOffsetX = -4;
        fighter.imageOffsetY = 4;
        fighter.frames = 14;
        fighter.jumpHeight = 90;
        fighter.doubleJumpMultiplier = 0.8f;
        fighter.speed = 9.2f;
        fighter.acceleration = .32f;
        fighter.jumpHeight = 160;
        fighter.friction = .22f;
        fighter.mass = 7.5f;
        fighter.jumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 7, true);
        fighter.description = "Emperor E. Vile is a big mean alligator who steals all of the bananas from the monkeys. He doesn't even eat bananas, nobody knows why he takes them in the first place.";
        fighter.debut = "King Kong City";

        shootAnimation = new Animation(new int[] {9, 5}, 18, true);
        chompAnimation = new Animation(new int[] {4, 5}, 6, true);
        barrel = new Animation(new int[] {10}, 60, true);
        chargeAnimation = new Animation(new int[] {11, 12, 13}, 8, true);
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 3;
    }

    @Override
    public void update(Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            shootAnimation.reset();
            player.startAttack(new Attack(new Cannonball(), player), shootAnimation, 18, 18, false);
            player.velocity.x *= 0.2f;
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            chompAnimation.reset();
            player.startAttack(new Attack(new Chomp(), player), chompAnimation, 4, 40, false);
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            barrel.reset();
            player.startAttack(new Attack(new ExplosiveBarrel(), player), barrel, 1, 30, false);
            player.removeJumps();
            player.usedRecovery = true;
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startChargeAttack(new PlayerAction(5, true, 0), 15, 120);
        }
    }

    @Override
    public void chargeAttack(Fighter fighter, Player player, int charge) {
        if (!player.usedRecovery) {
            chargeAnimation.reset();
            player.startAttack(new Attack(new MagicBanana(), player), chargeAnimation, 7, 10, false, new int[]{charge});
            player.velocity.y /= 3;
            player.velocity.x *= 0.9f;
            for (int i = 0; i < 8; i++) {
                player.battle.addParticle(new Particle(player.hitbox.getCenter(new Vector2()), new Vector2(2 * MathUtils.random(-1f, 1f), 4 * MathUtils.random()), 64, 64, 0, "bananafire.png"));
            }
        }
    }

    @Override
    public void charging(Fighter fighter, Player player, int charge) {
        if (Game.game.window.getTick() % 15 == 0) {
            for (int i = 0; i < 12; i++) {
                player.battle.addParticle(new Particle(player.hitbox.getCenter(new Vector2()).add(0, 32).add(new Vector2(96 * charge / 120 / 2, 0).rotateDeg(i * 30f)), new Vector2(), 32, 32, "bananafire.png"));
            }
        }
        player.frame = chargeAnimation.stepLooping();
    }
}
