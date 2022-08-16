package sab.game.fighters;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.ChainSlash;
import sab.game.attacks.projectiles.BoomerangKnife;

public class Chain extends FighterType {
    public boolean hasBoomerang;

    private Animation swingAnimation;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "chain";
        fighter.name = "Chain";
        fighter.costumes = 0;
        fighter.hitboxWidth = 32;
        fighter.hitboxHeight = 52;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 6;
        fighter.speed = .42f;
        fighter.jumpHeight = 128;
        fighter.frames = 9;
        fighter.friction = .03f;
        fighter.mass = 43.6f;
        fighter.walkAnimation = new Animation(0, 4, 10, true);

        hasBoomerang = true;
        swingAnimation = new Animation(new int[] {4, 5, 3}, 5, true);
    }

    @Override
    public void update(Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        swingAnimation.reset();
        player.startAttack(new Attack(new ChainSlash(), player), swingAnimation, 6, 8);
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (hasBoomerang) {
            swingAnimation.reset();
            player.startAttack(new Attack(new BoomerangKnife(), player), swingAnimation, 6, 12);
            hasBoomerang = false;
        } else {
            neutralAttack(fighter, player);
        }
    }
}