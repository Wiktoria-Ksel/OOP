package resort.simulation;

import resort.model.Athlete;
import resort.model.Route;

public class RouteFinishEvent extends Event {
    private final Athlete athlete;
    private final Route route;

    public RouteFinishEvent(int time, Athlete athlete, Route route) {
        super(time);
        this.athlete = athlete;
        this.route = route;
    }

    @Override
    public void execute(Simulation simulation) {
        if (athlete.isTracked()) {
            simulation.report(
                    getTime(),
                    "Athlete " + athlete.getId()
                            + " finished skiing down the route no. " + route.getId() + "."
            );
        }

        athlete.setCurrentNode(route.getTo());

        athlete.recordDescent(route);

        if (getTime() < Simulation.DECISION_END_TIME) {
            athlete.chooseNextActivity(simulation, getTime());
        }
    }
}