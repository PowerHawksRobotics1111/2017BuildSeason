package variables;

import edu.wpi.first.wpilibj.PIDController;

public class PIDValues {
	
	// Initialize the PID values for kP, kI, and kD respectively for each system
	public static final double ROTATE_KP = .029, ROTATE_KI = .0, ROTATE_KD = .000;
	public static final double DRIVE_TRAIN_KP_RIGHT = .000002, DRIVE_TRAIN_KI = .0, DRIVE_TRAIN_KD = .000, DRIVE_TRAIN_KP_LEFT = .000002;
//  .000002      .0000015
	
	
	// Initialize the PID Controller Objects for each Drive Motor set
	public static final PIDController leftDrive1 = new PIDController(PIDValues.DRIVE_TRAIN_KP_LEFT, PIDValues.DRIVE_TRAIN_KI,
			PIDValues.DRIVE_TRAIN_KD, 0.0, Motors.motorDriveLeft1, Motors.motorDriveLeft1);
	public static final PIDController leftDrive2 = new PIDController(PIDValues.DRIVE_TRAIN_KP_LEFT, PIDValues.DRIVE_TRAIN_KI,
			PIDValues.DRIVE_TRAIN_KD, 0.0, Motors.motorDriveLeft1, Motors.motorDriveLeft2);
	public static final PIDController rightDrive1 = new PIDController(PIDValues.DRIVE_TRAIN_KP_RIGHT,
			PIDValues.DRIVE_TRAIN_KI, PIDValues.DRIVE_TRAIN_KD, 0.0, Motors.motorDriveRight1, Motors.motorDriveRight2);
	public static final PIDController rightDrive2 = new PIDController(PIDValues.DRIVE_TRAIN_KP_RIGHT,
			PIDValues.DRIVE_TRAIN_KI, PIDValues.DRIVE_TRAIN_KD, 0.0, Motors.motorDriveRight1, Motors.motorDriveRight2);
	
	// Initialize the Rotation PID Controller Objects for each Drive Motor Set
	public static final PIDController leftNavX1 = new PIDController(PIDValues.ROTATE_KP,
			PIDValues.ROTATE_KI, PIDValues.ROTATE_KD, 0.0, Sensors.navX, Motors.motorDriveLeft1);
	public static final PIDController leftNavX2 = new PIDController(PIDValues.ROTATE_KP,
			PIDValues.ROTATE_KI, PIDValues.ROTATE_KD, 0.0, Sensors.navX, Motors.motorDriveLeft2);
	public static final PIDController rightNavX1 = new PIDController(PIDValues.ROTATE_KP,
			PIDValues.DRIVE_TRAIN_KI, PIDValues.DRIVE_TRAIN_KD, 0.0, Sensors.navX, Motors.motorDriveRight2);
	public static final PIDController rightNavX2 = new PIDController(PIDValues.ROTATE_KP,
			PIDValues.ROTATE_KI, PIDValues.ROTATE_KD, 0.0, Sensors.navX, Motors.motorDriveRight2);
}
