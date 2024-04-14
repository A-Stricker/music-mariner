package com.amandastricker.musicmariner.service;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Base64Utils;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;


@Service
public class SpotifyService {
    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final String spotifyApiBaseUrl = "https://api.spotify.com/v1";
    private final ResourceLoader resourceLoader;

    @Autowired
    public SpotifyService(RestTemplate restTemplate, OAuth2AuthorizedClientService authorizedClientService, ResourceLoader resourceLoader) {
        this.restTemplate = restTemplate;
        this.authorizedClientService = authorizedClientService;
        this.resourceLoader = resourceLoader;
    }

    // Method to create playlist
    public String createPlaylist(String userId, String playlistName, String description, boolean isPublic, OAuth2AuthenticationToken authentication) {
        String accessToken = getAccessToken(authentication);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        JSONObject playlistJson = new JSONObject();
        playlistJson.put("name", playlistName);
        playlistJson.put("description", description);
        playlistJson.put("public", isPublic);

        HttpEntity<String> entity = new HttpEntity<>(playlistJson.toString(), headers);
        String endpoint = spotifyApiBaseUrl + "/users/{user_id}/playlists";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class, userId);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return response.getBody();
            } else {

                throw new SpotifyServiceException("Failed to create playlist. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new SpotifyServiceException("An error occurred while creating the playlist", e);
        }
    }

    // Method to get access Token
    private String getAccessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        return client.getAccessToken().getTokenValue();
    }

    // Method to add 'Music Mariner' logo as playlist cover photo
    public void addImageToPlaylist(String playlistId, OAuth2AuthenticationToken authentication) throws IOException {
        String accessToken = getAccessToken(authentication);
        Resource imageResource = resourceLoader.getResource("classpath:static/images/music-mariner-logo.jpg");

        // Open an InputStream from the Resource and then pass it to ImageIO.read
        BufferedImage originalImage = ImageIO.read(imageResource.getInputStream());
        // Resize the image
        byte[] imageBytes = resizeImage(originalImage); // This needs to be a BufferedImage

        if (imageBytes.length > 256 * 1024) {
            throw new IOException("Image file is too large. Must be under 256 KB.");
        }

        String base64EncodedImage = Base64Utils.encodeToString(imageBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(base64EncodedImage, headers);

        String endpoint = spotifyApiBaseUrl + "/playlists/" + playlistId + "/images";

        try {
            restTemplate.exchange(endpoint, HttpMethod.PUT, entity, String.class);
        } catch (Exception e) {
            throw new SpotifyServiceException("An error occurred while adding an image to the playlist", e);
        }
    }

    private byte[] resizeImage(BufferedImage originalImage) throws IOException {
        Image resizedImage = originalImage.getScaledInstance(300, 300, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outputImage, "jpg", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return imageInByte;
    }

    // Method to search for track and artist
    public String searchTrack(String trackName, String artistName, OAuth2AuthenticationToken authentication) {
        String accessToken = getAccessToken(authentication);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Attempt a more structured query by specifying artist and track separately
        String query =  "name:" + trackName + " artist:" + artistName;

        String searchUrl = UriComponentsBuilder.fromHttpUrl(spotifyApiBaseUrl + "/search")
                .queryParam("q", query)
                .queryParam("type", "track")
                .queryParam("limit", "5") // Look through more results to find the best match
                .encode() // Ensure proper encoding of parameters
                .toUriString();

        // Print the generated search URL
        System.out.println("Generated search URL: " + searchUrl);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(searchUrl, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                JSONArray tracks = jsonResponse.getJSONObject("tracks").getJSONArray("items");

                if (!tracks.isEmpty()) {
                    // Directly return the URI of the first (and most relevant) track found
                    return tracks.getJSONObject(0).getString("uri");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print the full stack trace for debugging
            throw new SpotifyServiceException("An error occurred while searching for a track", e);
        }
        return null; // Return null if no match was found


    }

    // Method to add tracks to playlist
    public void addTracksToPlaylist(String playlistId, List<String> trackUris, OAuth2AuthenticationToken authentication) {
        String accessToken = getAccessToken(authentication);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        JSONObject tracksJson = new JSONObject();
        tracksJson.put("uris", new JSONArray(trackUris));

        HttpEntity<String> entity = new HttpEntity<>(tracksJson.toString(), headers);
        String endpoint = spotifyApiBaseUrl + "/playlists/" + playlistId + "/tracks";

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);
            HttpStatus statusCode = response.getStatusCode();

            if (statusCode == HttpStatus.CREATED || statusCode == HttpStatus.OK) {
                // Log successful addition
                System.out.println("Tracks successfully added to the playlist. Response: " + response.getBody());
            } else {
                // Handle unsuccessful addition
                throw new SpotifyServiceException("Failed to add tracks to playlist. Status code: " + statusCode);
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Error response body: " + e.getResponseBodyAsString());
            throw new SpotifyServiceException("An error occurred while adding tracks to the playlist", e);
        } catch (Exception e) {
            e.printStackTrace(); // This will print the stack trace to help identify other errors
            throw new SpotifyServiceException("An error occurred while adding tracks to the playlist", e);
        }
    }


    // Custom exception class for better error handling, add to this later
    public static class SpotifyServiceException extends RuntimeException {
        public SpotifyServiceException(String message) {
            super(message);
        }

        public SpotifyServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}