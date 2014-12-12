// Collision Detection engine: ATOMISM
//
// Hey hey, it's my first attempt at something like a game engine!
// Arguably I should finish my first game before my first game engine, but whatevs
//
// Handles rectangular objects, each with a current location and a target location.
// The engine assumes that objects can never overlap, and processes conflicts in the target locations to decide what ends up where
// Each object has a rank. 
// Roughly speaking, lower ranked objects are 'heavier', and higher ranked objects bounce off them.
// Lower ranked objects are processed first, and get priority in where they want to be.
// An object will only be stopped if it's going to hit objects of a lower rank.
// Objects of the same rank will never bump off each other and may end up overlapping.
//
// Required classes: CollisionDetector, MovingObject, Location

import java.util.ArrayList;
public class CollisionDetector{


	static final boolean equalRankCollisions = false;		// do two objects hit if they're the same rank?
														// my programming for this is probably going to be a bit janky.

	// Give collision detector your full list of MovingObjects, each with currentLoc and targetLoc set, and this method will figure out where they should all go and update their currentLoc values accordingly.
	public static void Process(ArrayList<MovingObject> objectList){
		// find the maximum rank the objects go up to.
		int maxRank = 0;
		for(MovingObject mo: objectList){
			int obRank = mo.getRank();
			if (obRank > maxRank){
				maxRank = obRank;
			}
		}

		//process objects in order of rank
		ArrayList<MovingObject> processedObjects = new ArrayList<MovingObject>();
		for (int r = 0; r<= maxRank; r++){
			for (MovingObject ob: objectList){
				if (ob.getRank() == r){
					if(r == 0){			// immovable objects get to skip the whole processing stage, so save some time.
						ob.setNewLoc(ob.getCurrentLoc());
					} else {
						ProcessObject(ob,r,processedObjects, objectList);
					}
					processedObjects.add(ob);
				}
			}
		}
		
		//do final moves for all objects
		for (MovingObject ob: objectList){
			ob.finalMove();
		}
	}
	
	// do collision detection for an object of given rank
	private static void ProcessObject(MovingObject mo, int rank, ArrayList<MovingObject> processedObjects, ArrayList<MovingObject> objectList){

		// The first thing we do is see if mo has been 'pushed' by any lower-ranked objects.
		// The location that mo wants to end up after being pushed
		Location pushTarget = pushObject(mo, rank, processedObjects);
		// create a new ghost object that tries to move in the push direction, and see how far it gets.
		Location currentLoc = mo.getCurrentLoc();
		Location targetLoc  = mo.getTargetLoc();
		Location pushedLoc;		// where mo will actually end up after pushing
		Location newPushedTarget;	// mo's adjusted target after being pushed
		// if no actual pushing happened, things are simple.
		if(pushTarget.x == currentLoc.x && pushTarget.y == currentLoc.y){
			pushedLoc = currentLoc;
			newPushedTarget = targetLoc;
		} else {
			GhostObject moBeingPushed = new GhostObject(mo.getWidth(), mo.getHeight(), currentLoc, pushTarget, rank, mo, mo.isEpiphenomenal());
			pushedLoc = travelTwoDirections(moBeingPushed, processedObjects, objectList);
			int horizPush = pushedLoc.x - currentLoc.x;
			int vertPush = pushedLoc.y - currentLoc.y;
			newPushedTarget = new Location(targetLoc.x + horizPush, targetLoc.y + vertPush);
		}
		


		MovingObject pushedMo = new GhostObject(mo.getWidth(), mo.getHeight(), pushedLoc, newPushedTarget, mo.getRank(), mo, mo.isEpiphenomenal());

		// boolean indicating whether mo is on track to overlap any lower-ranked objects
		boolean conflict = false;
		for (MovingObject bob: processedObjects){
			if(bob.getRank() < rank || (bob.getRank() == rank && equalRankCollisions)){
				// check if where mo wants to be conflicts with where bob is.
				if(checkOverlap(pushedMo, true, bob, true)){
					conflict = true;
				}
			} 
		}
		// now check if mo is on track to overlap any equal-ranked objects (if equalRankCollisions is set to true)
		if (equalRankCollisions && !conflict){
			for (MovingObject bob: objectList){
				if(!bob.isEpiphenomenal() && bob.getRank() == rank && rank != 0 && (bob.getCurrentLoc().x != mo.getCurrentLoc().x || bob.getCurrentLoc().y != mo.getCurrentLoc().y)){		// make sure they're not the same object...
					// check if where mo wants to be conflicts with where bob is / was before processing.
					if(checkOverlap(pushedMo, true, bob, false)){
	//					System.out.println("EQUALITY CONFLICT!" + bob.getRank() + " " + rank + "(" + bob.getCurrentLoc().x + "," + bob.getCurrentLoc().y + ") (" + pushedMo.getTargetLoc().x + "," + pushedMo.getTargetLoc().y + ")" );
						conflict = true;
					}
				}
			}
		}

		if (conflict){
			pushedMo.setNewLoc(travelTwoDirections(pushedMo, processedObjects, objectList));
		} else {
			// if no conflict, mo can go on her way.
			pushedMo.setNewLoc(pushedMo.getTargetLoc());
		}
		// Finally update the real Mo based on where pushedMo ended up.
		mo.setNewLoc(pushedMo.getNewLoc());
	}


	// Sees if mo would get pushed by any lower-ranked objects if tried to stay where it was.
	// Returns the new location mo would be pushed to.
	// (rank == rank of mo)
	private static Location pushObject(MovingObject mo, int rank, ArrayList<MovingObject> processedObjects){
		Location currentLoc = mo.getCurrentLoc();

		// first check horizontal pushing
		// we go through all objects that could push mo and store our best candidate..
		// How ranking candidates works:
		// if one object pushes mo left and one pushes her right, the lower-ranked object gets priority
		//		(for ties, whoever go there first has priority I guess)
		// if two objects are pushing mo in the same direction, the one that pushes further gets priority.
		// so rank determines direction and then value determines distance, I guess.
		// Anyway, in practice we change direction if a higher rank comes along, and otherwise just push as far as asked.
		int topPusherRank = rank;		// rank of the lowest-ranked object to push mo
		int topNegativeValue = 0;				// biggest push to the left
		int topPositiveValue = 0;				// biggest push to the right
		int topPushValue = 0;					// the current value of the horizontal push.
		int tempPushValue = 0;
		int tempPusherRank = rank;
		for (MovingObject bob: processedObjects){
			if (!bob.isEpiphenomenal()){
				tempPusherRank = bob.getRank();
				if (tempPusherRank < rank){
					if(checkInTheWay(bob, mo, Direction.LEFT, true, false)){
						tempPushValue = getPushValue(bob, mo, Direction.LEFT);
					} else if (checkInTheWay(bob, mo, Direction.RIGHT, true, false)){
						tempPushValue = getPushValue(bob, mo, Direction.RIGHT);
					}
					// if the current 'biggest' push is to the right:
					if (topPushValue >= 0){
						//push further right if bob pushes further right
						if (tempPushValue > topPositiveValue){
							topPositiveValue = tempPushValue;
							topPushValue = tempPushValue;
						}
						// otherwise, only do anything if bob is pushing left and is heavier than any previous pushers
						else if (tempPusherRank < topPusherRank && tempPushValue < 0){
							// change of plans! We're moving left!
							topPushValue = topNegativeValue;
							if (tempPushValue < topNegativeValue){
								topNegativeValue = tempPushValue;
								topPushValue = tempPushValue;
							}
						}
					} 	
					// otherwise, current 'biggest' push is to the left:
					else	{
						//push further left if bob pushes further left 
						if (tempPushValue < topNegativeValue){
							topNegativeValue = tempPushValue;
							topPushValue = tempPushValue;
						}
						// otherwise, only do anything if bob is pushing right and is heavier than any previous pushers
						else if (tempPusherRank < topPusherRank && tempPushValue > 0){
							// change of plans! We're moving right!
							topPushValue = topPositiveValue;
							if (tempPushValue > topPositiveValue){
								topPositiveValue = tempPushValue;
								topPushValue = tempPushValue;
							}
						}
					}
					// in any case, update the pusher rank 
					if(tempPushValue != 0 && tempPusherRank < topPusherRank){
						topPusherRank = tempPusherRank;
					}
				}
			}
		}
		// at the end, the horizontal push is the final number we ended up with.
		int horizPush = topPushValue;

		// Ok now do all that again but for vertical
		topPusherRank = rank;		// rank of the lowest-ranked object to push mo
		topNegativeValue = 0;				// biggest push to the up
		topPositiveValue = 0;				// biggest push to the down
		topPushValue = 0;					// the current value of the horizontal push.
		tempPushValue = 0;
		tempPusherRank = rank;

		for (MovingObject bob: processedObjects){
			if (!bob.isEpiphenomenal()){
				tempPusherRank = bob.getRank();
				if (tempPusherRank < rank){
					if(checkInTheWay(bob, mo, Direction.UP, true, false)){
						tempPushValue = getPushValue(bob, mo, Direction.UP);
					} else if (checkInTheWay(bob, mo, Direction.DOWN, true, false)){
						tempPushValue = getPushValue(bob, mo, Direction.DOWN);
					}
					// if the current 'biggest' push is down:
					if (topPushValue >= 0){
						//push further down if bob pushes further down
						if (tempPushValue > topPositiveValue){
							topPositiveValue = tempPushValue;
							topPushValue = tempPushValue;
						}
						// otherwise, only do anything if bob is pushing up and is heavier than any previous pushers
						else if (tempPusherRank < topPusherRank && tempPushValue < 0){
							// change of plans! We're moving up!
							topPushValue = topNegativeValue;
							if (tempPushValue < topNegativeValue){
								topNegativeValue = tempPushValue;
								topPushValue = tempPushValue;
							}
						}
					} 	
					// otherwise, current 'biggest' push up:
					else	{
						//push further up if bob pushes further up 
						if (tempPushValue < topNegativeValue){
							topNegativeValue = tempPushValue;
							topPushValue = tempPushValue;
						}
						// otherwise, only do anything if bob is pushing down and is heavier than any previous pushers
						else if (tempPusherRank < topPusherRank && tempPushValue > 0){
							// change of plans! We're moving down!
							topPushValue = topPositiveValue;
							if (tempPushValue > topPositiveValue){
								topPositiveValue = tempPushValue;
								topPushValue = tempPushValue;
							}
						}
					}
					// in any case, update the pusher rank 
					if(tempPushValue != 0 && tempPusherRank < topPusherRank){
						topPusherRank = tempPusherRank;
					}
				}
			}
		}
		// at the end, the horizontal push is the final number we ended up with.
		int vertPush = topPushValue;

		return new Location(currentLoc.x + horizPush, currentLoc.y + vertPush);
	}

	// Assuming mo (being processed) is standing in the way of bob (already processed) and has a higher rank, 
	// figure out how far bob will 'push' mo in the specified direction.
	// (based on bob's 'new' location and mo's 'current' location
	private static int getPushValue(MovingObject bob, MovingObject mo, Direction dir){
		int newCoord;	// the cord (in the x or y axis, depending on dir) of the location mo would end up in after being pushed.
		int pushValue;	// the difference between newCoord and mo's original location
		switch(dir){
			case UP:
				newCoord = bob.getNewLoc().y - mo.getHeight();
				pushValue = newCoord - mo.getCurrentLoc().y;
				break;
			case DOWN:
				newCoord = bob.getNewLoc().y + bob.getHeight();
				pushValue = newCoord - mo.getCurrentLoc().y;
				break;
			case LEFT:
				newCoord = bob.getNewLoc().x - mo.getWidth();
				pushValue = newCoord - mo.getCurrentLoc().x;
				break;
			default:
				newCoord = bob.getNewLoc().x + bob.getWidth();
				pushValue = newCoord - mo.getCurrentLoc().x;
				break;
		}
		// sanity check: push value should be positive if pushing down or right, negative if pushing up or left
		return pushValue;
	}

	// Return the point that an object moving strictly up, down left or right should end up at, based on obstacles in the way.
	private static Location travelOneDirection(MovingObject mo, ArrayList<MovingObject> processedObjects, ArrayList<MovingObject> objectList){
		Location current = mo.getCurrentLoc();
		Location target = mo.getTargetLoc();
		Location newLoc;
		// we assume moving in one cardinal direction - i.e. if one co-ordinate changes, the other stays the same.
		//moving UP
		if (target.y < current.y){
			newLoc =  new Location(target.x, firstObstacle(mo, Direction.UP, processedObjects, objectList)+1);
			if (newLoc.y != target.y){
	//			mo.bounceVertically();		// so we may assume it bounced off the vertical obstacle
			}
		} else 
		// moving DOWN
		if (target.y > current.y){
			newLoc = new Location(target.x, firstObstacle(mo,Direction.DOWN, processedObjects, objectList)-mo.getHeight());
			if (newLoc.y != target.y){
	//			mo.bounceVertically();		// so we may assume it bounced off the vertical obstacle
			}
		} else 
		// moving LEFT
		if (target.x < current.x){
			newLoc = new Location(firstObstacle(mo,Direction.LEFT, processedObjects, objectList)+1, target.y);
			if (newLoc.x != target.x){
	//			mo.bounceHorizontally();		// so we may assume it bounced off the vertical obstacle
			}
		} 
		//moving RIGHT
		else {
			newLoc =  new Location(firstObstacle(mo, Direction.RIGHT, processedObjects, objectList)-mo.getWidth(), target.y);
			if (newLoc.x != target.x){
	//			mo.bounceHorizontally();		// so we may assume it bounced off the vertical obstacle
			}
		}
		return newLoc;
	}

	// Return the point that an object moving up and left, down and right etc should end up at, based on obstacles in the way.
	private static Location travelTwoDirections(MovingObject mo, ArrayList<MovingObject> processedObjects, ArrayList<MovingObject> objectList){

		Location current = mo.getCurrentLoc();
		Location target = mo.getTargetLoc();

		// first off, handle the easy cases, which also happen to be the cases that'll cause divide-by-zero errors if we ignore them: mo is actually travelling straight up, down, left or right.
		if (target.x == current.x || target.y == current.y){
			return travelOneDirection(mo, processedObjects, objectList);
		}


		int horizStop;	// the x-coordinate the object would end up at if there were only 'horizontal' objects in the way.
		int vertStop;	// the y-coordinate the object would end up at if there were only 'vertical' objects in the way.

		// vertical movement - find the vertical stopping point
		if(target.y < current.y){
			vertStop = firstObstacle(mo, Direction.UP, processedObjects, objectList)+1;
		} else {
			vertStop = firstObstacle(mo,Direction.DOWN, processedObjects, objectList)-mo.getHeight();
		}
		
		//horizontal movement - find the horizontal stopping point
		if (target.x < current.x){
			horizStop = firstObstacle(mo, Direction.LEFT, processedObjects, objectList)+1;
		} else {
			horizStop = firstObstacle(mo, Direction.RIGHT, processedObjects, objectList) - mo.getWidth();
		}

		// ok, now we just have to figure out which of those stopping points is the one we'd reach first.
		// oh and calculate the other co-ordinate
		float horizFraction = (horizStop - current.x)/(target.x - current.x);
		float vertFraction = (vertStop - current.y)/(target.y - current.y);
	


		//first obstacle is the vertical one!
		if ((vertStop - current.y)/(target.y - current.y) < (horizStop - current.x)/(target.x - current.x)){
	//		mo.bounceVertically();
			Location collisionLoc = new Location(Math.round(current.x + vertFraction* (target.x-current.x)), vertStop);
			// At this point, there is still some horizontal movement that can happen as mo slides along the obstacle.
			Location newTarget = new Location(target.x, vertStop);
			MovingObject tempMo = new MovingObject(mo.getWidth(), mo.getHeight(), collisionLoc, newTarget, mo.getRank(), mo.isEpiphenomenal());
			return travelOneDirection(tempMo, processedObjects, objectList);
		// first obstacle is the horizontal one!
		} else if  ((horizStop - current.x)/(target.x - current.x)< (vertStop - current.y)/(target.y - current.y)){
	//		mo.bounceHorizontally();
			Location collisionLoc =  new Location(horizStop, Math.round(current.y + horizFraction*(target.y - current.y)));
			// At this point, there is still some vertical movement that can happen as mo slides along the obstacle.
			Location newTarget = new Location(horizStop, target.y);
			MovingObject tempMo = new MovingObject(mo.getWidth(), mo.getHeight(), collisionLoc, newTarget, mo.getRank(), mo.isEpiphenomenal());
			return travelOneDirection(tempMo, processedObjects, objectList);
		} else {			// technically we'd hit both at the same time! This comes up when running along floors or jumping into walls. Not sure what to do here yet. Calling it one way or the other leads to you either getting stuck against floors or against walls. Hmmmm.
			// ok, let's just try going both ways  and see if either works..
			Location collisionLoc =  new Location(horizStop, vertStop);
			Location newTarget1 = new Location(target.x, vertStop);
			MovingObject tempMo1 = new MovingObject(mo.getWidth(), mo.getHeight(), collisionLoc, newTarget1, mo.getRank(), mo.isEpiphenomenal());
			Location loc1 =  travelOneDirection(tempMo1, processedObjects, objectList);
			if (loc1.x != collisionLoc.x || loc1.y != collisionLoc.y){	// if moving horizontally is possible, do that
				if (vertStop != target.y){
	//				mo.bounceVertically();		// so we may assume it bounced off the vertical obstacle
				}
				return loc1;
			} else {					// otherwise, move vertically
				if (horizStop != target.x){
	//				mo.bounceHorizontally();		// so we may assume it bounced off the horizontal obstacle					
				}
				Location newTarget2 = new Location(horizStop, target.y);
				MovingObject tempMo2 = new MovingObject(mo.getWidth(), mo.getHeight(), collisionLoc, newTarget2, mo.getRank(), mo.isEpiphenomenal());
				Location loc2 =  travelOneDirection(tempMo2, processedObjects, objectList);
				return loc2;
			}
		}
	}

	
	// returns the x / y (as appropriate co-ordinate of the nearest edge of an object that will be an obstacle to mo travelling in a given direction.
	// eg. if dir == UP, returns the y-co-ordinate of the underside of the first object that mo looks likely to bang her head on.
	private static int firstObstacle(MovingObject mo, Direction dir, ArrayList<MovingObject> processedObjects, ArrayList<MovingObject> objectList){
		Location current = mo.getCurrentLoc();
		Location target = mo.getTargetLoc();

		int furthestOut; // the 'obstacle representing the border of where mo would end up if there were no obstacles.
		int nearestObstacle;
		int startingLine; // where mo's leading edge starts. Obstacles have to be between this and furthestOut (non-inclusive) to get in the way
		int tempVal;		// the line for a currently considered object
		int directionFlip = 1;	// set to 1 if travelling down or right, set to -1 if travelling up or left.

		// first up, find the 'furthest out' value.
		switch(dir){
			case UP:
				furthestOut = target.y-1;
				startingLine = current.y;
				directionFlip = -1;
				break;
			case DOWN:
				furthestOut = target.y + mo.getHeight();
				startingLine = current.y + mo.getHeight()-1;
				break;
			case LEFT:
				furthestOut = target.x-1;
				startingLine = current.x;
				directionFlip = -1;
				break;
			case RIGHT:
				furthestOut = target.x + mo.getWidth();
				startingLine = current.x+mo.getWidth()-1;
				break;
			default:	// i dunno, imagine we're going right I guess
				furthestOut = target.x + mo.getWidth();
				startingLine = current.x+mo.getWidth()-1;
				break;
		}

		// now we find the relevant lines for each potential obstacle, and see if they're closer than the current best.
		nearestObstacle = furthestOut;
		for(MovingObject bob: processedObjects){
			if(!bob.isEpiphenomenal()){
				switch(dir){
					case UP:
						tempVal = bob.getNewLoc().y+bob.getHeight()-1;
						break;
					case DOWN:
						tempVal = bob.getNewLoc().y;
						break;
					case LEFT:
						tempVal = bob.getNewLoc().x + bob.getWidth()-1;
						break;
					case RIGHT:
						tempVal = bob.getNewLoc().x;
						break;
					default:	// i dunno, imagine we're going right i guess
						tempVal = bob.getNewLoc().x;
						break;	
				}
				// now check if this value is between the 'startingLine' and 'furthestOut' values.
				if (directionFlip*tempVal< directionFlip*nearestObstacle && directionFlip*tempVal > directionFlip*startingLine){
					// if so (i.e. if bob is potentially a nearer obstacle than current), see if it is indeed an obstacle!
					if (checkObstacle(mo, bob, dir)){
						nearestObstacle = tempVal;
					}
				}
			}
		}

		// ok, now if we're doing equal rank collisions, we have to do a similar thing but for equal ranked (possibly unprocessed) objects in objectList
		if (equalRankCollisions){
			for(MovingObject bob: objectList){
				if (!bob.isEpiphenomenal() && bob.getRank() == mo.getRank() && (bob.getCurrentLoc().x != mo.getCurrentLoc().x || bob.getCurrentLoc().y != mo.getCurrentLoc().y)){
					switch(dir){
						case UP:
							tempVal = bob.getCurrentLoc().y+bob.getHeight()-1;
							break;
						case DOWN:
							tempVal = bob.getCurrentLoc().y;
							break;
						case LEFT:
							tempVal = bob.getCurrentLoc().x + bob.getWidth()-1;
							break;
						case RIGHT:
							tempVal = bob.getCurrentLoc().x;
							break;
						default:	// i dunno, imagine we're going right i guess
							tempVal = bob.getCurrentLoc().x;
							break;	
					}	
					// now check if this value is between the 'startingLine' and 'furthestOut' values.
					if (directionFlip*tempVal< directionFlip*nearestObstacle && directionFlip*tempVal > directionFlip*startingLine){
						// if so (i.e. if bob is potentially a nearer obstacle than current), see if it is indeed an obstacle!
						if (checkObstacle(mo, bob, dir)){
						//	System.out.println("Equality obstacle!");
							nearestObstacle = tempVal;
						}
					}
				}
			}
		}

		// ok so now we've found the nearest obstacle point for this direction! woooo
		return nearestObstacle;
	}


	// a public method to check if two objects are overlapping (called beforecollision detection occurs)
	public static boolean checkObjectsOverlap(MovingObject ob1, MovingObject ob2){
		return checkOverlap(ob1, false, ob2, false);
	}


	// Do two objects overlap?
	// assume object 2 has been processed and object 1 is being processed
	// ob1targetLoc indicates whether to check ob1's target position (true) or current position (false)
	// ob2newLoc indicates whether to consider ob2's new position (true) or original position (false)
	private static boolean checkOverlap(MovingObject ob1, boolean ob1targetLoc, MovingObject ob2, boolean ob2newLoc){
		// initialise locations, based on whether we want target or current for each object		
		Location loc1;
		Location loc2;
		if (ob1targetLoc){
			loc1 = ob1.getTargetLoc();
		} else {
			loc1 = ob1.getCurrentLoc();
		}
		if(ob2newLoc){
			loc2 = ob2.getNewLoc();
		} else {
			loc2 = ob2.getCurrentLoc();
		}
		// case: ob1 is to the left of ob2
		if (loc1.x + ob1.getWidth()-1 < loc2.x){
			return false;
		}
		// case: ob1 is to the right of ob2
		if (loc2.x + ob2.getWidth()-1 < loc1.x){
			return false;
		}
		// case: ob1 is above ob2
		if (loc1.y + ob1.getHeight()-1 < loc2.y){
			return false;
		}
		// case: ob1 is below ob2
		if (loc2.y + ob2.getHeight()-1 < loc1.y){
			return false;
		}
		//If none of these cases hold, they overlap! Because everything is a rectangle.
		return true;
	}

	// Given an object ob1 (currently being processed) and an object ob2 (previously processed),
	// is ob2 (in its new location) going to be an obstacle for the movement of ob1?
	private static boolean checkObstacle(MovingObject ob1, MovingObject ob2, Direction dir){
		// if ob2 is epiphenomenal, it can't count as an obstacle.
		if (ob2.isEpiphenomenal()){
			return false;
		}
		// ob2 can only count as an obstacle if it has lower rank than ob1
		// UNLESS we're doing the EqualRankCollisions thing.
		if (ob2.getRank() > ob1.getRank() || (ob2.getRank() >= ob1.getRank() && !equalRankCollisions)){
			return false;
		}	
		if (!countsAsObstacle(ob1, ob2, dir)){
			return false;
		} 

		if (equalRankCollisions && ob1.getRank() == ob2.getRank()){
			//System.out.println("hello");
			if ( checkInTheWay(ob1,ob2, dir, false, true) || checkInTheWay(ob1, ob2, dir, false, false)){
				return true;
			} else {
				return false;
			}
		} else {
			return checkInTheWay(ob1, ob2, dir, false, false);
		}
	}

	// Does ob2 count as an obstacle for ob1 if ob1 is moving in direction dir?
	// THIS IS THE METHOD TO EDIT IF YOU WANT SPECIAL COLLISION DETECTION RULES	
	// e.g. "this object is a 1-way platform", "these objects only collide if they're different colors"...
	private static boolean countsAsObstacle(MovingObject ob1, MovingObject ob2, Direction dir){
		return true;
	}

	// Is ob2 in the way of ob1, as ob1 moves in direction dir?
	// There are two situations in which this method may be called, which are distinguished by the boolean pushMode
	// pushMode == true: ob1 has been processed and ob2 hasn't, we are checking to see if ob1 should 'push' ob2
	// (check is based on the 'current' Location for ob2, and the 'current' and 'new' locations for ob1.
	// pushMode = false and equalsMode = true: objects are equal rank, ob1 is being processed, ob2 has possibly not been processed, we are checking to see if ob2's 'current' location is in the way of ob1's movement.
	// pushMode == false and equalMode = false: ob2 has been processed and ob1 hasn't, we are checking to see if ob2 will obstruct on1's movement.
	// (check is based on the 'new' location for ob2, and the 'current' and 'target' locations for ob1.
	private static boolean checkInTheWay(MovingObject ob1, MovingObject ob2, Direction dir, boolean pushMode, boolean equalsMode){	
		
		// if ob2 is epiphenomenal, it cannot be in the way of anything.
		if (ob2.isEpiphenomenal()){
			return false;
		}

		// if ob1 started off overlapping ob2 (when they were in their original positions), 
		// then ob2 cannot count as an 'in the way'		
		if (checkOverlap(ob1, false, ob2, false)){
			return false;
		}
	
		// Soe may assume ob1 started off not overlapping ob2, but is on track to overlap it.
		// it remains to check whether ob1 has to move into ob2 in the specified direction
		// First we calculate the relevant edge of ob2, and the leading corners for ob1
		Location leadingCorner1;		//a corner of ob1 that's going to hit stuff first (in its current position)
		Location leadingCorner2;		//a corner of ob1 that's going to hit stuff first (in its current position)
		Location edgePoint1;		//corner of the edge of ob2 facing ob1 as it moves in direction Dir
		Location edgePoint2;		//corner of the edge of ob2 facing ob1 as it moves in direction Dir

		Location current;
		Location target;
		Location ob2Loc;

		if (pushMode){
			current = ob1.getCurrentLoc();
			target = ob1.getNewLoc();			// in retrospect I regret this choice of variable name.
			ob2Loc = ob2.getCurrentLoc();
		} else if (equalsMode){
			current = ob1.getCurrentLoc();
			target = ob1.getTargetLoc();
			ob2Loc = ob2.getCurrentLoc();
		}else {
			current = ob1.getCurrentLoc();
			target = ob1.getTargetLoc();		
			ob2Loc = ob2.getNewLoc();
		}

		switch(dir){
			case UP:
				leadingCorner1 = current;
				leadingCorner2 = new Location (current.x + ob1.getWidth()-1, current.y);
				edgePoint1 = new Location(ob2Loc.x, ob2Loc.y + ob2.getHeight()-1);
				edgePoint2 = new Location(ob2Loc.x+ob2.getWidth()-1, ob2Loc.y + ob2.getHeight()-1);
				break;
			case DOWN:
				leadingCorner1 = new Location(current.x, current.y + ob1.getHeight()-1);
				leadingCorner2 = new Location(current.x + ob1.getWidth()-1, current.y + ob1.getHeight()-1);
				edgePoint1 = ob2Loc;
				edgePoint2 = new Location(ob2Loc.x+ob2.getWidth()-1, ob2Loc.y);
				break;
			case LEFT:
				leadingCorner1 = current;
				leadingCorner2 = new Location(current.x, current.y + ob1.getHeight()-1);
				edgePoint1 = new Location(ob2Loc.x+ob2.getWidth()-1, ob2Loc.y);
				edgePoint2 = new Location(ob2Loc.x+ob2.getWidth()-1, ob2Loc.y + ob2.getHeight()-1);
				break;
			case RIGHT:
				leadingCorner1 = new Location(current.x + ob1.getWidth()-1, current.y);
				leadingCorner2 = new Location(current.x + ob1.getWidth()-1, current.y + ob1.getHeight()-1);
				edgePoint1 = ob2Loc;
				edgePoint2 = new Location(ob2Loc.x, ob2Loc.y + ob2.getHeight()-1);
				break;
			default:
				return true;  // I mean, Direction has 4 values so we shouldn't even be here.
		}

		// cool, we have our corners sorted out, now we just need to check whether various lines are going to cross.

		// horizTravel and vertTravel are used for our lines - this is the vector everything travels along.
		int horizTravel = target.x - current.x;
		int vertTravel = target.y - current.y;

		// first check if leading corner 1 is going to pass through the relevant edge.
		if (linesCross(leadingCorner1, horizTravel, vertTravel, edgePoint1, edgePoint2)){
			return true;
		}
		// next check if leading corner 2 is going to pass through the relevant edge.
		if (linesCross(leadingCorner2, horizTravel, vertTravel, edgePoint1, edgePoint2)){
			return true;
		}
		// what if ob2 is shorter across then ob1? Then we need to check if the corners of ob2 are going to smash against the face of ob1. So we imagine that a corner of ob2 travles towards ob1, and see if it crosses that face.
		if (linesCross(edgePoint1, -horizTravel, -vertTravel, leadingCorner1, leadingCorner2)){
			return true;
		}
		if (linesCross(edgePoint2, -horizTravel, -vertTravel, leadingCorner1, leadingCorner2)){
			return true;
		}

		// if none of these line crosses happen, I'm pretty sure we're ok.
		return false;
	}


private static boolean linesCross(Location line1Start, Location line1End, Location line2Start, Location line2End){
		int line1Horiz = line1End.x - line1Start.x;
		int line1Vert = line1End.y - line1Start.y;
		int line2Horiz = line2End.x - line2Start.x;
		int line2Vert = line2End.y - line2Start.y;
		return linesCross(line1Start, line1Horiz, line1Vert, line2Start, line2Horiz, line2Vert);
	}

	private static boolean linesCross(Location line1Start, int line1Horiz, int line1Vert, Location line2Start, Location line2End){			
		int line2Horiz = line2End.x - line2Start.x;
		int line2Vert = line2End.y - line2Start.y;
		return linesCross(line1Start, line1Horiz, line1Vert, line2Start, line2Horiz, line2Vert);
	}

	private static boolean linesCross(Location line1Start, int line1Horiz, int line1Vert, Location line2Start, int line2Horiz, int line2Vert){

		// do the easy cases first - horizontal or vertical ranges disjoint
		if (Math.max(line1Start.x, line1Start.x + line1Horiz) < Math.min(line2Start.x, line2Start.x + line2Horiz)){
			return false;
		}
		if (Math.min(line1Start.x, line1Start.x + line1Horiz) > Math.max(line2Start.x, line2Start.x + line2Horiz)){
			return false;
		}
		if (Math.max(line1Start.y, line1Start.y + line1Vert) < Math.min(line2Start.y, line2Start.y + line2Vert)){
			return false;
		}
		if (Math.min(line1Start.y, line1Start.y + line1Vert) > Math.max(line2Start.y, line2Start.y + line2Vert)){
			return false;
		}

		// ok now harder cases...
		if (line1Horiz != 0){
		// line1 not vertical
			float yaTarget = line1Start.y + line1Vert* (line2Start.x - line1Start.x)/line1Horiz;		// y co-ord of line1 at line2start.x

			float ybTarget = line1Start.y + line1Vert * (line2Start.x + line2Horiz - line1Start.x)/line1Horiz; // y co-ord of line1 at line2start.x + line2Horiz

			if (line2Start.y <= yaTarget && (line2Start.y + line2Vert) >= ybTarget){
				return true;
			} else if (line2Start.y >= yaTarget && (line2Start.y + line2Vert) <= ybTarget){
				return true;
			} 
			return false;
		}

		// line1 vertical.

		if (line2Horiz != 0){
		// line2 not vertical, line1 vertical
		// new plan: find the equation for line2, and figure out if its height when it crosses line1.x is between the heights of line1.

			float yTarget = line2Start.y + line2Vert * (line1Start.x - line2Start.x)/line2Horiz;		// y co-ord of line2 at line1start.x
	
			// if yTarget is between the vertical ranges of line1, then line2 crosses it (we already know line2 exists at lineStart.x by the easy cases)
			if (Math.max(line1Start.y, line1Start.y+line1Vert) >= yTarget && Math.min(line1Start.y, line1Start.y+line1Vert) <= yTarget){
				return true;
			}
			return false;
		}

		//line1 and line2 both vertical.
		// we already know the vertical and horizontal ranges overlap. Which means they have the same co-ord and therefore the lines overlap each other.
		return true;
	}


	public static void testRun(){
		System.out.println("Doing test run...");

		Location current = new Location(355,253);
		Location target = new Location(360,253);
		MovingObject ob1 = new MovingObject(32,32, new Location(389,272), new Location (380,272), 3);
		MovingObject ob2 = new MovingObject(24,24, current, target, 3);
	//	ob2.setNewLoc(380,272);

		ArrayList<MovingObject> al = new ArrayList<MovingObject>();

		al.add(ob1);
		al.add(ob2);

		Process(al);
	
		System.out.println("(" + ob1.getCurrentLoc().x + "," + ob1.getCurrentLoc().y + ") (" + ob2.getCurrentLoc().x + "," + ob2.getCurrentLoc().y + ")");

		System.out.println("END OF TEST");

		
/*
		if (checkObstacle(ob1, ob2, Direction.RIGHT)){
			System.out.println("yeppers, an obstacle");
		} else {
			System.out.println("Noppers, not an obstacle");
		}



			Location ob2Loc = ob2.getNewLoc();
			Location	leadingCorner1 = new Location(current.x + ob1.getWidth()-1, current.y);
			Location	leadingCorner2 = new Location(current.x + ob1.getWidth()-1, current.y + ob1.getHeight()-1);
			Location	edgePoint1 = ob2Loc;
			Location	edgePoint2 = new Location(ob2Loc.x, ob2Loc.y + ob2.getHeight()-1);


// horizTravel and vertTravel are used for our lines - this is the vector everything travels along.
				int horizTravel = target.x - current.x;
				int vertTravel = target.y - current.y;

			// first check if leading corner 1 is going to pass through the relevant edge.
			if (linesCross(leadingCorner1, horizTravel, vertTravel, edgePoint1, edgePoint2)){
				System.out.println("leading corner 1 collision");
			} else {
				System.out.println("leading corner 1 no collision");
			}
			// next check if leading corner 2 is going to pass through the relevant edge.
			if (linesCross(leadingCorner2, horizTravel, vertTravel, edgePoint1, edgePoint2)){
				System.out.println("leading corner 2 collision");
			} else {
				System.out.println("leading corner 2 no collision");
			}
			// what if ob2 is shorter across then ob1? Then we need to check if the corners of ob2 are going to smash against the face of ob1. So we imagine that a corner of ob2 travles towards ob1, and see if it crosses that face.
			if (linesCross(edgePoint1, -horizTravel, -vertTravel, leadingCorner1, leadingCorner2)){
				System.out.println("edge point 1 collision");
			} else {
				System.out.println("edge point 1 no collision");
			}
			if (linesCross(edgePoint2, -horizTravel, -vertTravel, leadingCorner1, leadingCorner2)){
				System.out.println("edge point 2 collision");
			} else {
				System.out.println("edge point 2 no collision");
			}

			if (linesCross(new Location(168,40), 5, 3, new Location(0,5), new Location(40,5))){
				System.out.println("fixed example collision");
			} else {
				System.out.println("fixed example no collision");
			}

	


		if (linesCross(new Location(0,4), 5, 3, new Location(0,5), new Location(40,5))){
			System.out.println("fixed example collision");
		} else {
			System.out.println("fixed example no collision");
		}

		if (linesCross(new Location(0,4), 5, 3, new Location(0,5), 40, 0)){
			System.out.println("fixed example 2 collision");
		} else {
			System.out.println("fixed example 2 no collision");
		}

		int line1Vert = 3;
		int line1Horiz = 5;
		Location line1Start = new Location(0,4);
		Location line2Start = new Location(0,5);
		int line2Vert = 0;
		int line2Horiz = 40;

		double grad1 = line1Vert / line1Horiz;
		
	//	double grad1 = 3/5;		

		System.out.println("(" + grad1 + ")");


		double yaTarget = line1Start.y + line1Vert* (line2Start.x - line1Start.x)/line1Horiz;		// y co-ord of line1 at line2start.x
//		double yaTarget = 4 + (3*(0-0) / 5);
		System.out.println("(" + yaTarget + ")");

		double ybTarget = line1Start.y + line1Vert* (line2Start.x + line2Horiz - line1Start.x)/line1Horiz; // y co-ord of line1 at line2start.x + line2Horiz
//		double ybTarget = 4 + 	3*(0+40-0)/5;
		System.out.println("(" + ybTarget + ")");


		if (line2Start.y <= yaTarget && (line2Start.y + line2Vert) >= ybTarget){
			System.out.println("Yes, line 2 starts above line 1 and ends below");
		} else if (line2Start.y >= yaTarget && (line2Start.y + line2Vert) <= ybTarget){
			System.out.println("Yes, line 2 starts below line 1 and ends above");
		} else {
			System.out.println("nope, guess they don't cross");
		}


				leadingCorner1 = new Location(current.x + ob1.getWidth()-1, current.y);
				leadingCorner2 = new Location(current.x + ob1.getWidth()-1, current.y + ob1.getHeight()-1);
				edgePoint1 = ob2Loc;
				edgePoint2 = new Location(ob2Loc.x, ob2Loc.y + ob2.getHeight()-1);
*/

	}


}
