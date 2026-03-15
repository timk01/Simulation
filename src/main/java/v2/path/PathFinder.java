package v2.path;

import v2.entity.Entity;
import v2.map.Location;
import v2.map.WorldMap;

import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;

public class PathFinder {
    public List<Location> findPath(WorldMap map,
                                   Location originalLocation,
                                   Predicate<Entity> isGoal) {
        Set<Location> visited = new HashSet<>();
        Queue<Location> queue = new ArrayDeque<>();
        Map<Location, Location> cameFrom = new HashMap<>();


        visited.add(originalLocation);
        queue.add(originalLocation);
        cameFrom.put(originalLocation, null);

        while (!queue.isEmpty()) {
            Location firstElLoc = queue.poll();
            Optional<Entity> currentEntity = map.getEntity(firstElLoc);

            if ((currentEntity.isPresent() && isGoal.test(currentEntity.get()))) {
                return reconstructPath(cameFrom, originalLocation, firstElLoc);
            }

            for (Location neighbourLocation : generateNeighboursInsideMap(firstElLoc, map)) {
                if (visited.contains(neighbourLocation)) {
                    continue;
                }

                Optional<Entity> neighbour = map.getEntity(neighbourLocation);
                if (neighbour.isPresent() && isGoal.test(neighbour.get())) {
                    cameFrom.put(neighbourLocation, firstElLoc);
                    return reconstructPath(cameFrom, originalLocation, neighbourLocation);
                }

                if (neighbour.isPresent()) {
                    continue;
                }
                visited.add(neighbourLocation);
                queue.add(neighbourLocation);
                cameFrom.put(neighbourLocation, firstElLoc);
            }
        }

        return emptyList();
    }

    private List<Location> reconstructPath(Map<Location, Location> cameFrom, Location initialLocation, Location goal) {
        if (goal == null || !cameFrom.containsKey(goal)) {
            return emptyList();
        }

        List<Location> currentPath = new ArrayList<>();

        Location current = goal;

        while (current != null) {
            currentPath.add(current);
            if (current.equals(initialLocation)) {
                break;
            } else {
                current = cameFrom.get(current);
            }
        }

        if (isPathInvalid(initialLocation, currentPath)) {
            return emptyList();
        }

        Collections.reverse(currentPath);
        return currentPath;
    }

    private boolean isPathInvalid(Location initialLocation, List<Location> currentPath) {
        return !currentPath.get(currentPath.size() - 1).equals(initialLocation);
    }

    private List<Location> generateNeighboursInsideMap(Location initialLocation, WorldMap map) {
        List<Location> insideMapLocations = new ArrayList<>();
        for (Location nearby : initialLocation.neighbourLocations()) {
            if (map.isInsideMap(nearby)) {
                insideMapLocations.add(nearby);
            }
        }
        return insideMapLocations;
    }
}


