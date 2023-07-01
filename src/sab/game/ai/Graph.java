//package sab.game.ai;
//
//import com.badlogic.gdx.math.Vector2;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Graph {
//    private final Map<Integer, Node> nodes;
//    private final Map<Integer, List<Integer>> edges;
//    private int nextId;
//
//    public Graph() {
//        nodes = new HashMap<>();
//        edges = new HashMap<>();
//        nextId = 0;
//    }
//
//    public int addNode(Vector2 position) {
//        int id = nextId;
//        Node node = new Node(position);
//        nodes.put(id, new Node(position));
//        edges.put(id, new ArrayList<>());
//
//        return nextId++;
//    }
//
//    public int
//}
