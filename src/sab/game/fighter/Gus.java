package sab.game.fighter;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Player;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.gus.SussyVent;
import sab.game.attack.gus.Tongue;
import sab.game.attack.gus.Bullet;
import sab.game.attack.gus.MiniGus;
import sab.game.stage.Ledge;
import sab.game.stage.PassablePlatform;
import sab.game.stage.Platform;
import sab.net.Keys;

public class Gus extends FighterType {
    private Animation shootAnimation;
    private Animation tongueAnimation;
    private Animation placeMiniGusAnimation;

    private Attack miniGus;

    @Override
    public void setDefaults(sab.game.fighter.Fighter fighter) {
        fighter.id = "gus";
        fighter.name = "Gus";
        fighter.hitboxWidth = 40;
        fighter.hitboxHeight = 48;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 8;
        fighter.frames = 12;
        fighter.speed = 7f;
        fighter.acceleration = .75f;
        fighter.jumpHeight = 128;
        fighter.friction = .175f;
        fighter.mass = 2.8f;
        fighter.jumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 7, true);
        fighter.description = "First name Amon, This hazmat suit wearing astronaut is always getting into trouble no matter where they go. Sometimes they're the alien, sometimes they're chased by the alien, but they can never seem to catch a break.";
        fighter.debut = "Around Ourselves";
        fighter.costumes = 4;

        shootAnimation = new Animation(9, 10, 5, true);
        tongueAnimation = new Animation(4, 5, 7, true);
        placeMiniGusAnimation = new Animation(new int[] {11, 0}, 8, false);
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 100) {
            @Override
            protected void recover(Platform targetPlatform, Ledge targetLedge) {
                Vector2 center = player.hitbox.getCenter(new Vector2());

                boolean vented = false;
                for (Attack attack : player.battle.getAttacks()) {
                    if (attack.owner == player && attack.alive && attack.type instanceof SussyVent) {
                        vented = true;
                        break;
                    }
                }

                if (vented) {
                    if (targetPlatform != null) {
                        if (isDirectlyAbove(targetPlatform.hitbox)) {
                            pressKey(Keys.ATTACK);
                            return;
                        }
                        if (isDirectlyBelow(targetPlatform.hitbox)) {
                            if (targetPlatform.isSolid()) {
                                float distanceToLeftEdge = Math.abs(targetPlatform.hitbox.x - (player.hitbox.x + player.hitbox.width));
                                float distanceToRightEdge = Math.abs(targetPlatform.hitbox.x + targetPlatform.hitbox.width - player.hitbox.x);
                                pressKey(distanceToLeftEdge < distanceToRightEdge ? Keys.LEFT : Keys.RIGHT);
                            } else {
                                pressKey(Keys.UP);
                            }

                            return;
                        }

                        float platformCenterX = targetPlatform.hitbox.x + targetPlatform.hitbox.width / 2;
                        if (center.x < platformCenterX) pressKey(Keys.RIGHT);
                        else if (center.x > platformCenterX) pressKey(Keys.LEFT);

                        if (player.hitbox.y < targetPlatform.hitbox.y + targetPlatform.hitbox.height) pressKey(Keys.UP);
                    } else if (targetLedge != null) {
                        Vector2 ledgePosition = targetLedge.grabBox.getCenter(new Vector2());
                        if (center.x < ledgePosition.x) pressKey(Keys.RIGHT);
                        if (center.x > ledgePosition.x) pressKey(Keys.LEFT);
                        if (center.y < ledgePosition.y) pressKey(Keys.UP);
                        if (center.y > ledgePosition.y) pressKey(Keys.DOWN);
                    }
                } else {
                    if (player.getRemainingJumps() == 0 && !player.hasAction()) {
                            pressKey(Keys.UP);
                            pressKey(Keys.ATTACK);
                    } else {
                        if (player.velocity.y <= 0) {
                            pressKey(Keys.UP);
                        }
                    }
                }
            }

            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                preferredHorizontalDistance = Math.max(80, 100 - target.damage / 120 * 20);

                boolean vented = false;
                for (Attack attack : player.battle.getAttacks()) {
                    if (attack.owner == player && attack.alive && attack.type instanceof SussyVent) {
                        vented = true;
                        break;
                    }
                }
                if (vented) {
                    if (targetPosition.y > center.y + player.hitbox.height && target.touchingStage) {
                        pressKey(Keys.UP);
                    } else {
                        pressKey(Keys.ATTACK);
                        return;
                    }
                }

                if (Math.random() * 15 > difficulty) return;
                if (isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x)) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);

                    if (horizontalDistance <= 120) {
                        useSideAttack();
                    } else {
                        Gus gus = (Gus) player.fighter.type;
                        if (gus.miniGus == null || !gus.miniGus.alive) {
                            useDownAttack();
                        } else {
                            useNeutralAttack();
                        }
                    }
                } else if (isDirectlyBelow(target.hitbox)) {
                    for (GameObject platform : player.battle.getPassablePlatforms()) {
                        if (isDirectlyBelow(platform.hitbox)) {
                            pressKey(Keys.UP);
                            pressKey(Keys.ATTACK);
                            break;
                        }
                    }
                }
            }
        };
    }

    @Override
    public void neutralAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            shootAnimation.reset();
            player.startAttack(new Bullet(), shootAnimation, 5, 5, false);
        }
    }

    @Override
    public void sideAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            tongueAnimation.reset();
            player.startAttack(new Tongue(), tongueAnimation, 1, 15, false);
        }
    }

    @Override
    public void downAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (miniGus == null || !miniGus.alive) {
                placeMiniGusAnimation.reset();
                miniGus = player.startAttack(new MiniGus(), placeMiniGusAnimation, 8, 12, false);
            }
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startAttack(new SussyVent(), 8, 12, false);
            player.removeJumps();
        }
    }
}