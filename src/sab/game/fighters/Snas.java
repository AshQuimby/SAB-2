package sab.game.fighters;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
import sab.game.PlayerAction;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.GlasterBaster;
import sab.game.attacks.projectiles.BoneSpike;
import sab.game.attacks.projectiles.Frostball;
import sab.game.attacks.projectiles.SpinnyBone;
import sab.game.particles.Particle;
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
        fighter.acceleration = .28f;
        fighter.jumpHeight = 130;
        fighter.friction = .225f;
        fighter.mass = 5.4f;
        fighter.jumps = 1;
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
        if (player.usedRecovery && !player.stuckCondition() && beheaded) {
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
            player.startAttack(new Attack(new BoneSpike(), player), attackAnimation, 12, 18, false, new int[] {0});
            player.velocity.y /= 3;
            player.velocity.x *= 0.9f;
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery || beheaded) {
            attackAnimation.reset();
            player.startAttack(new Attack(new SpinnyBone(), player), attackAnimation, 4, 24, false);
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery && !beheaded) {
            player.usedRecovery = true;
            SABSounds.playSound("crunch.mp3");
            Particle particle = new Particle(0.25f, player.hitbox.getCenter(new Vector2()), player.velocity.cpy(), 60, 44, 2, Utils.appendCostumeToFilename("snas_body", player.costume, "png"));
            particle.direction = player.direction;
            player.battle.addParticle(particle);
            player.velocity.y = 28;
            player.removeJumps();
            beheaded = true;
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
