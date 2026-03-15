package v2.entity;

import v2.config.EntityStatsPreset;

public class EntityFactory {

    private final EntityStatsPreset entityStatsPreset;
    private final EntityStatsPreset.GrassStats grassStats;
    private final EntityStatsPreset.HerbivoreStats herbivoreStats;
    private final EntityStatsPreset.PredatorStats predatorStats;

    public EntityFactory(EntityStatsPreset entityStatsPreset) {
        this.entityStatsPreset = entityStatsPreset;
        this.grassStats = entityStatsPreset.getGrass();
        this.herbivoreStats = entityStatsPreset.getHerbivore();
        this.predatorStats = entityStatsPreset.getPredator();
    }

    public Entity createEntity(EntityType type) {
        return switch (type) {
            case ROCK -> new Rock();
            case TREE -> new Tree();
            case GRASS -> new Grass(
                    grassStats.nutrition()
            );
            case HERBIVORE -> new Herbivore(
                    herbivoreStats.speed(),
                    herbivoreStats.hp(),
                    herbivoreStats.maxHp()
            );
            case PREDATOR -> new Predator(
                    predatorStats.speed(),
                    predatorStats.hp(),
                    predatorStats.maxHp(),
                    predatorStats.attack()
            );
        };
    }
}
