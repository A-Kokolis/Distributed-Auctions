package edu.auctioneer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnected extends Thread {

	private Socket connection;
	private String serverName;

	private InputStream sockInput;
	private OutputStream out;

	private String clientName = null;

	private int step = 0;
	private long start = System.currentTimeMillis();

	private ClientConnected childClientConnect = null;

	ClientConnected(Socket connection, String serverName) {
		this.connection = connection;
		this.serverName = serverName;
	}

	public void run() {
		try {
			sockInput = connection.getInputStream();
			out = connection.getOutputStream();

			switch (step) {
			case 0:
				/*
				 * saveBittersName(); sentItem();
				 */
				byte[] buf = new byte[1024];
				int bytes_read = 0;
				// while (!connection.isClosed()) {
				bytes_read = sockInput.read(buf, 0, buf.length);
				// String[] data = new String(buf, 0, bytes_read).split(" ");
				clientName = new String(buf, 0, bytes_read);
				System.out.println(clientName);
				boolean inserted = DataBaseInteractions.adduser(clientName,
						serverName, 0);
				if (inserted) {
					String message = "ok irthe\n";
					byte[] bytemessage = message.getBytes();
					out.write(bytemessage, 0, bytemessage.length);
					out.flush();
				} else {
					String message = "user already exists\n";
					byte[] bytemessage = message.getBytes();
					out.write(bytemessage, 0, bytemessage.length);
					out.flush();
					connection.close();
				}

				break;
			case 1:
				sendItem();
				break;
			case 2:
				startBidding();
				break;
			default:
				break;
			}

			// }

			// connection.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendItem() {
		try {
			String message = null;
			if (this.serverName.equals("server1")) {
				message = "Item name= " + DataBaseInteractions.item_name1
						+ " Description= " + DataBaseInteractions.description1
						+ " Initial price="
						+ DataBaseInteractions.initial_price1 + "\n";
			} else {
				message = "Item name= " + DataBaseInteractions.item_name2
						+ " Description= " + DataBaseInteractions.description2
						+ " Initial price="
						+ DataBaseInteractions.initial_price2 + "\n";
			}
			byte[] bytemessage = message.getBytes();
			out.write(bytemessage, 0, bytemessage.length);
			out.flush();

			byte[] buf = new byte[1024];
			int bytes_read = 0;
			long start = System.currentTimeMillis();
			boolean answered = false;
			while (System.currentTimeMillis() - start < DataBaseInteractions.L) {
				if (sockInput.available() > 0 && !answered) {
					bytes_read = sockInput.read(buf, 0, buf.length);
					String response = new String(buf, 0, bytes_read);
					System.out.println(response);
					if (response.trim().equalsIgnoreCase("interested")) {
						Main.addToInterestedList(this);
						answered = true;
					}
				}
			}
			message = "Time for sending if you are intrested is up!\n";
			bytemessage = message.getBytes();
			out.write(bytemessage, 0, bytemessage.length);
			out.flush();

		} catch (IOException e) {

			if (this.serverName.equals("server1")) {
				System.out.println("Removing " + this.clientName
						+ " from clientServer1 and db1");
				Main.clientServer1.remove(this);
				DataBaseInteractions.deleteFromRegistration1(this);
			} else {
				System.out.println("Removing " + this.clientName
						+ " from clientServer2 and db2");
				Main.clientServer2.remove(this);
				DataBaseInteractions.deleteFromRegistration2(this);
			}
			// e.printStackTrace();
		}

	}

	public void startBidding() throws IOException {
		// String message = "Start bidding for item!\n";
		// byte[] bytemessage = message.getBytes();
		// out.write(bytemessage, 0, bytemessage.length);
		// out.flush();

		String message = "Start bidding for item!\nInitial price: "
				+ DataBaseInteractions.initial_price1 + " Holder is none\n";
		byte[] bytemessage = message.getBytes();
		out.write(bytemessage, 0, bytemessage.length);
		out.flush();

		byte[] buf = new byte[1024];
		int bytes_read = 0;
		start = System.currentTimeMillis();
		long oldStart = start;
		Integer bid = 0;
		boolean flag = true;
		while (System.currentTimeMillis() - start < DataBaseInteractions.L
				|| BiddingResults.valueIsChanging) {
			// if (oldStart != start) {
			// System.out.println("Allaxe apo " + oldStart + "se " + start);
			// oldStart = start;
			// }
			// if (System.currentTimeMillis() - start >= 55000 && flag) {
			// System.out.println(System.currentTimeMillis() - start
			// + ", 30000");
			// flag = false;
			// }
			if (sockInput.available() > 0) {
				bytes_read = sockInput.read(buf, 0, buf.length);
				String response = new String(buf, 0, bytes_read);
				System.out.println(response);
				bid = Integer.parseInt(response.trim());
				BiddingResults.checkAndUpdateBid(bid, clientName);
			}
		}
		message = "Stop bidding!\n";
		bytemessage = message.getBytes();
		out.write(bytemessage, 0, bytemessage.length);
		out.flush();

	}

	public void informValueChanged(String name, Integer biggestBid) {
		try {
			childClientConnect.setStart(System.currentTimeMillis());
			// out = connection.getOutputStream();
			// System.out.println(name);
			// name=name.replaceAll("\n", "");
			String message = "The highest bid  is " + biggestBid + " from "
					+ name;
			byte[] bytemessage = message.getBytes();

			out.write(bytemessage, 0, bytemessage.length);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void informForTheWinner(String name, Integer biggestBid) {
		try {
			String message = "Time for bidding is up!\n";
			byte[] bytemessage = message.getBytes();
			out.write(bytemessage, 0, bytemessage.length);
			out.flush();

			if (name!=null) {
				message = "With a bid of " + biggestBid + " winner is " + name;
				bytemessage = message.getBytes();
			} else {
				message = "No one has bidded and won the item\n";
				bytemessage = message.getBytes();
			}
			out.write(bytemessage, 0, bytemessage.length);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void informForNextRound() {
		try {
			String message = "No winner yet...Going for next round\n";
			byte[] bytemessage = message.getBytes();

			out.write(bytemessage, 0, bytemessage.length);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void informAuctionCompleted() {
		try {
			String message = "Auction is completed\n";
			byte[] bytemessage = message.getBytes();

			out.write(bytemessage, 0, bytemessage.length);
			out.flush();
		} catch (IOException e) {
			// e.printStackTrace();
		}

	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public Socket getConnection() {
		return connection;
	}

	public void setConnection(Socket connection) {
		this.connection = connection;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public ClientConnected getChildClientConnect() {
		return childClientConnect;
	}

	public void setChildClientConnect(ClientConnected childClientConnect) {
		this.childClientConnect = childClientConnect;
	}

}
