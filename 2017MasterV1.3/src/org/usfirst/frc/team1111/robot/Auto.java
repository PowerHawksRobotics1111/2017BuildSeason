package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.Drivetrain;
import subsystems.Vision;
import variables.Dimensions;
import variables.Motors;
import variables.Sensors;

public class Auto
{
	
	static boolean retrievedImage = false;
	static double degreesToRotate;
	static double targetAngle = 0;
	static double lineUpWithPegDist; // In inches
	static double lineUpWithBoilerDist; // In inches
	static double targetLineUpDist = 0;
	static double distToPeg; // In inches
	static double distToBoiler; // In inches
	static double targetMovementDist = 0;
	static double targetRetreatDistance = 0;
	static double targetAlignChangeDistance = 0;
	static double boilerToAlliance = 0; // NOT a dimension
	static double targetBarrierDistance = 0;
	
	static double startTime = -1;
	
	// These are the steps for the peg methods
	public static boolean findValues = false, moveAlignDist = false, turnAngle = false, visionAlign = false,
			findPegDist = false, movePegDist = false, placeGear = false, waitToSettle = false;
	
	// These are the steps for the boiler methods
	public static boolean findBoilerValues = false, moveBoilerAlignDist = false, turnBoilerAngle = false,
			boilerVisionAlign = false, findBoilerDist = false, moveBoilerDist = false, shootHigh = false;
	
	// These are the steps for the reach methods
	public static boolean doPegSide = false, calculateValues = false, moveAway = false, turnToAlliance = false,
			moveToBoilerLine = false, turnToBarrier = false, moveToDump;
	
	// ----------------MISCELLANIOUS METHODS----------------
	
	// Drop the gear
	public static void dropGear()
	{
		Motors.gearHold1.setAngle(Motors.gearStopdownAngle);
		Motors.gearHold2.setAngle(180 - Motors.gearStopdownAngle);
	}
	
	// Shooting automatically
	public static void shootFuel()
	{
		Motors.motorShooter.set(Motors.shooterVoltage);
	}
	
	// ----------------THRESHOLD METHODS----------------
	
	// With encoders
	public static void pegMiddle()// TODO this requires perfect alignment by
	// driveteam.
	{
		if ( !findValues)
		{
			double driveDistance = Dimensions.DIST_TO_DECK - (Dimensions.ROBOT_LENGTH) - (Dimensions.HOOK_LENGTH / 2);
			targetMovementDist = Motors.motorDriveLeft2.getEncPosition()
					+ (driveDistance * Sensors.INCHES_TO_DRIVE_ENCODER);// Inches
			
			findValues = true;
		}
		
		if ( !movePegDist)
		{
			Drivetrain.moveToDistance(targetMovementDist);
			double distanceDelta = targetMovementDist - Motors.motorDriveLeft2.getEncPosition();
			if ((distanceDelta > -50) && (distanceDelta < 50))
				movePegDist = true;
		}
		
		// Method to drop gear on peg when called
		if (movePegDist && !placeGear)
			dropGear();// TODO Do we want to back up?
	}
	
	// TODO Without encoders
	
	// ----------------OBJECTIVE METHODS----------------
	
	// Gears
	public static void pegSide(double distFromCorner, boolean left)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		if ( !findValues)
		{
			lineUpWithPegDist = ((Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_BASE
					- (Dimensions.PEGBASE_TO_BARRIER - distFromCorner)) * Math.tan(30.0 * (Math.PI / 180)))
					- (Dimensions.ROBOT_LENGTH / 2);
			targetLineUpDist = Motors.motorDriveLeft2.getEncPosition()
					+ (lineUpWithPegDist * Sensors.INCHES_TO_DRIVE_ENCODER);
			if (left)
				degreesToRotate = 60;
			else
				degreesToRotate = -60;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			SmartDashboard.putNumber("Target angle", targetAngle);
			
			findValues = true;
		}
		
		if ( !moveAlignDist)
		{
			Drivetrain.moveToDistance(targetLineUpDist);
			double distanceDelta = targetLineUpDist - Motors.motorDriveLeft2.getEncPosition();
			if ((distanceDelta > -50) && (distanceDelta < 50))
				moveAlignDist = true;
		}
		
		// Drivetrain.turnCWNumDegrees(60); Utilizes illegal while loop
		
		if (moveAlignDist && !turnAngle)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < 5)
				turnAngle = true;
		}
		
		// TODO This will run vision.autoalign for one iteration, then move on
		// to find peg distance. You need to check if Vision.autoAlign is
		// finished before you move on. I recommend returning a boolean.
		if (turnAngle && !visionAlign)
			visionAlign = Vision.autoAlignWithGear();
		// This may not keep the robot from moving forward whilst aligning
		
		if (visionAlign && !findPegDist)
		{
			distToPeg = (lineUpWithPegDist / (Math.cos(Math.PI / 3))) - (Dimensions.ROBOT_LENGTH / 2)
					- (Dimensions.HOOK_LENGTH / 2);
			targetMovementDist = Motors.motorDriveLeft2.getEncPosition()
					+ (distToPeg * Sensors.INCHES_TO_DRIVE_ENCODER);
			findPegDist = true;
		}
		
		if (findPegDist && !movePegDist)
		{
			Drivetrain.moveToDistance(targetMovementDist);
			double distanceDelta = targetMovementDist - Motors.motorDriveLeft2.getEncPosition();
			if ((distanceDelta > -50) && (distanceDelta < 50))
				movePegDist = true;
		}
		
		// Method to automatically drop gear on peg when called
		if (movePegDist && !placeGear)
			dropGear();
		
		if (placeGear && !waitToSettle)
		{
			if (startTime == -1)
				startTime = Timer.getMatchTime();
			
			if (Math.abs(startTime - Timer.getMatchTime()) >= 1.5)
				waitToSettle = true;
		}
		
		if (placeGear)
			doPegSide = true;// TODO do we want to back up at all?
	}
	
	// Fuel
	public static void highBoiler(double distFromCorner, boolean red)
	{
		if ( !findBoilerValues)
		{
			lineUpWithBoilerDist = (distFromCorner * Math.tan(43.75 * (Math.PI / 180))) - (Dimensions.ROBOT_LENGTH / 2);
			targetLineUpDist = Motors.motorDriveLeft2.getEncPosition() + lineUpWithBoilerDist;
			
			if (red)
				degreesToRotate = 136.25;
			else
				degreesToRotate = -136.25;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			SmartDashboard.putNumber("Target angle", targetAngle);
			
			findBoilerValues = true;
		}
		
		// move off of the wall until aligned with boiler
		if ( !moveBoilerAlignDist)
		{
			Drivetrain.moveToDistance(targetLineUpDist);
			double distanceDelta = targetLineUpDist - Motors.motorDriveLeft2.getEncPosition();
			if ((distanceDelta > -50) && (distanceDelta < 50))
				moveBoilerAlignDist = true;
		}
		
		// turn to boiler
		if (moveBoilerAlignDist && !turnBoilerAngle)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < 5)
				turnBoilerAngle = true;
		}
		
		// TODO align with vision
		if (turnBoilerAngle && !boilerVisionAlign)
			// Vision.autoAlignWithBoiler(); //TODO See peg vision alignment.
			// This may not keep the robot from
			// moving forward whilst aligning
			boilerVisionAlign = true;
		
		if (boilerVisionAlign && !findBoilerDist)
		{
			distToBoiler = (distFromCorner / Math.cos(43.75 * (Math.PI / 180))) - (Dimensions.ROBOT_LENGTH / 2);
			targetMovementDist = Motors.motorDriveLeft2.getEncPosition()
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER);
			findBoilerDist = true;
		}
		
		// move to the boiler (up against the wall)
		if (findBoilerDist && !moveBoilerDist)
		{
			Drivetrain.moveToDistance(targetMovementDist);
			double distanceDelta = targetMovementDist - Motors.motorDriveLeft2.getEncPosition();
			if ((distanceDelta > -50) && (distanceDelta < 50))
				moveBoilerDist = true;
		}
		
		// Shoot
		if (moveBoilerDist && !shootHigh)
			shootFuel();
	}
	
	// ----------------REACH METHODS----------------
	
	public static void pegAndBoiler(double distFromCorner, boolean red)
	{
		// do pegLeft
		if ( !doPegSide)
			pegSide(distFromCorner, !red);
			
		// This only applies for a passive gear system
		// if (doPegSide && !waitForRetrieval)
		// {
		// if (startTime == -1) startTime =
		// Timer.getMatchTime();
		//
		// if (Math.abs(startTime - Timer.getMatchTime()) >= 5.0)
		// {
		// waitForRetrieval = true;
		// }
		// }
		
		// calculate values (distances)
		if (doPegSide && !calculateValues)
		{
			double retreatDistance = 50;
			targetRetreatDistance = Motors.motorDriveLeft2.getEncPosition()
					- (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			double distFromAllianceWall = (Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_HYPOTENUSE - retreatDistance)
					* Math.cos(Math.PI / 3);
			double distFromBarrier = Dimensions.PEGBASE_TO_BARRIER - (retreatDistance * Math.cos(Math.PI / 6));
			boilerToAlliance = distFromBarrier / (Math.cos(43.75 * (Math.PI / 180)));
			double distToBoilerLine = distFromAllianceWall - boilerToAlliance;
			targetAlignChangeDistance = Motors.motorDriveLeft2.getEncPosition()
					+ (distToBoilerLine * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			if (red)
				degreesToRotate = -120;
			else
				degreesToRotate = 120;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		// move away
		if ( !moveAway)
		{
			Drivetrain.moveToDistance(targetRetreatDistance);
			double distanceDelta = targetRetreatDistance - Motors.motorDriveLeft2.getEncPosition();
			if ((distanceDelta > -50) && (distanceDelta < 50))
				moveAway = true;
		}
		
		// MAYBE need to recalculate based on actual distance retreated
		// turn towards alliance wall
		if (moveAway && !turnToAlliance)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < 5)
				turnToAlliance = true;
		}
		
		// move to boiler alignment line
		if (turnToAlliance && !moveToBoilerLine)
		{
			if (red)
				degreesToRotate = -43.75;
			else
				degreesToRotate = 43.75;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			Drivetrain.moveToDistance(targetAlignChangeDistance);
			double distanceDelta = targetAlignChangeDistance - Motors.motorDriveLeft2.getEncPosition();
			if ((distanceDelta > -50) && (distanceDelta < 50))
				moveToBoilerLine = true;
		}
		
		// turn towards boiler
		if (moveToBoilerLine && !turnBoilerAngle)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < 5)
				turnBoilerAngle = true;
		}
		
		// TODO align with vision
		if (turnBoilerAngle && !boilerVisionAlign)
			// Vision.autoAlignWithBoiler(); //TODO See peg vision alignment.
			// This may not keep the robot from
			// moving forward whilst aligning
			boilerVisionAlign = true;
		
		if (boilerVisionAlign && !findBoilerDist)
		{
			distToBoiler = (boilerToAlliance / Math.sin(43.75 * (Math.PI / 180))) - Dimensions.CORNER_TO_BOILER
					- (Dimensions.ROBOT_LENGTH / 2);
			targetMovementDist = Motors.motorDriveLeft2.getEncPosition()
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER);
			findBoilerDist = true;
		}
		
		// move to the boiler (up against the wall)
		if (findBoilerDist && !moveBoilerDist)
		{
			Drivetrain.moveToDistance(targetMovementDist);
			double distanceDelta = targetMovementDist - Motors.motorDriveLeft2.getEncPosition();
			if ((distanceDelta > -50) && (distanceDelta < 50))
				moveBoilerDist = true;
		}
		
		// shoot
		if (moveBoilerDist && !shootHigh)
			shootFuel();
		
	}
	
	public static void pegAndHopper(double distFromCorner, boolean red)
	{
		// do pegLeft
		if ( !doPegSide)
			pegSide(distFromCorner, !red);
			
		// This only applies for a passive gear system
		// if (doPegSide && !waitForRetrieval)
		// {
		// if (startTime == -1) startTime =
		// Timer.getMatchTime();
		//
		// if (Math.abs(startTime - Timer.getMatchTime()) >= 5.0)
		// {
		// waitForRetrieval = true;
		// }
		// }
		
		if (doPegSide && !calculateValues)
		{
			double retreatDistance = Dimensions.HOOK_LENGTH * 2;
			targetRetreatDistance = Motors.motorDriveLeft2.getEncPosition()
					- (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			double distFromBarrier = Dimensions.PEGBASE_TO_BARRIER - (retreatDistance * Math.cos(Math.PI / 6))
					- (Dimensions.ROBOT_LENGTH / 4);
			targetBarrierDistance = Motors.motorDriveLeft2.getEncPosition()
					+ (distFromBarrier * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			if (red)
			{
				degreesToRotate = 150;
			} else
			{
				degreesToRotate = -150;
			}
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		// move off the airship
		if ( !moveAway)
		{
			Drivetrain.moveToDistance(targetRetreatDistance);
			double distanceDelta = targetRetreatDistance - Motors.motorDriveLeft2.getEncPosition();
			if ((distanceDelta > -50) && (distanceDelta < 50))
				moveAway = true;
		}
		
		// turn towards the hopper
		if (moveAway && !turnToBarrier)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < 5)
				turnToBarrier = true;
		}
		
		// CHARGE
		if (turnToBarrier && !moveToDump)
		{
			Drivetrain.moveToDistance(targetBarrierDistance);
			double distanceDelta = targetBarrierDistance - Motors.motorDriveLeft2.getEncPosition();
			if ((distanceDelta > -50) && (distanceDelta < 50))
				moveToDump = true;
		}
	}
	
}
