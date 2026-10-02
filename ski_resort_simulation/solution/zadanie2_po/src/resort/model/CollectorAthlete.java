package resort.model;

import resort.simulation.Simulation;
import resort.simulation.PathFinder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectorAthlete extends PlanningAthlete {
    public CollectorAthlete(
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
        Map<Node, Integer> distances = PathFinder.computeDistances(currentNode, simulation.getData());

        List<Route> routes = new ArrayList<>(List.of(simulation.getData().getRoutes()));

        routes.sort((r1, r2) -> {
            int rides1 = athleteRideCounts[r1.getId()];
            int rides2 = athleteRideCounts[r2.getId()];
            if (rides1 != rides2) {
                return Integer.compare(rides1, rides2);
            }

            int dist1 = distances.get(r1.getFrom()) + 1;
            int dist2 = distances.get(r2.getFrom()) + 1;
            if (dist1 != dist2) {
                return Integer.compare(dist1, dist2);
            }

            double att1 = r1.attractivenessFor(this);
            double att2 = r2.attractivenessFor(this);
            return Double.compare(att2, att1);
        });

        return routes.get(0);
    }
}