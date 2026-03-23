package simulation.renderer;

import simulation.entity.*;
import simulation.map.Location;
import simulation.map.WorldMap;

import java.util.Optional;

public class ConsoleRenderer implements Renderer {
    private static final String EMPTY_CELL = " . ";
    private static final String ROCK_CELL = " r ";
    private static final String TREE_CELL = " t ";
    private static final String GRASS_CELL = " g ";
    private static final String HERBIVORE_CELL = " H ";
    private static final String PREDATOR_CELL = " P ";

    private final WorldMap map;

    public ConsoleRenderer(WorldMap map) {
        this.map = map;
    }

    @Override
    public void draw() {
        Location location;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                location = new Location(x, y);
                Optional<Entity> entity = map.getEntity(location);
                if (entity.isEmpty()) {
                    System.out.print(EMPTY_CELL);
                } else {
                    System.out.print(toSprite(entity.get()));
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private String toSprite(Entity entity) {
        if (entity instanceof Rock) {
            return ROCK_CELL;
        }
        if (entity instanceof Tree) {
            return TREE_CELL;
        }
        if (entity instanceof Grass) {
            return GRASS_CELL;
        }
        if (entity instanceof Herbivore) {
            return HERBIVORE_CELL;
        }
        if (entity instanceof Predator) {
            return PREDATOR_CELL;
        }
        throw new IllegalStateException("illegal sprite is found while getting it");
    }
}

