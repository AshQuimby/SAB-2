package sab.game.stage;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import com.seagull_engine.graphics.ParallaxBackground;
import org.lwjgl.system.NonnullDefault;
import sab.game.*;
import sab.game.ai.pathfinding.Edge;
import sab.game.ai.pathfinding.Graph;
import sab.game.ai.pathfinding.Node;
import sab.game.ai.pathfinding.NodeType;
import sab.util.Utils;

public class Stage {
    public String id;
    public String name;
    public String background;
    public String music;
    public float maxZoomOut;
    public float player1SpawnX;
    public float player2SpawnX;
    public float player1SpawnY;
    public float player2SpawnY;
    public boolean projectPlayerSpawns;
    public boolean descendingRespawnPlatforms;

    protected List<StageObject> stageObjects;
    protected List<Ledge> ledges;
    protected List<Slope> slopes;

    // Used for pathfinding
    public Graph<Node> graph;

    // Players can be outside this blast zone safely when not taking knockback
    protected Rectangle safeBlastZone;

    // Players cannot be below or to the right/left of this blast zone even when not taking knockback
    protected Rectangle unsafeBlastZone;

    protected Battle battle;
    public StageType type;
    public ParallaxBackground parallaxBackground;

    public Stage(StageType type) {
        id = "stage";
        name = "Stage";
        background = "background.png";
        music = "last_location.mp3";
        stageObjects = new ArrayList<>();
        ledges = new ArrayList<>();
        slopes = new ArrayList<>();
        graph = new Graph<>();
        maxZoomOut = 1;

        safeBlastZone = new Rectangle(-Game.game.window.resolutionX / 2 - 64, -Game.game.window.resolutionY / 2 - 64, Game.game.window.resolutionX + 128, Game.game.window.resolutionY + 128);
        unsafeBlastZone = new Rectangle(-Game.game.window.resolutionX / 2 - 128, -Game.game.window.resolutionY / 2 - 128, Game.game.window.resolutionX + 256, Game.game.window.resolutionY + 256);
        player1SpawnX = -128;
        player2SpawnX = 128;
        player1SpawnY = safeBlastZone.y + safeBlastZone.height;
        player2SpawnY = safeBlastZone.y + safeBlastZone.height;
        projectPlayerSpawns = true;
        descendingRespawnPlatforms = true;
        this.type = type;
    }

    public void reset() {
        if (battle.getGameObjects() != null) {
            battle.getGameObjects().removeAll(stageObjects);
            battle.getGameObjects().removeAll(ledges);
        }
        stageObjects.clear();
        ledges.clear();
        slopes.clear();
        graph = new Graph<>();
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

    private void generateGraph() {
        for (Ledge ledge : ledges) {
            graph.addNode(new Node(ledge.grabBox.getCenter(new Vector2()), NodeType.LEDGE));
        }

        for (StageObject stageObject : stageObjects) {
            if (stageObject.isSolid() || stageObject instanceof PassablePlatform) {
                List<Rectangle> collisionTests = new ArrayList<>();

                for (StageObject other : stageObjects) {
                    if ((other.isSolid() || other instanceof PassablePlatform) && other != stageObject) collisionTests.add(other.hitbox);
                }
                for (Slope slope : slopes) collisionTests.add(slope.bounds);

                Vector2 left = new Vector2(stageObject.hitbox.x, stageObject.hitbox.y + stageObject.hitbox.height);
                Vector2 right = new Vector2(stageObject.hitbox.x + stageObject.hitbox.width, stageObject.hitbox.y + stageObject.hitbox.height);

                for (Rectangle test : collisionTests) {
                    if (left != null && test.contains(left)) left = null;
                    if (right != null && test.contains(right)) right = null;
                    if (left == null && right == null) break;
                }

                if (left != null) graph.addNode(new Node(left.add(0, 16), NodeType.GROUND));
                if (right != null) graph.addNode(new Node(right.add(0, 16), NodeType.GROUND));
            }
        }

        List<Rectangle> collisionTests = new ArrayList<>(stageObjects.size() + slopes.size());
        for (StageObject stageObject : stageObjects) {
            if (stageObject.isSolid()) collisionTests.add(stageObject.hitbox);
        }
        for (Slope slope : slopes) collisionTests.add(slope.bounds);

        for (Node node : graph.getNodes()) {
            for (Node other : graph.getNodes()) {
                if (node == other) continue;

                if (!Utils.raycast(node.position, other.position, collisionTests.toArray(new Rectangle[0]))) {
                    graph.addEdge(new Edge<>(node, other));
                }
            }
        }
    }

    public void init() {
        reset();
        type.init(this);
        generateGraph();

        List<Node> disconnectedNodes = new ArrayList<>();
        for (Node node : graph.getNodes()) {
            // Assume all connections go both ways
            if (graph.getEdges(node).size() == 0) disconnectedNodes.add(node);
        }
        disconnectedNodes.forEach(graph.getNodes()::remove);
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

    public List<Slope> getSlopes() {
        return slopes;
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

    public void addSlope(Slope slope) {
        slopes.add(slope);
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
        if (parallaxBackground != null) parallaxBackground.render(g);
    }

    public void renderOverlay(Seagraphics g) {
        type.renderOverlay(this, g);
    }
}