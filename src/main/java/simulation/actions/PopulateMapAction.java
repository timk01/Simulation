package simulation.actions;

import simulation.config.EntitiesQuantityPreset;
import simulation.entity.EntityType;
import simulation.map.Location;
import simulation.map.WorldMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PopulateMapAction implements Action {
    private final ActionHelper actionHelper;
    private final EntitiesQuantityPreset entitiesPreset;

    public PopulateMapAction(ActionHelper actionHelper, EntitiesQuantityPreset entitiesPreset) {
        this.actionHelper = actionHelper;
        this.entitiesPreset = entitiesPreset;
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
            case ROCK -> entitiesPreset.getStartStonesQuantity();
            case GRASS -> entitiesPreset.getStartGrassQuantity();
            case TREE -> entitiesPreset.getStartTreeQuantity();
            case HERBIVORE -> entitiesPreset.getStartHerbivoresQuantity();
            case PREDATOR -> entitiesPreset.getStartPredatorsQuantity();
        };
    }
}
