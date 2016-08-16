package model;

import java.util.ArrayList;

public class JunqiPlayer {

	private ArrayList<JunqiPiece> pieces;
	private String playerId;
	
	public JunqiPlayer(String id) {
		pieces = new ArrayList<JunqiPiece>();
		playerId = id;
	}
	
	public String getId() {
		return playerId;
	}
	
	public ArrayList<JunqiPiece> getPieces() {
		return pieces;
	}
	
	public void addPiece(JunqiPiece piece) {
		pieces.add(piece);
	}
	
	public void removePiece(JunqiPiece piece) {
		pieces.remove(piece);
	}
	
	public JunqiPiece findPiece(int row, int col) {
		for (int i = 0; i < pieces.size(); i++) {
			if (pieces.get(i).getSpace().getRow() == row && pieces.get(i).getSpace().getCol() == col) {
				return pieces.get(i);
			}
		}
		return null;
	}
}
