package model;

public class JunqiMove {

	private JunqiSpace orig;
	private JunqiSpace target;

	public JunqiMove(JunqiSpace originalSpace, JunqiSpace targetSpace) {
		orig = originalSpace;
		target = targetSpace;
	}

	public JunqiSpace getOriginalSpace() {
		return orig;
	}
	
	public JunqiSpace getTargetSpace() {
		return target;
	}
	
	public String toString() {
		return orig.getRow() + "," + orig.getCol()
				+ "," + target.getRow() + "," + target.getCol();
	}
	
	/**
	 * Reverses the orientation of the board across the horizontal axis.
	 * That is, a piece at (3, 2) going to (3, 9) would go from (3, 10) to (3, 3)
	 */
	public static String reverseMove(String origMove) {
		String[] tokens = origMove.split(",");
		return (12-Integer.parseInt(tokens[0])) + "," + tokens[1] + "," + (12-Integer.parseInt(tokens[2])) + "," + tokens[3];
	}
	
	public static JunqiMove fromString(String move, JunqiBoard board) {
		String[] tokens = move.split(",");
		JunqiSpace orig = board.getSpace(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
		JunqiSpace space = board.getSpace(Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
		return new JunqiMove(orig, space);
	}
}
