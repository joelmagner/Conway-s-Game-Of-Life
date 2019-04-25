package Client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.scene.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class OptionsWindow{

    static boolean answer;
    static int value;
    public static int prompt(){

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Enter number");
        window.setMinWidth(150);
        window.setMinHeight(150);
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10,10,10,10));
        hbox.setSpacing(10);
        TextField input = new TextField();
        Button acceptBtn = new Button("Accept");
        acceptBtn.getStyleClass().add("button-success");
        VBox layout = new VBox(10);
        layout.setStyle("-fx-background:#34495e;");
        hbox.getChildren().addAll(input,acceptBtn);
        layout.getChildren().addAll(hbox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("style.css");
        window.setScene(scene);

        acceptBtn.setOnAction(e -> {
            value = Integer.parseInt(input.getText());
            window.close();
        });
        window.showAndWait();


        return value;
    }

    public static boolean display() throws FileNotFoundException {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Options");
        window.setMinWidth(300);
        window.setMinHeight(250);

        //load settings if there are any
        Settings s = new Settings();
        s.readSettingsFromFile(s.settingsFilePath);

        //GridsizeBox
        HBox gridSizeBox = new HBox();
        gridSizeBox.setPadding(new Insets(10,10,10,10));
        gridSizeBox.setSpacing(10);
        Label labelGridSize = new Label("Grid Size");
        labelGridSize.setStyle("-fx-fill:#ecf0f1;");
        TextField gridSize = new TextField();
        gridSize.setText(Integer.toString(s.gridSize));
        labelGridSize.setPrefSize(100,20);
        gridSize.setPrefSize(210,20);
        gridSizeBox.getChildren().addAll(labelGridSize,gridSize);

        //SquareSizeBox
        HBox squareSizeBox = new HBox();
        squareSizeBox.setPadding(new Insets(10,10,10,10));
        squareSizeBox.setSpacing(10);
        Label labelSquareSize = new Label("Square Size");
        TextField squareSize = new TextField();
        squareSize.setText(Integer.toString(s.squareSize));
        labelSquareSize.setPrefSize(100,20);
        squareSize.setPrefSize(210,20);
        squareSizeBox.getChildren().addAll(labelSquareSize,squareSize);

        //SpawnChanceBox
        HBox spawnChanceBox = new HBox();
        spawnChanceBox.setPadding(new Insets(10,10,10,10));
        spawnChanceBox.setSpacing(10);
        Label labelSpawnChance = new Label("Spawn %");
        TextField spawnChance = new TextField();
        spawnChance.setText(Integer.toString(s.spawnChance));
        labelSpawnChance.setPrefSize(100,20);
        spawnChance.setPrefSize(210,20);
        spawnChanceBox.getChildren().addAll(labelSpawnChance,spawnChance);

        HBox confirmBox = new HBox();
        confirmBox.setPadding(new Insets(10,10,10,10));
        confirmBox.setSpacing(10);
        Label confirmLabel = new Label("Save new settings?");
        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("No");
        yesBtn.setPrefSize(100,20);
        noBtn.setPrefSize(100,20);
        yesBtn.getStyleClass().add("button-success");
        noBtn.getStyleClass().add("button-danger");
        confirmBox.getChildren().addAll(confirmLabel,yesBtn,noBtn);

        yesBtn.setOnAction(e -> {
            answer = true;
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
            window.close();
        });

        noBtn.setOnAction(e -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-background:#34495e;");
        layout.getChildren().addAll(gridSizeBox,squareSizeBox,spawnChanceBox,confirmBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("style.css");
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
}
