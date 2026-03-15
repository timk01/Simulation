package v2.entity;

public class Grass extends StaticEntity {
    private final int nutrition;

    public Grass(int nutrition) {
        this.nutrition = nutrition;
    }

    public int getNutrition() {
        return nutrition;
    }
}
