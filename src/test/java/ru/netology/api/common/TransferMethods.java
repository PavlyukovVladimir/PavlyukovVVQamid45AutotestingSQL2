package ru.netology.api.common;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import ru.netology.api.requests.Transfer;
import ru.netology.data.DataHelper;


public class TransferMethods {
    private TransferMethods() {
    }

    public static void postTransfer(@NotNull String accessToken, DataHelper.Transfer.Info transferInfo) {
        Response response = Transfer.postTransfer(accessToken, transferInfo);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK); // 200
    }

    public static void postTransfer401(@NotNull String accessToken, DataHelper.Transfer.Info transferInfo) {
        Response response = Transfer.postTransfer(accessToken, transferInfo);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED); // 401
    }
}
