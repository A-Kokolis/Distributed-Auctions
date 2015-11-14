package edu.auctioneer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ThreadConnect extends Thread {

	private Socket connection;
	private String serverName;

	private InputStream sockInput;
	private OutputStream out;

	ThreadConnect(Socket connection, String serverName) {
		this.connection = connection;
		this.serverName = serverName;
	}

	public void run() {
		try {

			sockInput = connection.getInputStream();
			out = connection.getOutputStream();
			/*saveBittersName();
			sentItem();*/
			byte[] buf = new byte[1024];
			int bytes_read = 0;
			// while (!connection.isClosed()) {
			bytes_read = sockInput.read(buf, 0, buf.length);
			String[] data = new String(buf, 0, bytes_read).split(" ");
			System.out.println(new String(buf, 0, bytes_read));

			String message = "ok irthe";
			byte[] bytemessage = message.getBytes();
			out.write(bytemessage, 0, bytemessage.length);
			out.flush();
			// }

//			connection.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void sentItem() throws IOException{
		String message = "car";
		byte[] bytemessage = message.getBytes();
		out.write(bytemessage, 0, bytemessage.length);
		out.flush();
		
	}

}
