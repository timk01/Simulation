package org.entity;

public class Grass extends StaticEntity {
    public static final int DEFAULT_NUTRITION = 10;

    private int nutrition;
    private Herbivore eatenBy;

    public Grass() {
        this(DEFAULT_NUTRITION);
    }

    public Grass(int nutrition) {
        this.nutrition = Math.max(DEFAULT_NUTRITION, nutrition);
    }

    public int getNutrition() {
        return nutrition;
    }

    public void setNutrition(int nutrition) {
        this.nutrition = Math.max(DEFAULT_NUTRITION, nutrition);
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
