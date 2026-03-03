package v2;

import v2.entity.*;
import v2.map.Location;
import v2.map.WorldMap;
import v2.renderer.Renderer;

public class Main2 {
    public static void main(String[] args) {
        WorldMap worldMap = new WorldMap(10, 10);

        Location locationGrass = new Location(0, 0);
        worldMap.tryAddEntity(locationGrass, new Grass());

        Location locationTree = new Location(0, 5);
        worldMap.tryAddEntity(locationTree, new Tree());

        Location locationRock = new Location(5, 5);
        worldMap.tryAddEntity(locationRock, new Rock());

        Location locationHerbivore = new Location(9, 9);
        worldMap.tryAddEntity(locationHerbivore, new Herbivore());

        Location locationPredator = new Location(7, 7);
        worldMap.tryAddEntity(locationPredator, new Predator());

        Renderer renderer = new Renderer(worldMap);
        renderer.draw();
    }
}
