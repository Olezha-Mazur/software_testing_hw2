package ru.itmo.testing;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

final class ApiSpecifications {
    static final String REST_BASE_URI = "https://playground.nileslabs.com/api/v1";
    static final String GRAPHQL_BASE_URI = "https://graphql.postman-echo.com";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
    }

    private ApiSpecifications() {
    }

    static RequestSpecification jsonRequest(String baseUri) {
        return new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .build();
    }
}
