package Client;

import Common.Grid;
import Common.Square;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.Serializable;

public class Render implements Serializable {


    public Render(){

    }

    public Pane render(Grid g) {
        Pane p = new Pane();
        for (Square square : g.getGrid()) {
            int x 			= square.getSquareX(),
                y 			= square.getSquareY(),
                size 		= square.getSquareSize();
            Rectangle rect 	= new Rectangle(x*size,y*size,size, size);

            rect.setFill(Color.valueOf(square.getSquareFill()));

            rect.setStroke(Color.BLACK);
            p.setTranslateY(37);
            p.getChildren().add(rect);
        }
        return p;
    }
}
