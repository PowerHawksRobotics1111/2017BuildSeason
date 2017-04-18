package org.usfirst.frc.team1111.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import subsystems.Drivetrain;
import subsystems.Vision;
import variables.Joysticks;
import variables.Motors;
import variables.Sensors;

public class Operator {

	static Timer timer = new Timer();

	static boolean hanging = false;
	static boolean pistonRan = false;

	/**
	 * This is the master method, it is called periodically to check inputs and
	 * apply commands to the robot accordingly
	 */
	public static void operate() {
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.override1)
				|| Joysticks.joyOp.getRawButton(Joysticks.Buttons.override2))
			override();// Maybe don't need this as a separate function.
		else {
			intake();// Press and hold
			shoot();// press and hold
			hang();// toggle
			// placeGear();// Press to release gear
			// pistons();
			gearPiston();
			resetnavX();
		}
		// Driver method
		// autoAlignPeg();// Press and hold
	}

	/*
	 * static boolean aligned = false;
	 * 
	 * static void autoAlignPeg() { if
	 * (Joysticks.joyDrive.getRawButton(Joysticks.Buttons.driverPegAlign)) {
	 * Robot.overrideDriverJoysticks = true; // Only do alignment while in this
	 * loop if ( !aligned) aligned = Vision.autoAlignWithGear(); // TODO
	 * distance } else { Robot.overrideDriverJoysticks = false; aligned = false;
	 * // If we want, we can give them a separate reset so they can pause //
	 * align without cancel. } }
	 */
	static double leftDriveSetPoint = 0.0;
	static double rightDriveSetPoint = 0.0;

	static double left = 0.0;
	static double right = 0.0;

	static double driveModifier;

	static void drive() {

		// Left

		left = Joysticks.joyDrive.getRawAxis(3);
		right = Joysticks.joyDrive.getRawAxis(1);

		if (Joysticks.joyDrive.getRawButton(Joysticks.LEFT_BUMPER)) {
			leftDriveSetPoint = left;
			rightDriveSetPoint = right;
		} else {
			if (left < 0) {
				/*
				 * if (leftDriveSetPoint >= (left + Motors.DRIVE_ACCELERATION))
				 * leftDriveSetPoint -= Motors.DRIVE_ACCELERATION; else
				 */ if (leftDriveSetPoint <= (left - Motors.DRIVE_DEACCELERATION))
					leftDriveSetPoint += Motors.DRIVE_DEACCELERATION;
				else
					leftDriveSetPoint = left;
			} else if (left > 0) {
				/*
				 * if (leftDriveSetPoint <= (left - Motors.DRIVE_ACCELERATION))
				 * leftDriveSetPoint += Motors.DRIVE_ACCELERATION; else
				 */ if (leftDriveSetPoint > (left + Motors.DRIVE_DEACCELERATION))
					leftDriveSetPoint -= Motors.DRIVE_DEACCELERATION;
				else
					leftDriveSetPoint = left;
			} else if (leftDriveSetPoint <= -Motors.DRIVE_DEACCELERATION)
				leftDriveSetPoint += Motors.DRIVE_DEACCELERATION;
			else if (leftDriveSetPoint >= Motors.DRIVE_DEACCELERATION)
				leftDriveSetPoint -= Motors.DRIVE_DEACCELERATION;
			else
				leftDriveSetPoint = 0.0;

			// Right
			if (right < 0) {
				/*
				 * if (rightDriveSetPoint >= (right +
				 * Motors.DRIVE_ACCELERATION)) rightDriveSetPoint -=
				 * Motors.DRIVE_ACCELERATION; else
				 */ if (rightDriveSetPoint <= (right - Motors.DRIVE_DEACCELERATION))
					rightDriveSetPoint += Motors.DRIVE_DEACCELERATION;
				else
					rightDriveSetPoint = right;
			} else if (right > 0) {
				/*
				 * if (rightDriveSetPoint <= (right -
				 * Motors.DRIVE_ACCELERATION)) rightDriveSetPoint +=
				 * Motors.DRIVE_ACCELERATION; else
				 */if (rightDriveSetPoint > (right + Motors.DRIVE_DEACCELERATION))
					rightDriveSetPoint -= Motors.DRIVE_DEACCELERATION;
				else
					rightDriveSetPoint = right;
			} else if (rightDriveSetPoint <= -Motors.DRIVE_DEACCELERATION)
				rightDriveSetPoint += Motors.DRIVE_DEACCELERATION;
			else if (rightDriveSetPoint >= Motors.DRIVE_DEACCELERATION)
				rightDriveSetPoint -= Motors.DRIVE_DEACCELERATION;
			else
				rightDriveSetPoint = 0.0;
		}

		Drivetrain.drive(-leftDriveSetPoint, rightDriveSetPoint);
	}

	static void hang() {
		double averageAmperage = (Motors.motorHang.getOutputCurrent() + Motors.motorHang2.getOutputCurrent()) / 2.0;
		SmartDashboard.putNumber("Average Climb Amperage", averageAmperage);
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.hang))
			hanging = true;
		if (Joysticks.joyOp.getPOV() == 270) // 270 is left
		{
			Motors.motorHang.set(Motors.hangPower);
			Motors.motorHang2.set(Motors.hangPower);
		} else if (Joysticks.joyOp.getRawButton(Joysticks.X)) {
			Motors.motorHang.set(-.3);
			Motors.motorHang2.set(-.3);
		} else if (!hanging) {
			Motors.motorHang.set(0);
			Motors.motorHang2.set(0);
		} else if (hanging) {
			SmartDashboard.putDouble("Climbing Amperate", Motors.motorHang.getOutputCurrent());
			Motors.motorHang.set(Motors.hangPower);
			Motors.motorHang2.set(Motors.hangPower);
			if (averageAmperage >= Motors.hangStopCurrent)
				hanging = false;
		} else {
			System.out.println(Motors.motorHang.getOutputCurrent());
			SmartDashboard.putDouble("Climbing Amperate", Motors.motorHang.getOutputCurrent());
			Motors.motorHang.set(0.0);
			Motors.motorHang2.set(0.0);
		}
	}

	static double startShootTime = -1.0;

	static void shoot() {
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.shootButton)) {
			if (startShootTime < 0)
				startShootTime = Timer.getMatchTime();

			if (Math.abs(startShootTime - Timer.getMatchTime()) < .5) {
				Motors.motorTopShooter.set(-Motors.shooterVoltage);
				Motors.motorLowShooter.set(-Motors.shooterVoltage);
				Motors.fuelStop.setAngle(Motors.fuelCloseAngle);
			} else {
				Motors.motorTopShooter.set(-Motors.shooterVoltage);
				Motors.motorLowShooter.set(-Motors.shooterVoltage);
				Motors.fuelStop.setAngle(Motors.fuelOpenAngle);
			}

			// Motors.sh
		} else {
			Motors.fuelStop.setAngle(Motors.fuelCloseAngle);
			Motors.motorTopShooter.set(0.0);
			Motors.motorLowShooter.set(0.0);
			startShootTime = -1.0;

		}
	}

	static void intake() {
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.intakeButton))
			Motors.motorIntake.set(Motors.intakePower);
		else if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.outtakeButton))// TODO
																				// changed
			Motors.motorIntake.set(Motors.outtakePower);
		else
			Motors.motorIntake.set(0.0);

	}

	static void placeGear() {
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.gearPrimer)) {
			timer.reset();
			timer.stop();
			Motors.gearHold1.setAngle(Motors.lGearDropdownAngle);
			Motors.gearHold2.setAngle(Motors.rGearStopdownAngle);
		} else if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.gearRelease)
				|| Joysticks.joyDrive.getRawButton(Joysticks.Buttons.driveGearRelease)) {
			if (!pistonRan) {
				Motors.gearHold1.setAngle(Motors.lGearDropdownAngle);
				Motors.gearHold2.setAngle(Motors.rGearDropdownAngle);
				Motors.pushPiston1.set(DoubleSolenoid.Value.kForward);
				Motors.pushPiston2.set(DoubleSolenoid.Value.kForward);
				timer.reset();
			}
			if (timer.get() >= .5) {
				pistonRan = false;
				Motors.gearHold1.setAngle(Motors.lGearStopdownAngle);
				Motors.gearHold2.setAngle(Motors.rGearStopdownAngle);
				Timer.delay(.1);
				Motors.pushPiston1.set(DoubleSolenoid.Value.kReverse);
				Motors.pushPiston2.set(DoubleSolenoid.Value.kReverse);
				Robot.gearReleased = false;
				timer.reset();
				timer.stop();
			}
			Robot.gearReleased = true;
		} else {
			Motors.gearHold1.setAngle(Motors.lGearStopdownAngle);
			Motors.gearHold2.setAngle(Motors.rGearStopdownAngle);
			Motors.pushPiston1.set(DoubleSolenoid.Value.kReverse);
			Motors.pushPiston2.set(DoubleSolenoid.Value.kReverse);
			Robot.gearReleased = false;
		}
	}

	static double startTime = -1.0;

	static void gearPiston() {
		if (Joysticks.joyOp.getPOV() == Joysticks.Buttons.pForward) {
			Motors.pushPiston1.set(DoubleSolenoid.Value.kForward);
			Motors.pushPiston2.set(DoubleSolenoid.Value.kForward);
		} else {

			if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.gearRelease)
					|| Joysticks.joyDrive.getRawButton(Joysticks.Buttons.driveGearRelease)) {
				System.out.println("WHY");

				Robot.gearReleased = true;

				if (startTime < 0)
					startTime = Timer.getMatchTime();

				if ((startTime - Timer.getMatchTime()) <= .5) {
					Motors.gearHold1.setAngle(Motors.lGearDropdownAngle);
					Motors.gearHold2.setAngle(Motors.rGearDropdownAngle);
				} else {
					Motors.gearHold1.setAngle(Motors.lGearDropdownAngle);
					Motors.gearHold2.setAngle(Motors.rGearDropdownAngle);
					Motors.pushPiston1.set(DoubleSolenoid.Value.kForward);
					Motors.pushPiston2.set(DoubleSolenoid.Value.kForward);
				}
			} else {
				if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.gearPrimer)) {
					Motors.gearHold1.setAngle(Motors.lGearPrimeAngle);
					Motors.gearHold2.setAngle(Motors.rGearPrimeAngle);
				} else {
					if (Joysticks.joyOp.getRawAxis(2) <= 0)
					{
						Motors.gearHold1.setAngle(Motors.lGearStopdownAngle);
					} else
					{
						Motors.gearHold1.setAngle(Motors.lGearStopdownAngle
								- Math.abs((Motors.lGearStopdownAngle - Motors.lGearDropdownAngle)
										* Joysticks.joyOp.getRawAxis(2)));
					}
					
					if (Joysticks.joyOp.getRawAxis(0) >= 0)
					{
						Motors.gearHold2.setAngle(Motors.rGearStopdownAngle);
					} else
					{
						Motors.gearHold2.setAngle(Motors.rGearStopdownAngle
								+ Math.abs((Motors.rGearStopdownAngle - Motors.rGearDropdownAngle)
										* Joysticks.joyOp.getRawAxis(0)));
					}
				}
				Motors.pushPiston1.set(DoubleSolenoid.Value.kReverse);
				Motors.pushPiston2.set(DoubleSolenoid.Value.kReverse);
				Robot.gearReleased = false;
				startTime = -1.0;
			}
		}

	}

	static void pistons() {
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.pForward)) {
			Motors.pushPiston1.set(DoubleSolenoid.Value.kForward);
			Motors.pushPiston2.set(DoubleSolenoid.Value.kForward);
		} else {
			Motors.pushPiston1.set(DoubleSolenoid.Value.kReverse);
			Motors.pushPiston2.set(DoubleSolenoid.Value.kReverse);
		}
	}

	static void resetnavX() {
		if (Joysticks.joyDrive.getRawButton(Joysticks.Buttons.navXReset))
			Sensors.navX.reset();
		if (Joysticks.joyDrive.getRawButton(Joysticks.Buttons.navXYawReset))
			Sensors.navX.zeroYaw();
	}

	/**
	 * Check each function, and if it is being pressed, disable it, stop motors,
	 * reset to standard, finished state.
	 */
	static void override() {
		if (Joysticks.joyOp.getRawButton(Joysticks.Buttons.hang)) {
			hanging = false;
			Motors.motorHang.set(0.0);
			Motors.motorHang2.set(0.0);
		}
	}
}
