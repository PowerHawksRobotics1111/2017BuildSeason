package variables;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class VisionVar {
	
	/**
	 * Here is list of all need measurements that pertain to the Vision methods
	 */

	public final static double centerXVision = 160.0, centerYVision = 100.0, areaVision = 6174.0, widthVision = 159.0,
			heightVision = 125.0, focalLength = 241.733, fieldOfView = 67.0, pixelWidth = 320;

	public static NetworkTable gearTable = NetworkTable.getTable("GRIP/gearContoursReport"),
			boilerTable = NetworkTable.getTable("GRIP/boilerContoursReport");
}
