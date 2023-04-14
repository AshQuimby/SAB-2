package sab.game.attack.snas;

import com.badlogic.gdx.math.Vector2;

import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.net.Keys;

public class GlasterBaster extends MeleeAttackType {
    private Direction basterDirection;
    private boolean fired;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "glaster_baster.png";
        attack.hitbox.width = 76;
        attack.hitbox.height = 76;
        attack.drawRect.set(attack.hitbox);
        attack.reflectable = false;
        attack.life = 60;
        attack.frameCount = 5;
        attack.hitCooldown = 8;
        attack.damage = 0;
        attack.canHit = false;
        attack.frameCount = 2;
        fired = false;

        basterDirection = attack.owner.direction == 1 ? Direction.RIGHT : Direction.LEFT;
        offset = new Vector2();
        usePlayerDirection = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        offset = new Vector2(86 * attack.owner.direction, 8);

        attack.direction = 1;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);

        if (!fired) {
            if (!attack.owner.charging()) {
                attack.kill();
            }
            attack.life++;
            if (attack.owner.getCharge() >= 60) {
                attack.frame = 1;
            }
            if (attack.owner.getUsedCharge() > 0) {
                fired = true;
                attack.owner.battle.addAttack(new Attack(new BasterBeam(), attack.owner), new int[]{ basterDirection == Direction.UP ? 1 : basterDirection == Direction.DOWN ? -1 : 0, basterDirection == Direction.RIGHT ? 1 : basterDirection == Direction.LEFT ? -1 : 0, attack.owner.getUsedCharge()});
                SABSounds.playSound("glaster_baster.mp3");
                attack.life = 15;
            }

            if (attack.owner.keys.isJustPressed(Keys.UP)) {
                basterDirection = Direction.UP;
            }
            if (attack.owner.keys.isJustPressed(Keys.DOWN)) {
                basterDirection = Direction.DOWN;
            }
            if (attack.owner.keys.isJustPressed(Keys.LEFT)) {
                basterDirection = Direction.LEFT;
            }
            if (attack.owner.keys.isJustPressed(Keys.RIGHT)) {
                basterDirection = Direction.RIGHT;
            }
    
            switch (basterDirection) {
                case UP -> {
                    attack.rotation = 90;
                }
                
                case DOWN -> {
                    attack.rotation = 270;
                }
                
                case LEFT -> {
                    attack.rotation = 180;
                }
                
                case RIGHT -> {
                    attack.rotation = 0;
                }
                
                case NONE -> {
                    attack.rotation = attack.direction * 90 + 90;
                }
            }
        } else {
            if (attack.owner.isStuck()) {
                attack.kill();
            }
            attack.frame = 1;
        }
    }
}
