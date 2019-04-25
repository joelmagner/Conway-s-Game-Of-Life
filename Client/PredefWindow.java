package Client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class PredefWindow {
    static ArrayList<String> preDefValues;

    public static ArrayList<String> display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Options");
        window.setMinWidth(300);
        window.setMinHeight(250);

        //load settings if there are any
        Settings s = new Settings();
        s.readSettingsFromFile(s.settingsFilePath);

        //Custom values
        HBox predefBox = new HBox();
        predefBox.setPadding(new Insets(10,10,10,10));
        predefBox.setSpacing(10);
        Label labelPredef = new Label("predef format X:Y x:y");
        labelPredef.setStyle("-fx-fill:#ecf0f1;");
        TextField preDef = new TextField();
        labelPredef.setPrefSize(100,20);
        preDef.setPrefSize(210,20);
        predefBox.getChildren().addAll(labelPredef,preDef);


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
            ArrayList<String> xs = new ArrayList<String>();
            ArrayList<String> ys = new ArrayList<String>();
            for(String item : preDef.getText().split(" ")){
                xs.add(item.split(":")[0]);
                ys.add(item.split(":")[1]);
                System.out.println("X: " + item.split(":")[0] +" Y: " + item.split(":")[1]);
            }


            window.close();
        });

        noBtn.setOnAction(e -> {

            window.close();
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-background:#34495e;");
        layout.getChildren().addAll(predefBox,confirmBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("style.css");
        window.setScene(scene);
        window.showAndWait();


        return preDefValues;
    }
}
