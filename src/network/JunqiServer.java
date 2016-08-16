package network;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
public class JunqiServer extends Thread {

	/**
	 * A method to merge two boards into one board. This method assumes that
	 * both boards have pieces only the bottom half of the board.
	 * 
	 * @param board1
	 *            The board that will have pieces on the bottom (higher indices)
	 * @param board2
	 *            The board that will have pieces on the top (lower indices)
	 */
	public static String mergeBoards(String board1, String board2) {
		String result = "";
		int index = board2.length() - 5;
		int counter = 0;
		// Do this for 6 rows
		while (counter < 30) {
			result += board2.charAt(index);
			index++;
			counter++;
			if (counter % 5 == 0) {
				index -= 10;
			}
		}
		return result + "00000" + board1.substring(35);
	}

	public static void sendFile(File file, Socket out) {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			while (true) {
				try {
					// send file
					byte[] mybytearray = new byte[(int) file.length()];
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					bis.read(mybytearray, 0, mybytearray.length);
					os = out.getOutputStream();
					System.out.println("Sending " + file.getName() + "("
							+ mybytearray.length + " bytes)");
					os.write(mybytearray, 0, mybytearray.length);
					os.flush();
					System.out.println("Done.");
				} finally {
					if (bis != null)
						bis.close();
					if (os != null)
						os.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		System.out.println("Opening server...");

		int portNumber = 45000;
		ServerSocket serverSocket = null;

		// Keep accepting connections until we turn off the server
		// while (true) {
		try {

			serverSocket = new ServerSocket(portNumber);
			// Wait for 2 connections
			Socket clientSocket1 = serverSocket.accept();
			System.out.println("One connection");
			Socket clientSocket2 = serverSocket.accept();

			// Write to out to talk to client, read from in
			PrintWriter out1 = new PrintWriter(clientSocket1.getOutputStream(),
					true);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(
					clientSocket1.getInputStream()));
			PrintWriter out2 = new PrintWriter(clientSocket2.getOutputStream(),
					true);
			BufferedReader in2 = new BufferedReader(new InputStreamReader(
					clientSocket2.getInputStream()));

			// Set up chat server in a new thread

			Thread t = new Thread() {
				public void run() {
					ChatServer chatServer = new ChatServer();
					chatServer.openServer();
				}
			};
			t.start();

			// Initiate conversation with client

			out1.println("begin");
			out2.println("begin");
			System.out.println("signalled clients to begin");
			String board1 = "", board2 = "";
			while (board1.length() != 65) {
				board1 = in1.readLine();
				System.out.println("current board: " + board1);
			}
			while (board2.length() != 65) {
				board2 = in2.readLine();
				System.out.println("current board: " + board2);
			}
			System.out.println(board1);
			System.out.println(board2);
			out1.println(JunqiBoard.fromString(mergeBoards(board1, board2)));
			out2.println(JunqiBoard.fromString(mergeBoards(board2, board1)));
			out1.println("firstmove");
			String move1 = "", move2 = "";
			while (true) {
				move1 = in1.readLine();
				if (move1.equals("gameover")) {
					move1 = in1.readLine();
					out2.println(JunqiMove.reverseMove(move1));
					break;
				}
				out2.println(JunqiMove.reverseMove(move1));
				move2 = in2.readLine();
				if (move2.equals("gameover")) {
					move2 = in2.readLine();
					out1.println(JunqiMove.reverseMove(move2));
					break;
				}
				out1.println(JunqiMove.reverseMove(move2));
			}
			serverSocket.close();
		} catch (IOException e) {
			System.out
					.println("Exception caught when trying to listen on port "
							+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
		// }
	}
}
