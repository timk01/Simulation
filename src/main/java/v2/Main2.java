package v2;

import v2.actions.Action;
import v2.actions.MoveCreaturesAction;
import v2.actions.PopulateMapAction;
import v2.map.WorldMap;
import v2.renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public class Main2 {
    public static void main(String[] args) {
        WorldMap worldMap = new WorldMap(10, 10);
        Renderer renderer = new Renderer(worldMap);

        List<Action> actions = List.of(new PopulateMapAction(), new MoveCreaturesAction());
        actions.get(0).execute(worldMap);
        actions.get(1).execute(worldMap);
/*        for (Action action : actions) {
            action.execute(worldMap);
        }*/


        for (int i = 0; i < 10; i++) {
            try {
                actions.get(1).execute(worldMap);
                System.out.println();
                renderer.draw();
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted!");
                Thread.currentThread().interrupt();
            }
        }
    }
}
