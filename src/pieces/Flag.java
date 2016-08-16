package pieces;

import java.util.ArrayList;

import model.JunqiBoard;
import model.JunqiPiece;
import model.JunqiSpace;

public class Flag extends JunqiPiece {

	/**
	 * A flag has 0 attack value.
	 * 
	 * @param newSpace The flag the bomb begins on
	 * @param newBoard The flag the bomb begins on
	 */
	public Flag(JunqiSpace newSpace, JunqiBoard newBoard) {
		super(0, newSpace, newBoard);
		setID('f');
	}
	
	public void setName() {
		name = "军旗";
	}	
	
	/**
	 * Returns true if the starting location is valid for the piece.
	 * Assumes the piece is on the bottom (higher row indicies) of the board.
	 * 
	 * @param row The row index (>=7)
	 * @param col The column index
	 * @return True if the location is valid
	 */
	public boolean validStartingSpace(int row, int col) {
		return row == 12 && (col == 1 || col == 3);
	}
	
	public ArrayList<JunqiSpace> findAllMoves() {
		return new ArrayList<JunqiSpace>();
	}
}
