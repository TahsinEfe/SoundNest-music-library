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

public class UserLoginController {

    @FXML
    private Hyperlink adminLoginLink;

    @FXML
    private Hyperlink artistLoginLink;

    @FXML
    private Hyperlink userLoginLink;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField userEmailField;

    @FXML
    private PasswordField userPasswordField;

    @FXML
    void onAdminLoginClick(MouseEvent event) {
        try {
            Stage stage = (Stage) adminLoginLink.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/soundnest/soundnest/admin-login-view.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/soundnest/soundnest/artist-login-view.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRegisterClick(MouseEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/soundnest/soundnest/user-register-view.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onLoginClick(MouseEvent event) {
        String userName = usernameField.getText();
        String userMail = userEmailField.getText();
        String userPassword = userPasswordField.getText();

        if (userName.isEmpty() || userMail.isEmpty() || userPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Error");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }

        System.out.println("Login button clicked! Validating credentials...");

        if (authenticateAdmin(userName, userMail, userPassword)) {
            System.out.println("Login successful! Redirecting to UserMainPage...");
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("com/soundnest/soundnest/user-mainpage-view.fxml"));
                Scene scene = new Scene(loader.load());
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading user-mainpage-view.fxml!");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Invalid Credentials");
            alert.setContentText("The username, email, or password is incorrect. Please try again.");
            alert.showAndWait();
        }
    }


    private boolean authenticateAdmin(String userName, String userMail, String userPassword) {
        String query = "SELECT * FROM users WHERE user_name = ? AND email = ? AND password = ?";
        Connection connection = null;
        try {
            connection = DatabaseConnection.connect();
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, userMail);
                preparedStatement.setString(3, userPassword);

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
