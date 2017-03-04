package variables;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class VisionVar
{
	
	/**
	 * Here is list of all need measurements that pertain to the Vision methods
	 */
	
	public final static double centerXVision = 151, centerYVisionB = 100.0, centerYVisionG = 120.0, areaVision = 6174.0,
			widthVision = 159.0, heightVision = 125.0, focalLengthB = 241.733, focalLengthG = 190.681,
			fieldOfViewB = 67.0, fieldOfViewG = 80.0, pixelWidth = 320;
	
	public static NetworkTable gearTable = NetworkTable.getTable("GRIP/contoursReport"),
			boilerTable = NetworkTable.getTable("GRIP/boilerContoursReport");
}
