package network;

/**
 * A class to represent a synchronized chat object that can be used to notify threads.
 * Much help from http://tutorials.jenkov.com/java-concurrency/thread-signaling.html.
 */
public class WaitNotifyChat {

	private String message;
	private boolean wasSignalled = false;
	
	public WaitNotifyChat() {
		message = "";
	}

	public String getMessage() {
		return message;
	}
	
	public void setMessage(String newMessage) {
		message = newMessage;
	}
	

	public void reset() {
		wasSignalled = false;
		message = "";
	}

	public void doWait() {
		synchronized(this) {
			while (!wasSignalled) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		    //clear signal and continue running.
		    wasSignalled = false;
		}
	}
	
	public void doNotify() {
		synchronized(this) {
		      wasSignalled = true;
		      notify();
		}
	}
}
