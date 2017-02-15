package subsystems;

//import org.usfirst.frc.team1111.robot.Drivetrain;
import org.usfirst.frc.team1111.robot.Robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import variables.Motors;
import variables.Sensors;
import variables.VisionVar;

public class Vision
{
	
	// TODO Move constants VisionVars
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
	static double errorCorrection;
	
	public static double findThetaX(double currentCenterX, double focalLength)
	{
		double theta = 0.0;
		theta = Math.atan((VisionVar.centerXVision - currentCenterX) / focalLength) * (180 / Math.PI);
		retrievedImage = true;
		return theta * -1;
	}
	
	// TODO Check Functionality
	public static double findThetaForDist(double currentCenterY, double centerYVision, double focalLength)
	{
		double theta = 0.0;
		theta = (currentCenterY - centerYVision) / focalLength;
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
		if (!retrievedImage)
		{
			System.out.println("Blue Footed Boobies");
			try
			{
				TargetX = VisionVar.gearTable.getNumberArray("centerX");
				TargetY = VisionVar.gearTable.getNumberArray("centerY");
			} catch (Exception E)
			{
				SmartDashboard.putString("Vision status", "Could not retrieve image!");
				return false;
			}
			if (TargetX.length == 2)
			{
				Drivetrain.drive(0, 0);
				TargetY = VisionVar.gearTable.getNumberArray("centerY");
//				degreesToRotateDistance = findThetaForDist(getAverageY());
				degreesToRotate = findThetaX(getAverageX());
				SmartDashboard.putNumber("AverageX", getAverageX());
				targetAngle = Sensors.navX.getYaw() + degreesToRotate;
				SmartDashboard.putNumber("DegreesToRotate", degreesToRotate);
				retrievedImage = true;
			} else if (TargetX.length == 1)
			{
				if(errorCorrection == 0)
					errorCorrection = Sensors.navX.getYaw() + 3;
				if (VisionVar.gearTable.getNumberArray("centerX")[0] >= 160.0)
				{
					turnToTargetPID(errorCorrection);
					return false;
				} else
				{
					turnToTargetPID(errorCorrection - 6);
					return false;
				}
			}
			
		} else
		{
			SmartDashboard.putNumber("TargetAngle blah", degreesToRotate);
			System.out.println(degreesToRotate);
			Drivetrain.rotateRight(targetAngle);
		}
		return true;
		
	}
	
	public static boolean autoAlignWithBoiler()
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
				return false;
			}
			degreesToRotate = findThetaX(getAverageX(), VisionVar.focalLengthB);
			targetAngle = Sensors.navX.getYaw() + degreesToRotate;
			
			double[] CurrentCenterYVision = VisionVar.boilerTable.getNumberArray("centerY");
			degreesToRotateDistance = findThetaForDist(CurrentCenterYVision[0], VisionVar.centerYVisionB,
					VisionVar.focalLengthB);
			
		} else
			turnToTarget(targetAngle);
		return true;
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
	
	public static void turnNumDegreesPID(double angleDifference)
	{
		// The motors rotate CCW (viewed from the back) when set to a positive
		// value
		
		// If the target is right of the center of the camera view,
		if (angleDifference > 1)
		{
			Drivetrain.rotateLeft(angleDifference);
//			rotateCCW(.2);
		}
		else if (angleDifference < -1){
			Drivetrain.rotateRight(angleDifference);
		}
//			rotateCW(.2);
		else// Stop when at the target
			Drivetrain.drive(0, 0);
	}
	
	public static void turnToTargetPID(double targetAngle)
	{
		double angleDelta = Sensors.navX.getYaw() - targetAngle;
		SmartDashboard.putNumber("Target Angle", targetAngle);
		SmartDashboard.putNumber("NavX", Sensors.navX.getYaw());
		turnNumDegreesPID(angleDelta);
	}
	
	public static double findThetaX(double currentCenterX)
	{
		double theta = 0.0;
		theta = Math.atan((VisionVar.centerXVision - currentCenterX) / VisionVar.focalLengthG) * (180 / Math.PI);
		retrievedImage = true;
		return theta * -1;
	}
}
