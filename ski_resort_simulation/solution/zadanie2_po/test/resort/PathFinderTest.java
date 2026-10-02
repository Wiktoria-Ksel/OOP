package resort;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import resort.model.*;
import resort.simulation.*;
import java.util.List;
import java.util.Map;

public class PathFinderTest {
    private Node n0, n1, n2, n3, n4, n5;
    private Lift l0_1, l2_4, l2_3, l4_5;
    private Route r1_2, r1_0, r2_0, r3_4, r5_3_1, r5_3_2, r3_1;
    private Route dummy_r1, dummy_r2, dummy_r3, dummy_r4;
    private ResortData data;

    @BeforeEach
    public void setUp() {
        n0 = new Node(0, 100, 0, 0, true);
        n1 = new Node(1, 200, 1, 1, true);
        n2 = new Node(2, 300, 2, 2, true);
        n3 = new Node(3, 400, 3, 3, true);
        n4 = new Node(4, 500, 4, 4, true);
        n5 = new Node(5, 600, 5, 5, true);

        l0_1 = new Lift(0, n0, n1, 10, 4, 240); // 0-1
        l2_4 = new Lift(2, n2, n4, 10, 4, 240); // 2-4
        l2_3 = new Lift(7, n2, n3, 10, 4, 240); // 2-3
        l4_5 = new Lift(10, n4, n5, 10, 4, 240); // 4-5

        r1_2 = new Route(1, n1, n2, 3, 10, 0.5, 0.9);   // 1-2
        r1_0 = new Route(3, n1, n0, 3, 10, 0.5, 0.9);   // 1-0
        r2_0 = new Route(4, n2, n0, 3, 10, 0.5, 0.9);   // 2-0
        r3_4 = new Route(5, n3, n4, 3, 10, 0.5, 0.9);   // 3-4
        r5_3_1 = new Route(6, n5, n3, 3, 10, 0.5, 0.9); // 5-3 (pierwsza trasa równoległa)
        r5_3_2 = new Route(8, n5, n3, 3, 10, 0.5, 0.9); // 5-3 (druga trasa równoległa)
        r3_1 = new Route(9, n3, n1, 3, 10, 0.5, 0.9);   // 3-1 (bezpośrednie połączenie)

        // Helper routes (dummy) as BFS goals.
        Node dummyDestination = new Node(99, 0, 0, 0, false);
        dummy_r1 = new Route(101, n1, dummyDestination, 3, 10, 0.5, 0.9);
        dummy_r2 = new Route(102, n2, dummyDestination, 3, 10, 0.5, 0.9);
        dummy_r3 = new Route(103, n3, dummyDestination, 3, 10, 0.5, 0.9);
        dummy_r4 = new Route(104, n4, dummyDestination, 3, 10, 0.5, 0.9);

        n0.initRoutes(0);
        n0.initLifts(1); n0.addLift(l0_1);

        n1.initRoutes(3); n1.addRoute(r1_2); n1.addRoute(r1_0); n1.addRoute(dummy_r1);
        n1.initLifts(0);

        n2.initRoutes(2); n2.addRoute(r2_0); n2.addRoute(dummy_r2);
        n2.initLifts(2); n2.addLift(l2_4); n2.addLift(l2_3);

        n3.initRoutes(3); n3.addRoute(r3_4); n3.addRoute(r3_1); n3.addRoute(dummy_r3);
        n3.initLifts(0);

        n4.initRoutes(1); n4.addRoute(dummy_r4);
        n4.initLifts(1); n4.addLift(l4_5);

        n5.initRoutes(2); n5.addRoute(r5_3_1); n5.addRoute(r5_3_2);
        n5.initLifts(0);

        Node[] nodes = {n0, n1, n2, n3, n4, n5, dummyDestination};
        Lift[] lifts = {l0_1, l2_4, l4_5, l2_3};
        Route[] routes = {r1_2, r1_0, r2_0, r3_4, r5_3_1, r5_3_2, r3_1, dummy_r1, dummy_r2, dummy_r3, dummy_r4};
        Athlete[] athletes = {};

        data = new ResortData(nodes, lifts, routes, athletes);
    }

    /**
     * Scenario 1:
     * Path from vertex 0 to 4: leading through 0, 1, 2, 4; distance 3.
     */
    @Test
    public void testPathFrom0To4() {
        Map<Node, Integer> distances = PathFinder.computeDistances(n0, data);
        Assertions.assertTrue(distances.containsKey(n4));
        Assertions.assertEquals(3, distances.get(n4));

        List<Connection> path = PathFinder.findPath(n0, dummy_r4, data);
        Assertions.assertNotNull(path);
        // 3 steps to n4 + dummy route.
        Assertions.assertEquals(4, path.size());
        Assertions.assertEquals(l0_1, path.get(0));
        Assertions.assertEquals(r1_2, path.get(1));
        Assertions.assertEquals(l2_4, path.get(2));
        Assertions.assertEquals(dummy_r4, path.get(3));
    }

    /**
     * Scenario 2:
     * Direct path from 3 to 1.
     */
    @Test
    public void testPathFrom3To1() {
        Map<Node, Integer> distances = PathFinder.computeDistances(n3, data);
        Assertions.assertTrue(distances.containsKey(n1));
        Assertions.assertEquals(1, distances.get(n1));

        List<Connection> path = PathFinder.findPath(n3, dummy_r1, data);
        Assertions.assertNotNull(path);
        // 1 step to n1 and 1 dummy route.
        Assertions.assertEquals(2, path.size());
        Assertions.assertEquals(r3_1, path.get(0));
        Assertions.assertEquals(dummy_r1, path.get(1));
    }

    /**
     * Scenario 3:
     * Empty path from 2 to itself (distance 0).
     */
    @Test
    public void testPathFrom2To2() {
        Map<Node, Integer> distances = PathFinder.computeDistances(n2, data);
        Assertions.assertTrue(distances.containsKey(n2));
        Assertions.assertEquals(0, distances.get(n2));

        List<Connection> path = PathFinder.findPath(n2, dummy_r2, data);
        Assertions.assertNotNull(path);
        Assertions.assertEquals(1, path.size());
        Assertions.assertEquals(dummy_r2, path.get(0));
    }

    /**
     * Scenario 4:
     * Path from 4 to 3: the algorithm selects one of two possible paths of length 2.
     */
    @Test
    public void testPathFrom4To3ChooseOneOfTwo() {
        Map<Node, Integer> distances = PathFinder.computeDistances(n4, data);
        Assertions.assertTrue(distances.containsKey(n3));
        Assertions.assertEquals(2, distances.get(n3));

        List<Connection> path = PathFinder.findPath(n4, dummy_r3, data);
        Assertions.assertNotNull(path);
        // 1 step to n3 and 1 dummy route.
        Assertions.assertEquals(3, path.size());
        Assertions.assertEquals(dummy_r3, path.get(2));

        boolean isPathA = (path.get(0) == l4_5 && path.get(1) == r5_3_1);
        boolean isPathB = (path.get(0) == l4_5 && path.get(1) == r5_3_2);
        Assertions.assertTrue(isPathA || isPathB, "Chosen route should be one of two of length 2");
    }
}