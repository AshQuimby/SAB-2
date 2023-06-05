package sab.game.attack.gus;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.fighter.Gus;
import sab.net.Keys;

public class SussyVent extends AttackType {
    private boolean bigManMode;
    private boolean transitionVent;
    private int ventCloseTime;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "vent.png";
        attack.life = 400;
        attack.frameCount = 4;
        attack.hitbox.width = 64;
        attack.hitbox.height = 64;
        attack.drawRect.width = 64;
        attack.drawRect.height = 96;
        attack.collideWithStage = true;
        attack.reflectable = false;
        attack.canHit = false;
        ventCloseTime = 10;
    }

    @Override
    public void update(Attack attack) {
        if (ventCloseTime > 0) {
            ventCloseTime--;
            attack.frame = 1;
            if (attack.life >= 395) {
                attack.frame = 2;
            }
            if (bigManMode && !transitionVent || !bigManMode && transitionVent) attack.owner.frame = 36;
            else attack.owner.frame = 6;
            if (attack.owner.takingKnockback() && !transitionVent)
                attack.alive = false;
        } else if (attack.life > 10) {
            attack.owner.hide();
            attack.owner.stun(2);
            attack.owner.invulnerable = true;

            attack.frame = 0;
            attack.owner.usedRecovery = true;
            attack.owner.hitbox.setCenter(attack.hitbox.getCenter(new Vector2()));
            attack.owner.velocity.scl(0);

            if (!transitionVent) {

                if (attack.owner.keys.isPressed(Keys.UP)) {
                    attack.velocity.y += 1f;
                    if (bigManMode) attack.velocity.y += 1f;
                }
                if (attack.owner.keys.isPressed(Keys.DOWN)) {
                    attack.velocity.y -= 1f;
                    if (bigManMode) attack.velocity.y -= 1f;
                }
                if (attack.owner.keys.isPressed(Keys.LEFT)) {
                    attack.velocity.x -= 1f;
                    if (bigManMode) attack.velocity.x -= 1f;
                }
                if (attack.owner.keys.isPressed(Keys.RIGHT)) {
                    attack.velocity.x += 1f;
                    if (bigManMode) attack.velocity.x += 1f;
                }
                if (attack.owner.keys.isJustPressed(Keys.ATTACK)) {
                    attack.life = 11;
                }

                attack.velocity.scl(.8f);
            }
        } else {
            if (attack.life == 10) SABSounds.playSound("vent_open.mp3");
            attack.velocity.set(0, 0);
            if (bigManMode) attack.owner.velocity.y = 14;
            else attack.owner.velocity.y = 5;
            attack.owner.reveal();
            if (transitionVent) {
                Gus gus = (Gus) attack.owner.fighter.type;
                if (bigManMode) {
                    SABSounds.playSound("role_reveal.mp3");
                    gus.setAmongUsManDefaults(attack.owner.fighter, attack.owner);
                } else {
                    SABSounds.playSound("emergency_meeting.mp3");
                    gus.setGusDefaults(attack.owner.fighter);
                }
                transitionVent = false;
            }
            attack.owner.invulnerable = false;
            if (attack.life > 5) {
                attack.frame = 3;
            } else {
                attack.frame = 2;
            }
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        SABSounds.playSound("vent_open.mp3");
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.owner.velocity.x = 0;
        if (data != null) {
            if (data[0] == 0) {
                attack.owner.velocity.y = 5;
                attack.life = 60;
                transitionVent = true;
                bigManMode = true;
            } else if (data[0] == 1) {
                attack.owner.velocity.y = 10;
                bigManMode = true;
                attack.life += 7;
                ventCloseTime += 7;
            } else {
                attack.owner.velocity.y = 10;
                attack.life = 67;
                transitionVent = true;
                bigManMode = false;
                ventCloseTime += 7;

            }
        } else {
            attack.owner.velocity.y = 5;
        }
    }

    @Override
    public void onKill(Attack attack) {
        attack.owner.reveal();
    }
}
