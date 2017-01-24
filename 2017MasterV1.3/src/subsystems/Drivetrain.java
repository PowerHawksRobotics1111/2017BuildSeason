package subsystems;

import edu.wpi.first.wpilibj.PIDController;
import variables.Motors;
import variables.PIDValues;
import variables.Sensors;

//Tank drive
public class Drivetrain {

	// Initialize the PIDController
	PIDController right1PID = new PIDController(PIDValues.DRIVE_TRAIN_KP, PIDValues.DRIVE_TRAIN_KI,
			PIDValues.DRIVE_TRAIN_KD, Motors.motorDriveRight1, Motors.motorDriveRight1);
	PIDController right2PID = new PIDController(PIDValues.DRIVE_TRAIN_KP, PIDValues.DRIVE_TRAIN_KI,
			PIDValues.DRIVE_TRAIN_KD, Motors.motorDriveRight1, Motors.motorDriveRight2);
	PIDController left1PID = new PIDController(PIDValues.DRIVE_TRAIN_KP, PIDValues.DRIVE_TRAIN_KI,
			PIDValues.DRIVE_TRAIN_KD, Motors.motorDriveLeft1, Motors.motorDriveLeft1);
	PIDController left2PID = new PIDController(PIDValues.DRIVE_TRAIN_KP, PIDValues.DRIVE_TRAIN_KI,
			PIDValues.DRIVE_TRAIN_KD, Motors.motorDriveLeft1, Motors.motorDriveLeft2);

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
			if (((Sensors.navX.getYaw() + 180) % 360) > targetAngle)
				rotateCW(Motors.AUTO_ALIGN_POWER);
			if (((Sensors.navX.getYaw() + 180) % 360) < targetAngle)
				rotateCCW(Motors.AUTO_ALIGN_POWER);
		} else
			drive(0, 0);
	}

	public static void moveToDistance(double targetDistance)
	{
		double distanceDelta = targetDistance - Motors.motorDriveLeft2.getEncPosition();
		// TODO this will wufflo around it's 0 point. It is unlikely to ever
		// stop there. Set a tolerance.
		if (distanceDelta > 50)
			drive(.5, .5);
		else if (distanceDelta < -50)
			drive( -.5, -.5);
		else
			drive(0, 0);
	}

	public static void moveToDistancePID(double targetDistance)
	{
		double distanceDelta = targetDistance - Motors.motorDriveLeft2.getEncoPosition();
		if (distance)
	}
}
