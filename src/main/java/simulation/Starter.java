package simulation;

public class Starter {
    public static void main(String[] args) {
        SimulationApp simulationApp = new SimulationApp();
        try {
            simulationApp.runSimulation();
        } catch (InterruptedException e) {
            System.err.println("An error occurred in main while running simulation: " + e.getMessage());
        }
    }
}
