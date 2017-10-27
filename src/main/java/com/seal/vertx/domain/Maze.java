package com.seal.vertx.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.seal.vertx.Constants;
import com.seal.vertx.domain.SrcMazeData.PositionX;
import com.seal.vertx.domain.SrcMazeData.PositionY;
import com.seal.vertx.quadtree.GridCollection;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Maze {
    MazeData mazeData;
    GridCollection grid;

    /**
     *   *******
     *   **....*
     *   **.****
     *   **.****
     */
    public Maze() {
        try {
            mazeData = new Gson().fromJson(new String(Files.readAllBytes(Paths.get("client/maze.json"))), MazeData.class);
            List<Wall> walls = mazeData.wallBlocks.stream().map(loc -> new Wall(loc)).collect(Collectors.toList());
            grid = new GridCollection(walls, 18, 13);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public boolean checkWallCollision(Location from, Location to) {
        return collide(to, neighborhood(to));
    }

    private List<Wall> neighborhood(Location check) {
        return grid.candidates(check);
    }

    private boolean collide(Location check, List<Wall> walls) {
        for(Wall w : walls) {
            if (w.contains(check)) {
                System.out.println("Collision with wall:"+w.anchor.x+" "+w.anchor.y+" and player:"+check.x+" "+check.y);
                return true;
            }
        }
        return false;
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
    // 18 cols 13 rows
    public static void GenerateMazeFile(String srcFile, String dstFile) throws IOException {
        Gson gson = new Gson();
        String jsonStr = new String(Files.readAllBytes(Paths.get(srcFile)));
        SrcMazeData srcMazeData = gson.fromJson(jsonStr, SrcMazeData.class);
        MazeData mazeData = new MazeData();
        float cellSize = Constants.playerWidth;
        for(PositionY posY : srcMazeData.posY) {
            for(PositionX x : posY.posX) {
                if (x.type.equals("wall")) {
                    // back position not center
                    mazeData.wallBlocks.add(new Location((x.col-1)*cellSize, (posY.row-1)*cellSize));
                }
            }
        }
        String mazeStr = gson.toJson(mazeData, MazeData.class);
        Files.write(Paths.get(dstFile), mazeStr.getBytes());
    }

    public float timeToHit(Location location, Direction direction) {

        float absoluteSpeed = Math.abs(direction.getX() + direction.getY());
        int maxBlocksMoved = (int) Math.ceil(absoluteSpeed * Constants.timeStep/Constants.playerWidth) + 1;
        float colf = location.x/Constants.playerWidth;
        int coli = Math.round(colf);
        float errorX = colf-coli;
        float rowf = location.y/Constants.playerWidth;
        int rowi = Math.round(rowf);
        float errorY = colf-coli;
        List<Integer> colsToCheck = new ArrayList<>();
        colsToCheck.add(coli);
        if (Math.abs(errorX) > Constants.wallEpsilon) {
            colsToCheck.add(colf > coli ? coli + 1 : coli - 1);
        }
        List<Integer> rowsToCheck = new ArrayList<>();
        rowsToCheck.add(rowi);
        if (Math.abs(errorY) > Constants.wallEpsilon) {
            rowsToCheck.add(rowf > rowi ? rowi + 1 : rowi - 1);
        }
        List<Wall> yWallCandidates = getYWallCandidates(direction, maxBlocksMoved, rowi, colsToCheck);
        List<Wall> xWallCandidates = getXWallCandidates(direction, maxBlocksMoved, coli, rowsToCheck);
        switch (direction) {
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
        }
    }

    private float timeToHitX(float x, float d, List<Wall> xWallCandidates) {
        float time = 100000;
        for (Wall wall : xWallCandidates) {
            float wallTime = timeToHitX(x,d, wall);
            if (wallTime >= 0) {
                time = Math.min(time, wallTime);
            }
        }
        return time;
    }

    private float timeToHitY(float y, float d, List<Wall> yWallCandidates) {
        float time = 100000;
        for (Wall wall : yWallCandidates) {
            float wallTime = timeToHitY(y, d, wall);
            if (wallTime >= 0) {
                time = Math.min(time, wallTime);
            }
        }
        return time;
    }

    private float timeToHitX(float x, float d, Wall wall) {
        return (wall.anchor.x - x - Constants.playerWidth)/d;
    }

    private float timeToHitY(float y, float d, Wall wall) {
        return (wall.anchor.y - y - Constants.playerWidth)/d;
    }

    private List<Wall> getXWallCandidates(Direction direction, int maxBlocksMoved, int coli, List<Integer> rowsToCheck) {
        List<Wall> rowWalls = new ArrayList<>();
        int xDirSign = (int)Math.signum(direction.getX());
        for (int row : rowsToCheck) {
            for (int i = 0; i < maxBlocksMoved; i++) {
                if (coli + i*xDirSign == grid.getGrid().length) {
                    break;
                }
                rowWalls.addAll((List<Wall>)grid.getGrid()[coli + i*xDirSign][row]);
            }
        }
        return rowWalls;
    }

    private List<Wall> getYWallCandidates(Direction direction, int maxBlocksMoved, int rowi, List<Integer> colsToCheck) {
        List<Wall> colWalls = new ArrayList<>();
        int yDirSign = (int)Math.signum(direction.getY());
        for (int col : colsToCheck) {
            for (int i = 0; i < maxBlocksMoved; i++) {
                if (rowi + i*yDirSign == grid.getGrid()[col].length) {
                    break;
                }
                colWalls.addAll((List<Wall>)grid.getGrid()[col][rowi + i*yDirSign]);
            }
        }
        return colWalls;
    }

}
