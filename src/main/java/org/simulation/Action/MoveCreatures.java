package org.simulation.Action;

import org.entity.*;
import org.map.Location;
import org.map.WorldMap;
import org.map.path.PathFinder;
import org.simulation.TurnAction;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class MoveCreatures implements TurnAction {
    private static final Predicate<Map.Entry<Location, Entity>> ALIVE_CREATURE
            = e -> e.getValue() instanceof Creature c && c.getHp() > 0 && c.getDeathReason() == null;

    private final Statistic statistic;
    private final PathFinder pathFinder;

    public MoveCreatures(Statistic statistic, PathFinder pathFinder) {
        this.statistic = statistic;
        this.pathFinder = pathFinder;
    }

    private static void stay(WorldMap map, MoveResult result) {
        map.placeEntity(result.oldLocation, result.entity);
    }

    private void relocate(WorldMap map, MoveResult result) {
        map.removeEntity(result.oldLocation);
        map.placeEntity(result.newLocation, result.entity);
    }

    private void relocateAndReserve(WorldMap map, MoveResult result, Set<Location> reserve) {
        relocate(map, result);
        reserve.add(result.newLocation);
    }

    private void clearTargetAndRelocateAndReserve(WorldMap map, MoveResult result, Set<Location> reserve) {
        map.removeEntity(result.newLocation);
        relocateAndReserve(map, result, reserve);
    }

    private boolean hasPredatorKilled(MoveResult result, Entity occupant) {
        boolean isPredatorKiller = false;
        if (result.entity instanceof Predator && occupant instanceof Herbivore h) {
            isPredatorKiller = (h.getDeathReason() == Creature.DeathReason.KILLED_BY_PREDATOR)
                    && h.getKilledBy() == result.entity;
        }
        return isPredatorKiller;
    }

    private boolean hasHerbivoreEaten(MoveResult result, Entity occupant) {
        boolean herbivoreAte = false;
        if (result.entity instanceof Herbivore h && occupant instanceof Grass g) {
            herbivoreAte = g.getEatenBy() == h;
        }
        return herbivoreAte;
    }

    private static boolean isDead(Entity currOccupant) {
        return currOccupant instanceof Creature c && c.isDead();
    }

    @Override
    public void update(WorldMap map) {
        List<MoveResult> moveResults = map.getCells().entrySet().stream()
                .filter(ALIVE_CREATURE)
                .map(e -> {
                    Creature creature = (Creature) e.getValue();
                    Location oldLocation = e.getKey();
                    Location newLocation = creature.makeMove(map, oldLocation, pathFinder);
                    return new MoveResult(creature, oldLocation, newLocation);
                })
                .toList();

        Set<Location> occupiedLocation = new HashSet<>(moveResults.size());

        for (MoveResult result : moveResults) {
            Entity currOccupant = map.getEntityByLocation(result.newLocation);

            if (occupiedLocation.contains(result.newLocation)) {
                stay(map, result);
            } else if (currOccupant == null) {
                relocateAndReserve(map, result, occupiedLocation);
            } else if (hasPredatorKilled(result, currOccupant)) {
                Predator killer = (Predator) result.entity;
                Herbivore victim = (Herbivore) currOccupant;
                if (!statistic.isRegisteredDead(victim)) {
                    statistic.deathRegistrator(victim);
                    statistic.registerPredatorKill(killer);
                }
                clearTargetAndRelocateAndReserve(map, result, occupiedLocation);
            } else if (hasHerbivoreEaten(result, currOccupant)) {
                statistic.registerGrassEaten((Herbivore) result.entity);
                clearTargetAndRelocateAndReserve(map, result, occupiedLocation);
            } else if (isDead(currOccupant)) {
                clearTargetAndRelocateAndReserve(map, result, occupiedLocation);
            } else {
                stay(map, result);
            }
        }
    }

    static record MoveResult(Creature entity, Location oldLocation, Location newLocation) {
    }
}
