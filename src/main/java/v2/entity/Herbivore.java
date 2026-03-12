package v2.entity;

import v2.map.Location;
import v2.map.WorldMap;

import java.util.Optional;
import java.util.function.Predicate;

public final class Herbivore extends Creature {

    public Herbivore(int speed, int hp, int maxHp) {
        super(speed, hp, maxHp);
    }

    public Herbivore() {
    }

    @Override
    public Predicate<Entity> isGoal() {
        return entity -> entity instanceof Grass;
    }

    @Override
    boolean interactWithTarget(WorldMap map, Location targetEntityLocation, Entity target) {
        map.removeEntity(targetEntityLocation);
        heal(((Grass) target).getNutrition());
        return true;
    }
}
