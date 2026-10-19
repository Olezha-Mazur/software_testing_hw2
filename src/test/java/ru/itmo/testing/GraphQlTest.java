package ru.itmo.testing;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

class GraphQlTest {

    @Test
    @DisplayName("GraphQL query возвращает data без errors")
    void executesQuery() {
        Response response = executeGraphQl("query { hello(person: { name: \"API Student\", age: 20 }) }");

        assertThat(response.path("errors"), nullValue());
        assertThat(response.path("data.hello"), containsString("API Student"));
    }

    @Test
    @DisplayName("GraphQL CreatePerson возвращает созданные поля без errors")
    void createsPerson() {
        String name = "API Student";
        int age = 20;
        Response response = executeGraphQl("mutation { createPerson(person: { name: \"" + name
                + "\", age: " + age + " }) { id name age } }");

        assertThat(response.path("errors"), nullValue());
        assertThat(response.path("data.createPerson.id"), not(nullValue()));
        assertThat(response.path("data.createPerson.name"), equalTo(name));
        assertThat(response.path("data.createPerson.age"), equalTo(age));
    }

    private Response executeGraphQl(String query) {
        return given()
                .spec(ApiSpecifications.jsonRequest(ApiSpecifications.GRAPHQL_BASE_URI))
                .body(Map.of("query", query))
                .when().post("/graphql")
                .then().log().ifValidationFails()
                .statusCode(200)
                .body("data", not(nullValue()))
                .extract().response();
    }
}
