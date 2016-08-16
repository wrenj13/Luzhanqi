package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import model.JunqiBoard;
import model.JunqiMove;

/**
 * A class that represents the server for Junqi PVP games. Much help from
 * https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html.
 * 
 * @author REN-JAY_2
 *
 */
public class ChatServer {
	
	private boolean wasSignalled = false;
	private Chat latestChat = null;
	
	public void setChat(Chat newChat) {
		latestChat = newChat;
	}

	public void openServer() {

		System.out.println("Opening chat server...");

		int portNumber = 45001;
		ServerSocket serverSocket = null;

		// accept 2 connections
		try {

			serverSocket = new ServerSocket(portNumber);
			// Wait for 2 connections
			Socket clientSocket1 = serverSocket.accept();
			
			System.out.println("chat accepted 1");
			ChatMiniServer thread1 = new ChatMiniServer(clientSocket1, this);

			
			Socket clientSocket2 = serverSocket.accept();
			ChatMiniServer thread2 = new ChatMiniServer(clientSocket2, this);
			
			System.out.println("accepted two clients");
			
			thread1.start();
			thread2.start();

			// Write to out to talk to client, read from in
			PrintWriter out1 = new PrintWriter(clientSocket1.getOutputStream(),
					true);
			PrintWriter out2 = new PrintWriter(clientSocket2.getOutputStream(),
					true);
			
			// Initiate conversation with client

			while (true) {
				doWait();
				if (latestChat.getSocket() == clientSocket1) {
					out2.println(latestChat.getMessage());
				} else {
					out1.println(latestChat.getMessage());
				}
			}
		} catch (IOException e) {
			System.out
					.println("Exception caught when trying to listen on port "
							+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
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
	
	public static void main(String[] args) {
		ChatServer newServer = new ChatServer();
		newServer.openServer();
	}
}
