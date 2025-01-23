package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistMainPageController {

    @FXML
    private TilePane contentPane;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> searchResultsList;

    @FXML
    private PasswordField artistPasswordField;

    @FXML
    public void initialize() {
        loadImages();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });
    }

    @FXML
    private void onBackClick(MouseEvent event) {
        navigateToView("/com/example/projedemo/artist-mainpage-view.fxml", "Artist Main Page", event);
    }

    @FXML
    private void onHomeClick(MouseEvent event) {
        navigateToView("/com/example/projedemo/artist-mainpage-view.fxml", "Artist Main Page", event);
    }

    @FXML
    private void onLogOutClick(MouseEvent event) {
        navigateToView("com/soundnest/soundnest/artist-login-view.fxml", "Artist Login", event);
    }

    @FXML
    private void onProfileClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("com/soundnest/soundnest/artist-profile-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());

            ArtistProfileController controller = loader.getController();
            controller.setArtistData(1, "example", "password123");

            stage.setScene(scene);
            stage.setTitle("Edit Profile");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading artist-profile-view.fxml!");
        }
    }




    @FXML
    private void onSongsClick(MouseEvent event) {
        navigateToView("/com/example/projedemo/songs-view.fxml", "Artist Songs Page", event);
    }

    @FXML
    private void onAlbumClick(MouseEvent event) {
        navigateToView("/com/example/projedemo/artist-albums.fxml", "Artist Album Page", event);
    }

    @FXML
    public void onSearchButtonClick(ActionEvent event) {
        String query = searchField.getText().trim();
        performSearch(query);
    }

    private void performSearch(String query) {
        if (query == null || query.isEmpty()) {
            searchResultsList.getItems().clear();
            return;
        }

        String sql = "SELECT artist_name FROM artists WHERE LOWER(artist_name) LIKE ? "
                + "UNION "
                + "SELECT user_name FROM users WHERE LOWER(user_name) LIKE ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            String searchQuery = "%" + query.toLowerCase() + "%";
            preparedStatement.setString(1, searchQuery);
            preparedStatement.setString(2, searchQuery);

            ResultSet resultSet = preparedStatement.executeQuery();

            ObservableList<String> results = FXCollections.observableArrayList();

            while (resultSet.next()) {
                results.add(resultSet.getString(1));
            }

            searchResultsList.setItems(results);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error while searching.");
        }
    }


    private void navigateToView(String fxmlPath, String title, MouseEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading " + fxmlPath);
        }
    }

    private void loadImages() {
        contentPane.getChildren().clear();

        File folder = new File("src/main/resources/com/example/projedemo/Images/");
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".png"))) {
                    VBox artistBox = new VBox();
                    artistBox.setSpacing(5);
                    artistBox.setStyle("-fx-alignment: center;");
                    artistBox.getStyleClass().add("artist-box");

                    ImageView artistImage = new ImageView();
                    artistImage.setFitHeight(120);
                    artistImage.setFitWidth(120);

                    try {
                        Image image = new Image(file.toURI().toString());
                        artistImage.setImage(image);
                    } catch (Exception e) {
                        System.out.println("Error loading image for: " + file.getName());
                    }

                    Label artistName = new Label(file.getName().replace(".jpg", "").replace(".png", ""));
                    artistName.setStyle("-fx-text-fill: #444; -fx-font-size: 14px; -fx-font-weight: bold;");

                    artistBox.getChildren().addAll(artistImage, artistName);
                    contentPane.getChildren().add(artistBox);
                }
            }
        } else {
            System.out.println("No images found in directory!");
        }
    }
}

