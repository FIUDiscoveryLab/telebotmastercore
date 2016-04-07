package discoverylab.telebot.master.core.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static discoverylab.util.logging.LogUtils.*;

/**
 * CoreServerSocket
 * @author Irvin Steve Cardenas
 * 
 * Server Socket abstraction that deals with client socket connections in a non-blocking asynchronous manner.
 * Has a Callback Interface - SocketEventListener.
 * Classes that are interested in receiving client socket data can implement SocketEventListener
 * 
 *
 */
public class CoreServerSocket {

	public static String TAG = makeLogTag("CoreServerSocket");
	
	private SocketEventListener		eventListener;
	private ServerSocket			serverSocket;
	private Socket					clientSocket;
	
	private boolean closeSocket = false;
	private boolean isConnected = false;
	
	public CoreServerSocket(int port, SocketEventListener eventListener){
		this.eventListener = eventListener;
		
		try {
			serverSocket = new ServerSocket(port);
			LOGI(TAG, "Waiting for clients...");
		} catch (IOException e) {
			e.printStackTrace();
			LOGE(TAG, "Error Creating ServerSocket with port: " + port);
		}
	}
	/**
	 * Launch a Thread to check/wait for client to connect
	 * Without this the program blocks on:
	 * 			.accept();
	 */
	public boolean startServer() {
		
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(1);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        clientSocket = serverSocket.accept();
                        clientSocket.setKeepAlive(true);
                        LOGI(TAG, "Client Connected");
                        clientProcessingPool.submit(new ClientSocketTask(clientSocket));
                        isConnected = true;
                    }
                } catch (IOException e) {
                	LOGE(TAG, "Unable to process client request");
                    e.printStackTrace();
                }
            }
        };
        
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
        return isConnected;
	}
	
	/**
	 * Stop server
	 * @return true of both the client and the server close correctly
	 */
	public boolean stopServer(){
		boolean clientClosed = false;
		boolean serverClosed = false;
		if(!clientSocket.isClosed()){
			try {
				clientSocket.close();
				clientClosed = true;
				LOGI(TAG, "Closing Client Socket");
			} catch (IOException e) {
				LOGE(TAG, "Error Closing Client Socket");
				e.printStackTrace();
			}
		}
		
		if(!serverSocket.isClosed()){
			try {
				serverSocket.close();
				serverClosed = true;
				LOGI(TAG, "Closing Server Socket");
			} catch (IOException e) {
				LOGE(TAG, "Error Closing Server Socket");
				e.printStackTrace();
			}
		}
		
		closeSocket = true;
		
		return clientClosed && serverClosed;
	}
	/**
	 * Return CoreServerSocket CallbackInterface
	 * @return callbackInterface
	 */
	public SocketEventListener getCallbackInterface() {
		return eventListener;
	}
	
	
	/**
	 * Thread that takes care of ClientSocket connection
	 * In this case we feed 
	 * @author Irvin Steve Cardenas
	 *
	 */
	private class ClientSocketTask implements Runnable {
        private final Socket clientSocket;
        
        StringBuilder 	message 			= 	new StringBuilder();
    	Boolean 		receivingMessage 	= 	false;
        
        private ClientSocketTask(Socket clientSocket) {
        	this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            LOGI(TAG, "Got a Client!");
            
            // Do whatever required to process the client's request
            int clientMessageSize;
            String clientMessage;
            BufferedReader in = null;
            
            // STEP 1: Get Client Socket InputStream
            try {
				in = new BufferedReader(
				        new InputStreamReader(clientSocket.getInputStream()));
				LOGI(TAG, "Getting Client InputStream");
			} catch (IOException e1) {
				LOGE(TAG, "Error Getting Client InputStream");
				e1.printStackTrace();
			}
            
            // STEP 2: Read line from ClientSocket until we have to close the connection 
            // 		   or the line is null
            char [] buffer = new char[1];
            int buffSize = -1;
            try {
            	while(closeSocket != true && in.read(buffer) > 0){
                	
					for (char b: buffer) {
		                if (b == '<') {
		                    receivingMessage = true;
		                    message.setLength(0);
		                }
		                else if (receivingMessage == true) {
		                    //if (b == '\r') {
		                	if (b == '>') {
		                        receivingMessage = false;
		                        LOGI(TAG, "MESSAGE: " + message.toString());
		                        getCallbackInterface().callback(message.toString());
		                    }
		                    else {
		                        message.append((char)b);
		                    }
		                }
		            }              	
            	}
            	
            	if(closeSocket == true)
            	{
            		getCallbackInterface().callback("null");
            	}
            	
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

            // STEP 3: Lastly, close the socket
            try {
                clientSocket.close();
                LOGI(TAG, "Closing Client Socket");
            } catch (IOException e) {
            	LOGI(TAG, "ERROR Closing Client Socket");
                e.printStackTrace();
            }
        }
    }
	
	/**
	 * CallbackInterface
	 * @author Irvin Steve Cardenas
	 * 
	 * Callback interface to retrieve socket line string
	 * Purpose: Rendering socket line string
	 *
	 */
	public interface SocketEventListener{
		public void callback(String data);
	}
}