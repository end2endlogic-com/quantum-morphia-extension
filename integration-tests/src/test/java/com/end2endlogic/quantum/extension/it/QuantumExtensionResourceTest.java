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

    /**
     * The datastore maps exactly the indexed entities of the configured packages: nothing from a package
     * that is not listed, and nothing missing.
     */
    @Test
    public void mapsExactlyTheEntitiesOfTheConfiguredPackages() {
        given()
                .when().get("/hello/mapped-types")
                .then()
                .statusCode(200)
                .body(is("com.end2endlogic.quantum.extension.it.models.Animal,"
                        + "com.end2endlogic.quantum.extension.it.models.Author,"
                        + "com.end2endlogic.quantum.extension.it.models.Dog,"
                        + "com.end2endlogic.quantum.extension.it.models2.Book"));
    }

    /** Indexes are applied for the mapped entities (quarkus.morphia.create-indexes=true). */
    @Test
    public void appliesIndexesForMappedEntities() {
        given()
                .when().get("/hello/indexes/books")
                .then()
                .statusCode(200)
                .body(org.hamcrest.Matchers.containsString("published_1"));
    }

    /** An entity outside the configured packages gets neither a mapping nor a collection. */
    @Test
    public void leavesUnlistedEntitiesAlone() {
        given()
                .when().get("/hello/collections")
                .then()
                .statusCode(200)
                .body(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("unlisted")));
    }

    /**
     * A subclass that inherits its mapping annotation is mapped at startup, so the index it declares is
     * built. Only the subclass declares {@code breed}.
     */
    @Test
    public void mapsSubclassThatInheritsItsMappingAnnotation() {
        given()
                .when().get("/hello/indexes/animals")
                .then()
                .statusCode(200)
                .body(org.hamcrest.Matchers.containsString("breed_1"));
    }
}
