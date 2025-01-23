package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.Artist;
import com.soundnest.soundnest.Classes.DatabaseConnection;
import com.soundnest.soundnest.Classes.Notification;
import com.soundnest.soundnest.Classes.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditEntryController {
    @FXML
    private TextField inputId;
    @FXML
    private TextField inputName;
    @FXML
    private TextField inputPassword;
    private String currentTable;

    public EditEntryController() {
    }

    public void setData(String id, String name, String password, String table) {
        this.inputId.setText(id);
        this.inputName.setText(name);
        this.inputPassword.setText(password);
        this.currentTable = table;
    }

    @FXML
    void onSaveClick(MouseEvent event) {
        String id = this.inputId.getText();
        String name = this.inputName.getText();
        String password = this.inputPassword.getText();
        String query = "";
        switch (this.currentTable) {
            case "users" -> query = "UPDATE users SET user_name = ?, password = ? WHERE user_id = ?";
            case "artists" -> query = "UPDATE artists SET artist_name = ?, artist_password = ? WHERE artist_id = ?";
            case "admins" -> query = "UPDATE admins SET admin_name = ?, admin_password = ? WHERE user_id = ?";
        }

        try (
                Connection connection = DatabaseConnection.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, id);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Entry updated successfully!");
                Stage stage = (Stage)this.inputId.getScene().getWindow();
                stage.close();
            } else {
                System.out.println("Update failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
