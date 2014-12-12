// A copy of a moving object! Used internally in the collision detector, to see where things 'would' go.
public class GhostObject extends MovingObject{

	private MovingObject parent;


	public GhostObject(MovingObject parent){
		super(parent.getWidth(), parent.getHeight(), parent.getCurrentLoc(), parent.getTargetLoc(), parent.getRank(), parent.isEpiphenomenal());
		this.parent = parent;
	}

	public GhostObject(int width, int height, Location currentLoc, Location targetLoc, int rank, MovingObject parent, boolean epiphenomenal){
		super(width, height, currentLoc, targetLoc, rank, epiphenomenal);
		this.parent = parent;
	}



	// Returns the MovingObject on which this object was based, if this is a ghost object.
	// Otherwise, returns this object
	// (Overridden by GhostObject)
	public MovingObject getOriginalObject(){
		if (parent != null){
			return parent.getOriginalObject();
		} else {
			return this;
		}
	}

}
