package sab.game.fighter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import org.w3c.dom.css.Rect;
import sab.game.*;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.stephane.Baguette;
import sab.game.attack.stephane.Arrow;
import sab.game.attack.stephane.BlockSmash;
import sab.game.attack.stephane.Firework;
import sab.game.stage.Platform;
import sab.game.stage.Stage;
import sab.game.stage.StageObject;
import sab.game.stage.StageObjectBehaviour;
import sab.net.Keys;

public class Stephane extends FighterType {
    private Animation swingAnimation;
    private Animation blockPlaceAnimation;
    private Animation bowAnimation;
    private Animation bowFastAnimation;
    private Animation bowFireworkAnimation;
    private float blocks;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "stephane";
        fighter.name = "Stephane";
        fighter.hitboxWidth = 28;
        fighter.hitboxHeight = 56;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 2;
        fighter.imageOffsetY = 4;
        fighter.frames = 15;
        fighter.acceleration = .3f;
        fighter.jumpHeight = 130;
        fighter.friction = .2f;
        fighter.mass = 5.4f;
        fighter.jumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        fighter.description = "Nobody knows where Stephane came from as nobody knows what he is saying. All that comes from his mouth are ancient tongues and utterances like \"oui\" and \"tu comprends?\"";
        fighter.debut = "Blockbreak";
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 3;

        swingAnimation = new Animation(new int[] {4, 5}, 9, true);
        blockPlaceAnimation = new Animation(new int[] {4, 5, 0}, 6, true);
        bowAnimation = new Animation(new int[] {9, 10, 11, 14}, 8, true);
        bowFastAnimation = new Animation(new int[] {9, 10, 11, 14}, 4, true);
        bowFireworkAnimation = new Animation(new int[] {12, 13, 14}, 12, true);
        blocks = 16;
    }

    public boolean createBlock(Player player, Stage stage) {
        if (blocks <= 0) {
            return false;
        }
        Vector2 position = createBlockRectangle(player).getPosition(new Vector2());
        Platform block = new Platform(position.x, position.y, 32, 32, "block.png", stage, new StageObjectBehaviour() {
            private int life = 240;
            
            @Override
            public void update(StageObject object, Battle battle) {
                if (--life < 0) {
                    object.kill();
                }
            }
        });
        for (int i = 0; i < stage.getStageObjects().size(); i++) {
            StageObject object = stage.getStageObjects().get(i);
            if (object.isSolid()) {
                if (!object.hitbox.overlaps(block.hitbox)) {
                    stage.addStageObject(block);
                    break;
                }
            }
        }
        return true;
    }

    public Rectangle createBlockRectangle(Player player) {
        boolean moveUp = false;
        if (!canPlaceBelow(player)) {
            moveUp = true;
            player.move(new Vector2(0, 32));
        }
        player.velocity.y = 0;
        Rectangle virtualBlock = new Rectangle(0, 0 , 32, 32);
        virtualBlock.setCenter(player.getCenter());
        virtualBlock.y -= player.hitbox.height / 2f + 16;
        virtualBlock.x = Math.round((virtualBlock.x) / 32) * 32;
        virtualBlock.y = Math.round((virtualBlock.y) / 32) * 32;
        if (player.hitbox.overlaps(virtualBlock) || moveUp) player.hitbox.y = virtualBlock.y + virtualBlock.height;

        blockPlaceAnimation.reset();
        player.startAnimation(1, blockPlaceAnimation, 16, false);

        return virtualBlock;
    }

    private boolean canPlaceBelow(Player player) {
        Vector2 point = player.getCenter();
        point.y -= player.hitbox.height / 2 + 17;
        for (GameObject gameObject : player.battle.getSolidStageObjects()) {
            if (gameObject.hitbox.contains(point)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.usingAnimation(bowAnimation) && player.touchingStage) {
            if (bowAnimation.getFrame() == 11 && player.keys.isPressed(Keys.ATTACK)) {
                player.resetAction();
                bowFireworkAnimation.reset();
                player.startAttack(new Attack(new Firework(), player), bowFireworkAnimation, 24, 16, false);
            }
        }

        if (player.isReady() && player.keys.isPressed(Keys.ATTACK) && player.keys.isPressed(Keys.DOWN) && canPlaceBelow(player)) {
            createBlock(player, player.battle.getStage());
        }
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startRepeatingAttack(new Attack(new Baguette(), player), swingAnimation, 4, 18, false, new int[0]);
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (player.touchingStage) {
                bowAnimation.reset();
                player.startAttack(new Attack(new Arrow(), player), bowAnimation, 24, 12, false, null);
            } else {
                bowFastAnimation.reset();
                player.startAttack(new Attack(new Arrow(), player), bowFastAnimation, 12, 4, false, null);
            }
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        createBlock(player, player.battle.getStage());
        createBlock(player, player.battle.getStage());
        createBlock(player, player.battle.getStage());
        createBlock(player, player.battle.getStage());
        blockPlaceAnimation.reset();
        player.startAttack(new Attack(new BlockSmash(), player), blockPlaceAnimation, 1, 12, false, null);
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        createBlock(player, player.battle.getStage());
    }

    @Override
    public void onKill(Fighter fighter, Player player) {

    }

    @Override
    public void onEndAction(PlayerAction action, Fighter fighter, Player player) {
        if (action.usingAnimation(swingAnimation)) {
            blocks++;
        }
    }

    @Override
    public void renderUI(Fighter fighter, Player player, Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("stephane_ui.png"), player.getId() == 0 ? -256 - 48 : 256, -256, 48, 48);
        g.drawText("" + (int) blocks, g.imageProvider.getFont("SAB_font"), player.getId() == 0 ? -256 - 48 + 38 : 256 + 38, -256 + 24, 1, Color.WHITE, 1 );
    }
}
