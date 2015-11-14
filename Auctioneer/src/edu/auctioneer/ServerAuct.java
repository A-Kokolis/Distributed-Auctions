package edu.auctioneer;

import java.util.ArrayList;
import java.util.List;

public class ServerAuct extends Thread {

	private int choice = 0;
	private List<ClientConnected> listClients;
	private String serverName = null;

	public ServerAuct(String serverName, int choice, List<ClientConnected> listClients) {
		this.serverName = serverName;
		this.choice = choice;
		this.listClients = listClients;

	}

	public void run() {
		switch (choice) {
		case 1:
			String message = "Item name= " + DataBaseInteractions.item_name1
			+ " Description= " + DataBaseInteractions.description1
			+ " Initial price="
			+ DataBaseInteractions.initial_price1;
			Logging.writeLineToLog("sending "+message+ " to all clients");
			goTostep(listClients, 1);
			break;
		case 2:
			Logging.writeLineToLog("Bidding has started for all clients");
			goTostep(listClients, 2);
			break;

		default:
			break;
		}

	}

	public void goTostep(List<ClientConnected> listClients, int step) {
		try {
			int size = listClients.size();
			List<ClientConnected> listClientsNew= new ArrayList<ClientConnected>();
			for (int i = 0; i < size; i++) {
				ClientConnected clientConnected = new ClientConnected(
						listClients.get(i).getConnection(), listClients.get(i)
								.getServerName());
				clientConnected.setClientName(listClients.get(i)
						.getClientName());
				clientConnected.setStep(step);
				System.out.println(i + " name: " + listClients.get(i).getName());
				listClientsNew.add(clientConnected);
				if(step==2){
					listClients.get(i).setChildClientConnect(clientConnected); // we need a new list because the threads in clientConeected
				}															// list have already terminated once
				listClientsNew.get(i).start();    
				//listClientsNew.get(i).join();
			}
			
			for (int i=0;i<listClientsNew.size();i++){
				listClientsNew.get(i).join();
			}
			
			/*if(serverName.equals("server1") && step==2){
				Main.interestedclientServer1.clear();
				Main.interestedclientServer1.addAll(listClientsNew);
			}else if(serverName.equals("server2") && step==2){
				Main.interestedclientServer2.clear();
				Main.interestedclientServer2.addAll(listClientsNew);
			}*/
//			Thread.sleep(60*1000);
			/*for(int i = 0; i < size; i++){
				listClientsNew.get(i).start();
				listClientsNew.get(i).join();
				if(listClientsNew.get(i).isAlive()){
					listClientsNew.get(i).stop();
				}
			}*/

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/*public void startBiddingToAll(List<ClientConnected> listClients){
		try {
			int size = listClients.size();
			List<ClientConnected> listClientsNew= new ArrayList<ClientConnected>();
			for (int i = 0; i < size; i++) {
				ClientConnected clientConnected = new ClientConnected(
						listClients.get(i).getConnection(), listClients.get(i)
								.getServerName());
				clientConnected.setClientName(listClients.get(i)
						.getClientName());
				clientConnected.setStep(2);
				System.out.println(i + " name: " + listClients.get(i).getName());
				listClientsNew.add(clientConnected);
				clientConnected.start();
			}
//			Thread.sleep(60*1000);
			for(int i = 0; i < size; i++){
				listClientsNew.get(i).join();
				if(listClientsNew.get(i).isAlive()){
					listClientsNew.get(i).stop();
				}
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}*/
}
