package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddController {

    @FXML
    private ComboBox<String> roleDropdown;

    @FXML
    private TextField inputName, inputEmail, inputPassword;
    @FXML
    private TextField inputUserId, inputAdminName, inputAdminPassword;

    @FXML
    private Label labelName, labelEmail, labelPassword;
    @FXML
    private Label labelUserId, labelAdminName, labelAdminPassword;

    @FXML
    void onRoleChanged() {
        String selectedRole = roleDropdown.getValue();

        labelName.setVisible(false);
        inputName.setVisible(false);
        labelEmail.setVisible(false);
        inputEmail.setVisible(false);
        labelPassword.setVisible(false);
        inputPassword.setVisible(false);

        labelUserId.setVisible(false);
        inputUserId.setVisible(false);
        labelAdminName.setVisible(false);
        inputAdminName.setVisible(false);
        labelAdminPassword.setVisible(false);

        switch (selectedRole) {
            case "User":
                labelName.setVisible(true);
                inputName.setVisible(true);
                labelEmail.setVisible(true);
                inputEmail.setVisible(true);
                labelPassword.setVisible(true);
                inputPassword.setVisible(true);
                break;

            case "Artist":
                labelName.setVisible(true);
                inputName.setVisible(true);
                labelPassword.setVisible(true);
                inputPassword.setVisible(true);
                break;

            case "Admin":
                labelUserId.setVisible(true);
                inputUserId.setVisible(true);
                labelAdminName.setVisible(true);
                inputAdminName.setVisible(true);
                labelAdminPassword.setVisible(true);
                inputAdminPassword.setVisible(true);
                break;
        }
    }

    @FXML
    private void onBackClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projedemo/admin-dashboard-view.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Admin Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading admin-dashboard-view.fxml!");
        }
    }

    @FXML
    void onAddButtonClick(MouseEvent event) {
        String selectedRole = roleDropdown.getValue();
        if (selectedRole == null) {
            System.out.println("Please select a role.");
            return;
        }

        switch (selectedRole) {
            case "User":
                saveUser(inputName.getText(), inputEmail.getText(), inputPassword.getText());
                break;
            case "Artist":
                saveArtist(inputName.getText(), inputPassword.getText());
                break;
            case "Admin":
                saveAdmin(inputUserId.getText(), inputAdminName.getText(), inputAdminPassword.getText());
                break;
        }
    }

    private void saveUser(String name, String email, String password) {
        String query = "INSERT INTO users (user_name, email, password) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
            System.out.println("User saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveArtist(String name, String password) {
        String query = "INSERT INTO artists (artist_name, artist_password) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            System.out.println("Artist saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveAdmin(String userId, String adminName, String adminPassword) {
        String query = "INSERT INTO admin (user_id, admin_name, admin_password) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, adminName);
            preparedStatement.setString(3, adminPassword);
            preparedStatement.executeUpdate();
            System.out.println("Admin saved successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
