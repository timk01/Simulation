package v2.config;

public enum RepopulatePreset {
    SMALL(8, 4),
    MEDIUM(20, 10),
    LARGE(30, 15);
    private final int grassMin;
    private final int herbivoreMin;

    RepopulatePreset(int grassMin, int herbivoreMin) {
        this.grassMin = grassMin;
        this.herbivoreMin = herbivoreMin;
    }

    public int getGrassMin() {
        return grassMin;
    }

    public int getHerbivoreMin() {
        return herbivoreMin;
    }
}
