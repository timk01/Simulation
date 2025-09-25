package org.simulation.Action;

import org.entity.Entity;
import org.entity.Herbivore;
import org.map.WorldMap;
import org.simulation.TurnAction;

public class FlushGrassEatenAction implements TurnAction {
    private final Statistic statistic;

    public FlushGrassEatenAction(Statistic statistic) {
        this.statistic = statistic;
    }

    @Override
    public void update(WorldMap map) {
        for (Entity e : map.getCells().values()) {
            if (e instanceof Herbivore h) {
                int eaten = h.getConsumedGrass();
                if (eaten > 0) {
                    statistic.registerGrassEaten(h, eaten);
                    h.resetConsumedGrass();
                }
            }
        }
    }
}