package model;

import java.util.ArrayList;

public class JunqiSpace {
	
	private JunqiPiece piece;
	private int row;
	private int col;
	private boolean bunker;
	private ArrayList<JunqiSpace> neighbors;
	
	public JunqiSpace(int newRow, int newCol) {
		row = newRow;
		col = newCol;
		piece = null;
		bunker = false;
		neighbors = new ArrayList<JunqiSpace>();
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public JunqiPiece getPiece() {
		return piece;
	}
	
	public boolean isBunker() {
		return bunker;
	}
	
	public void setPiece(JunqiPiece newPiece) {
		piece = newPiece;
	}
	
	public ArrayList<JunqiSpace> getNeighbors() {
		return neighbors;
	}
	
	public void removePiece() {
		piece = null;
	}
	
	public void setBunker(boolean bunk) {
		bunker = bunk;
	}
	
	public void addNeighbor(JunqiSpace neighbor) {
		if (neighbor != null) {
			neighbors.add(neighbor);
		}
	}
	
	/**
	 * Searches for a neighbor with the given row and column indices.
	 * 
	 * @param row Row index
	 * @param col Column index
	 * @return The space if found, null otherwise.
	 */
	public JunqiSpace findNeighbor(int row, int col) {
		for (int i = 0; i < neighbors.size(); i++) {
			if (neighbors.get(i).getRow() == row && neighbors.get(i).getCol() == col) {
				return neighbors.get(i);
			}
		}
		return null;
	}
	
	public String toString() {
		return "(" + row + "," + col + "): " + piece;
	}
}
