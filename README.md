# Object-Oriented Programming

## Ski Resort Simulation — Java

A discrete-event simulation built around object-oriented modelling: interfaces, abstract classes, encapsulated state and polymorphic behaviour.

- **Domain model:** an `Athlete` hierarchy represents different decision-making strategies; a `Connection` interface abstracts movement through the resort graph.
- **Simulation engine:** event subclasses implement their own actions, while an `EventQueue` interface allows priority-queue and sorted-array implementations to be used interchangeably.
- **Algorithms and data structures:** breadth-first search for route planning, FIFO lift queues and chronological event scheduling.
- **Reporting:** queue and lift-utilisation statistics, plus LaTeX maps of the network and tracked participants' histories.
- **Tests:** JUnit 5 tests cover lift capacity and queue behaviour, as well as path finding and graph connectivity.

**Tools:** Java 21, Java collections, JUnit 5 and course-provided LaTeX map-generation utilities.

## Repository structure

| Directory | Contents |
| --- | --- |
| [src/resort](ski_resort_simulation/solution/zadanie2_po%20/src/resort/) | model, input handling, queues, simulation engine and map-generator integration |
| [test/resort](ski_resort_simulation/solution/zadanie2_po%20/test/resort/) | JUnit tests for lifts and path finding |
| [src/kadra](ski_resort_simulation/solution/zadanie2_po%20/src/kadra/) | map-generation utilities supplied with the assignment |
| [statements](ski_resort_simulation/statements/) | original assignment PDFs, input-format documentation and example data |

## Build and run

Requirements: **JDK 21 or newer**. The application uses the standard library and the included map utilities. The commands below use a POSIX shell.

From the repository root:

```sh
cd "ski_resort_simulation/solution/zadanie2_po"
mkdir -p build/classes build/maps
find src -name '*.java' -print > build/sources.txt
javac -encoding UTF-8 -d build/classes @build/sources.txt

java -cp build/classes resort.Main build/maps \
  < ../../statements/examples/przykladowe-dane-2.txt
```

The application reads the simulation parameters from standard input. Its required argument specifies the output directory for maps. It prints simulation statistics and writes `parametry.tex`, `statystyki.tex` and history maps for tracked participants to `build/maps/`. Rendering these files as PDFs requires a separate LaTeX installation with the packages referenced in the generated files.

## Run the tests

First build the application as above. Download [JUnit Platform Console Standalone 1.10.2](https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar), which includes JUnit Jupiter 5.10.2, and set `JUNIT_JAR` to its absolute path.

From the same solution directory:

```sh
JUNIT_JAR=/absolute/path/to/junit-platform-console-standalone-1.10.2.jar
mkdir -p build/test-classes
find test -name '*.java' -print > build/test-sources.txt
javac -encoding UTF-8 -cp "build/classes:$JUNIT_JAR" \
  -d build/test-classes @build/test-sources.txt

java -jar "$JUNIT_JAR" execute \
  --class-path build/classes:build/test-classes \
  --scan-class-path --fail-if-no-tests
```

See the [JUnit Console Launcher documentation](https://docs.junit.org/5.10.2/user-guide/#running-tests-console-launcher) for additional options.

## Assignment materials

The original [assignment statements and examples](ski_resort_simulation/statements/) are in Polish. The `kadra` package contains supporting code supplied by the course; the simulation implementation is in `resort`.
