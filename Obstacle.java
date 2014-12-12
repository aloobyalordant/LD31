public class Obstacle extends MovingObject{

	private boolean isActive = true;

	public Obstacle(int width, int height, Location loc){
		super(width,height, loc, 1);
	}

	public boolean isActive(){
		return isActive;
	}

	public void destroy(){
		isActive = false;
	}
}
