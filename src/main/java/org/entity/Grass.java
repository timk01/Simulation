package org.entity;

public class Grass extends StaticEntity {
    private int nutrition;
    private Herbivore eatenBy;

    public Grass() {
        nutrition = 10;
    }

    public Grass(int nutrition) {
        this.nutrition = nutrition;
    }

    public int getNutrition() {
        return nutrition;
    }

    public void setNutrition(int nutrition) {
        this.nutrition = nutrition;
    }

    public Herbivore getEatenBy() {
        return eatenBy;
    }

    public void setEatenBy(Herbivore eatenBy) {
        this.eatenBy = eatenBy;
    }
}
