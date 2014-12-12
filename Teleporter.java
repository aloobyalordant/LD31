public class Teleporter extends MovingObject{

	private static int teleporterWidth = Values.teleporterWidth;
	private static int teleporterHeight = Values.teleporterHeight;


	private boolean isOnline = false;
	private boolean hasBeenEntered = false;


	public Teleporter(GridRef gr){
		super(teleporterWidth,teleporterHeight,Location.locationFromGridRef(gr, teleporterWidth, teleporterHeight), Location.locationFromGridRef(gr, teleporterWidth, teleporterHeight), 1, true);			// teleporter are epiphenomenal, in the sense that they can overlap things.
	}


	public void activate(){
		isOnline = true;
	}

	public void enter(){
		hasBeenEntered = true;
	}


	public boolean isOnline(){
		return isOnline;
	}

	public boolean hasBeenEntered(){
		return hasBeenEntered;
	}
}
