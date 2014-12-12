import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.util.Random;


public class Menu {

	// level meta data
	private boolean debugMode; 	// decides whether to draw collision boxes or whatever.

	// frame count
	private int frame;
	
	// Drawy stuff for the drawer
 	Graphics  og;		// og stands for 'offGraphics' because I'm lazy.

	// creates new instance
	public Menu(boolean debugMode) {
		Initialise(debugMode);
	}

	private void Initialise(boolean debugMode){
		frame = 0;
	}


	// Calculates where everything should be next, based on what keys the user is pressing.
	// In other words, does everything except drawing.
	public void next( boolean leftPressed, boolean rightPressed, boolean upPressed, boolean downPressed, boolean enterPressed, int intPressed, int keyPressed) {
	}




//***************** PAINTING STUFF GOES HERE ****************

	//public void paint (Graphics g)
	// Draws a frame based on the current state of the world
	public void drawNewFrame(Dimension d, Image offImage)
	{
		og = offImage.getGraphics();		
		drawBackground(d);
		drawMessages();
	}

	private void drawBackground(Dimension d) {
		og.setColor(Color.red);
		og.fillRect(0, 0, d.width, d.height);
	}

	private void drawMessages() {
		og.setFont(new Font("Courier", Font.BOLD, 64));
		og.setColor(Color.black);
		og.drawString("PUASE" + frame, 150, 150);
	}



	public String getCommand(){
		return "";
	}





}
