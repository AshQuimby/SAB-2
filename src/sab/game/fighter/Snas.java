package sab.game.fighter;

import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.action.PlayerAction;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.snas.GlasterBaster;
import sab.game.attack.snas.BoneSpike;
import sab.game.attack.snas.SpinnyBone;
import sab.game.particle.Particle;
import sab.util.Utils;

public class Snas extends FighterType {
    private Animation attackAnimation;
    private Animation chargeAnimation;
    private boolean beheaded;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "snas";
        fighter.name = "Snas";
        fighter.hitboxWidth = 60;
        fighter.hitboxHeight = 80;
        fighter.renderWidth = 68;
        fighter.renderHeight = 88;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 4;
        fighter.frames = 9;
        fighter.acceleration = .34f;
        fighter.jumpHeight = 130;
        fighter.friction = .225f;
        fighter.mass = 5.4f;
        fighter.airJumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        fighter.description = "This laid back skeleton wizard doesn't always look the magical part. You should be wary as his boneomancy can pack quite a punch and he has a bone to pick with you.";
        fighter.debut = "Belowstory";
        
        beheaded = false;
        attackAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
        chargeAnimation = new Animation(new int[] {4}, 7, true);
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 3;
    }

    public void setDefaultHitbox(Fighter fighter, Player player) {
        player.resize(60, 80);
        fighter.imageOffsetY = 4;
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.usedRecovery && !player.isStuck() && beheaded) {
            player.frame = 6;
            player.resize(52, 32);
            fighter.imageOffsetY = 0;
        } else if (beheaded) {
            player.frame = 7;
            player.hitbox.y += 24;
            setDefaultHitbox(fighter, player);
            beheaded = false;
        }
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery || beheaded) {
            attackAnimation.reset();
            player.startAttack(new BoneSpike(), attackAnimation, 12, 18, false, new int[] {0});
            player.velocity.y /= 3;
            player.velocity.x *= 0.9f;
            SABSounds.playSound("snas.mp3");
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery || beheaded) {
            attackAnimation.reset();
            player.startAttack(new SpinnyBone(), attackAnimation, 4, 24, false);
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery && !beheaded) {
            player.usedRecovery = true;
            SABSounds.playSound("crunch.mp3");
            Particle particle = new Particle(0.25f, player.hitbox.getCenter(new Vector2()), player.velocity.cpy(), 60, 44, 2, Utils.appendCostumeToIdentifier("snas_body", player.costume, "png"));
            particle.direction = player.direction;
            player.battle.addParticle(particle);
            player.velocity.y = 28;
            player.removeJumps();
            beheaded = true;
            SABSounds.playSound("snas.mp3");
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery || beheaded) {
            player.startChargeAttack(new PlayerAction(4, true, 0), 30, 60);
            player.battle.addAttack(new Attack(new GlasterBaster(), player), new int[]{0});
        }
    }

    @Override
    public void chargeAttack(Fighter fighter, Player player, int charge) {
    }

    @Override
    public void charging(Fighter fighter, Player player, int charge) {
        player.velocity.y *= 0.8f;
        player.frame = chargeAnimation.stepLooping();
    }
}
