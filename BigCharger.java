public class BigCharger extends MovingObject{
	
	private static int bigChargerWidth = Values.bigChargerWidth;
	private static int bigChargerHeight = Values.bigChargerHeight;

	private int cooldown = 0;

	private boolean isAvailable = false;
	private boolean hasBeenGot = false;


	public BigCharger(GridRef gr){
		super(bigChargerWidth,bigChargerHeight,Location.locationFromGridRef(gr, bigChargerWidth, bigChargerHeight), Location.locationFromGridRef(gr, bigChargerWidth, bigChargerHeight), 1, true);			// chargers are epiphenomenal, in the sense that they can overlap things.

	}

	public BigCharger(Location currentLoc, Direction dir){
		super(bigChargerWidth,bigChargerHeight,currentLoc, currentLoc, 1, true);			// chargers are epiphenomenal, in the sense that they can overlap things.

	}


	public boolean isAvailable(){
		return isAvailable;
	}


	public void release(){
		isAvailable = true;
	}

	public void collect(){
		isAvailable = false;
		hasBeenGot = true;
	}

	public boolean hasBeenGot(){
		return hasBeenGot;
	}

}
