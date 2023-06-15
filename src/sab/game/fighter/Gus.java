package sab.game.fighter;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.DamageSource;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.action.PlayerAction;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.emperor_evil.Banana;
import sab.game.attack.gus.*;
import sab.game.stage.Ledge;
import sab.game.stage.Platform;
import sab.net.Keys;
import sab.util.SABRandom;

public class Gus extends FighterType {
    private Animation shootAnimation;
    private Animation tongueAnimation;
    private Animation placeMiniGusAnimation;
    private Animation idleBreatheAnimation;
    private Animation workOutAnimation;
    private Animation jogAnimation;
    private Animation punchHookAnimation;
    private Animation punchUppercutAnimation;
    private Animation flipAnimation;
    private Animation ventSlapAnimation;
    private Animation suplexGrabAnimation;
    private int amongUsManBreathTimer;
    private int amongUsManTimeLeft;
    private Attack miniGus;
    private boolean amongUsManMode;
    private boolean landingChecker;

    @Override
    public void setDefaults(sab.game.fighter.Fighter fighter) {
        setGusDefaults(fighter);
        fighter.description = "First name Amon, This hazmat suit wearing astronaut is always getting into trouble no matter where they go. Sometimes they're the alien, sometimes they're chased by the alien, but they can never seem to catch a break.";
        fighter.debut = "Around Ourselves";
        fighter.costumes = 6;

        // Attack animations
        shootAnimation = new Animation(9, 10, 5, true);
        tongueAnimation = new Animation(4, 5, 7, true);
        placeMiniGusAnimation = new Animation(new int[] { 11, 0 }, 8, true);
        idleBreatheAnimation = new Animation(new int[] { 14, 15, 16, 17 }, 30, true);
        workOutAnimation = new Animation(new int[] { 18, 19, 20, 21 }, 12, true);
        jogAnimation = new Animation(new int[] { 22, 23, 24, 25 }, 10, true);
        flipAnimation = new Animation(new int[] { 36 }, 1000, true);
        ventSlapAnimation = new Animation(40, 46, 6, true);
        suplexGrabAnimation = new Animation(47, 49, 12, true);
    }

    public void setGusDefaults(Fighter fighter) {
        amongUsManMode = false;
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
        fighter.friction = 0.175f;
        fighter.doubleJumpMultiplier = 0.75f;
        fighter.mass = 2.8f;
        fighter.airJumps = 1;
        fighter.ledgeAnimation = new Animation(new int[]{8}, 1, false);
        fighter.knockbackAnimation = new Animation(new int[]{7}, 1, false);
        fighter.freefallAnimation = new Animation(new int[]{6}, 1, false);
        fighter.idleAnimation = new Animation(new int[]{0}, 60, false);
        fighter.walkAnimation = new Animation(0, 3, 7, true);
        fighter.ledgeAnimation = new Animation(new int[]{ 8 }, 1, false);
        fighter.parryAnimation = new Animation(new int[]{5, 0, 0}, 10, false);
        fighter.airDodgeSpeed = 6;
        fighter.useWalkAnimationInAir = true;
    }

    public void setAmongUsManDefaults(Fighter fighter, Player player) {
        amongUsManTimeLeft = 600;
        amongUsManMode = true;
        fighter.id = "amon_gus_man";
        fighter.name = "Gus";
        fighter.hitboxWidth = 48;
        fighter.hitboxHeight = 92;
        fighter.renderWidth = 96;
        fighter.renderHeight = 112;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = (112 - 92) / 2 - 8;
        fighter.frames = 54;
        fighter.speed = 4.8f;
        fighter.acceleration = 0.38f;
        fighter.jumpHeight = 145;
        fighter.friction = 0.125f;
        fighter.doubleJumpMultiplier = 0.85f;
        fighter.mass = 1000f;
        fighter.airJumps = 1;
        fighter.walkAnimation = new Animation(1, 6, 7, true) {
            @Override
            public void onFrameChange(int newFrame) {
                if ((newFrame) % 3 == 0) {
                    impact(player);
                }
            }
        };

        punchHookAnimation = new Animation(26, 30, 6, true) {
            @Override
            public void onFrameChange(int newFrame) {
                player.move(new Vector2(8 * player.direction, 0));
            }
        };

        punchUppercutAnimation = new Animation(31, 35, 6, true) {
            @Override
            public void onFrameChange(int newFrame) {
                player.move(new Vector2(8 * player.direction, 0));
            }
        };

        fighter.knockbackAnimation = new Animation(new int[] { 11, 12, 13, 14 }, 5, true);
        fighter.ledgeAnimation = new Animation(new int[] { 7 }, 1, true);
        fighter.freefallAnimation = new Animation(new int[] { 9 }, 1, true);
        fighter.parryAnimation = new Animation(new int[] { 53, 0, 0 }, 10, false);
        fighter.airDodgeSpeed = 20;
        fighter.useWalkAnimationInAir = false;
    }

    private void impact(Player player) {
        player.battle.shakeCamera(6);
        SABSounds.playSound("john_step.mp3");
    }

    @Override
    public void start(Fighter fighter, Player player) {
    }

    @Override
    public boolean onHit(Fighter fighter, Player player, DamageSource source) {
        if (amongUsManMode) amongUsManTimeLeft -= source.damage / 2;
        return super.onHit(fighter, player, source);
    }

    @Override
    public void hitObject(Fighter fighter, Player player, Attack attack, GameObject hit) {
        if (amongUsManMode) amongUsManTimeLeft += attack.damage * 2;
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

                if (SABRandom.random() * 15 > difficulty) return;
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
    public void update(Fighter fighter, Player player) {
        if (amongUsManMode) {
            if (amongUsManTimeLeft > 0) {
                if (--amongUsManTimeLeft <= 0 && !player.hasAction()) {
                    player.velocity.scl(0);
                    player.startAttack(new SussyVent(), flipAnimation, 8, 12, true, new int[] { 2 });
                }
            }
            if (!player.hitbox.overlaps(player.battle.getStage().getSafeBlastZone()) && player.hitbox.y < 0) {
                player.hitbox.setCenter(player.battle.getStage().getSafeBlastZone().getCenter(new Vector2()).add(0, player.battle.getStage().getUnsafeBlastZone().height / 2));
            }
            if (player.touchingStage) {
                if (!landingChecker) {
                    impact(player);
                }
                landingChecker = true;
            } else {
                landingChecker = false;
            }

            if (player.isReady()) {
                if (!player.touchingStage) {
                    if (player.velocity.y < 0) {
                        player.frame = 9;
                    } else {
                        player.frame = 8;
                    }
                } else {
                    if (amongUsManBreathTimer == 120) {
                        switch (SABRandom.random(2)) {
                            case 0 :
                                fighter.idleAnimation = workOutAnimation;
                                workOutAnimation.reset();
                                break;
                            case 1 :
                                fighter.idleAnimation = idleBreatheAnimation;
                                idleBreatheAnimation.reset();
                                break;
                            case 2 :
                                fighter.idleAnimation = jogAnimation;
                                jogAnimation.reset();
                                break;
                        }
                    } else if (amongUsManBreathTimer < 120) {
                        fighter.idleAnimation = new Animation(new int[] { 0 }, 1, true);
                    }
                }
            }
            if (player.isStationary()) {
                amongUsManBreathTimer++;
            } else {
                amongUsManBreathTimer = 0;
            }
        }
    }

    @Override
    public void neutralAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (amongUsManMode) {
                suplexGrabAnimation.reset();
                player.startAttack(new AmonGrab(), suplexGrabAnimation, 12, 24, true, null);
            } else {
                shootAnimation.reset();
                player.startRepeatingAttack(new Bullet(), shootAnimation, 5, 5, false, null);
            }
        }
    }

    @Override
    public void sideAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (amongUsManMode) {
                if (player.velocity.y < 0) player.velocity.y = 0;
                player.velocity.x *= 0.5f;
                punchHookAnimation.reset();
                player.startAttack(new AmonGusManPunch(), punchHookAnimation, 12, 18, true, new int[0]);
            } else {
                tongueAnimation.reset();
                player.startAttack(new Tongue(), tongueAnimation, 4, 10, false);
            }
        }
    }

    @Override
    public void onEndAction(PlayerAction action, Fighter fighter, Player player) {
        if (amongUsManMode && amongUsManTimeLeft > 0) {
            // Side punch combo
            if (player.keys.isPressed(Keys.ATTACK)) {
                if (action.usingAnimation(punchHookAnimation)) {

                    // Start the next hit of the combo if the player is still holding attack
                    if (player.velocity.y < 0) player.velocity.y = 0;
                    player.velocity.x *= 0.5f;
                    punchUppercutAnimation.reset();
                    player.startAttack(new AmonGusManPunch(), punchUppercutAnimation, 12, 18, true, new int[0]);
                } else if (action.usingAnimation(punchUppercutAnimation)) {

                    // Allow the player to change directions when finishing the combo
                    if (player.keys.isPressed(Keys.RIGHT) && player.direction == -1) player.direction = 1;
                    else if (player.keys.isPressed(Keys.LEFT) && player.direction == 1) player.direction = -1;

                    // Restart the combo
                    sideAttack(fighter, player);
                }
            }
        }
    }

    @Override
    public void downAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (amongUsManMode) {
                if (player.touchingStage) {
                    ventSlapAnimation.reset();
                    player.velocity.scl(0);
                    player.startAttack(new VentSlap(), ventSlapAnimation, 12, 30, true);
                } else {
                    flipAnimation.reset();
                    player.startIndefiniteAttack(new AmonGrandSlam(), flipAnimation, 4, true);
                }
            } else {
                if (miniGus == null || !miniGus.alive || amongUsManMode) {
                    placeMiniGusAnimation.reset();
                    miniGus = player.startAttack(new MiniGus(), placeMiniGusAnimation, 8, 12, false);
                }
            }
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (amongUsManMode) {
                flipAnimation.reset();
                player.startAttack(new SussyVent(), flipAnimation, 8, 12, true, new int[] { 1 });
            } else {
                player.startAttack(new SussyVent(), 8, 12, false);
                player.removeJumps();
            }
        }
    }

    @Override
    public boolean finalAss(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            fighter.freefallAnimation.reset();
            player.startAttack(new SussyVent(), fighter.freefallAnimation, 8, 12, true, new int[] { 0 });
            return true;
        }
        return false;
    }

    @Override
    public void onSuccessfulParry(Fighter fighter, Player player, DamageSource parried) {
        // Retaliate if opponent is near and on a successful parry
        if (amongUsManMode && parried.owner != null) {
            if (parried.owner.getCenter().dst(player.getCenter()) < 96) {
                player.direction = (int) Math.signum(parried.owner.getCenter().x - player.getCenter().x);
                player.velocity.scl(0);
                punchHookAnimation.reset();
                player.startAttack(new AmonGusManPunch(), punchHookAnimation, 12, 18, true);
            }
        }
    }

    @Override
    public void onJump(Fighter fighter, Player player, boolean doubleJump) {
        if (amongUsManMode && !doubleJump) {
            impact(player);
        }
    }

    @Override
    public String getVictorySongId(Fighter fighter, Player player) {
        if (player.costume == 4) {
            if (amongUsManMode) return "deal_tastic_victory.mp3";
            else return "minion_victory.mp3";
        }
        return super.getVictorySongId(fighter, player);
    }
}