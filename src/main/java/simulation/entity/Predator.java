package simulation.entity;

import simulation.map.Location;
import simulation.map.WorldMap;

import java.util.function.Predicate;

public final class Predator extends Creature {

    private final int attack;

    public Predator(int speed, int hp, int maxHp, int attack) {
        super(speed, hp, maxHp);
        this.attack = attack;
    }

    @Override
    public Predicate<Entity> isGoal() {
        return entity -> entity instanceof Herbivore;
    }

    @Override
    boolean interactWithTarget(WorldMap worldMap, Location targetEntityLocation, Entity target) {
        Herbivore herbivore = (Herbivore) target;
        herbivore.takeDamage(attack);
        boolean result = false;
        if (herbivore.isDead()) {
            worldMap.removeEntity(targetEntityLocation);
            result = true;
        }
        heal(attack);
        return result;
    }
}
