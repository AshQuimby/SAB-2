package sab.game.fighter;

import java.util.List;

import org.lwjgl.system.linux.X11;

import java.util.ArrayList;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.Battle;
import sab.game.Game;
import sab.game.Player;
import sab.game.PlayerAction;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.melees.GlasterBaster;
import sab.game.attack.projectiles.BoneSpike;
import sab.game.attack.projectiles.Frostball;
import sab.game.attack.projectiles.SpinnyBone;
import sab.game.particle.Particle;
import sab.game.stage.Platform;
import sab.game.stage.Stage;
import sab.game.stage.StageObject;
import sab.game.stage.StageObjectBehaviour;
import sab.util.Utils;

public class Stephane extends FighterType {
    
    // Block system prone to change
    // public static List<Platform> blocks = new ArrayList<Platform>();

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "stephane";
        fighter.name = "Stephane";
        fighter.hitboxWidth = 60;
        fighter.hitboxHeight = 80;
        fighter.renderWidth = 68;
        fighter.renderHeight = 88;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 4;
        fighter.frames = 9;
        fighter.acceleration = .28f;
        fighter.jumpHeight = 130;
        fighter.friction = .225f;
        fighter.mass = 5.4f;
        fighter.jumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        fighter.description = "Block man \n\nBottom Text";
        fighter.debut = "Blockbreak";
        
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 3;
    }

    public boolean createBlock(Stage stage, Vector2 position) {
        stage.addStageObject(new Platform(position.x, position.y, 64, 64, "block.png", stage, new StageObjectBehaviour() {
            int life = 60;
            
            @Override
            public void update(StageObject object, Battle battle) {
                if (--life < 0) {
                    object.kill();
                }
            }
        }));
        return true;
    }

    @Override
    public void update(Fighter fighter, Player player) {
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        createBlock(player.battle.getStage(), player.hitbox.getCenter(new Vector2()));
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
    }

    @Override
    public void chargeAttack(Fighter fighter, Player player, int charge) {
    }

    @Override
    public void charging(Fighter fighter, Player player, int charge) {
    }
}
