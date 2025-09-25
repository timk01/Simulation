package org.simulation.Action;

import org.entity.*;
import org.map.Location;
import org.map.WorldMap;
import org.simulation.TurnAction;

import java.util.*;

public class MoveCreatures implements TurnAction {
    private final Statistic statistic;

    public MoveCreatures(Statistic statistic) {
        this.statistic = statistic;
    }

    @Override
    public void update(WorldMap map) {
        long grassCount = map.getCells().values().stream()
                .filter(e -> e instanceof Grass)
                .count();
        System.out.println("[DEBUG] grass on map=" + grassCount);

        List<MapSnapshot> snapshot = map.getCells().entrySet().stream()
                .map(e -> new MapSnapshot(e.getKey(), e.getValue()))
                .toList();

        List<MoveResult> moveResults = new ArrayList<>();
        for (MapSnapshot snap : snapshot) {
            Location oldLocation = snap.location();
            Entity entity = snap.entity();

            if (!(entity instanceof Creature creature)
                    || creature.getHp() <= 0
                    || creature.getDeathReason() != null) {
                continue;
            }

            Location newLocation = creature.makeMove(map, oldLocation);
            moveResults.add(new MoveResult(entity, oldLocation, newLocation));
        }

        Set<Location> occupied = new HashSet<>();

        for (MoveResult result : moveResults) {
            System.out.println("[MOVE] " + result.entity.getClass().getSimpleName()
                    + " " + result.oldLocation + " -> " + result.newLocation);
            Entity currOccupant = map.getEntityByLocation(result.newLocation);

            if (occupied.contains(result.newLocation)) {
                stay(map, result);
                System.out.println("[COLLISION] " + result.entity.getClass().getSimpleName()
                        + " stays at " + result.oldLocation + " (target " + result.newLocation + " occupied by move)");
            } else if (currOccupant == null) {
                move(map, result, occupied);
                System.out.println("[STEP] " + result.entity.getClass().getSimpleName()
                        + " moved to " + result.newLocation);
            } else if (hasPredatorKilled(result, currOccupant)) {
                Predator predator = (Predator) result.entity;
                Herbivore herbivore = (Herbivore) currOccupant;
                herbivore.setDeathReason(Creature.DeathReason.KILLED_BY_PREDATOR);
                herbivore.setKilledBy(predator);
                relocate(map, result);
                occupied.add(result.newLocation);
                statistic.deathRegistrator(herbivore);
                statistic.registerPredatorKill(predator);
                System.out.println("[ATTACK] Predator " + result.oldLocation + " -> " + result.newLocation
                        + " killed Herbivore");
            } else if (hasHerbivoreEaten(result, currOccupant)) {
                Herbivore herbivore = (Herbivore) result.entity;
                Entity occ = map.getEntityByLocation(result.newLocation);
                if (occ instanceof Grass g) {
                    System.out.println("[CLEAN][GRASS] remove at " + result.newLocation +
                            ", eatenBy=" + (g.getEatenBy() != null ? g.getEatenBy().getIdString() : "null"));
                    statistic.registerGrassEaten(herbivore);

                    map.removeEntity(result.newLocation);
                }

/*
                if (occ instanceof Grass) {
                    map.removeEntity(result.newLocation);
                    long eatenGrassNow = map.getCells().values().stream()
                            .filter(e -> e instanceof Grass gr && gr.getEatenBy() != null)
                            .count();
                    statistic.registerGrassEaten(herbivore, (int) eatenGrassNow);
                }*/

                relocate(map, result);

               /* long eatenGrassNow = map.getCells().values().stream()
                        .filter(e -> e instanceof Grass gr && gr.getEatenBy() != null)
                        .count();
                System.out.println("[DEBUG] eaten grass marked now=" + eatenGrassNow);
                statistic.registerGrassEaten(herbivore, (int)eatenGrassNow);
                herbivore.resetConsumedGrass();*/
                occupied.add(result.newLocation);
                System.out.println("[EAT] Herbivore " + result.oldLocation + " -> " + result.newLocation
                        + " ate Grass");
            } else {
                stay(map, result);
            }
        }
    }

    private void stay(WorldMap map, MoveResult result) {
        map.placeEntity(result.oldLocation, result.entity);
    }

    private void relocate(WorldMap map, MoveResult result) {
        Entity oldEntity = map.getEntityByLocation(result.oldLocation);
        Entity newEntity = map.getEntityByLocation(result.newLocation);

        System.out.println("[RELOCATE] before: old=" + result.oldLocation + " -> " + oldEntity
                + ", new=" + result.newLocation + " -> " + newEntity);

        map.removeEntity(result.oldLocation);
        map.placeEntity(result.newLocation, result.entity);

        System.out.println("[RELOCATE] after: old=" + result.oldLocation + " -> " + map.getEntityByLocation(result.oldLocation)
                + ", new=" + result.newLocation + " -> " + map.getEntityByLocation(result.newLocation));
    }

    private void move(WorldMap map, MoveResult result, Set<Location> occupied) {
        relocate(map, result);
        occupied.add(result.newLocation);
    }

    private boolean hasPredatorKilled(MoveResult result, Entity occupant) {
/*        return result.entity instanceof Predator predator
                && predator.getKilledEntities() > 0
                && occupant instanceof Herbivore;*/
        return result.entity instanceof Predator
                && occupant instanceof Herbivore h
                && h.getHp() <= 0;
    }

/*
    private void eatHerbivore(WorldMap map, MoveResult result, Predator predator) {
        relocate(map, result);
        predator.resetKilledEntities();
    }
*/

    private boolean hasHerbivoreEaten(MoveResult result, Entity occupant) {
        return result.entity instanceof Herbivore herbivore
                && herbivore.getConsumedGrass() > 0
                && occupant instanceof Grass;
    }

/*    private void eatGrass(WorldMap map, MoveResult result, Herbivore herbivore) {
        relocate(map, result);
        statistic.registerGrassEaten(herbivore);
        herbivore.resetConsumedGrass();
    }*/

    record MoveResult(Entity entity, Location oldLocation, Location newLocation) {

    }

    record MapSnapshot(Location location, Entity entity) {}
}
