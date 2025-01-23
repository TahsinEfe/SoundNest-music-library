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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


    public class AdminDashboardController {
        @FXML
        private Button addButton;
        @FXML
        private Button editButton;
        @FXML
        private Button deleteButton;
        @FXML
        private Button logOutButton;
        @FXML
        private TableView<User> dataTableUsers;
        @FXML
        private TableView<Artist> dataTableArtists;
        @FXML
        private TableView<Notification> dataTableNotifications;
        @FXML
        private Label tableLabel;

        public AdminDashboardController() {
        }

        @FXML
        void onUserContent(MouseEvent event) {
            this.loadUsers();
            this.dataTableArtists.setVisible(false);
            this.dataTableNotifications.setVisible(false);
            this.dataTableUsers.setVisible(true);
        }

        @FXML
        void onArtistContentClick(MouseEvent event) {
            this.loadArtists();
            this.dataTableUsers.setVisible(false);
            this.dataTableNotifications.setVisible(false);
            this.dataTableArtists.setVisible(true);
        }

        @FXML
        void onNotificationButtonClick(MouseEvent event) {
            this.loadNotifications();
            this.dataTableUsers.setVisible(false);
            this.dataTableArtists.setVisible(false);
            this.dataTableNotifications.setVisible(true);
        }

        @FXML
        void onDeleteButtonClick(MouseEvent event) {
            if (this.dataTableUsers.isVisible()) {
                this.deleteSelectedUser();
            } else if (this.dataTableArtists.isVisible()) {
                this.deleteSelectedArtist();
            } else if (this.dataTableNotifications.isVisible()) {
                this.deleteSelectedNotification();
            }

        }

        @FXML
        private void onAddButtonClick(MouseEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/com/example/projedemo/add-view.fxml"));
                Scene scene = new Scene((Parent) loader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Add Item");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading add-view.fxml!");
            }

        }

        @FXML
        void onEditButtonClick(MouseEvent event) {
            if (this.dataTableUsers.isVisible()) {
                this.editEntry((User) this.dataTableUsers.getSelectionModel().getSelectedItem(), "users");
            } else if (this.dataTableArtists.isVisible()) {
                this.editEntry((Artist) this.dataTableArtists.getSelectionModel().getSelectedItem(), "artists");
            } else if (this.dataTableNotifications.isVisible()) {
                this.editEntry((Notification) this.dataTableNotifications.getSelectionModel().getSelectedItem(), "admins");
            }

        }

        private void deleteSelectedUser() {
            User selectedUser = (User) this.dataTableUsers.getSelectionModel().getSelectedItem();
            if (selectedUser == null) {
                System.out.println("No user selected!");
            } else {
                String query = "DELETE FROM users WHERE user_id = ?";

                try (
                        Connection connection = DatabaseConnection.connect();
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                ) {
                    preparedStatement.setString(1, selectedUser.getUserId());
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("User deleted successfully!");
                        this.dataTableUsers.getItems().remove(selectedUser);
                    } else {
                        System.out.println("No user found to delete.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

        private void deleteSelectedArtist() {
            Artist selectedArtist = (Artist) this.dataTableArtists.getSelectionModel().getSelectedItem();
            if (selectedArtist == null) {
                System.out.println("No artist selected!");
            } else {
                String query = "DELETE FROM artists WHERE artist_id = ?";

                try (
                        Connection connection = DatabaseConnection.connect();
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                ) {
                    preparedStatement.setString(1, selectedArtist.getArtistId());
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Artist deleted successfully!");
                        this.dataTableArtists.getItems().remove(selectedArtist);
                    } else {
                        System.out.println("No artist found to delete.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

        private void deleteSelectedNotification() {
            Notification selectedNotification = (Notification) this.dataTableNotifications.getSelectionModel().getSelectedItem();
            if (selectedNotification == null) {
                System.out.println("No notification selected!");
            } else {
                String query = "DELETE FROM notifications WHERE notification_id = ?";

                try (
                        Connection connection = DatabaseConnection.connect();
                        PreparedStatement preparedStatement = connection.prepareStatement(query);
                ) {
                    preparedStatement.setString(1, selectedNotification.getNotificationId());
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Notification deleted successfully!");
                        this.dataTableNotifications.getItems().remove(selectedNotification);
                    } else {
                        System.out.println("No notification found to delete.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

        private void loadUsers() {
            ObservableList<User> userData = FXCollections.observableArrayList();
            String query = "SELECT * FROM users";

            try (
                    Connection connection = DatabaseConnection.connect();
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                while (resultSet.next()) {
                    userData.add(new User(resultSet.getString("user_id"), resultSet.getString("user_name"), resultSet.getString("email"), resultSet.getString("password"), resultSet.getString("created_account")));
                }

                this.dataTableUsers.getColumns().clear();
                TableColumn<User, String> userIdColumn = new TableColumn("ID");
                userIdColumn.setCellValueFactory(new PropertyValueFactory("userId"));
                TableColumn<User, String> userNameColumn = new TableColumn("Name");
                userNameColumn.setCellValueFactory(new PropertyValueFactory("userName"));
                TableColumn<User, String> emailColumn = new TableColumn("Email");
                emailColumn.setCellValueFactory(new PropertyValueFactory("email"));
                TableColumn<User, String> passwordColumn = new TableColumn("Password");
                passwordColumn.setCellValueFactory(new PropertyValueFactory("password"));
                TableColumn<User, String> createdAccountColumn = new TableColumn("Created");
                createdAccountColumn.setCellValueFactory(new PropertyValueFactory("createdAccount"));
                this.dataTableUsers.getColumns().addAll(new TableColumn[]{userIdColumn, userNameColumn, emailColumn, passwordColumn, createdAccountColumn});
                this.dataTableUsers.setItems(userData);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        private void loadArtists() {
            ObservableList<Artist> artistData = FXCollections.observableArrayList();
            String query = "SELECT * FROM artists";

            try (
                    Connection connection = DatabaseConnection.connect();
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                while (resultSet.next()) {
                    artistData.add(new Artist(resultSet.getString("artist_id"), resultSet.getString("artist_name"), resultSet.getString("artist_password")));
                }

                this.dataTableArtists.getColumns().clear();
                TableColumn<Artist, String> artistIdColumn = new TableColumn("ID");
                artistIdColumn.setCellValueFactory(new PropertyValueFactory("artistId"));
                TableColumn<Artist, String> artistNameColumn = new TableColumn("Name");
                artistNameColumn.setCellValueFactory(new PropertyValueFactory("artistName"));
                TableColumn<Artist, String> artistPasswordColumn = new TableColumn("Password");
                artistPasswordColumn.setCellValueFactory(new PropertyValueFactory("artistPassword"));
                this.dataTableArtists.getColumns().addAll(new TableColumn[]{artistIdColumn, artistNameColumn, artistPasswordColumn});
                this.dataTableArtists.setItems(artistData);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        private void loadNotifications() {
            ObservableList<Notification> notificationsData = FXCollections.observableArrayList();
            String query = "SELECT * FROM notifications";

            try (
                    Connection connection = DatabaseConnection.connect();
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                while (resultSet.next()) {
                    notificationsData.add(new Notification(resultSet.getString("notification_id"), resultSet.getString("user_id"), resultSet.getString("message"), resultSet.getString("created_at"), resultSet.getBoolean("is_read")));
                }

                this.dataTableNotifications.getColumns().clear();
                TableColumn<Notification, String> notificationIdColumn = new TableColumn("Notification ID");
                notificationIdColumn.setCellValueFactory(new PropertyValueFactory("notificationId"));
                TableColumn<Notification, String> userIdColumn = new TableColumn("User ID");
                userIdColumn.setCellValueFactory(new PropertyValueFactory("userId"));
                TableColumn<Notification, String> messageColumn = new TableColumn("Message");
                messageColumn.setCellValueFactory(new PropertyValueFactory("message"));
                TableColumn<Notification, String> createdAtColumn = new TableColumn("Created At");
                createdAtColumn.setCellValueFactory(new PropertyValueFactory("createdAt"));
                TableColumn<Notification, Boolean> isReadColumn = new TableColumn("Is Read");
                isReadColumn.setCellValueFactory(new PropertyValueFactory("isRead"));
                this.dataTableNotifications.getColumns().addAll(new TableColumn[]{notificationIdColumn, userIdColumn, messageColumn, createdAtColumn, isReadColumn});
                this.dataTableNotifications.setItems(notificationsData);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        @FXML
        void onLogOutClick(MouseEvent event) {
            try {
                Stage stage = (Stage) this.logOutButton.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/com/example/projedemo/user-login-view.fxml"));
                Scene scene = new Scene((Parent) loader.load());
                stage.setScene(scene);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error loading user-login-view.fxml!");
            }

        }

        private <T> void editEntry(T selectedItem, String table) {
            if (selectedItem == null) {
                System.out.println("No entry selected!");
            } else {
                try {
                    FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/com/example/projedemo/edit-entry-view.fxml"));
                    Stage stage = new Stage();
                    stage.setScene(new Scene((Parent) loader.load()));
                    EditEntryController controller = (EditEntryController) loader.getController();
                    if (table.equals("users")) {
                        User user = (User) selectedItem;
                        controller.setData(user.getUserId(), user.getUserName(), user.getPassword(), table);
                    } else if (table.equals("artists")) {
                        Artist artist = (Artist) selectedItem;
                        controller.setData(artist.getArtistId(), artist.getArtistName(), artist.getArtistPassword(), table);
                    } else if (table.equals("admins")) {
                        Notification admin = (Notification) selectedItem;
                        controller.setData(admin.getNotificationId(), admin.getMessage(), admin.getCreatedAt(), table);
                    }

                    stage.setTitle("Edit Entry");
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

