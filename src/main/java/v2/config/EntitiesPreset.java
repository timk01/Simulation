package v2.config;

public enum EntitiesPreset {
    SMALL(5, 5, 12, 10, 5),
    MEDIUM(15, 15, 40, 25, 15),
    LARGE(25, 25, 80, 40, 25);

    private final int startTreeQuantity;
    private final int startStonesQuantity;
    private final int startGrassQuantity;
    private final int startHerbivoresQuantity;
    private final int startPredatorsQuantity;

    EntitiesPreset(int startTreeQuantity,
                   int startStonesQuantity,
                   int startGrassQuantity,
                   int startHerbivoresQuantity,
                   int startPredatorsQuantity) {
        this.startTreeQuantity = startTreeQuantity;
        this.startStonesQuantity = startStonesQuantity;
        this.startGrassQuantity = startGrassQuantity;
        this.startHerbivoresQuantity = startHerbivoresQuantity;
        this.startPredatorsQuantity = startPredatorsQuantity;
    }

    public int getStartTreeQuantity() {
        return startTreeQuantity;
    }

    public int getStartStonesQuantity() {
        return startStonesQuantity;
    }

    public int getStartGrassQuantity() {
        return startGrassQuantity;
    }

    public int getStartHerbivoresQuantity() {
        return startHerbivoresQuantity;
    }

    public int getStartPredatorsQuantity() {
        return startPredatorsQuantity;
    }
}
