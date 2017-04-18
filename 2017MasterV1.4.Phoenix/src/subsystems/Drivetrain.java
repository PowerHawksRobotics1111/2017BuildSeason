package subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import variables.Motors;
import variables.PIDValues;
import variables.Sensors;

//Tank drive
public class Drivetrain
{
	
	static double AngleThreshold = 2;
	static double EncoderDistanceThreshold = 3000.0;
	static boolean PIDDone = false;
	static boolean retrievedAngle = false;
	static double initialAngle;
	static double maxCorrection = 0.3;
	static double lMotor = 0;
	static double rMotor = 0;
	
	// Actual bot
	public static void drive(double left, double right)
	{
		Motors.motorDriveRight1.set( -right);
		Motors.motorDriveLeft1.set(left);
		Motors.motorDriveRight2.set( -right);
		Motors.motorDriveLeft2.set(left);
	}
	
	public static void driveBackwards(double left, double right)
	{
		Motors.motorDriveRight1.set(right);
		Motors.motorDriveLeft1.set( -left);
		Motors.motorDriveRight2.set(right);
		Motors.motorDriveLeft2.set( -left);
	}
	
	public static void rotateCW(double power)
	{
		Motors.motorDriveRight1.set( -power);
		Motors.motorDriveLeft1.set( power);
		Motors.motorDriveRight2.set( -power);
		Motors.motorDriveLeft2.set( power);
	}
	
	public static void rotateCCW(double power)
	{
		Motors.motorDriveRight1.set(power);
		Motors.motorDriveLeft1.set(-power);
		Motors.motorDriveRight2.set(power);
		Motors.motorDriveLeft2.set(-power);
	}
	
	// TODO test
	public static void turnToAngle(double targetAngle)
	{
		SmartDashboard.putNumber("target angle to turn: ", targetAngle);
		double angleDelta = targetAngle - Sensors.navX.getYaw();
		if (Math.abs(angleDelta) > AngleThreshold)
		{
			if (Sensors.navX.getYaw() > targetAngle)
				rotateCW(Motors.AUTO_ALIGN_POWER);
			if (Sensors.navX.getYaw() < targetAngle)
				rotateCCW(Motors.AUTO_ALIGN_POWER);
		} else
			drive(0, 0);
	}
	
//	public static void moveToDistance(double targetDistance)
//	{
//		double lMotorPower = 0.6;
//		double rMotorPower = 0.6;
//				
////		if(!retrievedAngle)
////		{
////			initialAngle = Sensors.navX.getYaw();
////			retrievedAngle = true;
////		}
////		double angleDelta = Math.abs(initialAngle-Sensors.navX.getYaw());
////		double correctionValue = (2*angleDelta/180.0)*maxCorrection;
//		double distanceDelta = targetDistance + ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
//				+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
////		if(Sensors.navX.getYaw()<initialAngle)
////		{
////			rMotorPower+=correctionValue;
////		}
////		else if(Sensors.navX.getYaw()>initialAngle)
////		{
////			lMotorPower+=correctionValue;
////		}
//		if (distanceDelta > EncoderDistanceThreshold)
//			drive( -lMotorPower, rMotorPower);
//		else if (distanceDelta < -EncoderDistanceThreshold)
//			drive(lMotorPower, -rMotorPower);
//		else
//		{
//			drive(0, 0);
//			retrievedAngle = false;	
//		}
//	}
	
	public static void moveToDistance(double targetDistance)
	{
		double lMotorPower = 0.25;
		double rMotorPower = 0.25;
				
		if(!retrievedAngle)
		{
			initialAngle = Sensors.navX.getYaw();
			retrievedAngle = true;
		}
//		double angleDelta = Math.abs(initialAngle-Sensors.navX.getYaw());
//		double correctionValue = (2*angleDelta/180.0)*maxCorrection;
		
		double distanceDelta = targetDistance + ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
				+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
//		if(Sensors.navX.getYaw()<initialAngle-5)
//		{
//			rMotorPower+=0.1;
//		}
//		else if(Sensors.navX.getYaw()>initialAngle+5)
//		{
//			lMotorPower+=0.1;
//		}
		
//		if(Sensors.navX.getYaw()<initialAngle)
//		{
//			rMotorPower+=correctionValue;
//		}
//		else if(Sensors.navX.getYaw()>initialAngle)
//		{
//			lMotorPower+=correctionValue;
//		}
		if (distanceDelta > EncoderDistanceThreshold)
			drive( -lMotorPower, rMotorPower);
		else if (distanceDelta < -EncoderDistanceThreshold)
			drive(lMotorPower, -rMotorPower);
		else
		{
			drive(0, 0);
			retrievedAngle = false;	
		}
	}

	
	public static boolean moveToDistancePID(double targetDistance)
	{
		// get the distance needed to move
		double distanceDeltaLeft = (Motors.motorDriveLeft1.getEncPosition() + targetDistance);
		double distanceDeltaRight = Motors.motorDriveRight1.getEncPosition() + targetDistance;
		SmartDashboard.putNumber("distanceDeltaLeft", distanceDeltaLeft);
		SmartDashboard.putNumber("distanceDeltaRight", distanceDeltaRight);
		// set the encoder counts to zero
		// Motors.motorDriveLeft1.setEncPosition(0);
		// Motors.motorDriveRight1.setEncPosition(0);
		if ( !PIDDone && !PIDValues.leftDrive1.isEnabled() && !PIDValues.leftDrive2.isEnabled()
				&& !PIDValues.rightDrive1.isEnabled() && !PIDValues.rightDrive2.isEnabled())
		{
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
		}
		// test to see if the error is within 5000 encoder counts
		// if so, stop the motors
		// System.out.println("PIDRight error: " +
		// PIDValues.rightDrive1.getError());
		// System.out.println("PIDLeft error: " +
		// PIDValues.leftDrive1.getError());
		double averageEncoderErrorL = (Math.abs(PIDValues.leftDrive1.getError())
				+ Math.abs(PIDValues.leftDrive2.getError())) / 2.0;
		double averageEncoderErrorR = (Math.abs(PIDValues.rightDrive2.getError())
				+ Math.abs(PIDValues.rightDrive2.getError())) / 2.0;
		SmartDashboard.putNumber("Left encoder count", Motors.motorDriveLeft1.getEncPosition());
		SmartDashboard.putNumber("Right enc position", Motors.motorDriveRight1.getEncPosition());
		
		// System.out.println("Average Enc Error" + averageEncoderErrorL);
		// System.out.println("PIDRight error: " +
		// PIDValues.rightDrive1.getError());
		// System.out.println("PIDLeft error: " +
		// PIDValues.leftDrive1.getError());
		// System.out.println("MotorRight enc count: " +
		// Motors.motorDriveRight1.getEncPosition());
		// System.out.println("MotorLeft enc count: " +
		// Motors.motorDriveLeft1.getEncPosition());
		// System.out.println("MotorLeft setpoint: " +
		// PIDValues.leftDrive1.getSetpoint());
		// System.out.println("MotorRight setpoint: " +
		// PIDValues.rightDrive1.getSetpoint());
		System.out.println("MotorRight error" + PIDValues.rightDrive1.getError());
		System.out.println("MotorLeft error" + PIDValues.leftDrive1.getError());
		SmartDashboard.putNumber("MotorRight error PDIFDSFKJSDFKJDSF", PIDValues.rightDrive1.getError());
		SmartDashboard.putNumber("MotorLeft error apdfasdfasdfas;df", PIDValues.leftDrive1.getError());
		if (averageEncoderErrorL < 70000 && averageEncoderErrorR < 140000)
		{
			PIDValues.rightDrive1.disable();
			PIDValues.rightDrive2.disable();
			PIDValues.leftDrive1.disable();
			PIDValues.leftDrive2.disable();
			PIDDone = true;
			return true;
		} else
		{
			PIDDone = false;
		}
		return false;
	}
	
	// TODO finish
	public static boolean moveToDistancePID(double targetDistanceLeft, double targetDistanceRight)
	{
		// get the distance needed to move
		double distanceDeltaLeft = (Motors.motorDriveLeft1.getEncPosition() + targetDistanceLeft);
		double distanceDeltaRight = Motors.motorDriveRight1.getEncPosition() - targetDistanceRight;
		// set the encoder counts to zero
		// Motors.motorDriveLeft1.setEncPosition(0);
		// Motors.motorDriveRight1.setEncPosition(0);
		if ( !PIDDone && !PIDValues.leftDrive1.isEnabled() && !PIDValues.leftDrive2.isEnabled()
				&& !PIDValues.rightDrive1.isEnabled() && !PIDValues.rightDrive2.isEnabled())
		{
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
		}
		// test to see if the error is within 5000 encoder counts
		// if so, stop the motors
		// System.out.println("PIDRight error: " +
		// PIDValues.rightDrive1.getError());
		// System.out.println("PIDLeft error: " +
		// PIDValues.leftDrive1.getError());
		double averageEncoderErrorL = (Math.abs(PIDValues.leftDrive1.getError())
				+ Math.abs(PIDValues.leftDrive2.getError())) / 2.0;
		double averageEncoderErrorR = (Math.abs(PIDValues.rightDrive2.getError())
				+ Math.abs(PIDValues.rightDrive2.getError())) / 2.0;
		SmartDashboard.putNumber("Left encoder count", Motors.motorDriveLeft1.getEncPosition());
		SmartDashboard.putNumber("Right enc position", Motors.motorDriveRight1.getEncPosition());
		//
		// System.out.println("Average Enc Error" + averageEncoderErrorL);
		// System.out.println("PIDRight error: " +
		// PIDValues.rightDrive1.getError());
		// System.out.println("PIDLeft error: " +
		// PIDValues.leftDrive1.getError());
		// System.out.println("MotorRight enc count: " +
		// Motors.motorDriveRight1.getEncPosition());
		// System.out.println("MotorLeft enc count: " +
		// Motors.motorDriveLeft1.getEncPosition());
		// System.out.println("MotorLeft setpoint: " +
		// PIDValues.leftDrive1.getSetpoint());
		// System.out.println("MotorRight setpoint: " +
		// PIDValues.rightDrive1.getSetpoint());
		System.out.println("MotorRight error" + PIDValues.rightDrive1.getError());
		System.out.println("MotorLeft error" + PIDValues.leftDrive1.getError());
		SmartDashboard.putNumber("MotorRight error PDIFDSFKJSDFKJDSF", PIDValues.rightDrive1.getError());
		SmartDashboard.putNumber("MotorLeft error apdfasdfasdfas;df", PIDValues.leftDrive1.getError());
		if (averageEncoderErrorL < 72000 && averageEncoderErrorR < 143000)
		{
			PIDValues.rightDrive1.disable();
			PIDValues.rightDrive2.disable();
			PIDValues.leftDrive1.disable();
			PIDValues.leftDrive2.disable();
			PIDDone = true;
			return true;
		} /*
			 * else{ PIDDone = false; }
			 */
		return false;
	}
	
	public static boolean rotateRight(double angleToTurnTo)
	{
		// Get the necessary amount of degrees to turn
		// Set the setpoint value of the PIDControllers to degreesLeftToTurn
		// double degreesRightToTurn = Robot.navX.getYaw() + angleToTurnTo;
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
		if ( !PIDDone && !PIDValues.leftNavX1.isEnabled() && !PIDValues.leftNavX2.isEnabled()
				&& !PIDValues.rightNavX1.isEnabled() && !PIDValues.rightNavX2.isEnabled())
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
		double averageAngleError = ((Math.abs(PIDValues.leftNavX1.getError()) + Math.abs(PIDValues.leftNavX2.getError())
				+ Math.abs(PIDValues.rightNavX1.getError()) + Math.abs(PIDValues.rightNavX2.getError())) / 4.0);
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
		} else
		{
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
