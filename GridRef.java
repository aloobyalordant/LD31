// A grid reference, with an x and y co-ordinate.
// no difference from Location, but by convention Locations are used for places actual places in 2d space,
// and GridRefs are used for e.g. Squares on a map.

public class GridRef{

	public int x;
	public int y;

	public GridRef(int x, int y){
		this.x = x;
		this.y = y;
	}

	
	// return the GridRef of the space an object is fully contained in, if it exists (otherwise return null)
	public static GridRef spaceContaining(Location l, int width, int height){
		// are l.x and lx+width-1 on different sides of a multiple of blockWidth? Because if so it's not contained in anything.
		int temp1 = l.x/Values.blockWidth;
		int temp2 = (l.x+width-1)/Values.blockWidth;
		if (temp1 != temp2){
			return null;
		}
		int temp3 = l.y/Values.blockHeight;
		int temp4 = (l.y+height-1)/Values.blockHeight;
		if (temp3 != temp4){
			return null;
		}
		// otherwise, well we know where it is don't we?
		return new GridRef(temp1, temp3);
	}
	

	public static GridRef nearestSpace(Location l, int width, int height){
		int temp1 = l.x/Values.blockWidth;
		int temp2 = (l.x+width-1)/Values.blockWidth;
		int xCoord;
		if (temp1 == temp2){
			xCoord = temp1;
		} else {
			if (Math.abs(l.x - temp2*Values.blockWidth) <= Math.abs(l.x + width-1 - temp2*Values.blockWidth)){
				xCoord = temp2;
			} else {
				xCoord = temp1;
			}
		}
		int temp3 = l.y/Values.blockHeight;
		int temp4 = (l.y+height-1)/Values.blockHeight;
		int yCoord;
		if (temp3 == temp4){
			yCoord = temp3;
		} else {
			if (Math.abs(l.y - temp4*Values.blockHeight) <= Math.abs(l.y + height-1 - temp4*Values.blockHeight)){
				yCoord = temp4;
			} else {
				yCoord = temp3;
			}
		}
		return new GridRef (xCoord, yCoord);
	}

	public boolean equals(GridRef ref){
		if (ref == null){
			return false;
		} else {
			return (x == ref.x && y == ref.y);
		}
	}

}
