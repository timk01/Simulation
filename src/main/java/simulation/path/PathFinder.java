package simulation.path;

import simulation.entity.Entity;
import simulation.map.Location;
import simulation.map.WorldMap;

import java.util.List;
import java.util.function.Predicate;

public interface PathFinder {
    List<Location> findPath(WorldMap map,
                            Location originalLocation,
                            Predicate<Entity> isGoal);
}
