package resort.simulation;

// Stores simulation events ordered by time and provides access to the earliest one.
public interface EventQueue {
    void add(Event event);

    Event removeFirst();

    boolean isEmpty();
}