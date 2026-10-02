package resort.simulation;

import resort.model.Athlete;

// Event that starts an athlete's day on the slope and triggers their first activity choice.
public class AthleteStartEvent extends Event {
    private final Athlete athlete;

    public AthleteStartEvent(int time, Athlete athlete) {
        super(time);
        this.athlete = athlete;
    }

    @Override
    public void execute(Simulation simulation) {
        athlete.setCurrentNode(athlete.getStartNode());

        if (getTime() < Simulation.DECISION_END_TIME) {
            athlete.chooseNextActivity(simulation, getTime());
        }
    }
}