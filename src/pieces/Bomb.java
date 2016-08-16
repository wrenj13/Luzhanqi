package pieces;

import model.JunqiBoard;
import model.JunqiPiece;
import model.JunqiSpace;

public class Bomb extends JunqiPiece {

	/**
	 * A bomb has 0 attack value.
	 * 
	 * @param newSpace The space the bomb begins on
	 * @param newBoard The board the bomb begins on
	 */
	public Bomb(JunqiSpace newSpace, JunqiBoard newBoard) {
		super(0, newSpace, newBoard);
		setID('b');
	}

	public void setName() {
		name = "炸弹";
	}
	
	/**
	 * The default behavior for attacking an opponent piece.
	 * 
	 * @param oppPiece The piece that you are trying to capture.
	 * @return A string indicating the result
	 */
	public String capture(JunqiPiece oppPiece) {
		oppPiece.getSpace().removePiece();
		getSpace().removePiece();
		return "T";
	}
	
	/**
	 * The default behavior for responding to an attempted capture.
	 * 
	 * @param capturingPiece The piece trying to capture the current piece.
	 */
	public boolean response(JunqiPiece capturingPiece) {
		return false;		
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
		return row > 7 && super.validStartingSpace(row, col);
	}
}
