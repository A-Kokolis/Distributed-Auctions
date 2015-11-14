package edu.auctioneer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnection extends Thread{
	
	private ServerSocket server;
	private Socket connection;
	private ClientConnected tc = null;
	
	private int port;
	private String serverName;
	
	public ServerConnection(int port, String serverName) {
		this.port = port;
		this.serverName = serverName;
	}
	
	public void run() {

		try {
			server = new ServerSocket(port);
			Logging.writeLineToLog(serverName+" started and is ready to accept clients");
			System.out.println(serverName + ": about to accept.....");
			while (true) {
				connection = server.accept();
				System.out.println("accept");
				tc = new ClientConnected(connection, serverName);
				tc.start();
				if(serverName.equals("server1")){
					Main.clientServer1.add(tc);
				}else{
					Main.clientServer2.add(tc);
				}
					
					
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
