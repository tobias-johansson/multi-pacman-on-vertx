package com.seal.vertx.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.seal.vertx.Constants;
import com.seal.vertx.domain.SrcMazeData.PositionX;
import com.seal.vertx.domain.SrcMazeData.PositionY;
import com.seal.vertx.quadtree.Point;
import com.seal.vertx.quadtree.QuadTree;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Maze {
	MazeData mazeData;
	QuadTree quadTree;
	
    public Maze() {
    	try {
			mazeData = new Gson().fromJson(new String(Files.readAllBytes(Paths.get("client/maze.json"))), MazeData.class);
	    	quadTree = new QuadTree(0.0, 0.0, 1.0, 1.0);
	    	// populate qt with maze data
	    	mazeData.wallBlocks.stream().forEach(loc -> quadTree.set(loc.x, loc.y, new Wall(loc)));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
			// diediedie
		}
    }
    
    public boolean checkWallCollision(Location check) {
    	return collide(check, neighborhood(check));
    }
    
    private List<Wall> neighborhood(Location check) {
    	Point[] points = quadTree.searchIntersect(check.x - Constants.playerWidth, check.y - Constants.playerWidth, check.x + Constants.playerWidth, check.y + Constants.playerWidth);
    	List<Wall> walls = new ArrayList<Wall>();
    	for (Point p : points) {
    		walls.add((Wall)p.getValue());
    	}
    	return walls;
    }

    private boolean collide(Location check, List<Wall> walls) {
    	for(Wall w : walls) {
    		if (w.contains(check)) {
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
    
    public static void GenerateMazeFile(String srcFile, String dstFile) throws IOException {
    	Gson gson = new Gson();
    	String jsonStr = new String(Files.readAllBytes(Paths.get(srcFile)));
    	SrcMazeData srcMazeData = gson.fromJson(jsonStr, SrcMazeData.class);
    	MazeData mazeData = new MazeData();
    	float rows = srcMazeData.posY.length;
    	float cols = srcMazeData.posY[0].posX.length;
    	for(PositionY posY : srcMazeData.posY) {
    		for(PositionX x : posY.posX) {
    			if (x.type.equals("wall")) {
    				// back position not center
    				mazeData.wallBlocks.add(new Location(x.col/cols, posY.row/rows));
    			}
    		}
    	}
    	String mazeStr = gson.toJson(mazeData, MazeData.class);
    	Files.write(Paths.get(dstFile), mazeStr.getBytes());
    }
}
