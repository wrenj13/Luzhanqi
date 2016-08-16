package display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.awt.Stroke;

import javax.swing.JComponent;

import pieces.Flag;
import model.JunqiBoard;
import model.JunqiPiece;
import model.JunqiPlayer;
import model.JunqiSpace;

public class JunqiComponent extends JComponent {

	private JunqiBoard board;
	private int vertSize;
	private int hortSize;
	private int offset;
	private JunqiPlayer player;
	private JunqiPiece mouseOverPiece;
	private JunqiPiece selectedPiece;
	private boolean revealFlag;
	private JunqiSpace enemyFlag;
	private ArrayList<JunqiSpace> validMoves;

	public JunqiComponent(JunqiBoard b, JunqiPlayer p) {
		board = b;
		vertSize = 60;
		hortSize = 100;
		offset = 50;
		player = p;
		validMoves = null;
		revealFlag = false;
		if (board.getSpace(0, 1).getPiece() instanceof Flag) {
			enemyFlag = board.getSpace(0, 1);
		} else {
			enemyFlag = board.getSpace(0, 3);
		}
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

	public void setValidMoves(ArrayList<JunqiSpace> moves) {
		validMoves = moves;
	}
	
	public void revealFlag() {
		revealFlag = true;
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

		g2.setFont(new Font(g2.getFont().getName(), Font.PLAIN, (int) (g2
				.getFont().getSize() * 1.25)));

		// Draw the board, bunkers and pieces

		// We offset the board right and down to center the pieces
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
				// bunker circles
				if (space.isBunker()) {
					g2.setColor(Color.WHITE);
					g2.fillOval(
							(int) ((spaceCol + .375) * hortSize - .5 * vertSize)
									+ offset, (int) ((spaceRow + .375)
									* vertSize - .5 * vertSize)
									+ offset, vertSize, vertSize);
					g2.setColor(Color.RED);
				} else { // normal rectangles
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
				// Set piece backgrounds
				if (space.getPiece() != null) {
					if (space.getPiece().getPlayerId().equals(player.getId())) {
						g2.setColor(Color.BLUE);
					} else {
						g2.setColor(Color.DARK_GRAY);
					}
					g2.fillRect(offset + space.getCol() * hortSize, offset
							+ space.getRow() * vertSize,
							(int) (hortSize * .75), (int) (vertSize * .75));
					g2.setColor(Color.RED);

				}
			}
		}

		// highlight possible moves blue
		if (validMoves != null) {
			g2.setColor(new Color(0, 0, 128, 128));
			for (JunqiSpace space : validMoves) {
				if (space.isBunker()) {
					g2.fillOval(
							(int) ((space.getCol() + .375) * hortSize - .5 * vertSize)
									+ offset, (int) ((space.getRow() + .375)
									* vertSize - .5 * vertSize)
									+ offset, vertSize, vertSize);
				} else {
					g2.fillRect(offset + space.getCol() * hortSize, offset
							+ space.getRow() * vertSize,
							(int) (hortSize * .75), (int) (vertSize * .75));
				}
			}
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
		for (int i = 0; i < player.getPieces().size(); i++) {
			g2.setColor(Color.WHITE);
			JunqiSpace space = player.getPieces().get(i).getSpace();
			g2.drawString(player.getPieces().get(i).getName(), offset
					+ (int) ((space.getCol() + .15) * hortSize), offset
					+ (int) ((space.getRow() + .5) * vertSize));
		}
		
		if (revealFlag) {
			g2.drawString(enemyFlag.getPiece().getName(), offset
					+ (int) ((enemyFlag.getCol() + .15) * hortSize), offset
					+ (int) ((enemyFlag.getRow() + .5) * vertSize));
		}
	}
}
