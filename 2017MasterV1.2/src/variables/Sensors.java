package variables;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SerialPort;

public class Sensors
{
	
	public static AHRS navX = new AHRS(SerialPort.Port.kMXP);
	public final static double INCHES_TO_DRIVE_ENCODER = 0;
}
