package Common;

import java.io.Serializable;

/**
 * @author Joel Magnér
 *
 */
public class Square implements Serializable {
	public int x, y, squareSize;
	boolean status;
	String fill, stroke;

	/**
	 *
	 * @param x - x pos
	 * @param y - y pos
	 * @param squareSize - size of square
	 * @param status - status of square, alive of dead.
	 * @param fill - color of square
	 * @param stroke - outline of square
	 */

	public Square(int x, int y, int squareSize, boolean status, String fill, String stroke) {
		this.x = x;
		this.y = y;
		this.squareSize = squareSize;
		this.status = status;
		this.fill = status ? "#8BC34A" : fill;
		this.stroke = stroke;
	}

	public int getSquareX() {
		return this.x;
	}

	public int getSquareY() {
		return this.y;
	}

	public String getSquareFill() {
		return this.fill;
	}

	public String getSquareStroke() {
		return this.stroke;
	}

	public int getSquareSize() {
		return this.squareSize;
	}

	public boolean getSquareStatus() {
		return this.status;
	}

	public void setSquareX(int x) {
		this.x = x;
	}

	public void setSquareY(int y) {
		this.y = y;
	}

	public void setSquareFill(String fill) {
		this.fill = fill;
	}

	public void setSquareStatus(boolean status) {
		this.status = status;
	}

	public void setSquareStroke(String stroke) {
		this.stroke = stroke;
	}

	public void setSquareSize(int squareSize) {
		this.squareSize = squareSize;
	}

	public Square clone(){
		return new Square(this.x,this.y,this.squareSize, this.status, this.fill, this.stroke);
	}
}
