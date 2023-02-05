package ru.netology.data;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.TimeoutException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;


class DB {
    private DB() {
    }

    private static Connection getConnection() throws SQLException {
        Dotenv dotenv = Dotenv.configure().load();
        return DriverManager.getConnection(
                dotenv.get("DB_URL"), dotenv.get("DB_USER"), dotenv.get("DB_PASS"));
    }

    @SneakyThrows
    public static String getHashFromLogin(@NotNull String login) {
        var runner = new QueryRunner();
        try (var conn = getConnection()) {
            return runner.query(conn,
                    String.format("SELECT password FROM users WHERE login='%s'",
                            login),
                    new ScalarHandler<>());
        }
    }

    @SneakyThrows
    public static String getAuthCodeFromLogin(@NotNull String login) {
        var runner = new QueryRunner();
        try (var conn = getConnection()) {
//                String id = runner.query(conn,
//                        String.format("select id from users where login='%s' limit 1;", user.getLogin()),
//                        new ScalarHandler<>());
//                return runner.query(conn,
//                        String.format("select code from auth_codes where user_id = '%s' order by created desc limit 1;",
//                                id),
//                        new ScalarHandler<>());
            long timeout = 10000; // ms
            long start = new Date().getTime();
            while ((start - new Date().getTime() < timeout)) {
                String code = runner.query(conn,
                        String.format("SELECT ac.code " +
                                        "FROM auth_codes ac, users u " +
                                        "WHERE ac.user_id = u.id and u.login='%s' " +
                                        "order by created desc " +
                                        "limit 1;",
                                login),
                        new ScalarHandler<>());
                if (code != null) return code;
            }
            throw new TimeoutException("It didn't waited for the verification code.");
        }
    }

    @SneakyThrows
    public static User addUser(@NotNull User user) {
        var runner = new QueryRunner();
        var dataSQL = "INSERT INTO users(id, login, password, status) VALUES (?, ?, ?, ?);";
        try (var conn = getConnection()) {
            String hashStr = getHashFromLogin( // как бы пароль кэшируется
                    DataHelper.Auth.getLoginFromPassword( // метод не добавлял getLoginFromPassword инклудом специально, чтобы акцентировать откуда он
                            user.getPassword()));
            runner.update(conn, dataSQL, user.getId(), user.getLogin(), hashStr, user.getStatus());
        }
        return user;
    }

    @SneakyThrows
    public static void dropTables() {
        var runner = new QueryRunner();
        try (var conn = getConnection()) {
            runner.execute(conn, "DROP TABLE IF EXISTS card_transactions;");
            runner.execute(conn, "DROP TABLE IF EXISTS cards;");
            runner.execute(conn, "DROP TABLE IF EXISTS auth_codes;");
            runner.execute(conn, "DROP TABLE IF EXISTS users;");
        }
    }

    @SneakyThrows
    public static void createTables() {
        var runner = new QueryRunner();
        try (var conn = getConnection()) {
            runner.execute(conn, "CREATE TABLE users (\n" +
                    "    id       CHAR(36)     PRIMARY KEY,\n" +
                    "    login    VARCHAR(255) UNIQUE NOT NULL,\n" +
                    "    password VARCHAR(255) NOT NULL,\n" +
                    "    status   VARCHAR(255) NOT NULL DEFAULT 'active'\n" +
                    ");");
            runner.execute(conn, "CREATE TABLE auth_codes (\n" +
                    "    id      CHAR(36)   PRIMARY KEY,\n" +
                    "    user_id CHAR(36)   NOT NULL,\n" +
                    "    code    VARCHAR(6) NOT NULL,\n" +
                    "    created TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                    "    FOREIGN KEY (user_id) REFERENCES users (id)\n" +
                    ");");
            runner.execute(conn, "CREATE TABLE cards (\n" +
                    "    id                 CHAR(36)    PRIMARY KEY,\n" +
                    "    user_id            CHAR(36)    NOT NULL,\n" +
                    "    number             VARCHAR(19) UNIQUE NOT NULL,\n" +
                    "    balance_in_kopecks INT         NOT NULL,\n" +
                    "    FOREIGN KEY (user_id) REFERENCES users (id)\n" +
                    ");");
            runner.execute(conn, "CREATE TABLE card_transactions (\n" +
                    "    id                CHAR(36)    PRIMARY KEY,\n" +
                    "    source            VARCHAR(19) NOT NULL,\n" +
                    "    target            VARCHAR(19) NOT NULL,\n" +
                    "    amount_in_kopecks INT         NOT NULL,\n" +
                    "    created           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP\n" +
                    ");");
        }
    }

}
