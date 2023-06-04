package sab.game.fighter;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.*;
import sab.game.action.PlayerAction;
import sab.game.ai.AI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;

public class Fighter implements Cloneable {
    public final FighterType type;
    /*
     * The "id" value may be confusing, but it works internally
     * Instead of having to have multiple different strings for every possible image the character uses
     * we use "id" to tell the program the first word in all the image files.
     * This does mean that the names of images for fighters have to be standardized:
     * 
     * In battle art is "<id>.png"
     * Character select screen renders are "<id>_render.png"
     * Alternate costumes are followed with "_alt_<costume_number>" before ".png"
     */
    public String id;

    // The name of the character in the character select screen
    public String name;

    // The number of frames in the character's spritesheet
    public int frames;

    // The number of costumes the character has
    public int costumes;

    // The width & height of the character's hitbox
    public int hitboxWidth;
    public int hitboxHeight;

    // The width and height of the character when rendered
    public int renderWidth;
    public int renderHeight;

    // The offset of the character's hitbox (This allows you to have hitboxes that line up with the image of the character)
    public int imageOffsetX;
    public int imageOffsetY;

    // The offset of held items based off of the selected fighter
    public Vector2 itemOffset;

    // The number of midair jumps this character can use
    public int airJumps;

    // The description of the character to display in the "Fighters" screen
    public String description;

    // The character's debut to display in the "Fighters" screen
    public String debut;

    // The maximum horizontal speed of the character in pixels/tick
    public float speed;

    // The horizontal acceleration of the character in pixels/tick
    public float acceleration;

    // The number of pixels high the character jumps by default and multiplier for mid-air jumps
    public float jumpHeight;
    public float doubleJumpMultiplier;

    // The speed at which a fighter will move after air-dodging in a direction
    public float airDodgeSpeed;

    // The multiplier of the player's velocity, larger numbers make characters more "slippery" (greater than zero but less than one)
    public float friction;

    // Increases fall speed and decreases knockback
    public float mass;

    // Whether or not this fighter will use their walk animation in midair, useful for custom midair animations.
    public boolean useWalkAnimationInAir;

    // The animations all characters must have to function
    public Animation walkAnimation;
    public Animation ledgeAnimation;
    public Animation knockbackAnimation;
    public Animation freefallAnimation;
    public Animation idleAnimation;
    public Animation parryAnimation;

    public Fighter(FighterType type) {
        id = "fighter";
        name = "Fighter";
        costumes = 2;
        hitboxWidth = 64;
        hitboxHeight = 64;
        renderWidth = 64;
        renderHeight = 64;
        imageOffsetX = 0;
        imageOffsetY = 0;
        speed = 12;
        acceleration = 0.45f;
        airJumps = 1;
        doubleJumpMultiplier = 0.75f;
        jumpHeight = 50;
        friction = .05f;
        airDodgeSpeed = 8;
        mass = 1;
        walkAnimation = null;
        ledgeAnimation = new Animation(new int[]{8}, 1, false);
        knockbackAnimation = new Animation(new int[]{7}, 1, false);
        freefallAnimation = new Animation(new int[]{6}, 1, false);
        idleAnimation = new Animation(new int[]{0}, 60, false);
        parryAnimation = new Animation(new int[]{5, 0, 0}, 10, false);
        description = "This is the default description. Change it by changing the \"description\" field. Text wrapping is handled by the engine so you don't have to add new lines.";
        debut = "This is the debut, it is the \"game\" your character originates from.";
        itemOffset = new Vector2();
        this.type = type;
        useWalkAnimationInAir = true;
        type.setDefaults(this);
    }

    public void start(Player player) {
        type.start(this, player);
    }

    public AI getAI(Player player, int difficulty) {
        return type.getAI(player, difficulty);
    }

    public void update(Player player) {
        type.update(this, player);
    }

    public void neutralAttack(Player player) {
        type.neutralAttack(this, player);
    }

    public void sideAttack(Player player) {
        type.sideAttack(this, player);
    }

    public void upAttack(Player player) {
        type.upAttack(this, player);
    }

    public void downAttack(Player player) {
        type.downAttack(this, player);
    }

    public boolean finalAss(Player player) {
        return type.finalAss(this, player);
    }

    public void chargeAttack(Player player, int charge) {
        type.chargeAttack(this, player, charge);
    }

    public void charging(Player player, int charge) {
        type.charging(this, player, charge);
    }

    public boolean onHit(Player player, DamageSource source) {
        return type.onHit(this, player, source);
    }

    public void onKill(Player player) {
        type.onKill(this, player);
    }
    public void onEndAction(PlayerAction action, Player player) {
        type.onEndAction(action, this, player);
    }

    public void onJump(Player player, boolean doubleJump) {
        type.onJump(this, player, doubleJump);
    }

    public void useItem(Player player) {
        player.useItem();
        type.useItem(this, player);
    }

    public void hitObject(Player player, Attack attack, GameObject hit) {
        type.hitObject(this, player, attack, hit);
    }

    // Return false to override player's default render code
    public boolean preRender(Player player, Seagraphics g) {
        return type.preRender(this, player, g);
    }

    public void render(Player player, Seagraphics g) {
        type.render(this, player, g);
    }

    public void renderUI(Player player, Seagraphics g) {
        type.renderUI(this, player, g);
    }

    public String getVictorySongID(Player player) {
        return type.getVictorySongId(this, player);
    }

    public void onParry(Player player) {
        type.onParry(this, player);
    }

    public int getRandomCostume() {
        return type.getRandomCostume(this);
    }

    public void onSuccessfulParry(Player player, DamageSource parried) {
        type.onSuccessfulParry(this, player, parried);
    }

    public Fighter copy() {
        try {
           return (Fighter) this.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }
}
