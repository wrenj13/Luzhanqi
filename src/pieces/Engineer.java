package pieces;

import java.util.ArrayList;

import model.JunqiBoard;
import model.JunqiPiece;
import model.JunqiSpace;

public class Engineer extends JunqiPiece {

	public Engineer(JunqiSpace newSpace, JunqiBoard newBoard) {
		super(1, newSpace, newBoard);
		setID('e');
	}
	
	public void setName() {
		name = "工兵";
	}
	
	/**
	 * The default behavior for attacking an opponent piece.
	 * This method moves the piece to the new space as well.
	 * 
	 * @param oppPiece The piece that you are trying to capture.
	 * @return A string indicating the result
	 */
	public String capture(JunqiPiece oppPiece) {
		if (oppPiece instanceof Mine) {
			oppPiece.getSpace().removePiece();
			moveTo(oppPiece.getSpace());
			return "W";
		}
		else 
			return super.capture(oppPiece);
	}

	/**
	 * Finds all the possible moves the engineer can make.
	 */
	public ArrayList<JunqiSpace> findAllMoves() {
		ArrayList<JunqiSpace> result = new ArrayList<JunqiSpace>();
		if (!JunqiBoard.isRailroad(getSpace().getRow(), getSpace().getCol())) {
			// Add neighboring spaces
			for (int i = 0; i < getSpace().getNeighbors().size(); i++) {
				JunqiSpace neighborSpace = getSpace().getNeighbors().get(i);
				if (neighborSpace.getPiece() == null || (!neighborSpace.getPiece().getPlayerId().equals(getPlayerId()) && !neighborSpace.isBunker())) {
					result.add(neighborSpace);
				}
			}
			return result;
		}
		boolean[][] visited = new boolean[13][5];
		findRailroadMoves(getSpace().getRow()+1, getSpace().getCol(), result, visited, true);
		findRailroadMoves(getSpace().getRow()-1, getSpace().getCol(), result, visited, true);
		findRailroadMoves(getSpace().getRow(), getSpace().getCol()+1, result, visited, true);
		findRailroadMoves(getSpace().getRow(), getSpace().getCol()-1, result, visited, true);
		return result;
	}
	
	/**
	 * A helper method to find all places on the railroad the engineer can visit.
	 * 
	 * @param currentRow The current row of the space
	 * @param currentCol The current column of the space
	 * @param result The Arraylist of spaces that can be visited
	 * @param visited Indicates which spaces have been checked already
	 */
	public void findRailroadMoves(int currentRow, int currentCol, ArrayList<JunqiSpace> result, boolean[][] visited, boolean firstIteration) {
		if (!JunqiBoard.isRailroad(currentRow, currentCol) || visited[currentRow][currentCol]) {
			if (firstIteration && getBoard().validSpace(currentRow, currentCol)) {
				JunqiSpace neighborSpace = getBoard().getSpace(currentRow, currentCol);
				if (neighborSpace.getPiece() == null || (!neighborSpace.getPiece().getPlayerId().equals(getPlayerId()) && !neighborSpace.isBunker())) {
					result.add(neighborSpace);
				}
			}
			return;
		}
		if (getBoard().getSpace(currentRow, currentCol).getPiece() != null) {
			// if enemy piece, add the space
			if (!getBoard().getSpace(currentRow, currentCol).getPiece().getPlayerId().equals(getPlayerId()) && !getBoard().getSpace(currentRow, currentCol).isBunker()) {
				result.add(getBoard().getSpace(currentRow, currentCol));
			}
			return;
		}
		result.add(getBoard().getSpace(currentRow, currentCol));
		visited[currentRow][currentCol] = true;
		findRailroadMoves(currentRow-1, currentCol, result, visited, false);
		findRailroadMoves(currentRow+1, currentCol, result, visited, false);
		findRailroadMoves(currentRow, currentCol-1, result, visited, false);
		findRailroadMoves(currentRow, currentCol+1, result, visited, false);
	}
	
}
