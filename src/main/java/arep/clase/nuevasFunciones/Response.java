package arep.clase.nuevasFunciones;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private String contentType = "text/html";

    public Response() {
    }

    public void type(String type) {
        this.contentType = type;
    }

    public String getContentType() {
        return contentType;
    }

    public Map<String, String> readQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx +  1), StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }
}

