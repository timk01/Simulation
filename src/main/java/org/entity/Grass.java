package org.entity;

import java.util.concurrent.ThreadLocalRandom;

public class Grass extends StaticEntity {
    private static final int MIN_NUTRITION = 7;
    private static final int MAX_NUTRITION = 12;

    private int nutrition;
    private Herbivore eatenBy;

    public Grass() {
        this.nutrition = ThreadLocalRandom.current().nextInt(MAX_NUTRITION - MIN_NUTRITION + 1) + MIN_NUTRITION;
    }

    public Grass(int nutrition) {
        this.nutrition = Math.min(MAX_NUTRITION, Math.max(nutrition, MIN_NUTRITION));
    }

    public int getNutrition() {
        return nutrition;
    }

    public void setNutrition(int nutrition) {
        this.nutrition = Math.min(MAX_NUTRITION, Math.max(nutrition, MIN_NUTRITION));
    }

    public Herbivore getEatenBy() {
        return eatenBy;
    }

    public void setEatenBy(Herbivore eatenBy) {
        this.eatenBy = eatenBy;
    }

    @Override
    public String toString() {
        return "Grass{nutrition=" + nutrition +
                (eatenBy != null ? ", eatenBy=" + eatenBy.getIdString() : "") + "}";
    }
}
