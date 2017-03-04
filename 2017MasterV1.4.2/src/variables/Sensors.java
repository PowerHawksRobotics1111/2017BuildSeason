package variables;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Sensors
{
	
	public static AHRS navX;
	// public final static double INCHES_TO_DRIVE_ENCODER_LEFT = (2572.48 * 1.9)
	// + 200;
	public final static double INCHES_TO_DRIVE_ENCODER_LEFT = 4954.284;
	
	// public final static double INCHES_TO_DRIVE_ENCODER_RIGHT = (2572.48 *
	// 1.9) + 200;
	public final static double INCHES_TO_DRIVE_ENCODER_RIGHT = 5052.503;
	
	// original value of 2572.48
	
	public static Relay LEDRelay1, LEDRelay2;
	public static int RELAY_PORT1 = 0;
	
	public static void sensorsInit()
	{
		navX = new AHRS(SerialPort.Port.kMXP);
		
		navX.zeroYaw();
		SmartDashboard.putNumber("NavX zero?", navX.getAngle());
		
		LEDRelay1 = new Relay(RELAY_PORT1);
	}
}
