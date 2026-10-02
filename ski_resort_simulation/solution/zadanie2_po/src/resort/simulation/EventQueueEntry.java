package resort.simulation;

public class EventQueueEntry implements Comparable<EventQueueEntry> {
    private final Event event;
    private final long orderingNumber;

    public EventQueueEntry(Event event, long orderingNumber) {
        this.event = event;
        this.orderingNumber = orderingNumber;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public int compareTo(EventQueueEntry other) {
        int cmp = Integer.compare(this.event.getTime(), other.event.getTime());
        if (cmp != 0) {
            return cmp;
        }
        return Long.compare(this.orderingNumber, other.orderingNumber);
    }
}