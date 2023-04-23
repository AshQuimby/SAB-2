package sab.game.attack.empty_soldier;

import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class ViceroyWings extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "viceroy_wings.png";
        attack.reflectable = false;
        attack.parryable = false;
        attack.life = 20;
        attack.frameCount = 5;
        attack.hitbox.width = 128;
        attack.hitbox.height = 128;
        attack.drawRect.width = 128;
        attack.drawRect.height = 128;
        attack.damage = 1;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 1;
        attack.collideWithStage = false;
    }
}
