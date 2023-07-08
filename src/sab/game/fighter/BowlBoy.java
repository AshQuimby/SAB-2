package sab.game.fighter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.DamageSource;
import sab.game.Game;
import sab.game.Player;
import sab.game.SabSounds;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.bowl_boy.*;
import sab.game.particle.Particle;
import sab.game.stage.Ledge;
import sab.game.stage.Platform;
import sab.net.Keys;
import sab.util.SabRandom;

public class BowlBoy extends FighterType {
    private static final int PEASHOT = 0;
    private static final int SPREAD = 1;
    private static final int CHASER = 2;
    private static final int LOBBER = 3;
    private static final int CHARGE = 4;
    private static final int ROUNDABOUT = 5;

    private Vector2 gunHandPosition;
    private boolean gunMode;
    private boolean chargingShot;
    private int shootRecoil;
    private int bulletIndex;
    private int showBulletIcon;
    private int chargeShotCharge;
    private int spinTime;
    private float superMeter;
    private Animation freefallSpinAnimation;
    private Animation freefallNormalAnimation;
    private Animation exUseAnimation;

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 100) {
            private int targetShotType;

            @Override
            protected void recover(Platform targetPlatform, Ledge targetLedge) {
                if (targetPlatform == null && targetLedge == null) return; // Guess I'll just die then
                float x = player.hitbox.x + player.hitbox.width / 2;
                float ledgeX = targetLedge == null ? targetPlatform.hitbox.x + targetPlatform.hitbox.width / 2 : targetLedge.grabBox.x + targetLedge.grabBox.width / 2;

                if (player.velocity.y <= 0) {
                    pressKey(Keys.UP);
                    if (player.getRemainingJumps() == 0 && isFacing(ledgeX)) {
                        pressKey(Keys.ATTACK);
                        return;
                    }
                }

                if (x < ledgeX) {
                    pressKey(Keys.RIGHT);
                } else {
                    pressKey(Keys.LEFT);
                }
            }

            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                BowlBoy bowlBoy = (BowlBoy) player.fighter.type;
                int cards = bowlBoy.getCardCount();
                int shotType = bowlBoy.bulletIndex;

                if (bowlBoy.chargingShot && SabRandom.random() * 20 < difficulty && isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x)) {
                    releaseKey(Keys.ATTACK);
                    return;
                }

                if (shotType != targetShotType && !player.usedRecovery && mashCooldown == 0) {
                    useDownAttack();
                    setMashCooldown();
                    return;
                }

                if (cards > 0) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);
                    if (horizontalDistance < 150 && isFacing(targetPosition.x)) useSideAttack();
                } else {
                    if (isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x)) {
                        useNeutralAttack();
                    }
                }

                float horizontalDistance = Math.abs(center.x - targetPosition.x);
                if (isDirectlyHorizontal(target.hitbox)) {
                    if (isFacing(targetPosition.x)) {
                        if (horizontalDistance < 128 && cards < 3) targetShotType = SPREAD;
                        else if (target.damage > 100) targetShotType = CHARGE;
                        else if (!target.touchingStage) targetShotType = LOBBER;
                        else targetShotType = PEASHOT;
                    } else {
                        targetShotType = ROUNDABOUT;
                    }
                } else {
                    targetShotType = CHASER;
                }
            }
        };
    }

    @Override
    public void setDefaults(sab.game.fighter.Fighter fighter) {
        fighter.id = "bowl_boy";
        fighter.name = "Bowl Boy";
        fighter.hitboxWidth = 36;
        fighter.hitboxHeight = 46;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 2;
        fighter.imageOffsetY = 9;
        fighter.frames = 25;
        fighter.jumpHeight = 160;
        fighter.friction = .15f;
        fighter.acceleration = 0.3f;
        fighter.mass = 5f;
        fighter.airJumps = 1;
        fighter.description = "Bowl Boy and his sister, Pot Head, got a messenger pigeon from a demon about their car's extended warranty. They entered a pact with the demon that gave them gun hands, but didn't solve their root problem of having strength, intelligence, and constitution not much better than their porcelain counterparts. As it is, they suck and nobody likes them.";
        fighter.debut = "Bowl Boy & Pot Head in: Deal-tastic Demon";
        gunHandPosition = new Vector2();
        chargingShot = true;
        fighter.airDodgeSpeed = 8.5f;

        gunMode = false;

        fighter.walkAnimation = new Animation(4, 7, 4, true);
        fighter.idleAnimation = new Animation(0, 1, 16, true);
        fighter.ledgeAnimation = new Animation(14, 15, 16, true);
        fighter.knockbackAnimation = new Animation(new int[] { 20 }, 60, true);
        fighter.parryAnimation = new Animation(new int[] { 16, 0, 0 }, 10, true);
//        swingAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
//        squatAnimation = new Animation(new int[] {6}, 4, true);
//        chargeAnimation = new Animation(new int[] {9}, 4, true);
//        throwAnimation = new Animation(new int[] {10, 11}, 6, true);
        freefallSpinAnimation = new Animation(new int[]{ 17, 18, 19 }, 4, true);
        freefallNormalAnimation = new Animation(new int[]{ 3 }, 4, true);
        exUseAnimation = new Animation(8, 12, 6, true);
        fighter.freefallAnimation = freefallNormalAnimation;
        fighter.costumes = 2;
    }

    public int getCardCount() {
        return (int) superMeter / 10;
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.frame < 13) {
            gunHandPosition = new Vector2(26 * player.direction, 12);
        } else {

        }

        if (!player.takingKnockback() && !player.touchingStage && !player.isStuck() && !player.hasAction() && !player.usedRecovery) {
            player.frame = freefallNormalAnimation.stepLooping();
        }

        if (spinTime > 0) {
            if (player.keys.isPressed(Keys.UP) && player.keys.isPressed(Keys.ATTACK)) {
                player.velocity.y += 0.6f;
            }
            spinTime--;
        }

        if (player.usedRecovery) {
            if (spinTime > 0) fighter.freefallAnimation = freefallSpinAnimation;
            else fighter.freefallAnimation = freefallNormalAnimation;
        }

        if (gunMode) {
            if (!player.keys.isPressed(Keys.ATTACK)) {
                if (chargingShot) {
                    if (chargeShotCharge > 30) {
                        player.battle.addAttack(new Attack(new StrongCharge(), player), new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                        shootRecoil = 20;
                    } else {
                        player.battle.addAttack(new Attack(new WeakCharge(), player), new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                        shootRecoil = 15;
                    }
                    SabSounds.playSound("bowl_boy_shooty.mp3");
                    chargeShotCharge = 0;
                    chargingShot = false;
                }
                gunMode = false;
            }
            if (player.isStuck()) {
                gunMode = false;
            }
        } else {
            chargingShot = false;
        }

        if (shootRecoil > 0) {
            shootRecoil--;
        }
        if (gunMode) {
            if (chargingShot) {
                if (chargeShotCharge == 30) {
                    SabSounds.playSound("charge_shot_charged.mp3");
                }
                chargeShotCharge++;
                if (Game.game.window.getTick() % (chargeShotCharge > 30 ? 3 : 9) == 0) player.battle.addParticle(new Particle(player.getCenter().add(gunHandPosition), new Vector2(1, 0).rotateDeg(SabRandom.random(360)), 8, 8, "charge_shot_dust.png"));
            }  else if (shootRecoil <= 0) {
                shootRecoil = shootBullet(fighter, player);
                if (!chargingShot) SabSounds.playSound("bowl_boy_shooty.mp3");
                player.occupy(shootRecoil);
            }
        }

        if ((gunMode || shootRecoil > 1) && player.frame < 2) {
            player.frame = 2;
        }
    }

    private int shootBullet(Fighter fighter, Player player) {
        switch (bulletIndex) {
            case 0 :
                player.battle.addAttack(new Attack(new Peashot(), player), new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                return 10;
            case 1 :
                player.battle.addAttack(new Attack(new Spread(), player), new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y, 90 - 10});
                player.battle.addAttack(new Attack(new Spread(), player), new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y, 90});
                player.battle.addAttack(new Attack(new Spread(), player), new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y, 90 + 10});
                return 10;
            case 2 :
                player.battle.addAttack(new Attack(new Chaser(), player), new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                return 15;
            case 3 :
                player.battle.addAttack(new Attack(new Lobber(), player), new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                return 30;
            case 4 :
                chargingShot = true;
                return 0;
            case 5 :
                player.battle.addAttack(new Attack(new Roundabout(), player), new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                return 25;
        }
        return 0;
    }

    private void shootEX(Fighter fighter, Player player) {
        exUseAnimation.reset();
        switch (bulletIndex) {
            case 0 :
                player.startAttack(new MegaBlastLarge(), exUseAnimation,18, 12, false, new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                break;
            case 1 :
                player.startAttack(new EightWay(), exUseAnimation,18, 12, false, new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y, 0});
                break;
            case 2 :
                player.startAttack(new ChaosOrbit(), exUseAnimation,18, 12, false, new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y, 0});
                break;
            case 3 :
                player.startAttack(new Kablooey(), exUseAnimation,18, 12, false, new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                break;
            case 4 :
                player.startAttack(new RadialBarrage(), exUseAnimation,18, 12, false, new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                break;
            case 5 :
                player.startAttack(new JumboRebound(), exUseAnimation,18, 12, false, new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                break;
        }
    }

    // Probably the simplest neutral attack method in the game but also the most complex neutral attack
    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            gunMode = true;
        }
    }

    @Override
    public void hitObject(Fighter fighter, Player player, Attack attack, GameObject hit) {
        if (superMeter < 50 && attack.type instanceof BowlBoyShot) {
            superMeter += ((BowlBoyShot) attack.type).superMeterValue;
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            bulletIndex = (bulletIndex + 1) % 6;
            showBulletIcon = 90;
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery && spinTime <= 0) {
            player.velocity.y = 16;
            player.velocity.x = 20 * player.direction;
            player.usedRecovery = true;
            SabSounds.playSound("parry.mp3");
            SabSounds.playSound("jump.mp3");
            spinTime = 45;
            player.battle.addAttack(new Attack(new RecoverBump(), player), null);
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (getCardCount() > 0) {
                shootEX(fighter, player);
                superMeter -= 10;
            }
        }
    }

    @Override
    public boolean finalAss(Fighter fighter, Player player) {
        player.startAttack(new SuperLaserMilk(), null, 0, 0, true);
        return true;
    }

    @Override
    public void onSuccessfulParry(Fighter fighter, Player player, DamageSource parried) {
        if (getCardCount() < 5) superMeter = superMeter + 10 - superMeter % 10;
    }

    @Override
    public void render(Fighter fighter, Player player, Seagraphics g) {
        // player.battle == null so that it happens in the character select screen
        if (gunMode || player.battle == null || shootRecoil > 1) {
            g.usefulTintDraw(g.imageProvider.getImage("bowl_boy_gun_hands.png"), player.drawRect.x, player.drawRect.y, (int) player.drawRect.width, (int) player.drawRect.height, player.frame, fighter.frames, (int) (shootRecoil * player.direction * 1.5f), player.direction == 1, false, player.getIFrames() / 10 % 2 == 0 ? Color.WHITE : new Color(1, 1, 1, 0.5f));
        } else {
            g.usefulTintDraw(g.imageProvider.getImage("bowl_boy_hands.png"), player.drawRect.x, player.drawRect.y, (int) player.drawRect.width, (int) player.drawRect.height, player.frame, fighter.frames, 0, player.direction == 1, false, player.getIFrames() / 10 % 2 == 0 ? Color.WHITE : new Color(1, 1, 1, 0.5f));
        }

        if (showBulletIcon > 0) {
            float alphaComponent = Math.min(1, showBulletIcon / 30f);
            for (int i = 0; i < 6; i++) {
                Vector2 drawPos = new Vector2(player.getCenter().x - 16, player.getCenter().y - 16 + player.drawRect.height + 24);
                if (i == 0) drawPos.y += 40;
                else if (i == 1 || i == 5) drawPos.y += 20;
                else if (i == 2 || i == 4) drawPos.y -= 20;
                else if (i == 3) drawPos.y -= 40;
                if (i == 1 || i == 2) drawPos.x -= 40;
                else if (i == 4 || i == 5) drawPos.x += 40;
                g.usefulTintDraw(g.imageProvider.getImage("bowl_boy_bullet_icons_small.png"), drawPos.x, drawPos.y, 32, 32, i, 6, 0, false, false, new Color(1, 1, 1, alphaComponent));
                if (i == bulletIndex) g.usefulTintDraw(g.imageProvider.getImage("bowl_boy_bullet_selected.png"), drawPos.x - 4, drawPos.y - 4, 40, 40, 0, 1, 0, false, false, new Color(1, 1, 1, alphaComponent));
            }
            showBulletIcon--;
        }

        super.render(fighter, player, g);
    }

    @Override
    public void renderUI(Fighter fighter, Player player, Seagraphics g) {
        g.usefulDraw(g.imageProvider.getImage("bowl_boy_bullet_icons_big.png"), player.getId() == 0 ? -256 - 52 : 256 + 4, -256, 48, 48, bulletIndex, 6, 0, false, false);
        for (int i = 0; i < Math.min(5, getCardCount() + 1); i++) {

            int drawAmount = (int) (superMeter % 10 / 10f * 40);
            if (i >= getCardCount()) Game.game.window.batch.draw(g.imageProvider.getImage("bowl_boy_card.png"), (player.getId() == 0 ? -256 - 32 - 32 : 256 + 32) + 36 * i * (player.getId() == 0 ? 1 : -1), -186, 28, drawAmount / 4 * 4, 0, 0, 7, drawAmount / 4, false, false);
            else g.scalableDraw(g.imageProvider.getImage("bowl_boy_card_full.png"), (player.getId() == 0 ? -256 - 32 - 32 : 256 + 32) + 36 * i * (player.getId() == 0 ? 1 : -1), -186, 28, 40);
        }
    }
}
