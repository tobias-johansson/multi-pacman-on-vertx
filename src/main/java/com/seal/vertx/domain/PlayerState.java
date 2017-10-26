package com.seal.vertx.domain;

import java.util.List;
import java.util.Random;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class PlayerState {
    public final Player player;
    public final Location location;
    public final Direction direction;
    private static final Random rnd = new Random();

    public PlayerState(Player player, Location location, Direction direction) {
        this.player = player;
        this.location = location;
        this.direction = direction;
    }

    public static PlayerState randomState(GameState transforming, Player newPlayer) {
        List<Location> spawningLocations = transforming.maze.getSpawningLocations();
        int randomSpawn = rnd.nextInt(spawningLocations.size());
        Location location = spawningLocations.get(randomSpawn);
        int randomDirection = rnd.nextInt(4);
        Direction direction = Direction.RIGHT;
        switch (randomDirection) {
            case 0:
                direction = Direction.UP;
                break;
            case 1:
                direction = Direction.DOWN;
                break;
            case 2:
                direction = Direction.LEFT;
                break;
            case 3:
                direction = Direction.RIGHT;
                break;
            default:
                break;
        }
        return new PlayerState(newPlayer, location, direction);
    }
}
