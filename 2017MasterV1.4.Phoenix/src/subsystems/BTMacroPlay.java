package subsystems;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import variables.Motors;

/*Code outline to implement playing back a macro recorded in BTMacroRecord
*Be sure to read out of the same file created in BTMacroRecord
*BEWARE OF: setting your motors in a different order than in BTMacroRecord and changing motor values before
*time is up. Both issues are dealt with and explained below. Also only read/write from/to the motors 
*you have fully coded for, otherwise your code will cut out for no reason. 
*In main, the try/catch structure catches any IOExceptions or FileNotFoundExceptions. Necessary to play back
*the recorded routine during autonomous
*Dennis Melamed and Melanie (sorta, she slept)
*March 22nd, 2015
*/


public class BTMacroPlay {
	Scanner scanner;
	long startTime;

	boolean onTime = true;
	boolean reading = true;
	double nextDouble;
	

	public BTMacroPlay() throws FileNotFoundException
	{
		//create a scanner to read the file created during BTMacroRecord
		//scanner is able to read out the doubles recorded into recordedAuto.csv (as of 2015)
		scanner = new Scanner(new File(BTMain.autoFile));
		
		//let scanner know that the numbers are separated by a comma or a newline, as it is a .csv file
		scanner.useDelimiter(",|\\n");
		
		//lets set start time to the current time you begin autonomous
		startTime = -1;
	}
	
	public void read()
	{
		if(scanner.hasNext())
			System.out.println(scanner.next());
		else
			System.out.println("No more pizzs");
	}
	public void play()
	{
		//if recordedAuto.csv has a double to read next, then read it
		if (reading && (scanner.hasNextDouble()))
		{
			double t_delta;
			
			//if we have waited the recorded amount of time assigned to each respective motor value,
			//then move on to the next double value
			//prevents the macro playback from getting ahead of itself and writing different
			//motor values too quickly
			if(onTime)
			{
				//take next value
				nextDouble = scanner.nextDouble();
				System.out.println(nextDouble);
				System.out.println("red");
				if(startTime == -1)
					startTime = System.currentTimeMillis();	
			}
			
			//time recorded for values minus how far into replaying it we are--> if not zero, hold up
			t_delta = nextDouble - (System.currentTimeMillis()-startTime);
			if(!onTime)
				SmartDashboard.putBoolean("HI", true);
			//if we are on time, then set motor values
			if (t_delta <= 0)
			{
				//for 2015 robot. these are all the motors available to manipulate during autonomous.
				//it is extremely important to set the motors in the SAME ORDER as was recorded in BTMacroRecord
				//otherwise, motor values will be sent to the wrong motors and the robot will be unpredicatable
				if(scanner.hasNextDouble())
					Motors.motorDriveLeft1.set(-scanner.nextDouble());
				if(scanner.hasNextDouble())
					Motors.motorDriveLeft2.set(-scanner.nextDouble());
				if(scanner.hasNextDouble())
					Motors.motorDriveRight1.set(scanner.nextDouble());
				if(scanner.hasNextDouble())
					Motors.motorDriveRight2.set(scanner.nextDouble());
				if(scanner.hasNextDouble())
					Motors.gearHold1.setAngle(scanner.nextDouble());
				if(scanner.hasNextDouble())
					Motors.gearHold2.setAngle(scanner.nextDouble());
				if(scanner.hasNext())
					Motors.pushPiston1.set(DoubleSolenoid.Value.valueOf(scanner.next()));
//				if(scanner.hasNext())
//					Motors.pushPiston2.set(DoubleSolenoid.Value.valueOf(scanner.next()));
				
//				storage.robot.getFrontLeftMotor().setX(scanner.nextDouble());
//				storage.robot.getFrontRightMotor().setX(scanner.nextDouble());
//				storage.robot.getBackRightMotor().setX(scanner.nextDouble());
//				storage.robot.getBackLeftMotor().setX(scanner.nextDouble());
//				
//				storage.robot.getBarrelMotorLeft().setX(scanner.nextDouble());
//				storage.robot.getBarrelMotorRight().setX(scanner.nextDouble());
//				
//				storage.robot.getLeftForkLeft().setX(scanner.nextDouble());
//				storage.robot.getLeftForkRight().setX(scanner.nextDouble());
//				storage.robot.getRightForkLeft().setX(scanner.nextDouble());
//				storage.robot.getRightForkRight().setX(scanner.nextDouble());
//				
//				storage.robot.getToteClamp().set(storage.robot.getToteClamp().isExtended());
				
				//go to next double
				onTime = true;
			}
			//else don't change the values of the motors until we are "onTime"
			else
			{
				onTime = false;
			}
		}else
			end();
		//end play, there are no more values to find
		
	}
	
	//stop motors and end playing the recorded file
	public void end()
	{
		System.out.println("DIE STUPID PROGRAM");
		Motors.motorDriveLeft1.set(0);
		Motors.motorDriveLeft2.set(0);
		Motors.motorDriveRight1.set(0);
		Motors.motorDriveRight2.set(0);
//		Motors.gearHold1.setAngle(Motors.lGearStopdownAngle);
//		storage.robot.getBarrelMotorLeft().setX(0);
//		storage.robot.getBarrelMotorRight().setX(0);
//		
//		storage.robot.getLeftForkLeft().setX(0);
//		storage.robot.getLeftForkRight().setX(0);
//		storage.robot.getRightForkLeft().setX(0);
//		storage.robot.getRightForkRight().setX(0);
		//all this mess of a method does is keep the piston in the same state it ended in
		//if you want it to return to a specific point at the end of auto, change that here
		//storage.robot.getToteClamp().set(storage.robot.getToteClamp().isExtended());
		
		if (reading)
		{
			reading = false;
			scanner.close();
			System.out.println(scanner == null);

		}
		
	}
	
}