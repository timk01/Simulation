package v2.renderer;

import v2.entity.*;
import v2.map.Location;
import v2.map.WorldMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Renderer {

    private final WorldMap map;
    public Renderer(WorldMap map) {
        this.map = map;
    }

    public void draw() {
        Location location;
        for (int x = 0; x < map.getHeight(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                location = new Location(x, y);
                Entity entity = map.getEntity(location);
                if (entity == null) {
                    System.out.print(" . ");
                } else {
                    System.out.print(drawEntity(entity));
                }
            }
            System.out.println();
        }
    }

    private String drawEntity(Entity entity) {
        String drawing = "";
        if (entity instanceof Rock) {
            drawing = " R ";
        } else if (entity instanceof Tree) {
            drawing = " T ";
        } else if (entity instanceof Grass) {
            drawing = " G ";
        } else if (entity instanceof Herbivore) {
            drawing = " H ";
        } else if (entity instanceof Predator) {
            drawing = " P ";
        }
        return drawing;
    }
}

