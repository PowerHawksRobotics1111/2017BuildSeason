package subsystems;

import java.io.FileWriter;
import java.io.IOException;

import variables.Motors;

/*
*This macro records all the movements you make in teleop and saves them to the file you specify.
*make sure you record every variable you need, if you dont record the value from a motor or a solenoid,
*you won't be able to play it back. It records it in "frames" that contain a value for each output 
* you want to use during teleop
*BE AWARE: write into the same file as you do in the Play macro
*BE AWARE: Only write/read the motors/other things that you actually have fully created in 
*your code. Otherwise you'll lose robot code randomly with no reason
*In main, the try/catch structure catches any IOExceptions or FileNotFoundExceptions. Necessary to play back
*the recorded routine during autonomous
*Dennis Melamed, Melanie Quick
*22 March, 2015
*/


public class BTMacroRecord {
	
	//this object writes values into the file we specify
	FileWriter writer;
	
	long startTime;
	
	public BTMacroRecord() throws IOException
	{
			//record the time we started recording
			startTime = -1;
			
			//put the filesystem location you are supposed to write to as a string 
			//as the argument in this method, as of 2015 it is /home/lvuser/recordedAuto.csv
			writer = new FileWriter(BTMain.autoFile);
	}
	

	public void record() throws IOException
	{
		if(writer != null)
		{
			if(startTime == -1)
				startTime = System.currentTimeMillis();
			System.out.println("Writing");
		//start each "frame" with the elapsed time since we started recording
		writer.append("" + (System.currentTimeMillis()-startTime));
		
		//in this chunk, use writer.append to add each type of data you want to record to the frame
		//the 2015 robot used the following motors during auto
		
		//drive motors
		writer.append("," + Motors.motorDriveLeft1.getOutputVoltage());
		writer.append("," + Motors.motorDriveLeft2.getOutputVoltage());
		writer.append("," + Motors.motorDriveRight1.getOutputVoltage());		
		writer.append("," + Motors.motorDriveRight2.getOutputVoltage());
		writer.append("," + Motors.gearHold1.getAngle());
		writer.append("," + Motors.gearHold2.getAngle());
		writer.append("," + Motors.pushPiston1.get() + "\n");
//		writer.append("," + Motors.pushPiston2.get() + "\n");
		
		
		//barrel motors
//		writer.append("," + Motors.gearHold1.getAngle());
//		writer.append("," + Motors.gearHold2.getAngle());;
//		/*
//		 * THE LAST ENTRY OF THINGS YOU RECORD NEEDS TO HAVE A DELIMITER CONCATENATED TO 
//		 * THE STRING AT THE END. OTHERWISE GIVES NOSUCHELEMENTEXCEPTION
//		 */ 
//		
//		//this records a true/false value from a piston
//		writer.append("," + Motors.pushPiston1.get());
//		writer.append("," + Motors.pushPiston2.get() + "\n");
		//writer.append("," + storage.robot.getToteClamp().isExtended() + "\n");
		
		/*
		 * CAREFUL. KEEP THE LAST THING YOU RECORD BETWEEN THESE TWO COMMENTS AS A
		 * REMINDER TO APPEND THE DELIMITER
		 */
		}
	}
	
	
	//this method closes the writer and makes sure that all the data you recorded makes it into the file
	public void end() throws IOException
	{
		if(writer !=null)
		{
			System.out.println("flushing!");
		writer.flush();
		writer.close();
		}
	}
}