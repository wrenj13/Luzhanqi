package network;

import model.JunqiBoard;
import model.JunqiMove;

/**
 * A class to represent a synchronized board object that can be used across threads.
 * Much help from http://tutorials.jenkov.com/java-concurrency/thread-signaling.html.
 */
public class WaitNotifyBoard {

	private JunqiBoard board;
	private boolean wasSignalled = false;
	private boolean myTurn;
	private boolean gameOver;
	private JunqiMove move;
	
	public WaitNotifyBoard() {
		board = new JunqiBoard();
		myTurn = false;
		move = null;
		gameOver = false;
	}
	
	public WaitNotifyBoard(JunqiBoard newBoard) {
		board = newBoard;
	}
	
	public JunqiBoard getBoard() {
		return board;
	}
	
	public boolean getTurn() {
		return myTurn;
	}
		
	public boolean getGameOver() {
		return gameOver;
	}
	
	public JunqiMove getMove() {
		return move;
	}
	
	public void setTurn(boolean turn) {
		myTurn = turn;
	}
	
	public void setGameOver(boolean game) {
		gameOver = game;
	}
	
	public void setMove(JunqiMove latestMove) {
		move = latestMove;
	}
	
	public void reset() {
		wasSignalled = false;
		move = null;
	}

	public void doWait() {
		synchronized(board) {
			while (!wasSignalled) {
				try {
					board.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		    //clear signal and continue running.
		    wasSignalled = false;
		}
	}
	
	public void doNotify() {
		synchronized(board) {
		      wasSignalled = true;
		      board.notify();
		}
	}
}
