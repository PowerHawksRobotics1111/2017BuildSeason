package variables;

import edu.wpi.first.wpilibj.Joystick;

public class Joysticks
{
	private final static int DRIVER_PORT = 0, OPERATOR_PORT = 1;
	
	// Joystick Button Labels
	public static final int X = 1, A = 2, B = 3, Y = 4, RIGHT_BUMPER = 6, LEFT_BUMPER = 5, LEFT_TRIGGER = 7,
			RIGHT_TRIGGER = 8, BACK = 9, START = 10;
	
	public static final int DPAD_RIGHT = 0, DPAD_UPPER_RIGHT = 45, DPAD_UP = 90, DPAD_UPPER_LEFT = 135, DPAD_LEFT = 180,
			DPAD_LOWER_LEFT = 225, DPAD_DOWN = 270, DPAD_LOWER_RIGHT = 315, DPAD_OFF = -1;
	
	public static final Joystick joyDrive = new Joystick(DRIVER_PORT), joyOp = new Joystick(OPERATOR_PORT);
	
	public static final class Buttons
	{
		
		// Create all of the buttons that will be equal to the button numbers
		public static final int intakeButton = A;
		public static final int outtakeButton = B;
		public static final int shootButton = RIGHT_TRIGGER;
		public static final int gearRelease = LEFT_TRIGGER;
		public static final int driveGearRelease = RIGHT_BUMPER;
		public static final int hang = Y;
		public static final int gearPrimer = LEFT_BUMPER;
		public static final int recordButton = LEFT_TRIGGER;
		
		public static final int driverPegAlign = RIGHT_BUMPER;
		
		public static final int override1 = START;
		public static final int override2 = BACK;
		
		public static final int navXReset = X;
		public static final int navXYawReset = Y;
		
		public static final int pForward = DPAD_UP;
		
		public static final int driveBoost = LEFT_BUMPER ;
		
	}
}
