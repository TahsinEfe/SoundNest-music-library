package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditSongDialogController {

    @FXML
    private TextField songNameField;

    @FXML
    private TextField songLinkField;

    @FXML
    private ComboBox<String> playlistComboBox;

    private String originalSongName;
    private String originalSongLink;
    private PlaylistViewController playlistViewController;

    public void initializeData(String originalSongName, String originalSongLink, String songLink, PlaylistViewController controller) {
        this.originalSongName = originalSongName;
        this.originalSongLink = originalSongLink;
        this.playlistViewController = controller;

        songNameField.setText(originalSongName);
        songLinkField.setText(originalSongLink);

        loadPlaylists();
    }

    @FXML
    private void onComboBoxClicked(MouseEvent mouseEvent) {
        String selectedPlaylist = playlistComboBox.getValue();
        if (selectedPlaylist != null) {
            System.out.println("Selected Playlist: " + selectedPlaylist);
        }
    }

    @FXML
    private void onSaveButtonClick() {
        String updatedSongName = songNameField.getText();
        String updatedSongLink = songLinkField.getText();
        String selectedPlaylist = playlistComboBox.getValue();

        if (updatedSongName.isEmpty() || updatedSongLink.isEmpty() || selectedPlaylist == null || selectedPlaylist.isEmpty()) {
            System.out.println("Please fill in all fields and select a playlist.");
            return;
        }

        String query = "UPDATE playlists SET song_name = ?, song_link = ? WHERE playlist_name = ? AND song_name = ? AND song_link = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, updatedSongName);
            preparedStatement.setString(2, updatedSongLink);
            preparedStatement.setString(3, selectedPlaylist);
            preparedStatement.setString(4, originalSongName);
            preparedStatement.setString(5, originalSongLink);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Song updated successfully!");
                playlistViewController.loadSongsForPlaylist(selectedPlaylist); // Şarkıları güncelle
            } else {
                System.out.println("No rows were updated.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        ((javafx.scene.Node) songNameField).getScene().getWindow().hide();
    }


    @FXML
    private void onCancelButtonClick() {
        ((javafx.scene.Node) songNameField).getScene().getWindow().hide();
    }

    private void loadPlaylists() {
        ObservableList<String> playlists = FXCollections.observableArrayList();

        String query = "SELECT playlist_name FROM playlists";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                playlists.add(resultSet.getString("playlist_name"));
            }

            playlistComboBox.setItems(playlists);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

