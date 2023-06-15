package sab.game.fighter;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.DamageSource;
import sab.game.Game;
import sab.game.Player;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.empty_soldier.AngrySoul;
import sab.game.attack.empty_soldier.EmptySoldierSlash;
import sab.game.attack.empty_soldier.ShadowPlunge;
import sab.game.attack.empty_soldier.ViceroyWings;
import sab.game.particle.Particle;
import sab.net.Connection;
import sab.net.Keys;
import sab.util.Utils;
import sab.util.SABRandom;

import java.io.IOException;

public class EmptySoldier extends FighterType {
    private int spirit;
    private Animation swingAnimation;
    private Animation castAnimation;
    private int ticksSinceCast;
    private int swingDirection;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "empty_soldier";
        fighter.name = "Empty Soldier";
        fighter.hitboxWidth = 32;
        fighter.hitboxHeight = 64;
        fighter.renderWidth = 64;
        fighter.renderHeight = 68;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.airJumps = 1;
        fighter.speed = 12.2f;
        fighter.acceleration = 1f;
        fighter.jumpHeight = 176;
        fighter.frames = 12;
        fighter.friction = .3f;
        fighter.mass = 4.13f;
        fighter.airDodgeSpeed = 10;
        fighter.walkAnimation = new Animation(1, 3, 5, true);
        fighter.costumes = 3;
        fighter.description = "FOR THE LAST TIME. EMPTY SOLDIER WAS THE NAME OF THE SCIENTIST! THE BUG IS CALLED: \"EMPTY SOLDIER'S MONSTER!\"";
        fighter.debut = "Empty Soldier";
        fighter.freefallAnimation = new Animation(new int[] {6}, 1, false);
        fighter.ledgeAnimation = new Animation(new int[] {7}, 1, false);
        fighter.knockbackAnimation = new Animation(new int[] {8}, 1, false);

        spirit = 100;
        swingAnimation = new Animation(new int[] {4}, 8, true);
        castAnimation = new Animation(9, 11, 6, false);
        ticksSinceCast = 30;
        swingDirection = -1;
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 32) {
            private static final int SLASH_DISTANCE = 50;

            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                EmptySoldier emptySoldier = (EmptySoldier) player.fighter.type;
                if (emptySoldier.spirit < 15) {
                    preferredHorizontalDistance = 0;
                } else {
                    preferredHorizontalDistance = 32;
                }

                if (isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x)) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);

                    if (horizontalDistance <= SLASH_DISTANCE) {
                        useSideAttack();
                    } else {
                        if (emptySoldier.spirit >= 15 && SABRandom.random() * 25 < difficulty) {
                            useNeutralAttack();
                        }
                    }
                } else if (isDirectlyAbove(target.hitbox) && SABRandom.random() * 20 < difficulty) {
                    useDownAttack();
                }
            }
        };
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.isReady() && !player.touchingStage && !player.grabbingLedge()) {
            player.frame = player.velocity.y > -5 ? 5 : 6;
        }

        if (++ticksSinceCast < 30) {
            player.velocity.y = 0;
            player.frame = castAnimation.stepLooping();
        }
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (spirit >= 15) {
            castAnimation.reset();
            player.startAttack(new AngrySoul(), null, 10,  20, false);
            ticksSinceCast = 0;
            spirit -= 15;
        } else {
            sideAttack(fighter, player);
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        swingAnimation.reset();
        player.startAttack(new EmptySoldierSlash(), swingAnimation, 6, 6, false, new int[] {swingDirection});
        swingDirection = -swingDirection;
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startAttack(new ViceroyWings(), null, 6, 8, false);
            player.usedRecovery = true;
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.touchingStage && spirit >= 25) {
            player.startIndefiniteAttack(new ShadowPlunge(), null, 10, false);
            spirit -= 25;
        }
    }

    @Override
    public void hitObject(Fighter fighter, Player player, Attack attack, GameObject hit) {
        if (attack.type instanceof EmptySoldierSlash) {
            int spiritGained = Math.min(100, spirit + 20) - spirit;
            for (int i = 0; i < (spiritGained > 0 ? 3 : 0); i++) {
                attack.owner.battle.addParticle(new Particle(-0.1f, Utils.randomPointInRect(hit.hitbox), Utils.randomParticleVelocity(1), 16, 16, .97f, 0, "spirit_bubble.png"));
            }
            spirit += spiritGained;
        }
    }

    @Override
    public void renderUI(Fighter fighter, Player player, Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("spirit_bar_back.png"), player.getId() == 0 ? -256 - 72 - 4 : 256 + 4, -256, 72, 72);
        int drawAmount = (int) (spirit / 100f * 56);
        Game.game.window.batch.draw(g.imageProvider.getImage("spirit_bar.png"), player.getId() == 0 ? -256 - 56 - 12 : 256 + 12, -248, 56, drawAmount / 4 * 4, 0, 14, 14, -drawAmount / 4, false, true);
    }

    @Override
    public byte[] getData() {
        return new byte[] {(byte) spirit};
    }

    @Override
    public void setData(byte[] data) {
        spirit = data[0];
    }
}