package v2.actions;

import v2.entity.Entity;
import v2.entity.EntityFactory;
import v2.entity.EntityType;
import v2.entity.Tree;
import v2.map.Location;
import v2.map.WorldMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class PopulateMapAction implements Action {

    record EntityPlan(EntityType entityType, int quantity) {

    }

    @Override
    public void execute(WorldMap map) {
        EntityFactory entityFactory = new EntityFactory();

        List<Location> emptyLocations = fillEmptyLocationsList(map);
        // TODO: добавить пресеты конфигурации (SMALL/MEDIUM/LARGE) с фиксированными counts и size; убрать хардкод "2"
        // todo: начать с базы 20*20 как в старом проекте - и боже тебя упаси ставить капы. просто хардкод на проработанные размеры

        Collections.shuffle(emptyLocations);

        List<EntityPlan> planList = new ArrayList<>();
        for (EntityType entityType : EntityType.values()) {
            planList.add(new EntityPlan(entityType, 2));
            //toDo 2 - приходит из общего конфига как каунтер существ (максимальный)
        }

        int entitiesPlanted = 0;
        for (EntityPlan entityPlan : planList) {
            EntityType type = entityPlan.entityType();
            int quantity = entityPlan.quantity();
            for (int i = 0; i < quantity; i++) {
                map.tryAddEntity(emptyLocations.get(entitiesPlanted++), entityFactory.createEntity(type));
            }
        }
    }

    private List<Location> fillEmptyLocationsList(WorldMap map) {
        List<Location> freeLocations = new ArrayList<>();
        Location location;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                location = new Location(x, y);
                if (map.getEntity(location).isEmpty()) {
                    freeLocations.add(location);
                }
            }
        }
        return freeLocations;
    }
}
