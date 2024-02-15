package arep.clase.myspark;

import arep.clase.myspark.handle.Function;
import arep.clase.myspark.handle.PostFunction;
import arep.clase.nuevasFunciones.Response;
import arep.clase.nuevasFunciones.StaticFiles;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LBSpark {

    private static String serviceUri = "";
    private static Function service = null;
    private static PostFunction postService = null;

    private static boolean running = false;
    private static StaticFiles staticFiles = new StaticFiles();
    private static Response responseTemplate = new Response();
    private static Map<String, Function> getRoutes = new HashMap<>();
    private static Map<String, PostFunction> postRoutes = new HashMap<>();
    private static LBSpark _instance = new LBSpark();

    public LBSpark() {}

    public static LBSpark getInstance() {
        return _instance;
    }

    public static StaticFiles getStaticFiles() {
        return staticFiles;
    }

    public static Response getResponse() {
        return responseTemplate;
    }

    public void runServer(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        running = true;

        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            OutputStream out = clientSocket.getOutputStream();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine;
            byte[] outputLine;

            boolean firstLine = true;
            String uriStr = "";
            String method = "";
            StringBuilder bodyBuilder = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                if (firstLine) {
                    String[] parts = inputLine.split(" ");
                    method = parts[0];
                    uriStr = parts[1];
                    firstLine = false;
                }
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }

            if ("POST".equalsIgnoreCase(method)) {
                char[] buffer = new char[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) >  0) {
                    bodyBuilder.append(buffer,  0, bytesRead);
                }
            }

            URI requestUri = new URI(uriStr);

            try {
                if (requestUri.getPath().startsWith("/action")){
                    outputLine = callService(method, requestUri, bodyBuilder.toString());
                }else{
                    outputLine = htttpResponse(requestUri);  //es estático
                }
            } catch (Exception e) {
                e.printStackTrace();
                outputLine = httpError();
            }

            out.write(outputLine);

            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    public byte[] callService(String method, URI requestUri, String body) throws Exception {
        String calledServiceUri = requestUri.getPath().substring(7);
        byte[] outputBytes = new byte[0];

        if ("GET".equalsIgnoreCase(method)) {
            Function handler = getRoutes.get(calledServiceUri);
            if (handler != null) {
                String output = handler.handle(requestUri.getQuery());
                outputBytes = output.getBytes(StandardCharsets.UTF_8);
            }
        } else if ("POST".equalsIgnoreCase(method)) {
            PostFunction handler = postRoutes.get(calledServiceUri);
            if (handler != null) {
                String output = handler.handlePost(body);
                outputBytes = output.getBytes(StandardCharsets.UTF_8);
            }
        }

        String response = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + responseTemplate.getContentType() + "\r\n"
                + "\r\n";

        byte[] headerBytes = response.getBytes(StandardCharsets.UTF_8);

        byte[] resultBytes = new byte[headerBytes.length + outputBytes.length];
        System.arraycopy(headerBytes, 0, resultBytes, 0, headerBytes.length);
        System.arraycopy(outputBytes, 0, resultBytes, headerBytes.length, outputBytes.length);

        return resultBytes;
    }


    public static String get(String path, Function svc) {
        getRoutes.put(path, svc);

        return path;
    }

    public static void post(String path, PostFunction svc) {
        postRoutes.put(path, svc);
    }

    public static Map<String, Function> getGetRoutes() {
        return getRoutes;
    }

    public static Map<String, PostFunction> getPostRoutes() {
        return postRoutes;
    }

    /**
     * Generates an HTTP error response with status code 404 Not Found
     *
     * @return          the HTTP error response as a byte array
     */
    static byte[] httpError() {
        String errorResponse = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/html\r\n" +
                "\r\n" +
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Error Not found</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Error</h1>\n" +
                "    </body>\n" +
                "</html>";

        return errorResponse.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Generates an HTTP response based on the requested URI.
     *
     * @param  requestedURI   the URI that was requested
     * @return                the HTTP response as a byte array
     */
    public static byte[] htttpResponse(URI requestedURI) throws IOException {
        Path file;
        if ("/movie".equals(requestedURI.getPath())) {
            file = Paths.get(staticFiles.getDirectory(), "formulario.html");
        } else {
            file = Paths.get(staticFiles.getDirectory() + requestedURI.getPath());
        }

        if (Files.isRegularFile(file)) {
            String mimeType = Files.probeContentType(file);
            byte[] fileBytes = Files.readAllBytes(file);

            ByteArrayOutputStream response = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(response);

            dos.writeBytes("HTTP/1.1 200 OK\r\n");
            dos.writeBytes("Content-Type: " + mimeType + "\r\n");
            dos.writeBytes("Content-Length: " + fileBytes.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(fileBytes, 0, fileBytes.length);

            return response.toByteArray();
        } else {
            return httpError();
        }
    }

}
