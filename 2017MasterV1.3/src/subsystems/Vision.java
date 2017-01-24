package subsystems;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import variables.Motors;
import variables.Sensors;
import variables.VisionVar;

public class Vision {

	static final double deltaH = 72.5;
	static final double shootingDistance = 47.53;
	static boolean retrievedImage = false;
	static double targetAngle = 0;
	static double[] TargetX;
	static double[] TargetY;
	static double degreesToRotate;
	static double degreesToRotateDistance;
	static double distanceToMoveToTarget;
	static double averageX, averageY;

	public static double findThetaX(double currentCenterX)
	{
		double theta = 0.0;
		theta = Math.atan((VisionVar.centerXVision - currentCenterX) / VisionVar.focalLength) * (180 / Math.PI);
		retrievedImage = true;
		return theta * -1;
	}

	public static double findThetaForDist(double currentCenterY)
	{
		double theta = 0.0;
		theta = (currentCenterY - VisionVar.centerYVision) / VisionVar.focalLength;
		return theta;
	}

	public static double findDistToMove(double theta)
	{
		double x = 0.0;
		x = (deltaH / Math.tan(theta)) - shootingDistance;
		return x;
	}

	public static double getAverageX()
	{
		if (TargetX.length == 1)
			averageX = TargetX[0];
		else if (TargetX.length == 2)
			averageX = ((TargetX[0] + TargetX[1]) / 2);

		return averageX;
	}

	public static double getAverageY()
	{
		if (TargetY.length == 1)
			averageY = TargetY[0];
		else if (TargetY.length == 2)
			averageY = ((TargetY[0] + TargetY[1]) / 2);

		return averageY;
	}

	public static void turnNumDegrees(double angleDifference)
	{
		// The motors rotate CCW (viewed from the back) when set to a positive
		// value

		// If the target is right of the center of the camera view,
		if (angleDifference > 1)
			Drivetrain.rotateCW(Motors.AUTO_ALIGN_POWER);
		else if (angleDifference < -1)
			Drivetrain.rotateCCW(Motors.AUTO_ALIGN_POWER);
		else// Stop when at the target
			Drivetrain.drive(0, 0);
	}

	public static void turnToTarget(double targetAngle)
	{
		double angleDelta = targetAngle - Sensors.navX.getYaw();
		turnNumDegrees(angleDelta);
	}

	public static boolean autoAlignWithGear()
	{
		// TargetX will be a null array if no target is visible.
		// Trying to execute findTheta with a null array will crash the program.
		if ( !retrievedImage)
		{
			try
			{
				TargetX = VisionVar.gearTable.getNumberArray("centerX");
				TargetY = VisionVar.gearTable.getNumberArray("centerY");
			} catch (Exception E)
			{
				SmartDashboard.putString("Vision status", "Could not retrieve image!");
				return false;
			}

			degreesToRotate = findThetaX(getAverageX());
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;

			if (TargetX.length == 2)
			{
				// Find and move to the correct distance away from the target
				TargetY = VisionVar.gearTable.getNumberArray("centerY");
				degreesToRotateDistance = findThetaForDist(getAverageY());
				double distanceToMove = findDistToMove(degreesToRotateDistance);
				Drivetrain.moveToDistance(distanceToMove);
				if (true /* distance to move is acceptable */)
					return true;
				else
					return false;
			} else
			{
				if(VisionVar.gearTable.getNumberArray("key") >= /*value for right*/)
				{
					turnNumDegrees(5);
					return false;
				}else
				{
					turnNumDegrees(-5);
					return false;
				}
			}

		} else
		{
			turnToTarget(targetAngle);
		}
	}

	public static void autoAlignWithBoiler()
	{
		// TargetX will be a null array if no target is visible.
		// Trying to execute findTheta with a null array will crash the program.
		if ( !retrievedImage)
		{
			try
			{
				TargetX = VisionVar.boilerTable.getNumberArray("centerX");
				TargetY = VisionVar.boilerTable.getNumberArray("centerY");
			} catch (Exception E)
			{
				SmartDashboard.putString("Vision status", "Could not retrieve image!");
				return;
			}
			degreesToRotate = findThetaX(getAverageX());
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;

			// Find and move to the correct distance away from the target
			double[] CurrentCenterYVision = VisionVar.boilerTable.getNumberArray("centerY");
			degreesToRotateDistance = findThetaForDist(CurrentCenterYVision[0]);
			double distanceToMove = findDistToMove(degreesToRotateDistance);
			Drivetrain.moveToDistance(distanceToMove);

		} else
			turnToTarget(targetAngle);
	}

	/**
	 * Input the camera which you want to be streaming to GRIP/DS and check to
	 * see if it is on.
	 *
	 * If the inputed camera is not on, it switches to it.
	 */
	public static void cameraSwitch(int camera)
	{
		// TODO We can't really turn on/off the ip camera. We can do it for a
		// usb cam.
		// check to see which camera is on
		// if the on camera is the desired camera
	}
}
