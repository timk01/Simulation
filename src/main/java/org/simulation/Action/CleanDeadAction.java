package org.simulation.Action;

import org.entity.Creature;
import org.entity.Entity;
import org.map.Location;
import org.map.WorldMap;
import org.simulation.TurnAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CleanDeadAction implements TurnAction {
    @Override
    public void update(WorldMap map) {
        List<Location> locationList = new ArrayList<>();
        for (Map.Entry<Location, Entity> entry : map.getCells().entrySet()) {
            Entity entity = entry.getValue();
            if (entity instanceof Creature creature && creature.tick()) {
                locationList.add(entry.getKey());
            }
        }

        for (Location location : locationList) {
            map.removeEntity(location);
        }
    }
}
