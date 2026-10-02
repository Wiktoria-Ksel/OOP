package resort.queue;

import resort.model.Athlete;

// A FIFO queue implemented with a dynamically growing circular buffer.
public class AthleteQueue {
    private Athlete[] athletes;
    private int first;
    private int size;

    public AthleteQueue() {
        athletes = new Athlete[1];
        first = 0;
        size = 0;
    }

    public void add(Athlete athlete) {
        ensureCapacity();

        int index = (first + size) % athletes.length;
        athletes[index] = athlete;
        size++;
    }

    public Athlete removeFirst() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot remove athlete from empty queue.");
        }

        Athlete result = athletes[first];
        athletes[first] = null;

        first = (first + 1) % athletes.length;
        size--;

        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    // Keeps the logical order of the elements.
    private void ensureCapacity() {
        if (size < athletes.length) {
            return;
        }

        Athlete[] newAthletes = new Athlete[athletes.length * 2];

        for (int i = 0; i < size; i++) {
            int oldIndex = (first + i) % athletes.length;
            newAthletes[i] = athletes[oldIndex];
        }

        athletes = newAthletes;
        first = 0;
    }
}