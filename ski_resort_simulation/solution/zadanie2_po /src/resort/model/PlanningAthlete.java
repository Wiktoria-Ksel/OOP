package resort.model;

import resort.simulation.Simulation;
import resort.simulation.PathFinder;
import java.util.LinkedList;
import java.util.List;

public abstract class PlanningAthlete extends Athlete {
    protected final LinkedList<Connection> currentPlan = new LinkedList<>();

    public PlanningAthlete(
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
        if (!currentPlan.isEmpty()) {
            executeNextPlanStep(simulation, time);
            return;
        }

        if (simulation.getRandom().nextDouble() < spontaneity) {
            chooseRandomActivity(simulation, time);
        } else {
            Route targetRoute = selectStrategicTarget(simulation);
            if (targetRoute != null) {
                List<Connection> path = PathFinder.findPath(currentNode, targetRoute, simulation.getData());
                currentPlan.addAll(path);
            }

            if (!currentPlan.isEmpty()) {
                executeNextPlanStep(simulation, time);
            } else {
                chooseRandomActivity(simulation, time);
            }
        }
    }

    private void executeNextPlanStep(Simulation simulation, int time) {
        Connection next = currentPlan.poll();
        if (next != null) {
            next.traverse(this, simulation, time);
        }
    }

    protected abstract Route selectStrategicTarget(Simulation simulation);
}