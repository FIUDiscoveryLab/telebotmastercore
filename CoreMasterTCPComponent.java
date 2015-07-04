package discoverylab.telebot.master.core;

import discoverylab.telebot.master.core.socket.CoreServerSocket;

public abstract class CoreMasterTCPComponent {

	private CoreServerSocket serverSocket;
	
	public CoreMasterTCPComponent(int portNumber){
		serverSocket = new CoreServerSocket(portNumber);
	}
	
	public void initialize(){
		serverSocket.startServer();
	}
	
	public boolean shutdown(){
		return serverSocket.stopServer();
	}
}
