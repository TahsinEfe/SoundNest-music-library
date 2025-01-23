package com.soundnest.soundnest.Classes;

public class Notification {
    private String notificationId;
    private String userId;
    private String message;
    private String createdAt;
    private boolean isRead;


    public Notification(String notificationId, String userId, String message, String createdAt, boolean isRead) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean getIsRead() {
        return isRead;
    }
}
