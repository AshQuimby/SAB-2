package sab.game.fighter;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.Player;
import sab.game.animation.Animation;

public class BowlBoy extends FighterType {

    @Override
    public void setDefaults(sab.game.fighter.Fighter fighter) {
        fighter.id = "bowl_boy";
        fighter.name = "Bowl Boy";
        fighter.hitboxWidth = 40;
        fighter.hitboxHeight = 60;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.frames = 20;
        fighter.jumpHeight = 160;
        fighter.friction = .2f;
        fighter.mass = 5f;
        fighter.airJumps = 1;
        fighter.description = "Bowl Boy and his brother, Pot Head,.";
        fighter.debut = "Bowl Boy & Pot Head";

        fighter.walkAnimation = new Animation(4, 7, 4, true);
//        swingAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
//        squatAnimation = new Animation(new int[] {6}, 4, true);
//        chargeAnimation = new Animation(new int[] {9}, 4, true);
//        throwAnimation = new Animation(new int[] {10, 11}, 6, true);
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 3;
    }

    @Override
    public void render(Fighter fighter, Player player, Seagraphics g) {
        super.render(fighter, player, g);
        System.out.println("gabagack");
        g.usefulDraw(g.imageProvider.getImage("bowl_boy_hands.png"), player.drawRect.x, player.drawRect.y, (int) player.drawRect.width, (int) player.drawRect.height, player.frame, fighter.frames, 0, player.direction == 1, false);
    }
}
