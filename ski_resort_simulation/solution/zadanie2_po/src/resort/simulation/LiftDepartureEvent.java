package resort.simulation;

import resort.model.Athlete;
import resort.model.Lift;

public class LiftDepartureEvent extends Event {
    private final Lift lift;

    public LiftDepartureEvent(int time, Lift lift) {
        super(time);
        this.lift = lift;
    }

    @Override
    public void execute(Simulation simulation) {
        int taken = 0;

        if (getTime() < Simulation.DECISION_END_TIME) {
            while (taken < lift.getCapacity() && lift.hasWaitingAthletes()) {
                Athlete athlete = lift.removeFirstWaitingAthlete(getTime());

                if (athlete.isTracked()) {
                    simulation.report(
                            getTime(),
                            "Athlete " + athlete.getId()
                                    + " started the ascent with lift no. " + lift.getId() + "."
                    );
                }

                lift.incrementRideCount();
                lift.incrementTotalTransported();

                athlete.recordTransit(lift);

                simulation.addEvent(new LiftArrivalEvent(
                        getTime() + lift.getTravelTime(),
                        athlete,
                        lift
                ));

                taken++;
            }
        }

        int nextDepartureTime = getTime() + lift.getInterval();
        if (nextDepartureTime < Simulation.LIFT_END_TIME) {
            simulation.addEvent(new LiftDepartureEvent(nextDepartureTime, lift));
        }
    }
}