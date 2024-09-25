package com.end2endlogic.quantum.extension.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import com.end2endlogic.quantum.extension.runtime.MapperConfig;
import dev.morphia.Morphia;
import dev.morphia.config.MorphiaConfig;
import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class QuantumExtensionResourceTest {


    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body(is("Hello"));
    }

    @Test
    public void testMappingEndpoint() {
        given()
                .when().get("/hello/mapping")
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
