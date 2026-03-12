package v2;

import v2.actions.*;
import v2.entity.*;
import v2.map.Location;
import v2.map.WorldMap;
import v2.path.PathFinder;
import v2.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public class Main2 {
    public static void main(String[] args) {
        WorldMap worldMap = new WorldMap(10, 10);
        Renderer renderer = new Renderer(worldMap);
        PathFinder pathFinder = new PathFinder();
        EntityFactory entityFactory = new EntityFactory();
        ActionHelper actionHelper = new ActionHelper(entityFactory);


        List<Action> initActions = List.of(
                new PopulateMapAction(actionHelper));

        List<Action> turnActions = List.of(
                new MoveCreaturesAction(pathFinder),
                new KeepPopulationStableAction(actionHelper));

        initActions.get(0).execute(worldMap);

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
        renderer.draw();


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
        }
    }
}
