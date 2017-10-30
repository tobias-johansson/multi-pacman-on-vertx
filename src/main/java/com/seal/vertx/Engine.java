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

    public Engine(GameVerticle gameVerticle, Maze maze) {
        this.maze = maze;
        this.gameVerticle = gameVerticle;
    }

    public void start() {
        this.current = GameState.initial();
    }

    public GameState update(Map<String, Action> actions, List<String> activeUsers) {
        GameState transforming = current;
        //transforming = dropInactiveUsers(transforming, activeUsers);
        for (String userId : actions.keySet()) {
            Action action = actions.get(userId);
            transforming = updateState(transforming, userId, action);
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
                if (CollisionDetection.collides(ghost1, ghost2, pacman1, pacman2)) {
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
                    PlayerState deadPacman = new PlayerState(oldState.player, oldState.location, oldState.direction,
                            oldState.desiredDirection, Status.DEAD);
                    gameVerticle.scheduleReviving(oldState.player.id);
                    newPlayerStates.add(deadPacman);
                }
            } else {
                throw new RuntimeException("Unimplemented player type");
            }
        }
        return new GameState(newPlayerStates);
    }

    private List<PlayerState> wallCollidedPlayerStates(GameState transforming) {
        return transforming.playerStates.stream().map(ps -> {
            if (ps.status == Status.DEAD) {
                return ps;
            }
            if (warp(ps.location, ps.direction)) {
                Location warped = new Location(maze.grid.length * Constants.playerWidth - ps.location.x, ps.location.y);
                ps = new PlayerState(ps.player, warped, ps.direction, ps.desiredDirection, ps.status);
            }
            float timeToWall = maze.timeToWallImpact(ps.location, ps.direction);
            Direction newDirection = ps.direction;
            if (ps.desiredDirection != ps.direction) {
                float timeToTurnPoint = maze.timeToTurnPoint(ps.location, ps.direction, ps.desiredDirection);
                if (timeToTurnPoint <= timeToWall) {
                    timeToWall = timeToTurnPoint;
                    newDirection = ps.desiredDirection;
                }
            }
            float adjustedX = ps.location.x + timeToWall * ps.direction.getX();
            float adjustedY = ps.location.y + timeToWall * ps.direction.getY();
            Location adjusted = new Location(adjustedX, adjustedY);
            return new PlayerState(ps.player, adjusted, newDirection, ps.desiredDirection, ps.status);
        }).collect(Collectors.toList());
    }

    private boolean warp(Location adjusted, Direction direction) {
        if (direction == RIGHT && adjusted.x > (maze.grid.length-1)*Constants.playerWidth - Constants.wallEpsilon * Constants.playerWidth) {
            return true;
        }
        if (direction == LEFT && adjusted.x < Constants.wallEpsilon * Constants.playerWidth) {
            return true;
        }
        return false;
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

    Function<PlayerState, PlayerState> turn(String id, Direction desiredDirection) {
        return ps -> {
            if (ps.player.id.equals(id) && ps.status == Status.ALIVE) {
                Direction direction = desiredDirection.isParallell(ps.direction) ? desiredDirection : ps.direction;
                return new PlayerState(ps.player, ps.location, direction, desiredDirection, ps.status);
            } else {
                return ps;
            }
        };
    }
}
