package v2.entity;

import v2.map.Location;
import v2.map.WorldMap;

import java.util.Optional;
import java.util.function.Predicate;

public final class Predator extends Creature {

    private int attack = 8;

    public Predator(int speed, int hp, int maxHp, int attack) {
        super(3, hp, maxHp); //todo hardcoded
        this.attack = attack;
    }

    public Predator() {
    }

    @Override
    public Predicate<Entity> isGoal() {
        return entity -> entity instanceof Herbivore;
    }

    @Override
    boolean interactWithTarget(WorldMap map, Location targetEntityLocation, Entity target) {
        Herbivore herbivore = (Herbivore) target;
        herbivore.takeDamage(attack);
        System.out.println("herbivore HP after predator attack: " + herbivore.getHp());
        boolean result = false;
        if (herbivore.isDead()) {
            System.out.println("if Herbivore is dead: " + herbivore.isDead());
            map.removeEntity(targetEntityLocation);
            result = true;
        }
        System.out.println("predator HP before predator attack: " + this.getHp());
        heal(attack);
        System.out.println("predator HP after predator attack: " + this.getHp());
        return result;
    }
}
