package edu.auctioneer;

import java.sql.SQLException;

public class SynchronizeServer {

	public void synchSendItem() {

		ServerAuct auct1 = new ServerAuct("server1", 1, Main.clientServer1);
		auct1.start();
		// auct1.sendItemToAll(Main.clientServer1);

		ServerAuct auct2 = new ServerAuct("server2", 1, Main.clientServer2);
		auct2.start();
		// auct2.sendItemToAll(Main.clientServer2);

		try {
			auct1.join();
			auct2.join();
			System.out.println("telos ta join ");
			if (Main.interestedclientServer1.size() == 0
					&& Main.interestedclientServer2.size() == 0) {
				try {
					System.out.println("Noone is interested. Removing item!!");
					DataBaseInteractions.remove();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public void synchStartBidding() {
		ServerAuct auct1 = new ServerAuct("server1", 2,
				Main.interestedclientServer1);
		auct1.start();

		ServerAuct auct2 = new ServerAuct("server2", 2,
				Main.interestedclientServer2);
		auct2.start();

		try {
			auct1.join();
			auct2.join();
			System.out.println("telos to bidding ");
			BiddingResults.informAllForTheResult();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
