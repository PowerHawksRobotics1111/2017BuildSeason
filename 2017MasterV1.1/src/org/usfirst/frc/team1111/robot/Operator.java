package org.usfirst.frc.team1111.robot;

import variables.Joysticks;
import variables.Motors;

public class Operator {
	
	static boolean intaking = false;
	static boolean shooting = false;
	
	/**
	 * This is the master method, it is called periodically to check inputs and
	 * apply commands to the robot accordingly
	 */
	public static void operate()
	{
		intake();
		shoot();
		hang();
		
		override();
	}
	
	private static void hang()
	{
		// TODO Auto-generated method stub
		
	}
	
	private static void shoot()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.shoot))
			shooting = true;
		if (shooting)
			Motors.motorShooter.set(Motors.Powers.shooter);
		else
			Motors.motorShooter.set(0.0);
		// If they want press and hold:
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.shoot))
			Motors.motorShooter.set(Motors.Powers.shooter);
		else
			Motors.motorShooter.set(0.0);
	}
	
	private static void intake()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.intake))
			intaking = true;
		
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.outtake))
		{
			intaking = false;
			Motors.motorIntake.set(Motors.Powers.outtake);
		} else if (intaking)
			Motors.motorIntake.set(Motors.Powers.intake);
		else
			Motors.motorIntake.set(0.0);
			
		// If press and hold is desired:
		// if(Joysticks.joyOp.getRawButton(Joysticks.Buttons.intake))
		// Motors.motorIntake.set(Motors.Powers.intake);
		// else if(Joysticks.joyOp.getRawButton(Joysticks.Buttons.outake))
		// Motors.motorIntake.set(Motors.Powers.outtake);
		// else
		// Motors.motorIntake.set(0.0);
		//
		
	}
	
	private static void override()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.override1)
				|| Joysticks.joyOp.getRawButton(Joysticks.Buttons.override1))
		{
			// Check each function, and if it is being pressed, disable it, stop
			// motors, reset to standard, finished state.
			if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.intake)
					|| Joysticks.joyOp.getRawButton(Joysticks.Buttons.outtake))
				intaking = false;
			if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.shoot))
				shooting = false;
		}
	}
}
