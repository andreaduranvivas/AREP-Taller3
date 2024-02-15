package arep.clase;

import arep.clase.api.MovieApiClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class MovieApiClientTest {

    @Test
    void testFetchMovieData() {
        try {
            String movieTitle = "Inception";
            String movieData = MovieApiClient.fetchMovieData(movieTitle);

            // Verificar que la respuesta no sea nula y contenga datos relevantes
            assertNotNull(movieData, "The movie data should not be null");
            assert movieData.contains("Title") : "The movie data should contain the title field";
            assert movieData.contains("Plot") : "The movie data should contain the plot field";
            assert movieData.contains("Poster") : "The movie data should contain the poster field";

        } catch (Exception e) {
            fail("An exception occurred while fetching movie data: " + e.getMessage());
        }
    }
}

