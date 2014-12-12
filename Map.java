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

		calculateShortestDistance(mapData);
	}





	public void calculateShortestDistance(){
		calculateShortestDistance(mapData);
	}

	private void calculateShortestDistance(int[][] mapData){
		int width = mapData.length;
		int height = mapData[0].length;
	//	mapWidth = width;
	//	mapHeight = height;
		shortestDistance = new double[width][height][width][height]; 
	//	System.out.println("Initialising distance data...");
		// initialise distances - to begin with, everything is infinity from everything except for spaces,
		//	which are 0 from themselves.
		for (int x = 0; x < width; x++){
			for (int y = 0; y < height; y++){
				for (int a = 0; a < width; a++){
					for (int b = 0; b < height; b++){
						if (x == a && y == b && mapData[x][y] != 1){
							shortestDistance[x][y][a][b] = 0;
						} else {
							shortestDistance[x][y][a][b] = Double.POSITIVE_INFINITY;
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
							if (mapData[x][y] != 1 && mapData[a][b] != 1){
								double currentDist = shortestDistance[x][y][a][b];
								// now update the distance based on whether any neighbors have a shorter distance than the current distance minus 1
								if (x > 0 && shortestDistance[x-1][y][a][b] < currentDist -1){
									shortestDistance[x][y][a][b] = shortestDistance[x-1][y][a][b] + 1;
									noChange = false;
								}
								if (y > 0 && shortestDistance[x][y-1][a][b] < currentDist -1){
									shortestDistance[x][y][a][b] = shortestDistance[x][y-1][a][b] + 1;
									noChange = false;
								}
								if (x < width-1 && shortestDistance[x+1][y][a][b] < currentDist -1){
									shortestDistance[x][y][a][b] = shortestDistance[x+1][y][a][b] + 1;
									noChange = false;
								}
								if (y < height -1 && shortestDistance[x][y+1][a][b] < currentDist -1){
									shortestDistance[x][y][a][b] = shortestDistance[x][y+1][a][b] + 1;
									noChange = false;
								}
							}
						}
					}
				}
			}
		}

	//	System.out.println("Distance data done.");

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
