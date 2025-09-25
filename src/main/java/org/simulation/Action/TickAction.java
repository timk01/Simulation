package org.simulation.Action;

import org.entity.Creature;
import org.entity.Entity;
import org.map.WorldMap;
import org.simulation.TurnAction;

public class TickAction implements TurnAction {
    private final Statistic statistic;

    public TickAction(Statistic statistic) {
        this.statistic = statistic;
    }

    @Override
    public void update(WorldMap map) {
        for (Entity e : map.getCells().values()) {
            if (e instanceof Creature c) {
                if (c.tick()) { // true если умер от голода
                    statistic.deathRegistrator(c);
                }
            }
        }
    }
}