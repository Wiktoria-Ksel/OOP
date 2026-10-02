package resort.model;

import resort.simulation.Simulation;

public class LocalAthlete extends Athlete {
    public LocalAthlete(
            int id,
            int skillLevel,
            double spontaneity,
            double beta,
            double difficultyWeight,
            double surfaceWeight,
            double boredomWeight,
            boolean tracked,
            Node startNode,
            int startTime,
            int numRoutes
    ) {
        super(id, skillLevel, spontaneity, beta, difficultyWeight, surfaceWeight, boredomWeight, tracked, startNode, startTime, numRoutes);
    }

    @Override
    protected void planNextActivity(Simulation simulation, int time) {
        if (simulation.getRandom().nextDouble() < spontaneity) {
            chooseRandomActivity(simulation, time);
        } else {
            chooseBestActivity(simulation, time);
        }
    }
}