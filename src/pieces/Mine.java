package pieces;

import java.util.ArrayList;

import model.JunqiBoard;
import model.JunqiPiece;
import model.JunqiSpace;

public class Mine extends JunqiPiece {

	/**
	 * A mine has 10 attack value.
	 * 
	 * @param newSpace The space the mine begins on
	 * @param newBoard The board the mine begins on
	 */
	public Mine(JunqiSpace newSpace, JunqiBoard newBoard) {
		super(10, newSpace, newBoard);
		setID('m');
	}

	public void setName() {
		name = "地雷";
	}		
	
	/**
	 * The default behavior for responding to an attempted capture.
	 * 
	 * @param capturingPiece The piece trying to capture the current piece.
	 */
	public boolean response(JunqiPiece capturingPiece) {
		if (capturingPiece instanceof Engineer) {
			return true;
		}
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
		return row == 11	 || row == 12;
	}
	
	public ArrayList<JunqiSpace> findAllMoves() {
		return new ArrayList<JunqiSpace>();
	}
}
