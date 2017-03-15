package subsystems;

import edu.wpi.first.wpilibj.Timer;
import variables.Motors;

public class Fuel
{
	
	private static final double shooterSpinUpTime = 0;
	static double startTime = -1;
	
	public static void shoot()
	{
		// For without encoder.
		if (startTime == -1)
			startTime = Timer.getMatchTime();
		else if (Math.abs(startTime - Timer.getMatchTime()) < shooterSpinUpTime)
		{
			// TODO do a thing to shoot. How are we stopping this? are we just
			// running to end of match or will we find an average time to empty
			// hopper of the 10 fuel?
		}
		
	}
	
	public static void autoShoot()
	{
		Motors.motorTopShooter.set(-Motors.shooterVoltage);
		Motors.motorLowShooter.set(-Motors.shooterVoltage);
		Motors.motorAgitator.set(Motors.agitationPower);
	}
	
	public static void stopShoot()
	{
		Motors.motorTopShooter.set(0);
		Motors.motorLowShooter.set(0);
		Motors.motorAgitator.set(0);
	}
	
	public static void intake()
	{
		Motors.motorIntake.set(Motors.intakePower);
	}
	
	public static void outtake()
	{
		Motors.motorIntake.set(Motors.outtakePower);
	}
	
}
