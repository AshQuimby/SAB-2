package sab.game.ai.pathfinding;

import com.badlogic.gdx.math.Vector2;

public class Node {
    public final Vector2 position;
    public final NodeType type;

    public Node(Vector2 position, NodeType type) {
        this.position = position.cpy();
        this.type = type;
    }
}
