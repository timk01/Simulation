package simulation.entity;

import simulation.map.Location;
import simulation.map.WorldMap;

import java.util.function.Predicate;

public final class Herbivore extends Creature {

    public Herbivore(int speed, int hp, int maxHp) {
        super(speed, hp, maxHp);
    }

    @Override
    public Predicate<Entity> isGoal() {
        return entity -> entity instanceof Grass;
    }

    @Override
    boolean interactWithTarget(WorldMap worldMap, Location targetEntityLocation, Entity target) {
        worldMap.removeEntity(targetEntityLocation);
        heal(((Grass) target).getNutrition());
        return true;
    }
}
