package resort.input;

import resort.model.*;
import java.util.Locale;
import java.util.Scanner;

public class InputReader {
    private static final int OPENING_HOUR = 9;
    private static final int SECONDS_IN_HOUR = 3600;
    private static final int SECONDS_IN_MINUTE = 60;
    private Scanner input;

    public ResortData read() {
        input = new Scanner(System.in);

        Node[] nodes = readNodes();
        skipSeparatingLine();

        Lift[] lifts = readLifts(nodes);
        skipSeparatingLine();

        Route[] routes = readRoutes(nodes);
        skipSeparatingLine();

        Athlete[] athletes = readAthletes(nodes, routes.length);

        connectGraph(nodes, routes, lifts);

        return new ResortData(nodes, lifts, routes, athletes);
    }

    private void connectGraph(Node[] nodes, Route[] routes, Lift[] lifts) {
        int[] routeCounts = new int[nodes.length];
        int[] liftCounts = new int[nodes.length];

        for (Route route : routes) {
            routeCounts[route.getFrom().getId()]++;
        }
        for (Lift lift : lifts) {
            liftCounts[lift.getFrom().getId()]++;
        }
        for (Node node : nodes) {
            node.initRoutes(routeCounts[node.getId()]);
            node.initLifts(liftCounts[node.getId()]);
        }
        for (Route route : routes) {
            route.getFrom().addRoute(route);
        }
        for (Lift lift : lifts) {
            lift.getFrom().addLift(lift);
        }
    }

    private Node[] readNodes() {
        int count = Integer.parseInt(input.nextLine());
        Node[] nodes = new Node[count];
        for (int i = 0; i < count; i++) {
            String line = input.nextLine();
            Scanner lineScanner = scannerForLine(line);
            int height = lineScanner.nextInt();
            int x = lineScanner.nextInt();
            int y = lineScanner.nextInt();

            boolean connected = false;
            if (lineScanner.hasNext()) {
                String marker = lineScanner.next();
                connected = marker.equals("s");
            }
            nodes[i] = new Node(i, height, x, y, connected);
        }
        return nodes;
    }

    private Lift[] readLifts(Node[] nodes) {
        int count = Integer.parseInt(input.nextLine());
        Lift[] lifts = new Lift[count];
        for (int i = 0; i < count; i++) {
            String line = input.nextLine();
            Scanner lineScanner = scannerForLine(line);
            int fromId = lineScanner.nextInt();
            int toId = lineScanner.nextInt();
            int interval = lineScanner.nextInt();
            int capacity = lineScanner.nextInt();
            int travelTime = lineScanner.nextInt();

            lifts[i] = new Lift(i, nodes[fromId], nodes[toId], interval, capacity, travelTime);
        }
        return lifts;
    }

    private Route[] readRoutes(Node[] nodes) {
        int count = Integer.parseInt(input.nextLine());
        Route[] routes = new Route[count];
        for (int i = 0; i < count; i++) {
            String line = input.nextLine();
            Scanner lineScanner = scannerForLine(line);
            int fromId = lineScanner.nextInt();
            int toId = lineScanner.nextInt();
            int difficulty = lineScanner.nextInt();
            int travelTime = lineScanner.nextInt();
            double baseAttractiveness = lineScanner.nextDouble();
            double resistance = lineScanner.nextDouble();

            routes[i] = new Route(i, nodes[fromId], nodes[toId], difficulty, travelTime, baseAttractiveness, resistance);
        }
        return routes;
    }

    private Athlete[] readAthletes(Node[] nodes, int numRoutes) {
        int groupCount = Integer.parseInt(input.nextLine());
        Athlete[] athletes = new Athlete[1];
        int athleteCount = 0;

        for (int groupId = 0; groupId < groupCount; groupId++) {
            AthleteGroupData groupData = readAthleteGroupData();
            for (int i = 0; i < groupData.groupSize; i++) {
                if (athleteCount == athletes.length) {
                    athletes = growAthleteArray(athletes);
                }
                athletes[athleteCount] = createAthlete(athleteCount, groupData, nodes, i, numRoutes);
                athleteCount++;
            }
        }
        return trimAthleteArray(athletes, athleteCount);
    }

    private AthleteGroupData readAthleteGroupData() {
        Scanner firstScanner = scannerForLine(input.nextLine());

        int groupSize = firstScanner.nextInt();
        int skillLevel = firstScanner.nextInt();
        double spontaneity = firstScanner.nextDouble();
        double beta = firstScanner.nextDouble();
        String typeStr = firstScanner.next();
        char type = typeStr.charAt(0);
        boolean tracked = firstScanner.hasNext() && firstScanner.next().equals("s");

        Scanner secondScanner = scannerForLine(input.nextLine());
        double difficultyWeight = secondScanner.nextDouble();
        double surfaceWeight = secondScanner.nextDouble();
        double boredomWeight = secondScanner.nextDouble();

        Scanner thirdScanner = scannerForLine(input.nextLine());
        int startNodeId = thirdScanner.nextInt();
        String startTimeText = thirdScanner.next();
        int startInterval = 0;
        if (thirdScanner.hasNextInt()) {
            startInterval = thirdScanner.nextInt();
        }

        return new AthleteGroupData(groupSize, skillLevel, spontaneity, beta, type, tracked,
                difficultyWeight, surfaceWeight, boredomWeight, startNodeId, parseTime(startTimeText), startInterval);
    }

    private Athlete createAthlete(int athleteId, AthleteGroupData groupData, Node[] nodes, int indexInGroup, int numRoutes) {
        int startTime = groupData.firstStartTime + indexInGroup * groupData.startInterval;

        return AthleteFactory.createAthlete(
                groupData.type,
                athleteId,
                groupData.skillLevel,
                groupData.spontaneity,
                groupData.beta,
                groupData.difficultyWeight,
                groupData.surfaceWeight,
                groupData.boredomWeight,
                groupData.tracked,
                nodes[groupData.startNodeId],
                startTime,
                numRoutes
        );
    }

    private Athlete[] growAthleteArray(Athlete[] oldArray) {
        Athlete[] newArray = new Athlete[oldArray.length * 2];
        System.arraycopy(oldArray, 0, newArray, 0, oldArray.length);
        return newArray;
    }

    private Athlete[] trimAthleteArray(Athlete[] oldArray, int size) {
        Athlete[] newArray = new Athlete[size];
        System.arraycopy(oldArray, 0, newArray, 0, size);
        return newArray;
    }

    private void skipSeparatingLine() {
        if (input.hasNextLine()) {
            input.nextLine();
        }
    }

    private Scanner scannerForLine(String line) {
        Scanner scanner = new Scanner(line);
        scanner.useLocale(Locale.ENGLISH);
        return scanner;
    }

    private int parseTime(String time) {
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return hours * SECONDS_IN_HOUR + minutes * SECONDS_IN_MINUTE + seconds - OPENING_HOUR * SECONDS_IN_HOUR;
    }

    private static class AthleteGroupData {
        final int groupSize;
        final int skillLevel;
        final double spontaneity;
        final double beta;
        final char type;
        final boolean tracked;
        final double difficultyWeight;
        final double surfaceWeight;
        final double boredomWeight;
        final int startNodeId;
        final int firstStartTime;
        final int startInterval;

        AthleteGroupData(int groupSize, int skillLevel, double spontaneity, double beta, char type, boolean tracked,
                         double difficultyWeight, double surfaceWeight, double boredomWeight, int startNodeId,
                         int firstStartTime, int startInterval) {
            this.groupSize = groupSize;
            this.skillLevel = skillLevel;
            this.spontaneity = spontaneity;
            this.beta = beta;
            this.type = type;
            this.tracked = tracked;
            this.difficultyWeight = difficultyWeight;
            this.surfaceWeight = surfaceWeight;
            this.boredomWeight = boredomWeight;
            this.startNodeId = startNodeId;
            this.firstStartTime = firstStartTime;
            this.startInterval = startInterval;
        }
    }
}