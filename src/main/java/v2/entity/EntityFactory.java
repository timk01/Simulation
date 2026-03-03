package v2.entity;

public class EntityFactory {

    public Entity createEntity(EntityType type) {
        return switch (type) {
            case ROCK -> new Rock();
            case TREE -> new Tree();
            case GRASS -> new Grass();
            case HERBIVORE -> new Herbivore();
            case PREDATOR -> new Predator();
        };
    }
}
