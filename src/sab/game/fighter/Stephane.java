package sab.game.fighter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.*;
import sab.game.action.PlayerAction;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.stephane.Baguette;
import sab.game.attack.stephane.Arrow;
import sab.game.attack.stephane.BlockSmash;
import sab.game.attack.stephane.Firework;
import sab.game.particle.Particle;
import sab.game.stage.Platform;
import sab.game.stage.Stage;
import sab.game.stage.StageObject;
import sab.game.stage.StageObjectBehaviour;
import sab.net.Keys;
import sab.util.Utils;

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
        fighter.airJumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        fighter.debut = "Blockbreak";
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        if (Utils.aprilFools()) {
            fighter.description = "There is a version of Stephane called the pickle with a face, he is very smelly, a dirty cheater, a manipulator of minors (miners).";
            fighter.costumes = 4;
        } else {
            fighter.description = "Nobody knows where Stephane came from as nobody knows what he is saying. All that comes from his mouth are ancient tongues and utterances like \"oui\" and \"tu comprends?\"";
            fighter.costumes = 3;
        }

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
        blocks--;
        Vector2 position = createBlockRectangle(player).getPosition(new Vector2());
        Platform block = new Platform(position.x, position.y, 32, 32, "block.png", stage) {
            private int life = 240;

            @Override
            public void updateStageObject(Battle battle) {
                if (--life < 0) {
                    kill();
                }
            }

            @Override
            public void render(Seagraphics g) {
                super.render(g);
                g.usefulDraw(g.imageProvider.getImage("block_break.png"), drawRect.x, drawRect.y, (int) drawRect.width, (int) drawRect.height, 4 - life / 48, 5, 0, false, false);
            }
        };

        for (int i = 0; i < stage.getStageObjects().size(); i++) {
            StageObject object = stage.getStageObjects().get(i);
            if (object.isSolid()) {
                if (!object.hitbox.overlaps(block.hitbox)) {
                    stage.addStageObject(block);
                    break;
                }
            }
        }
        player.battle.addAttack(new Attack(new BlockSmash(), player), new int[0]);
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

    @Override
    public boolean onHit(Fighter fighter, Player player, DamageSource source) {
        if (player.costume == 3) {
            player.kill(1);
            return false;
        }
        return true;
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
                player.startAttack(new Firework(), bowFireworkAnimation, 24, 16, false);
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
            player.startRepeatingAttack(new Baguette(), swingAnimation, 4, 18, false, new int[0]);
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (player.touchingStage) {
                bowAnimation.reset();
                player.startAttack(new Arrow(), bowAnimation, 24, 12, false, null);
            } else {
                bowFastAnimation.reset();
                player.startAttack(new Arrow(), bowFastAnimation, 12, 4, false, null);
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
        player.startAnimation(1, blockPlaceAnimation, 12, false);
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
            // Make sure to only give blocks if mining on non-Stephane block
            if (blocks < 32) {
                if (player.touchingStage) {
                    boolean touchingPlatform = false;
                    for (StageObject platform : player.battle.getStage().getStageObjects()) {
                        if (platform.isSolid() && !platform.imageName.equals("block.png")) {
                            Rectangle box = new Rectangle(player.hitbox);
                            box.x -= 2;
                            box.y -= 2;
                            box.width += 4;
                            box.height += 4;
                            if (box.overlaps(platform.hitbox)) {
                                touchingPlatform = true;
                            }
                        }
                    }
                    if (touchingPlatform) {
                        blocks++;
                        SABSounds.playSound("crunch.mp3");
                        player.battle.addParticle(new Particle(1.2f, player.getCenter().add(24 * player.direction, -24), new Vector2(MathUtils.random(-1f, 1f), MathUtils.random(4f, 10f)),32,32,12, "block.png"));
                    }
                }
            }
        }
    }

    @Override
    public void renderUI(Fighter fighter, Player player, Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("stephane_ui.png"), player.getId() == 0 ? -256 - 48 : 256, -256, 48, 48);
        g.drawText("" + (int) blocks, Game.getDefaultFont(), player.getId() == 0 ? -256 - 48 + 38 : 256 + 38, -256 + 24, Game.getDefaultFontScale(), Color.WHITE, 1 );
    }
}
