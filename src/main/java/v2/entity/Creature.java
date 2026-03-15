package v2.entity;

import v2.map.Location;
import v2.map.WorldMap;
import v2.path.PathFinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Creature extends Entity {
    private static final int STEPS_LIMITER = 2;

    private final int speed;
    private int hp;
    private final int maxHp;

    private Location prevLocation;

    public Creature(int speed, int hp, int maxHp) {
        this.speed = speed;
        this.hp = hp;
        this.maxHp = maxHp;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHp() {
        return hp;
    }

    public void makeMove(WorldMap worldMap, Location oldLocation, PathFinder pathFinder) {
        Predicate<Entity> goal = isGoal();
        Location currentLocation = oldLocation;
        int stepsLeft = getSpeed();

        do {
            List<Location> steps = pathFinder.findPath(worldMap, currentLocation, goal);

            Location nextLocation;
            if (!steps.isEmpty() && steps.size() >= STEPS_LIMITER) {
                nextLocation = steps.get(1);
            } else {
                nextLocation = roam(worldMap, currentLocation);
            }

            if (nextLocation.equals(currentLocation)) {
                return;
            }

            checkLocations(worldMap, currentLocation);

            move(worldMap, nextLocation, currentLocation, goal);

            StepResult result = checkStepResult(worldMap, nextLocation, goal);
            if (result == StepResult.MOVED) {
                currentLocation = nextLocation;
                stepsLeft--;
            } else if (result == StepResult.ATTACKED) {
                stepsLeft--;
            } else if (result == StepResult.NO_ACTION) {
                break;
            }
        } while (stepsLeft > 0);
    }

    private Location roam(WorldMap worldMap, Location oldLocation) {
        List<Location> listOfClosestLocations = oldLocation.neighbourLocations();
        Collections.shuffle(listOfClosestLocations);
        List<Location> validMoves = makeValidMovesList(worldMap, listOfClosestLocations);
        validMoves = filterValidMoves(validMoves);
        if (validMoves.isEmpty()) {
            return oldLocation;
        }
        Collections.shuffle(validMoves);
        prevLocation = oldLocation;
        return validMoves.get(0);
    }

    private List<Location> makeValidMovesList(WorldMap worldMap, List<Location> listOfClosestLocations) {
        List<Location> validMoves = new ArrayList<>();
        for (Location nearby : listOfClosestLocations) {
            if (worldMap.isInsideMap(nearby) && worldMap.isCellFree(nearby)) {
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

    private void checkLocations(WorldMap worldMap, Location oldLocation) {
        Optional<Entity> entity = worldMap.getEntity(oldLocation);
        if (!(entity.isPresent() && entity.get().equals(this))) {
            throw new IllegalStateException("the creature is expected at " + oldLocation);
        }
    }

    private void move(WorldMap worldMap, Location nextLocation, Location oldLocation, Predicate<Entity> isGoal) {
        if (worldMap.isCellFree(nextLocation)) {
            worldMap.removeEntity(oldLocation);
            worldMap.tryAddEntity(nextLocation, this);
        } else if (isEntityPresent(worldMap, nextLocation) && isGoal.test(getEntity(worldMap, nextLocation))) {
            if (interactWithTarget(worldMap, nextLocation, getEntity(worldMap, nextLocation))) {
                worldMap.removeEntity(oldLocation);
                worldMap.tryAddEntity(nextLocation, this);
            }
        } else if (isEntityPresent(worldMap, nextLocation) && !isGoal.test(getEntity(worldMap, nextLocation))) {
            return;
        }
    }

    private boolean isEntityPresent(WorldMap worldMap, Location nextLocation) {
        return worldMap.getEntity(nextLocation).isPresent();
    }

    private Entity getEntity(WorldMap worldMap, Location nextLocation) {
        return worldMap.getEntity(nextLocation).get();
    }

    abstract boolean interactWithTarget(WorldMap worldMap, Location targetEntityLocation, Entity target);

    private StepResult checkStepResult(WorldMap worldMap, Location nextLocation, Predicate<Entity> goal) {
        Optional<Entity> nextEntity = worldMap.getEntity(nextLocation);
        if (nextEntity.isPresent() && nextEntity.get().equals(this)) {
            return StepResult.MOVED;
        } else if (nextEntity.isPresent() && goal.test(nextEntity.get())) {
            return StepResult.ATTACKED;
        } else {
            return StepResult.NO_ACTION;
        }
    }

    private enum StepResult {
        MOVED,
        ATTACKED,
        NO_ACTION

    }

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
