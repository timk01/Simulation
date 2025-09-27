package org.simulation.Action;

import org.entity.Creature;
import org.entity.Entity;
import org.entity.Herbivore;
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
        List<Location> toRemove = new ArrayList<>();

        for (Map.Entry<Location, Entity> entry : new ArrayList<>(map.getCells().entrySet())) {
            Creature creature = (entry.getValue() instanceof Creature c) ? c : null;
            boolean isCreatureDead = creature != null && creature.isDead();

            if (isCreatureDead) {
                if (shouldRegisterPredatorKill(creature)) {
                    statistic.registerPredatorKill(((Herbivore) creature).getKilledBy());
                }

                if (!statistic.isRegisteredDead(creature)) {
                    statistic.deathRegistrator(creature);
                }

                toRemove.add(entry.getKey());
            }
        }

        for (Location loc : toRemove) {
            map.removeEntity(loc);
        }
    }

    private boolean shouldRegisterPredatorKill(Creature creature) {
        return creature instanceof Herbivore h
                && h.getDeathReason() == Creature.DeathReason.KILLED_BY_PREDATOR
                && h.getKilledBy() != null
                && !statistic.isRegisteredDead(h);
    }
}