package resort.simulation;

import resort.model.Athlete;
import resort.model.Lift;
import resort.model.ResortData;
import resort.model.Route;

import java.util.Locale;
import java.util.Random;

public class Simulation {
    private static final int OPENING_HOUR = 9;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int SECONDS_IN_HOUR = 3600;
    public static final int DECISION_END_TIME = 6 * SECONDS_IN_HOUR;
    public static final int LIFT_END_TIME = 7 * SECONDS_IN_HOUR;

    private final ResortData data;
    private final EventQueue eventQueue;
    private final Random random;

    public Simulation(ResortData data, EventQueue eventQueue) {
        this.data = data;
        this.eventQueue = eventQueue;
        this.random = new Random();
    }

    public Random getRandom() { return random; }
    public ResortData getData() { return data; }

    public void initialize() {
        scheduleAthleteStarts();
        scheduleFirstLiftDepartures();
    }

    private void scheduleAthleteStarts() {
        for (Athlete athlete : data.getAthletes()) {
            addEvent(new AthleteStartEvent(
                    athlete.getStartTime(),
                    athlete
            ));
        }
    }

    private void scheduleFirstLiftDepartures() {
        for (Lift lift : data.getLifts()) {
            addEvent(new LiftDepartureEvent(
                    0,
                    lift
            ));
        }
    }

    public void addEvent(Event event) {
        eventQueue.add(event);
    }

    public void run() {
        while (!eventQueue.isEmpty()) {
            Event event = eventQueue.removeFirst();
            if (event.getTime() <= LIFT_END_TIME) {
                event.execute(this);
            }
        }

        for (Lift lift : data.getLifts()) {
            lift.updateQueueStats(Simulation.DECISION_END_TIME);
        }

        printStatistics();
    }

    private void printStatistics() {
        System.out.println();
        System.out.println("=== Route statistics ===");
        for (Route route : data.getRoutes()) {
            System.out.println(
                    String.format(Locale.US, "Route no. %d was ridden on %d times. Surface alignment: %.5f",
                            route.getId(), route.getRideCount(), route.getCurrentSurfaceAttractiveness())
            );
        }

        System.out.println();
        System.out.println("=== Lift statistics ===");
        for (Lift lift : data.getLifts()) {
            System.out.println(String.format(Locale.US,
                    "Lift no. %d:\n" +
                            "  Max queue length: %d\n" +
                            "  Average queue length: %.2f\n" +
                            "  Total transported passengers: %d\n" +
                            "  Occupied seats: %d / %d (%.2f%%)",
                    lift.getId(),
                    lift.getMaxQueueLength(),
                    lift.getAverageQueueLength(),
                    lift.getTotalTransported(),
                    lift.getTotalTransported(),
                    lift.getMaxPossiblePassengers(),
                    lift.getOccupiedPercentage()
            ));
        }
    }

    public void report(int time, String message) {
        System.out.println(formatTime(time) + ": " + message);
    }

    private String formatTime(int secondsFromNine) {
        int totalSeconds = secondsFromNine + OPENING_HOUR * SECONDS_IN_HOUR;
        int hours = totalSeconds / SECONDS_IN_HOUR;
        int minutes = (totalSeconds % SECONDS_IN_HOUR) / SECONDS_IN_MINUTE;
        int seconds = totalSeconds % SECONDS_IN_MINUTE;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}