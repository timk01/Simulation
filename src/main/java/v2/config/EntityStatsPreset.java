package v2.config;

public enum EntityStatsPreset {
    SMALL(
            new HerbivoreStats(2, 10, 20),
            new PredatorStats(3, 10, 20, 8),
            new GrassStats(5)
    ),
    MEDIUM(
            new HerbivoreStats(2, 10, 20),
            new PredatorStats(3, 10, 20, 8),
            new GrassStats(5)
    ),
    LARGE(
            new HerbivoreStats(2, 10, 20),
            new PredatorStats(3, 10, 20, 8),
            new GrassStats(5)
    );

    private final HerbivoreStats herbivore;
    private final PredatorStats predator;
    private final GrassStats grass;

    EntityStatsPreset(HerbivoreStats herbivore, PredatorStats predator, GrassStats grass) {
        validateEntityStats();
        this.herbivore = herbivore;
        this.predator = predator;
        this.grass = grass;
    }

    private void validateEntityStats() {
        if (herbivore.speed() <= 0 || herbivore.hp() <= 0 || herbivore.maxHp() <= 0) {
            throw new IllegalArgumentException("herbivore stats (speed, hp, maxHp)  must be positive");
        }
        if (predator.speed() <= 0 || predator.hp() <= 0 || predator.maxHp() <= 0 || predator.attack() <= 0) {
            throw new IllegalArgumentException("predator stats (speed, hp, maxHp, attack)  must be positive");
        }
        if (grass.nutrition() <= 0) {
            throw new IllegalArgumentException("grass stats (nutrition)  must be positive");
        }
    }

    public HerbivoreStats getHerbivore() {
        return herbivore;
    }

    public PredatorStats getPredator() {
        return predator;
    }

    public GrassStats getGrass() {
        return grass;
    }

    public record HerbivoreStats(int speed, int hp, int maxHp) {
    }

    public record PredatorStats(int speed, int hp, int maxHp, int attack) {
    }

    public record GrassStats(int nutrition) {
    }
}
