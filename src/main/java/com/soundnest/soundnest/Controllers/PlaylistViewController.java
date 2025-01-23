package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaylistViewController {
    @FXML
    private ListView<String> playlistList;
    @FXML
    private ListView<String> songList;
    @FXML
    private TextField playlistNameField;
    @FXML
    private TextField songNameField;
    @FXML
    private TextField songLinkField;
    private int currentSongIndex = -1;
    @FXML
    private Button playButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button previousButton;
    @FXML
    private WebView webView;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<String> searchResultsList;

    public PlaylistViewController() {
    }

    @FXML
    public void initialize() {
        this.loadPlaylists();
        this.playlistList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.loadSongsForPlaylist(newValue);
            }

        });
        if (this.searchField != null) {
            this.searchField.textProperty().addListener((observable, oldValue, newValue) -> this.performSearch(newValue));
        } else {
            System.out.println("Warning: searchField is not properly initialized in FXML.");
        }

    }

    private void performSearch(String query) {
        if (query != null && !query.isEmpty()) {
            String searchQuery = "%" + query.toLowerCase() + "%";
            String sql = "SELECT artist_name FROM artists WHERE LOWER(artist_name) LIKE ? UNION SELECT user_name FROM users WHERE LOWER(user_name) LIKE ?";

            try (
                    Connection connection = DatabaseConnection.connect();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ) {
                preparedStatement.setString(1, searchQuery);
                preparedStatement.setString(2, searchQuery);
                ResultSet resultSet = preparedStatement.executeQuery();
                ObservableList<String> results = FXCollections.observableArrayList();

                while(resultSet.next()) {
                    results.add(resultSet.getString(1));
                }

                this.searchResultsList.setItems(results);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            this.searchResultsList.getItems().clear();
        }
    }

    @FXML
    private void onEditPlaylist(MouseEvent event) {
        String selectedPlaylist = (String)this.playlistList.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            this.playlistNameField.setText(selectedPlaylist);
        } else {
            System.out.println("Please select a playlist to edit.");
        }

    }

    @FXML
    private void onDeletePlaylist(MouseEvent event) {
        String selectedPlaylist = (String)this.playlistList.getSelectionModel().getSelectedItem();
        if (selectedPlaylist == null) {
            System.out.println("Please select a playlist to delete.");
        } else {
            String query = "DELETE FROM playlists WHERE playlist_name = ?";

            try (
                    Connection connection = DatabaseConnection.connect();
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
            ) {
                preparedStatement.setString(1, selectedPlaylist);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Playlist deleted successfully!");
                    this.playlistList.getItems().remove(selectedPlaylist);
                } else {
                    System.out.println("Failed to delete playlist.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error deleting playlist!");
            }

        }
    }

    private void loadPlaylists() {
        this.playlistList.getItems().clear();
        String query = "SELECT playlist_name FROM playlists";

        try (
                Connection connection = DatabaseConnection.connect();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
        ) {
            while(resultSet.next()) {
                this.playlistList.getItems().add(resultSet.getString("playlist_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void loadSongsForPlaylist(String playlistName) {
        this.songList.getItems().clear();
        String query = "SELECT song_name, song_link FROM playlists WHERE playlist_name = ?";

        try (
                Connection connection = DatabaseConnection.connect();
                PreparedStatement statement = connection.prepareStatement(query);
        ) {
            statement.setString(1, playlistName);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                String songName = resultSet.getString("song_name");
                String songLink = resultSet.getString("song_link");
                this.songList.getItems().add(songName + " (" + songLink + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void onCreatePlaylist(MouseEvent event) {
        String playlistName = this.playlistNameField.getText();
        if (playlistName.isEmpty()) {
            System.out.println("Please enter a playlist name.");
        } else {
            String query = "INSERT INTO playlists (playlist_name) VALUES (?)";

            try (
                    Connection connection = DatabaseConnection.connect();
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
            ) {
                preparedStatement.setString(1, playlistName);
                preparedStatement.executeUpdate();
                System.out.println("Playlist created successfully!");
                this.loadPlaylists();
                this.playlistNameField.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    private void onAddSong(MouseEvent event) {
        String selectedPlaylist = (String)this.playlistList.getSelectionModel().getSelectedItem();
        String songName = this.songNameField.getText();
        String songLink = this.songLinkField.getText();
        if (selectedPlaylist != null && !songName.isEmpty() && !songLink.isEmpty()) {
            String query = "INSERT INTO playlists (playlist_name, song_name, song_link) VALUES (?, ?, ?)";

            try (
                    Connection connection = DatabaseConnection.connect();
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
            ) {
                preparedStatement.setString(1, selectedPlaylist);
                preparedStatement.setString(2, songName);
                preparedStatement.setString(3, songLink);
                preparedStatement.executeUpdate();
                System.out.println("Song added to playlist successfully!");
                this.loadSongsForPlaylist(selectedPlaylist);
                this.songNameField.clear();
                this.songLinkField.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Please select a playlist and fill all fields.");
        }
    }

    @FXML
    private void onEditSongClick(MouseEvent event) {
        String selectedPlaylist = (String)this.playlistList.getSelectionModel().getSelectedItem();
        String selectedSong = (String)this.songList.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null && selectedSong != null) {
            String[] songParts = selectedSong.split(" \\(");
            String songName = songParts[0];
            String songLink = songParts[1].substring(0, songParts[1].length() - 1);

            try {
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("com/soundnest/soundnest/edit-song-dialog.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene((Parent)loader.load()));
                stage.setTitle("Edit Song");
                EditSongDialogController controller = (EditSongDialogController)loader.getController();
                controller.initializeData(selectedPlaylist, songName, songLink, this);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error opening edit dialog!");
            }
        } else {
            System.out.println("Please select a playlist and a song to edit.");
        }

    }

    public void onDeleteSongClick(MouseEvent mouseEvent) {
        String selectedSong = (String)this.songList.getSelectionModel().getSelectedItem();
        if (selectedSong == null) {
            System.out.println("Please select a song to delete.");
        } else {
            String[] songParts = selectedSong.split(" \\(");
            String songName = songParts[0];
            String songLink = songParts[1].substring(0, songParts[1].length() - 1);
            String query = "DELETE FROM playlists WHERE playlist_name = ? AND song_name = ? AND song_link = ?";

            try (
                    Connection connection = DatabaseConnection.connect();
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
            ) {
                preparedStatement.setString(1, (String)this.playlistList.getSelectionModel().getSelectedItem());
                preparedStatement.setString(2, songName);
                preparedStatement.setString(3, songLink);
                preparedStatement.executeUpdate();
                System.out.println("Song deleted successfully!");
                this.loadSongsForPlaylist((String)this.playlistList.getSelectionModel().getSelectedItem());
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    @FXML
    private void onBackToMainClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("com/soundnest/soundnest/user-mainpage-view.fxml"));
            Parent root = (Parent)loader.load();
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("User Main Page");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading user-mainpage-view.fxml!");
        }

    }

    @FXML
    private void onPlayButtonClick(MouseEvent event) {
        String selectedSong = (String)this.songList.getSelectionModel().getSelectedItem();
        if (selectedSong == null) {
            System.out.println("Please select a song to play.");
        } else {
            try {
                String[] songParts = selectedSong.split(" \\(");
                String songLink = songParts[1].substring(0, songParts[1].length() - 1);
                if (!songLink.startsWith("https://www.youtube.com/") && !songLink.startsWith("https://youtu.be/")) {
                    System.out.println("Invalid YouTube link: " + songLink);
                    return;
                }

                this.webView.getEngine().load(songLink);
                System.out.println("Playing: " + songLink);
            } catch (Exception e) {
                System.out.println("Error playing the song.");
                e.printStackTrace();
            }

        }
    }

    @FXML
    private void onLoginWithGoogleClick() {
        this.webView.getEngine().load("https://accounts.google.com/");
    }
}

