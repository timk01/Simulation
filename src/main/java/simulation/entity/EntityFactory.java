package simulation.entity;

import simulation.config.EntityCharacteristics;

public class EntityFactory {

    private final EntityCharacteristics entityCharacteristics;
    private final EntityCharacteristics.GrassStats grassStats;
    private final EntityCharacteristics.HerbivoreStats herbivoreStats;
    private final EntityCharacteristics.PredatorStats predatorStats;

    public EntityFactory(EntityCharacteristics entityCharacteristics) {
        this.entityCharacteristics = entityCharacteristics;
        this.grassStats = entityCharacteristics.getGrass();
        this.herbivoreStats = entityCharacteristics.getHerbivore();
        this.predatorStats = entityCharacteristics.getPredator();
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
