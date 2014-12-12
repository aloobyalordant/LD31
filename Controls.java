// Class that stores the controls - i.e. stores a set of integers representing the keyCodes of the keys you need to press to do the various things in the game.
import java.awt.event.*;

class Controls{

	public static int MOVE_LEFT_KEY = KeyEvent.VK_LEFT;
	public static int MOVE_RIGHT_KEY = KeyEvent.VK_RIGHT;
	public static int MOVE_UP_KEY = KeyEvent.VK_UP;
	public static int MOVE_DOWN_KEY = KeyEvent.VK_DOWN;
	public static int ALT_MOVE_LEFT_KEY = KeyEvent.VK_A;
	public static int ALT_MOVE_RIGHT_KEY = KeyEvent.VK_D;
	public static int ALT_MOVE_UP_KEY = KeyEvent.VK_W;
	public static int ALT_MOVE_DOWN_KEY = KeyEvent.VK_S;
	public static int JUMP_KEY =  KeyEvent.VK_Z;
	public static int ACTION_KEY = KeyEvent.VK_F;
	public static int RESTART_KEY = KeyEvent.VK_R;
	public static int KILL_KEY = KeyEvent.VK_CONTROL;

	public static String MOVE_LEFT_STRING = KeyEvent.getKeyText(MOVE_LEFT_KEY);
	public static String MOVE_RIGHT_STRING = KeyEvent.getKeyText(MOVE_RIGHT_KEY);
	public static String MOVE_UP_STRING = KeyEvent.getKeyText(MOVE_UP_KEY);
	public static String MOVE_DOWN_STRING = KeyEvent.getKeyText(MOVE_DOWN_KEY);	
	public static String ALT_MOVE_LEFT_STRING = KeyEvent.getKeyText(ALT_MOVE_LEFT_KEY);
	public static String ALT_MOVE_RIGHT_STRING = KeyEvent.getKeyText(ALT_MOVE_RIGHT_KEY);
	public static String ALT_MOVE_UP_STRING = KeyEvent.getKeyText(ALT_MOVE_UP_KEY);
	public static String ALT_MOVE_DOWN_STRING = KeyEvent.getKeyText(ALT_MOVE_DOWN_KEY);
	public static String JUMP_STRING = KeyEvent.getKeyText(JUMP_KEY);
	public static String ACTION_STRING = KeyEvent.getKeyText(ACTION_KEY);
	public static String RESTART_STRING = KeyEvent.getKeyText(RESTART_KEY);
	public static String KILL_STRING = KeyEvent.getKeyText(KILL_KEY);

	public Controls(){

	}

}
