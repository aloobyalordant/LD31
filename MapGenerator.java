// the class that generates maps
import java.util.Random;
import java.util.ArrayList;
public class MapGenerator{

	private static Random ran = new Random();
	private static int segmentWidth = 7;
	private static int segmentHeight = 7;

	public static int[][]generateMap(int width, int height){

		int[][]map = new int[5*(segmentWidth-1)+1][3*(segmentHeight-1)+1];

/*
Here is a map of the edges these letters correspond to (true = the way is open, false = barrier there)
*********************
*   A   B   C   D   *
**E***F***G***H***I**
*   J   K   L   M   *
**N***O***P***Q***R**
*   S   T   U   V   *
*********************

*/


		boolean A = ran.nextBoolean(); boolean B = ran.nextBoolean();boolean C = ran.nextBoolean();boolean D = ran.nextBoolean();
	boolean E = ran.nextBoolean();boolean F=ran.nextBoolean();boolean G=ran.nextBoolean();boolean H=ran.nextBoolean();boolean I=ran.nextBoolean();
		boolean J = ran.nextBoolean();boolean K = ran.nextBoolean();boolean L = ran.nextBoolean();boolean M = ran.nextBoolean();
	boolean N=ran.nextBoolean();boolean O=ran.nextBoolean();boolean P=ran.nextBoolean();boolean Q=ran.nextBoolean();boolean R = ran.nextBoolean();
		boolean S = ran.nextBoolean();boolean T = ran.nextBoolean();boolean U = ran.nextBoolean();boolean V = ran.nextBoolean();

		// Make a guaranteed route from the centre to at least one of the four corners (being stuck out of charge with no access to any chargers is no fun)
		int routeChoice = ran.nextInt(12);
		switch(routeChoice){
			case 0: 
				A = true; B = true; G = true;
				break;
			case 1: 
				A = true; F = true; K = true;
				break;
			case 2: 
				E = true; J = true; K = true;
				break;
			case 3: 
				S = true; T = true; P = true;
				break;
			case 4: 
				S = true; O = true; K = true;
				break;
			case 5: 
				N = true; J = true; K = true;
				break;
			case 6: 
				D = true; C = true; G = true;
				break;
			case 7: 
				D = true; H = true; L = true;
				break;
			case 8: 
				I = true; M = true; L = true;
				break;
			case 9: 
				V = true; U = true; P = true;
				break;
			case 10: 
				V = true; Q = true; L = true;
				break;
			default: 
				R = true; M = true; L = true;
				break;
	
		}


		addSegment(map, 0,0, generateCornerSegment(false,false,A,E));
		addSegment(map, 1,0,generateSegment(false,A,B,F));
		addSegment(map, 2,0,generateSegment(false,B,C,G));
		addSegment(map, 3,0,generateSegment(false,C,D,H));
		addSegment(map, 4,0,generateCornerSegment(false,D,false,I));


		addSegment(map, 0,1, generateSegment(E,false,J,N));
		addSegment(map, 1,1,generateSegment(F,J,K,O));
		addSegment(map, 2,1,centreSegment());
		//addSegment(map, 2,1,generateSegment(G,K,L,P));
		addSegment(map, 3,1,generateSegment(H,L,M,Q));
		addSegment(map, 4,1,generateSegment(I,M,false,R));

		addSegment(map, 0,2, generateCornerSegment(N,false,S,false));
		addSegment(map, 1,2, generateSegment(O,S,T,false));
		addSegment(map, 2,2, generateSegment(P,T,U,false));
		addSegment(map, 3,2, generateSegment(Q,U,V,false));
		addSegment(map, 4,2, generateCornerSegment(R,V,false,false));

		// hack for now - make sure the charger spaces don't have blocks in them

		map[3][3] = 0;
		map[27][3] = 0;
		map[3][15] = 0;
		map[27][15] = 0;
 
		// make sure the avatar doesn't have to go through too many walls to get to any particular charger
		// (pick a random first charger to reduce distance to, to avoid bias in level structure)
		int chargerX;
		int chargerY;
		int chargerChoice = ran.nextInt(4);
		if(chargerChoice == 0){
			chargerX = 3;
			chargerY = 3;
		} else if(chargerChoice == 1){
			chargerX = 27;
			chargerY = 3;
		} else if(chargerChoice == 2){
			chargerX = 3;
			chargerY = 15;
		} else {
			chargerX = 27;
			chargerY = 15;
		}
		// make sure avatar has wall distance 1 to any charger (hmmmm, might want to change this later...)
		// actually, make it so avatr has wall distance at most 0 to at least 3 chargers
		map = reduceWallDistance(map,15,9,chargerX,chargerY,0);
		map = reduceWallDistance(map,15,9,30-chargerX,chargerY,0);
		map = reduceWallDistance(map,15,9,chargerX,18-chargerY,0);
		map = reduceWallDistance(map,15,9,30-chargerX,18-chargerY,1);



		// draw a bunch of unbreakable walls ( = 2)around the border.
		for (int i = 0; i < width; i++){
			map[i][0]=2;
			map[i][height-1] = 2;
			for (int j = 0; j <height; j++){
				map[0][j] = 2;
				map[width-1][j] = 2;
			}
		}






/*
		addSegment(map, 0,0,topLeftCornerSegment());
		addSegment(map, 1,0,topTJunctionSegment());
		addSegment(map, 2,0,corridorSegment());
		addSegment(map, 3,0,topTJunctionSegment());
		addSegment(map, 4,0,rotate(topLeftCornerSegment()));

		addSegment(map,0,1,rotate(rotate(rotate(topTJunctionSegment()))));
		addSegment(map,1,1,crossSegment());
		addSegment(map,2,1,centreSegment());
		addSegment(map,3,1,crossSegment());
		addSegment(map,4,1,rotate(topTJunctionSegment()));

		addSegment(map, 0,2, rotate(rotate(rotate(topLeftCornerSegment()))));
		addSegment(map, 1,2,rotate(rotate(topTJunctionSegment())));
		addSegment(map, 2,2,corridorSegment());
		addSegment(map, 3,2,rotate(rotate(topTJunctionSegment())));
		addSegment(map, 4,2,rotate(rotate(topLeftCornerSegment())));
*/

	/*	int numberSegmentColumns = (width-1)/(segmentWidth-1);
		int numberSegmentRows = (height-1)/(segmentHeight-1);
		for (int i = 0; i< numberSegmentColumns; i++){
			for (int j = 0; j < numberSegmentRows; j++){
				int[][] segment = generateSegment();
				for (int h = 0; h < segmentWidth; h++){
					for (int k = 0; k < segmentHeight; k++){
						if (map[i*(segmentWidth-1) + h][j*(segmentHeight-1) + k] == 0){
							map[i*(segmentWidth-1) + h][j*(segmentHeight-1) + k] = segment[h][k];
						}
					}
				}
			}
		}

		int[][] tempMap =  
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

		return tempMap;
*/

		return map;
	}


	// remove blocks from the map until the wall distance From (x,y) To (a,b) is at most maxDistance
	private static int[][] reduceWallDistance(int[][] mapData, int x, int y, int a, int b, int maxDistance){
		double[][][][] wallDistance = Map.calculateWallDistance(mapData);
		int currentBest = ((Double)wallDistance[x][y][a][b]).intValue();
		int distanceToReduce = Math.max(0,(currentBest-maxDistance));
		
		for (int c = 0; c < distanceToReduce; c++){
			int width = mapData.length;
			int height = mapData[0].length; 
			ArrayList<GridRef> deletionCandidates = new ArrayList<GridRef>();
			for (int i = 1; i < width-1; i++){
				for (int j = 1; j < height-1; j++){
					if(mapData[i][j] == 1){
						if(wallDistance[x][y][i-1][j] + wallDistance[i][j][a][b] < currentBest
							|| wallDistance[x][y][i][j-1] + wallDistance[i][j][a][b] < currentBest
							|| wallDistance[x][y][i+1][j] + wallDistance[i][j][a][b] < currentBest
							|| wallDistance[x][y][i][j+1] + wallDistance[i][j][a][b] < currentBest){
							deletionCandidates.add(new GridRef(i,j));
						}
					}
				}
			}
			
			int numberCandidates = deletionCandidates.size();
			if(numberCandidates > 0){
				int choice = ran.nextInt(numberCandidates);
				GridRef chosenGR = deletionCandidates.get(choice);
				mapData[chosenGR.x][chosenGR.y] = 0;
 				wallDistance = Map.calculateWallDistance(mapData);
			}
			currentBest--;
		}
		return mapData;
	}






// ********** SEGMENT GENERATION STUFF GOES HERE ************


	private static int[][] addSegment(int[][]mapData, int i, int j, int[][]segment){
		int segmentWidth = segment.length;	
		int segmentHeight = segment[0].length;
		for (int h = 0; h < segmentWidth; h++){
			for (int k = 0; k < segmentHeight; k++){
				if (mapData[i*(segmentWidth-1) + h][j*(segmentHeight-1) + k] < segment[h][k]){
					mapData[i*(segmentWidth-1) + h][j*(segmentHeight-1) + k] = segment[h][k];
				}
			}
		}
		return mapData;
	}



	private static int[][] generateSegment(boolean topOpen, boolean leftOpen, boolean rightOpen, boolean bottomOpen){
		
		int numberOfExits = 0;
		if (topOpen){
			numberOfExits++;
		}
		if (leftOpen){
			numberOfExits++;
		}
		if (rightOpen){
			numberOfExits++;
		}
		if (bottomOpen){
			numberOfExits++;
		}


		if (numberOfExits == 4){
			return fourWaySegment();
		} else if (numberOfExits == 0){
			return enclosedSegment();
		}

		// Now, we know there is at least one open and one closed side.
		// So spin things around until the left hand side is closed and the top side is open.
		// big risk of infinite loop here, if we haven't guaranteed that there's at least one open and one closed.
		// or if I've done the wrong permutation.
		if (!topOpen || leftOpen){
			return rotate(generateSegment(rightOpen ,topOpen,bottomOpen,leftOpen));
		}

		if (numberOfExits == 1){
			return deadEndSegment();
		} else if (numberOfExits == 2){
			if (topOpen == bottomOpen){
				return corridorSegment();
			} else {
				return bendSegment();
			}
		} else {
			return tJunctionSegment();
		}
	}


	private static int[][] generateCornerSegment(boolean topOpen, boolean leftOpen, boolean rightOpen, boolean bottomOpen){
		
		int numberOfExits = 0;
		if (topOpen){
			numberOfExits++;
		}
		if (leftOpen){
			numberOfExits++;
		}
		if (rightOpen){
			numberOfExits++;
		}
		if (bottomOpen){
			numberOfExits++;
		}


		if (numberOfExits == 4){
			leftOpen = false;
		}
		if (numberOfExits == 0){
			topOpen = true;
		}

		// Now, we know there is at least one open and one closed side.
		// So spin things around until the left hand side is closed and the top side is open.
		// big risk of infinite loop here, if we haven't guaranteed that there's at least one open and one closed.
		// or if I've done the wrong permutation.
		if (!topOpen || leftOpen){
			return rotate(generateCornerSegment(rightOpen ,topOpen,bottomOpen,leftOpen));
		}

		return rotate(rotate(topLeftCornerSegment()));

	}



/*
	{
		int[][] segment = new int [7][7];
		int choice;

		if (!topOpen){
			int[][] tempSegment =  
			{{1,0,0,0,0,0,0},
			{1,1,0,0,0,0,0}, 
			{1,0,0,0,0,0,0}, 
			{1,1,0,0,0,0,0}, 
			{1,0,0,0,0,0,0},
			{1,1,0,0,0,0,0},
			{1,0,0,0,0,0,0}};
			segment = addSegment(segment,0,0,tempSegment);
		}
		if (!leftOpen){
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{0,1,0,1,0,1,0}, 
			{0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0}};
			segment = addSegment(segment,0,0,tempSegment);
		}
		if (!rightOpen){
			int[][] tempSegment =  
			{{0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0}, 
			{0,0,0,0,0,0,0},
			{0,1,0,1,0,1,0},
			{1,1,1,1,1,1,1}};
			segment = addSegment(segment,0,0,tempSegment);
		}
		if (!bottomOpen){
			int[][] tempSegment =  
			{{0,0,0,0,0,0,1},
			{0,0,0,0,0,1,1}, 
			{0,0,0,0,0,0,1}, 
			{0,0,0,0,0,1,1}, 
			{0,0,0,0,0,0,1},
			{0,0,0,0,0,1,1},
			{0,0,0,0,0,0,1}};
			segment = addSegment(segment,0,0,tempSegment);
		}

/*
		choice = ran.nextInt(9);		

		if(choice == 0){
			int[][] tempSegment =  
			{{0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0}, 
			{0,0,1,1,1,0,0}, 
			{0,0,1,1,1,0,0}, 
			{0,0,1,1,1,0,0},
			{0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0}};
			segment = tempSegment;
		} else if(choice == 1){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,0,0,0,0,0,1}, 
			{1,0,1,1,1,0,1}, 
			{0,0,1,1,1,0,1}, 
			{1,0,1,1,1,0,1},
			{1,0,0,0,0,0,1},
			{1,1,1,0,1,1,1}};
			segment = tempSegment;
		} else if(choice == 2){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{0,0,1,0,0,0,0}, 
			{1,0,1,1,1,0,1}, 
			{0,0,1,0,0,0,0}, 
			{1,0,1,1,1,0,1},
			{0,0,0,0,1,0,0},
			{1,1,1,0,1,1,1}};
			segment = tempSegment;
		} else if(choice == 2){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{0,0,0,0,0,0,1}, 
			{1,1,1,1,1,0,1}, 
			{0,0,0,0,0,0,0}, 
			{1,0,1,1,1,1,1},
			{1,0,0,0,0,0,1},
			{1,0,1,0,1,1,1}};
			segment = tempSegment;
		} else if(choice == 3){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,0,0,0,0,0,1}, 
			{1,0,1,1,1,0,1}, 
			{0,0,1,1,1,0,0}, 
			{1,0,1,1,1,0,1},
			{1,0,0,0,0,0,1},
			{1,1,1,0,1,1,1}};
			segment = tempSegment;
		} else if(choice == 4){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,0,0,0,0,0,1}, 
			{1,0,1,0,1,0,1}, 
			{0,0,1,Y,1,0,0}, 
			{1,0,1,0,1,0,1},
			{1,0,0,0,0,0,1},
			{1,0,1,0,1,1,1}};
			segment = tempSegment;
		} else if (choice == 5) {
			int[][] tempSegment =  
			{{1,0,1,0,1,1,1},
			{1,0,1,0,0,0,1}, 
			{1,0,1,0,1,1,1}, 
			{0,0,0,0,1,0,0}, 
			{1,1,1,1,1,0,1},
			{0,0,0,0,0,0,1},
			{1,1,1,0,1,1,1}};
			segment = tempSegment;
		} else if (choice == 6) {
			int[][] tempSegment =  
			{{0,0,0,0,0,0,0},
			{0,1,0,1,1,1,0}, 
			{0,1,0,0,0,1,0}, 
			{0,1,0,1,0,1,0}, 
			{0,1,0,1,0,1,0},
			{0,1,0,0,0,0,0},
			{0,0,0,0,0,0,0}};
			segment = tempSegment;
		} else if (choice == 7) {
			int[][] tempSegment =  
			{{0,0,0,0,0,0,0},
			{0,0,1,0,0,0,0}, 
			{0,0,1,1,1,1,0}, 
			{0,0,0,0,0,0,0}, 
			{0,1,1,1,1,0,0},
			{0,0,0,0,1,0,0},
			{0,0,0,0,0,0,0}};
			segment = tempSegment;
		}else{
			int[][] tempSegment =  
			{{0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0}, 
			{0,0,1,1,1,0,0}, 
			{0,0,1,1,1,0,0}, 
			{0,0,1,1,1,0,0},
			{0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0}};
			segment = tempSegment;
		}
*/




/*		if(choice == 0){
			int[][] tempSegment =  
			{{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0}, 
			{0,0,0,1,1,0,0,0}, 
			{0,0,0,1,1,0,0,0}, 
			{0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0}};
			segment = tempSegment;
		} else if (choice == 1){
				int[][] tempSegment =  
				{{1,1,0,1,1,0,1,1},
				{0,0,0,0,0,0,0,0}, 
				{1,0,0,1,1,0,0,1}, 
				{1,0,0,1,1,0,0,1}, 
				{0,0,0,0,0,0,0,0},
				{1,1,0,1,1,0,1,1}};
				segment = tempSegment;
		} else if (choice == 2){
				int[][] tempSegment =  
				{{1,1,0,1,1,0,1,1},
				{0,0,0,0,0,0,0,0}, 
				{1,Y,1,1,1,1,1,1}, 
				{1,1,1,1,1,1,Z,1}, 
				{0,0,0,0,0,0,0,0},
				{1,1,0,1,1,0,1,1}};
				segment = tempSegment;
		} else if (choice == 3){
				int[][] tempSegment =  
				{{1,1,0,1,1,0,1,1},
				{0,0,0,0,0,0,0,0}, 
				{1,1,1,1,1,Y,1,1}, 
				{1,Z,1,1,1,1,1,1}, 
				{0,0,0,0,0,0,0,0},
				{1,1,0,1,1,0,1,1}};
				segment = tempSegment;
		} else if (choice == 4){
				int[][] tempSegment =  
				{{1,1,0,1,1,0,1,1},
				{0,0,0,0,0,0,0,0}, 
				{1,1,1,1,1,1,Z,1}, 
				{1,Y,1,1,1,1,1,1}, 
				{0,0,0,0,0,0,0,0},
				{1,1,0,1,1,0,1,1}};
				segment = tempSegment;
		} else if (choice == 5){
				int[][] tempSegment =  
				{{1,1,0,1,1,0,1,1},
				{0,0,0,0,0,0,0,0}, 
				{1,Z,1,1,1,1,1,1}, 
				{1,1,1,1,1,1,Y,1}, 
				{0,0,0,0,0,0,0,0},
				{1,1,0,1,1,0,1,1}};
				segment = tempSegment;
		} else if (choice == 6){
				int[][] tempSegment =  
				{{1,1,0,1,1,0,1,1},
				{0,0,0,0,0,0,0,0}, 
				{1,1,0,1,1,0,1,1}, 
				{1,1,0,1,1,0,1,1}, 
				{0,0,0,0,0,0,0,0},
				{1,1,0,1,1,0,1,1}};
				segment = tempSegment;
		} else if (choice == 7){
				int[][] tempSegment =  
				{{1,1,0,1,1,0,1,1},
				{0,0,0,0,0,0,0,0}, 
				{1,1,0,1,1,0,1,1}, 
				{1,1,0,1,1,0,1,1}, 
				{0,0,0,0,0,0,0,0},
				{1,1,0,1,1,0,1,1}};
				segment = tempSegment;
		} else if (choice == 8){
				int[][] tempSegment =  
				{{1,1,0,1,1,0,1,1},
				{0,0,0,0,0,0,0,0}, 
				{1,1,0,1,1,0,1,1}, 
				{1,1,0,1,1,0,1,1}, 
				{0,0,0,0,0,0,0,0},
				{1,1,0,1,1,0,1,1}};
				segment = tempSegment;
		} else {
				int[][] tempSegment =  
				{{1,1,0,1,1,0,1,1},
				{0,0,0,0,0,0,0,0}, 
				{1,0,0,0,0,0,0,1}, 
				{1,0,0,0,0,0,0,1}, 
				{0,0,0,0,0,0,0,0},
				{1,1,0,1,1,1,0,1}};
				segment = tempSegment;

	
		return segment;
	}		}
*/


	private static int[][] enclosedSegment(){

		int choice = ran.nextInt(2);

		if (choice == 0){
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{1,0,0,0,0,0,1}, 
			{1,0,1,1,1,0,1}, 
			{1,0,1,0,1,0,1}, 
			{1,0,1,1,1,0,1},
			{1,0,0,0,0,0,1},
			{1,1,1,1,1,1,1}};
			return tempSegment;
		} else {
			int[][] tempSegment =  
			{{1,1,0,1,0,0,0},
			{0,0,0,1,0,0,0}, 
			{0,0,1,1,1,0,0}, 
			{1,0,1,1,0,0,1}, 
			{1,0,0,0,0,0,1},
			{0,1,0,0,0,1,0},
			{1,0,1,1,0,1,0}};
			return tempSegment;
		}

	}

	private static int[][] fourWaySegment(){
	
		int choice = ran.nextInt(3);
	
		int Y = 0;
		int obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Y = 1;
		}

		if (choice == 0){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,1,1,0,1,1,1}, 
			{1,1,0,0,Y,0,1}, 
			{0,0,0,1,1,0,0}, 
			{1,1,0,1,1,0,1},
			{1,1,0,0,0,0,1},
			{1,1,1,0,1,1,1}};
			return tempSegment;
		} else if (choice == 1){
			int[][] tempSegment =  
			{{1,0,1,0,1,0,1},
			{0,0,0,0,1,1,0}, 
			{1,1,0,0,Y,0,1}, 
			{0,0,0,1,1,0,0}, 
			{1,0,1,1,0,0,1},
			{0,0,0,0,0,0,0},
			{1,0,1,0,1,0,1}};
			return tempSegment;
		} else {
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,0,0,0,0,0,1}, 
			{1,0,1,1,1,0,1}, 
			{0,0,1,0,1,0,0}, 
			{1,0,1,1,1,0,1},
			{1,0,0,0,0,0,1},
			{1,1,1,0,1,1,1}};
			return tempSegment;
		} 
	}


	private static int[][] deadEndSegment(){
		int choice = ran.nextInt(3);
		int Y = 0;
		int obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Y = 1;
		}


		if (choice == 0){
			int[][] tempSegment =  
			{{0,1,1,1,1,1,0},
			{1,0,0,0,0,0,1}, 
			{1,0,1,0,1,0,1}, 
			{0,0,1,1,1,1,1}, 
			{1,0,1,0,1,0,1},
			{0,0,0,0,0,0,1},
			{0,1,1,1,1,1,0}};
			return tempSegment;
		} else if (choice == 1) {
			int[][] tempSegment =  
			{{0,1,1,1,1,1,0},
			{0,1,1,1,1,1,1}, 
			{0,1,1,0,0,0,1}, 
			{0,0,0,0,1,0,1}, 
			{0,1,1,0,0,0,1},
			{0,1,1,1,1,1,1},
			{0,1,1,1,1,1,0}};
			return tempSegment;
		} else {
			int[][] tempSegment =  
			{{0,1,1,1,1,1,0},
			{0,1,1,1,1,1,1}, 
			{0,1,0,0,0,1,1}, 
			{0,0,0,1,0,1,1}, 
			{0,1,0,0,0,1,1},
			{0,1,1,1,1,1,1},
			{0,1,1,1,1,1,0}};
			return tempSegment;

		}
	}
	
	private static int[][] tJunctionSegment(){
		int choice = ran.nextInt(3);
		int Y = 0;
		int obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Y = 1;
		}

		if (choice == 0){
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{1,1,0,0,0,0,1}, 
			{1,1,0,1,1,0,1}, 
			{0,0,0,0,0,0,0}, 
			{1,0,0,1,1,Y,1},
			{0,0,0,0,1,Y,Y},
			{1,1,1,0,1,1,1}};
			return tempSegment;
		} else if (choice == 1){
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{0,0,0,0,0,0,0}, 
			{1,0,1,0,1,0,1}, 
			{0,0,1,0,1,0,0}, 
			{1,0,1,0,1,0,1},
			{1,0,0,0,0,0,1},
			{1,1,1,0,1,1,1}};
			return tempSegment;
		} else {
			int[][] tempSegment =  
			{{0,1,1,1,1,1,0},
			{1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1}, 
			{0,0,0,0,0,0,0}, 
			{1,1,1,0,1,1,1},
			{1,1,1,0,1,1,1},
			{0,1,1,0,1,1,0}};
		return tempSegment;
		}
	}

	private static int[][] bendSegment(){
		int choice = ran.nextInt(3);
		int Y = 0;
		int obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Y = 1;
		}

		if (choice == 0){
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{1,0,0,0,0,0,1}, 
			{1,0,1,0,1,0,1}, 
			{0,0,1,0,0,0,1}, 
			{1,Y,1,1,1,0,1},
			{1,0,0,0,0,0,0},
			{1,1,1,0,1,1,1}};
			return tempSegment;
		} else if (choice == 1){
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{1,0,1,1,1,1,1}, 
			{1,0,0,0,1,1,1}, 
			{0,0,0,0,0,0,Y}, 
			{1,0,0,0,1,1,1},
			{1,0,0,0,1,1,0},
			{1,1,1,0,1,1,1}};
			return tempSegment;
		} else{
			int[][] tempSegment =  
			{{0,1,1,1,1,1,0},
			{0,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1}, 
			{0,0,0,0,1,1,1}, 
			{1,1,1,0,1,1,1},
			{1,1,1,0,1,1,1},
			{0,1,1,0,1,1,0}};
			return tempSegment;
		}
	}

	private static int[][]corridorSegment(){
		int choice = ran.nextInt(5);
		int Y = 0;
		int obstacleChoice = ran.nextInt(5);
		if (obstacleChoice == 0){
			Y = 1;
		}

		if (choice == 0){
			int[][] tempSegment =  
			{{0,1,1,1,1,1,0},
			{1,1,1,1,1,1,1}, 
			{1,1,1,1,1,1,1}, 
			{0,0,0,0,0,0,0}, 
			{1,1,1,1,1,1,1},
			{1,1,1,1,1,1,1},
			{0,1,1,1,1,1,0}};
			return tempSegment;
		} else if (choice == 1){
			int[][] tempSegment =  
			{{0,1,1,1,1,1,0},
			{1,0,1,0,1,0,1}, 
			{1,0,1,0,1,0,1}, 
			{0,0,0,0,0,0,0}, 
			{1,0,1,0,1,0,1},
			{1,0,1,0,1,0,1},
			{0,1,1,1,1,1,0}};
			return tempSegment;
		} else if (choice == 2){
			int[][] tempSegment =  
			{{0,1,1,1,1,1,0},
			{1,1,1,0,0,0,1}, 
			{1,1,1,0,1,0,1}, 
			{0,0,1,0,1,0,0}, 
			{1,0,1,0,1,1,1},
			{1,0,0,0,1,1,1},
			{0,1,1,1,1,1,0}};
			return tempSegment;
		} else if (choice == 3){
			int[][] tempSegment =  
			{{0,1,1,1,1,1,0},
			{1,0,0,0,0,0,1}, 
			{1,0,1,0,1,1,1}, 
			{0,0,0,0,0,0,0}, 
			{1,0,1,0,1,0,1},
			{1,0,0,0,0,0,1},
			{0,1,1,1,1,1,0}};
			return tempSegment;
		} else{
			int[][] tempSegment =  
			{{0,1,1,1,1,1,0},
			{1,1,1,1,1,1,1}, 
			{1,0,0,0,0,0,1}, 
			{0,0,1,0,1,0,0}, 
			{1,0,0,0,0,0,1},
			{1,1,1,1,1,1,1},
			{0,1,1,1,1,1,0}};
			return tempSegment;
		}

	}



	private static int[][] topLeftCornerSegment(){
		int Y = 0;
		int obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Y = 1;
		}
		int Z = 1;
		obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Z = 0;
		}

		int[][] segment;
		int choice;

		choice = ran.nextInt(5);

		if(choice == 0){
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0}, 
			{1,0,1,1,1,0,0}, 
			{1,0,0,0,1,0,0}, 
			{1,1,1,1,1,0,0},
			{1,0,0,0,0,0,0},
			{1,0,0,0,0,0,0}};
			segment = tempSegment;
		} else if(choice == 1){
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{1,0,0,0,1,0,0}, 
			{1,0,1,0,1,0,0}, 
			{1,0,1,0,1,0,0}, 
			{1,0,1,1,1,0,0},
			{1,0,0,0,0,0,0},
			{1,0,0,0,0,0,0}};
			segment = tempSegment;
		}else if(choice == 2){
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0}, 
			{1,0,1,1,0,1,0}, 
			{1,0,1,0,0,0,0}, 
			{1,0,0,0,0,1,0},
			{1,0,1,0,1,1,0},
			{1,0,0,0,0,0,0}};
			segment = tempSegment;
		} else if(choice == 3){
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0}, 
			{1,0,1,0,1,0,0}, 
			{1,0,1,0,1,0,0}, 
			{1,0,1,0,1,0,0},
			{1,0,1,0,1,0,0},
			{1,0,0,0,0,0,0}};
			segment = tempSegment;
		}else {
			int[][] tempSegment =  
			{{1,1,1,1,1,1,1},
			{1,0,0,0,0,0,0}, 
			{1,0,1,1,1,1,0}, 
			{1,0,0,0,0,0,0}, 
			{1,0,1,1,1,1,0},
			{1,0,0,0,0,0,0},
			{1,0,0,0,0,0,0}};
			segment = tempSegment;
		}
		return segment;
	}

	private static int[][] topTJunctionSegment(){
		int Y = 0;
		int obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Y = 1;
		}
		int Z = 1;
		obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Z = 0;
		}

		int[][] segment;
		int choice;

		choice = ran.nextInt(7);

		if(choice == 0){
			int[][] tempSegment =  
			{{1,0,1,0,0,1,1},
			{1,0,1,0,0,1,1}, 
			{1,0,1,0,0,0,0}, 
			{1,0,1,1,1,1,1}, 
			{1,0,1,0,0,0,0},
			{1,0,1,0,0,1,1},
			{1,0,1,0,0,1,1}};
			segment = tempSegment;
		} else if(choice == 1){
			int[][] tempSegment =  
			{{1,0,1,0,0,1,1},
			{1,0,1,0,0,1,1}, 
			{1,0,1,0,0,0,0}, 
			{1,0,1,0,0,0,0}, 
			{1,0,1,0,0,0,0},
			{1,0,1,0,0,1,1},
			{1,0,1,0,0,1,1}};
			segment = tempSegment;
		} else if(choice == 2){
			int[][] tempSegment =  
			{{1,1,0,0,1,1,1},
			{1,0,0,0,0,1,1}, 
			{1,0,0,0,0,0,0}, 
			{1,0,1,1,1,1,1}, 
			{1,0,1,0,0,0,0},
			{1,0,1,0,0,1,1},
			{1,0,1,0,0,1,1}};
			segment = tempSegment;
		} else if(choice == 3){
			int[][] tempSegment =  
			{{1,0,1,0,1,1,1},
			{1,0,1,0,0,1,1}, 
			{1,0,1,0,0,0,0}, 
			{1,0,1,1,1,1,1}, 
			{1,0,0,0,0,0,0},
			{1,0,0,0,0,1,1},
			{1,1,0,0,0,1,1}};
			segment = tempSegment;
		} else if(choice == 4){
			int[][] tempSegment =  
			{{1,0,1,0,1,1,1},
			{1,0,0,0,0,1,1}, 
			{1,0,1,0,1,1,0}, 
			{1,0,1,0,0,0,1}, 
			{1,0,1,1,1,0,0},
			{1,0,0,0,0,0,1},
			{1,1,0,0,0,1,1}};
			segment = tempSegment;
		} else if(choice == 5){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,0,0,0,1,0,0}, 
			{1,0,1,0,1,0,0}, 
			{1,0,1,0,0,0,1}, 
			{1,0,1,0,1,1,0},
			{1,0,1,0,0,0,1},
			{1,1,0,0,0,1,1}};
			segment = tempSegment;
		} else if(choice == 6){
			int[][] tempSegment =  
			{{1,0,1,0,1,0,1},
			{1,0,0,0,1,0,0}, 
			{1,0,1,0,1,1,0}, 
			{1,0,0,0,1,0,1}, 
			{1,1,1,0,1,0,0},
			{1,0,0,0,0,0,1},
			{1,1,0,0,0,1,1}};
			segment = tempSegment;
		} else {
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,0,0,0,0,1,1}, 
			{1,0,1,0,0,0,1}, 
			{1,0,1,1,1,0,0}, 
			{1,0,1,0,0,0,0},
			{1,0,0,0,1,0,1},
			{1,1,0,0,1,1,1}};
			segment = tempSegment;
		} 
		return segment;
	}

	private static int[][] crossSegment(){
		int Y = 0;
		int obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Y = 1;
		}
		int Z = 1;
		obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Z = 0;
		}

		int[][] segment;
		int choice;

		choice = ran.nextInt(4);

		if(choice == 0){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,0,0,0,0,0,1}, 
			{1,0,1,1,1,0,1}, 
			{0,0,1,0,1,0,0}, 
			{0,0,1,1,1,0,1},
			{1,0,0,0,0,0,1},
			{1,1,1,0,1,1,1}};
			segment = tempSegment;
		} else if(choice == 1){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,0,1,0,0,0,1}, 
			{1,0,1,1,1,0,1}, 
			{0,0,0,0,1,0,0}, 
			{0,0,1,1,1,0,1},
			{1,0,0,0,0,0,1},
			{1,1,1,0,1,1,1}};
			segment = tempSegment;
		} else if(choice == 2){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,0,0,0,0,0,1}, 
			{1,1,1,1,1,0,1}, 
			{0,0,1,0,1,0,0}, 
			{0,0,1,0,1,0,1},
			{1,0,0,0,0,0,1},
			{1,1,1,0,1,1,1}};
			segment = tempSegment;
		} else {
			int[][] tempSegment =  
			{{1,0,0,0,0,1,1},
			{0,0,0,0,0,0,0}, 
			{0,0,1,0,1,0,0}, 
			{0,0,1,0,1,0,0}, 
			{0,0,1,0,1,0,0},
			{0,0,0,0,0,0,0},
			{1,0,0,0,0,0,1}};
			segment = tempSegment;
		}
		return segment;
	}


/*
	private static int[][] corridorSegment(){
		int Y = 0;
		int obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Y = 1;
		}
		int Z = 1;
		obstacleChoice = ran.nextInt(3);
		if (obstacleChoice == 0){
			Z = 0;
		}

		int[][] segment;
		int choice;

		choice = ran.nextInt(2);

		if(choice == 0){
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,1,1,0,1,1,1}, 
			{1,1,1,0,1,1,1}, 
			{1,1,Y,0,Z,1,1}, 
			{1,1,1,0,1,1,1},
			{1,1,1,0,1,1,1},
			{1,1,1,0,1,1,1}};
			segment = tempSegment;
		} else {
			int[][] tempSegment =  
			{{1,1,1,0,1,1,1},
			{1,0,0,0,1,1,1}, 
			{1,0,1,1,1,Y,1}, 
			{1,0,0,0,0,0,1}, 
			{1,Z,1,1,1,0,1},
			{1,1,1,0,0,0,1},
			{1,1,1,0,1,1,1}};
			segment = tempSegment;
		} 
		return segment;
	}

*/

	private static int[][] centreSegment(){
		int[][] segment;

		int[][] tempSegment =  
		{{0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0}, 
		{0,0,1,0,1,0,0}, 
		{0,0,0,0,0,0,0}, 
		{0,0,1,0,1,0,0},
		{0,0,0,0,0,0,0},
		{0,0,0,0,0,0,0}};
		segment = tempSegment;
		return segment;
	}


	// spin the segment... anti-clockwise?
	private static int[][] rotate(int[][]segment){
		int newWidth = segment[0].length;
		int newHeight = segment.length;
		int[][] newSegment = new int[newWidth][newHeight];
		for (int i = 0; i < newWidth; i++){
			for (int j = 0; j < newHeight; j++){
				newSegment[i][j] = segment[j][newHeight-i-1];
			}
		}
		return newSegment;
	}
	
	

}
