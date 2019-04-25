package Client;

import Common.Grid;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.*;
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;

public class Client extends Application implements Initializable, Serializable {


	@FXML private MenuItem openConnection;
	@FXML private MenuItem closeConnection;
	@FXML private MenuItem saveConnection;
	@FXML private MenuItem testRender;
	@FXML private MenuItem testRenderX;
	@FXML private MenuItem optionsMenuItem;
	@FXML private MenuItem nextRoundMenuItem;
	@FXML private MenuItem playMenuItem;
	@FXML private MenuItem predefMenuItem;
	private Group group;
	private Parent root;
	private Scene scene;
	private Grid grid;
	private Pane pane;
	private boolean initialRender = true;
	private Socket socket;
	private ObjectInputStream ois;
	private DataOutputStream dos;
	private Render render;
	public Pane getMainPane(){
		return this.pane;
	}

	public void setMainPane(Pane pane){
		this.pane = pane;
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void connectToServer() throws IOException{

		Settings settings = new Settings();
		settings.readSettingsFromFile(settings.connectionFilePath);
		this.socket = new Socket(settings.ipAddress, settings.port);
		this.ois = new ObjectInputStream(socket.getInputStream());
		this.dos = new DataOutputStream(socket.getOutputStream());

		openConnection.setText("Status: Connected");
		openConnection.setDisable(true);

	}

	private void closeConnection(){
		try{
			this.socket.close();
			this.ois.close();
			this.dos.close();
			System.out.println("Connection closed!");
			openConnection.setText("Quick Connect");
			openConnection.setDisable(false);
			this.pane.getChildren().clear();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	private void getGridFromServer() throws IOException{

		try{
			Settings s = new Settings();
			s.readSettingsFromFile(s.settingsFilePath);

			this.dos.writeByte(1);
			this.dos.writeUTF(s.gridSize+":"+s.squareSize+":"+s.spawnChance);
			this.dos.flush();
			this.grid = (Grid) this.ois.readObject();
			System.out.println("Initial grid" + this.grid);

			setMainPane(render.render(this.grid));
			this.group.getChildren().add(getMainPane());

		} catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
	}
	private void getNextRoundFromServer() throws IOException{
		try{

			this.dos.writeByte(2);
			this.dos.writeUTF("New round please");
			this.dos.flush(); // Send data
			this.grid = (Grid) this.ois.readObject();
			System.out.println("next round" + this.grid);

			setMainPane(render.render(this.grid));
			this.group.getChildren().add(getMainPane());

		} catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.render = new Render();
		this.pane = new Pane();
		this.group = new Group();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("style.fxml"));
		loader.setController(this);
		this.root = loader.load();
		this.group.getChildren().addAll(this.root, this.pane);
		this.scene = new Scene(this.group, 400, 425);
		primaryStage.setScene(this.scene);
		primaryStage.setTitle("Game Of Life");
		primaryStage.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		openConnection.setOnAction(e -> {
			try{
				connectToServer();
			}catch(IOException ex){ex.printStackTrace();}
		});

		saveConnection.setOnAction(e -> ConnectionWindow.display());

		closeConnection.setOnAction(e -> closeConnection());

		playMenuItem.setOnAction(e -> {
			try{
				getGridFromServer();
			}catch(IOException ex){ex.printStackTrace();}
		});

		optionsMenuItem.setOnAction(e -> {
			try {
				OptionsWindow.display();
			} catch (FileNotFoundException ex) {ex.printStackTrace();}
		});

		nextRoundMenuItem.setOnAction(e -> {
			try{
				getNextRoundFromServer();
			}catch(IOException ex){ex.printStackTrace();}
		});

		predefMenuItem.setOnAction(e -> PredefWindow.display());


	}
}