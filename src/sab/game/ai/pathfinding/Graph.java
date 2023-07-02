package sab.game.ai.pathfinding;

import java.util.*;
import java.util.function.BiFunction;

public class Graph<Node> {
    private final Set<Node> nodes;
    private final Map<Node, List<Edge<Node>>> edges;

    public Graph() {
        nodes = new HashSet<>();
        edges = new HashMap<>();
    }

    public Node addNode(Node node) {
        nodes.add(node);
        edges.put(node, new ArrayList<>());

        return node;
    }

    public void addEdge(Edge<Node> edge) {
        if (!nodes.contains(edge.a)) addNode(edge.a);
        if (!nodes.contains(edge.b)) addNode(edge.b);

        edges.get(edge.a).add(edge);
    }

    public void addTwoWayEdge(Edge<Node> edge) {
        if (!nodes.contains(edge.a)) addNode(edge.a);
        if (!nodes.contains(edge.b)) addNode(edge.b);

        edges.get(edge.a).add(edge);
        edges.get(edge.b).add(new Edge<>(edge.b, edge.a));
    }

    public List<Node> calculatePath(Node start, Node destination, BiFunction<Node, Node, Float> costFunction) {
        Map<Node, Float> totalCost = new HashMap<>();
        Map<Node, Node> cameFrom = new HashMap<>();
        PriorityQueue<Node> open = new PriorityQueue<>((Node a, Node b) -> totalCost.get(a) > totalCost.get(b) ? 1 : -1);

        open.add(start);
        totalCost.put(start, 0f);

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current == destination) break;

            for (Edge<Node> edge : edges.get(current)) {
                Node neighbor = edge.b;
                float oldCost = totalCost.getOrDefault(current, 0f);
                float newCost = costFunction.apply(edge.a, edge.b) + oldCost;

                if (!totalCost.containsKey(neighbor) || newCost < totalCost.get(neighbor)) {
                    totalCost.put(neighbor, newCost);
                    open.add(neighbor);
                    cameFrom.put(neighbor, current);
                }
            }
        }

        if (!cameFrom.containsKey(destination)) return null;
        Node current = destination;
        List<Node> path = new ArrayList<>();
        path.add(current);
        while (current != start) {
            Node previous = cameFrom.get(current);
            path.add(previous);
            current = previous;
        }

        Collections.reverse(path);
        return path;
    }

    public Set<Node> getNodes() {
        return nodes;
    }

    public List<Edge<Node>> getEdges(Node node) {
        return edges.get(node);
    }
}
