package simulation.config;

public record EntityStartValues(int startTreeQuantity, int startStonesQuantity, int startGrassQuantity,
                                int startHerbivoresQuantity, int startPredatorsQuantity) {
    public EntityStartValues(int startTreeQuantity,
                             int startStonesQuantity,
                             int startGrassQuantity,
                             int startHerbivoresQuantity,
                             int startPredatorsQuantity) {
        this.startTreeQuantity = startTreeQuantity;
        this.startStonesQuantity = startStonesQuantity;
        this.startGrassQuantity = startGrassQuantity;
        this.startHerbivoresQuantity = startHerbivoresQuantity;
        this.startPredatorsQuantity = startPredatorsQuantity;
        SimulationConfigValidator.validate(this);
    }
}