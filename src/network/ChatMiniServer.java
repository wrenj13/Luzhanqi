package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * We use a "mini server" to take charge of each client so that the parent server can wait for either client to send a message.
 * 
 * @author Ren-Jay
 *
 */
public class ChatMiniServer extends Thread {

	Socket clientSocket;
	ChatServer parentServer;

	public ChatMiniServer(Socket newSocket, ChatServer parent) {
		clientSocket = newSocket;
		parentServer = parent;
	}

	public void run() {
		BufferedReader in;
		// Write to out to talk to client, read from in
		try {
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		String userInput = "";
		try {
			while (true) {
				userInput = in.readLine();
				parentServer.setChat(new Chat(userInput, clientSocket));
				parentServer.doNotify();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
