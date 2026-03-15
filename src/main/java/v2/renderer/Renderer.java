package v2.renderer;

import v2.entity.*;
import v2.map.Location;
import v2.map.WorldMap;

import java.util.Optional;

public class Renderer {
    private static final String EMPTY_CELL = " . ";
    private static final String ROCK_CELL = " R ";
    private static final String TREE_CELL = " T ";
    private static final String GRASS_CELL = " G ";
    private static final String HERBIVORE_CELL = " H ";
    private static final String PREDATOR_CELL = " P ";

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
                    System.out.print(EMPTY_CELL);
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
            drawing = ROCK_CELL;
        } else if (entity instanceof Tree) {
            drawing = TREE_CELL;
        } else if (entity instanceof Grass) {
            drawing = GRASS_CELL;
        } else if (entity instanceof Herbivore) {
            drawing = HERBIVORE_CELL;
        } else if (entity instanceof Predator) {
            drawing = PREDATOR_CELL;
        }
        return drawing;
    }
}

