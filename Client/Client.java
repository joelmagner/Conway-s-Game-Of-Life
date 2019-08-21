package Client;

import Common.Grid;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.stage.StageStyle;
import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Joel Magnér
 * <p>
 *     The Client.
 *     * GUI - displays the program visually.
 *     * Connects to the server.
 *     * Interface to all other visual elements of this program.
 * </p>
 */

public class Client extends Application implements Initializable, Serializable {

	private static final long serialVersionUID = 1L;
	@FXML private BorderPane pane;
	@FXML private JFXButton exitButton;
	@FXML private FontAwesomeIconView createGridButton;
	@FXML private FontAwesomeIconView connectionIndicator;
	@FXML private FontAwesomeIconView nextRoundButton;
	@FXML private FontAwesomeIconView skipXRoundsButton;
	@FXML private FontAwesomeIconView goToGithub;
	@FXML private FontAwesomeIconView continuousPlayButton;
	@FXML private FontAwesomeIconView inputTextSubmitButton;
	@FXML private FontAwesomeIconView restoreGridButton;
	@FXML private FontAwesomeIconView controlsMenuIcon;
	@FXML private Text goToMyWebsite;
	@FXML private Text connectionButtonText;
	@FXML private Text controlsTextMenuItem;
	@FXML private HBox connectToServerMenuButton;
	@FXML private HBox communicationMenuButton;
	@FXML private HBox controlsMenuButton;
	@FXML private HBox predefinesMenuButton;
	@FXML private HBox settingsMenuButton;
	@FXML private HBox bannerArea;
	@FXML private BorderPane mainWindow;
	@FXML private JFXTextField inputTextField;
	private Parent root;
	private Scene scene;
	private Grid grid;
	private Stage stage;
	private Socket socket;
	private ObjectInputStream ois;
	private DataOutputStream dos;
	private Render render;
	private boolean connected = false;
	private boolean infinitePlay = false;
	private boolean toggleUI = true;
	private String preDefinedValues = null;
	private static double windowX, windowY;
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * @throws IOException
	 * <p>
	 * Used when user wants to restore their grid to the original state.
	 * </p>
	 */

	public void setBackupGrid() throws IOException{
		if(!this.connected) throw new IOException("Not connected!");
		try{
			this.dos.writeByte(4);
			this.dos.writeUTF("");
			this.dos.flush();
			this.grid = (Grid) this.ois.readObject();
		} catch(ClassNotFoundException ex){
			setConnectionStatus(false);
			ex.printStackTrace();
		}
		Platform.runLater(() -> setMainPane(render.render(this.grid)));
	}

	public void setMainPane(BorderPane pane){
		this.pane.getChildren().clear();
		this.pane.getChildren().add(pane);
	}

	/**
	 * <p>
	 *     Creates the connection streams and connects to the server with the users settings.
	 * </p>
	 */

	private void connectToServer(){
		try{
			Settings settings = new Settings();
			settings.readSettingsFromFile(settings.connectionFilePath);
			this.socket = new Socket(settings.ipAddress, settings.port);
			this.ois = new ObjectInputStream(socket.getInputStream());
			this.dos = new DataOutputStream(socket.getOutputStream());
			this.setConnectionStatus(true);
		}catch(IOException ex){
			this.closeConnection();
			this.setConnectionStatus(false);
			ex.printStackTrace();
		}

	}

	private void closeStreams() throws IOException {
		this.dos.flush();
		this.dos.close();
		this.ois.close();
		this.socket.close();
		this.clearConnectionStatus();
		System.out.println("Connection closed!");
	}

	/**
	 * <p>
	 *     Closes any open connections.
	 *     {@link #closeStreams() closeStreams}
	 * </p>
	 */

	private void closeConnection(){
		try{
			closeStreams();
		}catch(IOException ex){
			this.setConnectionStatus(false);
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @throws IOException
	 * <p>
	 *     Fetches the grid from the server.
	 *     Sends byte to let the server know what type of message is coming and the data belonging to it.
	 *     It will also check if the user has attempted to add any predefines to the grid.
	 * </p>
	 */

	private void getGridFromServer() throws IOException{
		try{
			if(!this.connected) throw new IOException("Not connected!");
			if(this.infinitePlay){
				this.infinitePlay = false;
				this.disableMenuButtons(false);
				this.scheduler.shutdown();
				this.continuousPlayButton.setGlyphName("PLAY_CIRCLE");
				this.continuousPlayButton.getStyleClass().remove("button_active");
			}else{
				try{
					this.continuousPlayButton.setGlyphName("PLAY_CIRCLE");
					this.continuousPlayButton.getStyleClass().remove("button_active");
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			Settings s = new Settings();
			s.readSettingsFromFile(s.settingsFilePath);
			this.dos.writeByte(1);
			if (preDefinedValues == null){
				this.dos.writeUTF(s.getGridSettings());
			} else {
				this.dos.writeUTF(s.getGridSettings()+preDefinedValues);
				preDefinedValues = "";
			}
			this.dos.flush();
			this.grid = (Grid) this.ois.readObject();
			this.backupGrid = this.grid;
			this.resizeWindow(s);
			Platform.runLater(() -> setMainPane(this.render.render(this.grid)));
		} catch(ClassNotFoundException ex){
			setConnectionStatus(false);
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @throws IOException
	 * <p>
	 *     Gets the next round from the server.
	 *     Sends a byte to let the server know what type of message it is and then the data beloning to it.
	 *     It will also check if the user has attempted to add any predefines to the grid.
	 * </p>
	 */

	private void getNextRoundFromServer() throws IOException{
		try{
			if(!this.connected) throw new IOException("Not connected!");
			this.dos.writeByte(2);
			if (this.preDefinedValues == null){
				this.dos.writeUTF("");
			}else{
				this.dos.writeUTF(this.preDefinedValues);
				preDefinedValues = "";
			}
			this.dos.flush();
			this.grid = (Grid) this.ois.readObject();
			Platform.runLater(() ->  setMainPane(this.render.render(this.grid)));
		} catch(ClassNotFoundException ex){
			setConnectionStatus(false);
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @param rounds String - amount of rounds the client wants to skip.
	 * @throws IOException
	 * <p>
	 *     same as {@link #getNextRoundFromServer() getNextRoundFromServer} but will
	 *     send along an amount of rounds it wants the server to run.
	 * </p>
	 */

	private void getXRoundsFromServer(String rounds) throws IOException{
		try{
			if(!this.connected) throw new IOException("Not connected!");
			this.dos.writeByte(3);
			this.dos.writeUTF(rounds);
			this.dos.flush();
			this.grid = (Grid) this.ois.readObject();
			setMainPane(render.render(this.grid));

		} catch(ClassNotFoundException ex){
			setConnectionStatus(false);
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @param s
	 * <p>
	 *     Will allow for resizing of the program when the grid changes.
	 * </p>
	 */

	private void resizeWindow(Settings s){
		int gridSizeXY[] = new int[] {
				s.squareSize*s.gridSize+166+20, //x
				s.squareSize*s.gridSize+90		//y
		};
		this.stage.setMinWidth(gridSizeXY[0]);
		this.stage.setMinHeight(gridSizeXY[1]);
		this.stage.setMaxWidth(gridSizeXY[0]);
		this.stage.setMaxHeight(gridSizeXY[1]);
	}

	private void setButtonStatus(FontAwesomeIconView button, boolean status){
		if(status){
			button.getStyleClass().add("button_active");
			button.setGlyphName("PLAY");
		}else {
			button.setGlyphName("PAUSE");
			button.getStyleClass().remove("button_active");
		}
	}

	private void clearConnectionStatus(){
		this.connected = false;
		this.connectionButtonText.setText("Disconnected");
		this.connectionButtonText.getStyleClass().remove("connection_status_on");
		this.connectToServerMenuButton.getStyleClass().remove("connection_status_on");
		this.createGridButton.getStyleClass().remove("button_enabled_from_disabled");
		this.connectionIndicator.getStyleClass().remove("connection_status_on");
	}

	private void setConnectionStatus(boolean status){
		this.connected = status;
		if(status){
			this.connectionIndicator.getStyleClass().add("connection_status_on");
			this.connectToServerMenuButton.getStyleClass().add("connection_status_on");
			this.connectionButtonText.getStyleClass().add("connection_status_on");
			this.connectionButtonText.setText("Connected");
			this.createGridButton.getStyleClass().add("button_enabled_from_disabled");
			this.connectionIndicator.getStyleClass().remove("connection_status_off");
			this.connectToServerMenuButton.getStyleClass().remove("connection_status_off");
			this.connectionButtonText.getStyleClass().remove("connection_status_off");
		}else{
			this.connectionIndicator.getStyleClass().add("connection_status_off");
			this.connectToServerMenuButton.getStyleClass().add("connection_status_off");
			this.connectionButtonText.getStyleClass().add("connection_status_off");
			this.connectionButtonText.setText("Connection Error");
			this.createGridButton.getStyleClass().remove("button_enabled_from_disabled");
		}
	}

	private void disableMenuButtons(boolean status){
		if(!status){
			this.nextRoundButton.getStyleClass().remove("button_disabled");
			this.nextRoundButton.setDisable(false);
			this.restoreGridButton.getStyleClass().remove("button_disabled");
			this.restoreGridButton.setDisable(false);
			this.skipXRoundsButton.getStyleClass().remove("button_disabled");
			this.skipXRoundsButton.setDisable(false);
		}else {
			this.nextRoundButton.getStyleClass().add("button_disabled");
			this.nextRoundButton.setDisable(true);
			this.restoreGridButton.getStyleClass().add("button_disabled");
			this.restoreGridButton.setDisable(true);
			this.skipXRoundsButton.getStyleClass().add("button_disabled");
			this.skipXRoundsButton.setDisable(true);
		}
	}

	/**
	 *
	 * @param n Node
	 * @param stage Stage
	 * <p>
	 *     Allows the user to move the program across the screen while the default menu-bar is disabled.
	 *     Adds event listener to the window to look for mouse clicks.
	 * </p>
	 */

	private void addDragListenerToWindow(final Node n, Stage stage){

		n.setOnMousePressed((MouseEvent me) -> {
			this.windowX = n.getScene().getWindow().getX() - me.getScreenX();
			this.windowY = n.getScene().getWindow().getY() - me.getScreenY();
		});

		n.setOnMouseDragged((MouseEvent me) -> {
			stage.setX(me.getScreenX() + this.windowX);
			stage.setY(me.getScreenY() + this.windowY);
		});
	}

	private void toggleControls(boolean status){
		Platform.runLater(() -> {
			this.createGridButton.setVisible(status);
			this.continuousPlayButton.setVisible(status);
			this.nextRoundButton.setVisible(status);
			this.skipXRoundsButton.setVisible(status);
			this.restoreGridButton.setVisible(status);
			this.goToGithub.setVisible(status);
			this.goToMyWebsite.setVisible(status);
			if(!status){
				this.controlsMenuButton.getStyleClass().add("active_menu_item");
				this.controlsTextMenuItem.getStyleClass().add("active_menu_item");
				this.controlsMenuIcon.getStyleClass().add("active_menu_item");
			}else{
				this.controlsMenuButton.getStyleClass().remove("active_menu_item");
				this.controlsTextMenuItem.getStyleClass().remove("active_menu_item");
				this.controlsMenuIcon.getStyleClass().remove("active_menu_item");
			}
		});
	}

	/**
	 *
	 * @param stage Stage
	 * @throws Exception
	 * <p>
	 *     start method - will initialize the GUI.
	 * </p>
	 */

	@Override
	public void start(Stage stage) throws Exception {
		this.render = new Render();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("style.fxml"));
		loader.setController(this);
		this.root = loader.load();
		this.stage = stage;
		this.scene = new Scene(root, 700, 600);

		stage.initStyle(StageStyle.UNDECORATED);
		this.addDragListenerToWindow(this.mainWindow,stage);
		stage.setScene(this.scene);
		stage.getIcons().add(new Image("file:assets/GameOfLifeIconWhite.png"));
		stage.show();
	}


	/**
	 *
	 * @param location URL
	 * @param resources ResourceBundle
	 * <p>
	 *     Due to how JavaFX handles lifetime events,
	 *     initialize() is run after {@link #start(Stage) start} and is part of javaFX.
	 *     initialize() defines user interaction behaviour with the GUI.
	 * </p>
	 */


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.inputTextSubmitButton.setVisible(false);
		this.inputTextField.setVisible(false);

		this.exitButton.setOnAction(e -> {
			scheduler.shutdown();
			Platform.exit();
		});

		/***********Left Sidebar****************/
		this.connectToServerMenuButton.setOnMouseClicked(e -> Platform.runLater(() ->  {
			if(!this.connected){
				this.connectToServer();
			}else{
				this.closeConnection();
			}
		}));

		this.settingsMenuButton.setOnMouseClicked(e -> Platform.runLater(() -> {
			try{
				this.preDefinedValues = OptionsWindow.display();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}));

		this.predefinesMenuButton.setOnMouseClicked(e -> Platform.runLater(() -> this.preDefinedValues = PredefWindow.display()));

		this.communicationMenuButton.setOnMouseClicked(e -> Platform.runLater(() -> ConnectionWindow.display()));

		this.controlsMenuButton.setOnMouseClicked(e -> {
			this.toggleUI = !toggleUI;
			this.toggleControls(this.toggleUI);
		});

		/***********Top Navbar****************/

		this.createGridButton.setOnMouseClicked(e -> {
			try{
				if(!this.connected) throw new IOException("Not connected!");
				this.getGridFromServer();
			}catch(IOException ex){ex.printStackTrace();}
		});

		this.continuousPlayButton.setOnMouseClicked(e -> {
			this.infinitePlay = !this.infinitePlay;
			if(!this.infinitePlay){
				this.setButtonStatus(continuousPlayButton,false);
				this.disableMenuButtons(false);
			}else {
				this.setButtonStatus(continuousPlayButton,true);
				this.disableMenuButtons(true);
			}
			if(this.scheduler.isShutdown()){
				this.scheduler = Executors.newScheduledThreadPool(1);
			}
			try{
				Runnable scheduledRound = () -> {
					try {
						if(!this.infinitePlay){
							this.scheduler.shutdown();
							return;
						}
						this.getNextRoundFromServer();
					} catch (IOException ex) {
						this.setConnectionStatus(false);
						ex.printStackTrace();
					}
				};
				if(this.infinitePlay){
					this.scheduler.scheduleAtFixedRate(scheduledRound, 0, 200, TimeUnit.MILLISECONDS);
				}else{
					this.scheduler.shutdown();
				}
			}catch(Exception ex){
				this.scheduler.shutdown();
				this.setButtonStatus(this.continuousPlayButton,false);
				ex.printStackTrace();
			}
		});

		this.nextRoundButton.setOnMouseClicked(e -> {
			if(!this.connected) return;
				Platform.runLater(() ->  {
					try{
						this.getNextRoundFromServer();
					}catch(IOException ex){ex.printStackTrace();}
				});
		});

		this.skipXRoundsButton.setOnMouseClicked(e -> {
			if(!this.connected) return;
			this.inputTextSubmitButton.setVisible(true);
			this.inputTextField.setVisible(true);
			this.inputTextSubmitButton.setOnMouseClicked(action -> {
				if(!this.inputTextField.getText().trim().isEmpty()){
					String rounds = this.inputTextField.getText();
					this.inputTextField.clear();
					try{
						getXRoundsFromServer(rounds);
					} catch(IOException ex){
						ex.printStackTrace();
					}
				}
			this.inputTextSubmitButton.setVisible(false);
			this.inputTextField.setVisible(false);
			});
		});

		this.restoreGridButton.setOnMouseClicked(e -> Platform.runLater(() -> {
			try {
				this.setBackupGrid();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}));

		this.goToGithub.setOnMouseClicked(e -> {
			HostServicesDelegate hostServices = HostServicesFactory.getInstance(this);
			hostServices.showDocument("https://github.com/joelmagner/");
		});

		this.goToMyWebsite.setOnMouseClicked(e -> {
			HostServicesDelegate hostServices = HostServicesFactory.getInstance(this);
			hostServices.showDocument("www.joelmagner.com");
		});
	}
}