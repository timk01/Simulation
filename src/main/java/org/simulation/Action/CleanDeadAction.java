package org.simulation.Action;

import org.entity.*;
import org.map.Location;
import org.map.WorldMap;
import org.simulation.TurnAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CleanDeadAction implements TurnAction {
    private final Statistic statistic;

    public CleanDeadAction(Statistic statistic) {
        this.statistic = statistic;
    }

    @Override
    public void update(WorldMap map) {
        List<Location> eatenGrass = new ArrayList<>();
        List<Location> toRemove = new ArrayList<>();

        for (Map.Entry<Location, Entity> entry : new ArrayList<>(map.getCells().entrySet())) {
            Entity entity = entry.getValue();

            if (entity instanceof Creature creature && creature.isDead()) {
                System.out.println("[CLEAN] removing " + creature.getClass().getSimpleName()
                        + " id=" + creature.getIdString()
                        + " at " + entry.getKey()
                        + " reason=" + creature.getDeathReason());

                if (!statistic.isRegisteredDead(creature)) {
                    statistic.deathRegistrator(creature);

                    if (creature instanceof Herbivore h
                            && h.getDeathReason() == Creature.DeathReason.KILLED_BY_PREDATOR) {
                        statistic.registerPredatorKill(h.getKilledBy());
                    }
                }

                toRemove.add(entry.getKey());
            }

            // Чистим съеденную траву
            if (entity instanceof Grass grass && grass.getEatenBy() != null) {
                System.out.println("[CLEAN] removing Grass at " + entry.getKey()
                        + " eatenBy=" + grass.getEatenBy().getIdString());
                statistic.registerGrassEaten(grass.getEatenBy());
                eatenGrass.add(entry.getKey());
            }
        }


        for (Location loc : toRemove) {
            map.removeEntity(loc);
        }
        // удаляем траву отдельно, чтобы не модифицировать map во время итерации
        for (Location loc : eatenGrass) {
            map.removeEntity(loc);
        }
    }

    private record DeadCreature(Location location, Creature creature) {}
}

