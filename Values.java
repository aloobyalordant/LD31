// Just a big old class of number that get used a bunch in various places

public class Values{


	public static final int blockWidth = 32;
	public static final int blockHeight = 32;

	public static final int mapWidthInBlocks = 31;
	public static final int mapHeightInBlocks = 19;
	// avatar stuff
	public static final int avatarWidth = 17; //20;
	public static final int avatarHeight = 20; //20;
	public static final int avatarSpeed = 5;
	public static final int AvatarImageYOffset = 4;
	// guard stuff
	public static final int guardWidth = 24;
	public static final int guardHeight = 24;
	public static final int guardSlowSpeed = 1;
	public static final int guardFastSpeed = 3;
	// detection ranges are now measured in.. pixels, rather than blocks, I guess (hopefully makes guard triggering less of a crapshoot)
	public static final int alertDetectionRange = 128;	//how far away a guard needs to be to keep track of you if they're alert.
	public static final int alertDetectionRangeSquared = alertDetectionRange*alertDetectionRange;
	//public static final int alertDetectionRange = 4;	//how far away a guard needs to be to keep track of you if they're alert.
	//public static final int alertDetectionRangeSquared = 16;	
	public static final int defaultGuardAlertCooldown = 60;
	public static final int disappointedGuardAlertCooldown = 20;	// how long a guard takes to stop being alert after losing the avatar.
	public static final int loudGuardAlertCooldown = 150;	// longer cooldown if it was a loud nise i.e. blowing things up/killing a guard
	public static final int guardParalysedByRageCooldown = 10;	// for a brief period after hearing the avatar, the guard is so angry they can't move.
	public static final int guardDormantCooldown = 50;
	// bullet stuff
	public static final int bulletWidth = 8;
	public static final int bulletHeight = 8;
	public static final int bulletSpeed = 10;	
	// chargers
	public static final int chargerWidth = 8;
	public static final int chargerHeight = 8;
	public static final int chargerCooldown = 1000;
	public static final int chargeFromChargers = 4;
	public static final int bigChargerWidth = 16;
	public static final int bigChargerHeight = 16;
	//weapon
	public static final int weaponDemand = 8;
	public static final int weaponInitialCharge = 8;
	public static final int weaponMaxCharge = 12;
	public static final int BigTreasureRechargeTime = 50;

	public static final int EmergencyGuardReleaseTime = 300;

	// torch?
	public static final int torchRadius = 130;
	public static final int superPoweredTorchRadius = 300;
	public static final float torchBrightestRadius = 0.1f;


	// misc
	// footstep sound srange now measured in pixels rather than blocks
	public static final int footstepSoundRange = 96; //3;
	public static final int footstepSoundRangeSquared = footstepSoundRange*footstepSoundRange;
	public static final int numberOfTreasures = 5;
	public static final int teleportDelay = 2000;		// time until the teleport out becomes active after grabbing the treasure.
	public static final int teleporterWidth = 32;	
	public static final int teleporterHeight = 16;	

	public static final int guardUpperLimit = 25;


}
