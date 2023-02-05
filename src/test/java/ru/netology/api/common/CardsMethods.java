package ru.netology.api.common;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import ru.netology.api.response.dto.CardsResponseItem;
import ru.netology.api.requests.Cards;

import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;


public class CardsMethods {
    private CardsMethods() {
    }

    public static List<CardsResponseItem> getCards(@NotNull String accessToken) {
        Response response = Cards.getCards(accessToken);
        return response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK) // 200
                .contentType(ContentType.JSON)
                .body(matchesJsonSchema(CardsResponseItem.getSchema()))
                .extract().jsonPath().getList("$", CardsResponseItem.class);
    }
}
