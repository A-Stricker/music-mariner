package com.amandastricker.musicmariner.model;

import java.util.List;

public class Playlist {
    private String intro;

    private String playlistName;
    private List<Song> songs;
    private String outro;

    public Playlist(String intro, String playlistName, List<Song> songs, String outro) {
        this.intro = intro;
        this.playlistName = playlistName;
        this.songs = songs;
        this.outro = outro;
    }

    public String getIntro() {
        return intro;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public String getOutro() {
        return outro;
    }
}
