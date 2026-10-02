package resort.model;

import resort.simulation.Simulation;
import resort.simulation.RouteFinishEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class Athlete {
    protected final int id;
    protected final int skillLevel;
    protected final double spontaneity;
    protected final double beta;
    protected final double difficultyWeight;
    protected final double surfaceWeight;
    protected final double boredomWeight;
    protected final boolean tracked;
    protected final Node startNode;
    protected final int startTime;
    protected final int[] athleteRideCounts;
    protected Node currentNode;

    // Structures for boredom optimizations.
    protected final double[] boredom;
    protected final int[] lastUpdated;
    protected int totalDescents;

    // Helper class for transition representation.
    public static class Transit {
        private final Connection connection;
        private final int seq;

        public Transit(Connection connection, int seq) {
            this.connection = connection;
            this.seq = seq;
        }

        public Connection getConnection() { return connection; }
        public int getSeq() { return seq; }
    }

    // List for tracked athletes only.
    protected final List<Transit> transitHistory = new ArrayList<>();

    public Athlete(
            int id,
            int skillLevel,
            double spontaneity,
            double beta,
            double difficultyWeight,
            double surfaceWeight,
            double boredomWeight,
            boolean tracked,
            Node startNode,
            int startTime,
            int numRoutes
    ) {
        this.id = id;
        this.skillLevel = skillLevel;
        this.spontaneity = spontaneity;
        this.beta = beta;
        this.difficultyWeight = difficultyWeight;
        this.surfaceWeight = surfaceWeight;
        this.boredomWeight = boredomWeight;
        this.tracked = tracked;
        this.startNode = startNode;
        this.currentNode = startNode;
        this.startTime = startTime;

        this.boredom = new double[numRoutes];
        this.lastUpdated = new int[numRoutes];
        this.athleteRideCounts = new int[numRoutes];
        this.totalDescents = 0;
    }

    public int getId() { return id; }
    public int getSkillLevel() { return skillLevel; }
    public double getDifficultyWeight() { return difficultyWeight; }
    public double getSurfaceWeight() { return surfaceWeight; }
    public double getBoredomWeight() { return boredomWeight; }
    public boolean isTracked() { return tracked; }
    public Node getStartNode() { return startNode; }
    public int getStartTime() { return startTime; }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public double getBoredom(Route route) {
        int steps = totalDescents - lastUpdated[route.getId()];
        return boredom[route.getId()] * Math.pow(1.0 - beta, steps);
    }

    public void recordDescent(Route route) {
        int routeId = route.getId();
        athleteRideCounts[routeId]++;

        double prevBoredom = getBoredom(route);
        double newBoredom = beta + (1.0 - beta) * prevBoredom;

        totalDescents++;
        boredom[routeId] = newBoredom;
        lastUpdated[routeId] = totalDescents;
    }

    public void recordTransit(Connection connection) {
        if (tracked) {
            transitHistory.add(new Transit(connection, transitHistory.size() + 1));
        }
    }

    public List<Integer> getHistoryForConnection(Connection connection) {
        List<Integer> seqs = new ArrayList<>();
        for (Transit t : transitHistory) {
            if (t.getConnection() == connection) {
                seqs.add(t.getSeq());
            }
        }
        return seqs;
    }

    public void chooseNextActivity(Simulation simulation, int time) {
        planNextActivity(simulation, time);
    }

    protected abstract void planNextActivity(Simulation simulation, int time);

    protected void chooseRandomActivity(Simulation simulation, int time) {
        int routeCount = currentNode.getRouteCount();
        int liftCount = currentNode.getLiftCount();
        int optionCount = routeCount + liftCount;

        if (optionCount == 0) return;

        int chosenOption = simulation.getRandom().nextInt(optionCount);

        if (chosenOption < routeCount) {
            Route route = currentNode.getRoute(chosenOption);
            startRoute(route, simulation, time);
        } else {
            int liftIndex = chosenOption - routeCount;
            Lift lift = currentNode.getLift(liftIndex);
            joinLiftQueue(lift, simulation, time);
        }
    }

    protected void chooseBestActivity(Simulation simulation, int time) {
        Route bestRoute = null;
        Lift liftLeadingToBestRoute = null;
        double bestAttractiveness = -1.0;

        for (int i = 0; i < currentNode.getRouteCount(); i++) {
            Route route = currentNode.getRoute(i);
            double attractiveness = route.attractivenessFor(this);
            if (attractiveness > bestAttractiveness) {
                bestAttractiveness = attractiveness;
                bestRoute = route;
            }
        }

        for (int i = 0; i < currentNode.getLiftCount(); i++) {
            Lift lift = currentNode.getLift(i);
            Node upperNode = lift.getTo();

            for (int j = 0; j < upperNode.getRouteCount(); j++) {
                Route route = upperNode.getRoute(j);
                double attractiveness = route.attractivenessFor(this);
                if (attractiveness > bestAttractiveness) {
                    bestAttractiveness = attractiveness;
                    bestRoute = route;
                    liftLeadingToBestRoute = lift;
                }
            }
        }

        if (bestRoute == null) {
            chooseRandomActivity(simulation, time);
            return;
        }

        if (liftLeadingToBestRoute == null) {
            startRoute(bestRoute, simulation, time);
        } else {
            joinLiftQueue(liftLeadingToBestRoute, simulation, time);
        }
    }

    protected void startRoute(Route route, Simulation simulation, int time) {
        if (tracked) {
            simulation.report(
                    time,
                    "Athlete " + id + " started skiing down the route no. " + route.getId() + "."
            );
        }
        route.incrementRideCount();
        recordTransit(route);

        simulation.addEvent(new RouteFinishEvent(time + route.getTravelTime(), this, route));
    }

    protected void joinLiftQueue(Lift lift, Simulation simulation, int time) {
        if (tracked) {
            simulation.report(time,
                    "Athlete " + id + " lined up for lift no. " + lift.getId() + "."
            );
        }
        lift.addWaitingAthlete(this, time);
    }
}