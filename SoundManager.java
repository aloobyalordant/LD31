import java.util.List;
import java.util.ArrayList;
import java.util.Random;
// Hopefully, a class with a static method that will take arguments from other classes and turn them into sounds that you hear.

public class SoundManager {

	public static List<String> soundFileList;		// list of filenames for sounds we want to play
	private static Random ran = new Random();

	public static void queue (String eventDescriptor) {
		if (eventDescriptor.equals("AvatarFootstep")){
			int choice = ran.nextInt(4);
			switch (choice){
				case 0:
					soundFileList.add("Sounds/151232__owlstorm__hard-female-footstep-1.wav");
					break;
				case 1:
					soundFileList.add("Sounds/151238__owlstorm__hard-female-footstep-2.wav");
					break;
				case 2:
					soundFileList.add("Sounds/151237__owlstorm__hard-female-footstep-3.wav");
					break;
				default: 
					soundFileList.add("Sounds/151222__owlstorm__hard-female-footstep-4.wav");
					break;
				// footstep sounds from http://www.freesound.org/people/OwlStorm/packs/9344/  - Natalie Kirk or Esper Studio. !
			}
		} 
	}

	public static void refresh() {
		soundFileList = new ArrayList<String>();
	}

	public static List<String> getSoundFileList()	{
		return soundFileList;
	}
}
