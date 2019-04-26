package Client;

import Common.Grid;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
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
	@FXML private MenuItem skipRoundsMenuItem;
	@FXML private MenuItem predefMenuItem;
	@FXML private MenuBar menuBarItem;
	private Group group;
	private Parent root;
	private Scene scene;
	private Grid grid;
	private Pane pane;
	private Socket socket;
	private ObjectInputStream ois;
	private DataOutputStream dos;
	private Render render;
	private boolean connected = false;
	private String preDefs = null;
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
		try{
			Settings settings = new Settings();
			settings.readSettingsFromFile(settings.connectionFilePath);
			this.socket = new Socket(settings.ipAddress, settings.port);
			this.ois = new ObjectInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
			this.connected = true;
			openConnection.setText("Status: Connected");
			openConnection.setDisable(true);
			closeConnection.setText("Disconnect");
			closeConnection.setDisable(false);
		}catch(IOException ex){
			this.connected = false;
			ex.printStackTrace();
		}

	}

	private void closeConnection(){
		try{
			this.dos.writeByte(-1);
			this.socket.close();
			this.ois.close();
			this.dos.close();
			this.connected = false;
			System.out.println("Connection closed!");
			openConnection.setText("Quick Connect");
			openConnection.setDisable(false);
			closeConnection.setText("Not Connected");
			closeConnection.setDisable(true);
			this.pane.getChildren().clear();
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	private void getGridFromServer() throws IOException{

		try{
			if(!this.connected) return;

			System.out.println("körde");
			Settings s = new Settings();
			s.readSettingsFromFile(s.settingsFilePath);

			this.dos.writeByte(1);
			if(preDefs == null){
				this.dos.writeUTF(s.getGridSettings());
			}else {
				this.dos.writeUTF(s.getGridSettings()+preDefs);
			}
			this.dos.flush();
			this.grid = (Grid) this.ois.readObject();
			setMainPane(render.render(this.grid));
			this.group.getChildren().add(getMainPane());

		} catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
	}
	private void getNextRoundFromServer() throws IOException{
		try{
			if(!this.connected) return;
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

	private void getXRoundsFromServer(String rounds) throws IOException{
		try{
			if(!this.connected) return;
			this.dos.writeByte(3);
			this.dos.writeUTF(rounds);
			this.dos.flush(); // Send data
			this.grid = (Grid) this.ois.readObject();
			System.out.println("next round" + this.grid);
			setMainPane(render.render(this.grid));
			this.group.getChildren().add(getMainPane());

		} catch(ClassNotFoundException ex){
			ex.printStackTrace();
		}
	}

	private void initialSetup() throws IOException{
		this.render = new Render();
		this.pane = new Pane();
		this.group = new Group();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("style.fxml"));

		loader.setController(this);
		this.root = loader.load();
		this.group.getChildren().addAll(this.root, this.pane);
		this.scene = new Scene(this.group, 400, 425);
		closeConnection.setText("Not connected");
		closeConnection.setDisable(true);
		menuBarItem.setMinWidth(1920);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		initialSetup();

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
			if(!this.connected) return;
			try{
				getGridFromServer();
			}catch(IOException ex){ex.printStackTrace();}
		});

		optionsMenuItem.setOnAction(e -> {
			if(!this.connected) return;
			try {
				OptionsWindow.display();
			} catch (FileNotFoundException ex) {ex.printStackTrace();}
		});

		nextRoundMenuItem.setOnAction(e -> {
			if(!this.connected) return;
			try{
				getNextRoundFromServer();
			}catch(IOException ex){ex.printStackTrace();}
		});

		predefMenuItem.setOnAction(e -> preDefs = PredefWindow.display());

		skipRoundsMenuItem.setOnAction(e -> {
			if(!this.connected) return;
			String rounds = Integer.toString(OptionsWindow.prompt());
			try{
				getXRoundsFromServer(rounds);
			} catch(IOException ex){
				ex.printStackTrace();
			}
		});

	}
}