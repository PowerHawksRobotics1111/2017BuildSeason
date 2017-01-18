package variables;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SerialPort;

public class Sensors
{
	
	public static AHRS navX = new AHRS(SerialPort.Port.kMXP);
	public static double encCountToInches = 12345; // TODO
}
