package edu.auctioneer;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DataBaseInteractions {

	private static DataBaseInteractions instance = null;
	
	private static String auction_file_path="C:/Users/user/Dropbox/Advanced DB/auction_file";			
	public static int L=0;  // L is the waiting time between each phase of the auction

	public static Connection connection = null;
	public static Statement statement = null;
	public static ResultSet resultSet = null;
	public static ResultSetMetaData metaData = null;

	public static Connection connection2 = null;
	public static Statement statement2 = null;
	public static ResultSet resultSet2 = null;
	public static ResultSetMetaData metaData2 = null;

	public static String item_name1;
	public static String description1;
	public static int initial_price1;
	public static int id1;

	public static String item_name2;
	public static String description2;
	public static int initial_price2;
	public static int id2;

	public static boolean auctionCompleted = false;

	private DataBaseInteractions() {

		connectToDb1();
		connectToDb2();
		// System.out.println(result);
		// resultSet = statement.executeQuery("SELECT " + result + " FROM "+
		// table + " WHERE name= '" + name + "'");
		// metaData = resultSet.getMetaData();
		// numberOfColumns = metaData.getColumnCount();

	}

	public static DataBaseInteractions getInstance() {
		if (instance == null) {
			instance = new DataBaseInteractions();
		}
		return instance;
	}

	private void connectToDb1() {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost/advanced_db", "root", "");
			statement = connection.createStatement();
			System.out.println("Connection Established");
			
			Logging.writeLineToLog("connected to db1");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void connectToDb2() {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			connection2 = DriverManager.getConnection(
					"jdbc:mysql://localhost/advanced_db2", "root", "");
			statement2 = connection2.createStatement();
			System.out.println("Connection Established");
			Logging.writeLineToLog("connected to db2");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean adduser(String user_name, String server_name,
			int socket_id) {
		if (server_name.equals("server1"))
			try {
				int resultSet = statement
						.executeUpdate("INSERT INTO registration (bidder_name, socket_id,canBid,connection_status) VALUES ('"
								+ user_name + "'," + socket_id + ",1,1);");
				Logging.writeLineToLog(user_name+" connected to "+server_name+" and stored to advanced_db1");
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("The name you used is not unique");
				Logging.writeLineToLog(user_name+" already exists");
				return false;
				// H adduser prepei na xanakalestei
			}
		else {
			try {
				int resultSet2 = statement2
						.executeUpdate("INSERT INTO registration (bidder_name, socket_id,canBid,connection_status) VALUES ('"
								+ user_name + "'," + socket_id + ",1,1);");
				Logging.writeLineToLog(user_name+" connected to "+server_name+" and stored to advanced_db2");
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				System.out.println("The name you used is not unique");
				Logging.writeLineToLog(user_name+" already exists");
				return false;

			}
		}

	}

	public void removeuser(String user_name, String server_name)
			throws SQLException {
		if (server_name.equals("server1")){
			resultSet = statement
					.executeQuery("DELETE FROM registration WHERE bidder_name="
							+ user_name + ";");
		Logging.writeLineToLog("user "+user_name+" deleted from advanced_db1");
		}
		else {
			resultSet2 = statement2
					.executeQuery("DELETE FROM registration WHERE bidder_name="
							+ user_name + ";");
			Logging.writeLineToLog("user "+user_name+" deleted from advanced_db2");
		}

	}

	public static void retrieve() throws SQLException {
		resultSet = statement.executeQuery("SELECT * FROM items LIMIT 1 ");
		resultSet2 = statement2.executeQuery("SELECT * FROM items LIMIT 1 ");
		// if (resultSet==null && resultSet2==null){
		// auctionCompleted=true;
		// }
		// System.out.println("ok1");
		if (resultSet.next()) {
			id1 = resultSet.getInt("id");
			item_name1 = resultSet.getString("name");
			description1 = resultSet.getString("description");
			initial_price1 = resultSet.getInt("initial_price");
			Logging.writeLineToLog("retrieved item:"+id1+" "+item_name1+" "+description1+" "+initial_price1);
		} else {
			auctionCompleted = true;
			Logging.writeLineToLog("DB's are empty....auction completed");
		}
		if (resultSet2.next()) {
			id2 = resultSet.getInt("id");
			item_name2 = resultSet2.getString("name");
			description2 = resultSet2.getString("description");
			initial_price2 = resultSet2.getInt("initial_price");
		}
		if (initial_price1 != initial_price2) {
			System.out.println("The two initial prices are different");
		} else {
			BiddingResults.setHighestBid(initial_price1);
		}

	}

	public static void update() throws SQLException {

		initial_price1 = (int) (0.9 * initial_price1);
		initial_price2 = (int) (0.9 * initial_price2);
		int result = statement.executeUpdate("UPDATE items SET initial_price='"
				+ initial_price1 + "'WHERE id = '" + id1 + "'");
		result = statement2.executeUpdate("UPDATE items SET initial_price='"
				+ initial_price2 + "'WHERE id = '" + id2 + "'");

		Logging.writeLineToLog("Item's value was reduced by 10%");
	}

	public static void remove() throws SQLException {
		int result = statement.executeUpdate("DELETE FROM items WHERE id="
				+ id1 + ";");
		System.out.println("id1= " + id1);
		result = statement2.executeUpdate("DELETE FROM items WHERE id=" + id2
				+ ";");
		System.out.println("id2= " + id2);
		Logging.writeLineToLog("item: "+item_name1+" was removed from DB");
	}

	public static void deleteFromRegistration1(ClientConnected clientConnected) {
		try {
			int result = statement
					.executeUpdate("DELETE FROM registration WHERE bidder_name='"
							+ clientConnected.getClientName() + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Logging.writeLineToLog("user "+clientConnected.getClientName()+" deleted from advanced_db1");
		System.out.println("bidder_name= " + clientConnected.getClientName());

	}

	public static void deleteFromRegistration2(ClientConnected clientConnected) {
		try {
			int result = statement2
					.executeUpdate("DELETE FROM registration WHERE bidder_name='"
							+ clientConnected.getClientName() + "';");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Logging.writeLineToLog("user "+clientConnected.getClientName()+" deleted from advanced_db2");
		System.out.println("bidder_name= " + clientConnected.getClientName());

	}

	public static void addItemsToDatabase() {
		
		int count = 0;
		int items = 0;
		String s3;
		String s4;
		Scanner sc2 = null;
		try {
			sc2 = new Scanner(new File(auction_file_path));
		} catch (FileNotFoundException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (sc2.hasNextLine()) {
			Scanner s2 = new Scanner(sc2.nextLine());
			boolean b;
			while (b = s2.hasNext()) {
				String s = s2.nextLine();
				System.out.println(s);
				if (count == 0) {
					// System.out.println(s +" ok");
					L = Integer.parseInt(s);
					count += 1;
				} else if (count == 1) {
					items = Integer.parseInt(s);
					// System.out.println(s +" ok2");

					count += 1;
				} else {
					try {
						String arr[] = s.split("	", 3);

						int result = statement
								.executeUpdate("INSERT INTO items (name, initial_price,description) VALUES ('"
										+ arr[1]
										+ "','"
										+ arr[0]
										+ "','"
										+ arr[2] + "');");
//						int result2 = statement2
//								.executeUpdate("INSERT INTO items (name, initial_price,description) VALUES ('"
//										+ arr[1]
//										+ "','"
//										+ arr[0]
//										+ "','"
//										+ arr[2] + "');");

					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}
		Logging.writeLineToLog("Items were inserted to both DB's");
	}

}
