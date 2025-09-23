package org.simulation.Action;

import org.entity.*;
import org.map.Location;
import org.map.WorldMap;
import org.simulation.TurnAction;

import java.util.*;

public class MoveCreatures implements TurnAction {
    @Override
    public void update(WorldMap map) {
        List<MoveResult> moveResults = new ArrayList<>();
        for (Map.Entry<Location, Entity> entry : map.getCells().entrySet()) {
            Location oldLocation = entry.getKey();
            Entity entity = entry.getValue();
            if (entity.getClass() == Herbivore.class || entity.getClass() == Predator.class) {
                Location newLocation = ((Creature) entity).makeMove(map, oldLocation);
                moveResults.add(new MoveResult(entity, oldLocation, newLocation));
            }
        }

        Set<Location> occupied = new HashSet<>();

        for (MoveResult result : moveResults) {
            Entity currOccupant = map.getEntityByLocation(result.newLocation);

            if (occupied.contains(result.newLocation)) {
                stay(map, result);
            } else if (currOccupant == null) {
                move(map, result, occupied);
            } else if (hasPredatorKilled(result, currOccupant)) {
                eatHerbivore(map, result, (Predator) result.entity);
            } else if (hasHerbivoreEaten(result, currOccupant)) {
                eatGrass(map, result, (Herbivore) result.entity);
            }
        }
    }

    private void stay(WorldMap map, MoveResult result) {
        map.placeEntity(result.oldLocation, result.entity);
    }

    private void relocate(WorldMap map, MoveResult result) {
        map.removeEntity(result.oldLocation);
        map.placeEntity(result.newLocation, result.entity);
    }

    private void move(WorldMap map, MoveResult result, Set<Location> occupied) {
        relocate(map, result);
        occupied.add(result.newLocation);
    }

    private boolean hasPredatorKilled(MoveResult result, Entity occupant) {
        return result.entity instanceof Predator predator
                && predator.getKilledEntities() > 0
                && occupant instanceof Herbivore;
    }

    private void eatHerbivore(WorldMap map, MoveResult result, Predator predator) {
        relocate(map, result);
        predator.resetKilledEntities();
    }

    private boolean hasHerbivoreEaten(MoveResult result, Entity occupant) {
        return result.entity instanceof Herbivore herbivore
                && herbivore.getConsumedGrass() > 0
                && occupant instanceof Grass;
    }

    private void eatGrass(WorldMap map, MoveResult result, Herbivore herbivore) {
        relocate(map, result);
        herbivore.resetConsumedGrass();
    }

    record MoveResult(Entity entity, Location oldLocation, Location newLocation) {

    }
}
