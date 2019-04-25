package src;
import javafx.scene.paint.Color;

public class Square {
	public int x, y, squareSize;
	boolean status;
	Color fill, stroke;

	public Square() {

	}

	public Square(int x, int y, int squareSize, boolean status) {
		this.x = x;
		this.y = y;
		this.squareSize = squareSize;
		this.status = status;
	}
	
	//get
	public int getSquareX() {
		return this.x;
	}

	public int getSquareY() {
		return this.y;
	}

	public Color getSquareFill() {
		return this.fill;
	}

	public Color getSquareStroke() {
		return this.stroke;
	}

	public int getSquareSize() {
		return this.squareSize;
	}

	public boolean getSquareStatus() {
		return this.status;
	}
	//set
	public void setSquareX(int x) {
		this.x = x;
	}

	public void setSquareY(int y) {
		this.y = y;
	}

	public void setSquareFill(Color fill) {
		this.fill = fill;
	}

	public void setSquareStatus(boolean status) {
		this.status = status;
	}

	public void setSquareStroke(Color stroke) {
		this.stroke = stroke;
	}

	public void setSquareSize(int squareSize) {
		this.squareSize = squareSize;
	}


}
