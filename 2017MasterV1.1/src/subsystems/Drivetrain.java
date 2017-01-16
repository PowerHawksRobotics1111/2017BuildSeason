package subsystems;

import variables.Motors;
import variables.Sensors;
import com.kauailabs.navx.frc.AHRS;

//Tank drive
public class Drivetrain 
{
	public static void drive(double left, double right)
	{
		Motors.motorDriveFrontRight.set(right);
		Motors.motorDriveFrontLeft.set(-left);
		Motors.motorDriveBackRight.set(right);
		Motors.motorDriveBackLeft.set(-left);	
	}
	
	public static void rotateCW(double power)
	{
		Motors.motorDriveFrontRight.set(-power);
		Motors.motorDriveFrontLeft.set(-power);
		Motors.motorDriveBackRight.set(-power);
		Motors.motorDriveBackLeft.set(-power);
	}
	
	public static void rotateCCW(double power)
	{
		Motors.motorDriveFrontRight.set(power);
		Motors.motorDriveFrontLeft.set(power);
		Motors.motorDriveBackRight.set(power);
		Motors.motorDriveBackLeft.set(power);
	}
	
	public static void moveForward(double power)
	{
		Motors.motorDriveBackLeft.set(-power);
		Motors.motorDriveBackRight.set(power);
		Motors.motorDriveFrontLeft.set(-power);
		Motors.motorDriveFrontRight.set(power);
	}
	
	public static void stopMoving()
	{
		Motors.motorDriveFrontRight.set(Motors.NO_POWER);
		Motors.motorDriveFrontLeft.set(Motors.NO_POWER);
		Motors.motorDriveBackRight.set(Motors.NO_POWER);
		Motors.motorDriveBackLeft.set(Motors.NO_POWER);
	}
	
	//Both of these use potentially problematic while loops
	/*
	public static void turnCWNumDegrees(double degrees)
	{
		double currentAngle = Sensors.navx.getYaw(); //To be implemented
		double targetAngle = (currentAngle+degrees)%360;
		while(currentAngle!=targetAngle)
		{
			double currentAngle = Sensors.navx.getYaw();
			rotateCW(Motors.QUARTER_POWER);
		}
	}*/
	
	/*
	public static void turnCCWNumDegrees(double degrees)
	{
		double currentAngle = Sensors.navx.getYaw(); //To be implemented
		double targetAngle = (currentAngle+degrees)%360;
		while(currentAngle!=targetAngle)
		{
			double currentAngle = Sensors.navx.getYaw();
			rotateCCW(Motors.QUARTER_POWER);
		}
	}*/
	
	public static void turnToAngle(double targetAngle)
	{
		double angleDelta = targetAngle-Sensors.navX.getYaw();
		if(Math.abs(angleDelta)>5)
		{
			if((Sensors.navX.getYaw+180)%360>targetAngle)
			{
				rotateCW(Motors.QUARTER_POWER);
			}
			if((Sensors.navX.getYaw+180)%360<targetAngle)
			{
				rotateCCW(Motors.QUARTER_POWER);
			}
		}
		else
		{
			stopMoving();
		}
	}
	
	public static void moveToDistance(double targetDistance)
	{
		double distanceDelta = targetDistance-Motors.motorDriveBackLeft.getDistance();
		if(distanceDelta>0)
		{
			moveForward(Motors.HALF_POWER);
		}
		else
		{
			stopMoving();
		}
	}
}

