package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArtistProfileController {
    @FXML
    private TextField artistNameField;
    @FXML
    private PasswordField artistPasswordField;
    private int artistId;

    public ArtistProfileController() {
    }

    public void setArtistData(int artistId, String artistName, String artistPassword) {
        this.artistId = artistId;
        this.artistNameField.setText(artistName);
        this.artistPasswordField.setText(artistPassword);
    }

    @FXML
    private void onSaveButtonClick(MouseEvent event) {
        String artistName = this.artistNameField.getText();
        String artistPassword = this.artistPasswordField.getText();
        if (!artistName.isEmpty() && !artistPassword.isEmpty()) {
            try (Connection connection = DatabaseConnection.connect()) {
                String query = "UPDATE artists SET artist_name = ?, artist_password = ? WHERE artist_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, artistName);
                preparedStatement.setString(2, artistPassword);
                preparedStatement.setInt(3, this.artistId);
                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("Artist profile updated successfully!");
                } else {
                    System.out.println("Error updating artist profile!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Please fill in all fields.");
        }
    }

    @FXML
    private void onBackClick(MouseEvent event) {
        try {
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("com/soundnest/soundnest/artist-mainpage-view.fxml"));
            Scene scene = new Scene((Parent)loader.load());
            stage.setScene(scene);
            stage.setTitle("Artist Main Page");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading artist-mainpage-view.fxml!");
        }

    }
}
