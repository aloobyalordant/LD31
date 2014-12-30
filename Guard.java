import java.util.ArrayList;
import java.util.Random;

public class Guard extends MovingObject{

	private static int guardWidth = Values.avatarWidth;
	private static int guardHeight = Values.avatarHeight;

	private int topSpeed = Values.guardSlowSpeed;
	private boolean alive = true;
	
	private boolean alert = false;
	private GridRef lastKnownPreyWhereabouts;	// Grid Ref of where the avatar was ast heard
	private int alertCoolDown = 0;			// how long until the guard stops being alert
	
	private int paralysedByRageCooldown = 0;	// how long until guard starts moving (after having just heard the explorer)

	private boolean isDormant = true;
	private int dormantCooldown = Values.guardDormantCooldown;	// Guards are dormant for a little bit when they first appear, can't interact.


	private GridRef previousGR;		// the nearest grid reference to the guard for the previous frame
	private int footstepSound = 0; 				// int from 0-2. Decides which footstep sound the guard has.
	private boolean footstep = false;		// whether the avatar is currently making a footstep noise

	private Map personalMap;	// the guard's map of where everything is, which they will update sporadically.
	private Direction currentDirection;
	private GridRef prevGridRef;	// where the guard just came from
	private GridRef	currentGridRef;	//where the guard currently is (on the map)
	private GridRef nextGridRef;	//where the guard next wants to go.

	private Random ran;		// the guard's own personal number generator


	public Guard(GridRef startGrid, Map mapData){
		super(guardWidth,guardHeight, Location.locationFromGridRef(startGrid, guardWidth, guardHeight), Location.locationFromGridRef(startGrid, guardWidth, guardHeight), 2);
		personalMap = new Map(mapData);
		currentDirection = Direction.UP;
		ran = new Random();
		footstepSound = ran.nextInt(2);
		//int alertVal = ran.nextInt(4);
		//if (alertVal == 3){
		//	alert = true;
		//}
	}


//	public Guard(Location currentLoc, Map mapData){
//		super(guardWidth,guardHeight,currentLoc);
//		personalMap = new Map(mapData);
//		currentDirection = Direction.UP;
//	}

//	public Guard(Location currentLoc, Location targetLoc, Map mapData){
//		super (guardWidth, guardHeight, currentLoc, targetLoc);
//		personalMap = new Map(mapData);
//		currentDirection = Direction.UP;
//	}

	// Process what the object is doing for the next frame.
	// Returns the newly calculated target location.
	public Location next(){

		// first, find out where we are now, and if we're making a footstep noise.
		// (footstep noises happen when the guard moves from one nearest grid reference to a another)
		GridRef nearestGR = getNearestSpace();
		if (nearestGR.equals(previousGR)){
			footstep = false;
		} else {
			footstep = true;
		}
		// update 'previousGR' value.
		previousGR = nearestGR;


		if (isDormant){
			dormantCooldown--;
			if (dormantCooldown == 0){
				isDormant = false;
			}
		}

		if (alert){
			topSpeed = Values.guardFastSpeed;
		} else {
			topSpeed = Values.guardSlowSpeed;
		}

		// if we've got to where the avatar was meant to be, set lastKnownPreyWherabouts to null
		if (lastKnownPreyWhereabouts != null && lastKnownPreyWhereabouts.equals(GridRef.spaceContaining(currentLoc, guardWidth, guardHeight))){
			lastKnownPreyWhereabouts = null;
			alertCoolDown = Math.min(alertCoolDown, Values.disappointedGuardAlertCooldown);
		}
		if (alertCoolDown > 0){
			alertCoolDown--;
		}
		if (alertCoolDown == 0){
			alert = false;
		}

	
		// decide the location we're next aiming for (which will be the center of some space nearby
		decideNextGridRef();
		Location nextSpaceLoc = Location.locationFromGridRef(nextGridRef,guardWidth,guardHeight);
	

		// now figure out how close you can get to that, based on current location and speed.
		int Xdiff = 0;
		int Ydiff = 0;
	
		if (nextSpaceLoc.x > currentLoc.x){
			Xdiff = Math.min(topSpeed, nextSpaceLoc.x-currentLoc.x);	
		} else if (nextSpaceLoc.x < currentLoc.x){
			Xdiff = -Math.min(topSpeed, currentLoc.x-nextSpaceLoc.x);	
		}

		if (nextSpaceLoc.y > currentLoc.y){
			Ydiff = Math.min(topSpeed, nextSpaceLoc.y-currentLoc.y);	
		} else if (nextSpaceLoc.y < currentLoc.y){
			Ydiff = -Math.min(topSpeed, currentLoc.y-nextSpaceLoc.y);	
		}

		targetLoc = new Location(currentLoc.x + Xdiff, currentLoc.y + Ydiff);

		// if however the guard is paralysed by rage at just having noticed an intruder,
		// ignore all that and have them stand still
		if (paralysedByRageCooldown >0){
			targetLoc = currentLoc;
			paralysedByRageCooldown--;
		} else if (isDormant){
			targetLoc = currentLoc;
		}

		return targetLoc;
	}


	// based on things like where the avatar is, or mayne just wandering around, pick the next grid reference to aim for.
	private void decideNextGridRef(){
		// find out which space we're currently contained in
		GridRef currentlyIn = GridRef.spaceContaining(currentLoc, guardWidth, guardHeight);
		// if we are currently not contained in any single space, keep going where we're going.	
		// otherwise, maybe do things
		if (currentlyIn != null){
			// if we're where were going, or if we haven't even picked a grid ref yet, we need to decide a new grid ref.
			if (nextGridRef == null || nextGridRef.equals(currentlyIn) ){

				// we will make a list of possible grid refs (i.e. adjacent ones not blocked off), weighted by likelihood of picking them. Straight ahead gets priority, then sides. Back the way we came only gets a look-in if the other ways are blocked off.

				ArrayList<GridRef> options = new ArrayList<GridRef>();
				ArrayList<Direction> optionDirections = new ArrayList<Direction>();
				GridRef GRnorth = new GridRef(currentlyIn.x, currentlyIn.y-1);
				GridRef GRsouth = new GridRef(currentlyIn.x, currentlyIn.y+1);
				GridRef GReast = new GridRef(currentlyIn.x+1, currentlyIn.y);
				GridRef GRwest = new GridRef(currentlyIn.x-1, currentlyIn.y);
				GridRef GRforward;
				GridRef GRback;
				GridRef GRleft;
				GridRef GRright;
				Direction DirForward;
				Direction DirBack;
				Direction DirLeft;
				Direction DirRight;

				switch(currentDirection){
					case UP:
						GRforward = GRnorth;
						GRback = GRsouth;
						GRleft = GRwest;
						GRright = GReast;
						DirForward = Direction.UP;
						DirBack = Direction.DOWN;
						DirLeft= Direction.LEFT;
						DirRight = Direction.RIGHT;
						break;
					case DOWN:
						GRforward = GRsouth;
						GRback = GRnorth;
						GRleft = GReast;
						GRright = GRwest;
						DirForward = Direction.DOWN;
						DirBack = Direction.UP;
						DirLeft= Direction.RIGHT;
						DirRight = Direction.LEFT;
						break;
					case LEFT:
						GRforward = GRwest;
						GRback = GReast;
						GRleft = GRsouth;
						GRright = GRnorth;
						DirForward = Direction.LEFT;
						DirBack = Direction.RIGHT;
						DirLeft= Direction.DOWN;
						DirRight = Direction.UP;
						break;
					default: //(RIGHT)
						GRforward = GReast;
						GRback = GRwest;
						GRleft = GRnorth;
						GRright = GRsouth;
						DirForward = Direction.RIGHT;
						DirBack = Direction.LEFT;
						DirLeft= Direction.UP;
						DirRight = Direction.DOWN;
						break;
				}
		
				// two probabilistic votes for forward, if it's available
				if (goodOption(currentlyIn, GRforward)){
					options.add(GRforward);
					options.add(GRforward);
					optionDirections.add(DirForward);
					optionDirections.add(DirForward);
				}
				// one vote for left
				if (goodOption(currentlyIn, GRleft)){
					options.add(GRleft);
					optionDirections.add(DirLeft);
				}
				// one vote for right
				if (goodOption(currentlyIn, GRright)){
					options.add(GRright);
					optionDirections.add(DirRight);
				}

				// backwards only gets a look-in if no other options are available.
				if (options.size() == 0){
					options.add(GRback);
					optionDirections.add(DirBack);
				}

				// finally, pick one of the available options at random
				int newDirection = ran.nextInt(options.size());
				
				nextGridRef = options.get(newDirection);
				currentDirection = optionDirections.get(newDirection);

			}
		} else {
			// ok this here is some insurance code, just in case for some reason you start off halfway between two spaces with no nextGridRef set.
			if (nextGridRef == null){
				nextGridRef = new GridRef(0,0);
			}
		}
	}


	// Decides if newGR is a good space to aim for next, based on 
	// (a) whether there is actually a space there and not an obstacle
	// (b) whether it will get the guard nearer to the known prey location, if the guard is alert and knows a location
	// (currentGR = current GridRef of guard)
	private boolean goodOption(GridRef currentGR, GridRef newGR){
		if (personalMap.getMapDataAt(newGR) != 0){
			return false;
		}
		if (!alert || lastKnownPreyWhereabouts == null){
			return true;
		}
		double currentDist = personalMap.shortestDistance(currentGR.x,currentGR.y, lastKnownPreyWhereabouts.x, lastKnownPreyWhereabouts.y);
		if(currentDist == Double.POSITIVE_INFINITY){
			return true;
		}
		double newDist = personalMap.shortestDistance(newGR.x,newGR.y, lastKnownPreyWhereabouts.x, lastKnownPreyWhereabouts.y);
		if (newDist < currentDist){
			return true;
		} else {
			return false;
		}
	}


	public boolean isAlive(){
		return alive; 
	}

	// take damage!
	public void hit(){
		alive = false;		// one hit kills! for now.
	}

	public void switchAlert(){
		alert = !alert;
	}	

	public void hearNoise(GridRef noiseLocation){
		if (!alert){
			paralysedByRageCooldown = Values.guardParalysedByRageCooldown;
			alert = true;
		}
		if (noiseLocation != null){
			lastKnownPreyWhereabouts = noiseLocation;
		}
		alertCoolDown = Math.max(alertCoolDown, Values.defaultGuardAlertCooldown);
	}

	public void hearLoudNoise(GridRef noiseLocation){
		if (!alert){
			paralysedByRageCooldown = Values.guardParalysedByRageCooldown;
			alert = true;
		}
		if (noiseLocation != null){
			lastKnownPreyWhereabouts = noiseLocation;
		}
		alertCoolDown = Math.max(alertCoolDown, Values.loudGuardAlertCooldown);
	}

	public void updateMap(Map mapData){
		personalMap = new Map(mapData);
	}


	public boolean isAlert(){
		return alert;
	}

	public boolean isDormant(){
		return isDormant;
	}

	public boolean footstep(){
		return footstep;
	}

	public int getFootstepSound(){
		return footstepSound;
	}
}

