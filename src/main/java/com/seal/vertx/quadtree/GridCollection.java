package com.seal.vertx.quadtree;

import java.util.ArrayList;
import java.util.List;

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

	public List<Wall> candidates(Location check) {
		int x = (int)(check.x * xRange);
		int y = (int)(check.y * yRange);
		return (List<Wall>)grid[x][y];
	}
	
	public void insert(Wall wall) {
		candidates(wall.anchor()).add(wall);
	}

	public Object[][] getGrid() {
		return grid;
	}
}
