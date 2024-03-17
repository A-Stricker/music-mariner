package com.amandastricker.musicmariner.controller;

import com.amandastricker.musicmariner.model.Playlist;
import com.amandastricker.musicmariner.service.PlaylistParserService;
import com.amandastricker.musicmariner.service.PlaylistService;
import com.amandastricker.musicmariner.service.SpotifyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.json.JSONObject;

import java.io.IOException;

@Controller
public class PlaylistController {

    private final PlaylistParserService playlistParserService;
    private final SpotifyService spotifyService;
    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistParserService playlistParserService, SpotifyService spotifyService, PlaylistService playlistService) {
        this.playlistParserService = playlistParserService;
        this.spotifyService = spotifyService;
        this.playlistService = playlistService;
    }

    @GetMapping("/playlist")
    public String getPlaylist(Model model, OAuth2AuthenticationToken authentication) {
        try {
            String sampleOutput = playlistService.readSamplePlaylist();
            Playlist playlist = playlistParserService.parsePlaylist(sampleOutput);

            // Assuming the user ID is part of the `OAuth2AuthenticationToken`. If not, you'll have to fetch it.
            String userId = authentication.getName(); // Or get the userID from the OAuth2 details.

            // Call SpotifyService to create the playlist
            String createPlaylistResponse = spotifyService.createPlaylist(
                    userId, playlist.getPlaylistName(), "Generated by Music Mariner", true, authentication
            );

            JSONObject responseJson = new JSONObject(createPlaylistResponse);
            String playlistId = responseJson.getString("id");
            // Store playlistId or add it to the model if you need it in the view

            // Add image to the playlist
            spotifyService.addImageToPlaylist(playlistId, authentication);

            // Add attributes to the model as needed
            model.addAttribute("playlist", playlist);
            model.addAttribute("createPlaylistResponse", createPlaylistResponse);
            model.addAttribute("playlistId", playlistId);

        } catch (IOException e) {
            model.addAttribute("error", "Could not read sample playlist");
        }
        return "playlist"; // This should match the name of the HTML template in src/main/resources/templates
    }


    // The rest of your code for handling Spotify interactions...
    // The above optional code assumes you have a method to obtain the userId
    // and the OAuth2 token is correctly configured in the SpotifyService

}