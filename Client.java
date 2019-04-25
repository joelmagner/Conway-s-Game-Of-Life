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
	@FXML private MenuItem testRenderX;
	@FXML private MenuItem optionsMenuItem;
	private Group group;
	private Parent root;
	private Scene scene;
	private Grid grid;
	private boolean initialRender = true;

	public static void main(String[] args) {
		launch(args);
	}

	private void connectToServer() {
		try {
			Settings settings = new Settings();
			Socket socket = new Socket("127.0.0.1", 5000);

			//obj-streams
			ObjectInputStream clientInputStream = new ObjectInputStream(socket.getInputStream());
			//str-streams
			DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
			// Send first message
			dOut.writeByte(1);
			dOut.writeUTF("20:20:15"); //gridsettings
			dOut.flush(); // Send off the data

			this.grid = (Grid) clientInputStream.readObject();

			System.out.println("new grid! ");
			new Render(this.grid);
			this.group.getChildren().addAll(this.grid.p);

			clientInputStream.close();
			dOut.writeByte(-1);
			dOut.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.group = new Group();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("style.fxml"));
		loader.setController(this);
		this.root = loader.load();
		this.group.getChildren().addAll(this.root);
		this.scene = new Scene(this.group, 400, 425);
		primaryStage.setScene(this.scene);
		primaryStage.setTitle("Game Of Life");
		primaryStage.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		testRender.setOnAction(e -> {
			System.out.println("Simulating render");
			if (this.initialRender) {
				this.initialRender = false;
				Settings s = new Settings();
				s.readSettingsFromFile(s.settingsFilePath);
				this.grid = new Grid(s.gridSize, s.squareSize, s.spawnChance);
				this.grid.p.setTranslateY(37);
				this.group.getChildren().addAll(this.grid.p);
			}
			new Render(this.grid);
		});

		testRenderX.setOnAction(e -> {
			int input = OptionsWindow.prompt();
			for (int i = 0; i < input; i++) {
				new Render(this.grid);
			}
		});

		openConnection.setOnAction(e -> connectToServer());

		saveConnection.setOnAction(e -> {
			try{
				ConnectionWindow.display();
			}catch(IOException ex){ex.printStackTrace();}
		});

		optionsMenuItem.setOnAction(e -> {
			try {
				OptionsWindow.display();
			} catch (FileNotFoundException ex) {ex.printStackTrace();}
		});
	}
}