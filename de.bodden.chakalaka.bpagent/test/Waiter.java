
public class Waiter {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Sleeping...");
		while(true) {
			foo();
		}
	}

	private static void foo() throws InterruptedException {
		System.out.print("");		
		System.out.print("");		
		System.out.print("");				
		Thread.sleep(1000);
	}

}
