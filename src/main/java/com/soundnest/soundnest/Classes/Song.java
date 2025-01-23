package com.soundnest.soundnest.Classes;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Song {
    private StringProperty title;
    private StringProperty duration;
    private StringProperty songName;
    private StringProperty songLink;

    public Song(String title, String duration, String songName, String songLink) {
        this.title = new SimpleStringProperty(title);
        this.duration = new SimpleStringProperty(duration);
        this.songName = new SimpleStringProperty(songName);
        this.songLink = new SimpleStringProperty(songLink);
    }

    public String getSongLink() {
        return (String)this.songLink.get();
    }

    public void setSongLink(String songLink) {
        this.songLink.set(songLink);
    }

    public StringProperty songLinkProperty() {
        return this.songLink;
    }

    public String getTitle() {
        return (String)this.title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return this.title;
    }

    public String getDuration() {
        return (String)this.duration.get();
    }

    public void setDuration(String duration) {
        this.duration.set(duration);
    }

    public StringProperty durationProperty() {
        return this.duration;
    }

    public String getSongName() {
        return (String)this.songName.get();
    }

    public void setSongName(String songName) {
        this.songName.set(songName);
    }

    public StringProperty songNameProperty() {
        return this.songName;
    }

    public String toString() {
        String var10000 = (String)this.title.get();
        return var10000 + " - " + (String)this.songName.get();
    }
}
