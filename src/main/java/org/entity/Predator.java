package org.entity;

import org.map.Location;
import org.map.WorldMap;

import java.util.concurrent.ThreadLocalRandom;

public class Predator extends Creature {

    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 4;
    private static final int MIN_HP = 15;
    private static final int MAX_HP = 25;
    private static final int MIN_ATTACK = 5;
    private static final int MAX_ATTACK = 15;

    private final int attackStrength;

    public Predator() {
        super(
                ThreadLocalRandom.current().nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED,
                ThreadLocalRandom.current().nextInt(MAX_HP - MIN_HP + 1) + MIN_HP
        );
        this.attackStrength = ThreadLocalRandom.current().nextInt(MAX_ATTACK - MIN_ATTACK + 1) + MIN_ATTACK;
    }

    public Predator(int speed, int hp, int attackStrength) {
        super(speed, hp);
        this.attackStrength = attackStrength;
    }
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

    @Override
    protected String extraToString() {
        return "atk=" + attackStrength;
    }

    private Location tryKill(Location predatorLoc, Location targetLoc, Herbivore herbivore) {
        if (herbivore.isDead()) {
            return predatorLoc;
        }

        int herbivoreHp = herbivore.getHp();
        herbivore.setHp(Math.max(0, herbivoreHp - this.attackStrength));

        if (herbivore.getHp() == 0) {
            herbivore.setDeathReason(DeathReason.KILLED_BY_PREDATOR);
            herbivore.setKilledBy(this);

            int selfBefore = this.getHp();
            int minGain = Math.max(1, this.attackStrength / 2);
            int maxGain = Math.max(minGain, herbivoreHp / 2);
            int killHp = ThreadLocalRandom.current().nextInt(maxGain - minGain + 1) + minGain;
            this.setHp(this.getHp() + killHp);

            System.out.printf("[EAT] %s killed %s at %s, gain=%d, hp=%d->%d%n",
                    this, herbivore, targetLoc, killHp, selfBefore, this.getHp());
            return targetLoc;
        }
        return predatorLoc;
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
                newRandomLocation = tryKill(location, newRandomLocation, herbivore);
            } else if (!canMoveInto(entityOnNextPoint)) {
                newRandomLocation = location;
            } else {
                isNextMovePossible = true;
            }
            turn++;
        } while (turn < speed && isNextMovePossible);

        return newRandomLocation;
    }
}
