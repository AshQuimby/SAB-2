package sab.game.fighter;

import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.Game;
import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.empty_soldier.EmptySoldierSlash;
import sab.game.attack.empty_soldier.ShadowPlunge;
import sab.game.attack.empty_soldier.ViceroyWings;
import sab.game.particle.Particle;
import sab.util.Utils;

public class EmptySoldier extends FighterType {
    private int spirit;
    private Animation swingAnimation;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "empty_soldier";
        fighter.name = "Empty Soldier";
        fighter.hitboxWidth = 32;
        fighter.hitboxHeight = 64;
        fighter.renderWidth = 64;
        fighter.renderHeight = 68;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.airJumps = 1;
        fighter.speed = 12.2f;
        fighter.acceleration = 1f;
        fighter.jumpHeight = 176;
        fighter.frames = 14;
        fighter.friction = .3f;
        fighter.mass = 4.13f;
        fighter.walkAnimation = new Animation(1, 3, 5, true);
        fighter.costumes = 3;
        fighter.description = "FOR THE LAST TIME. EMPTY SOLDIER WAS THE NAME OF THE SCIENTIST! THE BUG IS CALLED: \"EMPTY SOLDIER'S MONSTER!\"";
        fighter.debut = "Empty Soldier";
        fighter.freefallAnimation = new Animation(new int[] {8}, 1, false);
        fighter.ledgeAnimation = new Animation(new int[] {9}, 1, false);
        fighter.knockbackAnimation = new Animation(new int[] {10}, 1, false);

        spirit = 100;
        swingAnimation = new Animation(new int[] {4, 5, 6, 0}, 2, true);
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (!player.hasAction() && !player.touchingStage && !player.grabbingLedge()) {
            player.frame = player.velocity.y > -5 ? 7 : 8;
        }
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        swingAnimation.reset();
        player.startAttack(new EmptySoldierSlash(), swingAnimation, 6, 6, false);
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (spirit >= 30) {
            player.startAttack(new ViceroyWings(), null, 6, 8, false);
            spirit -= 30;
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.touchingStage && spirit >= 25) {
            player.startIndefiniteAttack(new ShadowPlunge(), null, 10, false);
            spirit -= 25;
        }
    }

    @Override
    public void hitObject(Fighter fighter, Player player, Attack attack, GameObject hit) {
        if (attack.type instanceof EmptySoldierSlash) {
            int spiritGained = Math.min(100, spirit + 20) - spirit;
            for (int i = 0; i < (spiritGained > 0 ? 3 : 0); i++) {
                attack.owner.battle.addParticle(new Particle(-0.1f, Utils.randomPointInRect(hit.hitbox), Utils.randomParticleVelocity(1), 16, 16, .97f, 0, "spirit_bubble.png"));
            }
            spirit += spiritGained;
        }
    }

    @Override
    public void renderUI(Fighter fighter, Player player, Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("spirit_bar_back.png"), player.getId() == 0 ? -256 - 72 - 4 : 256 + 4, -256, 72, 72);
        int drawAmount = (int) (spirit / 100f * 56);
        Game.game.window.batch.draw(g.imageProvider.getImage("spirit_bar.png"), player.getId() == 0 ? -256 - 56 - 12 : 256 + 12, -248, 56, drawAmount / 4 * 4, 0, 14, 14, -drawAmount / 4, false, true);
    }
}