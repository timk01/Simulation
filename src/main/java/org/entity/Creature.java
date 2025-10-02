package org.entity;

import org.map.Location;
import org.map.WorldMap;
import org.map.path.PathFinder;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Creature extends Entity {
    private static final AtomicInteger idOverallCounter = new AtomicInteger(0);
    private static final int DEFAULT_SPEED = 1;
    private static final int DEFAULT_HP = 10;
    private final int id;
    private int speed;
    private int hp;
    private DeathReason deathReason;

    public Creature(int speed, int hp) {
        this.id = idOverallCounter.incrementAndGet();
        this.speed = Math.max(speed, DEFAULT_SPEED);
        this.hp = Math.max(hp, DEFAULT_HP);
    }

    public String getIdString() {
        return getClass().getSimpleName() + "#" + id;
    }

    public int getId() {
        return id;
    }

    protected String extraToString() { return ""; }

    @Override
    public String toString() {
        String state = (deathReason == null) ? "alive" : ("dead:" + deathReason);
        String extra = extraToString();
        if (!extra.isEmpty() && !extra.startsWith(",")) {
            extra = ", " + extra;
        }

        return "%s#%d{hp=%d, speed=%d, %s%s}"
                .formatted(getClass().getSimpleName(), id, hp, speed, state, extra);
    }

    public boolean tick() {
        if (isDead()) {
            return false;
        }
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

    public abstract Location makeMove(WorldMap map, Location location, PathFinder pathFinder);

    public abstract boolean canMoveInto(Entity target);

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = Math.max(DEFAULT_SPEED, speed);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }

    protected boolean isInsideMap(WorldMap map, Location location) {
        return location.x() >= 0 && location.x() < map.getWidth()
                && location.y() >= 0 && location.y() < map.getHeight();
    }

    private boolean isMapTiny(WorldMap map) {
        return map.getHeight() <= 1 && map.getWidth() <= 1;
    }

    protected Location getRandomLocation(WorldMap map, Location location) {
        if (isMapTiny(map)) {
            return location;
        }

        Location newLocation;
        do {
            int x = location.x();
            int y = location.y();
            int number = ThreadLocalRandom.current().nextInt(4);
            switch (number) {
                case 0 -> x++;
                case 1 -> x--;
                case 2 -> y++;
                case 3 -> y--;
            }
            newLocation = new Location(x, y);
        } while (!isInsideMap(map, newLocation));

        return newLocation;
    }

    public DeathReason getDeathReason() {
        return deathReason;
    }

    public void setDeathReason(DeathReason deathReason) {
        this.deathReason = deathReason;
    }

    public enum DeathReason {
        STARVATION,
        KILLED_BY_PREDATOR;
    }
}
