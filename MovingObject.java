// A rectangular moving object, such as what might end up bumping into walls some day.

public class MovingObject{

	private int width;
	private int height;

	protected Location currentLoc;	// where the object is now
	protected Location targetLoc;	// where the object wants to go
	protected Location newLoc;		// where the collision detector will actually send the object.


	private boolean epiphenomenal = false;	// Ok. If this is true, this object can never push or get in the way of other objects,
									// but will bounce off of (non-epiphenomenal) objects of lower  (and maybe same) rank.


	// An important field that decides when the object gets processed by CollisionDetector.
	// Lower numbered objects are processed first, higher numbered objects have their position updated treating only lower numbered objects as obstacles.
	// By convention, 0 is for unmoving, solid objects such as walls and floors.
	// Default rank is 1.
	protected int rank;


	public MovingObject(int width, int height, Location currentLoc, Location targetLoc, int rank, boolean epiphenomenal){
		this.width = width;
		this.height = height;
		this.currentLoc = currentLoc;
		this.targetLoc = targetLoc;
		this.rank=rank;
		this.epiphenomenal = epiphenomenal;
	}

	public MovingObject(int width, int height, Location currentLoc, Location targetLoc, int rank){
		this(width, height, currentLoc, targetLoc, rank, false);
	}
	public MovingObject(int width, int height, Location currentLoc, Location targetLoc){
		this(width,height,currentLoc,targetLoc,1);
	}

	public MovingObject(int width, int height, Location currentLoc){
		this(width,height,currentLoc,currentLoc,1);
	}

	public MovingObject(int width, int height, Location currentLoc, int rank){
		this(width,height,currentLoc,currentLoc,rank);
	}




	// Process what the object is doing for the next frame.
	// Returns the newly calculated target location.
	public Location next(){
		targetLoc = currentLoc;
		return targetLoc;
	}


	// getters, woo

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}

	public Location getCurrentLoc(){
		return currentLoc;
	}

	public Location getCurrentCentre(){
		return new Location(currentLoc.x + width/2, currentLoc.y + height/2);
	}	

	public GridRef getNearestSpace(){
		return GridRef.nearestSpace(currentLoc, width, height);
	}

	public Location getTargetLoc(){
		return targetLoc;
	}

	public Location getNewLoc(){
		return newLoc;
	}

	public int getRank(){
		return rank;
	}


	public boolean isEpiphenomenal(){
		return epiphenomenal;
	}

	// is this a ghost object? i.e. one created for internal use by the collision detector? (GhostObject overrides this)
	public boolean isGhostObject(){
		return false;
	}
	
	// Returns the MovingObject on which this object was based, if this is a ghost object.
	// Otherwise, returns this object
	// (Overridden by GhostObject)
	public MovingObject getOriginalObject(){
		return this;
	}


	// Give the object a new location (normally calculated by the collision detection engine, based on currentLoc, targetLoc and the location of other objects/obstacles in the world.
	public void setNewLoc(Location newLoc){
		this.newLoc = newLoc;
	}

	// Reposition the object to a new location, taking x and y co-ordinates as arguments rather than a Location.
	public void setNewLoc(int x, int y){
		setNewLoc(new Location(x,y));
	}


	// Make the final move (only to be done once everything else has been processed and we no longer need to information
	// about the original value of currentLoc;
	public void finalMove(){
		//first, do some bouncing, in case the object is the kind to bounce.
		// this based on where th object is ending up, compared to where it intended to end up
		if (newLoc.x < targetLoc.x){
			bounce(Direction.LEFT);
		} else if (newLoc.x > targetLoc.x){
			bounce(Direction.RIGHT);
		}
		if (newLoc.y < targetLoc.y){
			bounce(Direction.UP);
		} else if (newLoc.y > targetLoc.y){
			bounce(Direction.DOWN);
		}

		// And Finally! Actually update the object position.
		currentLoc = newLoc;
	}





	// tell the object it hit an obstacle, and should now be travelling in the specified direction, 
	// if it's the sort of object to bounce off things.
	protected void bounce (Direction dir){

	}

	// tell the object it hit an obstacle in the horizontal direction. For bouncy things, mainly.
	public void bounceHorizontally(){

	}

	// tell the object it hit an obstacle in the verticl direction. For bouncy things, mainly.
	public void bounceVertically(){

	}
}
