package edu.auctioneer;

public class BiddingResults {
	
private static BiddingResults instance = null;

private static String bidHolder = null;
private static Integer highestBid = 0;
public static Boolean valueIsChanging = false;

	private BiddingResults() {
		
	}
	
	public static BiddingResults getInstance() {
	      if(instance == null) {
	         instance = new BiddingResults();
	      }
	      return instance;
	   }
	
	public static synchronized void checkAndUpdateBid(Integer bid, String bidder){
		Logging.writeLineToLog("Check new bid value:"+bid+" from "+bidder);
		if((bid > getHighestBid()) ||(bidHolder==null && bid>=getHighestBid())){
			setHighestBid(bid);
			setBidHolder(bidder);
			onBidValueChange();
			Logging.writeLineToLog("value changed...all clients were informed for the new bid");
		}
		
	}
	
	private static void onBidValueChange(){
		valueIsChanging = true;
		for(ClientConnected clientConnected: Main.interestedclientServer1){
			clientConnected.informValueChanged(getBidHolder(), getHighestBid());
		}
		for(ClientConnected clientConnected: Main.interestedclientServer2){
			clientConnected.informValueChanged(getBidHolder(), getHighestBid());
		}
		valueIsChanging = false;
	}
	
	public static void informAllForTheResult(){
		for(ClientConnected clientConnected: Main.interestedclientServer1){
			if (getBidHolder()!=null || Main.biddingCount==4){
			clientConnected.informForTheWinner(getBidHolder(), getHighestBid());
			}
			else{
				clientConnected.informForNextRound();
			}
		}
		for(ClientConnected clientConnected: Main.interestedclientServer2){
			if (getBidHolder()!=null || Main.biddingCount==4){
			clientConnected.informForTheWinner(getBidHolder(), getHighestBid());
			}
			else{
				clientConnected.informForNextRound();
			}
		}
		Logging.writeLineToLog("all users were informed for the result!! Winner= "+bidHolder+" with price of "+highestBid);
	}
	
	public static void clear(){
		setBidHolder(null);
		setHighestBid(0);
	}

	public static Integer getHighestBid() {
		return highestBid;
	}

	public static void setHighestBid(Integer highestBid) {
		BiddingResults.highestBid = highestBid;
	}

	public static String getBidHolder() {
		return bidHolder;
	}

	public static void setBidHolder(String bidHolder) {
		BiddingResults.bidHolder = bidHolder;
	}

}
