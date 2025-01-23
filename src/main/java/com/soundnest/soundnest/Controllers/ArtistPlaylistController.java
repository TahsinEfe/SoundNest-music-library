package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import com.soundnest.soundnest.Classes.Genre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ArtistPlaylistController {

    @FXML
    private ListView<String> albumList;

    @FXML
    private TextField albumNameField, releaseDateField;

    @FXML
    private ComboBox<Genre> genreComboBox;

    private int artistId;

    public void setArtistId(int artistId) {
        this.artistId = artistId;
        System.out.println("Set Artist ID: " + artistId);
        loadAlbums();
    }



    @FXML
    public void initialize() {
        loadGenres();

        albumList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadAlbumDetails(newValue);
            }
        });
    }

    private void loadGenres() {
        ObservableList<Genre> genres = FXCollections.observableArrayList();

        String query = "SELECT genre_id, genre_name FROM genres";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                genres.add(new Genre(resultSet.getInt("genre_id"), resultSet.getString("genre_name")));
            }

            genreComboBox.setItems(genres);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAlbums() {
        albumList.getItems().clear();

        String query = "SELECT album_name FROM albums WHERE artist_id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, artistId);
            ResultSet resultSet = preparedStatement.executeQuery();
            ObservableList<String> albums = FXCollections.observableArrayList();

            while (resultSet.next()) {
                albums.add(resultSet.getString("album_name"));
            }

            albumList.setItems(albums);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCreateAlbum(MouseEvent event) {
        String albumName = albumNameField.getText();
        String releaseDate = releaseDateField.getText();
        Genre selectedGenre = genreComboBox.getSelectionModel().getSelectedItem();

        if (albumName == null || albumName.isEmpty() || releaseDate == null || releaseDate.isEmpty() || selectedGenre == null) {
            System.out.println("Please fill in all fields.");
            return;
        }

        if (!releaseDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            System.out.println("Invalid release date format. Use YYYY-MM-DD.");
            return;
        }

        if (artistId <= 0) {
            System.out.println("Invalid artist ID.");
            return;
        }

        try {
            LocalDate releaseDateLD = LocalDate.parse(releaseDate, DateTimeFormatter.ISO_DATE);
            String query = "INSERT INTO albums (album_name, artist_id, release_date, genre_id) VALUES (?, ?, ?, ?)";

            try (Connection connection = DatabaseConnection.connect();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, albumName);
                preparedStatement.setInt(2, artistId);
                preparedStatement.setDate(3, Date.valueOf(releaseDateLD));
                preparedStatement.setInt(4, selectedGenre.getGenreId());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Album created successfully!");
                    loadAlbums();
                } else {
                    System.out.println("Failed to create album.");
                }

                albumNameField.clear();
                releaseDateField.clear();
                genreComboBox.getSelectionModel().clearSelection();
            }
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format. Use YYYY-MM-DD.");
        } catch (SQLException e) {
            System.err.println("Error while creating album: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void loadAlbumDetails(String albumName) {
    }

    @FXML
    private void onEditAlbumDetails(MouseEvent event) {
        String selectedAlbum = albumList.getSelectionModel().getSelectedItem();
        if (selectedAlbum == null) {
            System.out.println("Please select an album to edit.");
            return;
        }

        System.out.println("Editing details for album: " + selectedAlbum);
    }

    @FXML
    private void onBackToMainClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projedemo/artist-mainpage-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Artist Main Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading artist-mainpage-view.fxml!");
        }
    }

    public void onDeleteAlbum(MouseEvent mouseEvent) {
        String selectedAlbum = albumList.getSelectionModel().getSelectedItem();

        if (selectedAlbum == null) {
            System.out.println("Please select an album to delete.");
            return;
        }

        String query = "DELETE FROM albums WHERE album_name = ? AND artist_id = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, selectedAlbum);
            preparedStatement.setInt(2, artistId);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Album deleted successfully!");
                loadAlbums();
            } else {
                System.out.println("Error: Album could not be deleted. Check if it exists.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while deleting album!");
        }
    }

}
