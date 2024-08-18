package com.end2endlogic.quantum.extension.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class QuantumExtensionResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/quantum-extension")
                .then()
                .statusCode(200)
                .body(is("Hello quantum-extension"));
    }
}
