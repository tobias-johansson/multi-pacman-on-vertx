package com.seal.vertx;

import com.seal.vertx.domain.*;
import com.seal.vertx.verticles.GameVerticle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.seal.vertx.domain.Direction.DOWN;
import static com.seal.vertx.domain.Direction.LEFT;
import static com.seal.vertx.domain.Direction.RIGHT;
import static com.seal.vertx.domain.Direction.UP;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Engine {
    private GameState current;
    private GameVerticle gameVerticle;
    private Maze maze;

    public Engine(GameVerticle gameVerticle) {
        this.maze = new Maze();
        this.gameVerticle = gameVerticle;
        this.maze = new Maze();
    }

    public void start() {
        this.current = GameState.initial();
    }

    public GameState update(Map<String, Action> actions, List<String> activeUsers) {
        GameState transforming = current;
        transforming = dropInactiveUsers(transforming, activeUsers);
        for (String userId : actions.keySet()) {
            transforming = updateState(transforming, userId, actions.get(userId));
        }
        current = updateMechanics(transforming);
        return current;
    }

    private GameState dropInactiveUsers(GameState transforming, List<String> activeUsers) {
        return new GameState(
                transforming.playerStates.stream()
                        .filter(ps -> activeUsers.contains(ps.player.id))
                        .collect(Collectors.toList())
        );
    }

    private GameState updateMechanics(GameState transforming) {
        List<PlayerState> playerStates = transforming.playerStates;
        List<PlayerState> wallCollidedPlayerStates = wallCollidedPlayerStates(transforming);
        List<Integer> ghostIndices = new ArrayList<>();
        List<Integer> alivePacmanIndices = new ArrayList<>();
        List<Integer> deadPacmen = new ArrayList<>();
        for (int i = 0; i < playerStates.size(); i++) {
            if (playerStates.get(i).player.type == PlayerType.GHOST) {
                ghostIndices.add(i);
            }
            if (playerStates.get(i).player.type == PlayerType.PACMAN && playerStates.get(i).status == Status.ALIVE) {
                alivePacmanIndices.add(i);
            }
        }
        for (int ghost : ghostIndices) {
            for (int pacman : alivePacmanIndices) {
                PlayerState ghost1 = playerStates.get(ghost);
                PlayerState ghost2 = wallCollidedPlayerStates.get(ghost);
                PlayerState pacman1 = playerStates.get(pacman);
                PlayerState pacman2 = wallCollidedPlayerStates.get(pacman);
                if (collides(ghost1, ghost2, pacman1, pacman2)) {
                    deadPacmen.add(pacman);
                }
            }
        }
        List<PlayerState> newPlayerStates = new ArrayList<>();
        for (int i = 0; i < playerStates.size(); i++) {
            PlayerState playerState = wallCollidedPlayerStates.get(i);
            if (playerState.player.type == PlayerType.GHOST) {
                newPlayerStates.add(playerState);
            } else if (playerState.player.type == PlayerType.PACMAN) {
                if (!deadPacmen.contains(i)) {
                    newPlayerStates.add(playerState);
                } else {
                    PlayerState oldState = wallCollidedPlayerStates.get(i);
                    PlayerState deadPacman = new PlayerState(oldState.player, oldState.location, oldState.direction, Status.DEAD);
                    gameVerticle.scheduleReviving(oldState.player.id);
                    newPlayerStates.add(deadPacman);
                }
            } else {
                throw new RuntimeException("Unimplemented player type");
            }
        }
        return new GameState(newPlayerStates);
    }

    private boolean collides(PlayerState ghost1, PlayerState ghost2, PlayerState pacman1, PlayerState pacman2) {
        // takes care of case when chased into wall
        boolean collidesInEndX = collidesSpatial(ghost2.location.x, pacman2.location.x);
        boolean collidesInEndY = collidesSpatial(ghost2.location.y, pacman2.location.y);
        if (collidesInEndX && collidesInEndY) {
            return true;
        }
        TimeInterval total = new TimeInterval(0,Constants.timeStep);
        TimeInterval collidingX = collidingInterval(ghost1.location.x, pacman1.location.x, ghost1.direction.getX(), pacman1.direction.getX());
        TimeInterval collidingY = collidingInterval(ghost1.location.y, pacman1.location.y, ghost1.direction.getY(), pacman1.direction.getY());
        TimeInterval result = total.intersect(collidingX).intersect(collidingY);
        return result.endTime > result.startTime;
    }

    private TimeInterval collidingInterval(float pos1, float pos2, float d1, float d2) {
        float d = d1 - d2;
        float t1 = (pos2 - (pos1 + Constants.playerWidth)) / d;
        float t2 = ((pos2 + Constants.playerWidth) - pos1) / d;
        return new TimeInterval((long)Math.min(t1, t2), (long)Math.max(t1, t2));
    }

    private boolean collidesSpatial(float x1, float x2) {
        return Math.abs(x1-x2) < Constants.playerWidth;
    }


    private List<PlayerState> wallCollidedPlayerStates(GameState transforming) {
        return transforming.playerStates.stream().map(ps -> {
            if (ps.status == Status.DEAD) {
                return ps;
            }
            float x = Math.min(1.0f - Constants.playerWidth, Math.max(0.0f, ps.location.x + ps.direction.getX() * Constants.timeStep));
            float y = Math.min(1.0f - Constants.playerWidth, Math.max(0.0f, ps.location.y + ps.direction.getY() * Constants.timeStep));
            Location check = new Location(x,y);
            Location adjusted = (maze.checkWallCollision(ps.location, check)) ? ps.location : check;
            return new PlayerState(ps.player, adjusted, ps.direction, ps.status);
        }).collect(Collectors.toList());
    }

    private GameState updateState(GameState transforming, String userId, Action action) {
    	if (null == action) {
    		return transforming;
    	}
        List<PlayerState> newPlayers = new ArrayList<>();
        Optional<PlayerState> player = current.playerStates.stream().filter(ps -> ps.player.id.equals(userId)).findFirst();
        if (player.isPresent()) {
            switch (action) {
                case UP:
                    return new GameState(transforming.playerStates.stream().map(turn(userId, UP)).collect(Collectors.toList()));
                case DOWN:
                    return new GameState(transforming.playerStates.stream().map(turn(userId, DOWN)).collect(Collectors.toList()));
                case LEFT:
                    return new GameState(transforming.playerStates.stream().map(turn(userId, LEFT)).collect(Collectors.toList()));
                case RIGHT:
                    return new GameState(transforming.playerStates.stream().map(turn(userId, RIGHT)).collect(Collectors.toList()));
                case QUIT:
                    return new GameState(transforming.playerStates.stream().filter(ps -> !ps.player.id.equals(userId)).collect(Collectors.toList()));
                case REVIVE:
                    return new GameState(transforming.playerStates.stream().map(revive(userId, transforming)).collect(Collectors.toList()));
                default:
                    return transforming;
            }
        } else {
            switch (action) {
                case JOIN:
                    Player newPlayer = Player.randomPlayer(transforming, userId);
                    PlayerState newState = PlayerState.randomState(transforming, newPlayer, maze);
                    newPlayers.add(newState);
                    newPlayers.addAll(transforming.playerStates);
                    return new GameState(newPlayers);
                default:
                    return transforming;
            }
        }
    }

    Function<PlayerState, PlayerState> revive(String id, GameState gameState) {
        return ps -> {
            if (!ps.player.id.equals(id)) {
                return ps;
            } else {
                return PlayerState.randomState(gameState, ps.player, maze);
            }
        };
    }

    Function<PlayerState, PlayerState> turn(String id, Direction dir) {
        return ps -> {
            if (ps.player.id.equals(id) && ps.status == Status.ALIVE) {
                return new PlayerState(ps.player, ps.location, dir, ps.status);
            } else {
                return ps;
            }
        };
    }

}
