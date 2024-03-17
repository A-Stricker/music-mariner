package com.amandastricker.musicmariner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Base64Utils;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONObject;

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
                // More specific error handling can be implemented here based on the response status code
                throw new SpotifyServiceException("Failed to create playlist. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new SpotifyServiceException("An error occurred while creating the playlist", e);
        }
    }

    private String getAccessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        return client.getAccessToken().getTokenValue();
    }

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

    // Custom exception class for better error handling
    public static class SpotifyServiceException extends RuntimeException {
        public SpotifyServiceException(String message) {
            super(message);
        }

        public SpotifyServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}