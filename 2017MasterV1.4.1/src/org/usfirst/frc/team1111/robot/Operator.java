package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.Vision;
import variables.Joysticks;
import variables.Motors;

public class Operator
{
	
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
			// shoot();// press and hold
			hang();// toggle
			placeGear();// Press to release gear
		}
		// Driver method
		// autoAlignPeg();// Press and hold
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
			// If we want, we can give them a separate reset so they can pause
			// align without cancel.
		}
	}
	
	static void hang()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.hang))
			hanging = true;
		if (Joysticks.joyOp.getRawButton(Joysticks.X))
			Motors.motorHang.set( -.1);
		else if ( !hanging)
			Motors.motorHang.set(0);
		else if (hanging)
		{
			Motors.motorHang.set(Motors.hangPower);
			
			if (Motors.motorHang.getOutputCurrent() >= Motors.hangStopCurrent)
				hanging = false;
		} else
		{
			System.out.println(Motors.motorHang.getOutputCurrent());
			SmartDashboard.putDouble("Climbing Amperate", Motors.motorHang.getOutputCurrent());
			Motors.motorHang.set(0.0);
		}
	}
	
	static void shoot()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.shootButton))
		{
			Motors.fuelStop.setAngle(Motors.fuelOpenAngle);
			Motors.motorAgitator.set(Motors.agitationPower);
			Motors.motorShooter.set(Motors.shooterVoltage);
		} else
		{
			Motors.fuelStop.setAngle(Motors.fuelCloseAngle);
			Motors.motorAgitator.set(0.0);
			Motors.motorShooter.set(0.0);
		}
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
			Motors.gearHold1.setAngle(Motors.lGearDropdownAngle);
			Motors.gearHold2.setAngle(Motors.rGearDropdownAngle);
		} else
		{
			Motors.gearHold1.setAngle(Motors.lGearStopdownAngle);
			Motors.gearHold2.setAngle(Motors.rGearStopdownAngle);
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
