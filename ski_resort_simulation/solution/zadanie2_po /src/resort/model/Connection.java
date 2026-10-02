package resort.model;

import resort.simulation.Simulation;

public interface Connection {
    int getId();
    Node getFrom();
    Node getTo();
    boolean isLift();

    void traverse(Athlete athlete, Simulation simulation, int time);
}