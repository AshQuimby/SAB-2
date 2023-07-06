package sab.game.attack.snas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class BasterBeam extends AttackType {

    private boolean horizontal;
    private boolean charge;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "glaster_baster.png";
        attack.life = 15;
        attack.frameCount = 4;
        attack.hitbox.width = 40;
        attack.hitbox.height = 40;
        attack.drawRect.width = 40;
        attack.drawRect.height = 40;
        attack.damage = 1;
        attack.directional = true;
        attack.reflectable = false;
        attack.parryable = false;
    }
    
    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(86 * attack.owner.direction, 8));
        attack.resize(40 + Math.abs(data[1]) * 360, 40 + Math.abs(data[0]) * 360);
        attack.hitbox.x += (attack.hitbox.width / 2 - 20) * data[1];
        attack.hitbox.y += (attack.hitbox.height / 2 - 20) * data[0];
        attack.damage += Math.round(data[2] / 30f) + Math.min(1, 1 * attack.life % 3);
        attack.knockback = new Vector2(5 * data[1], 5 * data[0] + 2).scl(data[2] / 45f + 1);
        attack.staticKnockback = true;

        horizontal = data[0] != 0;
    }

    @Override
    public void update(Attack attack) {
        if (attack.life <= 4) attack.staticKnockback = false;
        attack.resize(attack.hitbox.width - (horizontal ? 2.6f : 0), attack.hitbox.height - (!horizontal ? 2.6f : 0));
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        if (hit instanceof Player) {
            Player player = (Player) hit;
            if (attack.life > 2) player.stun(4);
            player.battle.shakeCamera(5);
        }
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), attack.hitbox.x, attack.hitbox.y, (int) (attack.hitbox.width / (!horizontal ? 1 : 1)), (int) (attack.hitbox.height / (!horizontal ? 1 : 1)), 0, 1, 0, false, false, new Color(1, 1, 1, 0.5f));
        
        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), attack.hitbox.x + (attack.hitbox.width * (horizontal ? 0.25f : 0)), attack.hitbox.y + (attack.hitbox.height * (!horizontal ? 0.25f : 0)), (int) (attack.hitbox.width / (!horizontal ? 1 : 2)), (int) (attack.hitbox.height / (!horizontal ? 2 : 1)), 0, 1, 0, false, false, new Color(1, 1, 1, 0.75f));
    }
}