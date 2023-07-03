package sab.game.ai.pathfinding;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sab.game.stage.Slope;
import sab.game.stage.Stage;
import sab.game.stage.StageObject;
import sab.util.SabRandom;
import sab.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class Navigator {
    private final Stage stage;
    private final Graph<Node> graph;

    private final Vector2 position;
    private final Vector2 goal;

    private List<Node> path;

    private boolean losToGoal;

    public Navigator(Stage stage) {
        this.stage = stage;
        graph = stage.graph;

        position = new Vector2();
        goal = new Vector2();
    }

    public void setPosition(Vector2 position) {
        this.position.set(position);
    }

    public void setGoal(Vector2 goal) {
        this.goal.set(goal);
    }

    private Node getNearestUnblockedNode(Vector2 position, Rectangle... obstacles) {
        Node nearestAccessibleNode = null;
        float shortestDistance = -1;

        for (Node node : graph.getNodes()) {
            if (!Utils.raycast(position, node.position, obstacles)) {
                float distance = position.dst2(node.position);
                if (nearestAccessibleNode == null || distance < shortestDistance) {
                    nearestAccessibleNode = node;
                    shortestDistance = distance;
                }
            }
        }

        return nearestAccessibleNode;
    }

    public void update() {
        List<Rectangle> obstacleList = new ArrayList<>(stage.getStageObjects().size());
        stage.getStageObjects().stream().filter(StageObject::isSolid).map((StageObject s) -> s.hitbox).forEach(obstacleList::add);
        stage.getSlopes().forEach((Slope s) -> {
            if (!s.bounds.contains(goal) && !s.bounds.contains(position)) obstacleList.add(s.bounds);
        });
        Rectangle[] obstacles = obstacleList.toArray(new Rectangle[0]);

        losToGoal = !Utils.raycast(position, goal, obstacles);

        if ((path == null || path.size() == 0) && !losToGoal) {
            Node startingNode = getNearestUnblockedNode(position, obstacles);
            Node endingNode = getNearestUnblockedNode(goal, obstacles);
            if (startingNode == endingNode) return;

            if (startingNode != null && endingNode != null) {
                path = graph.calculatePath(startingNode, endingNode, (Node a, Node b) -> a.position.dst(b.position));
            }
        } else {
            // If target is closer to sampled node than current destination node, recalculate path to sampled node
            List<Node> nodes = graph.getNodes().stream().toList();
            if (nodes.size() == 0) return;
            if (path == null || path.size() == 0) return;
            Node sample = nodes.get(SabRandom.random(0, nodes.size()));

            if (!Utils.raycast(goal, sample.position)) {
                Node last = path.get(path.size() - 1);
                if (goal.dst2(sample.position) < goal.dst2(last.position)) {
                    Node startNode = getNearestUnblockedNode(position, obstacles);
                    if (startNode != null) {
                        List<Node> newPath = graph.calculatePath(startNode, sample, (Node a, Node b) -> a.position.dst(b.position));
                        if (newPath != null) path = newPath;
                    }
                }
            }

            // If the next node after the current one can be moved to directly, skip the current node
            if (path.size() > 1) {
                Node next = path.get(1);
                if (!Utils.raycast(position, next.position, obstacles)) {
                    path.remove(0);
                }
            }
        }
    }

    public Node currentNode() {
        if (path == null || path.size() == 0) return null;
        return path.get(0);
    }

    public void next() {
        if (path == null || path.size() == 0) return;
        path.remove(0);
    }

    public boolean hasLosToGoal() {
        return losToGoal;
    }
}
