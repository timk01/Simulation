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
        this.herbivore = herbivore;
        this.predator = predator;
        this.grass = grass;
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
