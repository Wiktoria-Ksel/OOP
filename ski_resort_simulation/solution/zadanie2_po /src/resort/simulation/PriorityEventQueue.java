package resort.simulation;

import java.util.PriorityQueue;

public class PriorityEventQueue implements EventQueue {
    private final PriorityQueue<EventQueueEntry> queue = new PriorityQueue<>();
    private long counter = 0;

    @Override
    public void add(Event event) {
        queue.add(new EventQueueEntry(event, counter++));
    }

    @Override
    public Event removeFirst() {
        EventQueueEntry wpis = queue.poll();
        return wpis != null ? wpis.getEvent() : null;
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }
}