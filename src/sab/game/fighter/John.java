package sab.game.fighter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.Player;
import sab.game.SabSounds;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.john.*;
import sab.game.particle.Particle;
import sab.game.screen.battle_adjacent.VictoryScreen;
import sab.net.Keys;
import sab.util.SabRandom;

public class John extends FighterType {
    private Animation suckAnimation;
    private Animation punchAnimation;
    private Animation slamAnimation;
    private Animation johnWalkAnimation;
    private Animation downPunchAnimation;
    private Animation swingGavelAnimation;
    private boolean justWon;
    private int ballTransformTime;

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
        fighter.frames = 16;
        fighter.jumpHeight = 160;
        fighter.friction = .2f;
        fighter.acceleration = 0.2f;
        fighter.mass = 8.5f;
        fighter.airJumps = 3;
        fighter.doubleJumpMultiplier = 0.6f;
        fighter.airDodgeSpeed = 5;

        fighter.description = "American attorney John Joseph has a spotless legal record. Despite this, he finds it tough to get work after he was cursed and turned into a pink sphere for all eternity. In this state he worked exclusively his upper body so much that his legs atrophied to the point of being vestigial.";
        fighter.debut = "John Joseph: Chief Prosecutor";
        fighter.walkAnimation = new Animation(0, 3, 12, true);
        suckAnimation = new Animation(new int[] { 0, 0, 12 }, 7, true);
        punchAnimation = new Animation(new int[] { 4, 4, 5, 5, 6, 7, 7 }, 4, true);
        slamAnimation = new Animation(new int[] { 8 }, 4, true);
        downPunchAnimation = new Animation(new int[] { 14 }, 21, true);
        swingGavelAnimation = new Animation(new int[] { 15 }, 90, true);
        fighter.freefallAnimation = new Animation(new int[] { 9 }, 1, true);
        fighter.ledgeAnimation = new Animation(new int[] { 11 }, 1, true);
        fighter.knockbackAnimation = new Animation(new int[] { 10 }, 1, true);
        fighter.airDodgeAnimation = new Animation(new int[] { 10 }, 1, true);
        fighter.costumes = 4;
        justWon = true;
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 16) {
            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                if (!player.touchingStage && player.hasAction() && getPlatformBelow() != null) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);
                    if (horizontalDistance <= 80) pressKey(Keys.ATTACK);
                }

                if (isFacing(targetPosition.x)) {
                    int ticksUntilInRange = 0;
                    for (int i = 16; i <= 28; i++) {
                        float predictedX = center.x + player.velocity.x * i / 2f;
                        float predictedOpponentX = targetPosition.x + target.velocity.x * i;
                        if (Math.abs(predictedX - predictedOpponentX) <= 60) {
                            ticksUntilInRange = i;
                            break;
                        }
                    }

                    if (ticksUntilInRange >= 16 && SabRandom.random() * 25 < difficulty) {
                        useSideAttack();
                    } else if (player.touchingStage && isDirectlyHorizontal(target.hitbox) && (Math.abs(center.x - targetPosition.x) < 120 || !target.touchingStage)) {
                        useDownAttack();
                    }
                } else if (isDirectlyAbove(target.hitbox) && SabRandom.random() * 20 < difficulty && !target.hasAction()) {
                    useDownAttack();
                } else if (SabRandom.random() * 25 < difficulty) {
                    useUpAttack();
                }
            }
        };
    }

    @Override
    public void start(Fighter fighter, Player player) {
        // Declare the special method body here so that way the sound doesn't play in the character select screen or crash from a null reference to a battle
        johnWalkAnimation = new Animation(0, 3, 12, true) {
            @Override
            public void onFrameChange(int newFrame) {
                if (newFrame % 2 == 0) {
                    player.battle.shakeCamera(3);
                    SabSounds.playSound("john_step.mp3");
                }
            }
        };
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (!player.isStuck() && !player.hasAction() && !player.touchingStage) {
            if (player.velocity.y > 0) {
                player.frame = 8;
            } else {
                player.frame = 9;
            }
            fighter.walkAnimation = fighter.freefallAnimation;
        } else {
            fighter.walkAnimation = johnWalkAnimation;
        }

        if (ballTransformTime > 0) {
            ballTransformTime--;
            if (ballTransformTime > 60) {
                if (ballTransformTime % 16 == 0) SabSounds.playSound("swish.mp3");
                player.frame = 12;
            } else {
                if (ballTransformTime % 8 == 0) SabSounds.playSound("swish.mp3");
                player.frame = 13;
            }
            Vector2 particleSpawnPos = player.getCenter().add(new Vector2(128 * player.direction, 0).rotateDeg(SabRandom.random(-30f, 30f)));
            Vector2 particleVelocity = player.getCenter().sub(particleSpawnPos).scl(0.05f);
            player.battle.addParticle(new Particle(particleSpawnPos, particleVelocity, 24, 24, "smoke.png"));
            if (ballTransformTime == 0) {
                player.battle.createAttack(new JohnBall(), player, null);
            }
        }
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            suckAnimation.reset();
            player.startAttack(new JohnSuck(), suckAnimation, 14, 10, false);
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            punchAnimation.reset();
            player.startAttack(new JohnPunch(), punchAnimation, 16, 12, true);
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            slamAnimation.reset();
            player.startIndefiniteAttack(new JohnSlam(), slamAnimation, 4, false);
            player.removeJumps();
            player.usedRecovery = true;
            player.velocity.y = 0;
            player.velocity.x *= 0.3f;
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (player.touchingStage) {
                swingGavelAnimation.reset();
                player.velocity.x *= 0.5f;
                player.startIndefiniteAttack(new GavelSlam(), swingGavelAnimation, 1, false);
            } else {
                downPunchAnimation.reset();
                player.velocity.y *= 0f;
                player.startAttack(new GavelSpin(), downPunchAnimation, 6, 30, false);
            }
        }
    }

    @Override
    public boolean finalAss(Fighter fighter, Player player) {
        player.stun(120);
        player.setIFrames(120);
        ballTransformTime = 120;
        return true;
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
                SabSounds.stopMusic();
                SabSounds.playMusic(getVictorySongId(fighter, player), false);
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
