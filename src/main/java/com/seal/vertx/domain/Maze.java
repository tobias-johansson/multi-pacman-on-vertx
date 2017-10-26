package com.seal.vertx.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.seal.vertx.domain.SrcMazeData.PositionX;
import com.seal.vertx.domain.SrcMazeData.PositionY;

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
