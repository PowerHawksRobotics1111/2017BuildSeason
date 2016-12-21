package org.usfirst.frc.team1111.robot;

import java.lang.Math;

import org.usfirst.frc.team1111.robot.Auto.Movement;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import variables.Motors;
import variables.Sensors;
import variables.Vision;
//import variables.Sensors;
//import variables.Sensors.Encoders;
public class Auto 
{
	static final double deltaH = 72.5;
	static final double shootingDistance = 47.53;
	static boolean retrievedImage = false;
	static double targetAngle = 0;
	static double[] TargetX;
	static double[] TargetY;
	static double degreesToRotate;
	static double degreesToRotateDistance;
	static double distanceToMoveToTarget;
	public static double findThetaX(double currentCenterX)
	{
		double theta = 0.0;
		theta = Math.atan((Vision.centerXVision - currentCenterX) / Vision.focalLength)*(180/Math.PI);
		retrievedImage = true;
		return theta*-1;
	}
	
	public static double findThetaForDist(double currentCenterY)
	{
		double theta = 0.0;
		theta = (currentCenterY-Vision.centerYVision)/Vision.focalLength;
		return theta;
	}
	
	public static double findDistToMove(double theta)
	{
		double x = 0.0;
		x = (deltaH/Math.tan(theta)) - shootingDistance;
		return x;
	}
	
	public static void turnNumDegrees(double angleDifference)
	{
		if(Math.abs(angleDifference)>5)
		{
			//The motors rotate CCW (viewed from the back) when set to a positive value
			
			//If the target is right of the center of the camera view,
			if(angleDifference>5)
			{
				//Rotate right
				Motors.motorDriveFrontRight.set(.2);
				Motors.motorDriveBackRight.set(.2);
				Motors.motorDriveFrontLeft.set(.2);
				Motors.motorDriveBackLeft.set(.2);
			}
			else if(angleDifference<-5)
			{
				//Rotate left
				Motors.motorDriveFrontLeft.set(-.2);
				Motors.motorDriveBackLeft.set(-.2);
				Motors.motorDriveFrontRight.set(-.2);
				Motors.motorDriveBackRight.set(-.2);
			}
		}
		else
		{
			//Stop when at the target
			Motors.motorDriveFrontLeft.set(0);
			Motors.motorDriveBackLeft.set(0);
			Motors.motorDriveFrontRight.set(0);
			Motors.motorDriveBackRight.set(0);
		}
	}
	public static void turnToTarget(double targetAngle)
	{
		double angleDelta = targetAngle-Sensors.navX.getYaw();
		if(Math.abs(angleDelta)>2)
		{
			//The motors rotate CCW (viewed from the back) when set to a positive value
			
			//If the target is right of the center of the camera view,
			if(angleDelta>2)
			{
				//Rotate right
				Motors.motorDriveFrontRight.set(-.25);
				Motors.motorDriveBackRight.set(-.25);
				Motors.motorDriveFrontLeft.set(-.25);
				Motors.motorDriveBackLeft.set(-.25);
			}
			else if(angleDelta<-2)
			{
				//Rotate left
				Motors.motorDriveFrontLeft.set(.25);
				Motors.motorDriveBackLeft.set(.25);
				Motors.motorDriveFrontRight.set(.25);
				Motors.motorDriveBackRight.set(.25);
			}
		}
		else
		{
			//Stop when at the target
			Motors.motorDriveFrontLeft.set(0);
			Motors.motorDriveBackLeft.set(0);
			Motors.motorDriveFrontRight.set(0);
			Motors.motorDriveBackRight.set(0);
		}
	}
	
	public static void autoAlign()
	{
		NetworkTable VisionTable = NetworkTable.getTable("GRIP/contoursReport");
		//TargetX will be a null array if no target is visible.
		//Trying to execute findTheta with a null array will crash the program.
		if(!retrievedImage)
		{
			try
			{
				TargetX = VisionTable.getNumberArray("centerX");
				TargetY = VisionTable.getNumberArray("centerY");
				SmartDashboard.putNumber("X", TargetX[0]);
				SmartDashboard.putNumber("Y", TargetY[0]);
			}
			catch(Exception E)
			{
				SmartDashboard.putString("Vision status", "Could not retrieve image!");
				return;
			}
			degreesToRotate = findThetaX(TargetX[0]);
			targetAngle = Sensors.navX.getYaw()+degreesToRotate;
			SmartDashboard.putNumber("Degrees to rotate", degreesToRotate);
			SmartDashboard.putNumber("Target angle", targetAngle);
			//Turn towards the target using the center values
			//this is just for the distance finding method to work
			//double[] Height = VisionTable.getNumberArray("height"); 							there is no height key
			//double distanceToTower = 0.0;
			//distanceToTower = findDistance(Height[0]);
			//SmartDashboard.putNumber("Distance to the target", distanceToTower);
			
			//Find and move to the correct distance away from the target
			double[] CurrentCenterYVision = VisionTable.getNumberArray("centerY");
			degreesToRotateDistance = findThetaForDist(CurrentCenterYVision[0]);
			double distanceToMove = findDistToMove(degreesToRotateDistance);
			driveToDistance(distanceToMove);
			
		}
		else
		{
			SmartDashboard.putNumber("Remaining degrees", targetAngle-Sensors.navX.getYaw());
			turnToTarget(targetAngle);
		}
	}
	public static void driveToDistance(double distance)
	{
		if(!retrievedImage)
		{
			
		}
	}
	
	static class Movement {
		public static void driveDriveMotors(double power)
		{
			Motors.motorDriveBackLeft.set(-power);
			Motors.motorDriveBackRight.set(power);
			Motors.motorDriveFrontLeft.set(-power);
			Motors.motorDriveFrontRight.set(power);
		}

		public static void stopDriveMotors()
		{
			Motors.motorDriveBackLeft.set(Motors.NO_POWER);
			Motors.motorDriveBackRight.set(Motors.NO_POWER);
			Motors.motorDriveFrontLeft.set(Motors.NO_POWER);
			Motors.motorDriveFrontRight.set(Motors.NO_POWER);
		}
		/**
		public static void driveToDistance(double distance)
		{
			if (Sensors.Encoders.resetEncoders)
			{
				Encoders.resetEncoders();
				Encoders.resetEncoders = false;
			}

			if (Encoders.encoderDriveLeft.getDistance() < distance)
				driveDriveMotors(TEMP_DEFAULT_AUTO_SPEED);
			else
				stopDriveMotors();
		}

		public static void orient(double targetAngle)
		{
			if (Sensors.navX.getYaw() > targetAngle + 5.0)
				turnInPlace("left", TEMP_DEFAULT_AUTO_SPEED);
			else if (Sensors.navX.getYaw() < targetAngle - 5.0)
				turnInPlace("right", TEMP_DEFAULT_AUTO_SPEED);
			else
				stopDriveMotors();
		}

		public static void turnInPlace(String direction, double speed)
		{
			if (direction.equals("left"))
			{
				//Robot.subState = "Turning Left...";

				Motors.motorDriveFrontLeft.set(speed);
				Motors.motorDriveBackLeft.set(speed);
				Motors.motorDriveFrontRight.set(-speed);
				Motors.motorDriveBackRight.set(-speed);
			} else if (direction.equals("right"))
			{
				//Robot.subState = "Turning Right...";

				Motors.motorDriveFrontRight.set(speed);
				Motors.motorDriveBackRight.set(speed);
				Motors.motorDriveFrontLeft.set(-speed);
				Motors.motorDriveBackLeft.set(-speed);
			}
		}
		 */

	}
	/**
	private static final double ANGLE_TO_GOAL = 0;
	private static final double ANGLE_TO_SHOOTING_SPOT = 0;
	private static final double DISTANCE_ACROSS_LOW_BAR = 0;
	private static final double DISTANCE_TO_SHOOTING_SPOT = 0;

	static int progress = 0;
	public static void lowBarShoot()//Includes shooting
	{

		if(progress == 0)
			if(Sensors.Encoders.encoderDriveLeft.get() < DISTANCE_ACROSS_LOW_BAR)
				Movement.driveToDistance(DISTANCE_ACROSS_LOW_BAR);
			else
				progress++;
		else if(progress == 1)
		{
			Sensors.navX.reset();
			progress++;
		}else if(progress == 2)
			if(Sensors.navX.getYaw() < ANGLE_TO_SHOOTING_SPOT - 5 || Sensors.navX.getYaw() > ANGLE_TO_SHOOTING_SPOT + 5)
				Movement.orient(ANGLE_TO_SHOOTING_SPOT);
			else
				progress++;
		else if(progress == 3)
		{
			Sensors.Encoders.resetEncoders();
			progress++;
		}else if(progress == 4)
			if(Sensors.Encoders.encoderDriveLeft.get() < DISTANCE_TO_SHOOTING_SPOT)
				Movement.driveToDistance(DISTANCE_TO_SHOOTING_SPOT);
			else
				progress++;
		else if(progress == 5)
		{
			Sensors.navX.reset();
			progress++;
		}else if(progress == 6)
			if(Sensors.navX.getYaw() < ANGLE_TO_GOAL - 5.0 || Sensors.navX.getYaw() > ANGLE_TO_GOAL + 5.0)
				Movement.orient(ANGLE_TO_GOAL);
			else
				progress++;
		else if(progress == 7)
			shoot();
	}
	 */

	static boolean timerStarted = false;
	static boolean autoDone = false;
	
	public static void lowBar()
	{
		if(Timer.getMatchTime() >= 15.0 - 3.5)
		{
			Movement.stopDriveMotors();
			Motors.motorArm.set(Motors.ARM_DOWN_POWER * .5);
//		}else if(Timer.getMatchTime() >= 15.0 - 0.0)
//		{
//			Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1.0 * .25); 
//			Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * .25);
//			Motors.motorDriveFrontLeft.set(-1.0 * .25);
//			Motors.motorDriveFrontRight.set(.25);
//
//			Motors.motorArm.set(Motors.NO_POWER);
		}
		else 
		{
			Movement.stopDriveMotors();
			Motors.motorArm.set(Motors.NO_POWER);
		}
	}
	
	public static void reach()
	{
		if(Timer.getMatchTime() >= 15.0 - 4.0)
		{
			Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1.0 * .25);
			Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * .25);
			Motors.motorDriveFrontLeft.set(-1.0 * .25);
			Motors.motorDriveFrontRight.set(.25);
		}
		else 
			Movement.stopDriveMotors();
	}
	
	public static void reachThenDropArm()
	{
		if(Timer.getMatchTime() >= 15.0 - 4.0)
		{
			Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1.0 * .25);
			Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO  *.25);
			Motors.motorDriveFrontLeft.set(-1.0 * .25);
			Motors.motorDriveFrontRight.set(.25);
		}else if(Timer.getMatchTime() >= 15.0 - 6.0)
		{
			Movement.stopDriveMotors();
			Motors.motorArm.enableBrakeMode(false);
			Motors.motorArm.set(Motors.ARM_DOWN_POWER * .5);
		}
		else 
		{
			Movement.stopDriveMotors();
			Motors.motorArm.set(Motors.NO_POWER);
		}
	}
	
	/**
	public static void dropArmThenReach()
	{
		if(Timer.getMatchTime() >= 15.0 - 2.0)
		{
			Movement.stopDriveMotors();
			Motors.motorArm.enableBrakeMode(false);
			Motors.motorArm.set(Motors.ARM_DOWN_POWER * .5);
		}else if(Timer.getMatchTime() >= 15.0 - 2.0)
		{
			Motors.motorDriveBackLeft.set(-1.0 * .25);
			Motors.motorDriveBackRight.set(1 *.25);
			Motors.motorDriveFrontLeft.set(-1.0 * .25);
			Motors.motorDriveFrontRight.set(1 *.25);

			Motors.motorArm.set(Motors.NO_POWER);
		}
		else 
		{
			Movement.stopDriveMotors();
			Motors.motorArm.set(Motors.NO_POWER);
		}
	}*/

	public static void moat()
	{
		if(Timer.getMatchTime() >= 15.0 -2.25)//TODO Better timings.
		{
			Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1);
			Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO );
			Motors.motorDriveFrontLeft.set(-1);
			Motors.motorDriveFrontRight.set(1.0);
		}
		else 
			Movement.stopDriveMotors();
	}

	public static void ramparts()//TODO Better timings. Auto counts down from 15 when we run it at school, so it ran at the end of auto.
	{
			if(Timer.getMatchTime() >= 15.0 - 3.0){
				Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1);
				Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO );
				Motors.motorDriveFrontLeft.set(-1);
				Motors.motorDriveFrontRight.set(1.0);
			}
			else 
			
				Movement.stopDriveMotors();
	}

	public static void roughTerrainRockwall()
	{
			if(Timer.getMatchTime() >= 15.0 - 1.75)
			{
				Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1);
				Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * 1);
				Motors.motorDriveFrontLeft.set(-1);
				Motors.motorDriveFrontRight.set(1);
			}
			else 
				Movement.stopDriveMotors();
	}
	
	static double startTime = 0.0;
	public static void rockwallKeepGoing()
	{
		if(Timer.getMatchTime() >= 15.0 - 1.75)
		{
			Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1);
			Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * 1);
			Motors.motorDriveFrontLeft.set(-1);
			Motors.motorDriveFrontRight.set(1);
		}
		else if(startTime == 0.0)
		{
			Movement.stopDriveMotors();
			startTime = 1000.0;
		}else if(startTime == 1000.0)
			startTime = Timer.getMatchTime();
		else if(Timer.getMatchTime() >= startTime - 4.0)
		{
			Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -.25);
			Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * .25);
			Motors.motorDriveFrontLeft.set(-.25);
			Motors.motorDriveFrontRight.set(.25);
		}else
			Movement.stopDriveMotors();
	}
	
	public static void crossTurnAroundTEST()
	{
		if(Timer.getMatchTime() >= 15.0 - 1.75)
		{
			Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1);
			Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * 1);
			Motors.motorDriveFrontLeft.set(-1);
			Motors.motorDriveFrontRight.set(1);
		}
		else if(startTime == 0.0)
		{
			Movement.stopDriveMotors();
			startTime = 1500.0;
		}else if(startTime == 1500.0)
		{
			if(Sensors.navX.getYaw() > 182.5 && Sensors.navX.getYaw() < 177.5)
			{
				if(Sensors.navX.getYaw() <= 180.0)
				{
					Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * .25);
					Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * .25);
					Motors.motorDriveFrontLeft.set(.25);
					Motors.motorDriveFrontRight.set(.25);
				}else if(Sensors.navX.getYaw() > 180.0)
				{
					Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1.0 * .25);
					Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1.0 * .25);
					Motors.motorDriveFrontLeft.set(-1.0 * .25);
					Motors.motorDriveFrontRight.set(-1.0 * .25);
				}
			}else
			{
				Movement.stopDriveMotors();
				startTime = 1000.0;
			}
		}else if(startTime == 1000.0)
			startTime = Timer.getMatchTime();
		else if(Timer.getMatchTime() >= startTime - 4.0)//4 sec on .25 to test align. 1.65 sec on 1.0 for run.
		{
			Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -.25);
			Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * .25);
			Motors.motorDriveFrontLeft.set(-.25);
			Motors.motorDriveFrontRight.set(.25);
		}else
			Movement.stopDriveMotors();
	}
	
	/**
	static double spyBoxShootStart = 0;
	
	public static void spyBoxShoot()
	{
		Motors.hardBallStop.setAngle(45.0);
		
		if(Sensors.getUltraAverage() > Dimensions.SPY_BOX_SHOOT_DIST)
		{
			Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * 1 *.25);
			Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1 * .25);
			Motors.motorDriveFrontLeft.set(1 * .25);
			Motors.motorDriveFrontRight.set(-1 *.25);
		}
		else if(Sensors.getUltraAverage() <= Dimensions.SPY_BOX_SHOOT_DIST)
		{
			if(spyBoxShootStart == 0.0)
			{
				spyBoxShootStart = Timer.getMatchTime();
			}
			
			Movement.stopDriveMotors();
			if(Timer.getMatchTime() >= spyBoxShootStart - 2.0)
			{
				Motors.motorArm.enableBrakeMode(false);
				Motors.motorArm.set(Motors.ARM_DOWN_POWER * .5);
			}else
			{
				Motors.motorArm.set(Motors.NO_POWER);
				Motors.motorArm.enableBrakeMode(true);
			}
			
			Motors.motorShooter.set(Motors.SHOOTER_POWER * Motors.SHOOTER_OPTIMAL_MAXIMUM_VOLTAGE);
			
			Motors.hardBallStop.setAngle(0.0);
			
			if(Timer.getMatchTime() <= (spyBoxShootStart - 3.0))
				Motors.motorInnerIntake.set(Motors.INNER_INTAKE_POWER);
			
			if(Timer.getMatchTime() >= spyBoxShootStart- 5.0)
			{
				Motors.motorShooter.set(0.0);
				Motors.motorInnerIntake.set(0.0);
			}
			
		}
		else
		{
			Movement.stopDriveMotors();
			Motors.motorShooter.set(0.0);
			Motors.motorInnerIntake.set(0.0);
		}
	}*/
	
	/**
	static Double startTime = 0.0;

	private static void shoot()//ODO bring the shooting setup in from Operator???
	{
				if(startTime == 0.0)
						startTime = Timer.getMatchTime();
				if(Timer.getMatchTime() - startTime < 3.5)//TODO better spinup time
					Motors.motorShooter.set(Motors.SHOOTER_POWER);
				else
				{
					Motors.motorInnerIntake.set(Motors.INNER_INTAKE_POWER);
					progress++;
		//		}
	}
	 */
	
	
}
