package v2.actions;

import v2.entity.Entity;
import v2.entity.EntityFactory;
import v2.entity.EntityType;
import v2.entity.Tree;
import v2.map.Location;
import v2.map.WorldMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PopulateMapAction implements Action {

    private int ROCK_COUNTER = 2;
    private int TREE_COUNTER = 2;
    private int GRASS_COUNTER = 2;
    private int HERBIVORE_COUNTER = 2;
    private int PREDATOR_COUNTER = 2;

    record EntityPlan(EntityType entityType, int quantity) {

    }

    @Override
    public void execute(WorldMap map) {
        EntityFactory entityFactory = new EntityFactory();

        List<Location> emptyLocations = fillEmptyLocationsList(map);
        Collections.shuffle(emptyLocations);

        List<EntityPlan> planList = new ArrayList<>();
        for (EntityType entityType : EntityType.values()) {
            planList.add(new EntityPlan(entityType, 2));
            //toDo 2 - приходит из общего конфига как каунтер существ (максимальный)
        }

        for (EntityType entityType : EntityType.values()) {
            for (int i = 0; i < 2; i++) {
            Location location = emptyLocations.get(0);
            emptyLocations.remove(0);
            map.tryAddEntity(location, entityFactory.createEntity(entityType));
            }
        }
    }

    private List<Location> fillEmptyLocationsList(WorldMap map) {
        List<Location> freeLocations = new ArrayList<>();
        Location location;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                location = new Location(x, y);
                if (map.getEntity(location).isEmpty()) {
                    freeLocations.add(location);
                }
            }
        }
        return freeLocations;
    }
}
