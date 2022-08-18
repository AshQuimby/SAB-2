package sab.game;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

public class DamageSource extends GameObject {
    public int damage;
    public Vector2 knockback;
    public boolean reflectable;
    public Player owner;

    @Override
    public void update() {
    }
}
