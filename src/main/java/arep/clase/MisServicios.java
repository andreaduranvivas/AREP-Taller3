package arep.clase;

import arep.clase.api.MovieApiClient;
import arep.clase.myspark.LBSpark;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static arep.clase.myspark.LBSpark.*;

public class MisServicios {
    public static void main(String[] args)  throws IOException, URISyntaxException {
        /**
        get("/hola", (req) -> {
            return "El Query es: " + req;
        });


        post("/hola", (body) -> {
            return "Recibido: " + body;
        });

         **/

        getStaticFiles().location("target/classes/public"); //funcion que cambia la ubicación de los archivos estaticos


        getResponse().type("application/json"); //funcion que cambia el tipo de respuesta a json

        LBSpark.get("/movies", (req) -> {

            Map<String, String> params = getResponse().readQueryParams(req); //funcion que lee los parametros de la query
            String movieTitle = params.getOrDefault("title", "");

            if (!movieTitle.isEmpty()) {
                try {
                    String movieDataJson = MovieApiClient.fetchMovieData(movieTitle);
                    return movieDataJson;
                } catch (Exception e) {
                    e.printStackTrace();
                    return ("HTTP/1.1   500 Internal Server Error\r\n\r\nError: " + e.getMessage());
                }
            } else {
                return ("HTTP/1.1   400 Bad Request\r\n\r\nError: No se proporcionó un título de película.");
            }
        });


        LBSpark.getInstance().runServer(args); //http://localhost:35000/action/hola?98
    }
}
