package bidder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AutoClient {
	final String host = "147.102.1.70";
	static int portNumber = 0;
	static String clientName;

	static String itemDescription; // the description of the current item so
									// that the user can see it at any time

	PrintWriter out;
	static Socket socket;
	static String clientDecision;
	static boolean sold = false;
	static boolean auctionCompleted = false;
	public boolean sendBid = false;

	public static ArrayList<String> interestedList = new ArrayList<String>();
	public static ArrayList<String> stayInAuction = new ArrayList<String>();
	public static ArrayList<Integer> bidList = new ArrayList<Integer>();
	public static ArrayList<Integer> sleepList = new ArrayList<Integer>();

	public static void main(String args[]) throws IOException,
			InterruptedException {

		String userInput;

		readListsFromFile(args[0]);

//		System.out.println("port= " + portNumber + " name= " + clientName);

//		for (int i = 0; i < interestedList.size(); i++) {
//			System.out.println("" + interestedList.get(i));
//		}
//		for (int i = 0; i < stayInAuction.size(); i++) {
//			System.out.println("" + stayInAuction.get(i));
//		}
//		for (int i = 0; i < bidList.size(); i++) {
//			System.out.println("" + bidList.get(i));
//		}
//		for (int i = 0; i < sleepList.size(); i++) {
//			System.out.println("" + sleepList.get(i));
//		}

		AutoClient autoClient = new AutoClient();
		autoClient.connectToServer();

		while (true) {

			clientDecision = null;
			itemDescription = null;
			autoClient.takeRequest();
			if (auctionCompleted) {
				break;
			}

			System.out.println("request of interest phase ended!!");

			if (clientDecision != null && clientDecision.equals("interested")) {
				autoClient.bid();
				System.out.println("bid phase for the current item is over");
			}

			System.out
					.println("If you want to stay in the auction press yes orelse press no!");
			userInput = stayInAuction.get(0);
			stayInAuction.remove(0);
			System.out.println(userInput);

			if (userInput.equals("no")) {
				break;
			}
		}
		socket.close();
		System.out.println("Thank you for participating in the auct!!");
	}

	private static void readListsFromFile(String file) {
		try {
			BufferedReader input = new BufferedReader(new FileReader(file));
			String line;
			portNumber = Integer.parseInt(input.readLine().trim());
			clientName = input.readLine().trim();

			int loopNum = Integer.parseInt(input.readLine().trim());

			for (int i = 0; i < loopNum; i++) {
				interestedList.add(input.readLine().trim());
			}
			for (int i = 0; i < loopNum + 1; i++) {
				stayInAuction.add(input.readLine().trim());
			}
 
			String[] parts = null;
			while ((line = input.readLine()) != null) {
				parts = line.split(" ");
				if (parts[0].equals("bid")) {
					bidList.add(Integer.parseInt(parts[1]));
				} else {
					sleepList.add(Integer.parseInt(parts[1]));
				}
			}
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void bid() throws IOException {
		String userInput;
		boolean isInt = true;
		InputStream sockInput;

		ArrayList<String> allbids = new ArrayList<String>();

		sockInput = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				sockInput));
		String serverInput = "";

		System.out.println("Bidding phase has started");
		System.out
				.println("press list_history for bidding history or description for item description");

		serverInput = reader.readLine();
		System.out.println("server says: " + serverInput);

		serverInput = reader.readLine();
		System.out.println("server says: " + serverInput);

		InputStreamReader fileInputStream = new InputStreamReader(System.in);
		BufferedReader bufferedReader = new BufferedReader(fileInputStream);

		(new Thread() {
			public void run() {
				try {
					if (sleepList.size() > 0) {
						sleep(sleepList.get(0));
						sleepList.remove(0);
						sendBid = true;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		while (true) {
			int inputInt;

			if (sendBid) {
				if (bidList.size() > 0) {
					inputInt = bidList.get(0);
					bidList.remove(0);
					out.println(inputInt);
					System.out.println(""+inputInt);
					sendBid = false;

					(new Thread() {
						public void run() {
							try {
								if (sleepList.size() > 0) {
									sleep(sleepList.get(0));
									sleepList.remove(0);
									sendBid = true;
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}

			if (reader.ready()) {
				serverInput = reader.readLine();
				System.out.println("server says: " + serverInput);

				if (serverInput.contains("Time")) {
					serverInput = reader.readLine();
					System.out.println("server says: " + serverInput);
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

		clientDecision = interestedList.get(0);
		out.println(clientDecision);
		System.out.println(clientDecision);
		interestedList.remove(0);

		while (true) {

			if (sockInput.available() > 0) {
				bytes_read = sockInput.read(buf, 0, buf.length);
				serverMessage = new String(buf, 0, bytes_read);
				System.out.println("server says:" + serverMessage);
				break;
			}
		}

	}

	private void connectToServer() {
		InputStream sockInput;

		System.out.println("Enter your username:");

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
