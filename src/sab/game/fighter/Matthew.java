package sab.game.fighter;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.DamageSource;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.matthew.MatthewSlash;
import sab.game.attack.matthew.MegaCounterSlash;
import sab.game.attack.matthew.PogoSword;
import sab.game.attack.matthew.UpwardsSlash;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.Utils;

public class Matthew extends FighterType {

    private Animation swingAnimation;
    private Animation bigSwingAnimation;
    private Animation parryAnimation;
    private Animation failedParryAnimation;
    private Animation verticalThrustAnimation;
    private int counterCharge;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "matthew";
        fighter.name = "Matthew";
        fighter.hitboxWidth = 40;
        fighter.hitboxHeight = 60;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.frames = 15;
        fighter.jumpHeight = 160;
        fighter.friction = .2f;
        fighter.mass = 5f;
        fighter.airJumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 6, true);
        fighter.description = "God is dead.\nMatthew killed him.";
        fighter.debut = "Our Sports Resort";

        parryAnimation = new Animation(new int[] {13, 14, 14, 14, 14, 13, 7}, 4, true);
        failedParryAnimation = new Animation(new int[] {13, 13, 7}, 8, true);
        swingAnimation = new Animation(new int[] {4, 5, 6, 7}, 4, true);
        bigSwingAnimation = new Animation(new int[] {12, 12, 13, 4, 4, 5, 6, 7, 7}, 5, true);
        verticalThrustAnimation = new Animation(new int[] {9}, 60, true);
        fighter.freefallAnimation = new Animation(new int[]{8}, 1, true);
        fighter.knockbackAnimation = new Animation(new int[]{10}, 1, true);
        fighter.ledgeAnimation = new Animation(new int[] {11}, 1, true);
        fighter.costumes = 3;
        counterCharge = 2;
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 150) {
            private static final int SLASH_DISTANCE = 64;
            private static final int FROSTBALL_DAMAGE_REQUIRED = 60;
            private final Matthew matthew = (Matthew) player.fighter.type;

            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                if (Math.random() * 25 > difficulty) return;

                if (isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x)) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);

                    if (horizontalDistance <= SLASH_DISTANCE) {
                        useSideAttack();
                    }
                } else if ((isDirectlyAbove(target.hitbox) || isDirectlyBelow(target.hitbox) && Math.abs(center.y - targetPosition.y) > 32) && Math.random() * 20 < difficulty) {
                    useUpAttack();
                }

                if (target.grabbingLedge()) {
                    preferredHorizontalDistance = -30f;
                } else {
                    preferredHorizontalDistance = 4f;
                }

                if (player.getRemainingJumps() > 0 && getPlatformBelow(target) == null && isDirectlyAbove(target.hitbox)) {
                    if (player.touchingStage) pressKey(Keys.UP);
                    else useDownAttack();
                }

                if (matthew.counterCharge == 2 && (MathUtils.random(difficulty + 2) > 2 || difficulty >= 5)) {
                    Attack nearestThreat = getNearestEnemyAttack();
                    if (nearestThreat != null) {
                        if (getFutureCollision(nearestThreat, 24) != null) {
                            useNeutralAttack();
                        }
                    }
                }
            }
        };
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (counterCharge == 2) {
            counterCharge = 0;
            parryAnimation.reset();
            player.startAnimation(4, parryAnimation, 24, false);
        } else {
            failedParryAnimation.reset();
            player.startAnimation(4, failedParryAnimation, 24, false);
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        swingAnimation.reset();
        player.startAttack(new MatthewSlash(), swingAnimation, 4, 12, false);
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        verticalThrustAnimation.reset();
        player.startAttack(new PogoSword(), verticalThrustAnimation, 4, 40, false);
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            verticalThrustAnimation.reset();
            player.startAttack(new UpwardsSlash(), verticalThrustAnimation, 4, 14, false);
            player.usedRecovery = true;
            player.removeJumps();
        }
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.usingAnimation(bigSwingAnimation)) {
            player.velocity.x = 0;
            player.velocity.y = 0;
        }
    }

    @Override
    public void hitObject(Fighter fighter, Player player, Attack attack, GameObject hit) {
        if (!(attack.type instanceof MegaCounterSlash)) {
            if (counterCharge < 2) counterCharge++;
        }
    }

    @Override
    public boolean onHit(Fighter fighter, Player player, DamageSource source) {
        if (source.parryable && counterCharge >= 2 && player.getAnimation().getFrame() == 14) {
            source.owner.stun(32);
            bigSwingAnimation.reset();
            SABSounds.playSound("mega_counter.mp3");
            player.battle.freezeFrame(10, 3, 45, true);
            player.frame = 12;
            source.owner.frame = source.owner.fighter.knockbackAnimation.stepLooping();
            player.startAttack(new MegaCounterSlash(), bigSwingAnimation, 20, 20, false, new int[] { source.damage });
            player.hitbox.setCenter(source.owner.getCenter().add(source.owner.direction * -64, 16));
            player.direction = (int) Math.signum(source.owner.getCenter().x - player.getCenter().x);
            String teleportImage = Utils.appendCostumeToIdentifier("matthew_teleport", player.costume, "png");
            player.battle.addParticle(new Particle(player.getCenter().add(-6 * player.direction, 12), new Vector2(), 100, 80, 12, 2, player.direction, teleportImage));
            player.setIFrames(10);
            return false;
        }
        return true;
    }

    @Override
    public void renderUI(Fighter fighter, Player player, Seagraphics g) {
        g.usefulDraw(g.imageProvider.getImage("matthew_ui.png"), player.getId() == 0 ? -256 - 56 - 12 : 256 + 4, -256, 64, 48, counterCharge == 0 ? 0 : (counterCharge + 2 * player.costume), 7, 0, false, false);
    }
}
