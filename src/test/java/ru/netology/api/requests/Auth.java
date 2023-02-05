package ru.netology.requests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.Value;
import ru.netology.Constants;
import ru.netology.data.DataHelper;

@Value
public class Auth {
    private Auth() {
        super();
    }

    private static final String endpointAuth = "/api/auth";
    private static final String endpointAuthVerification = "/api/auth/verification";

    public static Response postAuthUser(DataHelper.Auth.Info authInfo) {
        Response response = RestAssured.given()
                .baseUri(Constants.BASE_URL).basePath(endpointAuth)
                .headers("Content-Type", "application/json",
                        "Accept", "application/json")
                .body(authInfo)
                .when()
                .log()
                .all()
                .post();
        response.then()
                .log()
                .all();
        return response;
    }

    public static Response postAuthAuthVerification(DataHelper.Verify.Info verifyInfo) {
        Response response = RestAssured.given()
                .baseUri(Constants.BASE_URL).basePath(endpointAuthVerification)
                .headers("Content-Type", "application/json",
                        "Accept", "application/json")
                .body(verifyInfo)
                .when()
                .log()
                .all()
                .post();
        response.then()
                .log()
                .all();
        return response;
    }
}
