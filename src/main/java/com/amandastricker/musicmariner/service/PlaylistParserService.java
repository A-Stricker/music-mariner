package com.amandastricker.musicmariner.service;

import com.amandastricker.musicmariner.model.Playlist;
import com.amandastricker.musicmariner.model.Song;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service

public class PlaylistParserService {
    private static final Pattern INTRO_PATTERN = Pattern.compile("<<<intro>>>(.*?)<<<endintro>>>", Pattern.DOTALL);

    private static final Pattern PLAYLISTNAME_PATTERN = Pattern.compile("<<<startplaylistname>>>(.*?)<<<endplaylistname>>>", Pattern.DOTALL);
    private static final Pattern PLAYLIST_PATTERN = Pattern.compile("<<<startplaylist>>>(.*?)<<<endplaylist>>>", Pattern.DOTALL);
    private static final Pattern OUTRO_PATTERN = Pattern.compile("<<<startoutro>>>(.*?)<<<endoutro>>>", Pattern.DOTALL);

    public Playlist parsePlaylist(String gptOutput) {
        String intro = extractSection(gptOutput, INTRO_PATTERN);
        String outro = extractSection(gptOutput, OUTRO_PATTERN);
        String playlistName = extractSection(gptOutput, PLAYLISTNAME_PATTERN);
        List<Song> songs = extractSongs(gptOutput);

        return new Playlist(intro, playlistName, songs, outro);
    }

    private String extractSection(String text, Pattern pattern) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    private List<Song> extractSongs(String gptOutput) {
        List<Song> songs = new ArrayList<>();
        Matcher matcher = PLAYLIST_PATTERN.matcher(gptOutput);
        if (matcher.find()) {
            String[] tracks = matcher.group(1).trim().split("\n");
            for (String track : tracks) {
                String[] parts = track.split(" - ");
                if (parts.length == 2) {
                    songs.add(new Song(parts[0].trim().replace("\"", ""), parts[1].trim()));
                }
            }
        }
        return songs;
    }
}

