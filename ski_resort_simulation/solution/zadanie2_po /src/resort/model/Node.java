package resort.model;

import java.util.ArrayList;
import java.util.List;

// Represents a graph node where routes and lifts can start or end.
public class Node {
    private final int id;
    private final int height;
    private final int x;
    private final int y;
    private final boolean connected;

    private Route[] outgoingRoutes;
    private Lift[] outgoingLifts;

    private int routeCount;
    private int liftCount;

    public Node(int id, int height, int x, int y, boolean connected) {
        this.id = id;
        this.height = height;
        this.x = x;
        this.y = y;
        this.connected = connected;

        this.outgoingRoutes = new Route[0];
        this.outgoingLifts = new Lift[0];

        this.routeCount = 0;
        this.liftCount = 0;
    }

    public int getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isConnected() {
        return connected;
    }

    public void initRoutes(int count) {
        outgoingRoutes = new Route[count];
        routeCount = 0;
    }

    public void addRoute(Route route) {
        outgoingRoutes[routeCount] = route;
        routeCount++;
    }

    public int getRouteCount() {
        return routeCount;
    }

    public Route getRoute(int index) {
        return outgoingRoutes[index];
    }

    public void initLifts(int count) {
        outgoingLifts = new Lift[count];
        liftCount = 0;
    }

    public void addLift(Lift lift) {
        outgoingLifts[liftCount] = lift;
        liftCount++;
    }

    public int getLiftCount() {
        return liftCount;
    }

    public Lift getLift(int index) {
        return outgoingLifts[index];
    }

    public List<Connection> getOutgoingConnections() {
        List<Connection> connections = new ArrayList<>();
        for (int i = 0; i < routeCount; i++) {
            connections.add(outgoingRoutes[i]);
        }
        for (int i = 0; i < liftCount; i++) {
            connections.add(outgoingLifts[i]);
        }
        return connections;
    }
}