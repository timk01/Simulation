package org.simulation.config.preset;

import org.simulation.config.CreaturesConfig;

public enum CreaturesPreset {
    SMALL(10, 5, 0.07, 0.06),
    MEDIUM(25, 15, 0.08, 0.07),
    LARGE(40, 25, 0.1, 0.09);

    private final int herbivoreCount;
    private final int predatorCount;
    private final double herbivoresCapShare;
    private final double predatorsCapShare;

    CreaturesPreset(int herbivoreCount, int predatorCount, double herbivoresCapShare, double predatorsCapShare) {
        this.herbivoreCount = herbivoreCount;
        this.predatorCount = predatorCount;
        this.herbivoresCapShare = herbivoresCapShare;
        this.predatorsCapShare = predatorsCapShare;
    }

    public int getHerbivoreCount() {
        return herbivoreCount;
    }

    public int getPredatorCount() {
        return predatorCount;
    }

    public double getHerbivoresCapShare() {
        return herbivoresCapShare;
    }

    public double getPredatorsCapShare() {
        return predatorsCapShare;
    }

    public CreaturesConfig toConfig() {
        return new CreaturesConfig(herbivoreCount, predatorCount, herbivoresCapShare, predatorsCapShare);
    }
}
