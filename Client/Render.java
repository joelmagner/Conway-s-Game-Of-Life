package Client;

import Common.Grid;
import Common.Square;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.io.Serializable;

/**
 * @author Joel Magnér
 * <p>
 *     Renders the grid onto the clients screen.
 * </p>
 */

public class Render implements Serializable {


    public Render(){

    }

    /**
     *
     * @param g Grid
     * @return BorderPane
     * <p>
     *     Will render the squares properties onto the borderPane element in the client.
     * </p>
     */

    public BorderPane render(Grid g) {
        BorderPane p = new BorderPane();
        for (Square square : g.getGrid()) {
            int x 			= square.getSquareX(),
                y 			= square.getSquareY(),
                size 		= square.getSquareSize();
            Rectangle rect 	= new Rectangle(x*size,y*size,size, size);
            String fill = square.getSquareFill();
            String stroke = square.getSquareStroke();

            rect.setFill(Color.valueOf(fill));
            rect.setStroke(Color.valueOf(stroke));

            p.getChildren().add(rect);
        }
        return p;
    }
}
