import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Scanner;

public class Game extends Applet implements Runnable, KeyListener {

	Thread gameThread; 	// Thread for main loop

	private Font a_Font;
	private int frame = 0;
	static final int GAMESLEEP = 25;    // milliseconds per frame of animation.

	private static boolean soundsOn = true;

	private long time;

	private World gameWorld;
	private Menu gameMenu;

	public static boolean gamePlaying = true;
	public static boolean menuMode;			// should we be looking at the menu right now?


	static final boolean debugMode = false;		// toggles showing whatever extra info I need to track down a bug that day

	private int intPressed = -1;			//-1 for no number pressed, 0 to 9 otherwise
	private int keyPressed = -1;			// -1 for no key pressed, the keyCode if otherwise.
	private boolean jumpPressed;
	private boolean leftPressed;
	private boolean rightPressed;
	private boolean moveLeftPressed;
	private boolean moveRightPressed;
	private boolean moveUpPressed;
	private boolean moveDownPressed;
	private boolean upPressed;
	private boolean downPressed;
	private boolean actionPressed;
	private boolean restartPressed;
	private boolean modePressed;
	private boolean savePressed;
	private boolean killPressed;
	private boolean pausePressed;
	private boolean enterPressed;

	private int intJustPressed;
	private int keyJustPressed;
	private boolean modeJustPressed;
	private boolean leftJustPressed;
	private boolean rightJustPressed;
	private boolean moveLeftJustPressed;
	private boolean moveRightJustPressed;
	private boolean moveUpJustPressed;
	private boolean moveDownJustPressed;
	private boolean upJustPressed;
	private boolean downJustPressed;
	private boolean jumpJustPressed;
	private boolean actionJustPressed;
	private boolean restartJustPressed;
	private boolean saveJustPressed;
	private boolean pauseJustPressed;
	private boolean enterJustPressed;

	Dimension offDimension;
  	BufferedImage     offImage;
	Graphics  og;

	public void init()
	{
		setBackground(Color.black);
		a_Font = new Font("Helvetica", Font.PLAIN, 24);
		setFont(a_Font);
		time = System.currentTimeMillis();
		ImageManager.initialise();
	}

	
	public void start()
	{
		// Start the game thread! We're really cooking with gas here.
		if (gameThread == null)
		{
			gameThread = new Thread(this);
			gameThread.setName("Game thread");
			this.addKeyListener(this);
			gameThread.start();
		}
	}

	public void stop()
	{
	//	if (gameThread != null) {
	//		gameThread = null;
	//	}
	}

	public void run() {
		time = System.currentTimeMillis();

//		menuMode = true;
		gameMenu = new Menu(debugMode);

//		// load up level 1!
		gameWorld = new World(debugMode);

		while (Thread.currentThread() == gameThread) {


			// pause or unpause the game if player just pressed the pause key.
			if (pausePressed && !pauseJustPressed){
				menuMode = !menuMode;
			}

			if (menuMode) {
			
				if(intPressed != intJustPressed){
					intJustPressed = intPressed;
				} else {
					intPressed = -1;
				}
				if(keyPressed != keyJustPressed){
					keyJustPressed = keyPressed;
				} else {
					keyPressed = -1;
				}
				if ( (leftPressed && !leftJustPressed) 
				|| (rightPressed && !rightJustPressed)
				|| (upPressed && !upJustPressed)
				|| (downPressed && !downJustPressed)
				|| (enterPressed && !enterJustPressed) ) {
					gameMenu.next(leftPressed, rightPressed, upPressed, downPressed, enterPressed, intPressed, keyPressed);
				}
				 else {
					gameMenu.next(false,false,false,false, false, intPressed, keyPressed);
				}
				String command = gameMenu.getCommand();
				if (command != null) {
					Scanner scanner = new Scanner(command);
					scanner.useDelimiter(",");
					String subCommand = "";
					if (scanner.hasNext() ) {
						subCommand = scanner.next();
					}
					if (subCommand.equals("START")) {
						gameWorld = new World(debugMode);
						menuMode = false;
						gamePlaying = true;
					} else if (subCommand.equals("CONTINUE")){
						if (gameWorld == null){
							gameWorld = new World(debugMode);							
						}
						menuMode = false;
						gamePlaying = true;
					}
				}
			} else {

				// Make the World object construct its next frame.
				gameWorld.next(moveLeftPressed, moveRightPressed, moveUpPressed, moveDownPressed, jumpPressed, actionPressed, restartPressed, killPressed, pausePressed);
				// If the player just pressed pause, refresh the menu.
//				if (menuMode){
//					gameMenu.initialiseMenus();
//				}
				
				// if the user pressed restart, restart the world!
				if (restartPressed && !restartJustPressed){
					gameWorld = new World(debugMode);
				}
			}

			// Update the display.
     			repaint();


			// play sound effects
			if (soundsOn){
				for(String st: gameWorld.getSounds()){
					AudioClip temp = getAudioClip(getDocumentBase(), st);
					temp.play();
				}
			}


			// update your boolean button pressing detector thingies.
			modeJustPressed = modePressed;
			leftJustPressed = leftPressed;
			rightJustPressed = rightPressed;
			moveLeftJustPressed = moveLeftPressed;
			moveRightJustPressed = moveRightPressed;
			moveUpJustPressed = moveUpPressed;
			moveDownJustPressed = moveDownPressed;
			upJustPressed = upPressed;
			downJustPressed = downPressed;
			actionJustPressed = actionPressed;
			jumpJustPressed = jumpPressed;
			restartJustPressed = restartPressed;
			saveJustPressed = savePressed;
			pauseJustPressed = pausePressed;
			enterJustPressed = enterPressed;


      		// Suspend the thread for the specified time.
			frame++;	
			int temp = GAMESLEEP;
			time += temp;
      		try {
				int sleepytime = (int) (time - System.currentTimeMillis());
				sleepytime = (sleepytime < temp) ? sleepytime : temp;
       			 Thread.currentThread().sleep(temp);
      		}
      		catch (InterruptedException e) {}
   		}
	}

	public void update(Graphics g) {
	// TODO: Add some double-buffering
		paint(g);
	}

	private void updateOffImage() {
		Dimension d = getSize();

		if (offImage == null ||
				!d.equals(offDimension)) {
			offImage = new BufferedImage(d.width, d.height, BufferedImage.TYPE_4BYTE_ABGR);
			offDimension = d;
		}
	}

	public void paint (Graphics g)
	{
		// Get the image of the current frame from the World object
		updateOffImage();

		if (menuMode) {
			gameMenu.drawNewFrame(offDimension, offImage);
		} else if (gameWorld != null) {
			gameWorld.drawNewFrame(offDimension, offImage);
		}

		// Copy that drawn picture to the screen, woo woo!
		g.drawImage(offImage, 0, 0, this);
	}


	/** Handle the key typed event from the text field. */
	public void keyTyped(KeyEvent e) {
	}

	/** Handle the key pressed event from the text field. */
	public void keyPressed(KeyEvent e) {
		final int code = e.getKeyCode();

		/* VK_0 through VK_9 are contiguous, so we can subtract VK_0 to get the value of the key. */
		if (code == KeyEvent.VK_0) {
			intPressed = 0;
		} else if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9) {
			intPressed = code - KeyEvent.VK_0;

		} else if (code == KeyEvent.VK_M) {
			intPressed = -1;
		} else {
			updateKeyState(e, true);
			intPressed = -1;
		}
		keyPressed = code;
	}

	/** Handle the key released event from the text field. */
	public void keyReleased(KeyEvent e) {
		updateKeyState(e, false);
	}

	private void updateKeyState(KeyEvent e, boolean state) {

		int keyCode = e.getKeyCode();
	
		// keys that are hard-coded.
		if (keyCode == KeyEvent.VK_ENTER){
			enterPressed = state;
		} else if (keyCode == KeyEvent.VK_LEFT) {
			leftPressed = state;
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			rightPressed = state;
		} else if (keyCode == KeyEvent.VK_UP) {
			upPressed = state;
		} else if (keyCode == KeyEvent.VK_DOWN) {
			downPressed = state;
		} else if (keyCode == KeyEvent.VK_ESCAPE){
				pausePressed = state;
		}

		

		// keys that are not hard-coded (may be some overlap e.g. 'Left' = 'MoveLeft')
		if (keyCode == Controls.MOVE_LEFT_KEY || keyCode == Controls.ALT_MOVE_LEFT_KEY) {
			moveLeftPressed = state;
		}
		if (keyCode == Controls.MOVE_RIGHT_KEY || keyCode == Controls.ALT_MOVE_RIGHT_KEY) {
			moveRightPressed = state;
		}
		if (keyCode == Controls.MOVE_UP_KEY || keyCode == Controls.ALT_MOVE_UP_KEY) {
			moveUpPressed = state;
		}
		if (keyCode == Controls.MOVE_DOWN_KEY || keyCode == Controls.ALT_MOVE_DOWN_KEY ) {
			moveDownPressed = state;
		}
		if (keyCode == Controls.JUMP_KEY) {
			jumpPressed = state;
		}
		if (keyCode == Controls.ACTION_KEY) {
			actionPressed = state;
		}
		if (keyCode == Controls.RESTART_KEY) {
			restartPressed = state;
		}
		if (keyCode == Controls.KILL_KEY) {
			killPressed = state;
		}
	}

	public static void main(String[] args) {
		Applet a = new Game();
		a.init();
		a.start();

		JFrame window = new JFrame("World of Thing");
		window.setContentPane(a);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		/* the height here needs to be greater than that specified in
		 * Testing.html to get the same result, and I don't know why.
		 */
		window.setSize(992, 608);
		window.setVisible(true);

		soundsOn = false;
	}

}



