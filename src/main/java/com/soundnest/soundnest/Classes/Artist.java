package com.soundnest.soundnest.Classes;

public class Artist {
    private String artistId;
    private String artistName;
    private String artistPassword;

    public Artist(String artistId, String artistName, String artistPassword){
        this.artistId = artistId;
        this.artistName = artistName;
        this.artistPassword = artistPassword;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getArtistPassword() {
        return artistPassword;
    }
}
