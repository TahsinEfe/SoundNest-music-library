package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditController {
    @FXML
    private TextField inputName;
    @FXML
    private TextField inputEmail;
    @FXML
    private TextField inputPassword;
    private String userId;

    public EditController() {
    }

    public void setData(String userId, String name, String email, String password) {
        this.userId = userId;
        this.inputName.setText(name);
        this.inputEmail.setText(email);
        this.inputPassword.setText(password);
    }

    @FXML
    private void onSaveButtonClick(MouseEvent event) {
        String name = this.inputName.getText();
        String email = this.inputEmail.getText();
        String password = this.inputPassword.getText();
        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            String query = "UPDATE users SET user_name = ?, email = ?, password = ? WHERE user_id = ?";

            try (
                    Connection connection = DatabaseConnection.connect();
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
            ) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, password);
                preparedStatement.setString(4, this.userId);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("User updated successfully!");
                } else {
                    System.out.println("User not found.");
                }

                this.closeWindow(event);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Please fill in all fields.");
        }
    }

    @FXML
    private void onCancelButtonClick(MouseEvent event) {
        this.closeWindow(event);
    }

    private void closeWindow(MouseEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}

