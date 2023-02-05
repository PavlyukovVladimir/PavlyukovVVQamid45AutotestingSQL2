package ru.netology.api.requests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.Value;
import ru.netology.data.Constants;

@Value
public class Cards {
    private Cards() {
        super();
    }

    private static final String endpointCards = "/api/cards";

    public static Response getCards(String accessToken) {
        Response response = RestAssured.given()
                .baseUri(Constants.BASE_URL).basePath(endpointCards)
                .auth().oauth2(accessToken)
                .headers("Content-Type", "application/json",
                        "Accept", "application/json")
                .when()
                .log()
                .all()
                .get();
        response.then()
                .log()
                .all();
        return response;
    }

}
