package display;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import network.WaitNotifyBoard;
import pieces.Bomb;
import pieces.Engineer;
import pieces.Flag;
import pieces.Mine;
import model.JunqiBoard;
import model.JunqiPiece;
import model.JunqiPlayer;
import model.JunqiSpace;

// TODO Add timer
public class SetUpDisplay {
	
	private WaitNotifyBoard notifyBoard;
	private JunqiBoard board;
	private JunqiPiece currentPiece = null;
	private JunqiPiece pieceToMove = null;
	private ArrayList<JunqiPiece> pieces = new ArrayList<JunqiPiece>();
	private int piecesMovedToBoard = 0;
	
	public SetUpDisplay(WaitNotifyBoard newBoard) {
		notifyBoard = newBoard;
		board = newBoard.getBoard();
	}
	
	public JunqiPiece findPiece(int row, int col) {
		for (int i = 0; i < pieces.size(); i++) {
			if (pieces.get(i).getSpace().getRow() == row && pieces.get(i).getSpace().getCol() == col) {
				return pieces.get(i);
			}
		}
		return null;
	}
	
	public void setUp() {
		JFrame frame = new JFrame("Junqi");
		
		// add the pieces to the side of the board
		pieces.add(new JunqiPiece(9, new JunqiSpace(0, board.getCols()), board));
		pieces.add(new JunqiPiece(8, new JunqiSpace(1, board.getCols()), board));
		pieces.add(new JunqiPiece(7, new JunqiSpace(2, board.getCols()), board));
		pieces.add(new JunqiPiece(7, new JunqiSpace(3, board.getCols()), board));
		pieces.add(new JunqiPiece(6, new JunqiSpace(4, board.getCols()), board));
		pieces.add(new JunqiPiece(6, new JunqiSpace(5, board.getCols()), board));
		pieces.add(new JunqiPiece(5, new JunqiSpace(6, board.getCols()), board));
		pieces.add(new JunqiPiece(5, new JunqiSpace(7, board.getCols()), board));
		pieces.add(new JunqiPiece(4, new JunqiSpace(8, board.getCols()), board));
		pieces.add(new JunqiPiece(4, new JunqiSpace(9, board.getCols()), board));
		pieces.add(new JunqiPiece(3, new JunqiSpace(10, board.getCols()), board));
		pieces.add(new JunqiPiece(3, new JunqiSpace(11, board.getCols()), board));
		pieces.add(new JunqiPiece(3, new JunqiSpace(12, board.getCols()), board));
		pieces.add(new JunqiPiece(2, new JunqiSpace(0, board.getCols()+1), board));
		pieces.add(new JunqiPiece(2, new JunqiSpace(1, board.getCols()+1), board));
		pieces.add(new JunqiPiece(2, new JunqiSpace(2, board.getCols()+1), board));
		pieces.add(new Engineer(new JunqiSpace(3, board.getCols()+1), board));
		pieces.add(new Engineer(new JunqiSpace(4, board.getCols()+1), board));
		pieces.add(new Engineer(new JunqiSpace(5, board.getCols()+1), board));
		pieces.add(new Bomb(new JunqiSpace(6, board.getCols()+1), board));
		pieces.add(new Bomb(new JunqiSpace(7, board.getCols()+1), board));
		pieces.add(new Mine(new JunqiSpace(8, board.getCols()+1), board));
		pieces.add(new Mine(new JunqiSpace(9, board.getCols()+1), board));
		pieces.add(new Mine(new JunqiSpace(10, board.getCols()+1), board));
		pieces.add(new Flag(new JunqiSpace(11, board.getCols()+1), board));

		SetUpComponent component = new SetUpComponent(board, pieces);
		
		/**
		 * A class to provide mouse input. When the mouse is clicked and released, the pieces are moved according to the final and ending
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
				pieceToMove = currentPiece;
				component.setSelectedPiece(currentPiece);
				component.repaint();
			}

			/**
			 * Allows the player to move the selected piece, and then unhighlights it.
			 */
			public void mouseReleased(MouseEvent e) {
				if (pieceToMove == null) {
					return;
				}
				int y = e.getY()-component.getOffset();
				int x = e.getX()-component.getOffset();
				int row = y / component.getVertPointSize() ;
				int col = x / component.getHortPointSize();
				JunqiPiece pieceToBeSwapped =  findPiece(row, col);
				// Only bottom half of the board is valid
				if (row >= 7 && board.validSpace(row, col) && pieceToMove.validStartingSpace(row, col)) {
					// move the piece onto the board
					if (pieceToBeSwapped != null) { // if piece already exists at that spot
						JunqiSpace tempSpace = pieceToBeSwapped.getSpace();
						pieceToBeSwapped.setSpace(pieceToMove.getSpace());
						pieceToMove.setSpace(tempSpace);
					}
					else { // otherwise
						if (!board.validSpace(pieceToMove.getSpace().getRow(), pieceToMove.getSpace().getCol())) {
							piecesMovedToBoard++;
						}
						pieceToMove.setSpace(board.getSpace(row, col));
					}
				}
				pieceToMove = null;
				component.setSelectedPiece(null);
				component.repaint();
			}

			/**
			 * Updates the current space based on where the mouse is.
			 * This allows the board component to color the space.
			 */
			public void mouseMoved(MouseEvent e) {
				int y = e.getY()-component.getOffset();
				int x = e.getX()-component.getOffset();
				int row = y / component.getVertPointSize() ;
				int col = x / component.getHortPointSize();
				currentPiece = findPiece(row, col);
				component.setMouseOverPiece(currentPiece);
				component.repaint();
			}
		}
		
		/**
		 * A class to provide input from a button. When clicked, the button allows the player to complete setup.
		 * Then, the game class will be called.
		 */
		class CompleteListener implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				if (piecesMovedToBoard != 25) {
					return;
				}
				for (int i = 0; i < pieces.size(); i++) {
					JunqiSpace space = pieces.get(i).getSpace();
					space.setPiece(pieces.get(i));
				}
				frame.dispose();
				notifyBoard.doNotify();
			}
		}
		
		MouseAdapter junqiMouseListener = new JunqiMouseListener();
		
		// Create menu
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Game");
		JMenuItem menuItem = new JMenuItem("Setup complete");
		menuItem.addActionListener(new CompleteListener());
		menu.add(menuItem);
		menuBar.add(menu);
		
		frame.setTitle("Junqi");
		frame.setJMenuBar(menuBar);
		frame.add(component);
		frame.getContentPane().addMouseListener(junqiMouseListener);
		frame.getContentPane().addMouseMotionListener((MouseMotionListener) junqiMouseListener);
		frame.setSize((board.getCols() + 2) * component.getHortPointSize() + 2 * component.getOffset(), (board.getRows() + 1) * component.getVertPointSize() + 2 * component.getOffset());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
