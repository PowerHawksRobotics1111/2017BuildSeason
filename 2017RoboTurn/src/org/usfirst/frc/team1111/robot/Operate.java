package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Timer;
import variables.Joysticks;
import variables.Motors;
import variables.Sensors;

public class Operate {

	/*
	 * The following are booleans to carry states through iterations of the
	 * code.
	 * 
	 * bool intake determines if the intake is set to run, as to wait for a
	 * ball.
	 * int armPos holds arm state.
	 * String armState translate state to the dashboard
	 */
	static boolean intake = false;
	//	private static final int NUM_ARM_STATES = 5;
	//	static int armPos = 0;
	//	static String armState = "";


	/**
	 * Runs through test methods to determine what operation is requested
	 */
	public static void operate()
	{
		if (!Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier) && !Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier2))
		{
			intakeOutake();
			shoot();
//			tapeArm();
			armControl();
			hardStopToggle();
			//		armStates();
		}
		
		
//		orientByUltra();
//		distanceByUltra();
		flashlightToggle();

		functionStopOverride();
	}



	/**
	 * Method, checks the intake and outtake command buttons and runs the
	 * intake/outake motor, else stops the motor.
	 */
	static void intakeOutake()
	{
		if(!(Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier2) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier3) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier4)))
		{
			if( !shooting && Joysticks.joyOp.getRawButton(Joysticks.Buttons.intakeButton))
			{
				Motors.motorOuterIntake.set(Motors.OUTER_INTAKE_POWER);
				Motors.motorInnerIntake.set(Motors.INNER_INTAKE_POWER);
				intake = true;
				Motors.hardBallStop.setAngle(45.0);
			}else if ( !shooting && Joysticks.joyOp.getRawButton(Joysticks.Buttons.outtakeButton))
			{
				Motors.motorOuterIntake.set(Motors.OUTER_OUTAKE_POWER);
				Motors.motorInnerIntake.set(Motors.INNER_OUTAKE_POWER);
				intake = false;
			}else if(shooting && Joysticks.joyOp.getRawButton(Joysticks.Buttons.outtakeButton))
			{
				Motors.motorInnerIntake.set(Motors.INNER_OUTAKE_POWER);
			}else if(intake && (Sensors.intakeLimitSwitch.get() || Sensors.intakeLimitSwitch2.get()))
			{
				Timer.delay(.05);
				Motors.motorOuterIntake.set(Motors.NO_POWER);
				Motors.motorInnerIntake.set(Motors.NO_POWER);
				intake = false;
			}else if(!intake)
			{
				Motors.motorOuterIntake.set(Motors.NO_POWER);
				if(!shooting)
					Motors.motorInnerIntake.set(Motors.NO_POWER);
			}
		}
	}

	public static boolean shooting = false;
	static boolean shootingIntake = false;

	/**
	 * Method, runs or stops the shoot motor.
	 */
	static void shoot()
	{
		if(Joysticks.joyOp.getRawButton(Joysticks.Buttons.shootButton) && !(Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier2) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier3) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier4)))
			shooting = true;

		if(shooting)
		{
			Motors.motorShooter.set(Motors.SHOOTER_POWER * Motors.SHOOTER_OPTIMAL_MAXIMUM_VOLTAGE);
			Motors.hardBallStop.setAngle(0.0);
		}
		else
		{
			Motors.motorShooter.set(Motors.NO_POWER);
			shootingIntake = false;
		}

		if(shooting && (Joysticks.joyOp.getRawButton(Joysticks.Buttons.intakeButton) || Joysticks.joyDrive.getRawButton(Joysticks.Buttons.driverIntakeButton)))
			shootingIntake = true;

		if(shooting && shootingIntake)
		{
			Motors.motorInnerIntake.set(Motors.INNER_INTAKE_POWER);
			Motors.motorShooter.set(Motors.SHOOTER_POWER * Motors.SHOOTER_OPTIMAL_MAXIMUM_VOLTAGE);
		}
	}

	/**
	 * Method, controls the tape arm extension motor.
	 */
//	static void tapeArm()
//	{
//		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.tapeArmExtend))
//			//armStates = false;
//			//Motors.motorArm.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
//			Motors.motorTapeArm.set(Motors.TAPE_ARM_POWER);
//		else if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.tapeArmRetract))
//			//armStates = false;
//			//Motors.motorArm.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
//			Motors.motorTapeArm.set(-Motors.TAPE_ARM_POWER);
//		else
//			Motors.motorTapeArm.set(Motors.NO_POWER);
//	}

	//	static boolean armStates = false;

	/**
	 * Intake arm control
	 */
	static void armControl()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.armUp))
			Motors.motorArm.set(Motors.ARM_POWER);
		else if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.armDown))
			Motors.motorArm.set(Motors.ARM_DOWN_POWER);
		else
			Motors.motorArm.set(Motors.NO_POWER);
//		if(Joysticks.joyOp.getRawButton(Joysticks.Buttons.armDown))
//			if(Motors.motorArm.getEncPosition() >= -1600)
//				Motors.motorArm.set(Motors.ARM_DOWN_POWER * 0.5);
//			else
//				Motors.motorArm.set(0.0);
//		else if(Joysticks.joyOp.getRawButton(Joysticks.Buttons.armUp))
//			if(Motors.motorArm.getEncPosition() <= -300)
//				Motors.motorArm.set(Motors.ARM_POWER *0.4);
//			else
//				Motors.motorArm.set(0.0);
//		else
//			Motors.motorArm.set(0.0);
					
	}
	/**
	final static class ArmStates//Done from zero at the top to 90 at horizantal hopefully...
	{
		final static int UP = 0, SCALING = 45, DRAWBRIDGE = 45, INTAKE = 90, LOWBAR = 95;//TODO Angles for scale and drawbridge (If we get this working)
	}

	static int armStateNumber;

	static void armStates()
	{
		if(Joysticks.joyOp.getPOV() == Joysticks.D_PAD_UP)
		{
			armStateNumber++;
			armStates = true;
		}else if(Joysticks.joyOp.getPOV() == Joysticks.D_PAD_DOWN)
		{
			armStateNumber++;
			armStates = true;
		}

		String armPosForDash = "Not Position Based";

		if(armStates)
		{
			Motors.motorArm.changeControlMode(CANTalon.TalonControlMode.Position);
			int state = armStateNumber % NUM_ARM_STATES;
			if(state == 0)
			{
				Motors.motorArm.set(ArmStates.UP);
				armPosForDash = "Up";
			}else if(state == 1)
			{
				Motors.motorArm.set(ArmStates.SCALING);
				armPosForDash = "Scaling";
			}else if(state == 2)
			{
				Motors.motorArm.set(ArmStates.DRAWBRIDGE);
				armPosForDash = "DrawBidge";
			}else if(state == 3)
			{
				Motors.motorArm.set(ArmStates.INTAKE);
				armPosForDash = "Intake";
			}else if(state == 4)
			{
				Motors.motorArm.set(ArmStates.LOWBAR);
				armPosForDash = "Low Bar";
			}
		}

		SmartDashboard.putString("Arm State: ", armPosForDash);
	}
	 */

	/**
	 * Function override implementation
	 */
	static void functionStopOverride()
	{
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier2) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier3) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.overrideKillModifier4))
		{
			if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.intakeButton) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.outtakeButton))
			{
				Motors.motorInnerIntake.set(Motors.NO_POWER);
				Motors.motorOuterIntake.set(Motors.NO_POWER);
				intake = false;
			}
			if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.shootButton))
			{
				Motors.motorShooter.set(Motors.NO_POWER);
				Motors.motorInnerIntake.set(Motors.NO_POWER);
				shooting = false;
				shootingIntake = false;
				Motors.hardBallStop.setAngle(45);

			}
//			if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.tapeArmExtend) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.tapeArmRetract))
//				Motors.motorTapeArm.set(Motors.NO_POWER);
			if(Joysticks.joyOp.getRawButton(Joysticks.Buttons.armUp) || Joysticks.joyOp.getRawButton(Joysticks.Buttons.armDown))
			{
				//armStates = false;
				//Motors.motorArm.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
				Motors.motorArm.set(Motors.NO_POWER);
			}
		}
	}



	public static void disable()
	{
		Auto.Movement.stopDriveMotors();

		shooting = false;
		intake = false;
		shootingIntake = false;
	}

	public static boolean hardStop = false;
	
		static void hardStopToggle()
		{
			if( !hardStop && Joysticks.joyOp.getRawButton(Joysticks.Buttons.hardStop))
			{
				hardStop = true;
				if(Motors.hardBallStop.getAngle() == 0)
					Motors.hardBallStop.setAngle(45);
				else if(Motors.hardBallStop.getAngle() == 45)
					Motors.hardBallStop.setAngle(0);
			}
			
			if(hardStop && !Joysticks.joyOp.getRawButton(Joysticks.Buttons.hardStop))
				hardStop = false;
		}
				
		static void flashlightToggle()
		{
			if(Joysticks.joyDrive.getRawButton(Joysticks.Buttons.driverFlashlightButton) || Joysticks.joyDrive.getRawButton(Joysticks.Buttons.driverFlashlightButton2) || Joysticks.joyDrive.getRawButton(Joysticks.Buttons.driverFlashlightButton3))
				Motors.flashlight.set(Motors.flashlightVoltage);
			else
				Motors.flashlight.set(0.0);
		}
//		
//		static void orientByUltra()
//		{
//			if(Joysticks.joyDrive.getRawButton(Joysticks.Buttons.drivarAutoOrientButton))
//			{
//				 if(Sensors.leftUltra.getRangeInches() > Sensors.rightUltra.getRangeInches() +  2)
//				 {
//					 Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * 1.0 * .6);
//					 Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * .925 * .6);
//					 Motors.motorDriveFrontLeft.set(1.0 * .6);
//					 Motors.motorDriveFrontRight.set(.925 * .6);
//				 }else if(Sensors.leftUltra.getRangeInches() + 2 < Sensors.rightUltra.getRangeInches())
//				 {
//					 Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1.0 * .6);
//					 Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * -.925 * .6);
//					 Motors.motorDriveFrontLeft.set(-1.0 * .6);
//					 Motors.motorDriveFrontRight.set(-.925 * .6);
//				 }
//				 else
//					 Auto.Movement.stopDriveMotors();
//			}
//		}
//		
//		/**
//		 * Inches
//		 */
//		static final double ultraShootDistance = 0.0;
//		
//		static void distanceByUltra()
//		{
//			if(Joysticks.joyDrive.getRawButton(Joysticks.Buttons.drivarAutoDistanceAndOrientButton))
//				if(Sensors.leftUltra.getRangeInches() <= Sensors.rightUltra.getRangeInches() + 2 && Sensors.leftUltra.getRangeInches() >= Sensors.rightUltra.getRangeInches() - 2)
//					if((Sensors.leftUltra.getRangeInches() + Sensors.leftUltra.getRangeInches())/2.0 >= ultraShootDistance + 2.0)
//					{
//						Motors.motorDriveBackLeft.set(-1.0 * Motors.BACK_WHEEL_DRIVE_RATIO * -1.0 * .25); 
//						Motors.motorDriveBackRight.set(-1.0 * Motors.BACK_WHEEL_DRIVE_RATIO * .925 *.25);
//						Motors.motorDriveFrontLeft.set(-1.0 * -1.0 * .25);
//						Motors.motorDriveFrontRight.set(-1.0 * .925 *.25);
//					}else if((Sensors.leftUltra.getRangeInches() + Sensors.leftUltra.getRangeInches())/2.0 <= ultraShootDistance + 2.0)
//					{
//						Motors.motorDriveBackLeft.set(Motors.BACK_WHEEL_DRIVE_RATIO * -1.0 * .25); 
//						Motors.motorDriveBackRight.set(Motors.BACK_WHEEL_DRIVE_RATIO * .925 *.25);
//						Motors.motorDriveFrontLeft.set(-1.0 * .25);
//						Motors.motorDriveFrontRight.set(.925 *.25);
//					}else
//						Auto.Movement.stopDriveMotors();
//				else
//					orientByUltra();
//		}
		

}
