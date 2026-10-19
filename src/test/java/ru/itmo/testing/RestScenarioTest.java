package ru.itmo.testing;

import io.restassured.response.Response;
import io.restassured.filter.session.SessionFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class RestScenarioTest {

    @Test
    @DisplayName("POST => id => PATCH => GET сохраняет неизменённые данные поста")
    void createsUpdatesAndReadsTheSamePost() {
        String initialTitle = "API homework post";
        String changedTitle = "API homework post updated";
        String body = "This field must survive PATCH";
        int userId = 1;

        SessionFilter session = new SessionFilter();

        Response created = given()
                .spec(ApiSpecifications.jsonRequest(ApiSpecifications.REST_BASE_URI))
                .filter(session)
                .body(Map.of("title", initialTitle, "body", body, "userId", userId))
                .when().post("/posts")
                .then().log().ifValidationFails()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo(initialTitle))
                .extract().response();

        String id = created.path("id");
        assertThat(id, notNullValue());

        given()
                .spec(ApiSpecifications.jsonRequest(ApiSpecifications.REST_BASE_URI))
                .filter(session)
                .body(Map.of("title", changedTitle))
                .when().patch("/posts/{id}", id)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("title", equalTo(changedTitle));

        Response received = given()
                .spec(ApiSpecifications.jsonRequest(ApiSpecifications.REST_BASE_URI))
                .filter(session)
                .when().get("/posts/{id}", id)
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("title", equalTo(changedTitle))
                .extract().response();

        assertThat(received.path("body"), equalTo(body));
    }
}
