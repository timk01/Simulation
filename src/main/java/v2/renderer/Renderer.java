package v2.renderer;

import v2.entity.*;
import v2.map.Location;
import v2.map.WorldMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Renderer {

    private final WorldMap map;
    public Renderer(WorldMap map) {
        this.map = map;
    }

    public void draw() {
        Location location;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                location = new Location(x, y);
                Optional<Entity> entity = map.getEntity(location);
                if (entity.isEmpty()) {
                    System.out.print(" . ");
                } else {
                    System.out.print(drawEntity(entity.get()));
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

