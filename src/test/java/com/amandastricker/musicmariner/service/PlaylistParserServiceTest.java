package com.amandastricker.musicmariner.service;

import org.junit.jupiter.api.BeforeEach;
import com.amandastricker.musicmariner.model.Playlist;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

import static org.junit.jupiter.api.Assertions.*;

class PlaylistParserServiceTest {
    private PlaylistParserService playlistParserService;

    @BeforeEach
    void setUp() {
        playlistParserService = new PlaylistParserService();
    }

    @ParameterizedTest
    @MethodSource("provideGptOutputs")
    void testParsePlaylist(String gptOutput, String expectedStartOfIntro, String expectedPlaylistName, String expectedEndOfOutro) {
        Playlist result = playlistParserService.parsePlaylist(gptOutput);
        assertAll("Validating parsed playlist",
                () -> assertNotNull(result, "Playlist should not be null"),
                () -> assertTrue(result.getIntro().startsWith(expectedStartOfIntro), "Intro should start with the expected text"),
                () -> assertEquals(expectedPlaylistName, result.getPlaylistName(), "Playlist name should match the expected name"),
                () -> assertTrue(result.getOutro().contains(expectedEndOfOutro), "Outro should contain the expected text"),
                () -> assertNotNull(result.getSongs(), "Songs list should not be null"),
                () -> assertTrue(result.getSongs().size() > 0, "There should be at least one song")
        );
    }

    private static Stream<Arguments> provideGptOutputs() {
        return Stream.of(
                Arguments.of(
        "<<<intro>>>Ahoy, music matey! Gear up for a scholarly voyage with tunes that tickle the intellect and soothe the soul. This playlist is crafted to resonate with your honors symposium, blending chill vibes with clever lyrics and diverse genres. Whether it's to ponder over, present to, or simply play in the background, these tracks will set the perfect tone for your academic adventure.<<<endintro>>>\n" +
                "<<<startplaylistname>>>Symposium Serenade<<<endplaylistname>>>\n" +
                "<<<startplaylist>>>\n\n" +
                "\"The Scientist\" - Coldplay\n" +
                "\"Smarter\" - Eisley\n" +
                "\"E=MC2\" - Big Audio Dynamite\n" +
                "\"White & Nerdy\" - Weird Al Yankovic\n" +
                "\"Don't Stop Me Now\" - Queen\n" +
                "\"Bach: Cello Suite No. 1 in G Major\" - Yo-Yo Ma\n" +
                "\"Brain\" - Banks\n" +
                "\"I.Q.\" - Ani DiFranco\n" +
                "\"We Are All Made of Stars\" - Moby\n" +
                "\"Mathematics\" - Mos Def\n" +
                "\"Think\" - Aretha Franklin\n" +
                "\"Scholarships\" - Drake & Future\n" +
                "\"High Hopes\" - Panic! At The Disco\n" +
                "\"Book of Days\" - Enya\n" +
                "\"Beethoven: Symphony No. 9\" - Berlin Philharmonic & Herbert von Karajan\n" +
                "\"Beautiful Mind\" - Nas\n" +
                "\"Clair de Lune\" - Claude Debussy\n" +
                "\"Young Einstein\" - Yukmouth\n" +
                "\"Symphony of Science\" - John Boswell\n" +
                "\"Pure Imagination\" - Jamie Cullum\n" +
                "<<<endplaylist>>>\n" +
                "<<<startoutro>>>Your playlist has now docked! Thanks for sailing the sound waves with Music Mariner. Anchors aweigh until next time!<<<endoutro>>>",
                "Ahoy, music matey! Gear up for a scholarly voyage with tunes that tickle the intellect and soothe the soul.",
                "Symposium Serenade",
                "Anchors aweigh until next time!"




                )

        );
    }
}
