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

    //private int killedEntities;

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

/*    public int getKilledEntities() {
        return killedEntities;
    }

    public void resetKilledEntities() {
        this.killedEntities = 0;
    }*/

    public int getAttackStrength() {
        return attackStrength;
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
        Location newRandomLocation;
        int turn = 0;
        int speed = getSpeed();
        boolean isNextMovePossible = false;
        do {
            newRandomLocation = getRandomLocation(map, location);

            Entity entityOnNextPoint = map.getEntityByLocation(newRandomLocation);
            if (entityOnNextPoint instanceof Herbivore herbivore) {
                newRandomLocation = tryKill(map, location, newRandomLocation, herbivore);
            } else if (!canMoveInto(entityOnNextPoint)) {
                newRandomLocation = location;
            } else {
                isNextMovePossible = true;
            }
            turn++;
        } while (turn < speed && isNextMovePossible);

        return newRandomLocation;
        // todo
        // 1. если вблизи есть травоядное — атаковать +
        // 2. если нет — случайно переместиться +
        // !!! 3. добавить скорость И вижн
    }

    private Location tryKill(WorldMap map, Location predatorLoc, Location targetLoc, Herbivore herbivore) {
        int herbivoreHp = herbivore.getHp();
        herbivore.setHp(Math.max(0, herbivoreHp - this.attackStrength));

        Random random = new Random();
        if (herbivore.getHp() == 0) {
            herbivore.setDeathReason(DeathReason.KILLED_BY_PREDATOR);
            //killedEntities++;
            herbivore.setKilledBy(this);
            System.out.println("BEFORE " + "[EAT] Predator at " + targetLoc
                    + " killed " + herbivore.getClass().getSimpleName()
                    + ", predator hp=" + this.getHp());
            int minGain = Math.max(1, this.attackStrength / 2);
            int maxGain = Math.max(minGain, herbivoreHp / 2);
            int killHp = random.nextInt(maxGain - minGain + 1) + minGain;
            this.setHp(this.getHp() + killHp);
            System.out.println("AFTER " + "[EAT] Predator at " + targetLoc
                    + " killed " + herbivore.getClass().getSimpleName()
                    + ", predator hp=" + this.getHp());
            //map.removeEntity(targetLoc);
            return targetLoc;
        }
        return predatorLoc;
    }
}
