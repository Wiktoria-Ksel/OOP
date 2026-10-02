package resort.model;

import resort.simulation.Simulation;

public class GreedyAthlete extends PlanningAthlete {
    public GreedyAthlete(
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
    protected Route selectStrategicTarget(Simulation simulation) {
        Route targetRoute = null;
        double maxAttr = -1.0;
        for (Route r : simulation.getData().getRoutes()) {
            double attr = r.attractivenessFor(this);
            if (attr > maxAttr) {
                maxAttr = attr;
                targetRoute = r;
            }
        }
        return targetRoute;
    }
}