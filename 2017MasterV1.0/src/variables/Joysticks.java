package variables;

import edu.wpi.first.wpilibj.Joystick;

public class Joysticks 
{
	
	/*
	 * Initialize the values for the modes, buttons, and joysticks
	 */
	
	//Ports
	private final static int DRIVER_MODE = 0, OPERATOR_MODE = 1;
	
	//Buttons numbers
	public static final int X = 0, A = 1, B = 2, Y = 3, RIGHT_BUMBER = 4, LEFT_BUMBER = 5, LEFT_TRIGGER = 6, RIGHT_TRIGGER = 7, BACK = 8, START = 9; 
	
	public static final int DPAD_RIGHT = 0, DPAD_UPPER_RIGHT = 45, DPAD_UP = 90, DPAD_UPPER_LEFT = 135, DPAD_LEFT = 180, DPAD_LOWER_LEFT = 225, DPAD_DOWN = 270, DPAD_LOWER_RIGHT = 315, DPAD_OFF = -1;
	
	//Initialize the joysticks
	public static final Joystick joyDrive = new Joystick(DRIVER_MODE), joyOp = new Joystick(OPERATOR_MODE);
	
	public static class Buttons
	{
		//Create all of the buttons that will be equal to the button numbers
	}

}
