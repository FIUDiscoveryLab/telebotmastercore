package discoverylab.telebot.master.core.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static discoverylab.util.logging.LogUtils.*;

public class CoreServerSocket {
	
	public static String TAG = makeLogTag("CoreServerSocket");
	
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
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
                        clientProcessingPool.submit(new ClientTask(clientSocket));
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
	 * Thread that takes care of ClientSocket connection
	 * In this case we feed 
	 * @author Irvin Steve Cardenas
	 *
	 */
	private class ClientTask implements Runnable {
        private final Socket clientSocket;

        private ClientTask(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            System.out.println("Got a client !");
            LOGI(TAG, "Client Connected!");
            // Do whatever required to process the client's request
            
            while(!closeSocket){
            }

            try {
                clientSocket.close();
                LOGI(TAG, "Closing Client Socket");
            } catch (IOException e) {
            	LOGI(TAG, "ERROR Closing Client Socket");
                e.printStackTrace();
            }
        }
    }
	
	public interface CoreServerSocketInterface{
		
	}
}
