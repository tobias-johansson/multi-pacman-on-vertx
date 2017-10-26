package com.seal.vertx;

import com.seal.vertx.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Engine {
    private GameState current;

    public Engine() {
    }

    public void start() {
        this.current = GameState.initial();
    }

    public GameState update(Map<String, Action> actions) {
        GameState transforming = current;
        for (String userId : actions.keySet()) {
            transforming = updateState(transforming, userId, actions.get(userId));
        }
        current = updateMechanics(transforming);
        return current;
    }

    private GameState updateMechanics(GameState transforming) {
        List<PlayerState> playerStates = transforming.playerStates;
        List<PlayerState> wallCollidedPlayerStates = wallCollidedPlayerStates(transforming);
        List<Integer> ghostIndices = new ArrayList<>();
        List<Integer> pacmanIndices = new ArrayList<>();
        List<Integer> deadPacmen = new ArrayList<>();
        for (int i = 0; i < playerStates.size(); i++) {
            if (playerStates.get(i).player.type == PlayerType.GHOST) {
                ghostIndices.add(i);
            }
            if (playerStates.get(i).player.type == PlayerType.PACMAN) {
                pacmanIndices.add(i);
            }
        }
        for (int ghost : ghostIndices) {
            for (int pacman : pacmanIndices) {
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
                    newPlayerStates.add(deadPacman);
                }
            } else {
                throw new RuntimeException("Unimplemented player type");
            }
        }
        return transforming;
    }

    private boolean collides(PlayerState ghost1, PlayerState ghost2, PlayerState pacman1, PlayerState pacman2) {
        // takes care of case when chased into wall
        boolean collidesInEndX = collidesSpatial(ghost2.location.x, pacman2.location.x);
        boolean collidesInEndY = collidesSpatial(ghost2.location.y, pacman2.location.y);
        if (collidesInEndX && collidesInEndY) {
            return true;
        }
        TimeInterval total = new TimeInterval(0,Constants.timeStep);
        TimeInterval collidingX = collidingInterval(ghost1.location.x, pacman1.location.x, ghost1.direction.getX(),
                pacman1.direction.getX());
        TimeInterval collidingY = collidingInterval(ghost1.location.y, pacman1.location.y, ghost1.direction.getY(),
                pacman1.direction.getY());
        TimeInterval result = total.intersect(collidingX).intersect(collidingY);
        return result.endTime > result.startTime;
    }

    private TimeInterval collidingInterval(float pos1, float pos2, int d1, int d2) {
        pos1 + t1*d1
        return null;
    }

    private boolean collidesSpatial(float x1, float x2) {
        return Math.abs(x1-x2) < Constants.playerWidth;
    }


    private List<PlayerState> wallCollidedPlayerStates(GameState transforming) {
        return null;
    }

    private GameState updateState(GameState transforming, String userId, Action action) {
        List<PlayerState> newPlayers;
        switch (action) {
            case UP:
                return transforming;
            case DOWN:
                return transforming;
            case LEFT:
                return transforming;
            case RIGHT:
                return transforming;
            case JOIN:
                boolean playerExists = current.playerStates.stream().anyMatch(ps -> ps.player.id.equals(userId));
                if (playerExists) {
                    return transforming;
                }
                newPlayers = new ArrayList<>();
                Player newPlayer = Player.randomPlayer(transforming, userId);
                PlayerState newState = PlayerState.randomState(transforming, newPlayer);
                newPlayers.add(newState);
                newPlayers.addAll(transforming.playerStates);
                return new GameState(newPlayers, transforming.maze);
            case QUIT:
                newPlayers = transforming.playerStates.stream().filter(ps -> !ps.player.id.equals(userId))
                        .collect(Collectors.toList());
                return new GameState(newPlayers, transforming.maze);
            default:
                throw new RuntimeException("Unimplemented action");
        }
    }

}
