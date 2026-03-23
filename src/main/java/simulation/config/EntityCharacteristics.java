package simulation.config;

public class EntityCharacteristics {
    private final HerbivoreStats herbivore;
    private final PredatorStats predator;
    private final GrassStats grass;

    public EntityCharacteristics(HerbivoreStats herbivore, PredatorStats predator, GrassStats grass) {
        this.herbivore = herbivore;
        this.predator = predator;
        this.grass = grass;
        SimulationConfigValidator.validate(this);
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
