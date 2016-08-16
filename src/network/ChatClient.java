package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import display.Display;

/**
 * The client class has one thread to write to the client gui and one to listen to the client gui.
 * 
 * @author Ren-Jay
 *
 */
public class ChatClient extends Thread {
	
	private Socket chatSocket;
	private Display gui;
	private WaitNotifyChat syncChat;
	
	public ChatClient(Socket socket, Display display, WaitNotifyChat chat) {
		chatSocket = socket;
		gui = display;
		syncChat = chat;
	}
	
	public void run() {
		try {
			PrintWriter out = new PrintWriter(chatSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
			Thread clientToServer = new Thread() {
				public void run() {
					while (true) {
						syncChat.doWait();
						out.println(syncChat.getMessage());
					}
					
				}
			};
			clientToServer.start();
			while (true) {
				String message = in.readLine();
				gui.displayMessage(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
