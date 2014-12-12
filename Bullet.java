
public class Bullet extends MovingObject{

	private static int bulletWidth = Values.bulletWidth;
	private static int bulletHeight = Values.bulletHeight;

	private boolean isActive = true;

	private int xSpeed = 0;
	private int ySpeed = 0;


	public Bullet(Location currentLoc, Direction dir){
		super(bulletWidth,bulletHeight,currentLoc, currentLoc, 1, true);			// bullets are epiphenomenal, in the sense that they can overlap things.
		switch(dir){
			case UP:
				ySpeed = -Values.bulletSpeed;
				break;
			case DOWN:
				ySpeed = Values.bulletSpeed;
				break;
			case LEFT:
				xSpeed = -Values.bulletSpeed;
				break;
			default:	//(right)
				xSpeed = Values.bulletSpeed;
				break;
		}
	}

	public Location next(){
		targetLoc = new Location (currentLoc.x+xSpeed, currentLoc.y+ySpeed);
		return targetLoc;
	}

	public boolean isActive(){
		return isActive;
	}

	public void destroy(){
		isActive = false;
	}
}
