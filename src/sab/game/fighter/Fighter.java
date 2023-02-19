package sab.game.fighter;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.Player;
import sab.game.animation.Animation;

public class Fighter implements Cloneable {
    public final FighterType type;
    /*
     * The "id" value may be confusing but it works internally
     * Instead of having to have multiple different strings for every possible image the character uses
     * we use "id" to tell the program the first word in all of the image files.
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
    public int jumps;

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

    // The multiplier of the player's velocity, larger numbers make characters more "slippery" (greater than zero but less than one)
    public float friction;

    // Increases fall speed and decreases knockback
    public float mass;

    // The animations all characters have
    public Animation walkAnimation;
    public Animation ledgeAnimation;
    public Animation knockbackAnimation;
    public Animation freefallAnimation;
    public Animation idleAnimation;

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
        jumps = 1;
        doubleJumpMultiplier = 0.75f;
        jumpHeight = 50;
        friction = .05f;
        mass = 1;
        walkAnimation = null;
        ledgeAnimation = new Animation(new int[]{8}, 1, false);
        knockbackAnimation = new Animation(new int[]{7}, 1, false);
        freefallAnimation = new Animation(new int[]{6}, 1, false);
        idleAnimation = new Animation(new int[]{0}, 60, false);
        description = "This is the default description. Change it by changing the \"description\" field. Text wrapping is handled by the engine so you don't have to add new lines.";
        debut = "This is the debut, it is the \"game\" your character originates from.";
        itemOffset = new Vector2();
        this.type = type;
        type.setDefaults(this);
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

    public void chargeAttack(Player player, int charge) {
        type.chargeAttack(this, player, charge);
    }

    public void charging(Player player, int charge) {
        type.charging(this, player, charge);
    }

    public void onHit(Player player) {
        type.onHit(this, player);
    }

    public void onKill(Player player) {
        type.onKill(this, player);
    }

    public void useItem(Player player) {
        type.useItem(this, player);
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

    public Fighter copy() {
        try {
           return (Fighter) this.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }
}
