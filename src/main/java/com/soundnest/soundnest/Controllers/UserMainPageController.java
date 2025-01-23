package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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

public class UserMainPageController {

    @FXML
    private TilePane contentPane;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> searchResultsList;

    @FXML
    public void initialize() {
        loadImages();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });
    }

    @FXML
    private void onBackClick(MouseEvent event) {
        navigateToView("/com/example/projedemo/user-mainpage-view.fxml", "User Main Page", event);
    }

    @FXML
    private void onHomeClick(MouseEvent event) {
        navigateToView("/com/example/projedemo/user-mainpage-view.fxml", "User Main Page", event);
    }

    @FXML
    private void onLogOutClick(MouseEvent event) {
        navigateToView("/com/example/projedemo/user-login-view.fxml", "User Login", event);
    }

    @FXML
    private void onProfileClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projedemo/user-profile-view.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(loader.load());

            UserProfileController controller = loader.getController();
            controller.setUserData(1, "example", "example@gmail.com", "password123");

            stage.setScene(scene);
            stage.setTitle("Edit Profile");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading user-profile-view.fxml!");
        }
    }

    @FXML
    private void onSongsClick(MouseEvent event) {
        navigateToView("/com/soundnest/soundnest/songs-view.fxml", "User Songs Page", event);
    }

    @FXML
    private void onPlaylistClick(MouseEvent event) {
        navigateToView("/com/soundnest/soundnest/user-playlist.fxml", "User Playlist Page", event);
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

        File folder = new File("src/main/resources/Images/");
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

    @FXML
    private void onFollowButtonClick(ActionEvent event) {
        String selectedName = searchResultsList.getSelectionModel().getSelectedItem();

        if (selectedName == null) {
            System.out.println("No item selected to follow.");
            return;
        }

        try (Connection connection = DatabaseConnection.connect()) {
            int currentUserId = 1;

            int followedId = findFollowedId(connection, selectedName);

            if (followedId != -1) {
                String followedType = findFollowedType(connection, followedId);

                if(followedType != null) {


                    String insertFollowingQuery = "INSERT INTO following (follower_id, followed_id, followed_type, followed_artist_name, followed_user_name) VALUES (?, ?, ?, ?, ?)";

                    try (PreparedStatement ps = connection.prepareStatement(insertFollowingQuery)) {
                        ps.setInt(1, currentUserId);
                        ps.setInt(2, followedId);
                        ps.setString(3, followedType);

                        String followedName = findFollowedName(connection,followedId, followedType);

                        if (followedName != null) {
                            ps.setString(4, followedName);
                            ps.setString(5, followedName);
                        } else {
                            System.err.println("Couldn't retrieve followed name!"); //Log
                            return;
                        }


                        int rowsInserted = ps.executeUpdate();

                        if (rowsInserted > 0) {
                            System.out.println("Followed successfully!");
                        } else {
                            System.out.println("Follow operation failed.");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.err.println("Error during insert: " + e.getMessage());
                    }
                } else {
                    System.err.println("Couldn't determine followed type for ID: " + followedId);
                }
            } else {
                System.out.println("Selected item not found in users or artists.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error during database connection: " + e.getMessage());
        }
    }

    private int findFollowedId(Connection connection, String name) throws SQLException {
        String sql = "SELECT CASE WHEN EXISTS(SELECT 1 FROM users WHERE user_name = ? ) THEN (SELECT user_id FROM users WHERE user_name = ?) ELSE (SELECT artist_id FROM artists WHERE artist_name = ?) END";


        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, name);
            statement.setString(3, name);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return -1;
            }

        }
    }

    private String findFollowedType(Connection connection, int followedId) throws SQLException{
        String sql = "SELECT CASE WHEN EXISTS(SELECT 1 FROM users WHERE user_id = ?) THEN 'user' ELSE 'artist' END";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, followedId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString(1);
            }
        }
        return null;
    }



    private String findFollowedName(Connection connection, int followedId, String followedType) throws SQLException{
        if (followedType.equals("artist")) {
            String sql = "SELECT artist_name FROM artists WHERE artist_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, followedId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString("artist_name");
                }
            }
        } else if (followedType.equals("user")) {
            String sql = "SELECT user_name FROM users WHERE user_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, followedId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString("user_name");
                }
            }
        }
        return null;
    }

    @FXML
    public void onFollowers(MouseEvent event) {
        navigateToView("/com/soundnest/soundnest/followers.fxml", "Followers");
    }

    private void navigateToView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) searchField.getScene().getWindow();

            stage.getScene().setRoot(root);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML file: " + fxmlPath);
        }
    }

}

