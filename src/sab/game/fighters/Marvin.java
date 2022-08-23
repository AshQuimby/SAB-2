package sab.game.fighters;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
import sab.game.PlayerAction;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.Wrench;
import sab.game.attacks.projectiles.Fireball;
import sab.game.attacks.projectiles.Frostball;
import sab.game.attacks.projectiles.Toilet;
import sab.game.particles.Particle;

public class Marvin extends FighterType {
    private Animation swingAnimation;
    private Animation squatAnimation;
    private Animation chargeAnimation;
    private Animation throwAnimation;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "marvin";
        fighter.name = "Marvin";
        fighter.hitboxWidth = 40;
        fighter.hitboxHeight = 60;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.frames = 12;
        fighter.speed = 0.6f;
        fighter.jumpHeight = 160;
        fighter.friction = .05f;
        fighter.mass = 5f;
        fighter.jumps = 10000;
        fighter.walkAnimation = new Animation(0, 4, 5, true);
        fighter.description = "    Retired Albany plumber now"
        + "\nprincess saving daydreamer,"
        + "\nMarvin is a troubled man "
        + "\nwho is always distracted"
        + "\nby his dreams about the "
        + "\nthings he could be. He can't"
        + "\nseem to escape from his brother's" 
        + "\nsucess as a musician."
        + "\n"
        + "\nDebut: Super Marvin Plumber";

        swingAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
        squatAnimation = new Animation(new int[] {6}, 4, true);
        chargeAnimation = new Animation(new int[] {9}, 4, true);
        throwAnimation = new Animation(new int[] {10, 11}, 6, true);
        fighter.costumes = 3;
    }

    @Override
    public void update(Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        swingAnimation.reset();
        player.startAttack(new Attack(new Fireball(), player), swingAnimation, 6, 10, false);
        player.velocity.y /= 3;
        player.velocity.x *= 0.9f;
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        swingAnimation.reset();
        player.startAttack(new Attack(new Wrench(), player), swingAnimation, 4, 18, false);
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        squatAnimation.reset();
        player.startAttack(new Attack(new Toilet(), player), squatAnimation, 4, 30, false);
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        player.startChargeAttack(new PlayerAction(5, true, 0, null), 5, 60);
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
}
