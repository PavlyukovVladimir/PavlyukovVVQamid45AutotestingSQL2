package ru.netology.api.requests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.Value;
import ru.netology.data.Constants;
import ru.netology.data.DataHelper;


@Value
public class Transfer {
    private Transfer() {
        super();
    }

    private static final String endpointTransfer = "/api/transfer";

    public static Response postTransfer(String accessToken,DataHelper.Transfer.Info transferInfo) {
        Response response = RestAssured.given()
                .baseUri(Constants.BASE_URL).basePath(endpointTransfer)
                .auth().oauth2(accessToken)
                .headers("Content-Type", "application/json",
                        "Accept", "application/json")
                .body(transferInfo)
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
