package Server;

import Common.Grid;
import Common.Square;


public class GameManager {

    /**
     *
     * @param g Grid
     * @return Grid
     * <p>
     *     Overall function that is responsible for running one instance of the logic.
     *     Creates a clone of the grid, and then performs the calculation on the original grid
     *     whilst updating each square and adding its newly calculated value to the new grid.
     *     We want to avoid changing values of the current instance of the grid and changing those values,
     *     hence why we make the cloned grid.
     * </p>
     */
    public Grid round(Grid g) {
        Grid newGrid = g.clone();
        for (Square s : g.getGrid()) {
            newGrid.grid.add(this.constraints(s.clone(),g));
        }
        return newGrid;
    }

    /**
     *
     * @param square Square
     * @param grid Grid
     * @return Square
     * <p>
     *  x  x  x
     *  x [x] x
     *  x  x  x
     *  possible neighbours (8)
     *  Main logical function for the gamemanager, will decide along with
     *  {@link #calcNeighbours(Square, Grid) calcNeighbours} what squares are active in the grid.
     * </p>
     * <a href="https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life">Conways game of life</a>
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

    /**
     *
     * @param square Square
     * @param grid Grid
     * @return n int - neighbours.
     * <p>
     *     will calculate the amount of living neighbours for each and every square.
     * </p>
     */

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

    /**
     *
     * @param square Square
     * @param other Square
     * @return boolean
     * <p>
     *     looks at one square in the grid, compares all other squares to that one,
     *     and checks if they are neighbours.
     *     This result will be then later on be used in {@link #constraints(Square, Grid) constraints}
     * </p>
     */

    private boolean isNeighbour(Square square, Square other) {
        int x = square.getSquareX(),
            y = square.getSquareY(),
            ox = other.getSquareX(),
            oy = other.getSquareY();
        if (x == ox && y == oy) { //same square
            return false;
        }
        return  x - 1 <= ox &&
                ox <= x + 1 &&
                y - 1 <= oy &&
                oy <= y + 1;
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
