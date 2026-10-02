package resort.model;

import resort.queue.AthleteQueue;
import resort.simulation.Simulation;

public class Lift implements Connection {
    private final int id;
    private final Node from;
    private final Node to;
    private final int interval;
    private final int capacity;
    private final int travelTime;

    private int rideCount;
    private final AthleteQueue waitingQueue;

    private int maxQueueLength;
    private int totalTransported;
    private double accumulatedQueueLength;
    private int lastQueueUpdateTime;

    public Lift(int id, Node from, Node to, int interval, int capacity, int travelTime) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.interval = interval;
        this.capacity = capacity;
        this.travelTime = travelTime;
        this.rideCount = 0;
        this.waitingQueue = new AthleteQueue();

        this.maxQueueLength = 0;
        this.totalTransported = 0;
        this.accumulatedQueueLength = 0.0;
        this.lastQueueUpdateTime = 0;
    }

    @Override
    public int getId() { return id; }
    @Override
    public Node getFrom() { return from; }
    @Override
    public Node getTo() { return to; }
    @Override
    public boolean isLift() { return true; }

    public int getInterval() { return interval; }
    public int getCapacity() { return capacity; }
    public int getTravelTime() { return travelTime; }
    public int getRideCount() { return rideCount; }

    public void incrementRideCount() {
        rideCount++;
    }

    public void addWaitingAthlete(Athlete athlete, int time) {
        updateQueueStats(time);
        waitingQueue.add(athlete);
        updateQueueStats(time);
    }

    public Athlete removeFirstWaitingAthlete(int time) {
        updateQueueStats(time);
        Athlete athlete = waitingQueue.removeFirst();
        updateQueueStats(time);
        return athlete;
    }

    public boolean hasWaitingAthletes() {
        return !waitingQueue.isEmpty();
    }

    public void updateQueueStats(int currentTime) {
        int statsTime = Math.min(currentTime, Simulation.DECISION_END_TIME);
        int currentLen = waitingQueue.size();
        int duration = statsTime - lastQueueUpdateTime;

        if (duration > 0) {
            accumulatedQueueLength += currentLen * duration;
        }

        if (lastQueueUpdateTime < Simulation.DECISION_END_TIME) {
            lastQueueUpdateTime = statsTime;
        }

        if (currentTime < Simulation.DECISION_END_TIME) {
            if (currentLen > maxQueueLength) {
                maxQueueLength = currentLen;
            }
        }
    }

    @Override
    public void traverse(Athlete athlete, Simulation simulation, int time) {
        athlete.joinLiftQueue(this, simulation, time);
    }

    public int getMaxQueueLength() {
        return maxQueueLength;
    }

    public double getAverageQueueLength() {
        return accumulatedQueueLength / (double) Simulation.DECISION_END_TIME;
    }

    public int getTotalTransported() {
        return totalTransported;
    }

    public void incrementTotalTransported() {
        totalTransported++;
    }

    // Definition as declared at forum.
    public int getMaxPossiblePassengers() {
        int departures = (Simulation.DECISION_END_TIME - 1) / interval + 1;
        return departures * capacity;
    }

    public double getOccupiedPercentage() {
        int maxPossible = getMaxPossiblePassengers();
        if (maxPossible == 0) return 0.0;
        return (double) totalTransported * 100.0 / maxPossible;
    }
}