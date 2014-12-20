
import java.util.ArrayList;
import java.awt.Image;
import java.io.*;
import javax.imageio.*;
import java.net.URL;
import java.awt.*;
import java.applet.*; 
public class ImageManager{

	private static ArrayList<Image> imageList;
	private static Image floorTileImage;
	private static Image wallImage;
	private static Image guardImage;
	private static Image alertGuardImage;
	private static Image avatarImage;

	public ImageManager(){
		
	}

	public static void initialise(){
//		try {
    	//	      	URL url = new URL(getDocumentBase(), "FloorTile.png");
    	//	       	floorTileImage  = ImageIO.read(url);
	//		url = new URL(getDocumentBase(), "Wall.png");
  //      		wallImage  = ImageIO.read(url);
//			url = new URL(getDocumentBase(), "Guard2.gif");
//			guardImage  = ImageIO.read(url);
//			url = new URL(getDocumentBase(), "Guard3.gif");
//			alertGuardImage  = ImageIO.read(url);
//			url = new URL(getDocumentBase(), "Explorer2(17x20).gif");
 //            		avatarImage  = ImageIO.read(url);


//			floorTileImage = ImageIO.read(this.getClass().getResource("/FloorTile.png"));
//			wallImage = ImageIO.read(this.getClass().getResource("/Wall.png"));
//			guardImage = ImageIO.read(this.getClass().getResource("/Guard2.gif"));
//			alertGuardImage = ImageIO.read(this.getClass().getResource("/Guard3.gif"));
//			avatarImage = ImageIO.read(this.getClass().getResource("/Explorer2(17x20).gif"));

//			floorTileImage = ImageIO.read(new File("FloorTile.png"));
//			wallImage = ImageIO.read(new File("Wall.png"));
//			guardImage = ImageIO.read(new File("Guard2.gif"));
//			alertGuardImage = ImageIO.read(new File("Guard3.gif"));
//			avatarImage = ImageIO.read(new File("Explorer2(17x20).gif"));
//		} catch (IOException e) {
//		}

	}

	public static void setImage(String name, Image image){
		if (name.equals("FloorTile")){
			floorTileImage = image;
		} else if (name.equals("Wall")){
			wallImage = image;
		} else if (name.equals("Guard")){
			guardImage = image;
		} else if (name.equals("AlertGuard")){
			alertGuardImage = image;
		} else if (name.equals("Avatar")){
			avatarImage = image;
		}

	}

	public static Image getImage(String name){
		if (name.equals("FloorTile")){
			return floorTileImage;
		} else if (name.equals("Wall")){
			return wallImage;
		} else if (name.equals("Guard")){
			return guardImage;
		} else if (name.equals("AlertGuard")){
			return alertGuardImage;
		} else if (name.equals("Avatar")){
			return avatarImage;
		}else{
			return null;
		}
	}

}
