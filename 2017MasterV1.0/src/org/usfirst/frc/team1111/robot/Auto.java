package org.usfirst.frc.team1111.robot;
import java.lang.Math;

import variables.Dimensions;

public class Auto 
{
	//----------------THRESHOLD METHODS----------------
	
	
	//----------------OBJECTIVE METHODS----------------
	
	//Gears
	public static void pegStraightAhead()
	{
		double driveDistance; //In inches
		driveDistance=Dimensions.DIST_TO_DECK-(Dimensions.ROBOT_LENGTH)-(Dimensions.HOOK_LENGTH/2);
		//Move that distance
	}
	
	/*
	public static void pegLeft() //Assumes that the bottom right corner of the robot is at the intersection of the alliance wall and the key zone line
	{
		Unimplementable w/o more measurements.
	}
	*/
	
	public static void pegLeft(int distFromCorner)
	{
		//distFromCorner is the Distance from the nearest corner of the arena to the CENTER of the robot.
		double lineUpWithPegDist; //In inches
		lineUpWithPegDist=((Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_BASE-(Dimensions.PEGBASE_TO_BARRIER-distFromCorner))*Math.tan(30.0))-(Dimensions.ROBOT_LENGTH/2);
		//Move that distance
		//Turn 60 degrees CW
		double distToPeg;
		//Go forward a certain distance that I haven't calculated yet
	}
	
	/*
	public static void pegRight() //Assumes that the bottom right corner of the robot is at the intersection of the alliance wall and the retrieval zone line
	{
		Unimplementable w/o more measurements.
	}
	*/
	
	public static void pegRight(int distFromCorner)
	{
		//distFromCorner is the Distance from the nearest corner of the arena to the CENTER of the robot.
		double lineUpWithPegDist; //In inches
		lineUpWithPegDist=((Dimensions.PEGBASE_ALLIANCEWALL_TRIANGLE_BASE-(Dimensions.PEGBASE_TO_BARRIER-distFromCorner))*Math.tan(30.0))-(Dimensions.ROBOT_LENGTH/2);
		//Move that distance
		//Turn 60 degrees CCW
		double distToPeg;
		//Go forward a certain distance that I haven't calculated yet
	}
	
	//Fuel
	public static void lowBoiler()
	{
		
	}
	
	//----------------REACH METHODS----------------


}
