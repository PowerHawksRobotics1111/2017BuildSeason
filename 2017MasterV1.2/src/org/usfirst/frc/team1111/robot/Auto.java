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
	
	// These are the steps for the peg methods
	public static boolean findValues = false, moveAlignDist = false, turnAngle = false, visionAlign = false,
			findPegDist = false, movePegDist = false;
	
	// These are the steps for the boiler methods
	public static boolean findBoilerValues = false, moveBoilerAlignDist = false, turnBoilerAngle = false,
			boilerVisionAlign = false, findBoilerDist = false, moveBoilerDist = false, shootHigh = false;
	
	// These are the steps for the reach methods
	public static boolean doPegSide = false, waitForRetrieval = false, calculateValues = false, moveAway = false,
			turnToAlliance = false, moveToBoilerLine = false;
	
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
			if (distanceDelta < 0)
				movePegDist = true;
		}
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
			if (distanceDelta < 0)
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
		
		if (turnAngle && !visionAlign)// TODO This will run vision.autoalign for
										// one
										// iteration, then move on to find peg
										// distance. You need to check if
										// Vision.autoAlign is finished before
										// you move on. I recommend returning a
										// boolean.
		{
			Vision.autoAlign(); // This may not keep the robot from
			// moving forward whilst aligning
			visionAlign = true;
		}
		
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
			if (distanceDelta < 0)
				movePegDist = true;
		}
		
		if (movePegDist)
		{
			doPegSide = true;
		}
	}
	
	// Fuel
	public static void highBoiler(double distFromCorner)
	{
		if ( !findBoilerValues)
		{
			lineUpWithBoilerDist = (distFromCorner * Math.tan(43.75 * (Math.PI / 180))) - (Dimensions.ROBOT_LENGTH / 2);
			targetLineUpDist = Motors.motorDriveLeft2.getEncPosition() + lineUpWithBoilerDist;
			
			degreesToRotate = 136.25;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			SmartDashboard.putNumber("Target angle", targetAngle);
			
			findBoilerValues = true;
		}
		
		// move off of the wall until aligned with boiler
		if ( !moveBoilerAlignDist)
		{
			Drivetrain.moveToDistance(targetLineUpDist);
			double distanceDelta = targetLineUpDist - Motors.motorDriveLeft2.getEncPosition();
			if (distanceDelta < 0)
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
			if (distanceDelta < 0)
				moveBoilerDist = true;
		}
		
		// TODO shoot
		if (moveBoilerDist && !shootHigh)
		{
			// run shooting method
		}
	}
	
	// ----------------REACH METHODS----------------
	
	static double startTime = -1;
	
	public static void pegAndBoiler(double distFromCorner)
	{
		// do pegLeft
		if ( !doPegSide)
		{
			pegSide(distFromCorner, true);
		} // doPegSide is set to true at the end of pegSide
		
		if (doPegSide && !waitForRetrieval)
		{
			if (startTime == -1)
				startTime = Timer.getMatchTime();
			
			if (Math.abs(startTime - Timer.getMatchTime()) >= 5.0)
			{
				waitForRetrieval = true;
			}
			
		}
		
		// calculate values (distances)
		if (waitForRetrieval && !calculateValues)
		{
			double retreatDistance = 50;
			targetRetreatDistance = Motors.motorDriveLeft2.getEncPosition()
					- (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			double distFromAllianceWall = (Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_HYPOTENUSE - retreatDistance)
					* Math.sin(Math.PI / 3);
			double distFromBarrier = retreatDistance * Math.cos(Math.PI / 6);
			boilerToAlliance = distFromBarrier / (Math.cos(43.75 * (Math.PI / 180)));
			double distToBoilerLine = distFromAllianceWall - boilerToAlliance;
			targetAlignChangeDistance = Motors.motorDriveLeft2.getEncPosition()
					+ (distToBoilerLine * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			degreesToRotate = 120;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		// move away
		if ( !moveAway)
		{
			Drivetrain.moveToDistance(targetRetreatDistance);
			double distanceDelta = targetRetreatDistance - Motors.motorDriveLeft2.getEncPosition();
			if (distanceDelta < 0)
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
			degreesToRotate = 46.25;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			Drivetrain.moveToDistance(targetAlignChangeDistance);
			double distanceDelta = targetAlignChangeDistance - Motors.motorDriveLeft2.getEncPosition();
			if (distanceDelta < 0)
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
			if (distanceDelta < 0)
				moveBoilerDist = true;
		}
		
		// TODO shoot
		if (moveBoilerDist && !shootHigh)
		{
			// run shooting method
		}
		
	}
	
	public static void pegAndHopper(double distFromCorner)
	{
		// do pegLeft
		if ( !doPegSide)
		{
			pegSide(distFromCorner, true);
		} // doPegSide is set to true at the end of pegSide
		
		// move off the airship
		// turn towards the hopper
		// CHARGE
	}
	
}
