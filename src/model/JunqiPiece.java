package model;

import java.util.ArrayList;

public class JunqiPiece {

	private int value;
	private JunqiSpace space;
	private JunqiBoard board;
	private char pieceID;
	protected String name;
	private String playerId;

	public JunqiPiece(int newValue, JunqiSpace newSpace, JunqiBoard newBoard) {
		value = newValue;
		space = newSpace;
		board = newBoard;
		pieceID = (char) (newValue + '0');
		playerId = "DEF";
		setName();
	}

	public void setName() {
		switch (value) {
		case 9:
			name = "司令";
			break;
		case 8:
			name = "军长";
			break;
		case 7:
			name = "师长";
			break;
		case 6:
			name = "旅长";
			break;
		case 5:
			name = "团长";
			break;
		case 4:
			name = "营长";
			break;
		case 3:
			name = "连长";
			break;
		case 2:
			name = "排长";
			break;
		default:
			name = "other";
			break;
		}
	}

	public int getValue() {
		return value;
	}

	public JunqiSpace getSpace() {
		return space;
	}

	public JunqiBoard getBoard() {
		return board;
	}

	public char getID() {
		return pieceID;
	}

	public void setSpace(JunqiSpace newSpace) {
		space = newSpace;
	}

	public void setID(char newID) {
		pieceID = newID;
	}

	public String getName() {
		return name;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	/**
	 * The default behavior for attacking an opponent piece. This method moves
	 * the piece to the new space as well.
	 * 
	 * @param oppPiece
	 *            The piece that you are trying to capture.
	 * @return A string indicating the result
	 */
	public String capture(JunqiPiece oppPiece) {
		boolean capturingPieceSurvived = oppPiece.response(this);
		if (oppPiece.getValue() < value) {
			oppPiece.getSpace().removePiece();
			if (capturingPieceSurvived) {
				moveTo(oppPiece.getSpace());
				return "W";
			} else {
				getSpace().removePiece();
				return "T";
			}
		} else if (oppPiece.getValue() > value) {
			getSpace().removePiece();
			return "L";
		} else {
			oppPiece.getSpace().removePiece();
			getSpace().removePiece();
			return "T";
		}
	}

	/**
	 * The default behavior for responding to an attempted capture. This is
	 * called before the capturing piece attempts to capture the piece.
	 * 
	 * @param capturingPiece
	 *            The piece trying to capture the current piece.
	 * @return True if the capturing piece survived, and false otherwise.
	 */
	public boolean response(JunqiPiece capturingPiece) {
		return true;
	}

	public void moveTo(JunqiSpace space) {
		getSpace().setPiece(null);
		space.setPiece(this);
		setSpace(space);
	}

	/**
	 * Returns true if the starting location is valid for the piece. Assumes the
	 * piece is on the bottom (higher row indicies) of the board.
	 * 
	 * @param row
	 *            The row index (>=7)
	 * @param col
	 *            The column index
	 * @return True if the location is valid
	 */
	public boolean validStartingSpace(int row, int col) {
		return !board.getSpace(row, col).isBunker();
	}

	/**
	 * Returns true if the space is one that a user piece could move to.
	 * 
	 * @param row The row of the desired space
	 * @param col The column of the desired space
	 * @return True if valid, false otherwise.
	 */
	public boolean validMoveSpace(int row, int col) {
		JunqiSpace space = board.getSpace(row, col);
		if (space == null) {
			return false;
		}
		return space.getPiece() == null || (!space.isBunker() && !space.getPiece().getPlayerId().equals(getPlayerId()));
	}
	
	/**
	 * Returns all valid spaces the piece can move to.
	 * 
	 * @return An arraylist of valid moves
	 */
	public ArrayList<JunqiSpace> findAllMoves() {
		ArrayList<JunqiSpace> result = new ArrayList<JunqiSpace>();
		if (JunqiBoard.isHeadquarters(space.getRow(), space.getCol())) {
			return result;
		}
		// Add neighboring spaces
		for (int i = 0; i < space.getNeighbors().size(); i++) {
			if (validMoveSpace(space.getNeighbors().get(i).getRow(), space.getNeighbors().get(i).getCol())) {
				result.add(space.getNeighbors().get(i));
			}
		}
		if (!JunqiBoard.isRailroad(space.getRow(), space.getCol())) {
			return result;
		}
		// right
		if (board.validSpace(space.getRow(), space.getCol() + 1)
				&& board.getSpace(space.getRow(), space.getCol() + 1)
						.getPiece() == null) {
			for (int col = space.getCol() + 2; col < 5; col++) {
				if (!checkRailroadSpace(space.getRow(), col, result)) {
					break;
				}
			}
		}
		// left
		if (board.validSpace(space.getRow(), space.getCol() - 1)
				&& board.getSpace(space.getRow(), space.getCol() - 1)
						.getPiece() == null) {
			for (int col = space.getCol() - 2; col >= 0; col--) {
				if (!checkRailroadSpace(space.getRow(), col, result)) {
					break;
				}
			}
		}
		// down
		if (board.validSpace(space.getRow() + 1, space.getCol())
				&& board.getSpace(space.getRow() + 1, space.getCol())
						.getPiece() == null) {
			for (int row = space.getRow() + 2; row < 13; row++) {
				if (!checkRailroadSpace(row, space.getCol(), result)) {
					break;
				}
			}
		}
		// up
		if (board.validSpace(space.getRow() - 1, space.getCol())
				&& board.getSpace(space.getRow() - 1, space.getCol())
						.getPiece() == null) {
			for (int row = space.getRow() - 2; row >= 0; row--) {
				if (!checkRailroadSpace(row, space.getCol(), result)) {
					break;
				}
			}
		}
		return result;
	}

	public boolean checkRailroadSpace(int row, int col,
			ArrayList<JunqiSpace> result) {
		if (!board.validSpace(row, col)) {
			return false;
		}
		if (JunqiBoard.isRailroad(row, col)
				&& validMoveSpace(row, col)) {
			result.add(board.getSpace(row, col));
			if (board.getSpace(row, col).getPiece() != null) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}
}
