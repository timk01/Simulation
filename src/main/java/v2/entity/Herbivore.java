package v2.entity;

import v2.map.Location;
import v2.map.WorldMap;

import java.util.Optional;
import java.util.function.Predicate;

public final class Herbivore extends Creature {

/*    @Override
    public void makeMove(WorldMap map, Location location) {
        super.makeMove(map, location);
    }*/

    @Override
    Predicate<Entity> isGoal() {
        return entity -> entity instanceof Grass;
    }
}
