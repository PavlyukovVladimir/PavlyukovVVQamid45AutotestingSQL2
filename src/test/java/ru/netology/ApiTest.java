package ru.netology;

import org.junit.jupiter.api.*;
import ru.netology.data.Constants;
import ru.netology.data.DataHelper;
import ru.netology.api.response.dto.CardsResponseItem;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.api.common.AuthMethods.postAuth;
import static ru.netology.api.common.AuthMethods.postAuthVerification;
import static ru.netology.api.common.CardsMethods.getCards;
import static ru.netology.api.common.TransferMethods.postTransfer401;
import static ru.netology.api.common.TransferMethods.postTransfer;
import static ru.netology.data.DataHelper.Transfer;

import ru.netology.data.DataHelper.Auth;
import ru.netology.data.DataHelper.Exec.JarControl;


public class ApiTest {
    private static JarControl jarControl;
    private static String accessToken;
    private String firstCardId;
    private String secondCardId;
    private Double actualFirstCardBalance;
    private Double actualSecondCardBalance;

    @BeforeAll
    public static void allStart() {
        if (Constants.PRE_TEST_PREPARATION) {
            DataHelper.Exec.DBContainerControl.start();
            jarControl = new DataHelper.Exec.JarControl();
            jarControl.start();
        }
        Auth.Info info = new Auth.Info("vasya", "qwerty123");
        postAuth(info);
        accessToken = postAuthVerification(
                new DataHelper.Verify.Info(info.getLogin(),
                        DataHelper.Verify.getVerificationCode(info)))
                .getToken();
    }

    @AfterAll
    public static void allStop() {
        if (Constants.POST_TEST_PREPARATION) {
            jarControl.stop();
            DataHelper.Exec.DBContainerControl.stop();
        }
    }

    @BeforeEach
    public void setUp() {
        Transfer.resetBalance();
        List<CardsResponseItem> cardsLst = getCards(accessToken);
        assertThat(cardsLst, hasSize(2));
        firstCardId = cardsLst.get(0).getId();
        actualFirstCardBalance = cardsLst.get(0).getBalance();
        secondCardId = cardsLst.get(1).getId();
        actualSecondCardBalance = cardsLst.get(1).getBalance();
    }

    @Test
    @DisplayName("Копейки отображаются в апи Cards")
    public void hundredthsOfRubleTest() {
        Transfer.updateCardBalance(firstCardId, 1000001);
        Transfer.updateCardBalance(secondCardId, 1000099);

        updateActualBalanceInfo();

        assertEquals(10000.01, actualFirstCardBalance);
        assertEquals(10000.99, actualSecondCardBalance);
    }

    @Test
    @DisplayName("С первой на вторую целое число")
    public void fromFirstToSecondIntTest() {
        Double expectedFirstCardBalance = actualFirstCardBalance - 1000.0;
        Double expectedSecondCardBalance = actualSecondCardBalance + 1000.0;

        Transfer.Info transferInfo = Transfer.getTransferInfoById(firstCardId, secondCardId, 1000.0);
        postTransfer(accessToken, transferInfo);

        updateActualBalanceInfo();

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    @DisplayName("С первой на вторую 1 копейку")
    public void fromFirstToSecond0_01Test() {
        Double expectedFirstCardBalance = actualFirstCardBalance - 0.01;
        Double expectedSecondCardBalance = actualSecondCardBalance + 0.01;

        Transfer.Info transferInfo = Transfer.getTransferInfoById(firstCardId, secondCardId, 0.01);
        postTransfer(accessToken, transferInfo);

        updateActualBalanceInfo();

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    @DisplayName("Со второй на первую целое число")
    public void fromSecondToFirstIntTest() {
        Double expectedFirstCardBalance = actualFirstCardBalance + 2000.0;
        Double expectedSecondCardBalance = actualSecondCardBalance - 2000.0;

        Transfer.Info transferInfo = Transfer.getTransferInfoById(secondCardId, firstCardId, 2000.0);
        postTransfer(accessToken, transferInfo);

        updateActualBalanceInfo();

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    @DisplayName("С первой на вторую целое число больше чем есть")
    public void negativeFromFirstToSecondMoreThanThereIsTest() {
        Transfer.Info transferInfo = Transfer.getTransferInfoById(firstCardId, secondCardId, actualFirstCardBalance + 10000);
        postTransfer401(accessToken, transferInfo);
    }

    @Test
    @DisplayName("Со второй на первую целое число больше чем есть")
    public void negativeFecondToFirstMoreThanThereIsTest() {
        Transfer.Info transferInfo = Transfer.getTransferInfoById(secondCardId, firstCardId, actualSecondCardBalance + 10000);
        postTransfer401(accessToken, transferInfo);
    }

    @Test
    @DisplayName("С первой на вторую отрицательное число")
    public void negativeFromFirstToSecondNegativeAmountTest() {
        Transfer.Info transferInfo = Transfer.getTransferInfoById(firstCardId, secondCardId, -1000.0);
        postTransfer401(accessToken, transferInfo);
    }

    @Test
    @DisplayName("С первой на вторую 0")
    public void negativeFromFirstToSecond0Test() {
        Transfer.Info transferInfo = Transfer.getTransferInfoById(firstCardId, secondCardId, 0.0);
        postTransfer401(accessToken, transferInfo);
    }

    @Test
    @DisplayName("С первой на вторую c пустым номером")
    public void fromFirstToEmptySecondIntTest() {
        Transfer.Info transferInfo = Transfer.getTransferInfoById(firstCardId, secondCardId, 1000.0);
        transferInfo = new Transfer.Info(transferInfo.getFrom(), "", transferInfo.getAmount());
        postTransfer401(accessToken, transferInfo);
    }

    @Test
    @DisplayName("С первой на неизвестную вторую")
    public void fromFirstToUnknownIntTest() {
        Double expectedFirstCardBalance = actualFirstCardBalance - 1000.0;
        Double expectedSecondCardBalance = actualSecondCardBalance;

        Transfer.Info transferInfo = Transfer.getTransferInfoById(firstCardId, secondCardId, 1000.0);
        transferInfo = new Transfer.Info(transferInfo.getFrom(), "4459 0012 0000 1234", transferInfo.getAmount());
        postTransfer(accessToken, transferInfo);

        updateActualBalanceInfo();

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    @Test
    @DisplayName("Со второй на неизвестную первую")
    public void fromSecondToUnknownIntTest() {
        Double expectedFirstCardBalance = actualFirstCardBalance;
        Double expectedSecondCardBalance = actualSecondCardBalance - 1000.0;

        Transfer.Info transferInfo = Transfer.getTransferInfoById(secondCardId, firstCardId, 1000.0);
        transferInfo = new Transfer.Info(transferInfo.getFrom(), "4459 0012 0000 1234", transferInfo.getAmount());
        postTransfer(accessToken, transferInfo);

        updateActualBalanceInfo();

        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

    private void updateActualBalanceInfo() {
        List<CardsResponseItem> cardsLst = getCards(accessToken);
        actualFirstCardBalance = cardsLst.stream()
                .filter(x -> Objects.equals(x.getId(), firstCardId))
                .findFirst()
                .get()
                .getBalance();
        actualSecondCardBalance = cardsLst.stream()
                .filter(x -> Objects.equals(x.getId(), secondCardId))
                .findFirst()
                .get()
                .getBalance();
    }
}
