package bidder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
	final String host = "147.102.1.70";
	int portNumber = 0;
	String clientName;
	static String itemDescription; // the description of the current item so
									// that the
	// user can see it anytime
	PrintWriter out;
	static Socket socket;
	static String clientDecision;
	static boolean sold = false;
	static boolean auctionCompleted = false;

	public static void main(String args[]) throws IOException,
			InterruptedException {

		InputStreamReader fileInputStream = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(fileInputStream);
		String userInput;

		Client client = new Client();
		client.connectToServer();

		while (true) {

			sold = false;
			clientDecision = null;
			itemDescription = null;
			client.takeRequest();
			if (auctionCompleted) {
				break;
			}

			System.out.println("request of interest phase ended!!");

			if (clientDecision != null && clientDecision.equals("interested")) {
//				int biddingCount = 0;
//				while (biddingCount < 5 && !sold) {
					client.bid();
//					biddingCount++;
//				}
//				if (biddingCount == 5) {
//					System.out.println("The item was not sold after all...");
//				}
				System.out.println("bid phase for the current item is over");
			}

			System.out
					.println("If you want to stay in the auction press yes orelse press no!");
			userInput = bufferedReader.readLine();

			if (userInput.equals("no")) {
				break;
			}
		}
		socket.close();
		System.out.println("Thank you for participating in the auct!!");
	}

	private void bid() throws IOException {
		String userInput;
		boolean isInt = true;
		InputStream sockInput;

		ArrayList<String> allbids = new ArrayList<String>();

		sockInput = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				sockInput));

		byte[] buf = new byte[1024];
		int bytes_read = 0;

		String serverInput = "";

		System.out.println("Bidding phase has started");
		System.out
				.println("press list_history for bidding history or description for item description");
		// bytes_read = sockInput.read(buf, 0, buf.length);
		// System.out.println("server says:" + new String(buf, 0, bytes_read));

		serverInput = reader.readLine();
		System.out.println("server says: " + serverInput);
		
		serverInput = reader.readLine();
		System.out.println("server says: " + serverInput);

		InputStreamReader fileInputStream = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(fileInputStream);

		while (true) {
			if (bufferedReader.ready()) {
				userInput = bufferedReader.readLine();
				int inputInt;
				try {
					inputInt = Integer.parseInt(userInput);
				} catch (Exception e) {
					if (userInput.equals("list_history")) {
						list_high_bid(allbids);
					} else if (userInput.equals("description")) {
						System.out.println("Item description: "
								+ itemDescription);
					} else {
						System.out.println("Wrong input!");
					}
					isInt = false;
				}
				if (isInt) {
					out.println(userInput);
				}
				isInt = true;
			}
			if (reader.ready()) {
				// bytes_read = sockInput.read(buf, 0, buf.length);
				// String serverInput = new String(buf, 0, bytes_read);
				// System.out.println("server says:" + serverInput);
				serverInput = reader.readLine();
				System.out.println("server says: " + serverInput);

				if (serverInput.contains("Time")) {
//					while (true) {
//						// bytes_read = sockInput.read(buf, 0, buf.length);
//						// serverInput = new String(buf, 0, bytes_read);
//						// System.out.println("server says:" + serverInput);
//						if (sockInput.available() > 0) {
							serverInput = reader.readLine();
							System.out.println("server says: " + serverInput);
//							break;
//						}
//						if (!serverInput.contains("No")) {
//							sold = true;
//						}
//					}
					break;
				}
				allbids.add(serverInput);
			}
		}
	}

	private void list_high_bid(ArrayList<String> allbids) {

		for (int i = 0; i < allbids.size(); i++)
			System.out.println((i + 1) + ":" + allbids.get(i));
	}

	private void takeRequest() throws IOException, InterruptedException {
		String userInput;
		InputStream sockInput;
		String serverMessage;

		sockInput = socket.getInputStream();
		byte[] buf = new byte[1024];
		int bytes_read = 0;

		bytes_read = sockInput.read(buf, 0, buf.length);
		itemDescription = new String(buf, 0, bytes_read);
		if (itemDescription.contains("completed")) {
			System.out.println("Auction has completed...exiting");
			auctionCompleted = true;
			return;
		}
		System.out.println("server says:" + itemDescription);
		System.out
				.println("If you are interested about the item write : interested");

		InputStreamReader fileInputStream = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(fileInputStream);

		while (true) {
			if (bufferedReader.ready()) {

				userInput = bufferedReader.readLine();
				out.println(userInput);
				clientDecision = userInput;
			}
			if (sockInput.available() > 0) {
				bytes_read = sockInput.read(buf, 0, buf.length);
				serverMessage = new String(buf, 0, bytes_read);
				System.out.println("server says:" + serverMessage);
				break;
			}
		}

	}

	private void sendToServer() {
		InputStream sockInput;
		BufferedReader userInputBR;
		String userInput;
		try {
			sockInput = socket.getInputStream();
			byte[] buf = new byte[1024];
			int bytes_read = 0;

			while (true) {
				bytes_read = sockInput.read(buf, 0, buf.length);
				System.out.println("server says:"
						+ new String(buf, 0, bytes_read));
				// System.out.println("server says:" + br.readLine());

				userInputBR = new BufferedReader(new InputStreamReader(
						System.in));
				userInput = userInputBR.readLine();

				out.println(userInput);

				if ("exit".equalsIgnoreCase(userInput)) {
					socket.close();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void connectToServer() {
		Scanner sc = new Scanner(System.in);
		InputStream sockInput;

		while (portNumber != 5558 && portNumber != 5559) {
			System.out
					.println("Please enter the connection port: 5558 or 5559");
			System.out.println("" + portNumber);
			try {
				portNumber = sc.nextInt();
			} catch (InputMismatchException exception) {
				System.out.println("This is not an integer!");
				sc.next();
			}
		}

		System.out.println("Enter your username:");
		clientName = sc.next();

		System.out.println("clientName= " + clientName);
		System.out.println("Creating socket to '" + host + "' on port "
				+ portNumber);

		try {
			socket = new Socket(host, portNumber);
			System.out.println("connected:" + socket.isConnected());

			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				out.println(clientName);
				sockInput = socket.getInputStream();
				byte[] buf = new byte[1024];
				int bytes_read = 0;
				bytes_read = sockInput.read(buf, 0, buf.length);
				System.out.println("server says:"
						+ new String(buf, 0, bytes_read));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
