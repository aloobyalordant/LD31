
import java.util.ArrayList;
import java.awt.Image;
import java.io.*;
import javax.imageio.*;
public class ImageManager{

	private static ArrayList<Image> imageList;
	private static Image floorTileImage;
	private static Image wallImage;
	private static Image guardImage;
	private static Image alertGuardImage;
	private static Image avatarImage;

	public static void initialise(){
		try {
			floorTileImage = ImageIO.read(new File("Art/FloorTile.png"));
			wallImage = ImageIO.read(new File("Art/Wall.png"));
			guardImage = ImageIO.read(new File("Art/Guard2.gif"));
			alertGuardImage = ImageIO.read(new File("Art/Guard3.gif"));
			avatarImage = ImageIO.read(new File("Art/Explorer2(17x20).gif"));
		} catch (IOException e) {
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
