package sab.game.ai.pathfinding;

public class Edge<Node> {
    public final Node a;
    public final Node b;

    public Edge(Node a, Node b) {
        this.a = a;
        this.b = b;
    }
}
