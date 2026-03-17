package simulation.actions;

import simulation.entity.Creature;
import simulation.entity.Entity;
import simulation.map.Location;
import simulation.map.WorldMap;
import simulation.path.PathFinder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MoveCreaturesAction implements Action {
    private final PathFinder pathFinder;

    public MoveCreaturesAction(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    @Override
    public void execute(WorldMap worldMap) {
        Map<Creature, Location> creatureLocationMap = fillCurrentMapSnapshot(worldMap);
        for (Map.Entry<Creature, Location> creatureLocationEntry : creatureLocationMap.entrySet()) {
            if (isLocationMissed(worldMap, creatureLocationEntry)) {
                continue;
            }
            creatureLocationEntry.getKey().makeMove(worldMap, creatureLocationEntry.getValue(), pathFinder);
        }
    }

    private Map<Creature, Location> fillCurrentMapSnapshot(WorldMap worldMap) {
        Map<Creature, Location> creaturesLocation = new HashMap<>();
        Location location;
        for (int y = 0; y < worldMap.getHeight(); y++) {
            for (int x = 0; x < worldMap.getWidth(); x++) {
                location = new Location(x, y);
                Optional<Entity> nullableEntity = worldMap.getEntity(location);
                if (nullableEntity.isPresent() && nullableEntity.get() instanceof Creature) {
                    creaturesLocation.put((Creature) nullableEntity.get(), location);
                }
            }
        }
        return creaturesLocation;
    }

    private boolean isLocationMissed(WorldMap worldMap, Map.Entry<Creature, Location> creatureLocationEntry) {
        Creature currentCreature = creatureLocationEntry.getKey();
        Location oldLocation = creatureLocationEntry.getValue();
        Optional<Entity> entityBeforeMoving = worldMap.getEntity(oldLocation);
        return entityBeforeMoving.map(entity -> !entity.equals(currentCreature)).orElse(true);
    }
}
