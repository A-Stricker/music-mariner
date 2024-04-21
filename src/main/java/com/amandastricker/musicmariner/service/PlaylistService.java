package com.amandastricker.musicmariner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final ResourceLoader resourceLoader;

    @Autowired
    public PlaylistService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String readSamplePlaylist() throws IOException {
        var resource = resourceLoader.getResource("classpath:symposium_serenade.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}

