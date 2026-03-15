package v2.entity;

public enum EntityType {
    ROCK,
    TREE,
    GRASS,
    HERBIVORE,
    PREDATOR;

    public boolean matches(Entity entity) {
        return switch (this) {
            case ROCK -> entity instanceof Rock;
            case GRASS -> entity instanceof Grass;
            case TREE -> entity instanceof Tree;
            case HERBIVORE -> entity instanceof Herbivore;
            case PREDATOR -> entity instanceof Predator;
            default -> throw new IllegalArgumentException("wrong type while matching entity: " + this);
        };
    }
}
