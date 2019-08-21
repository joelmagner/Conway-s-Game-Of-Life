package Client;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

/**
 * @author Joel Magnér
 * <p>
 *     Option window for the client.
 *     * Presents usermade settings to the grid.
 *     * Predef editor with autocompletion.
 *     * Allows for the creation and naming of new predefines.
 * </p>
 */

public class OptionsWindow{

    static String preDefValues;
    static String fileName;
    static int keyPresses = 0;
    public static String display() {


        //load settings if there are any
        Settings s = new Settings();
        s.readSettingsFromFile(s.settingsFilePath);

        //GridsizeBox
        HBox gridSizeBox = new HBox();
        gridSizeBox.setPadding(new Insets(10,10,10,10));
        gridSizeBox.setSpacing(10);
        Label labelGridSize = new Label("Grid Size");
        labelGridSize.setStyle("-fx-text-inner-color:#fff;");
        labelGridSize.getStyleClass().add("label_format");
        gridSizeBox.getStyleClass().add("hover_row");
        JFXTextField gridSize = new JFXTextField();
        gridSize.setText(Integer.toString(s.gridSize));
        labelGridSize.setPrefSize(100,20);
        gridSize.setPrefSize(210,20);
        gridSizeBox.getChildren().addAll(labelGridSize,gridSize);

        //SquareSizeBox
        HBox squareSizeBox = new HBox();
        squareSizeBox.setPadding(new Insets(10,10,10,10));
        squareSizeBox.setSpacing(10);
        Label labelSquareSize = new Label("Square Size");
        labelSquareSize.getStyleClass().add("label_format");
        squareSizeBox.getStyleClass().add("hover_row");
        JFXTextField squareSize = new JFXTextField();
        squareSize.setText(Integer.toString(s.squareSize));
        labelSquareSize.setPrefSize(100,20);
        squareSize.setPrefSize(210,20);
        squareSizeBox.getChildren().addAll(labelSquareSize,squareSize);

        //SpawnChanceBox
        HBox spawnChanceBox = new HBox();
        spawnChanceBox.setPadding(new Insets(10,10,10,10));
        spawnChanceBox.setSpacing(10);
        Label labelSpawnChance = new Label("Spawn %");
        labelSpawnChance.getStyleClass().add("label_format");
        spawnChanceBox.getStyleClass().add("hover_row");
        JFXTextField spawnChance = new JFXTextField();
        spawnChance.setText(Integer.toString(s.spawnChance));
        labelSpawnChance.setPrefSize(100,20);
        spawnChance.setPrefSize(210,20);
        spawnChanceBox.getChildren().addAll(labelSpawnChance,spawnChance);

        HBox preDefBox = new HBox();
        preDefBox.setPadding(new Insets(0,10,10,10));
        preDefBox.setSpacing(10);
        Label labelPredef = new Label("Predefine");
        labelPredef.getStyleClass().add("label_format");
        preDefBox.getStyleClass().add("hover_row");
        JFXTextArea preDef = new JFXTextArea();

        preDef.setOnKeyReleased(e -> {
            if(e.getCode() != KeyCode.BACK_SPACE){
                keyPresses++;
                switch(keyPresses){
                    case 1:
                        preDef.setText(preDef.getText()+"x");
                        break;
                    case 2:
                        preDef.setText(preDef.getText()+" ");
                        keyPresses=0;
                        break;
                    default:
                        break;
                }
                preDef.selectPositionCaret(preDef.getText().length()+1);
                preDef.deselect();
            }
        });
        FontAwesomeIconView savePreDefToFileBtn = new FontAwesomeIconView();
        savePreDefToFileBtn.setGlyphName("CLOUD_DOWNLOAD");
        savePreDefToFileBtn.setSize("2em");
        savePreDefToFileBtn.setStyle("-fx-fill:#2ecc71");
        labelPredef.setPrefSize(100,20);
        preDef.setPrefSize(210,20);
        preDefBox.setAlignment(Pos.BOTTOM_CENTER);
        preDefBox.getChildren().addAll(labelPredef,preDef, savePreDefToFileBtn);

        HBox confirmBox = new HBox();
        confirmBox.setPadding(new Insets(10,10,10,10));
        confirmBox.setSpacing(10);
        confirmBox.setAlignment(Pos.CENTER);
        Button yesBtn = new Button("Save");
        Button noBtn = new Button("Cancel");
        yesBtn.setPrefSize(100,20);
        noBtn.setPrefSize(100,20);
        yesBtn.getStyleClass().add("confirm_button");
        noBtn.getStyleClass().add("cancel_button");
        confirmBox.getChildren().addAll(yesBtn,noBtn);

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(300);
        window.setMinHeight(250);

        Stage saveToFileWindow = new Stage();
        saveToFileWindow.initModality(Modality.APPLICATION_MODAL);
        saveToFileWindow.setMinWidth(300);
        saveToFileWindow.setMinHeight(250);
        Button save = new Button("SAVE");
        JFXTextField inputField = new JFXTextField();

        savePreDefToFileBtn.setOnMouseClicked(e -> {
            saveToFileWindow.show();
            save.setOnAction(click -> {
                Platform.runLater(() -> {
                    fileName = inputField.getText();
                    try {
                        preDefValues = preDef.getText();
                        s.writePreDefToFile(fileName+":"+preDefValues,s.preDefsFilePath);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                });
                saveToFileWindow.close();
            });
        });


        yesBtn.setOnAction(e -> {
            String[] newSettings = new String[]
            {
                "gridsize:"+gridSize.getText(),
                "squaresize:"+squareSize.getText(),
                "spawnchance:"+spawnChance.getText()
            };
            try{
                s.writeSettingsToFile(newSettings,s.settingsFilePath);
            } catch(IOException ex){
                ex.printStackTrace();
            }
            preDefValues = preDef.getText();
            window.close();
        });

        noBtn.setOnAction(e -> {
            window.close();
        });




        VBox saveToFileLayout = new VBox(10);
        saveToFileLayout.setAlignment(Pos.CENTER);
        saveToFileLayout.getChildren().addAll(inputField,save);
        saveToFileLayout.setStyle("-fx-background:#fff;");
        Scene saveFileScene = new Scene(saveToFileLayout);
        saveFileScene.getStylesheets().add("assets/style.css");
        saveToFileWindow.initStyle(StageStyle.UTILITY);
        saveToFileWindow.setTitle("Save Predefine as...");
        save.getStyleClass().add("confirm_button");
        saveToFileWindow.setScene(saveFileScene);




        VBox layout = new VBox(10);
        layout.setStyle("-fx-background:#fff;");
        layout.getChildren().addAll(gridSizeBox,squareSizeBox,spawnChanceBox,preDefBox,confirmBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("assets/style.css");
        window.initStyle(StageStyle.UTILITY);
        window.setScene(scene);
        window.showAndWait();

        return preDefValues;
    }
}
