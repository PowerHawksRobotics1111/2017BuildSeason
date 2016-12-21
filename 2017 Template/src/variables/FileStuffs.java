package variables;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileStuffs {
	
	final static String sensorLogFileName = "sensorLogFile.1111Log";
	static String logLines = "";
	static int numLogLines = 0;
	
	public static void logLine(double time)
	{
		if (numLogLines < 10)
		{
			logLines += genLine(time);
			numLogLines++ ;
		} else
		{
			numLogLines = 0;
			writeFile(logLines, sensorLogFileName);
			logLines = "";
			logLine(time);
		}
	}
	
	private static String genLine(double time)
	{
		// TODO Usabliity, Relevant Motor voltages and their respective encoder
		// values
		// Quaternions are to do with rotations and are based of mathematics I
		// (Max) Have not yet approached and therefore may not be necessary
		
		return "Time: " + time + "\n" + "Navx: Angle: " + Sensors.navX.getAngle() + " CompassHeading: "
				+ Sensors.navX.getCompassHeading() + " X: " + Sensors.navX.getDisplacementX() + " Y: "
				+ Sensors.navX.getDisplacementY() + " Z: " + Sensors.navX.getDisplacementZ() + " FusedHeading: "
				+ Sensors.navX.getFusedHeading() + " Pitch: " + Sensors.navX.getPitch() + " QW: "
				+ Sensors.navX.getQuaternionW() + " QX: " + Sensors.navX.getQuaternionX() + " QY: "
				+ Sensors.navX.getQuaternionY() + " QZ: " + Sensors.navX.getQuaternionZ() + " yawRotRate: "
				+ Sensors.navX.getRate() + " RawAccelX: " + Sensors.navX.getRawAccelX() + " RawAccelY: "
				+ Sensors.navX.getRawAccelY() + " RawAccelZ: " + Sensors.navX.getRawAccelZ() + " RawGyroX: "
				+ Sensors.navX.getRawGyroX() + " RawGyroY: " + Sensors.navX.getRawGyroY() + " RawGyroZ: "
				+ Sensors.navX.getRawGyroZ() + " RawMagX: " + Sensors.navX.getRawMagX() + " RawMagY: "
				+ Sensors.navX.getRawMagY() + " RawMagZ: " + Sensors.navX.getRawMagZ() + " Roll: "
				+ Sensors.navX.getRoll() + " TempertureC: " + Sensors.navX.getTempC() + " UpdateCount: "
				+ Sensors.navX.getUpdateCount() + " Vx: " + Sensors.navX.getVelocityX() + " Vy: "
				+ Sensors.navX.getVelocityY() + " Vz: " + Sensors.navX.getVelocityZ() + " WorldLinearAccelerationX: "
				+ Sensors.navX.getWorldLinearAccelX() + " WorldLinearAccelerationY: "
				+ Sensors.navX.getWorldLinearAccelY() + " WorldLinearAccelerationZ: "
				+ Sensors.navX.getWorldLinearAccelZ() + " Yaw: " + Sensors.navX.getYaw() + " IsCalibrating: "
				+ Sensors.navX.isCalibrating() + " IsMagneticDisturbance: " + Sensors.navX.isMagneticDisturbance()
				+ " IsMagneometerCalibrated: " + Sensors.navX.isMagnetometerCalibrated() + " IsMoving: "
				+ Sensors.navX.isMoving() + " IsRotating: " + Sensors.navX.isRotating() + " PIDyaw: "
				+ Sensors.navX.pidGet() + "\n" + "";
	}
	
	private static void writeFile(String in, String name)
	{
		BufferedWriter bw = null;
		try
		{
			bw = new BufferedWriter(new FileWriter(name));
			bw.write(in);
			bw.flush();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		} finally
		{
			if (bw != null)
				try
				{
					bw.close();
				} catch (IOException ioe2)
				{
					// meh
				}
		}
	}
	
}
