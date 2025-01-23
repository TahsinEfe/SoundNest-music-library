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

public class AdminLoginController {

    @FXML
    private TextField adminNameField;

    @FXML
    private PasswordField adminPasswordField;

    @FXML
    private Hyperlink userLoginLink;

    @FXML
    private Hyperlink artistLoginLink;

    @FXML
    void onLoginClick(MouseEvent event) {
        String adminName = adminNameField.getText();
        String adminPassword = adminPasswordField.getText();

        if (adminName.isEmpty() || adminPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        System.out.println("Login button clicked! Validating credentials...");

        if (authenticateAdmin(adminName, adminPassword)) {
            System.out.println("Login successful! Redirecting to AdminDashboard...");
            try {
                Stage stage = (Stage) adminNameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projedemo/admin-dashboard-view.fxml"));
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading admin-dashboard-view.fxml!");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("The admin name or password is incorrect. Please try again.");
            alert.showAndWait();
            System.out.println("Invalid admin credentials! Please try again.");
        }
    }


    private boolean authenticateAdmin(String adminName, String adminPassword) {
        String query = "SELECT * FROM admin WHERE admin_name = ? AND admin_password = ?";
        Connection connection = null;
        try {
            connection = DatabaseConnection.connect();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, adminName);
                preparedStatement.setString(2, adminPassword);

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
    void onArtistLoginClick(MouseEvent event) {
        try {
            Stage stage = (Stage) artistLoginLink.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projedemo/artist-login-view.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
