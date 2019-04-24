package src;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class Render implements Serializable {


    public Render(){

    }

    public Render(Grid g) {
        for (Square square : g.getGrid()) {
            int x 			= square.getSquareX(),
                y 			= square.getSquareY(),
                size 		= square.getSquareSize();
            Rectangle rect 	= new Rectangle(x*size,y*size,size, size);

            square = this.constraints(square, g);
            rect.setFill(square.getSquareFill());
            /*	red = dead
                green = alive
            */
            rect.setStroke(Color.BLACK);
            g.p.getChildren().add(rect);
        }
    }

    /*
    *
    * constraints is the method that calculates what state each square is.
    * TL;DR - each square can either be dead or alive.
    *
    * returns: Square object from within a Grid.
    *
    * */

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

        square.setSquareFill(Color.valueOf("#FFFFFF"));
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
        square.setSquareFill(Color.valueOf("#8BC34A"));
    }
}
