package resort.model;

public class ResortData {
    private final Node[] nodes;
    private final Lift[] lifts;
    private final Route[] routes;
    private final Athlete[] athletes;

    public ResortData(Node[] nodes, Lift[] lifts, Route[] routes, Athlete[] athletes) {
        this.nodes = nodes;
        this.lifts = lifts;
        this.routes = routes;
        this.athletes = athletes;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public Lift[] getLifts() {
        return lifts;
    }

    public Route[] getRoutes() {
        return routes;
    }

    public Athlete[] getAthletes() {
        return athletes;
    }
}