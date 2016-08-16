package network;

import java.net.Socket;

public class Chat {

	private String message;
	private Socket senderSocket;
	
	public Chat(String newMessage, Socket socket) {
		message = newMessage;
		senderSocket = socket;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Socket getSocket() {
		return senderSocket;
	}
}
