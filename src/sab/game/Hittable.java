package sab.game;

public interface Hittable {
    // Code executed when something implementing this is hit, returns whether or not the hit was successful
    boolean onHit(DamageSource source);
}
