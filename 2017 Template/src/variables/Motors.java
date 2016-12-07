package variables;

import edu.wpi.first.wpilibj.CANTalon;

public class Motors {

	public static CANTalon motorDriveFrontRight, motorDriveFrontLeft, motorDriveBackRight, motorDriveBackLeft;

	final static int FRONT_RIGHT_DRIVE = 47, FRONT_LEFT_DRIVE = 52, BACK_RIGHT_DRIVE = 55, BACK_LEFT_DRIVE = 60;
	// TODO MDBL Port possibly wrong mapped.

	// Standard motor powers should go here for easy access and adjustment
	final static double motorPower = 0.0;

	/** Motor initialization */
	public static void motorInit()
	{
		motorDriveFrontRight = new CANTalon(FRONT_RIGHT_DRIVE);
		motorDriveFrontLeft = new CANTalon(FRONT_LEFT_DRIVE);
		motorDriveBackRight = new CANTalon(BACK_RIGHT_DRIVE);
		motorDriveBackLeft = new CANTalon(BACK_LEFT_DRIVE);

	}
}
