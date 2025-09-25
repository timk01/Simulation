package org.entity;

import org.map.Location;
import org.map.WorldMap;

import java.util.Objects;
import java.util.Random;

public abstract class Creature extends Entity {
    private static int idOverallCounter = 0;
    private int id;
    private int speed;
    private int hp;
    private int turn;
    private DeathReason deathReason;

    public Creature(int speed, int hp) {
        this.speed = speed;
        this.hp = hp;
        this.id = ++idOverallCounter;
    }

    public String getIdString() {
        return getClass().getSimpleName() + "#" + id;
    }

    @Override
    public String toString() {
        return getIdString();
    }

    public boolean tick() {
        turn++;

        if (hp > 0) {
            hp--;
        }

        if (hp <= 0 && deathReason == null) {
            deathReason = DeathReason.STARVATION;
        }

        return hp <= 0;
    }

    public boolean isDead() {
        return this.hp <= 0 || this.deathReason != null;
    }

    public abstract Location makeMove(WorldMap map, Location location);

    public abstract boolean canMoveInto(Entity target);

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    protected Location getRandomLocation(WorldMap map, Location location) {
        int x;
        int y;
        Location newLocation;
        Random random = new Random();
        do {
            x = location.x();
            y = location.y();
            int number = random.nextInt(4);
            switch (number) {
                case 0 -> x++;
                case 1 -> x--;
                case 2 -> y++;
                case 3 -> y--;
                default -> {
                    return location;
                }
            }
            newLocation = new Location(x, y);
        } while (!isInsideMap(map, newLocation));
        return newLocation;
    }

    protected boolean isInsideMap(WorldMap map, Location location) {
        return location.x() >= 0 && location.x() < map.getWidth()
                && location.y() >= 0 && location.y() < map.getHeight();
    }

    public DeathReason getDeathReason() {
        return deathReason;
    }

    public void setDeathReason(DeathReason deathReason) {
        this.deathReason = deathReason;
    }

    public enum DeathReason {
        STARVATION,
        KILLED_BY_PREDATOR,
        UNKNOWN;
    }

/*
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Creature creature = (Creature) o;
        return id == creature.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
*/

/*    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Creature creature = (Creature) o;
        return id == creature.id && speed == creature.speed && hp == creature.hp && turn == creature.turn && deathReason == creature.deathReason;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, speed, hp, turn, deathReason);
    }*/
}
