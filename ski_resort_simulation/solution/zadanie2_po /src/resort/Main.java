package resort;

import resort.input.InputReader;
import kadra.pliki.WyjatekSystemuPlikow;
import resort.model.ResortData;
import resort.simulation.EventQueue;
import resort.simulation.PriorityEventQueue;
import resort.simulation.Simulation;
import resort.visualization.MapGeneratorService;

public class Main {
  public static void main(String[] args) {
    if (args.length == 0) {
      System.err.println("Error: The path to the map directory was not provided as a program argument.");
      return;
    }
    String directoryPath = args[0];

    try {
      InputReader reader = new InputReader();
      ResortData data = reader.read();

      EventQueue eventQueue = new PriorityEventQueue();
      Simulation simulation = new Simulation(data, eventQueue);

      simulation.initialize();
      simulation.run();

      // Generowate maps after simulation.
      MapGeneratorService mapService = new MapGeneratorService(data, directoryPath);
      mapService.generateAllMaps();

    } catch (Exception e) {
      if (e.getCause() instanceof WyjatekSystemuPlikow || e instanceof WyjatekSystemuPlikow) {
        System.err.println("Error: A file system problem occurred while generating maps.");
        System.err.println("Ensure that the provided directory path is correct and that the program has write permissions for the selected location.");
      } else {
        System.err.println("An unexpected error occurred while the program was running: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }
}