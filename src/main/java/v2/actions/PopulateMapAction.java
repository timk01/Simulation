package v2.actions;

import v2.config.EntitiesPreset;
import v2.entity.*;
import v2.map.Location;
import v2.map.WorldMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PopulateMapAction implements Action {

    private ActionHelper actionHelper;

    private EntitiesPreset entitiesPreset;

    public PopulateMapAction(ActionHelper actionHelper, EntitiesPreset entitiesPreset) {
        this.actionHelper = actionHelper;
        this.entitiesPreset = entitiesPreset;
    }

    record EntityPlan(EntityType entityType, int quantity) {

    }

    @Override
    public void execute(WorldMap map) {
        List<Location> emptyLocations = actionHelper.fillEmptyLocationsList(map);

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
                map.tryAddEntity(emptyLocations.get(entitiesPlanted++),
                        actionHelper.getEntityFactory().createEntity(type));
            }
        }
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
