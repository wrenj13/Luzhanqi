package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import model.JunqiBoard;
import model.JunqiMove;
import model.JunqiPlayer;
import display.Display;
import display.SetUpDisplay;

/**
 * A class to represent one player in a PVP.
 * 
 * Much help from
 * https://docs.oracle.com/javase/tutorial/networking/sockets/examples
 * /KnockKnockClient.java.
 * 
 TODO: Add timer to board setup
 * 
 * @author REN-JAY_2
 *
 */
public class JunqiClient extends Thread {
	private final String HOSTNAME = "RENJAY-PC";//"50.179.164.124";
	private final int PORTNUMBER = 45000;
	public boolean start = false;
	public int myscore = 0;
	public int theirscore = 0;

	public void run(){
		String userName = "";
		while (userName.length() == 0 || userName.length() > 8) {
			userName = JOptionPane.showInputDialog("Please choose a player ID (max 8 characters)");
		}
		try {
			Socket clientSocket = new Socket(HOSTNAME, PORTNUMBER);
			
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String fromServer, board;
			System.out.println("entered loop?");
			while ((fromServer = in.readLine()) != null) {
				if (fromServer.equals("begin")) {
					System.out.println("Received data!");
					Display gui = null;
					// Set up player and gui
					JunqiPlayer player = new JunqiPlayer(userName);
					
					// Thread where user sets up board
					WaitNotifyBoard syncBoard = new WaitNotifyBoard();
					SetUpDisplay setUpGUI = new SetUpDisplay(syncBoard);
					setUpGUI.setUp();
					// thread notifies client when finished (with timer)
					syncBoard.doWait();
					System.out.println("board completed!");
					System.out.println(syncBoard.getBoard().toString());
					// client receives board data and sends it to the server
					out.println(syncBoard.getBoard().toString());
					
			//		out.println("00000000000000000000000000000000000982e720e073b066b0405m33e5mfm24");
					// server sends back complete board to client
					board = in.readLine();
					syncBoard = new WaitNotifyBoard(JunqiBoard.fromString(board));
					WaitNotifyChat syncChat = new WaitNotifyChat();
					gui = new Display(syncBoard, syncChat, player);
					// Now we connect to chat server and run client thread
					System.out.println("opening chat socket");
					Socket chatSocket = new Socket(HOSTNAME, PORTNUMBER+1);
					ChatClient chatClient = new ChatClient(chatSocket, gui, syncChat);
					chatClient.start();
					gui.setUp();	
					// if client turn then: client shows board, waits for move, updates board, then sends move to server
					// if not client turn: client shows board and waits
					while ((fromServer = in.readLine()) != null) {
						syncBoard.setTurn(true);
						// if not the first move in the game, update board based on opponent's last move
						if (!fromServer.equals("firstmove")) {
							System.out.println("displaying move");
							JunqiMove opponentMove = JunqiMove.fromString(fromServer, syncBoard.getBoard());
							gui.highlightPiece(opponentMove.getOriginalSpace().getPiece());
							// wait one second
							try {
								sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							gui.displayMove(opponentMove);
							if (syncBoard.getGameOver()) { 	// user lost
								syncBoard.setTurn(false);	
								break;
							}
						}
						// wait for move
						syncBoard.doWait();
						System.out.println("player made a move!");
						syncBoard.setTurn(false);
						if (syncBoard.getGameOver()) { // user won
							out.println("gameover");
							out.println(syncBoard.getMove().toString());
							break;
						}
						out.println(syncBoard.getMove().toString());
						System.out.println("sent move, waiting for opponent to make a move...");
					}
				}	
				clientSocket.close();
				break;
			}
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + HOSTNAME);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ HOSTNAME);
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		JunqiClient client = new JunqiClient();
		client.run();
	}

}
