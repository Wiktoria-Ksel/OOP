package resort.model;

public class AthleteFactory {
    public static Athlete createAthlete(
            char type,
            int athleteId,
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
        switch (type) {
            case 'Z':
                return new GreedyAthlete(athleteId, skillLevel, spontaneity, beta,
                        difficultyWeight, surfaceWeight, boredomWeight, tracked, startNode, startTime, numRoutes);
            case 'K':
                return new CollectorAthlete(athleteId, skillLevel, spontaneity, beta,
                        difficultyWeight, surfaceWeight, boredomWeight, tracked, startNode, startTime, numRoutes);
            case 'L':
            default:
                return new LocalAthlete(athleteId, skillLevel, spontaneity, beta,
                        difficultyWeight, surfaceWeight, boredomWeight, tracked, startNode, startTime, numRoutes);
        }
    }
}