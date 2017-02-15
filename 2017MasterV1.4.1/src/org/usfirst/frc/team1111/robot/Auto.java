package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.Drivetrain;
import subsystems.Fuel;
import variables.Dimensions;
import variables.Motors;
import variables.Sensors;

public class Auto
{
	
	// Auto-specific constants
	static double EncoderDistanceThreshold = 2000.0;
	static double AngleThreshold = 5.0;
	
	// Auto Process variables
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
	
	static double robotSpeed = 120.0; // Inches/second
	static double driveTime = 0;
	
	static Timer timer = new Timer();
	
	static double startTime = -1;
	
	// These are the steps for the peg methods
	public static boolean findValues = false, moveAlignDist = false, turnAngle = false, visionAlign = false,
			findPegDist = false, movePegDist = false, placeGear = false, waitToSettle = false;
	
	// These are the steps for the boiler methods
	public static boolean findBoilerValues = false, moveBoilerAlignDist = false, turnBoilerAngle = false,
			boilerVisionAlign = false, findBoilerDist = false, moveBoilerDist = false, shootHigh = false;
	
	// These are the steps for the reach methods
	public static boolean doPegSide = false, calculateValues = false, moveAway = false, turnToAlliance = false,
			moveToBoilerLine = false, turnToBarrier = false, moveToDump = false, turnToLimit = false,
			moveToLimit = false;
	
	// ----------------MISCELLANIOUS METHODS----------------
	
	// Drop the gear
	public static void dropGear()
	{
		Motors.gearHold1.setAngle(Motors.lGearDropdownAngle);
		Motors.gearHold2.setAngle(Motors.rGearDropdownAngle);
	}
	
	// ----------------THRESHOLD METHODS----------------
	
	//TODO Replicate pegmiddle changes for ALL methods
	
	// With encoders
	public static void pegMiddle()// TODO this requires perfect alignment by
									// driveteam.
	{
		if ( !findValues)
		{
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);

			double driveDistance = Dimensions.DIST_TO_DECK - (Dimensions.ROBOT_LENGTH) - (Dimensions.HOOK_LENGTH / 2.0);
			targetMovementDist = -(driveDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			findValues = true;
		}
		if ( !movePegDist) // TODO Replace with PID
		{
			Drivetrain.moveToDistance(targetMovementDist);
			SmartDashboard.putDouble("Left Enc", Motors.motorDriveLeft1.getEncPosition());
			SmartDashboard.putDouble("right Enc", Motors.motorDriveRight1.getEncPosition());
			SmartDashboard.putDouble("average enc", ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0));
			double distanceDelta = targetMovementDist + ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			SmartDashboard.putNumber("distance delta", distanceDelta);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
			{
				System.out.println("pizzs");
				Drivetrain.drive(0, 0);
				movePegDist = true;
			}
		}
		
		// Method to drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			System.out.println("DROP IT");
			dropGear();
			placeGear = true;
		} // TODO Do we want to back up?
	}
	
	// Without encoders
	public static void pegMiddleNoEnc()// TODO this requires perfect
										// alignment by driveteam.
	{
		if ( !findValues)
		{
			double driveDistance = Dimensions.DIST_TO_DECK - (Dimensions.ROBOT_LENGTH) - (Dimensions.HOOK_LENGTH / 2);
			
			driveTime = driveDistance / robotSpeed;
			driveTime *= 2;
			
			timer.reset();
			timer.start();
			
			findValues = true;
		}
		System.out.println("driveTime: " + driveTime + "startTime: " + timer.get());
		if ( !movePegDist)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			System.out.println("time delta: " + Math.abs(startTime - timer.get()));
			if (Math.abs(startTime - timer.get()) < driveTime)
				Drivetrain.drive( .5, -.5);
			else
				movePegDist = true;
		}
		
		System.out.println("MovePegDist: " + movePegDist + "placeGear: " + placeGear);
		
		// Method to drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			System.out.println("pizza");
			Drivetrain.drive(0, 0);
			dropGear();
			placeGear = true;
		} // TODO Do we want to back up?
	}
	
	// ----------------OBJECTIVE METHODS----------------
	
	// Gears
	// With Encoders
	public static void pegSide(boolean keystart, boolean red)
	{
		// Default pegSide method, in which the robot starts with a corner
		// touching the boiler or the robot starts within the retrieval zone
		// with a corner touching the intersection of the retrieval zone and the
		// alliance wall.
		if (keystart)
			pegSide(Dimensions.CORNER_TO_BOILER_CORNER + (Dimensions.ROBOT_WIDTH / 2), keystart, red);
		else
			pegSide(Dimensions.RETRIEVAL_ALLIANCE_INTERSECT - (Dimensions.ROBOT_WIDTH / 2), keystart, red);
	}
	
	public static void pegSide(double distFromCorner, boolean keystart, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		if ( !findValues)
		{
			lineUpWithPegDist = ((Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_BASE
					- (Dimensions.PEGBASE_TO_BARRIER - distFromCorner))
					* Math.tan(30.0 * Dimensions.RADIANS_CONVERSION)) - (Dimensions.ROBOT_LENGTH / 2);
			targetLineUpDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (lineUpWithPegDist * Sensors.INCHES_TO_DRIVE_ENCODER);
			if ((keystart && red) || ( !keystart && !red))
				degreesToRotate = 120;
			else
				degreesToRotate = -120;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			SmartDashboard.putNumber("Target angle", targetAngle);
			
			findValues = true;
		}
		
		if ( !moveAlignDist)
		{
			Drivetrain.moveToDistance(targetLineUpDist);
			double distanceDelta = targetLineUpDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveAlignDist = true;
		}
		
		if (moveAlignDist && !turnAngle)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnAngle = true;
		}
		
		// if (turnAngle && !visionAlign)
		// visionAlign = Vision.autoAlignWithGear();
		// This may not keep the robot from moving forward whilst aligning
		
		if (/* visionAlign */ turnAngle && !findPegDist)
		{
			distToPeg = (lineUpWithPegDist / (Math.cos(Math.PI / 3.0))) - (Dimensions.ROBOT_LENGTH / 2)
					- (Dimensions.HOOK_LENGTH / 2);
			distToPeg = -distToPeg;
			targetMovementDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distToPeg * Sensors.INCHES_TO_DRIVE_ENCODER);
			findPegDist = true;
		}
		
		if (findPegDist && !movePegDist)
		{
			Drivetrain.moveToDistance(targetMovementDist);
			double distanceDelta = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				movePegDist = true;
		}
		
		// Method to automatically drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			dropGear();
			placeGear = true;
		}
		
		if (placeGear && !waitToSettle)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			
			if (Math.abs(startTime - timer.get()) >= 1.5)
				waitToSettle = true;
		}
		
		if (waitToSettle)
			doPegSide = true;// TODO do we want to back up at all?
	}
	
	// Without Encoders
	public static void pegSideNoEnc(double distFromCorner, boolean keystart, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		if ( !findValues)
		{
			lineUpWithPegDist = ((Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_BASE
					- (Dimensions.PEGBASE_TO_BARRIER - distFromCorner))
					* Math.tan(30.0 * Dimensions.RADIANS_CONVERSION)) - (Dimensions.ROBOT_LENGTH / 2);
			
			driveTime = lineUpWithPegDist / robotSpeed;
			
			if ((keystart && red) || ( !keystart && !red))
				degreesToRotate = 120;
			else
				degreesToRotate = -120;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			SmartDashboard.putNumber("Target angle", targetAngle);
			
			findValues = true;
		}
		
		System.out.println("driveTime: " + driveTime + "startTime: " + timer.get());
		if ( !moveAlignDist)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			System.out.println("time delta: " + Math.abs(startTime - timer.get()));
			if (Math.abs(startTime - timer.get()) < driveTime)
				Drivetrain.drive( -.5, .5);
			else
				moveAlignDist = true;
		}
		
		if (moveAlignDist && !turnAngle)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnAngle = true;
		}
		
		// if (turnAngle && !visionAlign)
		// visionAlign = Vision.autoAlignWithGear();
		// This may not keep the robot from moving forward whilst aligning
		
		if (/* visionAlign */ turnAngle && !findPegDist)
		{
			distToPeg = (lineUpWithPegDist / (Math.cos(Math.PI / 3.0))) - (Dimensions.ROBOT_LENGTH / 2)
					- (Dimensions.HOOK_LENGTH / 2);
			
			driveTime = distToPeg / robotSpeed;
			
			findPegDist = true;
		}
		
		System.out.println("driveTime: " + driveTime + "startTime: " + timer.get());
		if (findPegDist && !movePegDist)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			System.out.println("time delta: " + Math.abs(startTime - timer.get()));
			if (Math.abs(startTime - timer.get()) < driveTime)
				Drivetrain.drive( .5, -.5);
			else
				movePegDist = true;
		}
		
		// Method to automatically drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			dropGear();
			placeGear = true;
		}
		
		if (placeGear && !waitToSettle)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			
			if (Math.abs(startTime - timer.get()) >= 1.5)
				waitToSettle = true;
		}
		
		if (waitToSettle)
			doPegSide = true;// TODO do we want to back up at all?
	}
	
	// Fuel
	// With Encoders
	public static void highBoiler(boolean red)
	{
		// Default highBoiler method, in which the robot starts with a corner
		// touching the boiler.
		highBoiler(Dimensions.CORNER_TO_BOILER_CORNER + (Dimensions.ROBOT_WIDTH / 2), red);
	}
	
	public static void highBoiler(double distFromCorner, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		if ( !findBoilerValues)
		{
			distFromCorner += Dimensions.CORNER_BOILER_SHIFT; // Necessary so
																// the bot lines
																// up with the
																// corner of the
																// boiler rather
																// than lining
																// up with the
																// center of the
																// boiler
			lineUpWithBoilerDist = (distFromCorner
					* Math.tan(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- (Dimensions.ROBOT_LENGTH / 2);
			targetLineUpDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0) + lineUpWithBoilerDist;
			
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
			double distanceDelta = targetLineUpDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveBoilerAlignDist = true;
		}
		
		// turn to boiler
		if (moveBoilerAlignDist && !turnBoilerAngle)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnBoilerAngle = true;
		}
		
		// TODO align with vision
		// if (turnBoilerAngle && !boilerVisionAlign)
		// boilerVisionAlign = Vision.autoAlignWithBoiler(); // TODO make it a
		// boolean
		// This may not keep the robot from
		// moving forward whilst aligning
		
		if (/* boilerVisionAlign */ turnBoilerAngle && !findBoilerDist)
		{
			distToBoiler = (distFromCorner / Math.cos(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- (Dimensions.ROBOT_LENGTH / 2);
			targetMovementDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER);
			findBoilerDist = true;
		}
		
		// move to the boiler (up against the wall)
		if (findBoilerDist && !moveBoilerDist)
		{
			Drivetrain.moveToDistance(targetMovementDist);
			double distanceDelta = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveBoilerDist = true;
		}
		
		// Shoot
		if (moveBoilerDist && !shootHigh)
			Fuel.autoShoot();
	}
	
	// Without Encoders
	public static void highBoilerNoEnc(double distFromCorner, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		if ( !findBoilerValues)
		{
			distFromCorner += Dimensions.CORNER_BOILER_SHIFT; // Necessary so
																// the bot lines
																// up with the
																// corner of the
																// boiler rather
																// than lining
																// up with the
																// center of the
																// boiler
			lineUpWithBoilerDist = (distFromCorner
					* Math.tan(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- (Dimensions.ROBOT_LENGTH / 2);
			
			driveTime = lineUpWithBoilerDist / robotSpeed;
			
			if (red)
				degreesToRotate = 136.25;
			else
				degreesToRotate = -136.25;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			SmartDashboard.putNumber("Target angle", targetAngle);
			
			findBoilerValues = true;
		}
		
		// move off of the wall until aligned with boiler
		System.out.println("driveTime: " + driveTime + "startTime: " + timer.get());
		if ( !moveBoilerAlignDist)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			System.out.println("time delta: " + Math.abs(startTime - timer.get()));
			if (Math.abs(startTime - timer.get()) < driveTime)
				Drivetrain.drive( -.5, .5);
			else
				moveBoilerAlignDist = true;
		}
		
		// turn to boiler
		if (moveBoilerAlignDist && !turnBoilerAngle)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnBoilerAngle = true;
		}
		
		// TODO align with vision
		// if (turnBoilerAngle && !boilerVisionAlign)
		// boilerVisionAlign = Vision.autoAlignWithBoiler(); // TODO make it a
		// boolean
		// This may not keep the robot from
		// moving forward whilst aligning
		
		if (/* boilerVisionAlign */ turnBoilerAngle && !findBoilerDist)
		{
			distToBoiler = (distFromCorner / Math.cos(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- (Dimensions.ROBOT_LENGTH / 2);
			
			driveTime = distToBoiler / robotSpeed;
			
			findBoilerDist = true;
		}
		
		// move to the boiler (up against the wall)
		System.out.println("driveTime: " + driveTime + "startTime: " + timer.get());
		if (findBoilerDist && !moveBoilerDist)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			System.out.println("time delta: " + Math.abs(startTime - timer.get()));
			if (Math.abs(startTime - timer.get()) < driveTime)
				Drivetrain.drive( -.5, .5);
			else
				moveBoilerDist = true;
		}
		
		// Shoot
		if (moveBoilerDist && !shootHigh)
			Fuel.autoShoot();
	}
	
	// ----------------REACH METHODS----------------
	
	public static void pegAndBoiler(boolean red)
	{
		// Default pegAndBoiler method, in which the robot starts with a corner
		// touching the boiler.
		pegAndBoiler(Dimensions.CORNER_TO_BOILER_CORNER + (Dimensions.ROBOT_WIDTH / 2), red);
	}
	
	public static void pegAndBoiler(double distFromCorner, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		
		// do pegLeft
		if ( !doPegSide)
			pegSide(distFromCorner, true, red);
			
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
			double retreatDistance = 40; // TODO arbitrary value
			targetRetreatDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			double distFromAllianceWall = (Dimensions.PEGBASE_ALLIANCE_WALL_TRIANGLE_HYPOTENUSE - retreatDistance)
					* Math.cos(Math.PI / 3.0);
			double distFromBarrier = Dimensions.PEGBASE_TO_BARRIER - (retreatDistance * Math.cos(Math.PI / 6.0));
			boilerToAlliance = distFromBarrier
					/ (Math.cos(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION));
			double distToBoilerLine = distFromAllianceWall - boilerToAlliance;
			distToBoilerLine += (Dimensions.CORNER_BOILER_SHIFT
					* Math.tan(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION)); // Necessary
			// to
			// line
			// up
			// cornerbot
			// with
			// cornerboiler
			// so
			// we
			// can
			// shoot
			// accurately
			targetAlignChangeDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distToBoilerLine * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			if (red)
				degreesToRotate = 60;
			else
				degreesToRotate = -60;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		// move away
		if ( !moveAway)
		{
			Drivetrain.moveToDistance(targetRetreatDistance);
			double distanceDelta = targetRetreatDistance - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveAway = true;
		}
		
		// MAYBE need to recalculate based on actual distance retreated
		// turn towards alliance wall
		if (moveAway && !turnToAlliance)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnToAlliance = true;
		}
		
		// move to boiler alignment line
		if (turnToAlliance && !moveToBoilerLine)
		{
			if (red)
				degreesToRotate = -Dimensions.BOILER_ALIGN_ANGLE;
			else
				degreesToRotate = Dimensions.BOILER_ALIGN_ANGLE;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			Drivetrain.moveToDistance(targetAlignChangeDistance);
			double distanceDelta = targetAlignChangeDistance - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveToBoilerLine = true;
		}
		
		// turn towards boiler
		if (moveToBoilerLine && !turnBoilerAngle)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnBoilerAngle = true;
		}
		
		// TODO align with vision
		// if (turnBoilerAngle && !boilerVisionAlign)
		// boilerVisionAlign = Vision.autoAlignWithBoiler(); // TODO See peg
		// vision alignment.
		// This may not keep the robot from
		// moving forward whilst aligning
		// boilerVisionAlign = true;
		
		if (/* boilerVisionAlign */ turnBoilerAngle && !findBoilerDist)
		{
			distToBoiler = (boilerToAlliance / Math.sin(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- Dimensions.CORNER_TO_BOILER - (Dimensions.ROBOT_LENGTH / 2);
			targetMovementDist = Motors.motorDriveLeft1.getEncPosition()
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER);
			findBoilerDist = true;
		}
		
		// move to the boiler (up against the wall)
		if (findBoilerDist && !moveBoilerDist)
		{
			Drivetrain.moveToDistance(targetMovementDist);
			double distanceDelta = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveBoilerDist = true;
		}
		
		// shoot
		if (moveBoilerDist && !shootHigh)
			Fuel.autoShoot();
		
	}
	
	public static void pegAndBoilerNoEnc(double distFromCorner, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		
		// do pegLeft
		if ( !doPegSide)
			pegSideNoEnc(distFromCorner, true, red);
			
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
			double retreatDistance = 40; // TODO arbitrary value
			
			double distFromAllianceWall = (Dimensions.PEGBASE_ALLIANCE_WALL_TRIANGLE_HYPOTENUSE - retreatDistance)
					* Math.cos(Math.PI / 3.0);
			double distFromBarrier = Dimensions.PEGBASE_TO_BARRIER - (retreatDistance * Math.cos(Math.PI / 6.0));
			boilerToAlliance = distFromBarrier
					/ (Math.cos(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION));
			double distToBoilerLine = distFromAllianceWall - boilerToAlliance;
			distToBoilerLine += (Dimensions.CORNER_BOILER_SHIFT
					* Math.tan(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION)); // Necessary
			// to
			// line
			// up
			// cornerbot
			// with
			// cornerboiler
			// so
			// we
			// can
			// shoot
			// accurately
			
			driveTime = distToBoilerLine / robotSpeed;
			
			if (red)
				degreesToRotate = 60;
			else
				degreesToRotate = -60;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		// move away
		System.out.println("driveTime: " + (50.0 / robotSpeed) + "startTime: " + timer.get());
		if ( !moveAway)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			System.out.println("time delta: " + Math.abs(startTime - timer.get()));
			if (Math.abs(startTime - timer.get()) < (50.0 / robotSpeed))
				Drivetrain.drive(-.5, .5);
			else
				moveAway = true;
		}
		
		// MAYBE need to recalculate based on actual distance retreated
		// turn towards alliance wall
		if (moveAway && !turnToAlliance)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnToAlliance = true;
		}
		
		// move to boiler alignment line
		System.out.println("driveTime: " + driveTime + "startTime: " + timer.get());
		if (turnToAlliance && !moveToBoilerLine)
		{
			if (red)
				degreesToRotate = -Dimensions.BOILER_ALIGN_ANGLE;
			else
				degreesToRotate = Dimensions.BOILER_ALIGN_ANGLE;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			if (startTime == -1.0)
				startTime = timer.get();
			System.out.println("time delta: " + Math.abs(startTime - timer.get()));
			if (Math.abs(startTime - timer.get()) < driveTime)
				Drivetrain.drive( -.5, .5);
			else
				moveToBoilerLine = true;
		}
		
		// turn towards boiler
		if (moveToBoilerLine && !turnBoilerAngle)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnBoilerAngle = true;
		}
		
		// TODO align with vision
		// if (turnBoilerAngle && !boilerVisionAlign)
		// boilerVisionAlign = Vision.autoAlignWithBoiler(); // TODO See peg
		// vision alignment.
		// This may not keep the robot from
		// moving forward whilst aligning
		// boilerVisionAlign = true;
		
		if (/* boilerVisionAlign */ turnBoilerAngle && !findBoilerDist)
		{
			distToBoiler = (boilerToAlliance / Math.sin(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- Dimensions.CORNER_TO_BOILER - (Dimensions.ROBOT_LENGTH / 2);
			
			driveTime = distToBoiler / robotSpeed;
			
			findBoilerDist = true;
		}
		
		// move to the boiler (up against the wall)
		System.out.println("driveTime: " + driveTime + "startTime: " + timer.get());
		if (findBoilerDist && !moveBoilerDist)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			System.out.println("time delta: " + Math.abs(startTime - timer.get()));
			if (Math.abs(startTime - timer.get()) < driveTime)
				Drivetrain.drive( -.5, .5);
			else
				moveBoilerDist = true;
		}
		
		// shoot
		if (moveBoilerDist && !shootHigh)
			Fuel.autoShoot();
		
	}
	
	public static void pegAndHopper(boolean red)
	{
		// Default pegAndHopper method, in which the robot starts with a corner
		// touching the boiler.
		pegAndHopper(Dimensions.CORNER_TO_BOILER_CORNER + (Dimensions.ROBOT_WIDTH / 2), red);
	}
	
	public static void pegAndHopper(double distFromCorner, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		
		// do pegLeft
		if ( !doPegSide)
			pegSide(distFromCorner, true, red);
			
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
			targetRetreatDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			double distFromBarrier = Dimensions.PEGBASE_TO_BARRIER - (retreatDistance * Math.cos(Math.PI / 6.0))
					- (Dimensions.ROBOT_LENGTH / 4);
			targetBarrierDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distFromBarrier * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			if (red)
				degreesToRotate = -30;
			else
				degreesToRotate = 30;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		// move off the airship
		if ( !moveAway)
		{
			Drivetrain.moveToDistance(targetRetreatDistance);
			double distanceDelta = targetRetreatDistance - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveAway = true;
		}
		
		// turn towards the hopper
		if (moveAway && !turnToBarrier)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnToBarrier = true;
		}
		
		// CHARGE
		if (turnToBarrier && !moveToDump)
		{
			Drivetrain.moveToDistance(targetBarrierDistance);
			double distanceDelta = targetBarrierDistance - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveToDump = true;
		}
	}
	
	public static void pegAndHopperNoEnc(double distFromCorner, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		
		// do pegLeft
		if ( !doPegSide)
			pegSideNoEnc(distFromCorner, true, red);
			
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
			double retreatDistance = Dimensions.HOOK_LENGTH * 2; // TODO
																	// arbitrary
																	// value?
			
			double distFromBarrier = Dimensions.PEGBASE_TO_BARRIER - (retreatDistance * Math.cos(Math.PI / 6.0))
					- (Dimensions.ROBOT_LENGTH / 4);
			
			driveTime = distFromBarrier / robotSpeed;
			
			if (red)
				degreesToRotate = -30;
			else
				degreesToRotate = 30;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		// move off the airship
		System.out.println("driveTime: " + (50.0 / robotSpeed) + "startTime: " + timer.get());
		if ( !moveAway)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			System.out.println("time delta: " + Math.abs(startTime - timer.get()));
			if (Math.abs(startTime - timer.get()) < (50.0 / robotSpeed))
				Drivetrain.drive(-.5, .5);
			else
				moveAway = true;
		}
		
		// turn towards the hopper
		if (moveAway && !turnToBarrier)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnToBarrier = true;
		}
		
		// CHARGE
		System.out.println("driveTime: " + driveTime + "startTime: " + timer.get());
		if (turnToBarrier && !moveToDump)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			System.out.println("time delta: " + Math.abs(startTime - timer.get()));
			if (Math.abs(startTime - timer.get()) < driveTime)
				Drivetrain.drive( -.5, .5);
			else
				moveToDump = true;
		}
	}
	
	public static void pegAndPrep(double distFromCorner, boolean red)
	{
		if ( !doPegSide)
		{
			pegSide(distFromCorner, false, red);
		}
		
		if (doPegSide && !calculateValues)
		{
			double retreatDistance = 40;// TODO arbitrary value
			targetRetreatDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			double distToAlliance = (Dimensions.PEGBASE_ALLIANCE_WALL_TRIANGLE_HYPOTENUSE - retreatDistance)
					* Math.cos(Math.PI / 3.0);
			double distToLimit = Dimensions.ALLIANCEWALL_TO_AUTOLIMIT - distToAlliance - 12;
			// 12in is to make sure we don't cross the limit
			targetMovementDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distToLimit * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			if (red)
				degreesToRotate = 120;
			else
				degreesToRotate = -120;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		if (calculateValues && !moveAway)
		{
			Drivetrain.moveToDistance(targetRetreatDistance);
			double distanceDelta = targetRetreatDistance - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveAway = true;
		}
		
		if (moveAway && !turnToLimit)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnToBarrier = true;
		}
		
		if (turnToLimit && !moveToLimit)
		{
			Drivetrain.moveToDistance(targetMovementDist);
			double distanceDelta = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveToLimit = true;
		}
	}
	
	public static void pegAndPrep(boolean red)
	{
		pegAndPrep(Dimensions.RETRIEVAL_ALLIANCE_INTERSECT - (Dimensions.ROBOT_WIDTH / 2), red);
	}
	
	// ~~~~~~~~~ Start of the using PID for Autonomous
	
	// TODO: Make all of the following methods implement PID
	
	// methods~~~~~~~~~~~~~~~~~~~~~~~~~~~//
	
	// ----------------THRESHOLD METHODS----------------
	
	// With encoders
	public static void pegMiddlePID()// TODO this requires perfect alignment by
										// driveteam.
	{
		if ( !findValues)
		{
			double driveDistance = Dimensions.DIST_TO_DECK - (Dimensions.ROBOT_LENGTH) - (Dimensions.HOOK_LENGTH / 2.0);
			targetMovementDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					- (driveDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			findValues = true;
		}
		if ( !movePegDist) // TODO Replace with PID
		{
			Drivetrain.moveToDistancePID(targetMovementDist);
			double distanceDelta = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			System.out.println("distance delta" + distanceDelta);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
			{
				System.out.println("pizzs");
				// Drivetrain.drive(0, 0);
				Drivetrain.moveToDistancePID(distanceDelta);
				movePegDist = true;
			}
		}
		
		// Method to drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			dropGear();
			placeGear = true;
		} // TODO Do we want to back up?
	}
	
	// ----------------OBJECTIVE METHODS----------------
	
	// Gears
	// With Encoders
	public static void pegSidePID(boolean keystart, boolean red)
	{
		// Default pegSide method, in which the robot starts with a corner
		// touching the boiler or the robot starts within the retrieval zone
		// with a corner touching the intersection of the retrieval zone and the
		// alliance wall.
		if (keystart)
			pegSidePID(Dimensions.CORNER_TO_BOILER_CORNER + (Dimensions.ROBOT_WIDTH / 2), keystart, red);
		else
			pegSidePID(Dimensions.RETRIEVAL_ALLIANCE_INTERSECT - (Dimensions.ROBOT_WIDTH / 2), keystart, red);
	}
	
	public static void pegSidePID(double distFromCorner, boolean keystart, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		if ( !findValues)
		{
			lineUpWithPegDist = ((Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_BASE
					- (Dimensions.PEGBASE_TO_BARRIER - distFromCorner))
					* Math.tan(30.0 * Dimensions.RADIANS_CONVERSION)) - (Dimensions.ROBOT_LENGTH / 2);
			targetLineUpDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (lineUpWithPegDist * Sensors.INCHES_TO_DRIVE_ENCODER);
			if ((keystart && red) || ( !keystart && !red))
				degreesToRotate = 120;
			else
				degreesToRotate = -120;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			SmartDashboard.putNumber("Target angle", targetAngle);
			
			findValues = true;
		}
		
		if ( !moveAlignDist)
		{
			Drivetrain.moveToDistancePID(targetLineUpDist);
			double distanceDelta = targetLineUpDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveAlignDist = true;
		}
		
		if (moveAlignDist && !turnAngle)
		{
			if(degreesToRotate > 0){
				Drivetrain.rotateRight(targetAngle);
			}else{
				Drivetrain.rotateLeft(-targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnAngle = true;
		}
		
		// if (turnAngle && !visionAlign)
		// visionAlign = Vision.autoAlignWithGear();
		// This may not keep the robot from moving forward whilst aligning
		
		if (/* visionAlign */ turnAngle && !findPegDist)
		{
			distToPeg = (lineUpWithPegDist / (Math.cos(Math.PI / 3.0))) - (Dimensions.ROBOT_LENGTH / 2)
					- (Dimensions.HOOK_LENGTH / 2);
			targetMovementDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					- (distToPeg * Sensors.INCHES_TO_DRIVE_ENCODER);
			findPegDist = true;
		}
		
		if (findPegDist && !movePegDist)
		{
			Drivetrain.moveToDistancePID(targetMovementDist);
			double distanceDelta = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				movePegDist = true;
		}
		
		// Method to automatically drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			dropGear();
			placeGear = true;
		}
		
		if (placeGear && !waitToSettle)
		{
			if (startTime == -1.0)
				startTime = timer.get();
			
			if (Math.abs(startTime - timer.get()) >= 1.5)
				waitToSettle = true;
		}
		
		if (waitToSettle)
			doPegSide = true;// TODO do we want to back up at all?
	}
	
	// Fuel
	// With Encoders
	public static void highBoilerPID(boolean red)
	{
		// Default highBoiler method, in which the robot starts with a corner
		// touching the boiler.
		highBoilerPID(Dimensions.CORNER_TO_BOILER_CORNER + (Dimensions.ROBOT_WIDTH / 2), red);
	}
	
	public static void highBoilerPID(double distFromCorner, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		if ( !findBoilerValues)
		{
			distFromCorner += Dimensions.CORNER_BOILER_SHIFT; // Necessary so
																// the bot lines
																// up with the
																// corner of the
																// boiler rather
																// than lining
																// up with the
																// center of the
																// boiler
			lineUpWithBoilerDist = (distFromCorner
					* Math.tan(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- (Dimensions.ROBOT_LENGTH / 2);
			targetLineUpDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0) + lineUpWithBoilerDist;
			
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
			Drivetrain.moveToDistancePID(targetLineUpDist);
			double distanceDelta = targetLineUpDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveBoilerAlignDist = true;
		}
		
		// turn to boiler
		if (moveBoilerAlignDist && !turnBoilerAngle)
		{
			if(degreesToRotate > 0){
				Drivetrain.rotateRight(targetAngle);
			}else{
				Drivetrain.rotateLeft(-targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnBoilerAngle = true;
		}
		
		// TODO align with vision
		// if (turnBoilerAngle && !boilerVisionAlign)
		// boilerVisionAlign = Vision.autoAlignWithBoiler(); // TODO make it a
		// boolean
		// This may not keep the robot from
		// moving forward whilst aligning
		
		if (/* boilerVisionAlign */ turnBoilerAngle && !findBoilerDist)
		{
			distToBoiler = (distFromCorner / Math.cos(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- (Dimensions.ROBOT_LENGTH / 2);
			targetMovementDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER);
			findBoilerDist = true;
		}
		
		// move to the boiler (up against the wall)
		if (findBoilerDist && !moveBoilerDist)
		{
			Drivetrain.moveToDistancePID(targetMovementDist);
			double distanceDelta = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveBoilerDist = true;
		}
		
		// Shoot
		if (moveBoilerDist && !shootHigh)
			Fuel.autoShoot();
	}
	
	// ----------------REACH METHODS----------------
	
	public static void pegAndBoilerPID(boolean red)
	{
		// Default pegAndBoiler method, in which the robot starts with a corner
		// touching the boiler.
		pegAndBoilerPID(Dimensions.CORNER_TO_BOILER_CORNER + (Dimensions.ROBOT_WIDTH / 2), red);
	}
	
	public static void pegAndBoilerPID(double distFromCorner, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		
		// do pegLeft
		if ( !doPegSide)
			pegSidePID(distFromCorner, true, red);
			
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
			double retreatDistance = 40; // TODO arbitrary value
			targetRetreatDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			double distFromAllianceWall = (Dimensions.PEGBASE_ALLIANCE_WALL_TRIANGLE_HYPOTENUSE - retreatDistance)
					* Math.cos(Math.PI / 3.0);
			double distFromBarrier = Dimensions.PEGBASE_TO_BARRIER - (retreatDistance * Math.cos(Math.PI / 6.0));
			boilerToAlliance = distFromBarrier
					/ (Math.cos(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION));
			double distToBoilerLine = distFromAllianceWall - boilerToAlliance;
			distToBoilerLine += (Dimensions.CORNER_BOILER_SHIFT
					* Math.tan(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION)); // Necessary
			// to
			// line
			// up
			// cornerbot
			// with
			// cornerboiler
			// so
			// we
			// can
			// shoot
			// accurately
			targetAlignChangeDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distToBoilerLine * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			if (red)
				degreesToRotate = 60;
			else
				degreesToRotate = -60;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		// move away
		if ( !moveAway)
		{
			Drivetrain.moveToDistancePID(targetRetreatDistance);
			double distanceDelta = targetRetreatDistance - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveAway = true;
		}
		
		// MAYBE need to recalculate based on actual distance retreated
		// turn towards alliance wall
		if (moveAway && !turnToAlliance)
		{
			if(degreesToRotate > 0){
				Drivetrain.rotateRight(targetAngle);
			}else{
				Drivetrain.rotateLeft(-targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnToAlliance = true;
		}
		
		// move to boiler alignment line
		if (turnToAlliance && !moveToBoilerLine)
		{
			if (red)
				degreesToRotate = -Dimensions.BOILER_ALIGN_ANGLE;
			else
				degreesToRotate = Dimensions.BOILER_ALIGN_ANGLE;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			Drivetrain.moveToDistancePID(targetAlignChangeDistance);
			double distanceDelta = targetAlignChangeDistance - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveToBoilerLine = true;
		}
		
		// turn towards boiler
		if (moveToBoilerLine && !turnBoilerAngle)
		{
			if(degreesToRotate > 0){
				Drivetrain.rotateRight(targetAngle);
			}else{
				Drivetrain.rotateLeft(-targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnBoilerAngle = true;
		}
		
		// TODO align with vision
		// if (turnBoilerAngle && !boilerVisionAlign)
		// boilerVisionAlign = Vision.autoAlignWithBoiler(); // TODO See peg
		// vision alignment.
		// This may not keep the robot from
		// moving forward whilst aligning
		// boilerVisionAlign = true;
		
		if (/* boilerVisionAlign */ turnBoilerAngle && !findBoilerDist)
		{
			distToBoiler = (boilerToAlliance / Math.sin(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- Dimensions.CORNER_TO_BOILER - (Dimensions.ROBOT_LENGTH / 2);
			targetMovementDist = Motors.motorDriveLeft1.getEncPosition()
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER);
			findBoilerDist = true;
		}
		
		// move to the boiler (up against the wall)
		if (findBoilerDist && !moveBoilerDist)
		{
			Drivetrain.moveToDistancePID(targetMovementDist);
			double distanceDelta = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveBoilerDist = true;
		}
		
		// shoot
		if (moveBoilerDist && !shootHigh)
			Fuel.autoShoot();
		
	}
	
	public static void pegAndHopperPID(boolean red)
	{
		// Default pegAndHopper method, in which the robot starts with a corner
		// touching the boiler.
		pegAndHopperPID(Dimensions.CORNER_TO_BOILER_CORNER + (Dimensions.ROBOT_WIDTH / 2), red);
	}
	
	public static void pegAndHopperPID(double distFromCorner, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
		// ALL distances are measured in inches
		
		// do pegLeft
		if ( !doPegSide)
			pegSidePID(distFromCorner, true, red);
			
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
			targetRetreatDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			double distFromBarrier = Dimensions.PEGBASE_TO_BARRIER - (retreatDistance * Math.cos(Math.PI / 6.0))
					- (Dimensions.ROBOT_LENGTH / 4);
			targetBarrierDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distFromBarrier * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			if (red)
				degreesToRotate = -30;
			else
				degreesToRotate = 30;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		// move off the airship
		if ( !moveAway)
		{
			Drivetrain.moveToDistancePID(targetRetreatDistance);
			double distanceDelta = targetRetreatDistance - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveAway = true;
		}
		
		// turn towards the hopper
		if (moveAway && !turnToBarrier)
		{
			if(degreesToRotate > 0){
				Drivetrain.rotateRight(targetAngle);
			}else{
				Drivetrain.rotateLeft(-targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnToBarrier = true;
		}
		
		// CHARGE
		if (turnToBarrier && !moveToDump)
		{
			Drivetrain.moveToDistancePID(targetBarrierDistance);
			double distanceDelta = targetBarrierDistance - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveToDump = true;
		}
	}
	
	public static void pegAndPrepPID(double distFromCorner, boolean red)
	{
		if ( !doPegSide)
		{
			pegSidePID(distFromCorner, false, red);
		}
		
		if (doPegSide && !calculateValues)
		{
			double retreatDistance = 40;// TODO arbitrary value
			targetRetreatDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			double distToAlliance = (Dimensions.PEGBASE_ALLIANCE_WALL_TRIANGLE_HYPOTENUSE - retreatDistance)
					* Math.cos(Math.PI / 3.0);
			double distToLimit = Dimensions.ALLIANCEWALL_TO_AUTOLIMIT - distToAlliance - 12;
			// 12in is to make sure we don't cross the limit
			targetMovementDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distToLimit * Sensors.INCHES_TO_DRIVE_ENCODER);
			
			if (red)
				degreesToRotate = 120;
			else
				degreesToRotate = -120;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			calculateValues = true;
		}
		
		if (calculateValues && !moveAway)
		{
			Drivetrain.moveToDistancePID(targetRetreatDistance);
			double distanceDelta = targetRetreatDistance - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveAway = true;
		}
		
		if (moveAway && !turnToLimit)
		{
			if(degreesToRotate > 0){
				Drivetrain.rotateRight(targetAngle);
			}else{
				Drivetrain.rotateLeft(-targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnToBarrier = true;
		}
		
		if (turnToLimit && !moveToLimit)
		{
			Drivetrain.moveToDistancePID(targetMovementDist);
			double distanceDelta = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveAway = true;
		}
	}
	
	public static void pegAndPrepPID(boolean red)
	{
		pegAndPrepPID(Dimensions.RETRIEVAL_ALLIANCE_INTERSECT - (Dimensions.ROBOT_WIDTH / 2), red);
	}
	
}