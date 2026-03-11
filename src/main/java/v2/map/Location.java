package v2.map;

import java.util.ArrayList;
import java.util.List;

public record Location(int x, int y) {

    private static final List<Direction> DIRECTIONS = List.of(
            Direction.RIGHT, Direction.LEFT, Direction.UP, Direction.DOWN
    );

    public List<Location> neighbourLocations() {
        List<Location> nearbyLoc = new ArrayList<>();
        for (Direction direction : DIRECTIONS) {
            nearbyLoc.add(this.shift(direction));
        }
        return nearbyLoc;
    }

    private Location shift(Direction direction) {
        return new Location(x + direction.getX(), y + direction.getY());
    }
}
