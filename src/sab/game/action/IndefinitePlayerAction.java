package sab.game.action;

import sab.game.animation.Animation;
import sab.game.attack.Attack;

public class IndefinitePlayerAction extends PlayerAction {
    public IndefinitePlayerAction(int delay, boolean important, int endLag) {
        super(delay, important, endLag);
    }

    public IndefinitePlayerAction(int delay, Animation animation, boolean important, int endLag) {
        super(delay, animation, important, endLag);
    }

    public IndefinitePlayerAction(int delay, Attack attack, boolean important, int endLag, int[] data) {
        super(delay, attack, important, endLag, data);
    }

    public IndefinitePlayerAction(int delay, Attack attack, Animation animation, boolean important, int endLag, int[] data) {
        super(delay, attack, animation, important, endLag, data);
    }

    @Override
    public boolean finished() {
        return !attack.alive;
    }
}
