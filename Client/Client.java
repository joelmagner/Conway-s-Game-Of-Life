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

public class Client extends Application implements Initializable, Serializable {



	private static final long serialVersionUID = 1L;
	@FXML
	private BorderPane pane;
	@FXML private JFXButton exitButton;
	@FXML private FontAwesomeIconView createGridButton;
	@FXML private FontAwesomeIconView connectionIndicator;
	@FXML private FontAwesomeIconView nextRoundButton;
	@FXML private FontAwesomeIconView skipXRoundsButton;
	@FXML private FontAwesomeIconView goToGithub;
	@FXML private FontAwesomeIconView continuousPlayButton;
	@FXML private FontAwesomeIconView inputTextSubmitButton;
	@FXML private FontAwesomeIconView restoreGridButton;
	@FXML private Text goToMyWebsite;
	@FXML private Text connectionButtonText;
	@FXML private HBox settingsButton;
	@FXML private HBox connectToServerButton;
	@FXML private HBox communicationMenuButton;
	@FXML private HBox controlsMenuButton;
	@FXML private BorderPane mainWindow;
	@FXML private JFXTextField inputTextField;
	private Parent root;
	private Scene scene;
	private Grid grid,
			backupGrid;
	private Stage stage;
	private Socket socket;
	private ObjectInputStream ois;
	private DataOutputStream dos;
	private Render render;
	private boolean connected = false;
	public boolean infinitePlay = false;
	private String preDefinedValues = null;
	private static double windowX, windowY;
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public void setBackupGrid(){
		this.grid = this.backupGrid;
		Platform.runLater(() -> setMainPane(render.render(this.grid)));
	}

	public void setMainPane(BorderPane pane){
		this.pane.getChildren().clear();
		this.pane.getChildren().add(pane);
	}

	public static void main(String[] args) {
		launch(args);
	}

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

	private void setButtonStatus(FontAwesomeIconView button, boolean status){
		if(status){
			button.getStyleClass().add("button_active");
			this.continuousPlayButton.setGlyphName("PLAY");
		}else {
			this.continuousPlayButton.setGlyphName("PAUSE");
			button.getStyleClass().remove("button_active");
		}
	}
	private void clearConnectionStatus(){
		connectionButtonText.setText("Disconnected");
		connectionButtonText.getStyleClass().remove("connection_status_on");
		connectToServerButton.getStyleClass().remove("connection_status_on");
		createGridButton.getStyleClass().remove("button_enabled_from_disabled");
		connectionIndicator.getStyleClass().remove("connection_status_on");
	}
	private void setConnectionStatus(boolean status){
		this.connected = status;
		if(status){
			connectionIndicator.getStyleClass().add("connection_status_on");
			connectToServerButton.getStyleClass().add("connection_status_on");
			connectionButtonText.getStyleClass().add("connection_status_on");
			connectionButtonText.setText("Connected");
			createGridButton.getStyleClass().add("button_enabled_from_disabled");
			connectionIndicator.getStyleClass().remove("connection_status_off");
			connectToServerButton.getStyleClass().remove("connection_status_off");
			connectionButtonText.getStyleClass().remove("connection_status_off");

		}else{
			connectionIndicator.getStyleClass().add("connection_status_off");
			connectToServerButton.getStyleClass().add("connection_status_off");
			connectionButtonText.getStyleClass().add("connection_status_off");
			connectionButtonText.setText("Connection Error");
			createGridButton.getStyleClass().remove("button_enabled_from_disabled");
		}
	}

	private void resizeWindow(Settings s){
		int gridSizeXY[] = new int[] {
				s.squareSize*s.gridSize+166+20, //x
				s.squareSize*s.gridSize+90		//y
		};
		this.stage.setMinWidth(gridSizeXY[0]);
		this.stage.setMinHeight(gridSizeXY[1]);
	}

	private void closeConnection(){
		try{
			this.dos.writeByte(-1);
			this.socket.close();
			this.ois.close();
			this.dos.close();
			System.out.println("Connection closed!");
			this.clearConnectionStatus();
		}catch(IOException ex){
			this.setConnectionStatus(false);
			ex.printStackTrace();
		}
	}

	private void getGridFromServer() throws IOException{
		try{
			if(!this.connected) return;

			Settings s = new Settings();
			s.readSettingsFromFile(s.settingsFilePath);
			this.dos.writeByte(1);
			if (preDefinedValues == null){
				this.dos.writeUTF(s.getGridSettings());
			} else {
				this.dos.writeUTF(s.getGridSettings()+preDefinedValues);
			}
			this.dos.flush();
			this.grid = (Grid) this.ois.readObject();
			this.backupGrid = this.grid;
			this.resizeWindow(s);
			Platform.runLater(() -> setMainPane(render.render(grid)));
		} catch(ClassNotFoundException ex){
			setConnectionStatus(false);
			ex.printStackTrace();
		}
	}
	private void getNextRoundFromServer() throws IOException{
		try{
			if(!this.connected) return;
			this.dos.writeByte(2);
			if (preDefinedValues != null){
				this.dos.writeUTF(this.preDefinedValues);
				this.preDefinedValues = "";
			}else{
				this.dos.writeUTF("");
			}
			this.dos.flush();
			this.grid = (Grid) this.ois.readObject();
			Platform.runLater(() ->  setMainPane(render.render(this.grid)));
		} catch(ClassNotFoundException ex){
			setConnectionStatus(false);
			ex.printStackTrace();
		}
	}

	private void getXRoundsFromServer(String rounds) throws IOException{
		try{
			if(!this.connected) return;
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

	@Override
	public void start(Stage stage) throws Exception {
		this.render = new Render();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("style.fxml"));
		loader.setController(this);
		this.root = loader.load();
		this.stage = stage;
		this.scene = new Scene(root, 700, 600);

		stage.initStyle(StageStyle.UNDECORATED);
		addDragListenerToWindow(this.mainWindow,stage);
		stage.setScene(this.scene);
		stage.setTitle("Game Of Life");
		stage.show();
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		this.inputTextSubmitButton.setVisible(false);
		this.inputTextField.setVisible(false);

		this.exitButton.setOnAction(e -> {
			scheduler.shutdown();
			Platform.exit();
		});

		this.createGridButton.setOnMouseClicked(e -> {
			if(!this.connected) return;
			try{
				this.getGridFromServer();
			}catch(IOException ex){ex.printStackTrace();}
		});

		this.connectToServerButton.setOnMouseClicked(e -> Platform.runLater(() ->  {
			if(!this.connected){
				this.connectToServer();
			}else{
				this.closeConnection();
			}
		}));

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

		this.settingsButton.setOnMouseClicked(e -> Platform.runLater(() -> {
			try{
				this.preDefinedValues = OptionsWindow.display();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}));

		this.continuousPlayButton.setOnMouseClicked(e -> {
			this.infinitePlay = !this.infinitePlay;
			if(!this.infinitePlay){
				this.setButtonStatus(continuousPlayButton,false);
			}else {
				this.setButtonStatus(continuousPlayButton,true);
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
						getNextRoundFromServer();
					} catch (IOException ex) {
						this.setConnectionStatus(false);
						ex.printStackTrace();
					}
				};
				if(this.infinitePlay){
					this.scheduler.scheduleAtFixedRate(scheduledRound, 0, 1, TimeUnit.SECONDS);
				}else{
					this.scheduler.shutdown();
				}
			}catch(Exception ex){
				this.scheduler.shutdown();
				this.setButtonStatus(this.continuousPlayButton,false);
				ex.printStackTrace();
			}
		});

		this.controlsMenuButton.setOnMouseClicked(e -> Platform.runLater(() -> ConnectionWindow.display()));

		this.communicationMenuButton.setOnMouseClicked(e -> {
			Platform.runLater(() -> this.preDefinedValues = PredefWindow.display());
		});

		this.restoreGridButton.setOnMouseClicked(e -> this.setBackupGrid());

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