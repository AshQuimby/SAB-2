package sab.game.fighter;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.walouis.Racket;
import sab.game.attack.walouis.Bomb;
import sab.game.attack.walouis.Note;
import sab.game.attack.walouis.TinyNote;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.Utils;

public class Walouis extends FighterType {
    private Animation throwAnimation;

    @Override
    public void setDefaults(sab.game.fighter.Fighter fighter) {
        fighter.id = "walouis";
        fighter.name = "Walouis";
        fighter.hitboxWidth = 44;
        fighter.hitboxHeight = 88;
        fighter.renderWidth = 64;
        fighter.renderHeight = 96;
        fighter.imageOffsetX = 8;
        fighter.imageOffsetY = 4;
        fighter.frames = 12;
        fighter.airJumps = 1;
        fighter.speed = 7.5f;
        fighter.acceleration = .46f;
        fighter.doubleJumpMultiplier = 0.85f;
        fighter.jumpHeight = 160;
        fighter.friction = .1f;
        fighter.mass = 5.8f;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        throwAnimation = new Animation(new int[]{2, 4, 5}, 10, false);
        fighter.description = "Walouis, the world famous jazz musician and singer is known for his smash hit albums such as: 'Waaht is Love', 'A Wah's Life', and 'Waaghing in the shadows.' He also sees success in his professional badminton career.";
        fighter.debut = "Marvin Badminton";
        fighter.costumes = 3;
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 100) {
            boolean useRacket;
            Rectangle racketBounds = new Rectangle(0, 0, 208, 116);

            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                racketBounds.setCenter(center);
                racketBounds.y += 24;
                if (player.hasAction() && player.getCharge() < 90 && useRacket) {
                    pressKey(Keys.ATTACK);

                    if (racketBounds.overlaps(target.hitbox)) {
                        releaseKey(Keys.ATTACK);
                        return;
                    }
                }

                if (Math.random() < .1f) {
                    Rectangle futureTargetHitbox = new Rectangle(target.hitbox);
                    Vector2 futureTargetVelocity = target.velocity.cpy();
                    int ticksUntilCollision = 0;
                    for (int i = 0; i < 20 + difficulty * 10; i++) {
                        futureTargetHitbox.x += futureTargetVelocity.x;
                        futureTargetHitbox.y += futureTargetVelocity.y;
                        if (!target.touchingStage) futureTargetVelocity.y -= .96f;

                        if (racketBounds.overlaps(futureTargetHitbox)) {
                            ticksUntilCollision = i;
                            break;
                        }
                    }

                    if (ticksUntilCollision >= 6 && ticksUntilCollision <= 30) {
                        useSideAttack();
                        useRacket = true;
                        return;
                    }
                }

                if (isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x)) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);
                    if (horizontalDistance > 275 && Math.random() * 25 < difficulty) {
                        useDownAttack();
                    } else if (horizontalDistance < 150) {
                        useNeutralAttack();
                    }
                }
            }
        };
    }

    @Override
    public void update(sab.game.fighter.Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startRepeatingAttack(new TinyNote(), new Animation(new int[]{11}, 100, true), 1, 1, false, new int[]{0});
            player.frame = 11;
        }
    }

    @Override
    public void sideAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) player.startAttack(new Racket(), fighter.walkAnimation, 1, 0, false);
    }

    @Override
    public void upAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.velocity.y = 26;
            player.startAttack(new Note(), fighter.freefallAnimation, 1, 1, false, new int[]{0});
            player.usedRecovery = true;
            player.removeJumps();
            SABSounds.playSound("wagh.mp3");
        }
    }

    @Override
    public void downAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            throwAnimation.reset();
            player.velocity.x *= 0.1f;
            player.startAttack(new Bomb(), throwAnimation, 24, 12, false, new int[]{0});
        }
    }

    @Override
    public void charging(Fighter fighter, Player player, int charge) {
        if (Game.game.window.getTick() % 4 == 0) player.battle.addParticle(new Particle(Utils.randomPointInRect(player.hitbox), new Vector2(1, 0).rotateDeg(MathUtils.random() * 359), 24, 24, "fire.png"));
        player.velocity.y *= 0.85f;
        player.velocity.x *= 0.95f;
    }
}
