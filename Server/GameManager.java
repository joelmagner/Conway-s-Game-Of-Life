package Server;

import Common.Grid;
import Common.Square;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class GameManager {

    public Grid round(Grid g){
        Grid newGrid = new Grid();
        for (Square square : g.getGrid()) {
            newGrid.grid.add(this.constraints(square,g));
        }
        return newGrid;
    }

    public Square constraints(Square square, Grid grid) {
        // x  x  x
        // x [x] x
        // x  x  x
        // possible neighbours (8)
        int neighbours = this.calcNeighbours(square, grid);

        if (neighbours < 2) {
            this.death(square); // died of loneliness
        } else if ((neighbours == 2 || neighbours == 3) && square.getSquareStatus()) {
            this.birth(square); // alive
        } else if (neighbours > 3) {
            this.death(square); // died of overcrowding
        } else if (neighbours == 3 && !square.getSquareStatus()) {
            this.birth(square); // dead cell => alive cell
        } else {
            this.death(square);
        }
        return square;
    }

    private int calcNeighbours(Square square, Grid grid) {

        int x = square.getSquareX(),
                y = square.getSquareY(),
                n = 0; //neighbours

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
        square.setSquareFill("#FFFFFF");
    }

    public void birth(Square square) {
        square.setSquareStatus(true);
        square.setSquareFill("#8BC34A");
    }
    //overloads
    public void death(Square square, String color) {
        square.setSquareStatus(false);
        square.setSquareFill(color);
    }

    public void birth(Square square, String color) {
        square.setSquareStatus(true);
        square.setSquareFill(color);
    }


}
