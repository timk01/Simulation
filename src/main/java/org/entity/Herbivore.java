package org.entity;

import org.map.Location;
import org.map.WorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Herbivore extends Creature {
    private static final int MIN_SPEED = 1;
    private static final int MAX_SPEED = 3;
    private static final int MIN_HP = 10;
    private static final int MAX_HP = 20;

    private int consumedGrass;
    private Predator killedBy;

    public Herbivore() {
        super(
                ThreadLocalRandom.current().nextInt(MAX_SPEED - MIN_SPEED + 1) + MIN_SPEED,
                ThreadLocalRandom.current().nextInt(MAX_HP - MIN_HP + 1) + MIN_HP
        );
    }

    public Herbivore(int speed, int hp) {
        super(speed, hp);
    }

    @Override
    protected String extraToString() {
        StringBuilder sb = new StringBuilder("eaten=").append(consumedGrass);
        if (killedBy != null) {
            sb.append(", killedBy=").append(killedBy.getIdString());
        }
        return sb.toString();
    }

    public int getConsumedGrass() {
        return consumedGrass;
    }

    public void resetConsumedGrass() {
        this.consumedGrass = 0;
    }

    public Predator getKilledBy() {
        return killedBy;
    }

    public void setKilledBy(Predator killedBy) {
        this.killedBy = killedBy;
    }

    private boolean tryEatGrass(WorldMap map, Location newLocation) {
        boolean hasEaten = false;
        Entity entity = map.getEntityByLocation(newLocation);
        if (entity instanceof Grass grass) {
            int nutrition = grass.getNutrition();

            int minGain = Math.max(1, nutrition / 2);
            int maxGain = nutrition;
            int gain = ThreadLocalRandom.current().nextInt(maxGain - minGain + 1) + minGain;

            int hpBefore = getHp();
            this.setHp(hpBefore + gain);
            consumedGrass++;
            grass.setEatenBy(this);

            System.out.printf("[EAT] %s ate Grass at %s, gain=%d, hp=%d->%d%n",
                    this, newLocation, gain, hpBefore, getHp());
            hasEaten = true;
        }
        return hasEaten;
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

    private boolean ableToStep(WorldMap map, Location location) {
        return isInsideMap(map, location) && canMoveInto(map.getEntityByLocation(location));
    }

    private Location tryRunAway(WorldMap map, Location herbivoreLocation, Location predatorLocation) {
        int dx = predatorLocation.x() - herbivoreLocation.x();
        int dy = predatorLocation.y() - herbivoreLocation.y();
        char primary = 0;
        List<Character> alternatives = new ArrayList<>();
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
        if (ableToStep(map, primaryOutsideDangerDirection)) {
            return primaryOutsideDangerDirection;
        } else {
            for (Character alternativeDirections : alternatives) {
                Location secondaryOutsideDangerDirection =
                        moveOutsideDangerDirection(alternativeDirections, herbivoreLocation);
                if (ableToStep(map, secondaryOutsideDangerDirection)) {
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

    @Override
    public Location makeMove(WorldMap map, Location location) {
        Location currentLocation = location;
        int turn = 0;
        int speed = getSpeed();
        boolean isNextMovePossible;
        boolean ate = false;

        do {
            Location nextLocation = getRandomLocation(map, currentLocation);
            Entity entityOnNextPoint = map.getEntityByLocation(nextLocation);
            if (entityOnNextPoint instanceof Predator) {
                Location escapeLocation = tryRunAway(map, currentLocation, nextLocation);

                boolean escaped = !escapeLocation.equals(currentLocation);
                isNextMovePossible = escaped;

                if (escaped) {
                    currentLocation = escapeLocation;
                    ate = tryEatGrass(map, currentLocation);
                }
            } else if (!canMoveInto(entityOnNextPoint)) {
                isNextMovePossible = false;
            } else {
                currentLocation = nextLocation;
                ate = tryEatGrass(map, currentLocation);
                isNextMovePossible = true;
            }
            turn++;
        } while (turn < speed && isNextMovePossible && !ate);

        return currentLocation;
    }
}
