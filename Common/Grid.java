package Common;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Joel Magnér
 * <p>
 *     Responsible for the grid logic.
 *     2D-x-by-y grid.
 * </p>
 */

public class Grid implements Serializable {
	public ArrayList<Square> grid = new ArrayList<>();
	int gridSize, squareSize;

	/**
	 *
	 * @param gridSize - size of grid
	 * @param squareSize - size of square
	 * @param grid - Arraylist of class type Square
	 *
	 */

	public Grid(int gridSize, int squareSize, ArrayList<Square> grid){
		this.gridSize = gridSize;
		this.squareSize = squareSize;
		this.grid = grid;
	}

	/**
	 *
	 * @param gridSize int - size of grid.
	 * @param squareSize int - size of square.
	 * @param spawnChance - fetched from <a href="../assets/settings.txt">user settings</a>
	 *
	 * <p>
	 *		Will generate a grid made out of squares.
	 *   	Adds the newly created squares to the grid.
	 * </p>
	 */
	public Grid(int gridSize, int squareSize, double spawnChance) {
		this.gridSize = gridSize;
		this.squareSize = squareSize;
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				Square square = new Square(i, j, squareSize, Math.random() < spawnChance/100, "#ffffff", "#f5f6fa");
				this.grid.add(square);
			}
		}
	}

	public ArrayList<Square> getGrid() {
		return this.grid;
	}

	public void setGrid(ArrayList<Square> grid) {
		this.grid = grid;
	}
	public int getGridSize() {
		return this.gridSize;
	}
	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}
	public int getSquareSize() {
		return this.squareSize;
	}
	public void setSquareSize(int squareSize) {
		this.squareSize = squareSize;
	}
	public Grid clone(){
		return new Grid(this.gridSize,this.squareSize, new ArrayList<Square>());
	}
}
