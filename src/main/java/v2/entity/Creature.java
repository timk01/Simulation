package v2.entity;

import v2.map.Location;
import v2.map.WorldMap;

public abstract class Creature extends Entity {
    private int speed;
    private int hp;

    public void makeMove(WorldMap map, Location location) {
        //map.getCurrentCoordinates(this)
    }
}
