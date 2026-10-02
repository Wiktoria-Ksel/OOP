package resort.simulation;

import resort.model.Node;
import resort.model.Route;
import resort.model.Connection;
import resort.model.ResortData;
import java.util.*;

public class PathFinder {
    public static Map<Node, Integer> computeDistances(Node startNode, ResortData data) {
        Map<Node, Integer> distances = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();

        queue.add(startNode);
        distances.put(startNode, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int dist = distances.get(current);

            for (Connection conn : current.getOutgoingConnections()) {
                Node next = conn.getTo();
                if (!distances.containsKey(next)) {
                    distances.put(next, dist + 1);
                    queue.add(next);
                }
            }
        }
        return distances;
    }

    public static List<Connection> findPath(Node startNode, Route targetRoute, ResortData data) {
        if (startNode == targetRoute.getFrom()) {
            List<Connection> path = new ArrayList<>();
            path.add(targetRoute);
            return path;
        }

        Queue<Node> queue = new LinkedList<>();
        Map<Node, Connection> incomingEdge = new HashMap<>();
        Map<Node, Integer> distance = new HashMap<>();

        queue.add(startNode);
        distance.put(startNode, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            int dist = distance.get(current);

            for (Connection conn : current.getOutgoingConnections()) {
                Node next = conn.getTo();
                if (!distance.containsKey(next)) {
                    distance.put(next, dist + 1);
                    incomingEdge.put(next, conn);
                    queue.add(next);
                }
            }
        }

        List<Connection> path = new ArrayList<>();
        Node curr = targetRoute.getFrom();
        while (curr != startNode) {
            Connection conn = incomingEdge.get(curr);
            path.add(0, conn);
            curr = conn.getFrom();
        }
        path.add(targetRoute);
        return path;
    }
}