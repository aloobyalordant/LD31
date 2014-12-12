// The player character!

public class Avatar extends MovingObject{

	private static int avatarWidth = Values.avatarWidth;
	private static int avatarHeight = Values.avatarHeight;

	private int topSpeed = Values.avatarSpeed;
	private boolean alive = true;

	private Direction currentDir = Direction.DOWN;


	private GridRef previousGR;		// the nearest grid reference to the avatar for the previous frame
	private boolean footstep = false;		// whether the avatar is currently making a footstep noise
	

	public Avatar(GridRef startGrid){
		super(avatarWidth,avatarHeight,Location.locationFromGridRef(startGrid, avatarWidth, avatarHeight), Location.locationFromGridRef(startGrid, avatarWidth, avatarHeight), 2);
	}


	public Avatar(Location currentLoc){
		super(avatarWidth,avatarHeight,currentLoc, currentLoc, 2);
	}

	public Avatar(Location currentLoc, Location targetLoc){
		super (avatarWidth, avatarHeight, currentLoc, targetLoc, 2);
	}


	// Process what the object is doing for the next frame.
	// Returns the newly calculated target location.
	public Location next(boolean leftPressed, boolean rightPressed, boolean upPressed, boolean downPressed){

		// first, find out where we are now, and if we're making a footstep noise.
		// (footstep noises happen when the avatar moves from one nearest grid reference to a another)
		GridRef nearestGR = getNearestSpace();
		if (nearestGR.equals(previousGR)){
			footstep = false;
		} else {
			footstep = true;
		}
		// update 'previousGR' value.
		previousGR = nearestGR;

		int targetX = currentLoc.x;
		int targetY = currentLoc.y;
		
		if (leftPressed){
			targetX -= topSpeed;
			currentDir = Direction.LEFT;
		}
		if (rightPressed){
			targetX += topSpeed;
			currentDir = Direction.RIGHT;
		}
		if (upPressed){
			targetY -= topSpeed;
			currentDir = Direction.UP;
		}
		if (downPressed){
			targetY += topSpeed;
			currentDir = Direction.DOWN;
		}

		targetLoc = new Location(targetX, targetY);

		return targetLoc;
	}

	// take damage!
	public void hit(){
		alive = false;		// one hit kills! for now.
	}

	public void escape(){
		alive = false;		// apparently teleporting off away is the same as dying. good to know.
	}


	public boolean isAlive(){
		return alive; 
	}

	public boolean footstep(){
		return footstep;
	}

	public Direction getDirection(){
		return currentDir;
	}

}
