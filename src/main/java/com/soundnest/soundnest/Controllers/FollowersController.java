package com.soundnest.soundnest.Controllers;

import com.soundnest.soundnest.Classes.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FollowersController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> searchResultsList;

    @FXML
    private ListView<String> followingList;

    private int currentUserId = 1;

    @FXML
    public void initialize() {
        currentUserId = 1;
        loadFollowing();
    }

    @FXML
    private void onSearchButtonClick(ActionEvent event) {
        String query = searchField.getText().trim();
        performSearch(query);
    }

    private void loadSearch() {

        searchResultsList.getItems().clear();

        if (searchField.getText().isEmpty()) {
            return;
        }

        String searchQuery = "SELECT user_name FROM users WHERE lower(user_name) LIKE ? UNION SELECT artist_name FROM artists WHERE lower(artist_name) LIKE ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(searchQuery)) {

            ps.setString(1, "%" + searchField.getText().toLowerCase() + "%");
            ps.setString(2, "%" + searchField.getText().toLowerCase() + "%");

            ResultSet rs = ps.executeQuery();
            ObservableList<String> results = FXCollections.observableArrayList();
            while (rs.next()) {
                results.add(rs.getString("user_name"));
            }
            searchResultsList.setItems(results);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load search results.");
            e.printStackTrace();
        }

    }

    @FXML
    private void onFollowButtonClick(ActionEvent event) {
        String selectedItem = searchResultsList.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showAlert("No Item Selected", "Please select a user or artist to follow.");
            return;
        }

        try (Connection connection = DatabaseConnection.connect()) {
            int followedId = findFollowedId(connection, selectedItem);
            if (followedId != -1) {
                String followedType = findFollowedType(connection, followedId);
                if (followedType != null) {
                    String insertQuery = "INSERT INTO following (follower_id, followed_id, followed_type, followed_user_name) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = connection.prepareStatement(insertQuery)) {
                        ps.setInt(1, currentUserId);
                        ps.setInt(2, followedId);
                        ps.setString(3, followedType);
                        ps.setString(4, selectedItem);
                        int rowsInserted = ps.executeUpdate();
                        if (rowsInserted > 0) {
                            loadFollowing();
                            loadSearch();
                            showAlert("Success", "Successfully followed!");
                        } else {
                            showAlert("Error", "Failed to follow.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error during follow operation.");
        }
    }

    @FXML
    private void onDeleteButtonClick(ActionEvent event) {
        String selectedItem = followingList.getSelectionModel().getSelectedItem();

        if (selectedItem == null) {
            showAlert("No Selection", "Please select an item to delete.");
            return;
        }

        try (Connection connection = DatabaseConnection.connect()) {

            String deleteQuery = """
                DELETE FROM following
                WHERE follower_id = ?
                AND (followed_user_name = ? OR followed_artist_name = ?)
                """;

            try (PreparedStatement ps = connection.prepareStatement(deleteQuery)) {
                ps.setInt(1, currentUserId);
                ps.setString(2, selectedItem);
                ps.setString(3, selectedItem);

                int rowsDeleted = ps.executeUpdate();

                if (rowsDeleted > 0) {
                    loadFollowing();
                    showAlert("Success", "Successfully deleted!");
                } else {
                    showAlert("Error", "No matching record found to delete.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete the selected item.");
        }
    }


    @FXML
    private void onBackButtonClick(ActionEvent event) {
        navigateToView("/com/example/projedemo/user-mainpage-view.fxml", "User Main Page");
    }

    private void loadFollowing() {
        followingList.getItems().clear();

        String query = """
            SELECT followed_user_name AS name
            FROM following
            WHERE follower_id = ?
            UNION
            SELECT followed_artist_name AS name
            FROM following
            WHERE follower_id = ?
            """;

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, currentUserId);
            ps.setInt(2, currentUserId);

            ResultSet rs = ps.executeQuery();

            ObservableList<String> items = FXCollections.observableArrayList();
            while (rs.next()) {
                items.add(rs.getString("name"));
            }
            followingList.setItems(items);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load following list.");
        }
    }


    private void performSearch(String query) {
        searchResultsList.getItems().clear();

        if (query.isEmpty()) {
            return;
        }

        String sql = "SELECT user_name AS name FROM users WHERE lower(user_name) LIKE ? UNION SELECT artist_name AS name FROM artists WHERE lower(artist_name) LIKE ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + query.toLowerCase() + "%");
            ps.setString(2, "%" + query.toLowerCase() + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                searchResultsList.getItems().add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error performing search");
            e.printStackTrace();
        }
    }

    private int findFollowedId(Connection connection, String name) throws SQLException {
        String query = """
                SELECT user_id AS id FROM users WHERE user_name = ?
                UNION
                SELECT artist_id AS id FROM artists WHERE artist_name = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    private String findFollowedType(Connection connection, int id) throws SQLException {
        String query = """
                SELECT CASE
                    WHEN EXISTS(SELECT 1 FROM users WHERE user_id = ?) THEN 'user'
                    WHEN EXISTS(SELECT 1 FROM artists WHERE artist_id = ?) THEN 'artist'
                END AS type
                """;

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.setInt(2, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("type");
            }
        }
        return null;
    }

    private void navigateToView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane root = loader.load();
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.getScene().setRoot(root);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }
}

