package ru.netology.data;

import com.github.javafaker.Faker;
import com.ibm.icu.text.Transliterator;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.SneakyThrows;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
//import org.junit.jupiter.api.Test;
import org.openqa.selenium.NotFoundException;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.util.Map.entry;
import static ru.netology.data.DB.createTables;
import static ru.netology.data.DB.dropTables;


public class DataHelper {
    private DataHelper() {
    }


    public static class Exec {
        private Exec() {
        }

//        private static class ProcessRead implements Callable<List<String>> {
//
//            private InputStream inputStream;
//
//            private ProcessRead(@NotNull Process ps) {
//                this.inputStream = ps.getInputStream();
//            }
//
//            @Override
//            public List<String> call() {
//                return new BufferedReader(new InputStreamReader(inputStream))
//                        .lines()
//                        .collect(Collectors.toList());
//            }
//        }

        public static class JarControl {
            private final String pathToJarFile;
            private Process process = null;

            public JarControl() {
                this.pathToJarFile = "artifacts/app-deadline.jar";
            }

            public void start() {
                Dotenv dotenv = Dotenv.configure().load();
                process = execJar(pathToJarFile,
                        "-P:jdbc.url=" + dotenv.get("DB_URL"),
                        "-P:jdbc.user=" + dotenv.get("DB_USER"),
                        "-P:jdbc.password=" + dotenv.get("DB_PASS")
                        , " & echo $! > ./testserver.pid"); // для ручной остановки
            }

            public void stop() {
                process.destroy();
            }
        }

        public static class DBContainerControl {

            private static Process process = null;

            private DBContainerControl() {
            }

            @SneakyThrows
            public static void start() {
                if (process == null) {
                    process = execBashScriptFromFile("src/test/resources/startDBContainer.sh");
                    TimeUnit.SECONDS.sleep(30);
                } else {
                    throw new RuntimeException("Forbidden start the container if it is already running.");
                }
            }

            @SneakyThrows
            public static void stop() {
                dropTables();
                createTables();
                if (process != null) {
                    process = execBashScriptFromFile("src/test/resources/stopDBContainer.sh");
                    long start = System.currentTimeMillis();
                    long timeout = 60000;
                    while (process.isAlive() && (System.currentTimeMillis() - start) < timeout) {
                        TimeUnit.SECONDS.sleep(1);
                    }
                    if (!process.isAlive()) {
                        process = null;
                    } else {
                        process.destroyForcibly();
                        process = null;
                        throw new RuntimeException("The container stop timeout has been exceeded.");
                    }

                } else {
                    throw new RuntimeException("Forbidden stop the container if it is already stopped.");
                }
            }
        }

        @SneakyThrows
        private static Process execJar(@NotNull String pathToJarFile, String... params) {
//            String javaHome = System.getProperty("java.home");
//            String javaBinStr = javaHome +
//                    File.separator + "bin" +
//                    File.separator + "java";
            List<String> commands = new ArrayList<>();
            commands.add("java");
            commands.add("-jar");
            commands.add(pathToJarFile);
            commands.addAll(Arrays.asList(params));
            return new ProcessBuilder()
                    .directory(new File("./"))
                    .command(commands)
                    .start();
        }

        @SneakyThrows
        private static Process execBashScriptFromFile(@NotNull String pathToScriptFile, String... params) {
            List<String> commands = new ArrayList<>();
            commands.add("sh");
            commands.add(pathToScriptFile);
            commands.addAll(Arrays.asList(params));
            return new ProcessBuilder()
                    .directory(new File("./"))
                    .command(commands)
                    .start();
        }
    }

    public static class Auth {
        private Auth() {
        }

        @Value
        public static class Info {
            String login;
            String password;
        }

        public enum AuthStatuses {
            active,
            blocked
        }

        public enum BreakCredentialsType {
            BOTH,
            LOGIN,
            PASSWORD
        }

        public static Info breakCredentials(@NotNull Info info, @NotNull BreakCredentialsType type) {
            Faker faker = new Faker(Locale.forLanguageTag("ru"));
            Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");
            boolean isLogin = false;
            boolean isPass = false;
            switch (type) {
                case LOGIN:
                    isLogin = true;
                    break;
                case PASSWORD:
                    isPass = true;
                    break;
                default:
                    isLogin = true;
                    isPass = true;
            }
            return new Info(
                    isLogin ? toLatinTrans.transliterate(faker.name().username()) : info.getLogin(),
                    isPass ? faker.internet().password() : info.getPassword()
            );
        }

        private final static Map<String, String> secret = Map.ofEntries(
                entry("vasya", "qwerty123"),
                entry("petya", "123qwerty"));

        private static String getRandomPassword() {
            String[] passwords = secret.values().toArray(String[]::new);
            return passwords[new Random().nextInt(secret.size())];
        }

        // метод к сожалению нужен внутри пакета data в DB.addUser() поэтому доступ пакетный
        static String getLoginFromPassword(@NotNull String password) {
            for (Map.Entry<String, String> entry : secret.entrySet()) {
                if (password.equals(entry.getValue())) {
                    return entry.getKey();
                }
            }
            throw new NotFoundException("Unknown password");
        }

        public static Info getAuthorisationInfo() {
            Faker faker = new Faker(Locale.forLanguageTag("ru"));
            Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");
            return new Info(
                    toLatinTrans.transliterate(faker.name().username()),
                    getRandomPassword());
        }

        public static Info setCredentials(@NotNull Info info, @NotNull AuthStatuses status) {
            DB.addUser(new User(
                    UUID.randomUUID().toString(),
                    info.getLogin(),
                    info.getPassword(),
                    status.toString()
            ));
            return info;
        }
    }

    public static class Verify {
        private Verify() {
        }

        public static String getVerificationCode(@NotNull Auth.Info info) {
            return DB.getAuthCodeFromLogin(info.getLogin());
        }
    }

}
