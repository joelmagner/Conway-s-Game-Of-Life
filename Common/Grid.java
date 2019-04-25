package Common;

import java.io.Serializable;
import java.util.ArrayList;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Grid implements Serializable {
	public ArrayList<Square> grid = new ArrayList<Square>();
	int gridSize;

	public Grid(){}

	public Grid(int gridSize, int squareSize, double spawnChance) {
		this.gridSize = gridSize;
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				Square square = new Square(i, j, squareSize, Math.random() < spawnChance/100);
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
}
