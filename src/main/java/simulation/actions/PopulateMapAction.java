package simulation.actions;

import simulation.entity.EntityType;
import simulation.map.Location;
import simulation.map.WorldMap;
import simulation.config.EntityStartValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PopulateMapAction implements Action {
    private final ActionHelper actionHelper;
    private final EntityStartValues entityStartValues;

    public PopulateMapAction(ActionHelper actionHelper, EntityStartValues entityStartValues) {
        this.actionHelper = actionHelper;
        this.entityStartValues = entityStartValues;
    }

    @Override
    public void execute(WorldMap worldMap) {
        List<Location> emptyLocations = actionHelper.fillEmptyLocationsList(worldMap);

        Collections.shuffle(emptyLocations);

        List<EntityPlan> planList = new ArrayList<>();
        for (EntityType entityType : EntityType.values()) {
            planList.add(new EntityPlan(entityType, getTypeQuantity(entityType)));
        }

        int entitiesPlanted = 0;
        for (EntityPlan entityPlan : planList) {
            EntityType type = entityPlan.entityType();
            int quantity = entityPlan.quantity();
            for (int i = 0; i < quantity; i++) {
                worldMap.tryAddEntity(emptyLocations.get(entitiesPlanted++),
                        actionHelper.getEntityFactory().createEntity(type));
            }
        }
    }

    record EntityPlan(EntityType entityType, int quantity) {
    }

    private int getTypeQuantity(EntityType entityType) {
        return switch (entityType) {
            case ROCK -> entityStartValues.startStonesQuantity();
            case GRASS -> entityStartValues.startGrassQuantity();
            case TREE -> entityStartValues.startTreeQuantity();
            case HERBIVORE -> entityStartValues.startHerbivoresQuantity();
            case PREDATOR -> entityStartValues.startPredatorsQuantity();
        };
    }
}
