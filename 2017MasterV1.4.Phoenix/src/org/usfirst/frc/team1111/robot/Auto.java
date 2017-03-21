package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import subsystems.Drivetrain;
import subsystems.Fuel;
import subsystems.Vision;
import variables.Dimensions;
import variables.Motors;
import variables.PIDValues;
import variables.Sensors;

public class Auto
{
	
	// Auto-specific constants
	static double EncoderDistanceThreshold = 10000.0; // 3000
	static double AngleThreshold = 2;
	
	// Auto Process variables
	static double degreesToRotate;
	static double targetAngle = 0;
	static double lineUpWithPegDist; // In inches
	static double lineUpWithBoilerDist; // In inches
	static double targetLineUpDist = 0;
	static double distToPeg; // In inches
	static double distToBoiler; // In inches
	static double targetMovementDist = 0;
	static double targetMovementDistLeft = 0;
	static double targetRetreatDistance = 0;
	static double targetAlignChangeDistance = 0;
	static double boilerToAlliance = 0; // NOT a dimension
	static double targetBarrierDistance = 0;
	static double targetMovementDistRight = 0;
	static double targetLineUpDistLeft = 0;
	static double targetLineUpDistRight = 0;
	
	static double robotSpeed = 120.0; // Inches/second
	static double driveTime = 0;
	
	static Timer timer = new Timer();
	
	static double startTime = -1.0;
	
	// These are the steps for the peg methods
	public static boolean findValues = false, moveAlignDist = false, turnAngle = false, visionAlign = false,
			findPegDist = false, movePegDist = false, placeGear = false, waitToSettle = false;
	
	// These are the steps for the boiler methods
	public static boolean findBoilerValues = false, moveBoilerAlignDist = false, turnBoilerAngle = false,
			boilerVisionAlign = false, findBoilerDist = false, moveBoilerDist = false, shootHigh = false;
	
	// These are the steps for the reach methods
	public static boolean extraTasks = false, doPegSide = false, calculateValues = false, moveAway = false,
			turnToAlliance = false, moveToBoilerLine = false, turnToBarrier = false, moveToDump = false,
			turnToLimit = false, moveToLimit = false;
	
	// Temporary?
	public static boolean primeGear = false;
	
	// ----------------MISCELLANIOUS METHODS----------------
	
	// Drop the gear
	public static void dropGear()
	{
		// if (timer.get() == 0)
		// {
		Drivetrain.drive(Motors.AUTO_ALIGN_POWER, -Motors.AUTO_ALIGN_POWER);
		Motors.gearHold1.set(Motors.lGearDropdownAngle);
		Motors.gearHold2.set(Motors.rGearDropdownAngle);
		Timer.delay(1.5);
		Motors.pushPiston1.set(DoubleSolenoid.Value.kForward);
		Motors.pushPiston2.set(DoubleSolenoid.Value.kForward);
		Timer.delay(1);
		// timer.start();
		// }
		// if (timer.get()>= .5)
		// {
		// Motors.gearHold1.set(Motors.lGearStopdownAngle);
		// Motors.gearHold2.set(Motors.rGearStopdownAngle);
		// Motors.pushPiston1.set(DoubleSolenoid.Value.kReverse);
		// Motors.pushPiston2.set(DoubleSolenoid.Value.kReverse);
		// Robot.gearReleased = false;
		// timer.reset();
		// timer.stop();
		// }
	}
	
	// ----------------THRESHOLD METHODS----------------
	// With encoders
	public static void pegMiddle()
	{
		if ( !findValues)
		{
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			
			double driveDistance = Dimensions.DIST_TO_DECK - (Dimensions.ROBOT_LENGTH) - (Dimensions.HOOK_LENGTH / 2.0);
			targetMovementDist = -(driveDistance * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
			findValues = true;
		}
		
		if ( !movePegDist)
		{
			// Motors.gearHold1.set(Motors.lGearPrimeAngle);
			// Motors.gearHold2.set(Motors.rGearPrimeAngle);
			
			Drivetrain.moveToDistance(targetMovementDist);
			// SmartDashboard.putDouble("Left Enc",
			// Motors.motorDriveLeft1.getEncPosition());
			// SmartDashboard.putDouble("right Enc",
			// Motors.motorDriveRight1.getEncPosition());
			// SmartDashboard.putDouble("average enc",
			// ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
			// + Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0));
			double distanceDelta = targetMovementDist + ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			// SmartDashboard.putNumber("distance delta", distanceDelta);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
			{
				Drivetrain.drive(0, 0);
				movePegDist = true;
				timer.reset();
				timer.stop();
			}
		}
		
		// Method to drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			System.out.println("DROP IT");
			dropGear();
			if (Motors.gearHold1.getAngle() >= Motors.lGearDropdownAngle)
			{
				
				Timer.delay(.5);
				timer.reset();
				placeGear = true;
				timer.start();
			}
		}
		
		// Back up
		if (placeGear)
		{
			if (timer.get() < 0.58)
			{
				Drivetrain.drive( -.3, .3); // TODO Backwards for phoenix is
											// neg, pos
			} else
			{
				Drivetrain.drive(0, 0);
			}
		}
	}
	
	// Without encoders
	public static void pegMiddleNoEnc()
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
				Drivetrain.drive(.5, -.5);
			else
			{
				movePegDist = true;
				timer.reset();
				timer.stop();
			}
		}
		
		System.out.println("MovePegDist: " + movePegDist + "placeGear: " + placeGear);
		
		// Method to drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			System.out.println("pizza");
			Drivetrain.drive(0, 0);
			dropGear();
			if (Motors.gearHold1.getAngle() >= Motors.lGearDropdownAngle)
			{
				Timer.delay(1);
				timer.reset();
				placeGear = true;
				timer.start();
			}
		}
		
		// Back up
		if (placeGear)
		{
			if (timer.get() < 1)
			{
				Drivetrain.drive( -.5, -.5);
			} else
			{
				Drivetrain.drive(0, 0);
			}
		}
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
		if ( !findValues)
		{
			System.out.println("Finding Values");
			
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			Sensors.navX.zeroYaw();
			
			lineUpWithPegDist = ((Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_BASE
					- (Dimensions.PEGBASE_TO_BARRIER - distFromCorner))
					* Math.tan(30.0 * Dimensions.RADIANS_CONVERSION)) - ((3 * Dimensions.ROBOT_LENGTH) / 5);
			targetLineUpDist = (lineUpWithPegDist * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			if ((keystart && red) || ( !keystart && !red))
				degreesToRotate = 117;
			else
				degreesToRotate = -117;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			// SmartDashboard.putNumber("lineUpWithPegDist", lineUpWithPegDist);
			// SmartDashboard.putNumber("Target angle (find values)",
			// targetAngle);
			// SmartDashboard.putNumber("TargetLineUp (find values)",
			// targetLineUpDist);
			findValues = true;
		}
		
		if ( !moveAlignDist)
		{
			System.out.println("Moving the alignment distance");
			
			Drivetrain.moveToDistance(targetLineUpDist);
			double distanceDelta = targetLineUpDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			System.out.println(distanceDelta);
			// SmartDashboard.putNumber("Distance delta (moveAlignDist)",
			// distanceDelta);
			// SmartDashboard.putNumber("DistanceDelta (in)", distanceDelta /
			// Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
			{
				moveAlignDist = true;
				Sensors.navX.zeroYaw();
				Drivetrain.drive(0, 0);
			}
		}
		
		if (moveAlignDist && !turnAngle)
		{
			System.out.println("Turning intially");
			
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			// SmartDashboard.putNumber("NavX Yaw (turnAngle)",
			// Sensors.navX.getYaw());
			// SmartDashboard.putNumber("Remaining degrees (turnAngle)",
			// angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
			{
				turnAngle = true;
				Sensors.navX.zeroYaw();
				
				Drivetrain.drive(0, 0);
				Timer.delay(1);
			}
		}
		
		if (turnAngle && !visionAlign)
		{
			System.out.println("Vision aligning");
			
			visionAlign = Vision.autoAlignWithGear();
		}
		
		if (visionAlign && !findPegDist)
		{
			System.out.println("Finding the peg distance");
			
			System.out.println("Vision Align" + visionAlign);
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			Motors.motorDriveRight1.reset();
			Motors.motorDriveLeft1.reset();
			Timer.delay(1);
			// SmartDashboard.putNumber("Motor Enc (findPegDist)",
			// Motors.motorDriveLeft1.getEncPosition());
			// SmartDashboard.putNumber("Motors Right (findPegDist)",
			// Motors.motorDriveRight1.getEncPosition());
			distToPeg = (lineUpWithPegDist / (Math.cos(Math.PI / 3.0))) - (Dimensions.ROBOT_LENGTH / 2)
					- (Dimensions.HOOK_LENGTH / 2);
			distToPeg = -distToPeg;
			targetMovementDist = (distToPeg * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT)
					+ (7 * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			targetMovementDist = targetMovementDist - Math.abs(Motors.motorDriveLeft1.getEncPosition());
			
			// SmartDashboard.putNumber("targetMovementDist (findPegDist)",
			// targetMovementDist);
			findPegDist = true;
		}
		
		if (findPegDist && !movePegDist)
		{
			Motors.gearHold1.set(Motors.lGearPrimeAngle);
			Motors.gearHold2.set(Motors.rGearPrimeAngle);
			Drivetrain.moveToDistance(targetMovementDist);
			double distanceDelta = targetMovementDist + /*-*/ ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			// SmartDashboard.putNumber("Distance Delta 2-21-7", distanceDelta);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
			{
				movePegDist = true;
				Drivetrain.drive(0, 0);
				timer.reset();
				timer.stop();
			}
		}
		
		// Method to automatically drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			System.out.println("Placing the gear");
			
			dropGear();
			if (Motors.gearHold1.getAngle() >= Motors.lGearDropdownAngle)
			{
				placeGear = true;
			}
		}
		
		if (placeGear && !waitToSettle)
		{
			System.out.println("Waiting...");
			
			if (startTime == -1.0)
			{
				timer.start();
				startTime = timer.get();
			}
			
			if (Math.abs(startTime - timer.get()) >= 1.5)
			{
				if ( !extraTasks)
				{
					Timer.delay(1);
					timer.reset();
					waitToSettle = true;
					timer.start();
				}
				waitToSettle = true;
			}
		}
		
		if (waitToSettle && extraTasks)
		{
			doPegSide = true;
		} else if (waitToSettle && !extraTasks)
		{
			System.out.println("Backing up!");
			
			// Back up
			if (placeGear)
			{
				if (timer.get() < 1)
				{
					Drivetrain.drive( -.5, -.5);
				} else
				{
					Drivetrain.drive(0, 0);
				}
			}
		}
	}
	
	// Without Encoders
	public static void pegSideNoEnc(double distFromCorner, boolean keystart, boolean red)
	{
		// distFromCorner is the Distance from the nearest corner of the arena
		// to the CENTER of the robot in inches.
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
			// SmartDashboard.putNumber("Target angle", targetAngle);
			
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
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnAngle = true;
		}
		
		if (turnAngle && !visionAlign)
			visionAlign = Vision.autoAlignWithGear();
		
		if (visionAlign && !findPegDist)
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
				Drivetrain.drive(.5, -.5);
			else
				movePegDist = true;
		}
		
		// Method to automatically drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			dropGear();
			if (Motors.gearHold1.getAngle() >= Motors.lGearDropdownAngle)
			{
				placeGear = true;
			}
		}
		
		if (placeGear && !waitToSettle)
		{
			System.out.println("Waiting...");
			
			if (startTime == -1.0)
			{
				timer.start();
				startTime = timer.get();
			}
			
			if (Math.abs(startTime - timer.get()) >= 1.5)
			{
				if ( !extraTasks)
				{
					Timer.delay(1);
					timer.reset();
					waitToSettle = true;
					timer.start();
				}
				waitToSettle = true;
			}
		}
		
		if (waitToSettle && extraTasks)
		{
			doPegSide = true;
		} else if (waitToSettle && !extraTasks)
		{
			// Back up
			if (placeGear)
			{
				if (timer.get() < 1)
				{
					Drivetrain.drive( -.5, -.5);
				} else
				{
					Drivetrain.drive(0, 0);
				}
			}
		}
	}
	
	// Fuel
	// With Encoders
	public static void highBoilerEasy(boolean red)
	{
		if ( !findValues)
		{
			Sensors.navX.zeroYaw();
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			double retreatDistance = 40;
			targetRetreatDistance = retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT
					+ ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
							+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if (red)
			{
				degreesToRotate = -136.25;
			} else
			{
				degreesToRotate = 136.25;
			}
			Motors.fuelStop.set(Motors.fuelCloseAngle);
			findValues = true;
		}
		
		if ( !shootHigh)
		{
			if (startTime == -1.0)
			{
				timer.start();
				startTime = timer.get();
				Fuel.autoShoot();
			}
			if (Math.abs(startTime - timer.get()) >= 13.0)
			{
				Drivetrain.drive(0, 0);
			} else if (Math.abs(startTime - timer.get()) >= 11.0)
			{
				Drivetrain.drive(Motors.AUTO_ALIGN_POWER, -Motors.AUTO_ALIGN_POWER);
			} else if (Math.abs(startTime - timer.get()) >= 9.0)
			{
				if (red)
				{
					Drivetrain.rotateCCW(Motors.AUTO_ALIGN_POWER);
				} else if ( !red)
				{
					Drivetrain.rotateCW(Motors.AUTO_ALIGN_POWER);
				}
			} else if (Math.abs(startTime - timer.get()) >= 8.0)
			{
				Drivetrain.drive(Motors.AUTO_ALIGN_POWER, -Motors.AUTO_ALIGN_POWER);
			} else if (Math.abs(startTime - timer.get()) >= 7.0)
			{
				Fuel.stopShoot();
				Motors.motorIntake.set(0);
				Motors.fuelStop.set(Motors.fuelCloseAngle);
			} else if (Math.abs(startTime - timer.get()) >= 2.5)
			{
				Motors.motorIntake.set(Motors.intakePower * .5);
			} else if (Math.abs(startTime - timer.get()) >= 1.0)
			{
				Motors.fuelStop.set(Motors.fuelOpenAngle);
			}
		}
		
		// if(shootHigh && !moveAway)
		// {
		// Drivetrain.moveToDistance(targetRetreatDistance);
		// double distanceDelta = targetRetreatDistance -
		// ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
		// + Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
		// //SmartDashboard.putNumber("L Encoder Easy",
		// Motors.motorDriveLeft1.getEncPosition());
		// //SmartDashboard.putNumber("R Encoder Easy",
		// Motors.motorDriveRight1.getEncPosition());
		// //SmartDashboard.putNumber("Distance delta", distanceDelta);
		// if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta <
		// EncoderDistanceThreshold))
		// {
		// Drivetrain.drive(0, 0);
		// moveAway = true;
		// }
		// if (Math.abs(startTime - timer.get()) >= 8.0)
		// {
		// Drivetrain.drive(Motors.AUTO_ALIGN_POWER, -Motors.AUTO_ALIGN_POWER);
		// }
		// else
		// {
		// moveAway=true;
		// }
		// }
		//
		// if(moveAway && !turnToLimit)
		// {
		// double driveDistance =
		// Dimensions.DIST_TO_BASELINE+Dimensions.FIVE_FEET;
		// targetMovementDist =
		// driveDistance*Sensors.INCHES_TO_DRIVE_ENCODER_LEFT+((Math.abs(Motors.motorDriveLeft1.getEncPosition())
		// + Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
		//
		// Drivetrain.turnToAngle(targetAngle);
		// double angleDelta = targetAngle - Sensors.navX.getYaw();
		// if (Math.abs(angleDelta) < AngleThreshold)
		// {
		// Drivetrain.drive(0, 0);
		// turnToLimit = true;
		// }
		// if (red && Math.abs(startTime - timer.get()) >= 9.0)
		// {
		// Drivetrain.rotateCCW(Motors.AUTO_ALIGN_POWER);
		// }
		// else if(!red && Math.abs(startTime - timer.get()) >= 9.0)
		// {
		// Drivetrain.rotateCW(Motors.AUTO_ALIGN_POWER);
		// }
		// else
		// {
		// turnToLimit=true;
		// }
		// }
		//
		// if(turnToLimit && !moveToLimit)
		// {
		// Drivetrain.moveToDistance(targetMovementDist);
		// double distanceDelta = targetMovementDist -
		// ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
		// + Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
		// if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta <
		// EncoderDistanceThreshold))
		// {
		// Drivetrain.drive(0, 0);
		// moveToLimit = true;
		// }
		// if (Math.abs(startTime - timer.get()) >= 11.0)
		// {
		// Drivetrain.drive(Motors.AUTO_ALIGN_POWER, -Motors.AUTO_ALIGN_POWER);
		// }
		// else
		// {
		// Drivetrain.drive(0, 0);
		// moveToLimit=true;
		// }
		// }
	}
	
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
		if ( !findBoilerValues)
		{
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			
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
			// SmartDashboard.putNumber("Target angle", targetAngle);
			
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
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnBoilerAngle = true;
		}
		
		// if (turnBoilerAngle && !boilerVisionAlign)
		// boilerVisionAlign = Vision.autoAlignWithBoiler();
		
		if (/* boilerVisionAlign */ turnBoilerAngle && !findBoilerDist)
		{
			distToBoiler = (distFromCorner / Math.cos(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- (Dimensions.ROBOT_LENGTH / 2);
			targetMovementDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
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
			// SmartDashboard.putNumber("Target angle", targetAngle);
			
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
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnBoilerAngle = true;
		}
		
		// if (turnBoilerAngle && !boilerVisionAlign)
		// boilerVisionAlign = Vision.autoAlignWithBoiler();
		
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
		
		// do pegLeft
		extraTasks = true;
		if ( !doPegSide)
			pegSide(distFromCorner, true, red);
		
		// calculate values (distances)
		if (doPegSide && !calculateValues)
		{
			double retreatDistance = 40;
			targetRetreatDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
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
					+ (distToBoilerLine * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
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
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
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
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnBoilerAngle = true;
		}
		
		// if (turnBoilerAngle && !boilerVisionAlign)
		// boilerVisionAlign = Vision.autoAlignWithBoiler();
		
		if (/* boilerVisionAlign */ turnBoilerAngle && !findBoilerDist)
		{
			distToBoiler = (boilerToAlliance / Math.sin(Dimensions.BOILER_ALIGN_ANGLE * Dimensions.RADIANS_CONVERSION))
					- Dimensions.CORNER_TO_BOILER - (Dimensions.ROBOT_LENGTH / 2);
			targetMovementDist = Motors.motorDriveLeft1.getEncPosition()
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
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
		
		// do pegLeft
		extraTasks = true;
		if ( !doPegSide)
			pegSideNoEnc(distFromCorner, true, red);
		
		// calculate values (distances)
		if (doPegSide && !calculateValues)
		{
			double retreatDistance = 40;
			
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
				Drivetrain.drive( -.5, -.5);
			else
				moveAway = true;
		}
		
		// turn towards alliance wall
		if (moveAway && !turnToAlliance)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
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
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnBoilerAngle = true;
		}
		
		// if (turnBoilerAngle && !boilerVisionAlign)
		// boilerVisionAlign = Vision.autoAlignWithBoiler();
		
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
		
		// do pegLeft
		extraTasks = true;
		if ( !doPegSide)
			pegSide(distFromCorner, true, red);
		
		if (doPegSide && !calculateValues)
		{
			double retreatDistance = Dimensions.HOOK_LENGTH * 2;
			targetRetreatDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
			double distFromBarrier = Dimensions.PEGBASE_TO_BARRIER - (retreatDistance * Math.cos(Math.PI / 6.0))
					- (Dimensions.ROBOT_LENGTH / 4);
			targetBarrierDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distFromBarrier * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
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
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
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
		
		// do pegLeft
		extraTasks = true;
		if ( !doPegSide)
			pegSideNoEnc(distFromCorner, true, red);
		
		if (doPegSide && !calculateValues)
		{
			double retreatDistance = Dimensions.HOOK_LENGTH * 2;
			
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
				Drivetrain.drive( -.5, -.5);
			else
				moveAway = true;
		}
		
		// turn towards the hopper
		if (moveAway && !turnToBarrier)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
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
		extraTasks = true;
		
		if ( !doPegSide)
		{
			pegSide(distFromCorner, false, red);
		}
		
		if (doPegSide && !calculateValues)
		{
			double retreatDistance = 40;
			targetRetreatDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
			double distToAlliance = (Dimensions.PEGBASE_ALLIANCE_WALL_TRIANGLE_HYPOTENUSE - retreatDistance)
					* Math.cos(Math.PI / 3.0);
			double distToLimit = Dimensions.ALLIANCEWALL_TO_AUTOLIMIT - distToAlliance - 12;
			// 12in is to make sure we don't cross the limit
			targetMovementDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distToLimit * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
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
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnToLimit = true;
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
	
	// TODO: Make all of the following methods implement PID methods
	// TODO: Back up in appropriate methods (pegmiddle and pegside). Implement
	// in a similar manner to the non-PID methods.
	
	// ----------------THRESHOLD METHODS----------------
	
	public static void pegMiddlePID()
	{
		if ( !findValues)
		{
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			
			double driveDistance = Dimensions.DIST_TO_DECK - (Dimensions.ROBOT_LENGTH) - (Dimensions.HOOK_LENGTH / 2.0);
			targetMovementDist = -(driveDistance * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT)
					- (21 * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			targetMovementDistRight = -(driveDistance * Sensors.INCHES_TO_DRIVE_ENCODER_RIGHT)
					- (36 * Sensors.INCHES_TO_DRIVE_ENCODER_RIGHT);
			
			findValues = true;
		}
		if ( !movePegDist) // TODO Replace with PID
		{
			Motors.gearHold1.set(Motors.rGearPrimeAngle);
			Motors.gearHold2.set(Motors.lGearPrimeAngle);
			// SmartDashboard.putDouble("Left Enc",
			// Motors.motorDriveLeft1.getEncPosition());
			// SmartDashboard.putDouble("right Enc",
			// Motors.motorDriveRight1.getEncPosition());
			// SmartDashboard.putDouble("average enc",
			// ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
			// + Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0));
			double distanceDeltaLeft = targetMovementDist + ((Math.abs(Motors.motorDriveLeft1.getEncPosition())));
			double distanceDeltaRight = targetMovementDistRight + Math.abs(Motors.motorDriveRight1.getEncPosition());
			// SmartDashboard.putNumber("distance delta left",
			// distanceDeltaLeft);
			// SmartDashboard.putNumber("distance delta right",
			// distanceDeltaRight);
			if (Drivetrain.moveToDistancePID(targetMovementDist, targetMovementDistRight))
			{
				PIDValues.rightDrive1.disable();
				PIDValues.rightDrive2.disable();
				PIDValues.leftDrive1.disable();
				PIDValues.leftDrive2.disable();
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
			if (Motors.gearHold1.getAngle() >= Motors.lGearDropdownAngle)
			{
				Timer.delay(2);
				timer.reset();
				placeGear = true;
				timer.start();
				
			}
		} // TODO Do we want to back up?
		if (placeGear)
		{
			if (timer.get() < 1)
			{
				Drivetrain.drive( -.25, -.25);
			} else
			{
				Drivetrain.drive(0, 0);
			}
		}
	}
	
	// With encoders
	public static void pegMiddlePIDDEPR()
	{
		if ( !findValues)
		{
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			double driveDistance = Dimensions.DIST_TO_DECK - (Dimensions.ROBOT_LENGTH) - (Dimensions.HOOK_LENGTH / 2.0);
			targetMovementDist = -(driveDistance * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			targetMovementDistRight = -(driveDistance * Sensors.INCHES_TO_DRIVE_ENCODER_RIGHT);
			
			findValues = true;
		}
		if ( !movePegDist) // TODO Replace with PID
		{
			Drivetrain.moveToDistancePID(targetMovementDist, targetMovementDistRight);
			double distanceDeltaLeft = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())));
			double distanceDeltaRight = targetMovementDistRight
					- ((Math.abs(Motors.motorDriveRight1.getEncPosition())));
			System.out.println("distance delta" + distanceDeltaLeft);
			if (((distanceDeltaLeft > -EncoderDistanceThreshold) && (distanceDeltaLeft < EncoderDistanceThreshold))
					&& ((distanceDeltaRight > -EncoderDistanceThreshold)
							&& distanceDeltaRight < EncoderDistanceThreshold))
			{
				System.out.println("pizzs");
				// Drivetrain.drive(0, 0);
				Drivetrain.moveToDistancePID(distanceDeltaLeft, distanceDeltaRight);
				movePegDist = true;
			}
		}
		
		// Method to drop gear on peg when called
		if (movePegDist && !placeGear)
		{
			dropGear();
			placeGear = true;
			if (Motors.gearHold1.getAngle() >= Motors.lGearDropdownAngle)
			{
				Timer.delay(1);
				timer.reset();
				placeGear = true;
				timer.start();
				
			}
		} // TODO Do we want to back up?
		if (placeGear)
		{
			Drivetrain.moveToDistancePID( -Sensors.INCHES_TO_DRIVE_ENCODER_LEFT * 12,
					-Sensors.INCHES_TO_DRIVE_ENCODER_RIGHT * 12);
		}
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
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			
			lineUpWithPegDist = ((Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_BASE
					- (Dimensions.PEGBASE_TO_BARRIER - distFromCorner))
					* Math.tan(30.0 * Dimensions.RADIANS_CONVERSION)) - (Dimensions.ROBOT_LENGTH / 2);
			targetLineUpDistLeft = (Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ (lineUpWithPegDist * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT));
			targetLineUpDistRight = Math.abs(Motors.motorDriveRight1.getEncPosition())
					+ (lineUpWithPegDist * Sensors.INCHES_TO_DRIVE_ENCODER_RIGHT);
			if ((keystart && red) || ( !keystart && !red))
				degreesToRotate = 110;
			else
				degreesToRotate = -110;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			// SmartDashboard.putNumber("Target angle", targetAngle);
			
			findValues = true;
		}
		
		if ( !moveAlignDist)
		{
			Drivetrain.moveToDistancePID(targetLineUpDistLeft, targetLineUpDistRight);
			double distanceDelta = targetLineUpDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveAlignDist = true;
		}
		
		if (moveAlignDist && !turnAngle)
		{
			if (degreesToRotate > 0)
			{
				Drivetrain.rotateRight(targetAngle);
			} else
			{
				Drivetrain.rotateRight(targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
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
			targetMovementDist = (Math.abs(Motors.motorDriveLeft1.getEncPosition()))
					- (distToPeg * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			targetMovementDistRight = Math.abs(Motors.motorDriveRight1.getEncPosition())
					- (distToPeg * Sensors.INCHES_TO_DRIVE_ENCODER_RIGHT);
			findPegDist = true;
		}
		
		if (findPegDist && !movePegDist)
		{
			Drivetrain.moveToDistancePID(targetMovementDist, targetMovementDistRight);
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
			Motors.motorDriveLeft1.setEncPosition(0);
			Motors.motorDriveRight1.setEncPosition(0);
			
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
			targetLineUpDistLeft = ((Math.abs(Motors.motorDriveLeft1.getEncPosition() + lineUpWithBoilerDist)));
			targetLineUpDistRight = ((Math.abs(Motors.motorDriveRight1.getEncPosition() + lineUpWithBoilerDist)));
			
			if (red)
				degreesToRotate = 136.25;
			else
				degreesToRotate = -136.25;
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			// SmartDashboard.putNumber("Target angle", targetAngle);
			
			findBoilerValues = true;
		}
		
		// move off of the wall until aligned with boiler
		if ( !moveBoilerAlignDist)
		{
			Drivetrain.moveToDistancePID(targetLineUpDistLeft, targetLineUpDistRight);
			double distanceDeltaLeft = targetLineUpDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())));
			double distanceDeltaRight = targetLineUpDist - (Math.abs(Motors.motorDriveRight1.getEncPosition()));
			if ((distanceDeltaLeft > -EncoderDistanceThreshold) && (distanceDeltaLeft < EncoderDistanceThreshold)
					&& ((distanceDeltaRight > -EncoderDistanceThreshold)
							&& (distanceDeltaRight < EncoderDistanceThreshold)))
				moveBoilerAlignDist = true;
		}
		
		// turn to boiler
		if (moveBoilerAlignDist && !turnBoilerAngle)
		{
			if (degreesToRotate > 0)
			{
				Drivetrain.rotateRight(targetAngle);
			} else
			{
				Drivetrain.rotateRight(targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
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
			targetMovementDistLeft = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT)));
			
			targetMovementDistRight = ((Math.abs(Motors.motorDriveRight1.getEncPosition()
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER_RIGHT))));
			findBoilerDist = true;
		}
		
		// move to the boiler (up against the wall)
		if (findBoilerDist && !moveBoilerDist)
		{
			Drivetrain.moveToDistancePID(targetMovementDistLeft, targetMovementDistRight);
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
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
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
					+ (distToBoilerLine * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
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
			if (degreesToRotate > 0)
			{
				Drivetrain.rotateRight(targetAngle);
			} else
			{
				Drivetrain.rotateLeft( -targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
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
			if (degreesToRotate > 0)
			{
				Drivetrain.rotateRight(targetAngle);
			} else
			{
				Drivetrain.rotateLeft( -targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
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
					+ (distToBoiler * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
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
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
			double distFromBarrier = Dimensions.PEGBASE_TO_BARRIER - (retreatDistance * Math.cos(Math.PI / 6.0))
					- (Dimensions.ROBOT_LENGTH / 4);
			targetBarrierDistance = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distFromBarrier * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
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
			if (degreesToRotate > 0)
			{
				Drivetrain.rotateRight(targetAngle);
			} else
			{
				Drivetrain.rotateLeft( -targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
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
					+ (retreatDistance * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
			double distToAlliance = (Dimensions.PEGBASE_ALLIANCE_WALL_TRIANGLE_HYPOTENUSE - retreatDistance)
					* Math.cos(Math.PI / 3.0);
			double distToLimit = Dimensions.ALLIANCEWALL_TO_AUTOLIMIT - distToAlliance - 12;
			// 12in is to make sure we don't cross the limit
			targetMovementDist = ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0)
					+ (distToLimit * Sensors.INCHES_TO_DRIVE_ENCODER_LEFT);
			
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
			if (degreesToRotate > 0)
			{
				Drivetrain.rotateRight(targetAngle);
			} else
			{
				Drivetrain.rotateLeft( -targetAngle);
			}
			double angleDelta = targetAngle - Sensors.navX.getYaw();
			// SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if (Math.abs(angleDelta) < AngleThreshold)
				turnToLimit = true;
		}
		
		if (turnToLimit && !moveToLimit)
		{
			Drivetrain.moveToDistancePID(targetMovementDist);
			double distanceDelta = targetMovementDist - ((Math.abs(Motors.motorDriveLeft1.getEncPosition())
					+ Math.abs(Motors.motorDriveRight1.getEncPosition())) / 2.0);
			if ((distanceDelta > -EncoderDistanceThreshold) && (distanceDelta < EncoderDistanceThreshold))
				moveToLimit = true;
		}
	}
	
	public static void pegAndPrepPID(boolean red)
	{
		pegAndPrepPID(Dimensions.RETRIEVAL_ALLIANCE_INTERSECT - (Dimensions.ROBOT_WIDTH / 2), red);
	}
	
}