package sab.game.stage;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import org.lwjgl.system.NonnullDefault;
import sab.game.*;

public class Stage {
    public String id;
    public String name;
    public String background;
    public String music;
    public float maxZoomOut;
    public float player1SpawnX;
    public float player2SpawnX;

    protected List<StageObject> stageObjects;
    protected List<Ledge> ledges;

    // Players can be outside this blast zone safely when not taking knockback
    protected Rectangle safeBlastZone;

    // Players cannot be below or to the right/left of this blast zone even when not taking knockback
    protected Rectangle unsafeBlastZone;

    protected Battle battle;
    public StageType type;

    public Stage(StageType type) {
        id = "stage";
        name = "Stage";
        background = "background.png";
        music = "last_location.mp3";
        stageObjects = new ArrayList<>();
        ledges = new ArrayList<>();
        maxZoomOut = 1;

        safeBlastZone = new Rectangle(-Game.game.window.resolutionX / 2 - 64, -Game.game.window.resolutionY / 2 - 64, Game.game.window.resolutionX + 128, Game.game.window.resolutionY + 128);
        unsafeBlastZone = new Rectangle(-Game.game.window.resolutionX / 2 - 128, -Game.game.window.resolutionY / 2 - 128, Game.game.window.resolutionX + 256, Game.game.window.resolutionY + 256);
        player1SpawnX = -128;
        player2SpawnX = 128;
        this.type = type;
    }

    public void reset() {
        if (battle.getGameObjects() != null) {
            battle.getGameObjects().removeAll(stageObjects);
            battle.getGameObjects().removeAll(ledges);
        }
        stageObjects.clear();
        ledges.clear();
    }

    public void update() {
        type.update(battle, this);
        List<StageObject> deadStageObjects = new ArrayList<>();
        for (StageObject stageObject : stageObjects) {
            stageObject.updateStageObject(battle);
            if (!stageObject.alive) {
                deadStageObjects.add(stageObject);
            }
        }
        stageObjects.removeAll(deadStageObjects);
        List<Ledge> deadLedges = new ArrayList<>();
        for (Ledge ledge : ledges) {
            ledge.update();
            if (ledge.ownerRemoved()) {
                deadLedges.add(ledge);
            }
        }
        ledges.removeAll(deadLedges);
    }

    public void init() {
        reset();
        type.init(this);
    }

    public Ledge grabLedge(Player player) {
        for (Ledge ledge : ledges) {
            if (ledge.grabBox.overlaps(player.hitbox)) {
                return ledge;
            }
        }

        return null;
    }

    public void onPlayerHit(Player player, DamageSource damageSource, boolean finishingBlow) {
        type.onPlayerHit(this, player, damageSource, finishingBlow);
    }

    public List<Ledge> getLedges() {
        return ledges;
    }

    public Rectangle getSafeBlastZone() {
        return new Rectangle(safeBlastZone);
    }

    public Rectangle getUnsafeBlastZone() {
        return new Rectangle(unsafeBlastZone);
    }

    public void addStageObject(StageObject stageObject, int index) {
        stageObjects.add(index, stageObject);
    }

    public void addStageObject(StageObject stageObject) {
        stageObjects.add(stageObject);
    }

    public void addLedge(Ledge ledge) {
        ledges.add(ledge);
    }

    public float getStageEdge(Direction side) {
        switch (side) {
            case UP :
                return safeBlastZone.y + safeBlastZone.height;
            case DOWN :
                return safeBlastZone.y;
            case LEFT :
                return safeBlastZone.x;
            case RIGHT :
                return safeBlastZone.x + safeBlastZone.width;
            default :
                return 0;
        }
    }

    public List<StageObject> getStageObjects() {
        return stageObjects;
    }

    public void renderDetails(Seagraphics g) {
        for (StageObject platform : stageObjects) {
            if (platform.inBackground()) platform.render(g);
        }
    }

    public void renderPlatforms(Seagraphics g) {
        for (StageObject platform : stageObjects) {
            if (!platform.inBackground()) platform.render(g);
        }
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public void renderBackground(Seagraphics g) {
        type.renderBackground(this, g);
    }

    public void renderOverlay(Seagraphics g) {
        type.renderOverlay(this, g);
    }
}