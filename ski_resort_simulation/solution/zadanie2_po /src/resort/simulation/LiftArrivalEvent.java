package resort.simulation;

import resort.model.Athlete;
import resort.model.Lift;


// Event fired when an athlete reaches the upper station of a lift.
public class LiftArrivalEvent extends Event {
    private final Athlete athlete;
    private final Lift lift;

    public LiftArrivalEvent(int time, Athlete athlete, Lift lift) {
        super(time);
        this.athlete = athlete;
        this.lift = lift;
    }

    @Override
    public void execute(Simulation simulation) {
        if (athlete.isTracked()) {
            simulation.report(
                    getTime(),
                    "Athlete " + athlete.getId()
                            + " left lift no. " + lift.getId() + "."
            );
        }

        athlete.setCurrentNode(lift.getTo());

        if (getTime() < Simulation.DECISION_END_TIME) {
            athlete.chooseNextActivity(simulation, getTime());
        }
    }
}