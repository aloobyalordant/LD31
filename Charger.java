public class Charger extends MovingObject{
	
	private static int chargerWidth = Values.chargerWidth;
	private static int chargerHeight = Values.chargerHeight;

	private int cooldown = 0;

	private boolean isAvailable = true;
	private boolean hasBeenGot = false;


	public Charger(GridRef gr){
		super(chargerWidth,chargerHeight,Location.locationFromGridRef(gr, chargerWidth, chargerHeight), Location.locationFromGridRef(gr, chargerWidth, chargerHeight), 1, true);			// chargers are epiphenomenal, in the sense that they can overlap things.

	}

	public Charger(Location currentLoc, Direction dir){
		super(chargerWidth,chargerHeight,currentLoc, currentLoc, 1, true);			// chargers are epiphenomenal, in the sense that they can overlap things.

	}

	public Location next(){
		if (cooldown > 0){
			cooldown--;
		}
		if (cooldown == 0){
			isAvailable = true;
		}
		return currentLoc;
	}

	public boolean isAvailable(){
		return isAvailable;
	}

	public void deplete(){
		isAvailable = false;
		cooldown = Values.chargerCooldown;
		hasBeenGot = true;
	}

	public boolean hasBeenGot(){
		return hasBeenGot;
	}
}
