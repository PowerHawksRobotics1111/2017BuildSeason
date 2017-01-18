package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.Drivetrain;
import subsystems.Vision;
import variables.Dimensions;
import variables.Motors;
import variables.Sensors;

public class Auto
{
	
	static boolean retrievedImage = false;
	static double[] TargetX;
	static double[] TargetY;
	static double degreesToRotate;
	static double targetAngle = 0;
	static double lineUpWithPegDist; // In inches
	static double lineUpWithBoilerDist; // In inches
	static double targetLineUpDist = 0;
	static double distToPeg; // In inches
	static double targetPegDist = 0;
	
	// These are for the peg methods
	public static boolean findValues = false, moveAlignDist = false, turnAngle = false, visionAlign = false,
			findPegDist = false, movePegDist = false;
	
	// These are for the boiler
	public static boolean findBoilerValues = false, moveBoilerAlignDist = false, turnBoilerAngle = false,
			boilerVisionAlign = false, findBoilerDist = false, moveBoilerDist = false;
	
	// ----------------THRESHOLD METHODS----------------
	
	// With encoders
	public static void pegMiddle()// TODO this requires perfect alignment by
									// driveteam.
	{
		if ( !findValues)
		{
			double driveDistance; // In inches
			driveDistance = Dimensions.DIST_TO_DECK - (Dimensions.ROBOT_LENGTH) - (Dimensions.HOOK_LENGTH / 2);
			targetPegDist = Motors.motorDriveLeft2.getEncPosition() + driveDistance;
			
			findValues = true;
		}
		
		if (findValues && !movePegDist)
		{
			Drivetrain.moveToDistance(targetPegDist);
			double distanceDelta = targetPegDist - Motors.motorDriveLeft2.getEncPosition();
			if (distanceDelta < 0)
				movePegDist = true;
		}
	}
	
	// Without encoders
	
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
					- (Dimensions.PEGBASE_TO_BARRIER - distFromCorner)) * Math.tan(30.0))
					- (Dimensions.ROBOT_LENGTH / 2);
			targetLineUpDist = Motors.motorDriveLeft2.getEncPosition() + lineUpWithPegDist;
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
		
		if (turnAngle && !visionAlign)
		{
			Vision.autoAlignWithAirship(); // This may not keep the robot from
											// moving forward whilst aligning
			visionAlign = true;
		}
		
		if (visionAlign && !findPegDist)
		{
			distToPeg = (lineUpWithPegDist / (Math.cos(Math.PI / 3))) - (Dimensions.ROBOT_LENGTH / 2)
					- (Dimensions.HOOK_LENGTH / 2);
			targetPegDist = Motors.motorDriveLeft2.getEncPosition() + distToPeg;
			findPegDist = true;
		}
		
		if (findPegDist && !movePegDist)
		{
			Drivetrain.moveToDistance(targetPegDist);
			double distanceDelta = targetPegDist - Motors.motorDriveLeft2.getEncPosition();
			if (distanceDelta < 0)
				movePegDist = true;
		}
	}
	
	// Fuel
	public static void highBoiler(double distfromcorner)
	{
		// move off of the wall until aligned with boiler
		// turn to boiler
		// align with vision
		// move to the boiler (up against the wall)
		// shoot
	}
	
	// ----------------REACH METHODS----------------
	public static void pegAndBoiler()
	{
		// do pegLeft
		// turn around
		// align with boiler
		// align using vision
		// move to the boiler (up against the wall)
		// shoot
	}
	
	public static void pegAndHopper()
	{
		// do pegLeft
		// move off the airship
		// turn towards the hopper
		// CHARGE
	}
	
}
