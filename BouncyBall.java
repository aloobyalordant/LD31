// A bouncing object! Probably rectangular rather than a ball, though
import java.util.Random; 

public class BouncyBall extends MovingObject{


	private int topSpeed = 5;
	private int xSpeed = 5;
	private int ySpeed = 5;
	

	public BouncyBall(int width, int height, Location currentLoc){
		super(width,height,currentLoc, 2);			// bouncy balls have rank 2 for the purpose of this demo.
		Random ran = new Random();
		xSpeed = -5 + ran.nextInt(11);
		ySpeed = -5 + ran.nextInt(11);
	}



	// Process what the object is doing for the next frame.
	// Returns the newly calculated target location.
	public Location next(){
		targetLoc = new Location (currentLoc.x+xSpeed, currentLoc.y+ySpeed);
		return targetLoc;
	}

	public void bounceHorizontally(){
		xSpeed = -xSpeed;
	//	System.out.println("xspeed: " + xSpeed);
	}

	public void bounceVertically(){
		ySpeed = -ySpeed;
	//	System.out.println("yspeed: " + ySpeed);
	}

	protected void bounce(Direction dir){
		switch(dir){
			case UP:
				if (ySpeed > 0){
					ySpeed = -ySpeed;
				}
				break;
			case DOWN:
				if (ySpeed < 0){
					ySpeed = -ySpeed;
				}
				break;
			case LEFT:
				if (xSpeed > 0){
					xSpeed = -xSpeed;
				}
				break;
			default:
				if (xSpeed < 0){
					xSpeed = -xSpeed;
				}
				break;
		}
	}



}
