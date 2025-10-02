package org.entity;

import org.map.Location;
import org.map.WorldMap;
import org.map.path.MapAndGoal;
import org.map.path.PathFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    /**
     * @deprecated Случайное блуждание. Оставлено для совместимости/тестов.
     * Использовать {@link #makeMove(WorldMap, Location, PathFinder)}, который
     * умеет бежать от хищников и идти к ближайшей достижимой траве.
     */
    @Deprecated(forRemoval = false)
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

    /**
     * @deprecated Использовать multi-target поиск!:
     * {@link PathFinder#findClosestPath(WorldMap, Location, Creature, java.util.function.Predicate)}
     * + {@link PathFinder#reconstructPath(Map, Location, Location)} (Map, Location, Location)}.
     * Этот же метод делает BFS к КАЖДОЙ травинке и неэффективен.
     */
    @Deprecated(forRemoval = false)
    private Location getFirstLocation(WorldMap map, Location location, PathFinder pathFinder) {
        Location currentLocation = location;
        List<Map.Entry<Location, Entity>> grassList = map.getCells().entrySet().stream()
                .filter(e -> e.getValue() instanceof Grass)
                .toList();
        List<List<Location>> grassPathList = new ArrayList<>();
        for (Map.Entry<Location, Entity> entry : grassList) {
            Map<Location, Location> path = pathFinder.findPath(map, currentLocation, entry.getKey());
            List<Location> locationList = pathFinder.reconstructPath(path, currentLocation, entry.getKey());
            if (!locationList.isEmpty()) {
                grassPathList.add(locationList);
            }
        }
        if (grassPathList.isEmpty()) {
            return currentLocation;
        }
        int size = grassPathList.get(0).size();
        int listNumber = 0;
        for (int i = 1; i < grassPathList.size(); i++) {
            if (size > grassPathList.get(i).size()) {
                size = grassPathList.get(i).size();
                listNumber = i;
            }
        }

        List<Location> shortestRoutreLocationList = grassPathList.get(listNumber);
        return shortestRoutreLocationList.get(1);
    }

    private Location findDangerNearby(WorldMap map, Location currentLocation, PathFinder pathFinder) {
        for (Location neighbourLocation : pathFinder.generateNeighboursInsideMap(currentLocation, map)) {
            if (map.getEntityByLocation(neighbourLocation) instanceof Predator) {
                return neighbourLocation;
            }
        }
        return null;
    }

    @Override
    public Location makeMove(WorldMap map, Location initialLocation, PathFinder pathFinder) {
        Location currentLocation = initialLocation;
        int stepsLeft = getSpeed();

        do {
            Location dangerNearby = findDangerNearby(map, currentLocation, pathFinder);
            if (dangerNearby != null) {
                Location escapeLocation = tryRunAway(map, currentLocation, dangerNearby);
                if (escapeLocation.equals(currentLocation)) {
                    break;
                }
                currentLocation = escapeLocation;
                stepsLeft--;
                if (tryEatGrass(map, currentLocation) || stepsLeft == 0) {
                    break;
                }
                continue;
            }

            MapAndGoal closestMapPath =
                    pathFinder.findClosestPath(map, currentLocation, this, Herbivore::isEntityGrass);

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
                tryEatGrass(map, currentLocation);
                break;
            }

            Location nextLocation = closestPathTillGoal.get(1);
            Entity entityOnNextPoint = map.getEntityByLocation(nextLocation);
            if (!canMoveInto(entityOnNextPoint)) {
                break;
            }

            currentLocation = nextLocation;
            stepsLeft--;
            if (tryEatGrass(map, currentLocation)) {
                break;
            }
        } while (stepsLeft > 0);

        return currentLocation;
    }

    public static boolean isEntityGrass(Entity e) {
        return e instanceof Grass;
    }
}
