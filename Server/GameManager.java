package Server;

import Common.Grid;
import Common.Square;


public class GameManager {

    public Grid round(Grid g) {
        Grid newGrid = g.clone();
        for (Square s : g.getGrid()) {
            newGrid.grid.add(this.constraints(s.clone(),g));
        }
        return newGrid;
    }

    /**
     *
     * @param square
     * @param grid
     * @return Square
     * <p>
     *  x  x  x
     *  x [x] x
     *  x  x  x
     *  possible neighbours (8)
     * </p>
     * Any live cell with fewer than two live neighbours dies, as if by underpopulation.
     * Any live cell with two or three live neighbours lives on to the next generation.
     * Any live cell with more than three live neighbours dies, as if by overpopulation.
     * Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.
     */

    public Square constraints(Square square, Grid grid) {
        int neighbours = this.calcNeighbours(square, grid);

        if (neighbours < 2) {
             this.death(square);
        } else if ((neighbours == 3 || neighbours == 2) && square.getSquareStatus()) {
            this.birth(square);
        } else if (neighbours > 3 && square.getSquareStatus()){
            this.death(square);
        } else if (neighbours == 3 && !square.getSquareStatus()) {
            this.birth(square);
        }
        return square;
}

    private int calcNeighbours(Square square, Grid grid) {
        int n = 0; //neighbours
        for (Square other : grid.getGrid()) {
            boolean alive = other.getSquareStatus();
            if (!alive) continue;
            if (isNeighbour(square, other)) {
                n++;
            }
        }
        return n;
    }
    private boolean isNeighbour(Square square, Square other) {
        int x = square.getSquareX(),
            y = square.getSquareY(),
            px = other.getSquareX(),
            py = other.getSquareY();
        if (x == px && y == py) { //same square
            return false;
        }
        return  x - 1 <= px &&
                px <= x + 1 &&
                y - 1 <= py &&
                py <= y + 1;
    }

    public void death(Square square) {
        square.setSquareStatus(false);
        square.setSquareFill("#FFFFFF");
    }

    public void birth(Square square) {
        square.setSquareStatus(true);
        square.setSquareFill("#8BC34A");
    }
}
