public class Map{

	private int[][] mapData;
	// 1 = obstacle
	private int width = 50;
	private int height = 20;

	private double[][][][]	shortestDistance;


	public Map(int width, int height){
		this.width=width;
		this.height=height;
		generateMapData();
	}


	public Map(Map newMap){
		this.width = newMap.getWidth();
		this.height = newMap.getHeight();
		mapData = newMap.getMapData();
		shortestDistance = newMap.getShortestDistanceData();
	}


	private void generateMapData(){
		mapData = new int[width][height];
		for (int i=0; i < width; i++){
			mapData[i][0] = 1;
			mapData[i][height-1] = 1;
		}
		for (int j=0; j < height; j++){
			mapData[0][j] = 1;
			mapData[width-1][j] = 1;
		}
		for (int i=0; i < width; i = i+3){
			for (int j= 0; j < height; j = j+3){
				mapData[i][j] = 1;
			}
		}
		

/*		int[][] tempMap =  
				{{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				 {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				 {1,0,1,1,1,0,0,1,1,1,1,1,1,0,1,1,1,1},
				 {1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1,1},
				 {1,1,1,0,0,1,0,0,0,0,0,0,0,0,1,0,1,1},
				 {1,0,0,1,0,1,0,0,0,0,0,0,0,0,1,0,1,1},
				 {1,0,1,1,0,1,0,0,1,0,0,1,0,0,0,0,0,1},
				 {1,0,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1},
				 {1,0,1,1,1,1,1,1,0,0,1,0,0,0,1,0,1,1},
				 {1,0,0,0,0,1,0,0,0,0,0,0,1,0,1,1,1,1},
				 {1,0,1,1,0,1,1,1,0,0,1,0,0,0,1,0,1,1},
				 {1,0,1,1,0,1,0,0,1,0,0,0,0,1,1,0,0,1},
				 {1,0,0,1,1,0,0,0,1,0,0,1,1,1,1,0,0,1},
				 {1,1,1,0,1,0,1,1,0,1,0,1,0,0,0,0,0,1},
				 {1,0,0,0,1,0,0,1,0,0,0,1,1,0,0,0,0,1},
				 {1,0,1,0,1,0,1,1,0,0,1,1,0,0,0,0,0,1},
				 {1,0,1,0,1,0,1,0,0,1,0,0,0,0,1,1,1,1},
				 {1,0,1,1,0,0,1,0,0,1,0,0,1,0,0,0,0,1},
				 {1,0,1,0,1,1,0,1,0,1,1,1,1,0,1,1,0,1},
				 {1,0,0,0,0,1,0,1,0,1,0,0,1,0,0,0,0,1},
				 {1,1,1,0,0,1,0,1,0,1,0,0,0,0,0,0,1,1},
				 {1,0,0,0,0,0,0,0,0,0,1,0,0,1,0,1,1,1},
				 {1,0,1,1,1,1,0,1,0,0,0,1,1,1,0,0,0,1},
				 {1,0,1,0,0,1,0,0,0,1,0,0,0,0,0,1,0,1},
				 {1,0,1,0,1,0,1,1,0,0,0,1,1,1,0,1,0,1},
				 {1,0,1,0,1,0,1,0,0,1,0,0,0,1,0,1,0,1},
				 {1,0,0,0,1,0,1,1,0,0,0,1,0,0,0,0,0,1},
				 {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
		mapData = tempMap;
*/
		mapData = MapGenerator.generateMap(width, height);

		shortestDistance = calculateShortestDistance(mapData);
	}






	public void calculateShortestDistance(){
		shortestDistance = calculateShortestDistance(mapData);
	}

	private static double[][][][] calculateShortestDistance(int[][] mapData){
		int width = mapData.length;
		int height = mapData[0].length;
	//	mapWidth = width;
	//	mapHeight = height;
		double [][][][] tempShortestDistance = new double[width][height][width][height]; 
	//	System.out.println("Initialising distance data...");
		// initialise distances - to begin with, everything is infinity from everything except for spaces,
		//	which are 0 from themselves.
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				for (int a = 0; a < width; a++){
					for (int b = 0; b < height; b++){
						if (x == a && y == b && mapData[x][y] == 0){
							tempShortestDistance[x][y][a][b] = 0;
						} else {
							tempShortestDistance[x][y][a][b] = Double.POSITIVE_INFINITY;
						}
					}
				}
			}
		}


	//	System.out.println("Processing distance data...");

		// Now we do a whole bunch of updating the values! Woo.
		boolean noChange = false;
		for (int iteration = 0; iteration < width*height && !noChange; iteration++){
			if (iteration % 10 == 0){
	//			System.out.println("Iteration " + iteration + "...");
			}
			noChange = true;
			for (int x = 0; x < width; x++){
				for (int y = 0; y < height; y++){
					for (int a = 0; a < width; a++){
						for (int b = 0; b < height; b++){
							// obstacles are always going to be infinity from everything, so never update these values.
							if (mapData[x][y] == 0 && mapData[a][b] == 0){
								double currentDist = tempShortestDistance[x][y][a][b];
								// now update the distance based on whether any neighbors have a shorter distance than the current distance minus 1
								if (x > 0 && tempShortestDistance[x-1][y][a][b] < currentDist -1){
									tempShortestDistance[x][y][a][b] = tempShortestDistance[x-1][y][a][b] + 1;
									noChange = false;
								}
								if (y > 0 && tempShortestDistance[x][y-1][a][b] < currentDist -1){
									tempShortestDistance[x][y][a][b] = tempShortestDistance[x][y-1][a][b] + 1;
									noChange = false;
								}
								if (x < width-1 && tempShortestDistance[x+1][y][a][b] < currentDist -1){
									tempShortestDistance[x][y][a][b] = tempShortestDistance[x+1][y][a][b] + 1;
									noChange = false;
								}
								if (y < height -1 && tempShortestDistance[x][y+1][a][b] < currentDist -1){
									tempShortestDistance[x][y][a][b] = tempShortestDistance[x][y+1][a][b] + 1;
									noChange = false;
								}
							}
						}
					}
				}
			}
		}
		return tempShortestDistance;
//		shortestDistance = tempShortestDistance;
	}





//	public void calculateWallDistance(){
//		shortestDistance = calculateStDistance(mapData);
//	}


	// like ShortestDistance, but now we are calculating how many wall blocks 
	// you need to pass through to get from one space to another.
	// The cost of moving into a wall block is 1.
	// The cost of moving into an empty space is 0.
	// Note that this means the graph is slightly assymetric: 
	// wallDistance[x][y][a][b] may be different to wallDistance[a][b][x][y]
	// if (a,b) is a wall and (x,y) is not.
	// SO let us say that wallDistance[x][y] is the number of walls you have to enter to 
	// go FROM (x,y) TO (a,b)
	public static double[][][][] calculateWallDistance(int[][] mapData){
		int width = mapData.length;
		int height = mapData[0].length;
	//	mapWidth = width;
	//	mapHeight = height;
		double [][][][] tempWallDistance = new double[width][height][width][height]; 
	//	System.out.println("Initialising distance data...");
		// initialise distances - to begin with, everything is infinity from everything else,
		// and 0 from itself.
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				for (int a = 0; a < width; a++){
					for (int b = 0; b < height; b++){
						if (x == a && y == b){
							tempWallDistance[x][y][a][b] = 0;
						} else {
							tempWallDistance[x][y][a][b] = Double.POSITIVE_INFINITY;
						}
					}
				}
			}
		}


		// Now we do a whole bunch of updating the values! Woo.
		boolean noChange = false;
		for (int iteration = 0; iteration < width*height && !noChange; iteration++){
			noChange = true;
			for (int x = 0; x < width; x++){
				for (int y = 0; y < height; y++){
					for (int a = 0; a < width; a++){
						for (int b = 0; b < height; b++){
				//			// obstacles are always going to be infinity from everything, so never update these values.
				//			if (mapData[x][y] != 1 && mapData[a][b] != 1){
								double currentDist = tempWallDistance[x][y][a][b];
							// now update the distance based on whether any neighbors have a shorter distane.
							// if the neighbour is a space, use the neighbour's distance if it's shorter.
							// if the neighbour is a wall, use the neighbour's distance if that plus 1 is shorter
							if (x > 0 && tempWallDistance[x-1][y][a][b] < currentDist){
								if (mapData[x-1][y] == 0){
									tempWallDistance[x][y][a][b] = tempWallDistance[x-1][y][a][b];
									noChange = false;
								} else if (tempWallDistance[x-1][y][a][b] < currentDist-1){
									tempWallDistance[x][y][a][b] = tempWallDistance[x-1][y][a][b] + 1;
									noChange = false;
								}
							}
							if (y > 0 && tempWallDistance[x][y-1][a][b] < currentDist){
								if (mapData[x][y-1] == 0){
									tempWallDistance[x][y][a][b] = tempWallDistance[x][y-1][a][b];
									noChange = false;
								} else if (tempWallDistance[x][y-1][a][b] < currentDist-1){
									tempWallDistance[x][y][a][b] = tempWallDistance[x][y-1][a][b] + 1;
									noChange = false;
								}
							}
							if (x < width-1 && tempWallDistance[x+1][y][a][b] < currentDist){
								if (mapData[x+1][y] == 0){
									tempWallDistance[x][y][a][b] = tempWallDistance[x+1][y][a][b];
									noChange = false;
								} else if (tempWallDistance[x+1][y][a][b] < currentDist-1){
									tempWallDistance[x][y][a][b] = tempWallDistance[x+1][y][a][b] + 1;
									noChange = false;
								}
							}
							if (y < height -1 && tempWallDistance[x][y+1][a][b] < currentDist){
								if (mapData[x][y+1] == 0){
									tempWallDistance[x][y][a][b] = tempWallDistance[x][y+1][a][b];
									noChange = false;
								} else if (tempWallDistance[x][y+1][a][b] < currentDist-1){
									tempWallDistance[x][y][a][b] = tempWallDistance[x][y+1][a][b] + 1;
									noChange = false;
								}
							}
						}
					}
				}
			}
		}
		return tempWallDistance;
//		shortestDistance = tempShortestDistance;
	}




	public int[][] getMapData(){
		return mapData;
	}

	public int getMapDataAt(int i, int j){
		return mapData[i][j];
	}

	
	public double shortestDistance(int i, int j, int x, int y){
		return shortestDistance[i][j][x][y];
	}

	public double[][][][] getShortestDistanceData(){
		return shortestDistance;
	}

	public int getMapDataAt(GridRef ref){
		return mapData[ref.x][ref.y];
	}
	
	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}
	
	public void setValue(GridRef gr, int val){
		mapData[gr.x][gr.y] = val;
	}

}
