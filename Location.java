// A point in 2d space, with an x and y co-ordinate.

public class Location{

	public int x;
	public int y;

	public Location(int x, int y){
		this.x = x;
		this.y = y;
	}

	// admin thing - figure out Location in space of an object, from a GridRef (assuming object is in the middle of that GridRef space)
	public static Location locationFromGridRef(GridRef ref, int width, int height){
		int Xoffset = (Values.blockWidth - width)/2;
		int Yoffset = (Values.blockHeight - height)/2;
		return new Location(ref.x*Values.blockWidth + Xoffset, ref.y*Values.blockHeight+Yoffset);
	}
}
