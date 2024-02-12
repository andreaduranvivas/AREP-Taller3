package arep.clase.myspark;
import static arep.clase.myspark.LBSpark.get;
import static spark.Spark.post;

import java.io.IOException;
import java.net.URISyntaxException;

public class MisServicios {
    public static void main(String[] args)  throws IOException, URISyntaxException {
        get("/hola", (req) -> {
            return "El Query es: " + req;
        });


        post("/hola", (req, res) -> {
            String body = req.body();
            return "Recibido: " + body;
        });

        LBSpark.getInstance().runServer(args); //http://localhost:35000/action/hola?98
    }
}
