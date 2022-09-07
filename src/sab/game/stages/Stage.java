package sab.game.stages;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.seagull_engine.Seagraphics;

import sab.game.Player;

public class Stage {
    public String id;
    public String name;
    public String background;
    public String music;

    protected List<StageObject> stageObjects;
    protected List<Ledge> ledges;

    // Players can be outside this blast zone safely when not taking knockback
    protected Rectangle safeBlastZone;

    // Players cannot be below or to the right/left of this blast zone even when not taking knockback
    protected Rectangle unsafeBlastZone;
    
    private StageType type;

    public Stage(StageType type) {
        id = "stage";
        name = "Stage";
        background = "background.png";
        music = "last_location.mp3";
        stageObjects = new ArrayList<>();
        ledges = new ArrayList<>();

        safeBlastZone = new Rectangle(-1152 / 2 - 64, -704 / 2 - 64, 1152 + 128, 704 + 128);
        unsafeBlastZone = new Rectangle(-1152 / 2 - 128, -704 / 2 - 128, 1152 + 256, 704 + 256);
        
        this.type = type;
        this.type.init(this);
    }

    public void update() {
        for (StageObject stageObject : stageObjects) {
            stageObject.preUpdate();
        }
        for (Ledge ledge : ledges) {
            ledge.update();
        }
    }

    public Ledge grabLedge(Player player) {
        for (Ledge ledge : ledges) {
            if (ledge.grabBox.overlaps(player.hitbox)) {
                return ledge;
            }
        }

        return null;
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

    public void addStageObject(StageObject stageObject) {
        stageObjects.add(stageObject);
    }

    public void addLedge(Ledge ledge) {
        ledges.add(ledge);
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
}