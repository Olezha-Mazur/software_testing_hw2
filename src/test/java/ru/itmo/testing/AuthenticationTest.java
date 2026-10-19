package ru.itmo.testing;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

class AuthenticationTest {

    @Test
    @DisplayName("Токен, полученный при login, даёт доступ к профилю")
    void logsInAndUsesFreshBearerTokenForProfile() {
        Response login = given()
                .spec(ApiSpecifications.jsonRequest(ApiSpecifications.REST_BASE_URI))
                .body(Map.of("username", "Bret", "password", "Password@123"))
                .when().post("/auth/login")
                .then().log().ifValidationFails()
                .statusCode(200)
                .extract().response();

        String token = login.path("access_token");
        assertThat(token, not(anyOf(nullValue(), is(""))));

        Response profile = given()
                .spec(ApiSpecifications.jsonRequest(ApiSpecifications.REST_BASE_URI))
                .header("Authorization", "Bearer " + token)
                .when().get("/auth/me")
                .then().log().ifValidationFails()
                .statusCode(200)
                .extract().response();

        assertThat(profile.path("id") != null || profile.path("username") != null || profile.path("login") != null,
                is(true));
    }

    @Test
    @DisplayName("Неверный Bearer token не открывает профиль")
    void rejectsInvalidBearerToken() {
        given()
                .spec(ApiSpecifications.jsonRequest(ApiSpecifications.REST_BASE_URI))
                .header("Authorization", "Bearer deliberately-invalid-token")
                .when().get("/auth/me")
                .then().log().ifValidationFails()
                .statusCode(anyOf(is(401), is(403)));
    }

    @ParameterizedTest(name = "Неверные credentials: {0} / {1}")
    @MethodSource("invalidCredentials")
    @DisplayName("Неверные учётные данные отклоняются с понятной ошибкой")
    void rejectsInvalidLoginCredentials(String username, String password) {
        given()
                .spec(ApiSpecifications.jsonRequest(ApiSpecifications.REST_BASE_URI))
                .body(Map.of("username", username, "password", password))
                .when().post("/auth/login")
                .then().log().ifValidationFails()
                .statusCode(401)
                .body("error", equalTo("Invalid username or password."));
    }

    private static Stream<Arguments> invalidCredentials() {
        return Stream.of(
                Arguments.of("Bret", "wrong-password"),
                Arguments.of("unknown-user", "Password@123")
        );
    }
}
