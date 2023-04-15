package sab.game.fighter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.Player;
import sab.game.PlayerAction;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.marvin.Wrench;
import sab.game.attack.marvin.Fireball;
import sab.game.attack.marvin.Frostball;
import sab.game.attack.marvin.Toilet;
import sab.game.particle.Particle;
import sab.game.screen.VictoryScreen;

public class John extends FighterType {
    private Animation swingAnimation;
    private Animation squatAnimation;
    private Animation chargeAnimation;
    private Animation throwAnimation;
    private Animation johnWalkAnimation;
    private boolean justWon;

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
        fighter.frames = 14;
        fighter.jumpHeight = 160;
        fighter.friction = .2f;
        fighter.acceleration = 0.2f;
        fighter.mass = 8.5f;
        fighter.jumps = 5;
        fighter.doubleJumpMultiplier = 0.8f;

        fighter.description = "American attorney John Joseph has a spotless legal record. Despite this, he finds it tough to get work after he was cursed and turned into a pink sphere for all eternity. In this state he worked exclusively his upper body so much that his legs atrophied to the point of being vestigial.";
        fighter.debut = "John Joseph's Nightmare World";
        fighter.walkAnimation = new Animation(0, 3, 12, true);
        swingAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
        squatAnimation = new Animation(new int[] {6}, 4, true);
        chargeAnimation = new Animation(new int[] {9}, 4, true);
        throwAnimation = new Animation(new int[] {10, 11}, 6, true);
        fighter.freefallAnimation = new Animation(new int[]{8}, 1, true);
        fighter.ledgeAnimation = new Animation(new int[] {10}, 1, true);
        fighter.knockbackAnimation = new Animation(new int[] {9}, 1, true);
        fighter.costumes = 4;
        justWon = true;
    }

    @Override
    public void start(Fighter fighter, Player player) {
        // Declare the special method body here so that way the sound doesn't play in the character select screen
        johnWalkAnimation = new Animation(0, 3, 12, true) {
            @Override
            public void onFrameChange(int newFrame) {
                if (newFrame % 2 == 0) {
                    player.battle.shakeCamera(3);
                    SABSounds.playSound("john_step.mp3");
                }
            }
        };
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (!player.isStuck() && !player.touchingStage) {
            if (player.velocity.y > 0) {
                player.frame = 7;
            } else {
                player.frame = 8;
            }
            fighter.walkAnimation = fighter.freefallAnimation;
        } else {
            fighter.walkAnimation = johnWalkAnimation;
        }
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startAttack(new Fireball(), swingAnimation, 6, 10, false);
            player.velocity.y /= 3;
            player.velocity.x *= 0.9f;
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startAttack(new Wrench(), swingAnimation, 4, 18, false);
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            squatAnimation.reset();
            player.startAttack(new Toilet(), squatAnimation, 4, 30, false);
            player.removeJumps();
            player.usedRecovery = true;
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startChargeAttack(new PlayerAction(5, true, 0), 5, 60);
        }
    }

    @Override
    public void chargeAttack(Fighter fighter, Player player, int charge) {
        throwAnimation.reset();
        player.startAttack(new Frostball(), throwAnimation, 7, 10, false, new int[]{charge});
        player.velocity.y /= 3;
        player.velocity.x *= 0.9f;
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
                SABSounds.stopMusic();
                SABSounds.playMusic(getVictorySongId(fighter, player), false);
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
