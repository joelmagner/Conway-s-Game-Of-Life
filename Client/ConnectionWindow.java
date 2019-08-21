package Client;

import com.jfoenix.controls.JFXTextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.scene.*;
import java.io.IOException;

/**
 * @author Joel Magnér
 */

public class ConnectionWindow{

    static boolean answer;

    /**
     *
     * @return answer - user answer to save or discard changes
     * <p>
     *     User can change their connection settings here.
     * </p>
     */

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
        JFXTextField ipAddress = new JFXTextField();
        labelIpAddress.getStyleClass().add("label_format");
        ipAddressBox.getStyleClass().add("hover_row");
        ipAddress.setText(s.ipAddress);
        labelIpAddress.setPrefSize(100,20);
        ipAddress.setPrefSize(210,20);
        ipAddressBox.getChildren().addAll(labelIpAddress,ipAddress);


        //port
        HBox portBox = new HBox();
        portBox.setPadding(new Insets(10,10,10,10));
        portBox.setSpacing(10);
        Label labelPort = new Label("Port");
        JFXTextField port = new JFXTextField();
        port.setText(Integer.toString(s.port));
        labelPort.getStyleClass().add("label_format");
        portBox.getStyleClass().add("hover_row");
        labelPort.setPrefSize(100,20);
        port.setPrefSize(210,20);
        portBox.getChildren().addAll(labelPort,port);

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
        layout.setStyle("-fx-background:#ffffff;");
        layout.getChildren().addAll(ipAddressBox,portBox,confirmBox);
        layout.setAlignment(Pos.CENTER);

        window.initStyle(StageStyle.UTILITY);
        Scene scene = new Scene(layout);
        scene.getStylesheets().add("assets/style.css");
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
}
