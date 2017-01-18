package subsystems;

import variables.Motors;
import variables.Sensors;

//Tank drive
public class Drivetrain
{
	
	public static void drive(double left, double right)
	{
		Motors.motorDriveRight1.set(right);
		Motors.motorDriveLeft1.set( -left);
		Motors.motorDriveRight2.set(right);
		Motors.motorDriveLeft2.set( -left);
	}
	
	public static void rotateCW(double power)
	{
		Motors.motorDriveRight1.set( -power);
		Motors.motorDriveLeft1.set( -power);
		Motors.motorDriveRight2.set( -power);
		Motors.motorDriveLeft2.set( -power);
	}
	
	public static void rotateCCW(double power)
	{
		Motors.motorDriveRight1.set(power);
		Motors.motorDriveLeft1.set(power);
		Motors.motorDriveRight2.set(power);
		Motors.motorDriveLeft2.set(power);
	}
	
	// Both of these use potentially problematic while loops
	/*
	 * public static void turnCWNumDegrees(double degrees) { double currentAngle
	 * = Sensors.navx.getYaw(); //To be implemented double targetAngle =
	 * (currentAngle+degrees)%360; while(currentAngle!=targetAngle) { double
	 * currentAngle = Sensors.navx.getYaw(); rotateCW(.25); } }
	 */
	
	/*
	 * public static void turnCCWNumDegrees(double degrees) { double
	 * currentAngle = Sensors.navx.getYaw(); //To be implemented double
	 * targetAngle = (currentAngle+degrees)%360;
	 * while(currentAngle!=targetAngle) { double currentAngle =
	 * Sensors.navx.getYaw(); rotateCCW(.25); } }
	 */
	
	public static void turnToAngle(double targetAngle)
	{
		double angleDelta = targetAngle - Sensors.navX.getYaw();
		if (Math.abs(angleDelta) > 5)
		{
			if ((Sensors.navX.getYaw() + 180) % 360 > targetAngle)
			{
				rotateCW(Motors.AUTO_ALIGN_POWER);
			}
			if ((Sensors.navX.getYaw() + 180) % 360 < targetAngle)
			{
				rotateCCW(Motors.AUTO_ALIGN_POWER);
			}
		} else
		{
			drive(0, 0);
		}
	}
	
	public static void moveToDistance(double targetDistance)
	{
		double distanceDelta = targetDistance - Motors.motorDriveLeft2.getEncPosition();
		if (distanceDelta > 0)
		{
			drive(.5, .5);
		} else
		{
			drive(0, 0);
		}
	}
}
