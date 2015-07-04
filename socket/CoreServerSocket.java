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
 * 
 * @author Irvin Steve Cardenas
 *
 */
public class CoreServerSocket {
	
	public static String TAG = makeLogTag("CoreServerSocket");
	
	private CallbackInterface		callbackInterface;
	private ServerSocket			serverSocket;
	private Socket					clientSocket;
	
	private boolean closeSocket = false;
	
	public CoreServerSocket(int port){
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
	public void startServer(){
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        clientSocket = serverSocket.accept();
                        clientProcessingPool.submit(new ClientSocketTask(clientSocket));
                    }
                } catch (IOException e) {
                	LOGE(TAG, "Unable to process client request");
                    e.printStackTrace();
                }
            }
        };
        
        Thread serverThread = new Thread(serverTask);
        serverThread.start();

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
	public CallbackInterface getCallbackInterface() {
		return callbackInterface;
	}
	
	/**
	 * Thread that takes care of ClientSocket connection
	 * In this case we feed 
	 * @author Irvin Steve Cardenas
	 *
	 */
	private class ClientSocketTask implements Runnable {
        private final Socket clientSocket;
        
        private ClientSocketTask(Socket clientSocket) {
        	this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            System.out.println("Got a client !");
            LOGI(TAG, "Client Connected!");
            
            // Do whatever required to process the client's request
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
            try {
				while(!closeSocket && (clientMessage = in.readLine()) != null){
					getCallbackInterface().callback(clientMessage);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
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
	public interface CallbackInterface{
		public void callback(String data);
	}
}