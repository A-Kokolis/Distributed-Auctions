package edu.auctioneer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Logging {
	
	private static PrintWriter writer=null;
	
	private static Logging instance = null;


		private Logging() {
			try {
				writer = new PrintWriter("C:\\Users\\user\\Dropbox\\Advanced DB\\AuctionLogging.log", "UTF-8");
			} catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		public static Logging getInstance() {
		      if(instance == null) {
		         instance = new Logging();
		      }
		      return instance;
		   }
		
		public synchronized static void writeLineToLog(String line){
			writer.println(line);
			
		}
		
		public static void closeLogFile(){
			writer.close();
		}
	

}
