import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;


public class World {

	// level meta data
	private boolean debugMode; 	// decides whether to draw collision boxes or whatever.

	// frame count
	private int frame;

	// avatar
	private Avatar ava;
	private ArrayList<Obstacle> obstacles;
	private ArrayList<Obstacle> solidObstacles;	// walls that can't be destroyed by bullets
//	private ArrayList<BouncyBall> bonnieBalls;	
	private ArrayList<Guard> guards;
	private ArrayList<Bullet> bullets;	
	private ArrayList<Charger> chargers;
	private BigCharger bigC;
	private Teleporter tellie;

	private boolean killJustPressed;

	private int weaponCharge = Values.weaponInitialCharge;
	private int chargersGot = 0;

	private boolean BigTreasureReleased = false;
	private boolean BigTreasureCollected = false;
	private boolean TeleporterOn = false;
	
	private int timeToTeleport = 0;

	// map stuff
	private Map levelMap;

	private Random ran;


	// various hard coded numbers.
	private static int blockWidth = Values.blockWidth;
	private static int blockHeight = Values.blockHeight;

	// Drawy stuff for the drawer
 	Graphics2D  og;		// og stands for 'offGraphics' because I'm lazy.

	// creates new instance
	public World(boolean debugMode) {
		Initialise(debugMode);
	}

	private void Initialise(boolean debugMode){
		frame = 0;
		SoundManager.refresh();
		levelMap = new Map (Values.mapWidthInBlocks,Values.mapHeightInBlocks);
		ava = new Avatar(new GridRef(15,9));
		obstacles = new ArrayList<Obstacle>();
		solidObstacles = new ArrayList<Obstacle>();
		guards = new ArrayList<Guard>();
		bullets = new ArrayList<Bullet>();
		chargers = new ArrayList<Charger>();
		bigC = new BigCharger(new GridRef(15,9));
		tellie = new Teleporter(new GridRef(15,9));

		ran = new Random();

		int[][] levelMapData = levelMap.getMapData();
		for (int i = 0; i < levelMapData.length; i++){
			for (int j = 0; j < levelMapData[i].length; j++){
				if (levelMapData[i][j] == 1){
					obstacles.add(new Obstacle(blockWidth,blockHeight, new Location(i*blockWidth,j*blockHeight)));	
				} else if (levelMapData[i][j] == 2){
					solidObstacles.add(new Obstacle(blockWidth,blockHeight, new Location(i*blockWidth,j*blockHeight)));
				}
			}
		}


		guards.add(new Guard(new GridRef(3,3), levelMap));
		guards.add(new Guard(new GridRef(27,3), levelMap));
		guards.add(new Guard(new GridRef(3,15), levelMap));
		guards.add(new Guard(new GridRef(27,15), levelMap));
		
		// one extra guard gets placed in one of the four spawn points at random
		int lastGuard = ran.nextInt(4);
		switch(lastGuard){
			case 1:
				guards.add(new Guard(new GridRef(3,3), levelMap));
				break;
			case 2:
				guards.add(new Guard(new GridRef(27,3), levelMap));
				break;
			case 3:
				guards.add(new Guard(new GridRef(3,15), levelMap));
				break;
			default:
				guards.add(new Guard(new GridRef(27,15), levelMap));
				break;
		
		}


		chargers.add(new Charger(new GridRef(3,3)));
		chargers.add(new Charger(new GridRef(27,3)));
		chargers.add(new Charger(new GridRef(3,15)));
		chargers.add(new Charger(new GridRef(27,15)));
		
	}


	// Calculates where everything should be next, based on what keys the user is pressing.
	// In other words, does everything except drawing.
	public void next( boolean leftPressed, boolean rightPressed, boolean upPressed, boolean downPressed, boolean jumpPressed, boolean actionPressed, boolean restartPressed, boolean killPressed, boolean pausePressed) {
		frame++;



		// objec moving / collision detection stuff.
		if (ava.isAlive()){
			ava.next(leftPressed, rightPressed, upPressed, downPressed);
		}
		for (MovingObject ob: obstacles){
			ob.next();
		}
		for (MovingObject ob: solidObstacles){
			ob.next();
		}
		for (Guard gary: guards){
			gary.next();
		}
		for (Bullet bill: bullets){
			bill.next();
		}
		for (Charger chaz: chargers){
			chaz.next();
		}	

		doCollisionDetections();


		// shooting! Pressing this button makes the avatar fire a bullet and depletes the weapon charge, if 
		if (killPressed && !killJustPressed && weaponCharge >= Values.weaponDemand){
			bullets.add(new Bullet(ava.getCurrentCentre(), ava.getDirection()));
			weaponCharge = weaponCharge - Values.weaponDemand;
		}
		killJustPressed = killPressed;

		// Now check for fun stuff. Like hitting enemies!


		// play footstep sounds if the avatar is making them.
		if (ava.footstep()){
			SoundManager.queue("AvatarFootstep");
			
		}


		// Guards doing things!
		for (Guard gary: guards){
			if (gary.isAlive()){


				// hurt the avatar if the guard hits them.
				if (checkOverlap(ava, gary) && !gary.isDormant()){
					ava.hit();
				}

				// hearing and making footstep sounds.
				Location garyLoc = gary.getCurrentCentre();
				Location avaLoc = ava.getCurrentCentre();
				int xDiff = avaLoc.x - garyLoc.x;
				int yDiff = avaLoc.y - garyLoc.y;
				int hearingRangeSquared = Values.footstepSoundRangeSquared;
				int guardNoiseRangeSquared = Values.guardFootstepSoundRangeSquared;
				// distance for both haring and making footstep sounds is increased if the guard is alert i.e. running.
				if (gary.isAlert()){
					hearingRangeSquared = Values.alertDetectionRangeSquared;
					guardNoiseRangeSquared = Values.loudGuardFootstepSoundRangeSquared;
				}

				// play a boomy footstep sound if the guard is near enough.
				if (xDiff*xDiff + yDiff*yDiff <= guardNoiseRangeSquared){
					if(gary.footstep()){
						SoundManager.queue("GuardFootstep", gary.getFootstepSound());
					}
				}

				// guard hear the avatar if they make a noise while close enough.
				if (ava.footstep()){
					if (xDiff*xDiff + yDiff*yDiff <= hearingRangeSquared){
						gary.hearNoise(ava.getNearestSpace());

					}
				}
			}
		}


		// bullets hitting things and making noise!
		GridRef loudNoiseGR = null;
		for (Bullet bill: bullets){
			// explode and kill a guard if the bullet hits one.
			if (bill.isActive()){
				for (Guard gary: guards){
					if (gary.isAlive() && checkOverlap(bill, gary)){
						gary.hit();
						bill.destroy();
						loudNoiseGR = gary.getNearestSpace();
					}
				}
			}
			// explode and destroy a wall if the bullet hits one
			if (bill.isActive()){
				for (Obstacle ob: obstacles){
					if (ob.isActive() && checkOverlap(bill, ob)){
						ob.destroy();
						bill.destroy();
						loudNoiseGR = ob.getNearestSpace();
						levelMap.setValue(loudNoiseGR,0);
					}
				}
				// solid walls don't get destroyed, but it still makes a loud explosion
				for (Obstacle ob: solidObstacles){
					if (ob.isActive() && checkOverlap(bill, ob)){
						bill.destroy();
						loudNoiseGR = ob.getNearestSpace();
					}
				}
			}
		}
		// if the bullet made a loud noise, all the guards hear it (and update their knowledge of the map)
		if (loudNoiseGR != null){
			//update the shortest distances on map
			levelMap.calculateShortestDistance();
			for (Guard gary: guards){
				if (gary.isAlive()){
					gary.updateMap(levelMap);
					gary.hearLoudNoise(loudNoiseGR);
				}
			}
		}

		// picking up chargers depletes the charger and adds to the weapon charge, and we keep track of which chargers have been got so far.
		for (Charger chaz: chargers){
			if (chaz.isAvailable() && checkOverlap(ava, chaz) && weaponCharge < Values.weaponMaxCharge){
				weaponCharge = Math.min(weaponCharge + Values.chargeFromChargers, Values.weaponMaxCharge);
				if(!chaz.hasBeenGot()){
					chargersGot++;
				}
				chaz.deplete();
			}
		}




		// release the big charger if the others have all been collected at least once.
		if(chargersGot >= 4 && !BigTreasureReleased){
			bigC.release();
			BigTreasureReleased = true;
		}

		// if the big treasure is picked up, change to endgame mode, give the weapon a big charge, alert all the guards, and start a big countdown timer for the escape teleport.
		if (BigTreasureReleased && bigC.isAvailable() && checkOverlap(ava, bigC)){
			weaponCharge = weaponCharge + Values.weaponDemand;
			if(!bigC.hasBeenGot()){
				chargersGot++;
			}
			bigC.collect();
			BigTreasureCollected = true;
			for (Guard gary: guards){
				if (gary.isAlive()){
					gary.hearLoudNoise(bigC.getNearestSpace());
				}
			}
			timeToTeleport = Values.teleportDelay;
		}



		// If the big charger has been picked up, it recharges the weapon by a little bit at regular intervals.
		// The guards are also made aware of the avatar's location at regular intervals.
		if (BigTreasureCollected && frame % Values.BigTreasureRechargeTime == 0){
			if (weaponCharge < Values.weaponMaxCharge){	
				weaponCharge++;
			}
			for (Guard gary: guards){
				if (gary.isAlive()){
					gary.hearLoudNoise(ava.getNearestSpace());
				}
			}
		}

		// If the big charger has been picked up, create a new guard in a random spawn location at regular intervals
		if (BigTreasureCollected && frame % Values.EmergencyGuardReleaseTime == 0 && guards.size() < Values.guardUpperLimit){
			int choice = ran.nextInt(4);
			switch(choice){
				case 0:
					guards.add(new Guard(new GridRef(3,3), levelMap));
					break;
				case 1:
					guards.add(new Guard(new GridRef(27,3), levelMap));
					break;
				case 2:
					guards.add(new Guard(new GridRef(3,15), levelMap));
					break;
				default:
					guards.add(new Guard(new GridRef(27,15), levelMap));
					break;
			}
		}

		// summon the telepad if the timer has run out.
		if (BigTreasureCollected && timeToTeleport > 0){
			timeToTeleport--;
			if(timeToTeleport == 0){
				TeleporterOn = true;
				tellie.activate();
			}
		}


		// if the avatar enters the telepad, they teleport away! You win! That's the end of the game.
		if  (TeleporterOn && checkOverlap(ava, tellie)){
			tellie.enter();
			ava.escape();
		}
	
		// cheat button! Pressing this summons the big trasure / charger. CUT THIS BIT OUT		
		if(jumpPressed){
			bigC.release();
			BigTreasureReleased = true;
		}

	}


	
//***************** COLLISION DETECT GO HERE ****************

	private void doCollisionDetections(){
	//	Location l = ava.getTargetLoc();
	//	ava.setNewLoc(l);
		ArrayList<MovingObject> al = new ArrayList<MovingObject>();
	//	ArrayList<MovingObject> al = new ArrayList<MovingObject>(obstacles);
		for (Obstacle ob: obstacles){
			if (ob.isActive()){
				al.add(ob);
			}
		}
		for (Obstacle ob: solidObstacles){
			al.add(ob);
		}
		al.add(ava);
		for (Guard gary: guards){
			if (gary.isAlive()){
				al.add(gary);
			}
		}
		for (Bullet bill: bullets){
			if (bill.isActive()){
				al.add(bill);
			}
		}
		CollisionDetector.Process(al);
	}

	// check whether two obkects are overlapping.
	// This happens after the stuff with actual collision detector.
	// Maybe this was not the best place to put this method.
	private boolean checkOverlap(MovingObject ob1, MovingObject ob2){

		// initialise locations, based on whether we want target or current for each object		
		Location loc1 = ob1.getCurrentLoc();
		Location loc2 = ob2.getCurrentLoc();
		loc1 = ob1.getCurrentLoc();

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



//****************** SOUND STUFF GOES HERE ******************

	// returns a list of sound files we want playing.
	public List<String> getSounds() {
		List<String> temp = SoundManager.getSoundFileList();
		SoundManager.refresh();				// don't want these sound files building up...
		return temp;
	}

//***************** PAINTING STUFF GOES HERE ****************

	//public void paint (Graphics g)
	// Draws a frame based on the current state of the world
	public void drawNewFrame(Dimension d, BufferedImage offImage)
	{
//		og = offImage.getGraphics();	
		og = offImage.createGraphics();	
		drawBackground(d);
		drawObstacles();
	//	drawBouncyBalls();
		drawBullets();
		drawChargers();
		drawTeleporter();
		drawGuards();
		drawAvatar();
		drawDarkness(d);
		drawAlertGuards();
		drawMessages();
	}

	private void drawBackground(Dimension d) {
		og.setColor(Color.gray);
		og.fillRect(0, 0, d.width, d.height);
		
		Image temp = ImageManager.getImage("FloorTile");

		int blockWidth = Values.blockWidth;
		int blockHeight = Values.blockHeight;
		int[][] mapData = levelMap.getMapData();
		for (int i=0; i < Values.mapWidthInBlocks; i++){
			for (int j = 0; j < Values.mapHeightInBlocks; j++){
				if (mapData[i][j] == 0){
					og.drawImage(temp, i*blockWidth, j*blockHeight + Values.YOffset, null);
				}
			}
		}
	}

	private void drawObstacles(){
		Image temp = ImageManager.getImage("Wall");

		//og.setColor(Color.black);
		for (Obstacle ob: obstacles){
			if (ob.isActive()){
				Location l = ob.getCurrentLoc();
				//og.fillRect(l.x,l.y, ob.getWidth(), ob.getHeight());
				og.drawImage(temp, l.x, l.y + Values.YOffset, null);
			}
		}		

		temp = ImageManager.getImage("SolidWall");
		for (Obstacle ob: solidObstacles){
			Location l = ob.getCurrentLoc();
			//og.fillRect(l.x,l.y, ob.getWidth(), ob.getHeight());
			og.drawImage(temp, l.x, l.y + Values.YOffset, null);
		}	
	}

	private void drawMessages() {
		og.setFont(new Font("Courier", Font.BOLD, 32));
		og.setColor(Color.green);
		og.drawString("Charge: " + weaponCharge + "/" + Values.weaponDemand, 50, 25);
		og.setColor(Color.green);
		og.drawString("Treasures: " + chargersGot + "/" + Values.numberOfTreasures, 300, 25);
		if (BigTreasureCollected){
			og.drawString("Escape in: " + timeToTeleport, 600, 25);
		}		
	}


	private void drawChargers(){
		og.setColor(Color.green);
		for (Charger chaz: chargers){
			if (chaz.isAvailable()){
				Location l = chaz.getCurrentLoc();
				og.fillOval(l.x,l.y + Values.YOffset, chaz.getWidth(), chaz.getHeight());
			}
		}
		if (BigTreasureReleased && bigC.isAvailable()){
			Location l = bigC.getCurrentLoc();
			og.fillOval(l.x,l.y + Values.YOffset, bigC.getWidth(), bigC.getHeight());
		}		
	}

	private void drawTeleporter(){
		if (TeleporterOn && ! tellie.hasBeenEntered()){
			og.setColor(Color.cyan);
			Location l = tellie.getCurrentLoc();
			og.fillOval(l.x,l.y + Values.YOffset, tellie.getWidth(), tellie.getHeight());
		}		
	}

	private void drawGuards(){
		og.setColor(Color.red);
		Image temp = ImageManager.getImage("Guard");
		for (Guard gary: guards){
			if (gary.isAlive()){
				Location l = gary.getCurrentLoc();
			//	og.fillRect(l.x,l.y, gary.getWidth(), gary.getHeight()); 
				og.drawImage(temp, l.x, l.y + Values.YOffset, null);
			}
		}
	}

	private void drawBullets(){
		og.setColor(Color.green);
		for (Bullet bill: bullets){
			if (bill.isActive()){
				Location l = bill.getCurrentLoc();
				og.fillRect(l.x,l.y + Values.YOffset, bill.getWidth(), bill.getHeight()); 
			}
		}
	}



	private void drawAvatar() {
		if (ava.isAlive()){
			Location l = ava.getCurrentLoc();
			Image temp = ImageManager.getImage("Avatar");

			og.setColor(Color.blue);
		//	og.fillRect(l.x,l.y, ava.getWidth(), ava.getHeight());
			og.drawImage(temp, l.x, l.y - Values.AvatarImageYOffset + Values.YOffset, null);
		}
	}

	private void drawDarkness(Dimension d){
		Location l = ava.getCurrentLoc();
		int radius = BigTreasureCollected ? Values.superPoweredTorchRadius: Values.torchRadius;
		float[] dist={Values.torchBrightestRadius,1f};
		Color[] colors={new Color(0,0,0,0), Color.black};
		RadialGradientPaint rgp = new RadialGradientPaint(l.x,l.y + Values.YOffset, radius, dist, colors);
		og.setPaint(rgp);
		og.fillRect(0, 0 + Values.YOffset, d.width, d.height);
	}


	private void drawAlertGuards(){
		og.setColor(Color.red);
		Image temp = ImageManager.getImage("AlertGuard");
		for (Guard gary: guards){
			if (gary.isAlive() && gary.isAlert()){
				Location l = gary.getCurrentLoc();
			//	og.fillRect(l.x,l.y, gary.getWidth(), gary.getHeight()); 
				og.drawImage(temp, l.x, l.y + Values.YOffset, null);
			}
		}
	}


}
