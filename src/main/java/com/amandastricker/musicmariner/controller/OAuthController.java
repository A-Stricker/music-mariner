package com.amandastricker.musicmariner.controller;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuthController {
    private final OAuth2AuthorizedClientService authorizedClientService;

    public OAuthController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/oauth/login")
    public String redirectToSpotifyLogin() {
        // This endpoint could be used to redirect to Spotify's login page.
        // The actual redirection is handled by Spring Security's OAuth2LoginAuthenticationFilter.
        return "redirect:/";
    }

    @GetMapping("/oauth/callback")
    public String spotifyCallback(@RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient, Model model) {
        // Here, you can access the client's details, such as the access token
        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        // Use the access token to make requests to the Spotify API
        // For example, to get the current user's profile or playlists
        // Implement these methods in a service class where you handle the Spotify API calls
        // String userProfileJson = spotifyService.getCurrentUserProfile(accessToken);
        // model.addAttribute("userProfile", userProfileJson);

        return "redirect:/playlist"; // Redirect to the playlist view or another appropriate view
    }
}



