package org.entity;

import org.map.Location;
import org.map.WorldMap;
import org.map.path.MapAndGoal;
import org.map.path.PathFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Predator extends Creature {
    private static final Logger log = LoggerFactory.getLogger(Predator.class);

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

    private KillResult tryKill(Location predatorLoc, Location targetLoc, Herbivore herbivore) {
        if (herbivore == null || herbivore.isDead()) {
            return new KillResult(predatorLoc, false);
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

            log.debug("[KILL] {} killed {} at {}, gain={}, hp={}->{}",
                    this, herbivore, targetLoc, killHp, selfBefore, this.getHp());
            return new KillResult(targetLoc, true);
        }
        return new KillResult(predatorLoc, false);
    }

    /**
     * @deprecated Случайное блуждание. Оставлено для совместимости/тестов.
     * Использовать {@link #makeMove(WorldMap, Location, PathFinder)}, который
     * умеет блуждать если не нашел цель и идти к ближайшей достижимой цели-травоядному, если таковая достижима.
     */
    @Deprecated(forRemoval = false)
    public Location makeMove(WorldMap map, Location location) {
        Location newRandomLocation;
        int turn = 0;
        int speed = getSpeed();
        boolean isNextMovePossible = false;
        do {
            newRandomLocation = getRandomLocation(map, location);

            Entity entityOnNextPoint = map.getEntityByLocation(newRandomLocation);
            if (entityOnNextPoint instanceof Herbivore herbivore) {
                newRandomLocation = tryKill(location, newRandomLocation, herbivore).finalLocation();
            } else if (!canMoveInto(entityOnNextPoint)) {
                newRandomLocation = location;
            } else {
                isNextMovePossible = true;
            }
            turn++;
        } while (turn < speed && isNextMovePossible);

        return newRandomLocation;
    }

    @Override
    public Location makeMove(WorldMap map, Location initialLocation, PathFinder pathFinder) {
        Location currentLocation = initialLocation;
        int stepsLeft = getSpeed();

        do {
            MapAndGoal closestMapPath =
                    pathFinder.findClosestPath(map, currentLocation, this, Predator::isLiveHerbivore);

            if (closestMapPath.finalLocation() == null) {
                Location randomLocation = getRandomLocation(map, currentLocation);
                if (canMoveInto(map.getEntityByLocation(randomLocation))) {
                    currentLocation = randomLocation;
                }
                break;
            }

            List<Location> closestPathTillGoal =
                    pathFinder.reconstructPath(closestMapPath.map(), currentLocation, closestMapPath.finalLocation());
            if (closestPathTillGoal.isEmpty()) {
                break;
            }

            if (closestPathTillGoal.size() == 1) {
                Entity entity = map.getEntityByLocation(currentLocation);
                if (entity instanceof Herbivore herbivore) {
                    Location targetLoc = closestPathTillGoal.get(0);
                    currentLocation = tryKill(currentLocation, targetLoc, herbivore).finalLocation();
                    break;
                }
            }

            Location nextLocation = closestPathTillGoal.get(1);
            Entity entityOnNextPoint = map.getEntityByLocation(nextLocation);

            if (entityOnNextPoint instanceof Herbivore herbivore) {
                KillResult killResult = tryKill(currentLocation, nextLocation, herbivore);
                currentLocation = killResult.finalLocation();
                break;
            }

            if (!canMoveInto(entityOnNextPoint)) {
                break;
            }

            currentLocation = nextLocation;
            stepsLeft--;
        } while (stepsLeft > 0);

        return currentLocation;
    }

    public static boolean isLiveHerbivore(Entity e) {
        return (e instanceof Herbivore h) && h.getHp() > 0 && h.getDeathReason() == null;
    }

    record KillResult(Location finalLocation, boolean isTargetKilled) {
    }
}
