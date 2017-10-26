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
        locations.add( new Location(0.0f,0.0f));
        locations.add( new Location(0.2f,0.2f));
        locations.add( new Location(0.4f,0.4f));
        locations.add( new Location(0.6f,0.6f));
        locations.add( new Location(0.8f,0.8f));
        return locations;
    }
}
