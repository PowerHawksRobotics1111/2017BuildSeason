package org.usfirst.frc.team1111.robot;

import subsystems.Vision;
import variables.Joysticks;
import variables.Motors;

public class Operator {
	
	static boolean hanging = false;
	
	/**
	 * This is the master method, it is called periodically to check inputs and
	 * apply commands to the robot accordingly
	 */
	public static void operate()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.override1)
				|| Joysticks.joyOp.getRawButton(Joysticks.Buttons.override2))
			override();// Maybe don't need this as a separate function.
		else
		{
			intake();// Press and hold
			shoot();// press and hold
			hang();// toggle
			placeGear();// Press to release gear
		}
		
		autoAlignPeg();
	}
	
	static boolean aligned = false;
	
	static void autoAlignPeg()
	{
		if (Joysticks.joyDrive.getRawButton(Joysticks.Buttons.driverPegAlign))
		{
			Robot.overrideDriverJoysticks = true;
			// Only do alignment while in this loop
			if ( !aligned)
				aligned = Vision.autoAlignWithGear();
			// TODO distance
		} else
		{
			Robot.overrideDriverJoysticks = false;
			aligned = false;
		}
	}
	
	static void hang()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.hang))
			hanging = true;
		
		if (hanging)
		{
			Motors.motorHang.set(Motors.hangPower);
			
			if (Motors.motorHang.getOutputCurrent() >= Motors.hangStopCurrent)
				hanging = false;
		} else
			Motors.motorHang.set(0.0);
	}
	
	static void shoot()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.shootButton))
			Motors.motorShooter.set(Motors.shooterVoltage);
		else
			Motors.motorShooter.set(0.0);
	}
	
	static void intake()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.intakeButton))
			Motors.motorIntake.set(Motors.intakePower);
		else if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.outtakeButton))
			Motors.motorIntake.set(Motors.outtakePower);
		else
			Motors.motorIntake.set(0.0);
		
	}
	
	static void placeGear()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.gearRelease))
		{
			Motors.gearHold1.setAngle(0);
			Motors.gearHold2.setAngle(180);
		} else
		{
			Motors.gearHold1.setAngle(Motors.gearStopdownAngle);
			Motors.gearHold2.setAngle(180 - Motors.gearStopdownAngle);
		}
	}
	
	/**
	 * Check each function, and if it is being pressed, disable it, stop motors,
	 * reset to standard, finished state.
	 */
	static void override()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.hang))
		{
			hanging = false;
			Motors.motorHang.set(0.0);
		}
	}
}
