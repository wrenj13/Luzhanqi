package display;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JTextField;

import pieces.Flag;
import network.WaitNotifyBoard;
import network.WaitNotifyChat;
import model.JunqiBoard;
import model.JunqiMove;
import model.JunqiPiece;
import model.JunqiPlayer;
import model.JunqiSpace;

public class Display {

	private WaitNotifyBoard waitNotifyBoard;
	private WaitNotifyChat syncChat;
	private JunqiBoard board;
	private JunqiComponent component;
	private JunqiPlayer player;
	private JTextField info;
	private ChatComponent chatBox;
	private JunqiPiece currentPiece = null;
	private ArrayList<JunqiSpace> validMoves = new ArrayList<JunqiSpace>();

	public Display(WaitNotifyBoard newBoard, WaitNotifyChat newChat, JunqiPlayer newPlayer) {
		waitNotifyBoard = newBoard;
		syncChat = newChat;
		board = waitNotifyBoard.getBoard();
		player = newPlayer;
	}

	public void associateBottomPieces() {
		for (int row = 7; row < board.getRows(); row++) {
			for (int col = 0; col < board.getCols(); col++) {
				JunqiPiece piece = board.getSpace(row, col).getPiece();
				if (piece != null) {
					piece.setPlayerId(player.getId());
					player.addPiece(piece);
				}
			}
		}
	}

	public void highlightPiece(JunqiPiece piece) {
		if (component == null) {
			return;
		}
		component.setSelectedPiece(piece);
		component.repaint();
	}
	
	/**
	 * Displays the move from the defending perspective.
	 * 
	 * @param move The opponent's move.
	 */
	public void displayMove(JunqiMove move) {
		if (component == null) {
			return;
		}
		JunqiPiece movingPiece = move.getOriginalSpace().getPiece();
		// check if capture move
		JunqiSpace desiredSpace = move.getTargetSpace();
		JunqiPiece userPiece = desiredSpace.getPiece();
		if (desiredSpace.getPiece() != null) {
			String result = movingPiece.capture(userPiece);
			if (result.equals("W")) {
				info.setText("Opponent captured user " + userPiece.getName());
				player.removePiece(userPiece);
				if (userPiece instanceof Flag) {
					waitNotifyBoard.setGameOver(true);
					info.setText("You lost.");
				}
			}
			else if (result.equals("T")) {
				info.setText("Both opponent " + movingPiece.getName() + " and user " + userPiece.getName() + " were eliminated");
				player.removePiece(userPiece);
				if (movingPiece.getValue() == 9) {
					component.revealFlag();
				}
			} else {
				info.setText("Opponent " + movingPiece.getName() + " was eliminated");
				if (movingPiece.getValue() == 9) {
					component.revealFlag();
				}
			}
		}
		else {
			movingPiece.moveTo(move.getTargetSpace());
		}
		component.setSelectedPiece(null);
		component.repaint();
	}
	
	public void displayMessage(String message) {
		String msg = message.substring(message.indexOf(":")+2);
		String username = message.substring(0, message.indexOf(":"));
		chatBox.addString(msg, username);
	}
	
	public void setUp() {
		associateBottomPieces();
		JFrame frame = new JFrame("Junqi");
		
		frame.setLayout(new BorderLayout());

		component = new JunqiComponent(board, player);
		
		info = new JTextField("It's time to play Junqi!");
		info.setHorizontalAlignment(JTextField.CENTER);

		/**
		 * A class to provide mouse input. When the mouse is clicked and
		 * released, the pieces are moved according to the final and ending
		 * positions.
		 * 
		 * @author REN-JAY_2
		 * 
		 */
		class JunqiMouseListener extends MouseAdapter {

			/**
			 * Highlights the selected piece.
			 */
			public void mousePressed(MouseEvent e) {
				if (currentPiece == null) {
					return;
				}
				component.setSelectedPiece(currentPiece);
				validMoves = currentPiece.findAllMoves();
				component.setValidMoves(validMoves);
				component.repaint();
			}

			/**
			 * Allows the player to move the selected piece, and then
			 * unhighlights it.
			 */
			public void mouseReleased(MouseEvent e) {
				if (currentPiece == null) {
					return;
				}
				int y = e.getY() - component.getOffset();
				int x = e.getX() - component.getOffset();
				int row = y / component.getVertPointSize();
				int col = x / component.getHortPointSize();
				// check turn
				if (waitNotifyBoard.getTurn()) {
					// check for valid move
					JunqiSpace desiredSpace = board.getSpace(row, col);
					if (validMoves.contains(desiredSpace)) {
						JunqiMove latestMove = new JunqiMove(currentPiece.getSpace(),
								board.getSpace(row, col));
						// if contains an enemy piece
						if (desiredSpace.getPiece() != null) {
							JunqiPiece opponentPiece = desiredSpace.getPiece();
							String result = currentPiece.capture(desiredSpace.getPiece());
							if (result.equals("W")) {
								info.setText("Captured opponent " + opponentPiece.getName());
								if (opponentPiece instanceof Flag) {
									info.setText("You win!");
									waitNotifyBoard.setGameOver(true);
								}
							}
							else if (result.equals("T")) {
								info.setText("Both opponent " + opponentPiece.getName() + " and user " + currentPiece.getName() + " were eliminated");
								player.removePiece(currentPiece);
								if (opponentPiece.getValue() == 9) {
									component.revealFlag(); // reveal the flag
								}
							} else {
								info.setText("User " + currentPiece.getName() + " was eliminated by enemy " + opponentPiece.getName());
								player.removePiece(currentPiece);
							} 
						}
						else {
							currentPiece.moveTo(board.getSpace(row, col));
						}
						waitNotifyBoard.setMove(latestMove);
						waitNotifyBoard.doNotify();
					}
				}
				component.setSelectedPiece(null);
				component.setValidMoves(null);
				component.repaint();
			}

			/**
			 * Updates the current space based on where the mouse is. This
			 * allows the board component to color the space.
			 */
			public void mouseMoved(MouseEvent e) {
				int y = e.getY() - component.getOffset();
				int x = e.getX() - component.getOffset();
				int row = y / component.getVertPointSize();
				int col = x / component.getHortPointSize();
				currentPiece = player.findPiece(row, col);
				component.setMouseOverPiece(currentPiece);
				component.repaint();
			}
		}

		MouseAdapter junqiMouseListener = new JunqiMouseListener();
		chatBox = new ChatComponent(player.getId(), syncChat);
		
		frame.setTitle("Junqi - " + player.getId());
		frame.add(info, BorderLayout.PAGE_START);
		frame.add(component, BorderLayout.CENTER);
		frame.add(chatBox, BorderLayout.LINE_END);
		component.addMouseListener(junqiMouseListener);
		component.addMouseMotionListener(
				(MouseMotionListener) junqiMouseListener);
		frame.setSize((board.getCols() + 1) * component.getHortPointSize() + 2
				* component.getOffset() + (int) (chatBox.getPreferredSize().getWidth() * .8),
				(board.getRows() + 1) * component.getVertPointSize() + 2
						* component.getOffset());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
