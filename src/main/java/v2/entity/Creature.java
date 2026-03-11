package v2.entity;

import v2.map.Location;
import v2.map.WorldMap;
import v2.path.PathFinder;

import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class Creature extends Entity {
    private int speed;
    private int hp;

    List<Location> listOfClosestLocations;

    public void makeMove(WorldMap map, Location oldLocation, PathFinder pathFinder) {
        List<Location> steps = pathFinder.findClosestPath(map, oldLocation, isGoal());
        Location nextLocation;
        if (!steps.isEmpty() && steps.size() >= 2) {
            nextLocation = steps.get(1);
        } else {
            nextLocation = whereToStep(map, oldLocation); //toDo actually make predetermined move here ?
        }

        if (oldLocation.equals(nextLocation)) {
            return;
        }
        checkLocations(map, oldLocation);
        move(map, nextLocation, oldLocation, isGoal());

        //map.removeEntity(oldLocation);
 /*       boolean hasPut = map.tryAddEntity(nextLocation, this);
        if (!hasPut) {
            map.tryAddEntity(oldLocation, this);
        }*/
    }

    void move(WorldMap map, Location nextLocation, Location oldLocation,  Predicate<Entity> isGoal) {
        boolean isCellFree = map.isCellFree(nextLocation);
        if (isCellFree) {
            map.removeEntity(oldLocation);
            map.tryAddEntity(oldLocation, this);
        } else if (!isCellFree && map.getEntity(nextLocation).isPresent() && isGoal.test(map.getEntity(nextLocation).get())) {
            map.removeEntity(oldLocation); //съели траву//травоядное - интерктФизТаргет вместо простого ремува
            map.removeEntity(nextLocation);
            map.tryAddEntity(oldLocation, this);
        } else if (!isCellFree && map.getEntity(nextLocation).isPresent() && !isGoal.test(map.getEntity(nextLocation).get())) {
            nextLocation = oldLocation;
        }
    }

    private void checkLocations(WorldMap map, Location oldLocation) {
        Optional<Entity> entity = map.getEntity(oldLocation);
        if (!(entity.isPresent() && entity.get().equals(this))) {
            throw new IllegalStateException("the creaturecreature is expected at " + oldLocation);
        }
    }

    private Location whereToStep(WorldMap map, Location location) {
        listOfClosestLocations = location.neighbourLocations();
        for (Location nearby : listOfClosestLocations) {
            if (map.isInsideMap(nearby) && map.isCellFree(nearby)) {
                return nearby;
            }
        }
        return location;
    }

    Predicate<Entity> isGoal() {
        return entity -> false;
    }
}
