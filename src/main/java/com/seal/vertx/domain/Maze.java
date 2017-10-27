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
			grid = new GridCollection(walls, 20, 20);
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
}
