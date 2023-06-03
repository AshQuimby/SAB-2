package sab.game.fighter;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import sab.game.Game;
import sab.game.Player;
import sab.game.action.PlayerAction;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.marvin.*;
import sab.game.particle.Particle;
import sab.net.Keys;

public class Random extends FighterType {
    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "random";
        fighter.name = "Random";
        fighter.frames = 1;
        fighter.walkAnimation = new Animation(new int[] { 0 }, 5, true);
        fighter.costumes = 1;
    }
}
