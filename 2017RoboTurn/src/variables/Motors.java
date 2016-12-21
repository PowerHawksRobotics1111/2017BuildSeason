package variables;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Motors 
{

	//motor variables for drive, arm, intake, outer intake, tape arm, and servo
	public static CANTalon motorDriveFrontRight, motorDriveFrontLeft, motorDriveBackRight, motorDriveBackLeft; 
	public static CANTalon motorArm; 
	public static CANTalon motorInnerIntake, motorOuterIntake, motorShooter, motorTapeArm;
	public static CANTalon flashlight;
	public static double wheelCircumfrance = 18.85;
	
//	public static Servo brake;
	public static Servo hardBallStop;
	
//	public static Relay flashlightControlSpike;
//	public static Relay lightingControlSpike;

	//motor ports for drive, arm, intake, outer intake, tape arm, and servo
	final static int FRONT_RIGHT_DRIVE = 47, FRONT_LEFT_DRIVE = 52, BACK_RIGHT_DRIVE = 55, BACK_LEFT_DRIVE = 60;  //TODO MDBL Port possibly wrong mapped.
	final static int ARM = 53; 
	final static int INNER_INTAKE = 44, OUTER_INTAKE = 62, SHOOTER = 61, TAPE_ARM = 45; 
	final static int TAPE_BRAKE_SERVO = 0;
		final static int BALL_STOP = 1;

	//Motor power variables for forward, reverse, intake, outer intake, arm, shooter, and tape arm
	public final static double FULL_POWER = 1.0, THREE_QUARTERS_POWER = .75, HALF_POWER = .5, QUARTER_POWER = .25, NO_POWER = 0.0;
	public final static double REVERSE_FULL_POWER = -1.0, REVERSE_THREE_QUARTERS_POWER = -.75, REVERSE_HALF_POWER = -.5, REVERSE_QUARTER_POWER = -.25;
	public static final double INNER_INTAKE_POWER = .68, OUTER_INTAKE_POWER = .60/*was  .50*/, OUTER_OUTAKE_POWER = -.80, ARM_POWER = -.58, /*SHOOTER_POWER = .725,*/ TAPE_ARM_POWER = .75, INNER_OUTAKE_POWER = -1.0, ARM_DOWN_POWER = .50;

	public static double SHOOTER_POWER = .3625;
	//public static final double BRAKE_ANGLE = 120; //TODO find accurate breaking angle
	
	public static final double BACK_WHEEL_DRIVE_RATIO = 7.66/8.0; //TODO this is to adjust for the pneumatic wheels if we need to!!!
	public static final double SHOOTER_OPTIMAL_MAXIMUM_VOLTAGE = 12.6;//TODO Set to the optimal voltage and adjust to voltage control mode.
	public static final double flashlightVoltage = 9.0;
	//private static final int ARM_TICKS_PER_REV = 18;

	
	public static void shooterSpeedDashboardInit()
	{
		SmartDashboard.putString("Shooter Adjustment Instructions", "Shooter speed must be positive double less then 1.0." + "/n" + "Competition shooter speed is .725." + "/n" + "Default speed for demo is .3625");
		SmartDashboard.putNumber("Shooter Speed Setting", SHOOTER_POWER);
	}
	
	public static void shooterSpeedDashboardUpdate()
	{
		SHOOTER_POWER = SmartDashboard.getNumber("Shooter Speed Setting");
	}

	/** Motor initialization*/
	public static void motorInit()
	{
		motorDriveFrontRight = new CANTalon(FRONT_RIGHT_DRIVE);
		motorDriveFrontLeft = new CANTalon(FRONT_LEFT_DRIVE);
		motorDriveBackRight = new CANTalon(BACK_RIGHT_DRIVE);
		motorDriveBackLeft = new CANTalon(BACK_LEFT_DRIVE);

		motorArm = new CANTalon(ARM);
		Motors.motorArm.enableBrakeMode(true);
		//motorArm.setVoltageRampRate(3); TODO this stopped the arm entirely
		//motorArm.configEncoderCodesPerRev(ARM_TICKS_PER_REV);
		motorArm.setEncPosition(0);
		
		motorTapeArm = new CANTalon(TAPE_ARM);
		motorTapeArm.setInverted(false);

		motorShooter = new CANTalon(SHOOTER);//TODO VOltage control mode
		motorShooter.changeControlMode(TalonControlMode.Voltage);

		motorInnerIntake = new CANTalon(INNER_INTAKE);
		motorOuterIntake = new CANTalon(OUTER_INTAKE);

		motorInnerIntake.setInverted(false);
		motorOuterIntake.setInverted(true);

//		brake = new Servo(TAPE_BRAKE_SERVO);

		hardBallStop = new Servo(BALL_STOP);
		
//		flashlightControlSpike = new Relay(0);
//		
//		lightingControlSpike = new Relay(1);
		
		flashlight = new CANTalon(50);
		flashlight.changeControlMode(TalonControlMode.Voltage);

	}
} 