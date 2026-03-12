package v2.entity;

public class Grass extends StaticEntity {
    private int nutrition = 5;

    public Grass(int nutrition) {
        this.nutrition = nutrition;
    }

    public Grass() {
    }

    public int getNutrition() {
        return nutrition;
    }

    public void setNutrition(int nutrition) {
        this.nutrition = nutrition;
    }
}
