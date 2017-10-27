package com.seal.vertx.quadtree;

import java.util.ArrayList;
import java.util.List;

import com.seal.vertx.domain.Direction;
import com.seal.vertx.domain.Location;
import com.seal.vertx.domain.Wall;

public class GridCollection {
	private Object[][] grid;
	private int xRange;
	private int yRange;

	public GridCollection(List<Wall> blocks, int xRange, int yRange) {
		this.xRange = xRange;
		this.yRange = yRange;
		this.grid = new Object[xRange+1][yRange+1];
		for(int x=0;x<xRange+1;x++){  
			 for(int y=0;y<yRange+1;y++){  
				 grid[x][y] = (Object)new ArrayList<Wall>();
			 }  
		}
		blocks.stream().forEach(wall -> insert(wall));
	}

	
	public List<Wall> candidatesAt(Location check) {
		int x = (int)(check.x * xRange);
		int y = (int)(check.y * yRange);
		return (List<Wall>)grid[x][y];
	}
	
	public List<Wall> candidates(Location check, Direction dir) {
		// check starts at from position.. verify that initally
		int x = (int)(check.x * xRange);
		int y = (int)(check.y * yRange);
		List<Wall> candidates = (List<Wall>)grid[x][y];
		int xInc = (Direction.LEFT.equals(dir)) ? -1 : (Direction.RIGHT.equals(dir)) ? 1 :0;
		int yInc = (Direction.UP.equals(dir)) ? -1 : (Direction.DOWN.equals(dir)) ? 1 :0;
		do {
			x += xInc;
			y += yInc;
			if (x < 0 || x > xRange || y < 0 || y > yRange) {
				return candidates;
			}
			candidates.addAll((List<Wall>)grid[x][y]);
			System.out.println("Candidates size:"+candidates.size());
		} while (candidates.isEmpty());
		return candidates;
	}
	
	public void insert(Wall wall) {
		candidatesAt(wall.anchor()).add(wall);
	}

}
