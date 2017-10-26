package com.seal.vertx.domain;

public class SrcMazeData {
	public PositionY[] posY;
	
	public class PositionY {
		public int row;
		public PositionX posX[];
	}
	public class PositionX {
		public int col;
		public String type;
	}
}


