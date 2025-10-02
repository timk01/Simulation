package org.map.path;

import org.entity.*;
import org.map.Location;
import org.map.WorldMap;

import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;

public class PathFinder {
    // private static final Logger log = LoggerFactory.getLogger(PathFinder.class);

    private boolean isInsideMap(WorldMap map, Location location) {
        return location.x() >= 0 && location.x() < map.getWidth()
                && location.y() >= 0 && location.y() < map.getHeight();
    }

    public List<Location> generateNeighboursInsideMap(Location initialLocation, WorldMap map) {
        int x = initialLocation.x();
        int y = initialLocation.y();
        List<Location> neighbourLocations = new ArrayList<>();
        neighbourLocations.add(new Location(x + 1, y));
        neighbourLocations.add(new Location(x - 1, y));
        neighbourLocations.add(new Location(x, y + 1));
        neighbourLocations.add(new Location(x, y - 1));
        List<Location> locationListInsideMap = new ArrayList<>();
        for (Location neighbourLocation : neighbourLocations) {
            if (isInsideMap(map, neighbourLocation)) {
                locationListInsideMap.add(neighbourLocation);
            }
        }
        return locationListInsideMap;
    }

    public boolean isGrass(Entity target) {
        return target instanceof Grass;
    }

    public boolean isHardObstacle(Entity target) {
        return target instanceof Rock
                || target instanceof Tree;
    }

    public boolean isAlive(Entity target) {
        return target instanceof Herbivore
                || target instanceof Predator;
    }

    public boolean isPassableForReach(Location initialLocation, Location wantedLocation, WorldMap map) {
        if (initialLocation.equals(wantedLocation)) {
            return true;
        }
        Entity entity = map.getEntityByLocation(wantedLocation);
        return entity == null || !(isHardObstacle(entity) || isAlive(entity));
    }

    public boolean isPassableForGoThrough(Creature creature, Location wantedLocation, WorldMap map) {
        Entity entityOnWantedLocation = map.getEntityByLocation(wantedLocation);

        boolean empty = (entityOnWantedLocation == null);
        boolean hard = isHardObstacle(entityOnWantedLocation);
        boolean living = isAlive(entityOnWantedLocation);
        boolean grass = isGrass(entityOnWantedLocation);

        if (creature instanceof Herbivore) {
            return empty || grass || (!hard && !living);
        }
        return empty || (!grass && !hard && !living);
    }

    public MapAndGoal findClosestPath(WorldMap map,
                                      Location originalLocation, Creature originalCreature,
                                      Predicate<Entity> isTypeOf) {


        Entity originalMovingCreature = map.getEntityByLocation(originalLocation);

        if (originalMovingCreature == null) {
            // TODO: вруби уже логи, дядя! (сейчас оба валятся)
            // log.debug("Planning from {}: map has null; proceeding with provided start", originalLocation);
        }
        if (originalMovingCreature != originalCreature) {
            // TODO: вруби уже логи, дядя! (сейчас оба валятся)
            // log.debug("Planning from {}: map has {} (expected {}); proceeding",
            //           originalLocation, current, originalCreature);
        }

        if (isTypeOf.test(originalMovingCreature)) {
            return new MapAndGoal(Map.of(originalLocation, null), originalLocation);
        }

        Set<Location> visited = new HashSet<>();
        Queue<Location> queue = new ArrayDeque<>();
        Map<Location, Location> cameFrom = new HashMap<>();

        visited.add(originalLocation);
        queue.add(originalLocation);
        cameFrom.put(originalLocation, null);

        while (!queue.isEmpty()) {
            Location firstEl = queue.poll();
            Entity entity = map.getEntityByLocation(firstEl);

            if (entity != null && isTypeOf.test(entity)) {
                return new MapAndGoal(cameFrom, firstEl);
            }

            for (Location neighbourLocation : generateNeighboursInsideMap(firstEl, map)) {
                if (visited.contains(neighbourLocation)) {
                    continue;
                }

                Entity neighbour = map.getEntityByLocation(neighbourLocation);
                if (neighbour != null && isTypeOf.test(neighbour)) {
                    cameFrom.put(neighbourLocation, firstEl);
                    return new MapAndGoal(cameFrom, neighbourLocation);
                }

                if (!isPassableForGoThrough((Creature) originalMovingCreature, neighbourLocation, map)) {
                    continue;
                }

                visited.add(neighbourLocation);
                queue.add(neighbourLocation);
                cameFrom.put(neighbourLocation, firstEl);

            }
        }

        return new MapAndGoal(cameFrom, null);
    }

    /**
     * @deprecated Предпочтителен multi-target BFS (что работает значительно быстрей)
     * работает в связке с
     * {@link #findClosestPath(WorldMap, Location, Creature, java.util.function.Predicate)}.
     * который выдает в данный метод конечные координаты КАЖДОЙ сущности (здесь же только начальная точка + конечная)
     * Оставьте этот метод только для детерминированных тестов с фиксированной целью.
     */
    @Deprecated(forRemoval = false)
    public Map<Location, Location> findPath(WorldMap map, Location initialLocation, Location finalLocation) {
        Set<Location> visited = new HashSet<>();
        Queue<Location> queue = new ArrayDeque<>();
        Map<Location, Location> cameFrom = new HashMap<>();

        visited.add(initialLocation);
        queue.add(initialLocation);
        cameFrom.put(initialLocation, null);

        while (!queue.isEmpty()) {
            Location firstEl = queue.poll();
            if (firstEl.equals(finalLocation)) {
                return cameFrom;
            }

            for (Location neighbourLocation : generateNeighboursInsideMap(firstEl, map)) {
                if (visited.contains(neighbourLocation)) {
                    continue;
                }
                if (!isPassableForReach(neighbourLocation, finalLocation, map)) {
                    continue;
                }

                visited.add(neighbourLocation);
                queue.add(neighbourLocation);
                cameFrom.put(neighbourLocation, firstEl);

                if (neighbourLocation.equals(finalLocation)) {
                    return cameFrom;
                }
            }
        }

        return cameFrom;
    }

    public List<Location> reconstructPath(Map<Location, Location> cameFromCameToMap, Location initialLocation, Location goal) {
        if (goal == null || !cameFromCameToMap.containsKey(goal)) {
            return emptyList();
        }

        List<Location> theRealPath = new ArrayList<>();

        Location current = goal;

        while (current != null) {
            theRealPath.add(current);
            if (current.equals(initialLocation)) {
                break;
            } else {
                current = cameFromCameToMap.get(current);
            }
        }

        if (!theRealPath.get(theRealPath.size() - 1).equals(initialLocation)) {
            return emptyList();
        }

        Collections.reverse(theRealPath);
        return theRealPath;
    }
}


