package subsystems;

import java.io.FileNotFoundException;
import java.io.IOException;

import variables.Joysticks;

public class BTMain
{
	boolean recorderCheck = false;
	boolean isRecording = false;
	//autoNumber defines an easy way to change the file you are recording to/playing from, in case you want to make a
	//few different auto programs
	public static String autoFile;
	//autoFile is a global constant that keeps you from recording into a different file than the one you play from
	BTMacroPlay player;
    BTMacroRecord recorder;

    public BTMain(String autoSelected, boolean record)
    {
    	 autoFile = "/home/lvuser/auto/" + autoSelected;
    	 System.out.println(autoFile);
    	 recorder = null;

    	 if(record)
    	 {
	    	try
	    	{
	    		player = new BTMacroPlay();
	    	}
	    	catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
    	 }
    	 else
    	 {
	         try {
	 			recorder = new BTMacroRecord();
	 		} 
	 		catch (IOException e) 
	 		{
	 			e.printStackTrace();
	 		}
    	 }
    }
	
	public void robotInit()
    {
		//do whatevah you do here
	}
	
    public void autonomous()
    {
    	//during autnomous, create new player object to read recorded file
    	
    	
		//if not, print out an error
		
    	
    	//once autonomous is enabled

			//as long as there is a file you found, then use the player to scan the .csv file
			//and set the motor values to their specific motors
			if (player != null)
			{
				System.out.println("recorder.autonomous");
				player.play();
			}
			//do nothing if there is no file
		
		//if there is a player and you've disabled autonomous, then flush the rest of the values
		//and stop reading the file
		
    	
    }
    
    boolean held = false;
    
    public void operatorControl() throws IOException
    {
		//lets make a new record object, it will feed the stuff we record into the .csv file
    	
		

    		//the statement in this "if" checks if a button you designate as your record button 
    		//has been pressed, and stores the fact that it has been pressed in a variable
//    		if (!held &&Joysticks.joyDrive.getRawButton(Joysticks.Buttons.recordButton))
//    			held = true;
//    		
//    		if(held && !Joysticks.joyDrive.getRawButton(Joysticks.Buttons.recordButton))
//    		{isRecording = !isRecording;
//    		held = false;}
    		 
			//if our record button has been pressed, lets start recording!
			if (Joysticks.joyDrive.getRawButton(Joysticks.Buttons.recordButton))
			{
    			try
    			{

    				//if we succesfully have made the recorder object, lets start recording stuff
    				//2220 uses a storage object that we can get motors values, etc. from.
    				//if you don't need to pass an object like that in, modify the methods/classes
    				if(recorder != null)
    				{
    					recorder.record();
    				}
    			
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
			else
			{
				recorder.writer.flush();
//				if(recorder.writer != null)
//					recorder.end();
			}
		
		//once we're done recording, the last thing we'll do is clean up the recording using the end
		//function. more info on the end function is in the record class
    	
    }
    
    public void end()
    {
    	try 
    	{
    		if(recorder != null)
    		{
    			recorder.end();
    		}
    		
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
    }
	
    public void disabled()
    {

    }
}

