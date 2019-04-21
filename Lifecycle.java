
import javafx.scene.paint.Color;

public class Lifecycle {

	public Square constraints(Square square, Grid grid) {
		// x  x  x
		// x [x] x
		// x  x  x
		// possible neighbours (8)
		int neighbours = 0;
		neighbours = calcNeighbours(square, grid);
		
		if (neighbours < 2) { 
			this.death(square); // died of loneliness
		} else if (neighbours >= 2 && neighbours <= 3) { 
			this.birth(square); // alive
		} else if (neighbours >= 3) { 
			this.death(square); // died of overcrowding
		} else if (neighbours == 3 && square.getSquareStatus() == false) {
			this.birth(square); // dead cell => alive cell
		} else {
			System.out.println(neighbours + " : " + square.getSquareStatus());
			this.death(square);
		}

		return square;
	}

	private int calcNeighbours(Square square, Grid grid) {

		int x = square.getSquareX(), 
			y = square.getSquareY(), 
			size = square.getSquareSize(), 
			gridSize = grid.gridSize;
			// areaX/Y = x * size

		int n = 0;
		for (Square s : grid.getGrid()) {
			int sx = s.getSquareX(), 
					sy = s.getSquareY();
			boolean st = s.getSquareStatus();

			if (x + 1 	== sx && y + 1 	== sy && st) n++;
			if (x + 1 	== sx && y - 1 	== sy && st) n++;
			if (x + 1 	== sx && y 		== sy && st) n++;
			if (x - 1 	== sx && y + 1 	== sy && st) n++;
			if (x - 1 	== sx && y - 1 	== sy && st) n++;
			if (x - 1 	== sx && y 		== sy && st) n++;
			if (x 		== sx && y + 1 	== sy && st) n++;
			if (x 		== sx && y - 1 	== sy && st) n++;
			//             ^                ^     ^
			//         SquareX           SquareY  Active
		}
		return n;
	}

	public void death(Square square) {
		square.setSquareStatus(false);
		square.setSquareFill(Color.BROWN);
	}

	public void death(Square square, Color color) {
		square.setSquareStatus(false);
		square.setSquareFill(color);
	}

	public void birth(Square square, Color color) {
		square.setSquareStatus(true);
		square.setSquareFill(color);
	}

	public void birth(Square square) {
		square.setSquareStatus(true);
		square.setSquareFill(Color.GREEN);
	}
}
