package edu.auctioneer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.fabric.xmlrpc.base.Data;

public class Main {

	public static List<ClientConnected> clientServer1 = new ArrayList<ClientConnected>();
	public static List<ClientConnected> clientServer2 = new ArrayList<ClientConnected>();
	public static List<ClientConnected> interestedclientServer1 = new ArrayList<ClientConnected>();
	public static List<ClientConnected> interestedclientServer2 = new ArrayList<ClientConnected>();

	public static int biddingCount=0;
	
	public static void main(String[] args) throws InterruptedException,
			IOException {
		
		Logging.getInstance();
		
		ServerThread server1 = new ServerThread(5558, "server1");
		server1.start();
		ServerThread server2 = new ServerThread(5559, "server2");
		server2.start();

		DataBaseInteractions.getInstance();
		BiddingResults.getInstance();
		
		SynchronizeServer synchronizeServer = new SynchronizeServer();
		
		DataBaseInteractions.addItemsToDatabase(); //insert items to database from file. Comment out this line if you have
												   // you want to insert items manually
		
		System.out.println("Items have been added to Database");
		System.out.println("The value of L is "+DataBaseInteractions.L);
		while (true) {
			Thread.sleep(DataBaseInteractions.L);
			try {
				DataBaseInteractions.retrieve();
				if (DataBaseInteractions.auctionCompleted == true) {
					System.out.println("Auction has completed..No more items");
					break;
				}
			} catch (SQLException e) {
				System.out
						.println("Retrieve method failed to fetch from database");
				e.printStackTrace();
			}
			synchronizeServer.synchSendItem();
			if (interestedclientServer1.size() != 0
					|| interestedclientServer2.size() != 0) {
				biddingCount = 0;
				while (biddingCount < 5) {
					synchronizeServer.synchStartBidding();
					if (BiddingResults.getBidHolder() != null) {
						try {
							DataBaseInteractions.remove();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						break;
					} else {
						biddingCount++;
						try {
							DataBaseInteractions.update();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						try {
							DataBaseInteractions.retrieve(); // retrieve again
																// for the new
																// values of the
																// item
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
				if (biddingCount == 5) {
					try {
						DataBaseInteractions.remove();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			interestedclientServer1.clear();
			interestedclientServer2.clear();
			BiddingResults.clear();
		}

		for (ClientConnected clientConnected : clientServer1) {
			clientConnected.informAuctionCompleted();
		}

		for (ClientConnected clientConnected : clientServer2) {
			clientConnected.informAuctionCompleted();
		}
		
		Logging.closeLogFile();

		// katharise lista, katharise Bidding results

		// for(ClientConnected clientConnected: interestedclientServer1){
		// System.out.println(clientConnected.getClientName());
		// }
		// for(ClientConnected clientConnected: interestedclientServer2){
		// System.out.println(clientConnected.getClientName());
		// }

		/*
		 * for(ClientConnected tc: tcs){ tc.sentItem(); }
		 * 
		 * Thread.sleep(60*1000);
		 * 
		 * List<ClientConnected> peopleThatAreIntrested = new
		 * ArrayList<ClientConnected>(); for(ClientConnected tc: tcs){
		 * if(tc.getIsIntrested()){ peopleThatAreIntrested.add(tc); } }
		 * 
		 * for(ClientConnected tc: peopleThatAreIntrested){ tc.startBidding();
		 * 
		 * }
		 */

		/*
		 * server1.sentItem(); server2.sentItem();
		 */

		/* DataBaseInteractions baseInteractions = */

	}

	public static synchronized void addToInterestedList(
			ClientConnected clientConnected) {
		if (clientConnected.getServerName().endsWith("server1")) {
			interestedclientServer1.add(clientConnected);
		} else {
			interestedclientServer2.add(clientConnected);
		}
	}

}
