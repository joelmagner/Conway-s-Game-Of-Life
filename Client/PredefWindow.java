package Client;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.ArrayList;

public class PredefWindow {
    static String preDefValues;

    public static String display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Select Predefine");
        window.setMinWidth(300);
        window.setMinHeight(50);


        HBox preDefBox = new HBox();


        //load settings if there are any
        Settings s = new Settings();
        ArrayList<String> preDefs = s.readPreDefFromFile(s.preDefsFilePath);




        VBox layout = new VBox();
        layout.setStyle("-fx-background:#34495e;");
        layout.getChildren().addAll(preDefBox);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("assets/style.css");


        for(String entry : preDefs){
            String fileName = entry.split(":")[0];
            String values = entry.split(":")[1];
            HBox placeholder = new HBox();

            Button preDefButton = new Button(fileName);
            Button deleteButton = new Button("X");

            placeholder.getChildren().addAll(preDefButton,deleteButton);
            preDefButton.getStyleClass().add("options_button");
            deleteButton.getStyleClass().add("cancel_button");
            preDefButton.setPrefWidth(250);
            layout.getChildren().add(placeholder);
            preDefButton.setOnAction(e -> {
               preDefValues = values;
               window.close();
            });
            deleteButton.setOnAction(e -> {
                try {
                    s.deletePreDefFromFile(fileName,s.preDefsFilePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Platform.runLater(() -> {
                    layout.getChildren().remove(placeholder);
                    window.setHeight((36*(layout.getChildren().size()))+8);
                });
            });

        }
        window.setHeight((36*(layout.getChildren().size()))+8);
        layout.setAlignment(Pos.TOP_CENTER);
        window.initStyle(StageStyle.UTILITY);
        window.setScene(scene);
        window.showAndWait();

        return preDefValues;
    }
}
