package v2.config;

public enum EntitiesQuantityPreset {
    SMALL(5, 5, 12, 10, 5),
    MEDIUM(15, 15, 40, 25, 15),
    LARGE(25, 25, 80, 40, 25);

    private final int startTreeQuantity;
    private final int startStonesQuantity;
    private final int startGrassQuantity;
    private final int startHerbivoresQuantity;
    private final int startPredatorsQuantity;

    EntitiesQuantityPreset(int startTreeQuantity,
                           int startStonesQuantity,
                           int startGrassQuantity,
                           int startHerbivoresQuantity,
                           int startPredatorsQuantity) {
        validateEntityQuantity(startTreeQuantity, startStonesQuantity, startGrassQuantity, startHerbivoresQuantity, startPredatorsQuantity);
        this.startTreeQuantity = startTreeQuantity;
        this.startStonesQuantity = startStonesQuantity;
        this.startGrassQuantity = startGrassQuantity;
        this.startHerbivoresQuantity = startHerbivoresQuantity;
        this.startPredatorsQuantity = startPredatorsQuantity;
    }

    private void validateEntityQuantity(int startTreeQuantity, int startStonesQuantity, int startGrassQuantity, int startHerbivoresQuantity, int startPredatorsQuantity) {
        if (startTreeQuantity < 0
                || startStonesQuantity < 0
                || startGrassQuantity < 0
                || startHerbivoresQuantity < 0
                || startPredatorsQuantity < 0) {
            throw new IllegalArgumentException("entity start quantities cannot be negative");
        }
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
