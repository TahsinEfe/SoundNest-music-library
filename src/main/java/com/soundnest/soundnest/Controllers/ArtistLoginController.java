package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistLoginController {

    @FXML
    private TextField artistNameField;

    @FXML
    private PasswordField artistPasswordField;

    @FXML
    private Hyperlink userLoginLink;

    @FXML
    private Hyperlink adminLoginLink;

    @FXML
    void onUserLoginClick(MouseEvent event) {
        try {
            Stage stage = (Stage) userLoginLink.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projedemo/user-login-view.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAdminLoginClick(MouseEvent event) {
        try {
            Stage stage = (Stage) adminLoginLink.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projedemo/admin-login-view.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onLoginClick(MouseEvent event) {
        String artistName = artistNameField.getText();
        String artistPassword = artistPasswordField.getText();

        if (artistName.isEmpty() || artistPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        System.out.println("Login button clicked! Validating credentials...");

        if (authenticateAdmin(artistName, artistPassword)) {
            System.out.println("Login successful! Redirecting to AdminDashboard...");
            try {
                Stage stage = (Stage) artistNameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projedemo/artist-mainpage-view.fxml"));
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading artist-mainpage-view.fxml!");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("The artist name or password is incorrect. Please try again.");
            alert.showAndWait();
            System.out.println("Invalid artist credentials! Please try again.");
        }
    }


    private boolean authenticateAdmin(String artistName, String artistPassword) {
        String query = "SELECT * FROM artists WHERE artist_name = ? AND artist_password = ?";
        Connection connection = null;
        try {
            connection = DatabaseConnection.connect();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, artistName);
                preparedStatement.setString(2, artistPassword);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

