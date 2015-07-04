package discoverylab.telebot.master.core;

import static discoverylab.util.logging.LogUtils.*;
import TelebotDDSCore.DDSCommunicator;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.DataReaderImpl;
import com.rti.dds.topic.Topic;

import jssc.SerialPort;

/**
 * CoreMasterDeviceComponent
 * @author Irvin Steve Cardenas
 *
 */
public abstract class CoreMasterDeviceComponent {
	
	public static String TAG = makeLogTag("CoreMasterDeviceComponent");

// Serial
	private 	SerialPort	serialPort;
	protected 	Boolean 	serialConnected 		= false;
	private 	Boolean 	serialPortsAvailable 	= false;
	private 	String 		serialPortName;
	private 	Integer 	baudRate;
	private 	Integer 	dataBits;
	private 	Integer 	stopBits; 
	private 	Integer 	parityType;
	private 	Integer 	eventMask;
	

//  DDS 
	private 		DDSCommunicator 		communicator;
	private static 	Topic 					topic 			= null;
	private static 	DataReaderImpl 			reader 			= null;
//	private static 	CoreDataReaderAdapter	listener		= null;
	InstanceHandle_t instance_handle 						= InstanceHandle_t.HANDLE_NIL;
	
	public CoreMasterDeviceComponent(){
		
	}
	
	public CoreMasterDeviceComponent(SerialPort serialPort){
		
	}
	
	public boolean initiateTransmissionProtocol(String topicName){
		return false;
	}
}