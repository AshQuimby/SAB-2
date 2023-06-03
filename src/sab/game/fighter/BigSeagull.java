package sab.game.fighter;

import com.badlogic.gdx.math.Vector2;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.big_seagull.Glide;
import sab.game.attack.big_seagull.Peck;
import sab.game.attack.big_seagull.FeatherDart;
import sab.game.attack.big_seagull.Gust;
import sab.game.stage.Platform;
import sab.net.Keys;

public class BigSeagull extends FighterType {
    private Animation hoverAnimation;
    private Animation flyingAnimation;
    private Animation peckAnimation;
    private Animation gustAnimation;

    @Override
    public void setDefaults(sab.game.fighter.Fighter fighter) {
        fighter.id = "big_seagull";
        fighter.name = "Big Seagull";
        fighter.hitboxWidth = 72;
        fighter.hitboxHeight = 80;
        fighter.renderWidth = 96;
        fighter.renderHeight = 96;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 8;
        fighter.frames = 19;
        fighter.speed = 14f;
        fighter.acceleration = .666f;
        fighter.jumpHeight = 160;
        fighter.doubleJumpMultiplier = .5f;
        fighter.friction = .1f;
        fighter.mass = 6.66f;
        fighter.airJumps = 5;
        fighter.walkAnimation = new Animation(1, 6, 6, true);
        fighter.ledgeAnimation = new Animation(new int[] {14}, 1, true);
        fighter.description = "Big Seagull is an Elder God, a being so powerful that some other deities cannot survive its presence and as old as the universe itself. Although Big Seagull has followers from the Church of Big Seagull, it does not care for worship in the same way as earthly gods.";
        fighter.debut = "Real Life";
        fighter.costumes = 4;
        fighter.airDodgeSpeed = 14;

        flyingAnimation = new Animation(7, 10, 8, true);
        hoverAnimation = new Animation(new int[] {13, 16, 17, 18}, 10, true);
        peckAnimation = new Animation(11, 12, 6, true);
        gustAnimation = new Animation(new int[] {13, 5}, 12, true);
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 32) {
            @Override
            public void parry(Attack attack) {
                if (attack.reflectable) useNeutralAttack();
                else super.parry(attack);
            }

            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                Platform platform = getPlatformBelow();
                if (platform != null) {
                    if (distanceToLeftSide(platform.hitbox) < 32) {
                        pressKey(Keys.RIGHT);
                        return;
                    } else if (distanceToRightSide(platform.hitbox) < 32) {
                        pressKey(Keys.LEFT);
                        return;
                    }
                }

                if (isDirectlyHorizontal(target.hitbox) && Math.random() * 20 < difficulty && isFacing(targetPosition.x)) {
                    if (target.damage > 30 && Math.random() * 100 + target.damage > 100) {
                        useNeutralAttack();
                    } else {
                        if (Math.random() < .2) {
                            useDownAttack();
                        } else {
                            useSideAttack(player.direction);
                        }
                    }
                }
            }
        };
    }

    @Override
    public void update(sab.game.fighter.Fighter fighter, Player player) {
        if (player.isReady()) {
            if (Math.abs(player.velocity.x) < 0.1f && player.velocity.y == 0 && player.keys.isPressed(Keys.DOWN)) {
                player.frame = 12;
            }

            if (!player.touchingStage && !player.grabbingLedge()) {
                if (player.velocity.len() < 10) {
                    player.frame = hoverAnimation.stepLooping();
                } else {
                    player.frame = flyingAnimation.stepLooping();
                }
                float targetRotation = (float) Math.toDegrees(Math.atan2(player.velocity.y, Math.abs(player.velocity.x)))
                        * player.direction * Math.abs(player.velocity.x) / 20;
                player.rotation = player.rotation + (targetRotation - player.rotation) * .1f;
            } else {
                flyingAnimation.reset();
                hoverAnimation.reset();
                player.rotation = 0;
            }
        }

        if (player.velocity.y < 0) player.velocity.y *= 0.95f;
        if (player.usedRecovery) player.velocity.y *= 0.85f;
    }

    @Override
    public void neutralAttack(sab.game.fighter.Fighter fighter, Player player) {
        gustAnimation.reset();
        player.startAttack(new Gust(), gustAnimation, 1, 24, true);
        SABSounds.playSound("gust.mp3");
    }

    @Override
    public void sideAttack(sab.game.fighter.Fighter fighter, Player player) {
        peckAnimation.reset();
        player.startAttack(new Peck(), peckAnimation, 1, 12, true);
    }

    @Override
    public void upAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            hoverAnimation.reset();
            player.startAttack(new Glide(), hoverAnimation, 4, 180, true);
            player.velocity.y = 12;
            SABSounds.playSound("gust.mp3");
            player.usedRecovery = true;
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        gustAnimation.reset();
        player.startAttack(new FeatherDart(), gustAnimation, 1, 24, true, new int[] {0});
    }
}