package com.soundnest.soundnest.Classes;

public class Playlist {
    private String playlistName;
    private String createdAccount;

    public Playlist(String playlistName, String createdAccount){
        this.playlistName = playlistName;
        this.createdAccount = createdAccount;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getCreatedAccount() {
        return createdAccount;
    }

    public void setCreatedAccount(String createdAccount) {
        this.createdAccount = createdAccount;
    }
}
