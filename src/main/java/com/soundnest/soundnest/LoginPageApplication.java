package com.soundnest.soundnest;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LoginPageApplication extends Application {
    public LoginPageApplication() {
    }

    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/com/soundnest/soundnest/user-login-view.fxml"));
            Scene scene = new Scene((Parent)loader.load());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Sound-Nest");
            String iconPath = "/image.png";
            if (this.getClass().getResource(iconPath) != null) {
                primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream(iconPath)));
            } else {
                System.err.println("Icon file not found at: " + iconPath);
            }

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}