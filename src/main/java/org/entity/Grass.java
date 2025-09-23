package org.entity;

public class Grass extends StaticEntity {
    private int nutrition;

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
}
