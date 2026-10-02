package resort;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import resort.model.*;
import resort.simulation.*;

public class LiftTest {
    private Node from;
    private Node to;
    private Lift lift;
    private LocalAthlete a1, a2, a3, a4, a5;
    private ResortData data;
    private EventQueue queue;
    private Simulation simulation;

    @BeforeEach
    public void setUp() {
        from = new Node(0, 100, 0, 0, true);
        to = new Node(1, 200, 1, 1, false);
        lift = new Lift(0, from, to, 10, 3, 100);

        a1 = new LocalAthlete(1, 5, 0.0, 0.2, 0.5, 0.5, 0.0, false, from, 0, 0);
        a2 = new LocalAthlete(2, 5, 0.0, 0.2, 0.5, 0.5, 0.0, false, from, 0, 0);
        a3 = new LocalAthlete(3, 5, 0.0, 0.2, 0.5, 0.5, 0.0, false, from, 0, 0);
        a4 = new LocalAthlete(4, 5, 0.0, 0.2, 0.5, 0.5, 0.0, false, from, 0, 0);
        a5 = new LocalAthlete(5, 5, 0.0, 0.2, 0.5, 0.5, 0.0, false, from, 0, 0);

        Node[] nodes = {from, to};
        Lift[] lifts = {lift};
        Route[] routes = {};
        Athlete[] athletes = {a1, a2, a3, a4, a5};

        data = new ResortData(nodes, lifts, routes, athletes);
        queue = new PriorityEventQueue();
        simulation = new Simulation(data, queue);
    }

    /**
     * Scenario 1:
     * A lift with a capacity of 3 and an empty queue. Four athletes line up.
     * Departure occurs. Three depart, one remains. The ride counter increases by 3.
     */
    @Test
    public void testThreeDepartOneLeftInQueue() {
        assertFalse(lift.hasWaitingAthletes());

        lift.addWaitingAthlete(a1, 0);
        lift.addWaitingAthlete(a2, 0);
        lift.addWaitingAthlete(a3, 0);
        lift.addWaitingAthlete(a4, 0);

        LiftDepartureEvent event = new LiftDepartureEvent(0, lift);
        event.execute(simulation);

        assertTrue(lift.hasWaitingAthletes());

        Athlete leftAthlete = lift.removeFirstWaitingAthlete(0);
        Assertions.assertEquals(a4, leftAthlete);
        assertFalse(lift.hasWaitingAthletes());

        assertEquals(3, lift.getRideCount());
    }

    /**
     * Scenario 2:
     * Lift with a capacity of 3. Two athletes line up.
     * Departure takes place. Both depart.
     */
    @Test
    public void testTwoDepartBothLeaveQueue() {
        lift.addWaitingAthlete(a1, 0);
        lift.addWaitingAthlete(a2, 0);

        LiftDepartureEvent event = new LiftDepartureEvent(0, lift);
        event.execute(simulation);

        assertFalse(lift.hasWaitingAthletes());

        assertEquals(2, lift.getRideCount());
    }

    /**
     * Scenario 3:
     * Four athletes line up one by one. A carriage departs, and then another athlete joins the line.
     * The maximum queue length should correctly be 4.
     */
    @Test
    public void testMaxQueueLengthTracking() {
        lift.addWaitingAthlete(a1, 0);
        lift.addWaitingAthlete(a2, 5);
        lift.addWaitingAthlete(a3, 10);
        lift.addWaitingAthlete(a4, 15);

        assertEquals(4, lift.getMaxQueueLength());

        LiftDepartureEvent event = new LiftDepartureEvent(20, lift);
        event.execute(simulation);

        lift.addWaitingAthlete(a5, 25);

        assertEquals(4, lift.getMaxQueueLength());
    }
}