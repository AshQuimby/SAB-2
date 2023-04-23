package sab.game.fighter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.Player;
import sab.game.animation.Animation;

public class BowlBoy extends FighterType {

    Vector2 gunHandPosition;

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
        fighter.friction = .15f;
        fighter.acceleration = 0.3f;
        fighter.mass = 5f;
        fighter.airJumps = 1;
        fighter.description = "Bowl Boy and his sister, Pot Head, are useless at just about everything. They entered a pact with a demon that gave them gun hands, but didn't solve their root problem of having strength, intelligence, and constitution not much better than their porcelain counterparts. As it is, they suck and nobody likes them.";
        fighter.debut = "Bowl Boy & Pot Head";
        gunHandPosition = new Vector2();

        fighter.walkAnimation = new Animation(4, 7, 4, true);
        fighter.idleAnimation = new Animation(0, 1, 16, true);
        fighter.ledgeAnimation = new Animation(14, 15, 16, true);
//        swingAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
//        squatAnimation = new Animation(new int[] {6}, 4, true);
//        chargeAnimation = new Animation(new int[] {9}, 4, true);
//        throwAnimation = new Animation(new int[] {10, 11}, 6, true);
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 3;
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.frame < 13) {
            gunHandPosition = new Vector2();
        } else {

        }
    }

    @Override
    public void render(Fighter fighter, Player player, Seagraphics g) {
        super.render(fighter, player, g);

        // Happens in the character select screen
        if (player.battle == null) {
            g.usefulTintDraw(g.imageProvider.getImage("bowl_boy_gun_hands.png"), player.drawRect.x, player.drawRect.y, (int) player.drawRect.width, (int) player.drawRect.height, player.frame, fighter.frames, 0, player.direction == 1, false, player.getIFrames() / 10 % 2 == 0 ? Color.WHITE : new Color(1, 1, 1, 0.5f));
        } else {
            g.usefulTintDraw(g.imageProvider.getImage("bowl_boy_hands.png"), player.drawRect.x, player.drawRect.y, (int) player.drawRect.width, (int) player.drawRect.height, player.frame, fighter.frames, 0, player.direction == 1, false, player.getIFrames() / 10 % 2 == 0 ? Color.WHITE : new Color(1, 1, 1, 0.5f));
        }
    }
}
