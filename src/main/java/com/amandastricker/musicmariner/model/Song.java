package com.amandastricker.musicmariner.model;

public class Song {
    private String title;
    private String artist;

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }
}
