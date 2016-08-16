package display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import model.JunqiBoard;
import model.JunqiPiece;
import model.JunqiPlayer;
import model.JunqiSpace;

// TODO: Set it up so the piece size can be adjusted by changing one variable
public class SetUpComponent extends JComponent {

	private JunqiBoard board;
	private int vertSize;
	private int hortSize;
	private int offset;
	private ArrayList<JunqiPiece> pieces;
	private JunqiPiece mouseOverPiece;
	private JunqiPiece selectedPiece;

	public SetUpComponent(JunqiBoard b, ArrayList<JunqiPiece> newPieces) {
		board = b;
		vertSize = 60;
		hortSize = 100;
		offset = 50;
		pieces = newPieces;
		mouseOverPiece = null;
		selectedPiece = null;
	}

	public int getVertPointSize() {
		return vertSize;
	}

	public int getHortPointSize() {
		return hortSize;
	}

	public int getOffset() {
		return offset;
	}

	public void setMouseOverPiece(JunqiPiece piece) {
		mouseOverPiece = piece;
	}

	public void setSelectedPiece(JunqiPiece piece) {
		selectedPiece = piece;
	}

	/**
	 * Paints the board and pieces.
	 * 
	 * @param g
	 *            the graphics to be used
	 */
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		Stroke defaultStroke = g2.getStroke();
		
		g2.setColor(Color.RED);

		// Draw the board

		// We offset right and down to center the pieces
		// Then we add the offset to each coordinate to give space for the
		// images
		
		for (int row = 0; row < board.getRows(); row++) {
			for (int col = 0; col < board.getCols(); col++) {
				JunqiSpace space = board.getSpace(row, col);
				if (space == null) {
					continue;
				}
				int spaceRow = space.getRow();
				int spaceCol = space.getCol();
				for (JunqiSpace neighbor : space.getNeighbors()) {
					if (neighbor.getRow() > spaceRow
							|| (neighbor.getRow() == spaceRow && neighbor
									.getCol() > spaceCol)) {
						g2.drawLine(
								(int) ((spaceCol + .375) * hortSize) + offset,
								(int) ((spaceRow + .375) * vertSize) + offset,
								(int) ((neighbor.getCol() + .375) * hortSize + offset),
								(int) ((neighbor.getRow() + .375) * vertSize + offset));
					}
				}
				if (space.isBunker()) {
					g2.setColor(Color.WHITE);
					g2.fillOval(
							(int) ((spaceCol + .375) * hortSize - .5 * vertSize)
									+ offset, (int) ((spaceRow + .375)
									* vertSize - .5 * vertSize)
									+ offset, vertSize, vertSize);
					g2.setColor(Color.RED);
				}
				else { // normal rectangles
						g2.setColor(Color.BLACK);
						g2.setStroke(new BasicStroke(8));
						g2.drawRect(offset + space.getCol() * hortSize, offset
								+ space.getRow() * vertSize,
								(int) (hortSize * .75), (int) (vertSize * .75));
						g2.setColor(Color.WHITE);
						g2.fillRect(offset + space.getCol() * hortSize, offset
								+ space.getRow() * vertSize,
								(int) (hortSize * .75), (int) (vertSize * .75));
						g2.setStroke(defaultStroke);
						g2.setColor(Color.RED);
					}
			}
		}

		// Draw opponent pieces

		g2.setColor(Color.DARK_GRAY);
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < board.getCols(); col++) {
				JunqiSpace space = board.getSpace(row, col);
				if (!space.isBunker()) {
					g2.fillRect(offset + space.getCol() * hortSize, offset
							+ space.getRow() * vertSize,
							(int) (hortSize * .75), (int) (vertSize * .75));
				}
			}
		}

		// Draw the pieces
		g2.setFont(new Font(g2.getFont().getName(), Font.PLAIN, (int) (g2
				.getFont().getSize() * 1.25)));

		for (int i = 0; i < pieces.size(); i++) {
			g2.setColor(Color.BLUE);
			JunqiSpace space = pieces.get(i).getSpace();
			g2.fillRect(offset + space.getCol() * hortSize,
					offset + space.getRow() * vertSize, (int) (hortSize * .75),
					(int) (vertSize * .75));
			g2.setColor(Color.WHITE);

		}

		if (mouseOverPiece != null) {
			g2.setColor(Color.GREEN);
			JunqiSpace space = mouseOverPiece.getSpace();
			g2.fillRect(offset + space.getCol() * hortSize,
					offset + space.getRow() * vertSize, (int) (hortSize * .75),
					(int) (vertSize * .75));
		}

		if (selectedPiece != null) {
			g2.setColor(Color.RED);
			JunqiSpace space = selectedPiece.getSpace();
			g2.fillRect(offset + space.getCol() * hortSize,
					offset + space.getRow() * vertSize, (int) (hortSize * .75),
					(int) (vertSize * .75));
		}

		// Draw labels
		for (int i = 0; i < pieces.size(); i++) {
			g2.setColor(Color.WHITE);
			JunqiSpace space = pieces.get(i).getSpace();
			g2.drawString(pieces.get(i).getName(),
					offset + (int) ((space.getCol() + .15) * hortSize), offset
							+ (int) ((space.getRow() + .5) * vertSize));
		}
	}
}
