package org.simulation.Action;

import org.entity.Entity;
import org.entity.Grass;
import org.map.Location;
import org.map.WorldMap;
import org.simulation.TurnAction;
import org.simulation.config.GrassConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GrowGrass implements TurnAction {
    private final GrassConfig grassConfig;

    public GrowGrass(GrassConfig grassConfig) {
        this.grassConfig = grassConfig;
    }

    public boolean isGrass(Entity target) {
        return target instanceof Grass;
    }

    public boolean isLocationValidForGrassSpawn(Location initialLocation, WorldMap map) {
        int x = initialLocation.x();
        int y = initialLocation.y();
        List<Location> neighbourLocations = new ArrayList<>();
        neighbourLocations.add(new Location(x + 1, y));
        neighbourLocations.add(new Location(x - 1, y));
        neighbourLocations.add(new Location(x, y + 1));
        neighbourLocations.add(new Location(x, y - 1));

        neighbourLocations.add(new Location(x + 1, y + 1));
        neighbourLocations.add(new Location(x - 1, y - 1));
        neighbourLocations.add(new Location(x + 1, y - 1));
        neighbourLocations.add(new Location(x - 1, y + 1));
        for (Location neighbourLocation : neighbourLocations) {
            Entity neighbour = map.getEntityByLocation(neighbourLocation);
            if (isGrass(neighbour)) {
                return false;
            }
        }
        return true;
    }

    private List<Location> prepareMap(WorldMap map, int howManyGrassToSpawn) {
        List<Location> emptyLocations = map.listEmptyLocationsShuffled();

        List<Location> spawnAbleLocations = new ArrayList<>();
        for (Location location : emptyLocations) {
            if (isLocationValidForGrassSpawn(location, map)) {
                spawnAbleLocations.add(location);
            }
            if (spawnAbleLocations.size() == howManyGrassToSpawn) {
                break;
            }
        }
        return spawnAbleLocations;
    }

    private int countGrassSpawnings(WorldMap map) {
        int totalMapCells = map.getInitialCapacity();
        int grassCap = (int) Math.floor(totalMapCells * grassConfig.getCapShare());
        int currentGrass = (int) map.getCells().values().stream()
                .filter(e -> e instanceof Grass)
                .count();
        int freeCap = Math.max(0, grassCap - currentGrass);

        int basicSpawn = (int) Math.floor(totalMapCells * grassConfig.getRegenPerTickShare());
        int realSpawn = Math.max(basicSpawn, grassConfig.getMinSpawnPerTick());

        return Math.min(freeCap, realSpawn);
    }

    private void spawnGrass(WorldMap map, List<Location> spawnAbleLocations, int howManyGrassToSpawn) {
        for (Location spawnAbleLocation : spawnAbleLocations) {
            if (howManyGrassToSpawn == 0) {
                break;
            }

            map.placeEntity(spawnAbleLocation, new Grass());
            howManyGrassToSpawn--;
        }
    }

    @Override
    public void update(WorldMap map) {
        int howManyGrassToSpawn = countGrassSpawnings(map);

        List<Location> spawnAbleLocations = prepareMap(map, howManyGrassToSpawn);

        spawnGrass(map, spawnAbleLocations, howManyGrassToSpawn);
    }
}
