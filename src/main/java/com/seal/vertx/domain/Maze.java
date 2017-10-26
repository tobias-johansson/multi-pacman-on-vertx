package com.seal.vertx.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Maze {
    public Maze() {
    }

    public List<Location> getSpawningLocations() {
        List<Location> locations = new ArrayList<>();
        Location location1 = new Location(0.2f,0.2f);
        locations.add(location1);
        return locations;
    }
}
