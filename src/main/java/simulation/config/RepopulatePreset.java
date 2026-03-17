package simulation.config;

public enum RepopulatePreset {
    SMALL(8, 4),
    MEDIUM(20, 10),
    LARGE(30, 15);
    private final int grassMin;
    private final int herbivoreMin;

    RepopulatePreset(int grassMin, int herbivoreMin) {
        this.grassMin = grassMin;
        this.herbivoreMin = herbivoreMin;
        validateRepopulateStats();
    }

    private void validateRepopulateStats() {
        if (grassMin <= 0 || herbivoreMin <= 0) {
            throw new IllegalArgumentException("grassMin and herbivoreMin stat must be positive");
        }
    }

    public int getGrassMin() {
        return grassMin;
    }

    public int getHerbivoreMin() {
        return herbivoreMin;
    }
}
