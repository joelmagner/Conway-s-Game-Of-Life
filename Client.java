package src;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.scene.*;
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;


public class Client extends Application implements Initializable {


	@FXML private MenuItem openConnection;
	@FXML private MenuItem closeConnection;
	@FXML private MenuItem testConnection;
	@FXML private MenuItem saveConnection;
	@FXML private MenuItem testRender;
	private Group group;
	private Parent root;
	private Scene scene;
	private Grid grid;
	public static void main(String[] args) {
		launch(args);
	}

	private void connectToServer(){
		try {

			Grid newGrid = new Grid(20,20,15); //will have to due for now...
			Socket socketConnection = new Socket("127.0.0.1", 5000);


			ObjectOutputStream clientOutputStream = new ObjectOutputStream(socketConnection.getOutputStream());
			ObjectInputStream clientInputStream = new ObjectInputStream(socketConnection.getInputStream());

			clientOutputStream.writeObject(newGrid);

			newGrid = (Grid)clientInputStream.readObject();

			//i'm assuming that I need to retrive a grid-object here,
			//and just call "new Render(newGrid);"
			//afterwards, send a new request from server,

			clientOutputStream.close();
			clientInputStream.close();

		} catch (Exception e) {System.out.println(e); }
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.group = new Group();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("style.fxml"));
		loader.setController(this);
		this.root = loader.load();
		this.grid = new Grid(20,20,15);		// (GridSize:20, squareSize:20, spawnChance:15%).
		grid.p.setTranslateY(25);											// visual offset. (compensate for menubar)
		this.group.getChildren().addAll(grid.p,this.root);
		new Render(grid);
		this.scene = new Scene(this.group, 400, 425);


		primaryStage.setScene(this.scene);
		primaryStage.setTitle("Game Of Life");
		primaryStage.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		testRender.setOnAction(e -> {
			System.out.println("Simulating render");
			new Render(this.grid);
		});
		openConnection.setOnAction(e -> connectToServer());
	}
}