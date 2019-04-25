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

import java.io.IOException;

public class ConnectionWindow{

    static boolean answer;

    public static boolean display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Connection Settings");
        window.setMinWidth(300);
        window.setMinHeight(250);

        //load settings if there are any
        Settings s = new Settings();
        s.readSettingsFromFile(s.connectionFilePath);


        //ipaddress
        HBox ipAddressBox = new HBox();
        ipAddressBox.setPadding(new Insets(10,10,10,10));
        ipAddressBox.setSpacing(10);
        Label labelIpAddress = new Label("IP Address");
        TextField ipAddress = new TextField();
        ipAddress.setText(s.ipAddress);
        labelIpAddress.setPrefSize(100,20);
        ipAddress.setPrefSize(210,20);
        ipAddressBox.getChildren().addAll(labelIpAddress,ipAddress);

        //port
        HBox portBox = new HBox();
        portBox.setPadding(new Insets(10,10,10,10));
        portBox.setSpacing(10);
        Label labelPort = new Label("Port");
        TextField port = new TextField();
        port.setText(Integer.toString(s.port));
        labelPort.setPrefSize(100,20);
        port.setPrefSize(210,20);
        portBox.getChildren().addAll(labelPort,port);

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
                            "ipaddress:"+ipAddress.getText(),
                            "port:"+port.getText()
                    };
            try{
                s.writeSettingsToFile(newSettings,s.connectionFilePath);
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
        layout.getChildren().addAll(ipAddressBox,portBox,confirmBox);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("style.css");
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
}
