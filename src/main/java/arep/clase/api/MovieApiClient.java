package arep.clase.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MovieApiClient {
    private static final String API_KEY = "b7232f2";
    private static final String BASE_URL = "https://www.omdbapi.com/?apikey=" + API_KEY + "&t=";

    public static String fetchMovieData(String movieTitle) throws Exception {
        String encodedTitle = URLEncoder.encode(movieTitle, StandardCharsets.UTF_8);
        String fullUrl = BASE_URL + encodedTitle;

        URL url = new URL(fullUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return content.toString();
        } else {
            throw new Exception("Failed to fetch movie data: HTTP " + status);
        }
    }
}