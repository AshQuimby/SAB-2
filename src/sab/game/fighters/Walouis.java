package sab.game.fighters;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.Racket;
import sab.game.attacks.projectiles.Bomb;
import sab.game.attacks.projectiles.Note;
import sab.game.attacks.projectiles.TinyNote;
import sab.game.particles.Particle;

public class Walouis extends FighterType {
    private Animation throwAnimation;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "walouis";
        fighter.name = "Walouis";
        fighter.hitboxWidth = 44;
        fighter.hitboxHeight = 88;
        fighter.renderWidth = 64;
        fighter.renderHeight = 96;
        fighter.imageOffsetX = 8;
        fighter.imageOffsetY = 4;
        fighter.frames = 11;
        fighter.jumps = 1;
        fighter.speed = 8.5f;
        fighter.acceleration = .86f;
        fighter.doubleJumpMultiplier = 0.85f;
        fighter.jumpHeight = 160;
        fighter.friction = .1f;
        fighter.mass = 5.8f;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        throwAnimation = new Animation(new int[]{2, 4, 5}, 10, false);
        fighter.description = "Walouis, the world famous jazz musician and singer is known for his smash hit albums such as: 'Waaht is Love', 'A Wah's Life', and 'Waaghing in the shadows.' He also sees sucess in his professional badminton career.";
        fighter.debut = "Marvin Badminton";
        fighter.costumes = 3;
    }

    @Override
    public void update(Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) player.startRepeatingAttack(new Attack(new TinyNote(), player), new Animation(new int[]{10}, 100, true), 1, 1, false, new int[]{0});
        if (!player.usedRecovery) player.frame = 10;
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) player.startAttack(new Attack(new Racket(), player), fighter.walkAnimation, 1, 0, false);
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.velocity.y = 26;
            player.startAttack(new Attack(new Note(), player), fighter.freefallAnimation, 1, 1, false, new int[]{0});
            player.usedRecovery = true;
            player.removeJumps();
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            throwAnimation.reset();
            player.velocity.x *= 0.1f;
            player.startAttack(new Attack(new Bomb(), player), throwAnimation, 24, 12, false, new int[]{0});
        }
    }

    @Override
    public void charging(Fighter fighter, Player player, int charge) {
        if (Game.game.window.getTick() % 4 == 0) player.battle.addParticle(new Particle(player.hitbox.getCenter(new Vector2()).add(0, 16), new Vector2(1, 0).rotateDeg(MathUtils.random() * 359), 24, 24, "fire.png"));
        player.velocity.y *= 0.85f;
        player.velocity.x *= 0.95f;
    }
}
