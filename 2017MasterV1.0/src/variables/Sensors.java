package variables;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Encoder;

public class Sensors {

	//initialize encoders using format Encoder(channelA, channelB), unless it is connected to the talon, in which case you use the talon's method.
	Encoder leftDriveEnc = new Encoder(null, null);
}
