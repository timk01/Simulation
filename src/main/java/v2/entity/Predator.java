package v2.entity;

import v2.map.Location;
import v2.map.WorldMap;

import java.util.Optional;
import java.util.function.Predicate;

public final class Predator extends Creature {

    @Override
    public Predicate<Entity> isGoal() {
        return entity -> entity instanceof Herbivore;
    }
}
