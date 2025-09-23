package org.entity;

import org.map.Location;
import org.map.WorldMap;

import java.util.Random;

public class Predator extends Creature {

    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 4;
    private static final int MIN_HP = 15;
    private static final int MAX_HP = 25;
    private static final int MIN_ATTACK = 5;
    private static final int MAX_ATTACK = 15;

    private final int attackStrength;

    private int killedEntities;

    public Predator(Random random) {
        super(
                random.nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED,
                random.nextInt(MAX_HP - MIN_HP + 1) + MIN_HP
        );
        this.attackStrength = random.nextInt(MAX_ATTACK - MIN_ATTACK + 1) + MIN_ATTACK;
    }

    public Predator(int speed, int hp, int attackStrength) {
        super(speed, hp);
        this.attackStrength = attackStrength;
    }

    public int getKilledEntities() {
        return killedEntities;
    }

    public void resetKilledEntities() {
        this.killedEntities = 0;
    }

    public int getAttackStrength() {
        return attackStrength;
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
            }
            ;
        } while (!isFree(map, newLocation));
        return newLocation;
    }

    @Override
    public boolean canMoveInto(Entity target) {
        return !(target instanceof Rock
                || target instanceof Tree
                || target instanceof Grass
                || target instanceof Predator);
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

    @Override
    public Location makeMove(WorldMap map, Location location) {
        Location newRandomLocation = getRandomLocation(map, location);

        Entity entityOnNextPoint = map.getEntityByLocation(newRandomLocation);
        if (entityOnNextPoint instanceof Herbivore herbivore) {
            return tryKill(map, location, newRandomLocation, herbivore);
        }
        if (!canMoveInto(entityOnNextPoint)) {
            return location;
        }
        return newRandomLocation;
        // todo
        // 1. если вблизи есть травоядное — атаковать +
        // 2. если нет — случайно переместиться +
        // !!! 3. добавить скорость И вижн
    }

    private Location tryKill(WorldMap map, Location location, Location newRandomLocation, Herbivore herbivore) {
        int herbivoreHp = herbivore.getHp();
        herbivore.setHp(Math.max(0, herbivoreHp - this.attackStrength));

        Random random = new Random();
        if (herbivore.getHp() == 0) {
            killedEntities++;
            int minGain = Math.max(1, this.attackStrength / 2);
            int maxGain = Math.max(minGain, herbivoreHp / 2);
            int killHp = random.nextInt(maxGain - minGain + 1) + minGain;
            this.setHp(this.getHp() + killHp);
/*            map.placeEntity(newRandomLocation, this);
            map.removeEntity(location);*/
            return newRandomLocation;
        }
        return location;
    }
}
