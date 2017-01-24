package subsystems;

import edu.wpi.first.wpilibj.Timer;

public class Fuel {

	private static final double shooterSpinUpTime = 0;
	static double startTime = -1;

	public static void shoot()
	{
		// For without encoder.
		if (startTime == -1)
			startTime = Timer.getMatchTime();
		else if (Math.abs(startTime - Timer.getMatchTime()) < shooterSpinUpTime)
		{
			// do a thing to shoot. How are we stopping this? are we just
			// running to end of match or will we find an average time to empty
			// hopper of the 10 fuel?
		}

	}

}
