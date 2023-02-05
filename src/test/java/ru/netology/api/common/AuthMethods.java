package ru.netology.common;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import ru.netology.data.DataHelper;
import ru.netology.dto.AuthVerificationResponse;
import ru.netology.requests.Auth;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;

public class AuthMethods {
    private AuthMethods() {
    }

    public static void postAuth(@NotNull DataHelper.Auth.Info authInfo) {
        Response response = Auth.postAuthUser(authInfo);
        response.then()
                .assertThat().statusCode(HttpStatus.SC_OK); // 200
    }

    public static AuthVerificationResponse postAuthVerification(@NotNull DataHelper.Verify.Info verifyInfo) {
        Response response = Auth.postAuthAuthVerification(verifyInfo);
        return response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK) // 200
                .contentType(ContentType.JSON)
                .body(matchesJsonSchema(AuthVerificationResponse.getSchema()))
                .extract().response().as(AuthVerificationResponse.class);
    }
}
