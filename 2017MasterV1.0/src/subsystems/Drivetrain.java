package subsystems;

import variables.Motors;

//Tank drive
public class Drivetrain 
{
	public void drive(double left, double right)
	{
		Motors.motorDriveFrontRight.set(right);
		Motors.motorDriveFrontLeft.set(-left);
		Motors.motorDriveBackRight.set(right);
		Motors.motorDriveBackLeft.set(-left);
	}
		
}

