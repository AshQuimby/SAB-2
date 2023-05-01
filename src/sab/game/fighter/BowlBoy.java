package sab.game.fighter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.Game;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.bowl_boy.*;
import sab.net.Keys;

import java.security.Key;

public class BowlBoy extends FighterType {

    private Vector2 gunHandPosition;
    private boolean gunMode;
    private int shootRecoil;
    private int bulletIndex;
    private int showBulletIcon;

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
        fighter.frames = 21;
        fighter.jumpHeight = 160;
        fighter.friction = .15f;
        fighter.acceleration = 0.3f;
        fighter.mass = 5f;
        fighter.airJumps = 1;
        fighter.description = "Bowl Boy and his sister, Pot Head, got a messenger pigeon from a demon about their car's extended warranty. They entered a pact with the demon that gave them gun hands, but didn't solve their root problem of having strength, intelligence, and constitution not much better than their porcelain counterparts. As it is, they suck and nobody likes them.";
        fighter.debut = "Bowl Boy & Pot Head in: Deal-tastic Demon";
        gunHandPosition = new Vector2();

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
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 3;
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.frame < 13) {
            gunHandPosition = new Vector2(18 * player.direction, 12);
        } else {

        }

        if (gunMode && player.keys.isPressed(Keys.ATTACK) && !player.isStuck()) {
            gunMode = true;
        } else {
            gunMode = false;
        }
        if (shootRecoil > 0) {
            shootRecoil--;
        }
        if (gunMode) {
            if (shootRecoil <= 0) {
                SABSounds.playSound("bowl_boy_shooty.mp3");
                shootRecoil = shootBullet(fighter, player);
                player.occupy(shootRecoil);
            }
        }

        if (gunMode && player.frame < 2) {
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
                return 0;
            case 5 :
                player.battle.addAttack(new Attack(new Roundabout(), player), new int[]{(int) gunHandPosition.x, (int) gunHandPosition.y});
                return 25;
        }
        return 0;
    }

    // Probably the simplest neutral attack method in the game but also the most complex neutral attack
    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            gunMode = true;
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
    public void render(Fighter fighter, Player player, Seagraphics g) {
        // player.battle == null so that it happens in the character select screen
        if (gunMode || player.battle == null) {
            g.usefulTintDraw(g.imageProvider.getImage("bowl_boy_gun_hands.png"), player.drawRect.x, player.drawRect.y, (int) player.drawRect.width, (int) player.drawRect.height, player.frame, fighter.frames, (int) (shootRecoil * player.direction * 1.5f), player.direction == 1, false, player.getIFrames() / 10 % 2 == 0 ? Color.WHITE : new Color(1, 1, 1, 0.5f));
        } else {
            g.usefulTintDraw(g.imageProvider.getImage("bowl_boy_hands.png"), player.drawRect.x, player.drawRect.y, (int) player.drawRect.width, (int) player.drawRect.height, player.frame, fighter.frames, 0, player.direction == 1, false, player.getIFrames() / 10 % 2 == 0 ? Color.WHITE : new Color(1, 1, 1, 0.5f));
        }

        if (showBulletIcon > 0) {
            float alphaComponent = Math.min(1, showBulletIcon / 30f);
            g.usefulTintDraw(g.imageProvider.getImage("bowl_boy_bullet_icons_small.png"), player.getCenter().x - 16, player.getCenter().y - 16 + player.drawRect.height * 0.8f, 32, 32, bulletIndex, 6, 0, false, false, new Color(1, 1, 1, alphaComponent));
            showBulletIcon--;
        }

        super.render(fighter, player, g);
    }

    @Override
    public void renderUI(Fighter fighter, Player player, Seagraphics g) {
        g.usefulDraw(g.imageProvider.getImage("bowl_boy_bullet_icons_big.png"), player.getId() == 0 ? -256 - 52 : 256 + 4, -256, 48, 48, bulletIndex, 6, 0, false, false);
    }
}
