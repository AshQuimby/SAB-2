package sab.game.fighter;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.melees.SussyVent;
import sab.game.attack.melees.Tongue;
import sab.game.attack.projectiles.Bullet;
import sab.game.attack.projectiles.MiniGus;

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
    public void neutralAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            shootAnimation.reset();
            player.startAttack(new Attack(new Bullet(), player), shootAnimation, 5, 5, false);
        }
    }

    @Override
    public void sideAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            tongueAnimation.reset();
            player.startAttack(new Attack(new Tongue(), player), tongueAnimation, 1, 15, false);
        }
    }

    @Override
    public void downAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (miniGus == null || !miniGus.alive) {
                placeMiniGusAnimation.reset();
                miniGus = new Attack(new MiniGus(), player);
                player.startAttack(miniGus, placeMiniGusAnimation, 8, 12, false);
            }
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startAttack(new Attack(new SussyVent(), player), 8, 12, false);
            player.removeJumps();
        }
    }
}