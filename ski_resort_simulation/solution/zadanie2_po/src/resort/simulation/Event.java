package resort.simulation;

// Base class for all simulation events stored in the event queue.
public abstract class Event {
    private final int time;

    public Event(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public abstract void execute(Simulation simulation);
}