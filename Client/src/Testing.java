import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.io.*;

import javax.xml.bind.DatatypeConverter;

public class Testing {
	private String str = "";
	BufferedReader in;
	
	Testing a;
	Timer timer;
	
	TimerTask task = new TimerTask() {
		public void run() {
			if (str.equals("")) {
				System.out.println("you input nothing. exit...");
				// System.exit(0);
				try {
					System.out.println("Going to sleep");
					TimeUnit.SECONDS.sleep(5);
				} catch (InterruptedException e) {
					System.out.println("In the catch of timeunit");
					e.printStackTrace();
				}
				waitThere();
			}
		}
	};


	public void getInput() throws Exception {
		Timer timer = new Timer();
		timer.schedule(task, 5 * 1000);

		in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Input a string within 5 seconds: ");
		str = in.readLine();

		timer.cancel();
		System.out.println("you have entered: " + str);
		System.exit(0);
	}



	public static void main(String[] args) {

		waitThere();
		try {
			Testing a=new Testing();
			a.getInput();
		} catch (Exception e) {
			System.out.println(e);
		}
		System.out.println("main exit...");
	}

	private static void waitThere() {
		while (true) {
			System.out.println("Entered waitThere...");
			try {
				Testing a=new Testing();
				a.getInput();
				System.out.println("After testing");
			} catch (Exception e) {
				System.out.println(e);
			}

		}
	}

}
