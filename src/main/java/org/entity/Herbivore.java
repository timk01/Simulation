package org.entity;

import org.map.Location;
import org.map.WorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Herbivore extends Creature {

    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 3;
    private static final int MIN_HP = 10;
    private static final int MAX_HP = 20;

    private int consumedGrass;

    public Herbivore(Random random) {
        super(
                random.nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED,
                random.nextInt(MAX_HP - MIN_HP + 1) + MIN_HP);
    }

    public Herbivore(int speed, int hp) {
        super(speed, hp);
    }

    public int getConsumedGrass() {
        return consumedGrass;
    }

    public void resetConsumedGrass() {
        this.consumedGrass = 0;
    }

    private Location getRandomLocation(WorldMap map, Location location) {
        int x;
        int y;
        Location newLocation;
        Random random = new Random();
        do {
            x = location.x();
            y = location.y();
            int number = random.nextInt(4);
            switch (number) {
                case 0 -> newLocation = new Location(x + 1, y);
                case 1 -> newLocation = new Location(x - 1, y);
                case 2 -> newLocation = new Location(x, y + 1);
                case 3 -> newLocation = new Location(x, y - 1);
                default -> newLocation = location;
            };
        } while (!isFree(map, newLocation));
        return newLocation;
    }

    @Override
    public Location makeMove(WorldMap map, Location location) {
        Location newRandomLocation = getRandomLocation(map, location);

        Entity entityOnNextPoint = map.getEntityByLocation(newRandomLocation);
        if (entityOnNextPoint instanceof Predator) {
            Location escapeWay = tryRunAway(map, location, newRandomLocation);
            tryEatGrass(map, escapeWay);
            return escapeWay;
        }
        if (!canMoveInto(entityOnNextPoint)) {
            return location;
        }
        tryEatGrass(map, newRandomLocation);

        return newRandomLocation;
        //todo
        //3. иначе шагает случайно. при этом заодно проверяет "куда" шагает: +
        //1. убегает от хищника (может и перекусить, если так совпадет), +
        //2.5 стоит на месте
        //2. ест траву рядом +
        //!!! 4. (сильно позже) - добавить скорость И Вижн у существ.
    }

    private void tryEatGrass(WorldMap map, Location newLocation) {
        Entity entity = map.getEntityByLocation(newLocation);
        if (entity instanceof Grass grass) {
            int nutrition = grass.getNutrition();
            Random random = new Random();

            int minGain = Math.max(1, nutrition / 2);
            int maxGain = nutrition;

            int gain = random.nextInt(maxGain - minGain + 1) + minGain;
            this.setHp(this.getHp() + gain);

            consumedGrass++;
/*
            map.removeEntity(newLocation);
*/
        }
    }

    private boolean isFree(WorldMap map, Location location) {
        if (location.x() < 0 || location.x() >= map.getWidth()) {
            return false;
        }
        if (location.y() < 0 || location.y() >= map.getHeight()) {
            return false;
        }
        return true;
    }

    private Location moveOutsideDangerDirection(char primary, Location herbivoreLocation) {
        return switch (primary) {
            case 'D' -> new Location(herbivoreLocation.x(), herbivoreLocation.y() + 1);
            case 'U' -> new Location(herbivoreLocation.x(), herbivoreLocation.y() - 1);
            case 'R' -> new Location(herbivoreLocation.x() + 1, herbivoreLocation.y());
            case 'L' -> new Location(herbivoreLocation.x() - 1, herbivoreLocation.y());
            default -> herbivoreLocation;
        };
    }

    private Location tryRunAway(WorldMap map, Location herbivoreLocation, Location predatorLocation) {
        int dx = predatorLocation.x() - herbivoreLocation.x();
        int dy = predatorLocation.y() - herbivoreLocation.y();
        Location location = herbivoreLocation;
        char primary = 'A';
        List<Character> alternatives = null;
        if (dy < 0) {
            primary = 'D';
            alternatives = List.of('L', 'R');
        } else if (dy > 0) {
            primary = 'U';
            alternatives = List.of('L', 'R');
        } else if (dx < 0) {
            primary = 'R';
            alternatives = List.of('U', 'D');
        } else if (dx > 0) {
            primary = 'L';
            alternatives = List.of('U', 'D');
        }
        Location primaryOutsideDangerDirection = moveOutsideDangerDirection(primary, herbivoreLocation);
        if (isFree(map, primaryOutsideDangerDirection)
                && canMoveInto(map.getEntityByLocation(primaryOutsideDangerDirection))) {
            return primaryOutsideDangerDirection;
        } else {
            for (Character alternativeDirections : alternatives) {
                Location secondaryOutsideDangerDirection =
                        moveOutsideDangerDirection(alternativeDirections, herbivoreLocation);
                if (isFree(map, secondaryOutsideDangerDirection)
                        && canMoveInto(map.getEntityByLocation(secondaryOutsideDangerDirection))) {
                    return secondaryOutsideDangerDirection;
                }
            }
        }
        return herbivoreLocation;
    }

    @Override
    public boolean canMoveInto(Entity target) {
        return !(target instanceof Rock
                || target instanceof Tree
                || target instanceof Herbivore
                || target instanceof Predator);
    }
}
