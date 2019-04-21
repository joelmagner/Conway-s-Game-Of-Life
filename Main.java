import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class Main extends Application{

	public void renderGrid(Pane p, Lifecycle l, Grid g) {
		
        for (Square square : g.getGrid()) {
        	int x 			= square.getSquareX(),
    			y 			= square.getSquareY(),
    			size 		= square.getSquareSize(),
    			gridSize	= g.getGridSize();
        	Rectangle rect 	= new Rectangle(x*size,y*size,size, size);
        	if(square.getSquareStatus()) {
        		rect.setFill(Color.AQUAMARINE); 		// initial values
        	} else {
	        	square = l.constraints(square, g); 		//check each squares constraints
        		rect.setFill(square.getSquareFill()); 	//square has gotten its new values returned
        		/*	red = dead
        			green = alive
        			blue = initial values 
    			*/
        	}	
        	rect.setStroke(Color.BLACK);
        	p.getChildren().add(rect);
        }
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Grid g = new Grid(20, 20, 4); // gridsize, rect.size, spawn percentage (in %)
		g.p.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
		Scene scene = new Scene(g.p, 400, 400);
		Lifecycle start = new Lifecycle();
		primaryStage.setScene(scene);
		primaryStage.show();
		this.renderGrid(g.p, start, g);
	}
}
