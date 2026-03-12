package v2.entity;

import v2.map.Location;
import v2.map.WorldMap;
import v2.path.PathFinder;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Creature extends Entity {
    private int speed;
    private int hp = 10;
    private int maxHp = 20;

    List<Location> listOfClosestLocations;

    Location prevLocation;

    public Creature(int speed, int hp, int maxHp) {
        this.speed = speed;
        this.hp = hp;
        this.maxHp = maxHp;
    }

    public Creature() {
    }

    public int getSpeed() {
        return speed;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void makeMove(WorldMap map, Location oldLocation, PathFinder pathFinder) {
        Predicate<Entity> goal = isGoal();
        List<Location> steps = pathFinder.findClosestPath(map, oldLocation, goal);
        Location nextLocation;
        if (!steps.isEmpty() && steps.size() >= 2) {
            nextLocation = steps.get(1);
        } else {
            nextLocation = whereToStep(map, oldLocation);
        }

        if (oldLocation.equals(nextLocation)) {
            return;
        }
        checkLocations(map, oldLocation);
        move(map, nextLocation, oldLocation, goal);
    }

    void move(WorldMap map, Location nextLocation, Location oldLocation, Predicate<Entity> isGoal) {
        boolean isCellFree = map.isCellFree(nextLocation);
        if (isCellFree) {
            map.removeEntity(oldLocation);
            map.tryAddEntity(nextLocation, this);
        } else if (isEntityPresent(map, nextLocation) && isGoal.test(getEntity(map, nextLocation))) {
            System.out.println("before interactWithTarget");
            if (interactWithTarget(map, nextLocation, getEntity(map, nextLocation))) {
                System.out.println("inside interactWithTarget: (hardcoded) " + true);
                map.removeEntity(oldLocation);
                System.out.println("is oldLocRemoved: " + map.getEntity(oldLocation));
                map.tryAddEntity(nextLocation, this);
                System.out.println("is newLoc, placed: " + map.getEntity(nextLocation));

            }
        } else if (isEntityPresent(map, nextLocation) && !isGoal.test(getEntity(map, nextLocation))) {
            return;
        }
    }

    private Entity getEntity(WorldMap map, Location nextLocation) {
        return map.getEntity(nextLocation).get();
    }

    private boolean isEntityPresent(WorldMap map, Location nextLocation) {
        return map.getEntity(nextLocation).isPresent();
    }

    private void checkLocations(WorldMap map, Location oldLocation) {
        Optional<Entity> entity = map.getEntity(oldLocation);
        if (!(entity.isPresent() && entity.get().equals(this))) {
            throw new IllegalStateException("the creature is expected at " + oldLocation);
        }
    }

    private Location whereToStep(WorldMap map, Location oldLocation) {
        listOfClosestLocations = oldLocation.neighbourLocations();
        Collections.shuffle(listOfClosestLocations);
        List<Location> validMoves = makeValidMovesList(map);
        validMoves = filterValidMoves(validMoves);
        if (validMoves.isEmpty()) {
            return oldLocation;
        }
        Collections.shuffle(validMoves);
        prevLocation = oldLocation;
        return validMoves.get(0);
    }

    private List<Location> makeValidMovesList(WorldMap map) {
        List<Location> validMoves = new ArrayList<>();
        for (Location nearby : listOfClosestLocations) {
            if (map.isInsideMap(nearby) && map.isCellFree(nearby)) {
                validMoves.add(nearby);
            }
        }
        return validMoves;
    }

    private List<Location> filterValidMoves(List<Location> validMoves) {
        if (prevLocation != null && validMoves.size() > 1) {
            validMoves = validMoves.stream()
                    .filter(location1 -> !location1.equals(prevLocation)).collect(Collectors.toList());
        }
        return validMoves;
    }

    public Predicate<Entity> isGoal() {
        System.out.println("isGoal: " + Entity.class);
        return entity -> false;
    }

    abstract boolean interactWithTarget(WorldMap map, Location targetEntityLocation, Entity target);

    public void heal(int healAmount) {
        hp += healAmount;
        if (hp >= maxHp) {
            hp = maxHp;
        }
    }

    public void takeDamage(int damageAmount) {
        hp -= damageAmount;
    }

    public boolean isDead() {
        return hp <= 0;
    }
}
