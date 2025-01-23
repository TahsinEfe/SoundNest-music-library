package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import com.soundnest.soundnest.Classes.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SongsViewController {

    @FXML
    private Button backButton, addButton, saveButton, deleteButton;
    @FXML
    private TableView<Song> songsTable;
    @FXML
    private TableColumn<Song, String> titleColumn, durationColumn, songNameColumn, songLinkColumn;
    @FXML
    private TextField titleField, durationField, songNameField, songLinkField;
    @FXML
    private VBox addSongForm;

    private ObservableList<Song> songList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        songNameColumn.setCellValueFactory(cellData -> cellData.getValue().songNameProperty());
        songLinkColumn.setCellValueFactory(cellData -> cellData.getValue().songLinkProperty());

        loadSongs();
    }

    private void loadSongs() {
        songList.clear();
        String query = "SELECT title, duration, song_name, song_link FROM tracks";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                songList.add(new Song(
                        resultSet.getString("title"),
                        resultSet.getString("duration"),
                        resultSet.getString("song_name"),
                        resultSet.getString("song_link")
                ));
            }
            songsTable.setItems(songList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddClick(MouseEvent event) {
        addSongForm.setVisible(true);
    }

    @FXML
    private void onSaveClick(MouseEvent event) {
        String title = titleField.getText();
        String duration = durationField.getText();
        String songName = songNameField.getText();
        String songLink = songLinkField.getText();


        if (title.isEmpty() || duration.isEmpty() || songName.isEmpty()|| songLink.isEmpty()) {
            System.out.println("Please fill in all fields.");
            return;
        }

        String query = "INSERT INTO tracks (title, duration, song_name, song_link) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, duration);
            preparedStatement.setString(3, songName);
            preparedStatement.setString(4, songLink);
            preparedStatement.executeUpdate();

            System.out.println("Song added successfully!");
            songList.add(new Song(title, duration, songName, songLink));
            addSongForm.setVisible(false);
            titleField.clear();
            durationField.clear();
            songNameField.clear();
            songLinkField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDeleteClick(MouseEvent event) {
        Song selectedSong = songsTable.getSelectionModel().getSelectedItem();

        if (selectedSong == null) {
            System.out.println("Please select a song to delete.");
            return;
        }

        String query = "DELETE FROM tracks WHERE title = ? AND duration = ? AND song_name = ? AND song_link = ?";
        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, selectedSong.getTitle());
            preparedStatement.setString(2, selectedSong.getDuration());
            preparedStatement.setString(3, selectedSong.getSongName());
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Song deleted successfully!");
                songList.remove(selectedSong);
            } else {
                System.out.println("Failed to delete the song.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onBackClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projedemo/user-mainpage-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("User Main Page");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error loading user-mainpage-view.fxml!");
        }
    }
}

