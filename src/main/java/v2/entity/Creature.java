package v2.entity;

import v2.map.Location;
import v2.map.WorldMap;
import v2.path.PathFinder;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Creature extends Entity {
    private int speed;
    private int hp;

    List<Location> listOfClosestLocations;

    Location prevLocation;

    public void makeMove(WorldMap map, Location oldLocation, PathFinder pathFinder) {
        Predicate<Entity> goal = isGoal();
        List<Location> steps = pathFinder.findClosestPath(map, oldLocation, goal);
        Location nextLocation;
        if (!steps.isEmpty() && steps.size() >= 2) {
            nextLocation = steps.get(1);
        } else {
            nextLocation = whereToStep(map, oldLocation);
        }

        if (oldLocation.equals(nextLocation)) {
            return;
        }
        checkLocations(map, oldLocation);
        move(map, nextLocation, oldLocation, goal);
    }

    void move(WorldMap map, Location nextLocation, Location oldLocation, Predicate<Entity> isGoal) {
        boolean isCellFree = map.isCellFree(nextLocation);
        if (isCellFree) {
            map.removeEntity(oldLocation);
            map.tryAddEntity(nextLocation, this);
        } else if (map.getEntity(nextLocation).isPresent() && isGoal.test(map.getEntity(nextLocation).get())) {
            map.removeEntity(oldLocation); //съели траву//травоядное - интерктФизТаргет вместо простого ремува
            map.removeEntity(nextLocation);
            map.tryAddEntity(nextLocation, this);
        } else if (map.getEntity(nextLocation).isPresent() && !isGoal.test(map.getEntity(nextLocation).get())) {
            return;
        }
    }

    private void checkLocations(WorldMap map, Location oldLocation) {
        Optional<Entity> entity = map.getEntity(oldLocation);
        if (!(entity.isPresent() && entity.get().equals(this))) {
            throw new IllegalStateException("the creature is expected at " + oldLocation);
        }
    }

    private Location whereToStep(WorldMap map, Location oldLocation) {
        listOfClosestLocations = oldLocation.neighbourLocations();
        Collections.shuffle(listOfClosestLocations);
        List<Location> validMoves = makeValidMovesList(map);
        validMoves = filterValidMoves(validMoves);
        if (validMoves.isEmpty()) {
            return oldLocation;
        }
        Collections.shuffle(validMoves);
        prevLocation = oldLocation;
        return validMoves.get(0);
    }

    private List<Location> makeValidMovesList(WorldMap map) {
        List<Location> validMoves = new ArrayList<>();
        for (Location nearby : listOfClosestLocations) {
            if (map.isInsideMap(nearby) && map.isCellFree(nearby)) {
                validMoves.add(nearby);
            }
        }
        return validMoves;
    }

    private List<Location> filterValidMoves(List<Location> validMoves) {
        if (prevLocation != null && validMoves.size() > 1) {
            validMoves = validMoves.stream()
                    .filter(location1 -> !location1.equals(prevLocation)).collect(Collectors.toList());
        }
        return validMoves;
    }

    public Predicate<Entity> isGoal() {
        return entity -> false;
    }
}
