package sab.game.stages;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import sab.game.Player;

public class Stage {
    public String id;
    public String name;

    protected List<StageObject> stageObjects;
    protected List<Ledge> ledges;
    private StageType type;

    public Stage(StageType type) {
        id = "stage";
        name = "Stage";
        stageObjects = new ArrayList<>();
        ledges = new ArrayList<>();
        
        this.type = type;
        type.init(this);
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

    public void addStageObject(StageObject stageObject) {
        stageObjects.add(stageObject);
    }

    public void addLedge(Ledge ledge) {
        ledges.add(ledge);
    }

    public List<StageObject> getStageObjects() {
        return stageObjects;
    }
}