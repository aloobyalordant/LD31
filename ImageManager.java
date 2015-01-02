
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
	private static Image solidWallImage;
	private static Image guardImage;
	private static Image alertGuardImage;
//	private static Image avatarImage;
	private static Image avatarDownImage;
	private static Image avatarUpImage;
	private static Image avatarLeftImage;
	private static Image avatarRightImage;

	private static  Image[] batteryImages = new Image[9];

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
		}  else if (name.equals("SolidWall")){
			solidWallImage = image;
		} else if (name.equals("Guard")){
			guardImage = image;
		} else if (name.equals("AlertGuard")){
			alertGuardImage = image;
		} //else if (name.equals("Avatar")){
			//avatarImage = image;
		//}

	}

	public static void setImage(String name, Direction dir, Image image){
		if (name.equals("Avatar")){
			switch(dir){
				case DOWN:
					avatarDownImage = image;
					break;
				case UP:
					avatarUpImage = image;
					break;
				case LEFT:
					avatarLeftImage = image;
					break;
				default:
					avatarRightImage = image;
					break;
			}
		}

	}

	public static void setImage(String name, Image image, int i){
		if (name.equals("Battery")){
			batteryImages[i] = image;
		}
	}

	public static Image getImage(String name){
		if (name.equals("FloorTile")){
			return floorTileImage;
		} else if (name.equals("Wall")){
			return wallImage;
		}  else if (name.equals("SolidWall")){
			return solidWallImage;
		}else if (name.equals("Guard")){
			return guardImage;
		} else if (name.equals("AlertGuard")){
			return alertGuardImage;
	//	} else if (name.equals("Avatar")){
	//		return avatarImage;
		}else{
			return null;
		}
	}

	public static Image getImage(String name, int val){
		if (name.equals("Battery")){
			return batteryImages[val];
		} else {
			return null;
		}
	}

	public static Image getImage(String name, Direction dir){
		if (name.equals("Avatar")){
			switch(dir){
				case DOWN:
					return avatarDownImage;
				//	break;
				case UP:
					return avatarUpImage;
				//	break;
				case LEFT:
					return avatarLeftImage;
				//	break;
				default:
					return avatarRightImage;
				//	break;
			}
		} else {
			return null;
		}
	}


}
