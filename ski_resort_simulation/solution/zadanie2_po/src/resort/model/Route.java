package resort.model;

import resort.simulation.Simulation;

public class Route implements Connection {
    private final int id;
    private final Node from;
    private final Node to;
    private final int difficulty;
    private final int travelTime;
    private final double baseAttractiveness;
    private final double resistance;
    private int rideCount;

    public Route(
            int id,
            Node from,
            Node to,
            int difficulty,
            int travelTime,
            double baseAttractiveness,
            double resistance
    ) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.difficulty = difficulty;
        this.travelTime = travelTime;
        this.baseAttractiveness = baseAttractiveness;
        this.resistance = resistance;
        this.rideCount = 0;
    }

    @Override
    public int getId() { return id; }
    @Override
    public Node getFrom() { return from; }
    @Override
    public Node getTo() { return to; }
    @Override
    public boolean isLift() { return false; }

    public int getTravelTime() { return travelTime; }
    public int getDifficulty() { return difficulty; }
    public double getBaseAttractiveness() { return baseAttractiveness; }
    public double getResistance() { return resistance; }
    public int getRideCount() { return rideCount; }

    public void incrementRideCount() {
        rideCount++;
    }

    public double attractivenessFor(Athlete athlete) {
        double difficultyFit = difficultyFitFor(athlete);
        double surface = surfaceAttractiveness();
        double z = athlete.getBoredom(this);

        return athlete.getDifficultyWeight() * difficultyFit
                + athlete.getSurfaceWeight() * surface
                + athlete.getBoredomWeight() * (1.0 - z);
    }

    private double difficultyFitFor(Athlete athlete) {
        int routeLevel = difficulty;
        int athleteLevel = athlete.getSkillLevel();

        if (routeLevel >= athleteLevel + 5) {
            return 0.0;
        }
        if (routeLevel >= athleteLevel) {
            return 1.0 - (routeLevel - athleteLevel) / 5.0;
        }
        return Math.max(0.2, 1.0 - (athleteLevel - routeLevel) / 7.0);
    }

    public double surfaceAttractiveness() {
        return baseAttractiveness
                + (1.0 - baseAttractiveness) * Math.pow(resistance, rideCount);
    }

    public double getCurrentSurfaceAttractiveness() {
        return surfaceAttractiveness();
    }

    @Override
    public void traverse(Athlete athlete, Simulation simulation, int time) {
        athlete.startRoute(this, simulation, time);
    }
}