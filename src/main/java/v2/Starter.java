package v2;

public class Starter {
    public static void main(String[] args) {
        SimulationApp simulationApp = new SimulationApp();
        try {
            simulationApp.runSimulation();
        } catch (InterruptedException e) {
            System.err.println("An error occurred in main while running simulation: " + e.getMessage());
            e.printStackTrace();
        }
/*        WorldMap worldMap = new WorldMap(10, 10);
        Renderer renderer = new Renderer(worldMap);
        Controller controller = new Controller();
        Simulation simulation = new Simulation(worldMap, renderer, controller);
        Thread simThread = new Thread(simulation);
        simThread.start();*/
        //simulation.startSimulation();
/*        WorldMap worldMap = new WorldMap(10, 10);
        Renderer renderer = new Renderer(worldMap);
        PathFinder pathFinder = new PathFinder();
        EntityFactory entityFactory = new EntityFactory();
        ActionHelper actionHelper = new ActionHelper(entityFactory);


        List<Action> initActions = List.of(
                new PopulateMapAction(actionHelper));

        List<Action> turnActions = List.of(
                new MoveCreaturesAction(pathFinder),
                new KeepPopulationStableAction(actionHelper));

        initActions.get(0).execute(worldMap);*/

        //actions.get(0).execute(worldMap);
        //actions.get(1).execute(worldMap);
/*        for (Action action : actions) {
            action.execute(worldMap);
        }*/

/*        worldMap.tryAddEntity(new Location(2, 0), new Predator());
        worldMap.tryAddEntity(new Location(2, 1), new Herbivore());
        worldMap.tryAddEntity(new Location(2, 2), new Grass());
        worldMap.tryAddEntity(new Location(1, 2), new Rock());
        worldMap.tryAddEntity(new Location(2, 3), new Rock());
        worldMap.tryAddEntity(new Location(3, 2), new Rock());*/
        //worldMap.tryAddEntity(new Location(0, 9), new Grass());

        //actions.get(1).execute(worldMap);
/*        renderer.draw();


        for (int i = 0; i < 10; i++) {
            try {
                //actions.get(1).execute(worldMap);
                for (Action action : turnActions) {
                    action.execute(worldMap);
                }
                System.out.println();
                renderer.draw();
                Thread.sleep(300);
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted!");
                Thread.currentThread().interrupt();
            }
        }*/
    }
}
