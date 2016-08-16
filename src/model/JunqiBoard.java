package model;

import pieces.Bomb;
import pieces.Engineer;
import pieces.Flag;
import pieces.Mine;

public class JunqiBoard {

	private JunqiSpace[][] spaces;
	
	public JunqiBoard() {
		spaces = new JunqiSpace[13][5];
		for (int row = 0; row < spaces.length; row++) {
			for (int col = 0; col < spaces[0].length; col++) {
				spaces[row][col] = new JunqiSpace(row, col);
			}
		}
		// Empty middle spaces
		spaces[6][1] = null;
		spaces[6][3] = null;
		// Add horizontal connections
		for (int row = 0; row < spaces.length; row++) {
			if (row != 6) {
				for (int col = 0; col < spaces[0].length-1; col++) {
					addConnection(row, col, row, col+1);
				}
			}
		}
		// Add vertical connections
		for (int col = 0; col < spaces[0].length; col++) {
			for (int row = 0; row < spaces.length-1; row++) {
				if (row != 6 || (col != 1 && col != 3)) {
					addConnection(row, col, row+1, col);
				}
			}
		}
		// Set bunkers
		createBunker(spaces[2][1]);
		createBunker(spaces[2][3]);
		createBunker(spaces[3][2]);
		createBunker(spaces[4][1]);
		createBunker(spaces[4][3]);
		createBunker(spaces[8][1]);
		createBunker(spaces[8][3]);
		createBunker(spaces[9][2]);
		createBunker(spaces[10][1]);
		createBunker(spaces[10][3]);
	}
	
	public JunqiSpace getSpace(int row, int col) {
		if (row >=0 && row < spaces.length && col >=0 && col < spaces[0].length)
			return spaces[row][col];
		return null;
	}

	public int getRows() {
		return spaces.length;
	}
	
	public int getCols() {
		return spaces[0].length;
	}
	
	public boolean validSpace(int row, int col) {
		if (row < 0 || row >= spaces.length || col < 0 || col >= spaces[0].length) {
			return false;
		}
		if (row == 6) {
			if (col == 1 || col == 3) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * A wrapper function to add a neighbor, used to perform boundary checks and to prevent inconsistency bugs.
	 * Adds the neighbor only for the first space.
	 * 
	 * @param row1 The row of the first space
	 * @param col1 The column of the first space
	 * @param row2 The row of the second space.
	 * @param col2 The column of the second space.
	 */
	public void addNeighbor(int row1, int col1, int row2, int col2) {
		if (row1 < 0 || row1 >= spaces.length || col1 < 0 || col1 >= spaces[0].length) {
			return;
		}
		if (row2 < 0 || row2 >= spaces.length || col2 < 0 || col2 >= spaces[0].length) {
			return;
		}
		if (spaces[row1][col1] != null && spaces[row2][col2] != null) {
			spaces[row1][col1].addNeighbor(spaces[row2][col2]);
		}
	}
	
	
	/**
	 * A wrapper function to add neighbors, used to perform boundary checks and to prevent inconsistency bugs.
	 * Adds connections both ways.
	 * 
	 * @param row1 The row of the first space
	 * @param col1 The column of the first space
	 * @param row2 The row of the second space.
	 * @param col2 The column of the second space.
	 */
	public void addConnection(int row1, int col1, int row2, int col2) {
		if (row1 < 0 || row1 >= spaces.length || col1 < 0 || col1 >= spaces[0].length) {
			return;
		}
		if (row2 < 0 || row2 >= spaces.length || col2 < 0 || col2 >= spaces[0].length) {
			return;
		}
		if (spaces[row1][col1] != null && spaces[row2][col2] != null) {
			spaces[row1][col1].addNeighbor(spaces[row2][col2]);
			spaces[row2][col2].addNeighbor(spaces[row1][col1]);
		}
	}

	/**
	 * Adds the diagonal neighbor to the space. If the neighbor is a bunker, the connection is added one way; otherwise it is added both ways.
	 * This method is designed for bunkers.
	 * 
	 * @param space The space to add neighbors to
	 */
	public void addBunkerNeighbor(JunqiSpace space, int neighborRow, int neighborCol) {
		if (space == null || !validSpace(neighborRow, neighborCol)) {
			return;
		}
		int row = space.getRow();
		int col = space.getCol();
		if (spaces[neighborRow][neighborCol].isBunker()) {
			addNeighbor(row, col, neighborRow, neighborCol);
		} else {
			addConnection(row, col, neighborRow, neighborCol);
		}
	}
	
	public void createBunker(JunqiSpace space) {
		int row = space.getRow();
		int col = space.getCol();
		// add diagonal neighbors
		addBunkerNeighbor(space, row-1, col-1);
		addBunkerNeighbor(space, row-1, col+1);
		addBunkerNeighbor(space, row+1, col-1);
		addBunkerNeighbor(space, row+1, col+1);
		space.setBunker(true);
	}
	
	public String toString() {
		String s = "";
		for (int row = 0; row < spaces.length; row++) {
			for (int col = 0; col < spaces[0].length; col++) {
				if (spaces[row][col] == null || spaces[row][col].getPiece() == null) {
					s += "0";
				} else {
					s += spaces[row][col].getPiece().getID();
				}
			}
		}
		return s;
	}
	
	public static JunqiBoard fromString(String s) {
		JunqiBoard board = new JunqiBoard();
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != '0') {
				JunqiSpace space = board.getSpace(i/5, i%5);
				if (s.charAt(i) == 'b') {
					Bomb b = new Bomb(space, board);
					space.setPiece(b);
				} else if (s.charAt(i) == 'e') {
					Engineer e = new Engineer(space, board);
					space.setPiece(e);
				} else if (s.charAt(i) == 'f') {
					Flag f = new Flag(space, board);
					space.setPiece(f);
				} else if (s.charAt(i) == 'm') {
					Mine m = new Mine(space, board);
					space.setPiece(m);
				} else {
					JunqiPiece piece = new JunqiPiece(s.charAt(i)-'0', space, board);
					space.setPiece(piece);
				}
			}
		}
		return board;
	}
	
	/**
	 * Returns true if the space is on the board's railroad.
	 * 
	 * @param row The row index
	 * @param col The column index
	 * @return True if railroad
	 */
	public static boolean isRailroad(int row, int col) {
		// check boundary conditions
		if (col < 0 || col > 4) {
			return false;
		}
		// middle row
		if (row == 6) {
			return col == 0 || col == 2 || col == 4;
		}
		if (col == 0 || col == 4) {
			return row > 0 && row < 12;
		}
		return row == 1 || row == 5 || row == 7 || row == 11;
	}
	
	public static boolean isHeadquarters(int row, int col) {
		if (row == 0 || row == 12) {
			if (col == 1 || col == 3) {
				return true;
			}
		}
		return false;
	}
}
