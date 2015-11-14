import java.lang.*;

public class ThreadDemo  extends Thread implements Runnable{

   public void run() {
   
      Thread t = Thread.currentThread();
      System.out.print(t.getName());
      //checks if this thread is alive
      System.out.println(", status = " + t.isAlive());
      try {
		sleep(60*1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
   
   public void printSomethingBitch(){
	   System.out.println("opata leme gamw");
	   try {
		sleep(60*1000);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }

   public static void main(String args[]) throws Exception {
   
      ThreadDemo t = new ThreadDemo();
      // this will call run() function
      t.start();
      // waits for this thread to die
      System.out.println("started thread trying to print");
      t.join();
      System.out.print(t.getName());
      //checks if this thread is alive
      System.out.println(", status = " + t.isAlive());
      t.printSomethingBitch();
      System.out.println("yeah");
      
   }
}