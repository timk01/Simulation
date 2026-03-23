package simulation.config;

public class RepopulateValues {
    private final int grassMin;
    private final int herbivoreMin;

    RepopulateValues(int grassMin, int herbivoreMin) {
        this.grassMin = grassMin;
        this.herbivoreMin = herbivoreMin;
        SimulationConfigValidator.validate(this);
    }

    public int getGrassMin() {
        return grassMin;
    }

    public int getHerbivoreMin() {
        return herbivoreMin;
    }
}
