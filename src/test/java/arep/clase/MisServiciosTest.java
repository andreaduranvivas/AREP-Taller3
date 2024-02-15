package arep.clase;

import arep.clase.myspark.LBSpark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

class MisServiciosTest {

    private LBSpark lbSpark;

    @BeforeEach
    void setUp() {
        lbSpark = new LBSpark();
    }

    @Test
    void testGetRequest() throws Exception {
        lbSpark.get("/helloo", req -> "Hello, world!");

        String expectedResponse = "Hello, world!";
        String response = Arrays.toString(lbSpark.callService("GET", URI.create("/helloo"), "Hello, world!"));

        assertEquals(expectedResponse, "Hello, world!");
    }
}
