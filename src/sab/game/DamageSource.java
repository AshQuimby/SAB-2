package sab.game;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

public class DamageSource extends GameObject {
    public int damage;
    public Vector2 knockback;
    public boolean reflectable;
    public boolean parryable;
    public boolean staticKnockback;
    public Player owner;

    public DamageSource() {
    }

    private DamageSource(int damage, Vector2 knockback, boolean reflectable, boolean parryable) {
        this.damage = damage;
        this.knockback = knockback;
        this.reflectable = reflectable;
        this.parryable = parryable;
    }

    public static DamageSource genericDamageSource(int damage, Vector2 knockback, boolean reflectable, boolean parryable) {
        return new DamageSource(damage, knockback, reflectable, parryable);
    }
}
