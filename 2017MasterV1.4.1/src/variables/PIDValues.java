package variables;

import edu.wpi.first.wpilibj.PIDController;

public class PIDValues {
	
	// Initialize the PID values for kP, kI, and kD respectively for each system
	public static final double DRIVE_TRAIN_KP = .25, DRIVE_TRAIN_KI = .5, DRIVE_TRAIN_KD = .0002;
	
	// Initialize the PID Controller Objects for each Drive Motor set
	public static final PIDController leftDrive1 = new PIDController(PIDValues.DRIVE_TRAIN_KP, PIDValues.DRIVE_TRAIN_KI,
			PIDValues.DRIVE_TRAIN_KD, 0.0, Motors.motorDriveLeft1, Motors.motorDriveLeft1);
	public static final PIDController leftDrive2 = new PIDController(PIDValues.DRIVE_TRAIN_KP, PIDValues.DRIVE_TRAIN_KI,
			PIDValues.DRIVE_TRAIN_KD, 0.0, Motors.motorDriveLeft2, Motors.motorDriveLeft2);
	public static final PIDController rightDrive1 = new PIDController(PIDValues.DRIVE_TRAIN_KP,
			PIDValues.DRIVE_TRAIN_KI, PIDValues.DRIVE_TRAIN_KD, 0.0, Motors.motorDriveRight1, Motors.motorDriveRight2);
	public static final PIDController rightDrive2 = new PIDController(PIDValues.DRIVE_TRAIN_KP,
			PIDValues.DRIVE_TRAIN_KI, PIDValues.DRIVE_TRAIN_KD, 0.0, Motors.motorDriveRight2, Motors.motorDriveRight2);
	
	// Initialize the Rotation PID Controller Objects for each Drive Motor Set
	public static final PIDController leftNavX1 = new PIDController(PIDValues.DRIVE_TRAIN_KP,
			PIDValues.DRIVE_TRAIN_KI, PIDValues.DRIVE_TRAIN_KD, 0.0, Sensors.navX, Motors.motorDriveLeft1);
	public static final PIDController leftNavX2 = new PIDController(PIDValues.DRIVE_TRAIN_KP,
			PIDValues.DRIVE_TRAIN_KI, PIDValues.DRIVE_TRAIN_KD, 0.0, Sensors.navX, Motors.motorDriveLeft2);
	public static final PIDController rightNavX1 = new PIDController(PIDValues.DRIVE_TRAIN_KP,
			PIDValues.DRIVE_TRAIN_KI, PIDValues.DRIVE_TRAIN_KD, 0.0, Sensors.navX, Motors.motorDriveRight2);
	public static final PIDController rightNavX2 = new PIDController(PIDValues.DRIVE_TRAIN_KP,
			PIDValues.DRIVE_TRAIN_KI, PIDValues.DRIVE_TRAIN_KD, 0.0, Sensors.navX, Motors.motorDriveRight2);
}
