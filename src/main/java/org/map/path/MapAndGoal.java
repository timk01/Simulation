package org.map.path;

import org.map.Location;

import java.util.Map;

public record MapAndGoal(Map<Location, Location> map, Location finalLocation) {
}