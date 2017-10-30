package com.seal.vertx.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.seal.vertx.CollisionDetection;
import com.seal.vertx.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Maze {
    private static final Logger LOG = LoggerFactory.getLogger(Maze.class);

    public final boolean[][] grid;
    public final Set[][] blockedDirections;

    /**
     *   *******
     *   **....*
     *   **.****
     *   **.****
     */
    public Maze() {
        SrcMazeData src = parseMazeFile();
        this.grid = gridFromSrc(src);
        this.blockedDirections = blockedDirectionsFromGrid(this.grid);
    }

    private Set<Direction>[][] blockedDirectionsFromGrid(boolean[][] grid) {
        int cols = grid.length;
        int rows = grid[0].length;
        Set[][] blockedDirections = new Set[cols][rows];
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                addBlockedDirection(Direction.UP, col, row, col, row - 1, blockedDirections, grid, cols, rows);
                addBlockedDirection(Direction.DOWN, col, row, col, row + 1, blockedDirections, grid, cols, rows);
                addBlockedDirection(Direction.RIGHT, col, row, col + 1, row, blockedDirections, grid, cols, rows);
                addBlockedDirection(Direction.LEFT, col, row, col - 1, row, blockedDirections, grid, cols, rows);
            }
        }
        return blockedDirections;
    }

    private void addBlockedDirection(Direction dir, int col, int row, int colNeigh, int rowNeigh,
                                     Set[][] blockedDirections, boolean[][] grid, int cols, int rows) {
        if (colNeigh >= 0 && rowNeigh >= 0 && colNeigh < cols && rowNeigh < rows) {
            if (grid[colNeigh][rowNeigh]) {
                if (blockedDirections[col][row] == null) {
                    blockedDirections[col][row] = new HashSet();
                }
                blockedDirections[col][row].add(dir);
            }
        }

    }

    public List<Location> getSpawningLocations() {
        List<Location> locations = new ArrayList<>();
        locations.add( new Location(Constants.playerWidth*1f, Constants.playerWidth*1f));
		locations.add( new Location(Constants.playerWidth*1f, Constants.playerWidth*11f));
		locations.add( new Location(Constants.playerWidth*16f, Constants.playerWidth*1f));
		locations.add( new Location(Constants.playerWidth*16f, Constants.playerWidth*11f));
        return locations;
    }

    public static SrcMazeData parseMazeFile() {
        Gson gson = new Gson();
        try {
            String jsonStr = new String(Files.readAllBytes(Paths.get("client/platzhersh.json")));
            return gson.fromJson(jsonStr, SrcMazeData.class);
        } catch (IOException e) {
            LOG.error("Unable to parse maze file.");
            throw new RuntimeException(e);
        }
    }

    public static boolean[][] gridFromSrc(SrcMazeData src) {
        int rows = src.posY.length;
        int cols = src.posY[0].posX.length;
        boolean[][] grid = new boolean[cols][rows];
        for (SrcMazeData.PositionY py : src.posY) {
            for (SrcMazeData.PositionX px : py.posX) {
                grid[px.col-1][py.row-1] = px.type.equals("wall");
            }
        }
        return grid;
    }

    public float timeToWallImpact(Location location, Direction direction) {
        List<Integer> colsToCheck = colsToCheck(location);
        List<Integer> rowsToCheck = rowsToCheck(location);
        List<GridCoordinates> boundary = getBoundary(direction, colsToCheck, rowsToCheck);
        float timeSpent = 0;
        while (timeSpent < Constants.timeStep) {
            GridCoordinates b1 = boundary.get(0);
            timeSpent = CollisionDetection.timeToImpact(location, direction.getX(), direction.getY(),
                    new Location(b1.x * Constants.playerWidth, b1.y * Constants.playerWidth), 0, 0);
            if (anyWallInBoundary(boundary)) {
                return Math.min(Constants.timeStep, timeSpent);
            }
            boundary = moveBoundary(boundary, direction);
        }
        return Constants.timeStep;
    }

    public float timeToTurnPoint(Location location, Direction direction, Direction desiredDirection) {
        List<Integer> colsToCheck = colsToCheck(location);
        List<Integer> rowsToCheck = rowsToCheck(location);
        List<GridCoordinates> boundary = getBoundary(direction, colsToCheck, rowsToCheck);
        float timeToImpactDesiredDirection = timeToWallImpact(location, desiredDirection);
        if (timeToImpactDesiredDirection > Constants.wallEpsilon*Constants.playerWidth/Constants.speed) {
            return 0;
        }
        GridCoordinates b1 = boundary.get(0);
        float timeSpent = CollisionDetection.timeToImpact(location, direction.getX(), direction.getY(),
                new Location(b1.x * Constants.playerWidth, b1.y * Constants.playerWidth), 0, 0);
        Location virtualLocation = new Location(location.x + timeSpent * direction.getX(), location.y + direction.getY());
        while (timeSpent < Constants.timeStep) {
            timeToImpactDesiredDirection = timeToWallImpact(location, desiredDirection);
            if (timeToImpactDesiredDirection > Constants.wallEpsilon/Constants.speed) {
                return timeSpent;
            }
            virtualLocation = new Location(virtualLocation.x + Constants.playerWidth, virtualLocation.y + Constants.playerWidth);
            timeSpent += Constants.playerWidth/Constants.speed;
        }
        return Constants.timeStep + 1;
    }

    private boolean anyWallInBoundary(List<GridCoordinates> boundary) {
        for (GridCoordinates square : boundary) {
            if (square.x >= grid.length || square.x < 0 || square.y >= grid[0].length || square.y < 0) {
                return true;
            }
            if (grid[square.x][square.y]) {
                return true;
            }
        }
        return false;
    }

    private List<GridCoordinates> moveBoundary(List<GridCoordinates> boundary, Direction direction) {
        int dx = (int)Math.signum(direction.getX());
        int dy = (int)Math.signum(direction.getY());
        List<GridCoordinates> movedBoundary = new ArrayList<>();
        for (GridCoordinates square : boundary) {
            movedBoundary.add(new GridCoordinates(square.x + dx, square.y + dy));
        }
        return movedBoundary;
    }

    private List<GridCoordinates> getBoundary(Direction direction, List<Integer> colsToCheck, List<Integer> rowsToCheck) {
        List<GridCoordinates> boundary = new ArrayList<>();
        switch (direction) {
            case UP:
                for (int col : colsToCheck) {
                    int row = rowsToCheck.get(0) - 1;
                    boundary.add(new GridCoordinates(col, row));
                }
                return boundary;
            case DOWN:
                for (int col : colsToCheck) {
                    int row = rowsToCheck.get(rowsToCheck.size()-1) + 1;
                    boundary.add(new GridCoordinates(col, row));
                }
                return boundary;
            case RIGHT:
                for (int row : rowsToCheck) {
                    int col = colsToCheck.get(colsToCheck.size()-1) + 1;
                    boundary.add(new GridCoordinates(col, row));
                }
                return boundary;
            case LEFT:
                for (int row : rowsToCheck) {
                    int col = colsToCheck.get(0) - 1;
                    boundary.add(new GridCoordinates(col, row));
                }
                return boundary;
            default:
                throw new RuntimeException("Direction should be base direction");
        }
    }

    private List<Integer> rowsToCheck(Location location) {
        List<Integer> rowsToCheck = new ArrayList<>();
        float rowf = location.y/ Constants.playerWidth;
        int rowi = Math.round(rowf);
        float errorY = rowf-rowi;
        rowsToCheck.add(rowi);
        if (Math.abs(errorY) > Constants.wallEpsilon) {
            rowsToCheck.add(rowf > rowi ? rowi + 1 : rowi - 1);
        }
        return rowsToCheck;
    }

    private List<Integer> colsToCheck(Location location) {
        float colf = location.x/ Constants.playerWidth;
        int coli = Math.round(colf);
        float errorX = colf-coli;
        List<Integer> colsToCheck = new ArrayList<>();
        colsToCheck.add(coli);
        if (Math.abs(errorX) > Constants.wallEpsilon) {
            colsToCheck.add(colf > coli ? coli + 1 : coli - 1);
        }
        return colsToCheck;
    }
}
