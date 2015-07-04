package discoverylab.telebot.master.core;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import discoverylab.telebot.master.core.CoreSerialReader;

/**
 * ArmSerialReader
 * @author Irvin Steve Cardenas
 *  
 * Arm Serial Reader Implementation
 * Message Format can vary depending on device interface being used.
 * For example if an Arduino-based pilot device is being used 
 * then we can specify the message format to be the following:
 * 		< MESSAGE_TYPE VAL0 VAL1 VAl2 ... >
 * If we are directly connecting from a device that has it's own embedded message
 * then we can specify the message format on the reader and implement or our parsing method.
 */
public class SerialReaderImpl extends CoreSerialReader{
	
	StringBuilder 	message 			= 	new StringBuilder();
	Boolean 		receivingMessage 	= 	false;
	
	public SerialReaderImpl(SerialPort serialPort,
			CallbackInterface callbackInterface) {
		super(serialPort, callbackInterface);
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		//TODO TEST: might need to add this for Windows machine
		// if(event.isRXCHAR() && event.getEventValue() > 0)
		if (event.isRXCHAR())
		{
			try
			{
				byte[] buffer = getSerialPort().readBytes();
				if (buffer != null)
				{
					for (byte b: buffer) {
						
						message.append((char)b);
						getCallbackInterface().callback(message.toString());
		                /*if (b == '<') {
		                    receivingMessage = true;
		                    message.setLength(0);
		                }
		                else if (receivingMessage == true) {
		                    //if (b == '\r') {
		                	if (b == '>') {
		                        receivingMessage = false;
		                        
		                        // Step 1: Transform Message Bytes to a Command String
		                        String command = message.toString();
		                        
		                        // Step 2: Clean Command String - Remove '<' and '>'
		                        String parsedCommand = parse(command);
		                        
		                        // Step 3: Transform Command String to a Data Object
		                        
		                        // Step 4: Write to Robot
		                        // writer.write(commandData);
		                        
		                        // Step 5: Log Command Data on GUI
		                        // User super class interface to call back
		                        getCallbackInterface().callback(command);
		                    }
		                    else {
		                        message.append((char)b);
		                    }
		                }*/
		            } 
				}
			}
			catch (SerialPortException exception)
			{
				System.err.println(exception.toString());
			}
		}
	}

	@Override
	public String parseMessage(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseCommand(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object parseCommand(ArrayList<String> message) {
		// TODO Auto-generated method stub
		return null;
	}

}
