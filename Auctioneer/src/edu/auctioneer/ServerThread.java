package edu.auctioneer;


public class ServerThread extends Thread {

	private int port;
	private String serverName;
	
	private ServerConnection serverConnection;
//	private ServerAuct serverAuct;

	public ServerThread(int port, String serverName) {
		this.port = port;
		this.serverName = serverName;

	}

	public void run() {
		serverConnection= new ServerConnection(port, serverName);
		serverConnection.start();
		
		/*serverAuct = new ServerAuct(0);
		serverAuct.start();*/
		
	}
	
	/*public void sentItem(){
//		Set<Thread> threadSet = ThreadConnect.getAllStackTraces().keySet();
		for(Thread t : threadSet){
			if(t != null && t instanceof ThreadConnect){
				try {
					((ThreadConnect)t).sentItem();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
	}*/

}
