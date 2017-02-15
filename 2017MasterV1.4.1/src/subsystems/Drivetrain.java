package subsystems;

import org.usfirst.frc.team1111.robot.Robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import variables.Motors;
import variables.PIDValues;
import variables.Sensors;

//Tank drive
public class Drivetrain
{
	
	static double AngleThreshold = 5.0;
	static double EncoderDistanceThreshold = 2000.0;
	static boolean PIDDone = false;
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
	
	// TODO test
	public static void turnToAngle(double targetAngle)
	{
		double angleDelta = targetAngle - Sensors.navX.getYaw();
		if (Math.abs(angleDelta) > AngleThreshold)
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
		double distanceDelta = targetDistance + ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
				+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
		if (distanceDelta > EncoderDistanceThreshold)
			drive(.3, -.3);
		else if (distanceDelta < -EncoderDistanceThreshold)
			drive( -.3, .3);
		else
			drive(0, 0);
	}
	
	// TODO finish
	public static boolean moveToDistancePID(double targetDistance)
	{
		// get the distance needed to move
		double distanceDeltaLeft = targetDistance - Motors.motorDriveLeft1.getEncPosition();
		double distanceDeltaRight = targetDistance - Motors.motorDriveRight1.getEncPosition();
		// set the encoder counts to zero
		Motors.motorDriveLeft1.setEncPosition(0);
		Motors.motorDriveRight1.setEncPosition(0);
		// Set the PIDControllers to the distance needed to move
		PIDValues.rightDrive1.setSetpoint(distanceDeltaRight);
		PIDValues.rightDrive2.setSetpoint(distanceDeltaRight);
		PIDValues.leftDrive1.setSetpoint(distanceDeltaLeft);
		PIDValues.leftDrive2.setSetpoint(distanceDeltaLeft);
		// Enable the PIDController with the new setpoint
		PIDValues.rightDrive1.enable();
		PIDValues.rightDrive2.enable();
		PIDValues.leftDrive1.enable();
		PIDValues.leftDrive2.enable();
		// test to see if the error is within 5000 encoder counts
		// if so, stop the motors
		double averageEncoderError = ((Motors.motorDriveLeft1.getError() + Motors.motorDriveRight1.getError()) / 2.0);
		if (averageEncoderError < 5000)
		{
			PIDValues.rightDrive1.disable();
			PIDValues.rightDrive2.disable();
			PIDValues.leftDrive1.disable();
			PIDValues.leftDrive2.disable();
			return true;
		}
		return false;
	}
	
	public static boolean rotateRight(double angleToTurnTo)
	{
		// Get the necessary amount of degrees to turn
		// Set the setpoint value of the PIDControllers to degreesLeftToTurn
//		double degreesRightToTurn = Robot.navX.getYaw() + angleToTurnTo;
		double degreesRightToTurn = angleToTurnTo;
		SmartDashboard.putNumber("Setpoint", degreesRightToTurn);
		SmartDashboard.putNumber("NavX", Sensors.navX.getYaw());
		if (degreesRightToTurn > 180)
		{
			degreesRightToTurn = degreesRightToTurn - 360;
		} else if (degreesRightToTurn < -180)
		{
			degreesRightToTurn += 360;
		}
		SmartDashboard.putBoolean("PID Controller is enabled", PIDValues.leftNavX1.isEnabled());
		SmartDashboard.putNumber("Left NavX error: ", PIDValues.leftNavX1.getError());
		System.out.println("Setpoint: " + PIDValues.leftNavX1.getSetpoint());
		if (!PIDDone && !PIDValues.leftNavX1.isEnabled() && !PIDValues.leftNavX2.isEnabled() && !PIDValues.rightNavX1.isEnabled()
				&& !PIDValues.rightNavX2.isEnabled())
		{
			PIDValues.leftNavX1.setSetpoint(degreesRightToTurn);
			PIDValues.leftNavX2.setSetpoint(degreesRightToTurn);
			PIDValues.rightNavX1.setSetpoint(degreesRightToTurn);
			PIDValues.rightNavX2.setSetpoint(degreesRightToTurn);
			// enable all of the pidControllers
			PIDValues.leftNavX1.enable();
			PIDValues.leftNavX2.enable();
			PIDValues.rightNavX1.enable();
			PIDValues.rightNavX2.enable();
		}
		
		// Get the average error of the angle counts
		// double averageAngleError = ((PIDValues.left.getError() +
		// PIDValues.rightDrive1.getError()) / 2.0);
		// System.out.println("Angle Error: " + averageAngleError);
		double averageAngleError = ((Math.abs(PIDValues.leftNavX1.getError()) + Math.abs(PIDValues.leftNavX2.getError()) + 
				Math.abs(PIDValues.rightNavX1.getError()) + Math.abs(PIDValues.rightNavX2.getError())) / 4.0);
		SmartDashboard.putBoolean("PID Controller is enabled After", PIDValues.leftNavX1.isEnabled());

				
		if (averageAngleError < 2)
		{
			System.out.println("Anything");
			PIDValues.rightNavX1.disable();
			PIDValues.rightNavX2.disable();
			PIDValues.leftNavX1.disable();
			PIDValues.leftNavX2.disable();
			PIDDone = true;
			return true;
		}else{
			PIDDone = false;
		}
		return false;
	}
	
	public static boolean rotateLeft(double angleToTurnTo)
	{
		// Get the necessary amount of degrees to turn
		// Set the setpoint value of the PIDControllers to degreesLeftToTurn
		double degreesRightToTurn = Sensors.navX.getYaw() + angleToTurnTo;
		if (degreesRightToTurn > 180)
		{
			degreesRightToTurn = degreesRightToTurn - 360;
		} else if (degreesRightToTurn < -180)
		{
			degreesRightToTurn += 360;
		}
		if ( !PIDValues.leftNavX1.isEnabled() && !PIDValues.leftNavX2.isEnabled() && !PIDValues.rightNavX1.isEnabled()
				&& !PIDValues.rightNavX2.isEnabled())
		{
			PIDValues.leftNavX1.setSetpoint(degreesRightToTurn);
			PIDValues.leftNavX2.setSetpoint(degreesRightToTurn);
			PIDValues.rightNavX1.setSetpoint(degreesRightToTurn);
			PIDValues.rightNavX2.setSetpoint(degreesRightToTurn);
			// enable all of the pidControllers
			PIDValues.leftNavX1.enable();
			PIDValues.leftNavX2.enable();
			PIDValues.rightNavX1.enable();
			PIDValues.rightNavX2.enable();
		}
		// Get the average error of the angle counts
		if (PIDValues.leftNavX1.onTarget() && PIDValues.rightNavX1.onTarget())
		{
			PIDValues.rightNavX1.disable();
			PIDValues.rightNavX2.disable();
			PIDValues.leftNavX1.disable();
			PIDValues.leftNavX2.disable();
			return true;
		}
		return false;
	}
	
	public boolean resetPID()
	{
		Sensors.navX.zeroYaw();
		
		PIDValues.leftNavX1.reset();
		PIDValues.leftNavX2.reset();
		PIDValues.rightNavX1.reset();
		PIDValues.rightNavX2.reset();
		
		return true;
	}
}
