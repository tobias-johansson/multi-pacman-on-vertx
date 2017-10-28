package com.seal.vertx.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.seal.vertx.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Maze {
    private static final Logger LOG = LoggerFactory.getLogger(Maze.class);

    public final boolean[][] grid;

    /**
     *   *******
     *   **....*
     *   **.****
     *   **.****
     */
    public Maze() {
        SrcMazeData src = parseMazeFile();
        this.grid = gridFromSrc(src);
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

    public float timeToHit(Location location, Direction direction) {
        float absoluteSpeed = Math.abs(direction.getX() + direction.getY());
        int maxBlocksMoved = (int) Math.ceil(absoluteSpeed * Constants.timeStep/Constants.playerWidth) + 1;

        List<GridCoordinates> yWallCandidates = getYWallCandidates(direction, maxBlocksMoved, rowi, colsToCheck);
        //List<GridCoordinates> xWallCandidates = getXWallCandidates(direction, maxBlocksMoved, coli, rowsToCheck);
        /*switch (direction) {
            case UP:
                return timeToHitY(location.y, direction.getY(), yWallCandidates);
            case DOWN:
                return timeToHitY(location.y, direction.getY(), yWallCandidates);
            case LEFT:
                return timeToHitX(location.x, direction.getX(), xWallCandidates);
            case RIGHT:
                return timeToHitX(location.x, direction.getX(), xWallCandidates);
            default:
                return 0;
        }*/
        return 0;
    }
//    private float timeToHitX(float x, float d, List<Wall> xWallCandidates) {
//        float time = 100000;
//        for (Wall wall : xWallCandidates) {
//            float wallTime = timeToHitX(x,d, wall);
//            if (wallTime >= 0) {
//                time = Math.min(time, wallTime);
//            }
//        }
//        return time;
//    }
//
//    private float timeToHitY(float y, float d, List<Wall> yWallCandidates) {
//        float time = 100000;
//        for (Wall wall : yWallCandidates) {
//            float wallTime = timeToHitY(y, d, wall);
//            if (wallTime >= 0) {
//                time = Math.min(time, wallTime);
//            }
//        }
//        return time;
//    }
//
//    private float timeToHitX(float x, float d, Wall wall) {
//        return (wall.anchor.x - x - Constants.playerWidth)/d;
//    }
//
//    private float timeToHitY(float y, float d, Wall wall) {
//        return (wall.anchor.y - y - Constants.playerWidth)/d;
//    }

//    private List<Wall> getXWallCandidates(Direction direction, int maxBlocksMoved, int coli, List<Integer> rowsToCheck) {
//        List<Wall> rowWalls = new ArrayList<>();
//        int xDirSign = (int)Math.signum(direction.getX());
//        for (int row : rowsToCheck) {
//            for (int i = 0; i < maxBlocksMoved; i++) {
//                int col = coli + i * xDirSign;
//                if (col >= grid.getGrid().length ||  row >= grid.getGrid()[col].length) {
//                    break;
//                }
//                rowWalls.addAll((List<Wall>)grid.getGrid()[col][row]);
//            }
//        }
//        return rowWalls;
//    }

    private List<GridCoordinates> getYWallCandidates(Direction direction, int maxBlocksMoved, int rowi, List<Integer> colsToCheck) {
        List<GridCoordinates> colWalls = new ArrayList<>();
        int yDirSign = (int)Math.signum(direction.getY());
        for (int col : colsToCheck) {
            for (int i = -1; i < maxBlocksMoved; i++) {
                int row = rowi + i*yDirSign;
                if (col >= grid.getGrid().length || row >= grid.getGrid()[col].length) {
                    break;
                }
                colWalls.addAll((List<Wall>)grid.getGrid()[col][rowi + i*yDirSign]);
            }
        }
        return colWalls;
    }

    public Location move(Location location, Direction direction) {
        float timeLeft = Constants.timeStep;
        float x = location.x;
        float y = location.y;
        int xDirSign = (int)Math.signum(direction.getX());
        int yDirSign = (int)Math.signum(direction.getY());
        boolean sideWays = direction.getX() != 0;
        List<Integer> colsToCheck = colsToCheck(location);
        List<Integer> rowsToCheck = rowsToCheck(location);
        while (timeLeft != 0) {
            if (sideWays) {
                
            }
        }
        return new Location(x,y);
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
