package subsystems;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import variables.Motors;
import variables.Sensors;
import variables.VisionVar;

public class Vision
{
	
	static final double deltaH = 72.5;
	static final double shootingDistance = 47.53;
	static boolean retrievedImage = false;
	static double targetAngle = 0;
	static double[] TargetX;
	static double[] TargetY;
	static double degreesToRotate;
	static double degreesToRotateDistance;
	static double distanceToMoveToTarget;
	
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
	
	public static void autoAlignWithAirship()
	{
		NetworkTable VisionTable = NetworkTable.getTable("GRIP/contoursReport");
		// TargetX will be a null array if no target is visible.
		// Trying to execute findTheta with a null array will crash the program.
		if ( !retrievedImage)
		{
			try
			{
				TargetX = VisionTable.getNumberArray("centerX");
				TargetY = VisionTable.getNumberArray("centerY");
				SmartDashboard.putNumber("X", (TargetX[0] + TargetX[1]) / 2);
				SmartDashboard.putNumber("Y", (TargetY[0] + TargetY[1]) / 2);
			} catch (Exception E)
			{
				SmartDashboard.putString("Vision status", "Could not retrieve image!");
				return;
			}
			degreesToRotate = findThetaX((TargetX[0] + TargetX[1]) / 2);
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			SmartDashboard.putNumber("Degrees to rotate", degreesToRotate);
			SmartDashboard.putNumber("Target angle", targetAngle);
			
			// Find and move to the correct distance away from the target
			double[] CurrentCenterYVision = VisionTable.getNumberArray("centerY");
			degreesToRotateDistance = findThetaForDist(CurrentCenterYVision[0]);
			double distanceToMove = findDistToMove(degreesToRotateDistance);
			driveToDistance(distanceToMove);
			
		} else
		{
			SmartDashboard.putNumber("Remaining degrees", targetAngle - Sensors.navX.getYaw());
			turnToTarget(targetAngle);
		}
	}
	
	public static void autoAlignWithBoiler()
	{
		
	}
	
	public static void driveToDistance(double distance)
	{
		if ( !retrievedImage)
		{
			
		}
	}
}
