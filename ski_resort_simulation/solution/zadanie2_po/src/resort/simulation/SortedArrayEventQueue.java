package resort.simulation;

// Event queue implemented with a dynamically growing sorted array.
// Events are stored in descending time order,
// so that the earliest event is removed in O(1).
public class SortedArrayEventQueue implements EventQueue {
    private Event[] events;
    private int size;

    public SortedArrayEventQueue() {
        this.events = new Event[16];
        this.size = 0;
    }

    @Override
    public void add(Event event) {
        ensureCapacity();

        int position = size;

        // For equal times, the new event is placed before older events,
        // because events are removed from the end of the array.
        while (position > 0 && events[position - 1].getTime() <= event.getTime()) {
            events[position] = events[position - 1];
            position--;
        }

        events[position] = event;
        size++;
    }

    @Override
    public Event removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty. Cannot delete event");
        }

        Event first = events[size - 1];
        events[size - 1] = null;
        size--;

        return first;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (size < events.length) {
            return;
        }

        Event[] newEvents = new Event[events.length * 2];

        System.arraycopy(events, 0, newEvents, 0, events.length);

        events = newEvents;
    }
}