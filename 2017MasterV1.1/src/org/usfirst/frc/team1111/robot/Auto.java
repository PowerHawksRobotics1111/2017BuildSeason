package org.usfirst.frc.team1111.robot;
import java.lang.Math;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.Drivetrain;
import variables.Dimensions;
import variables.Motors;
import variables.Sensors;
import com.kauailabs.navx.frc.AHRS;

public class Auto 
{
	static boolean retrievedImage = false;
	static double[] TargetX;
	static double[] TargetY;
	static double degreesToRotate;
	static double targetAngle = 0;
	static double lineUpWithPegDist; //In inches
	static double targetLineUpDist = 0;
	static double distToPeg; //In inches
	static double targetPegDist = 0;
	
	
	//----------------THRESHOLD METHODS----------------
	
	//With encoders
	public static void pegMiddle()
	{
		double driveDistance; //In inches
		driveDistance=Dimensions.DIST_TO_DECK-(Dimensions.ROBOT_LENGTH)-(Dimensions.HOOK_LENGTH/2);
		//Move that distance with encoders
	}
	
	//Without encoders
	
	
	
	//----------------OBJECTIVE METHODS----------------
	
	//Gears
	public static void pegLeft(double distFromCorner)
	{
		//distFromCorner is the Distance from the nearest corner of the arena to the CENTER of the robot.
		if(!Robot.findValues)
		{
			lineUpWithPegDist=((Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_BASE-(Dimensions.PEGBASE_TO_BARRIER-distFromCorner))*Math.tan(30.0))-(Dimensions.ROBOT_LENGTH/2);
			targetLineUpDist=Motors.motorDriveBackLeft.getDistance()+lineUpWithPegDist;
			
			degreesToRotate = 60;
			targetAngle = Sensors.navX.getYaw()+degreesToRotate;
			SmartDashboard.putNumber("Target angle", targetAngle);

			Robot.findValues=true;
		}
		
		
		if(Robot.findValues && !Robot.moveAlignDist)
		{
			Drivetrain.moveToDistance(targetLineUpDist);
			double distanceDelta = targetLineUpDist-Motors.motorDriveBackLeft.getDistance();
			if(distanceDelta<0)
			{
				Robot.movePegDist=true;
			}
		}
		
		//Drivetrain.turnCWNumDegrees(60); Utilizes illegal while loop
		
		if(Robot.findValues && Robot.moveAlignDist && !Robot.turnAngle)
		{
			Drivetrain.turnToAngle(targetAngle);
			double angleDelta = targetAngle-Sensors.navX.getYaw();
			SmartDashboard.putNumber("Remaining degrees", angleDelta);
			if(Math.abs(angleDelta)<5)
			{
				Robot.turnAngle=true;
			}
		}
		
		if(Robot.findValues && Robot.moveAlignDist && Robot.turnAngle && !Robot.visionAlign)
		{
			//Align with vision stuff
			Robot.visionAlign=true;
		}
		
		if(Robot.findValues && Robot.moveAlignDist && Robot.turnAngle && Robot.visionAlign && !Robot.findPegDist)
		{
			distToPeg=(lineUpWithPegDist/(Math.cos(Math.PI/3)))-(Dimensions.ROBOT_LENGTH/2)-(Dimensions.HOOK_LENGTH/2);
			targetPegDist=Motors.motorDriveBackLeft.getDistance()+distToPeg;
			Robot.findPegDist=true;
		}
		
		if(Robot.findValues && Robot.moveAlignDist && Robot.turnAngle && Robot.visionAlign && Robot.findPegDist && !Robot.movePegDist)
		{
			Drivetrain.moveToDistance(targetPegDist);
			double distanceDelta = targetPegDist-Motors.motorDriveBackLeft.getDistance();
			if(distanceDelta<0)
			{
				Robot.movePegDist=true;
			}
		}
	}
	
	public static void pegRight(int distFromCorner) //FIX BASED ON PEGLEFT
	{
		//distFromCorner is the Distance from the nearest corner of the arena to the CENTER of the robot.
		double lineUpWithPegDist; //In inches
		lineUpWithPegDist=((Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_BASE-(Dimensions.PEGBASE_TO_BARRIER-distFromCorner))*Math.tan(Math.PI/6))-(Dimensions.ROBOT_LENGTH/2);
		//Move that distance with encoders
		
		//Drivetrain.turnCCWNumDegrees(60); Utilizes illegal while loop
		
		
		double distToPeg;
		distToPeg=(lineUpWithPegDist/(Math.cos(Math.PI/3)))-(Dimensions.ROBOT_LENGTH/2)-(Dimensions.HOOK_LENGTH/2);
		//Move that distance
	}
	
	//Fuel
	public static void lowBoiler()
	{
		
	}
	
	public static void reach()
	{
		
	}
	
	
	//----------------REACH METHODS----------------


}
