package sab.game.fighters;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.AirSlash;
import sab.game.attacks.melees.ChainSlash;
import sab.game.attacks.projectiles.BoomerangKnife;
import sab.game.attacks.projectiles.FallingKnife;

public class Chain extends FighterType {
    public boolean hasBoomerang;

    private Animation swingAnimation;
    private Animation flyingAnimation;

    private Attack fallingKnife;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "chain";
        fighter.name = "Chain";
        fighter.hitboxWidth = 32;
        fighter.hitboxHeight = 52;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 6;
        fighter.jumps = 1;
        fighter.speed = .42f;
        fighter.jumpHeight = 128;
        fighter.frames = 9;
        fighter.friction = .1f;
        fighter.mass = 4.36f;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        fighter.costumes = 3;

        hasBoomerang = true;
        swingAnimation = new Animation(new int[] {4, 5, 3}, 5, true);
        flyingAnimation = new Animation(new int[] {6}, 5, true);
    }

    @Override
    public void update(Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        swingAnimation.reset();
        player.startAttack(new Attack(new ChainSlash(), player), swingAnimation, 6, 8, false);
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (hasBoomerang) {
            swingAnimation.reset();
            player.startAttack(new Attack(new BoomerangKnife(), player), swingAnimation, 6, 12, false);
            hasBoomerang = false;
        } else {
            neutralAttack(fighter, player);
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        player.startAttack(new Attack(new AirSlash(), player), flyingAnimation, 1, 40, false);
        player.removeJumps();
        player.velocity.y = 4;
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (fallingKnife != null) {
            fallingKnife.alive = false;
            fallingKnife = null;
        }

        fallingKnife = new Attack(new FallingKnife(), player);
        swingAnimation.reset();
        player.startAttack(fallingKnife, swingAnimation, 6, 16, false);
    }
}