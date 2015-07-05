package discoverylab.telebot.master.core;

import static discoverylab.util.logging.LogUtils.*;

import java.util.ArrayList;

import javax.swing.JComponent;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

/**
 * CoreSerialReader
 * @author Irvin Steve Cardenas
 * 
 * Core Serial Reader handles Serial Connection
 */
public abstract class CoreSerialReader implements SerialPortEventListener{
	
	public static String TAG = makeLogTag("CoreSerialReader");
	
	private CallbackInterface callbackInterface;
	
	private SerialPort serialPort;
	
	public CoreSerialReader(SerialPort serialPort, CallbackInterface callbackInterface ){
		this.setSerialPort(serialPort);
		this.setCallbackInterface(callbackInterface);
	}
	@Override
	/**
	 * If using a GUI that needs to update based on the serial values.
	 * The callback method should be called within this method
	 */
	public abstract void serialEvent(SerialPortEvent event);
	
	public SerialPort getSerialPort() {
		return serialPort;
	}
	public void setSerialPort(SerialPort serialPort) {
		this.serialPort = serialPort;
	}

	public CallbackInterface getCallbackInterface() {
		return callbackInterface;
	}
	public void setCallbackInterface(CallbackInterface callbackInterface) {
		this.callbackInterface = callbackInterface;
	}
	
	/**
	 * Parse Default Message Format
	 * Removes the closing and ending brackets
	 * Message:
	 * 			< MESSAGE_TYPE Val1 Val2 Val3.. >
	 * @param message
	 * @return
	 */
	public String parse(String message){
		return new StringBuilder(message.substring(1, message.length()-1)).toString();
	}
	
	/**
	 * Parses data read from serial
	 * @param str
	 * @return
	 */
	public abstract String parseMessage(String str);
	
	public abstract Object parseCommand(String str);
	
	public abstract Object parseCommand(ArrayList<String> message);

	/**
	 * CallbackInterface
	 * @author Irvin Steve Cardenas
	 * 
	 * Callback interface to retrieve serial data
	 * Purpose: Rendering serial data in GUI
	 *
	 */
	public interface CallbackInterface{
		public void callback(String data);
	}
}
