package sab.game.fighter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import org.w3c.dom.css.Rect;
import sab.game.Battle;
import sab.game.Game;
import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.stephane.Baguette;
import sab.game.attack.stephane.Arrow;
import sab.game.attack.stephane.Firework;
import sab.game.stage.Platform;
import sab.game.stage.Stage;
import sab.game.stage.StageObject;
import sab.game.stage.StageObjectBehaviour;
import sab.net.Keys;

public class Stephane extends FighterType {
    private Animation swingAnimation;
    private Animation bowAnimation;
    private Animation bowFastAnimation;
    private Animation bowFireworkAnimation;

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

        swingAnimation = new Animation(new int[] { 5, 6, 0 }, 3, true);
        bowAnimation = new Animation(new int[] {9, 10, 11, 14}, 8, true);
        bowFastAnimation = new Animation(new int[] {9, 10, 11, 14}, 4, true);
        bowFireworkAnimation = new Animation(new int[] {12, 13, 14}, 12, true);
    }

    public boolean createBlock(Stage stage, Vector2 position) {
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

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.usingAnimation(bowAnimation) && player.touchingStage) {
            if (bowAnimation.getFrame() == 11 && player.keys.isPressed(Keys.ATTACK)) {
                player.resetAction();
                bowFireworkAnimation.reset();
                player.startAttack(new Attack(new Firework(), player), bowFireworkAnimation, 24, 16, false);
            }
        }
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startAttack(new Attack(new Baguette(), player), swingAnimation, 4, 9, false);
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
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {

        Vector2 point = new Vector2(0, 0);
        point = player.getCenter();
        point.y -= player.hitbox.height / 2 + 17;
        boolean moveUp = false;
        for (GameObject gameObject : player.battle.getSolidStageObjects()) {
            if (gameObject.hitbox.contains(point)) {
                moveUp = true;
                player.move(new Vector2(0, 32));
                break;
            }
        }
        player.velocity.y = 0;
        Rectangle virtualBlock = new Rectangle(0, 0, 32, 32);
        virtualBlock.setCenter(player.getCenter());
        virtualBlock.y -= player.hitbox.height / 2 + 16;
        virtualBlock.x = (int) ((virtualBlock.x + 16) / 32) * 32;
        virtualBlock.y = (int) ((virtualBlock.y) / 32) * 32;
        if (moveUp) player.hitbox.y = virtualBlock.y + virtualBlock.height;

        createBlock(player.battle.getStage(), virtualBlock.getPosition(new Vector2()));
    }

    @Override
    public void chargeAttack(Fighter fighter, Player player, int charge) {
    }

    @Override
    public void charging(Fighter fighter, Player player, int charge) {
    }

    @Override
    public void onKill(Fighter fighter, Player player) {

    }

    @Override
    public void renderUI(Fighter fighter, Player player, Seagraphics g) {
        //g.shapeRenderer.setColor(new Color(0, 0, 0, 0.5f));
        //g.shapeRenderer.rect(0, Game.game.window.resolutionY / 8 * 7, 128, 64);
        //g.drawText("Stephane fell from a high place.", g.imageProvider.getFont("SAB_font"), 0, Game.game.window.resolutionY / 8 * 7, 1, Color.WHITE, 0);
    }
}
