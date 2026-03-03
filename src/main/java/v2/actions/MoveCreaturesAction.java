package v2.actions;

import v2.entity.Creature;
import v2.entity.Entity;
import v2.entity.EntityType;
import v2.map.Location;
import v2.map.WorldMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MoveCreaturesAction implements Action {

    @Override
    public void execute(WorldMap map) {
        Map<Creature, Location> creatureLocationMap = fillCurrentMapSnapshot(map);
        for (Map.Entry<Creature, Location> creatureLocationEntry : creatureLocationMap.entrySet()) {
            creatureLocationEntry.getKey().makeMove(map, creatureLocationEntry.getValue());
        }
    }

    private Map<Creature, Location> fillCurrentMapSnapshot(WorldMap map) {
        Map<Creature, Location> creaturesLocation = new HashMap<>();
        Location location;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                location = new Location(x, y);
                Optional<Entity> nullableEntity = map.getEntity(location);
                if (nullableEntity.isPresent() && nullableEntity.get() instanceof Creature) {
                    creaturesLocation.put((Creature) nullableEntity.get(), location);
                    //toDo подумать, нужно ли различать конкретные типы существа ? (логика раазная или ?)
                }
            }
        }
        return creaturesLocation;
    }
}
