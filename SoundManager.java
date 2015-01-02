import java.util.List;
import java.util.ArrayList;
import java.util.Random;
// Hopefully, a class with a static method that will take arguments from other classes and turn them into sounds that you hear.

public class SoundManager {

	public static List<String> soundFileList;		// list of filenames for sounds we want to play
	private static Random ran = new Random();

	private static int guardStep0Count = 0;
	private static int guardStep1Count = 0;
	private static int guardStep2Count = 0;
	private static int lastFootstepChoice = 0;

	public static void queue (String eventDescriptor) {
		if (eventDescriptor.equals("AvatarFootstep")){
			// pick a random number from 0-3, that's not the one we just had
			int choice = ran.nextInt(3);
			if (choice >= lastFootstepChoice){
				choice++;
			}
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
				// footstep sounds from http://www.freesound.org/people/OwlStorm/packs/9344/  - Natalie Kirk / Esper Studio. 
			}
			lastFootstepChoice = choice;
		} else if (eventDescriptor.equals("Explosion")){
			soundFileList.add("Sounds/244345__willlewis__musket-explosion.wav");
			// explosion sound from http://www.freesound.org/people/Willlewis/sounds/244345/  - Will Lewis

		} else if (eventDescriptor.equals("Gong")){
			soundFileList.add("Sounds/122681__juskiddink__gong-3.wav");
			// gong sound from http://www.freesound.org/people/juskiddink/sounds/122681/ - juskiddink
		} 
	}


	public static void queue (String eventDescriptor, int choice) {
		if (eventDescriptor.equals("GuardFootstep")){
			switch (choice){
				case 0:
					if (guardStep0Count % 4 == 0){
						soundFileList.add("Sounds/114590__herbertboland__bigdrum3.wav");
					} else  if (guardStep0Count % 4 == 1){
						soundFileList.add("Sounds/114590__herbertboland__bigdrum3v1.wav");
					} else  if (guardStep0Count % 4 == 2){
						soundFileList.add("Sounds/114590__herbertboland__bigdrum3v2.wav");
					} else  if (guardStep0Count % 4 == 3){
						soundFileList.add("Sounds/114590__herbertboland__bigdrum3v3.wav");
					}
					guardStep0Count++;
					break;
				case 1:
					if (guardStep1Count % 4 == 0){
						soundFileList.add("Sounds/114591__herbertboland__bigdrum4.wav");
					} else if (guardStep1Count % 4 == 1){
						soundFileList.add("Sounds/114591__herbertboland__bigdrum4v1.wav");
					} else if (guardStep1Count % 4 == 2){
						soundFileList.add("Sounds/114591__herbertboland__bigdrum4v2.wav");
					} else if (guardStep1Count % 4 == 3){
						soundFileList.add("Sounds/114591__herbertboland__bigdrum4v3.wav");
					}
					guardStep1Count++;
					break;
				default:
					if (guardStep2Count % 4 == 0){
						soundFileList.add("Sounds/114592__herbertboland__bigdrum5.wav");
					} else if (guardStep2Count % 4 == 1){
						soundFileList.add("Sounds/114592__herbertboland__bigdrum5v1.wav");
					} else if (guardStep2Count % 4 == 2){
						soundFileList.add("Sounds/114592__herbertboland__bigdrum5v2.wav");
					} else if (guardStep2Count % 4 == 3){
						soundFileList.add("Sounds/114592__herbertboland__bigdrum5v3.wav");
					}
					guardStep2Count++;
					break;
				// guard footstep sounds from http://www.freesound.org/people/HerbertBoland/packs/7189/  - Herbet Boland. 
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
