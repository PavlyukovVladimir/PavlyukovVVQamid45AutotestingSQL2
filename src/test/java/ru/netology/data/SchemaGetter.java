package io.thrive.fs.help;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
//import java.lang.invoke.MethodHandles;

public class SchemaGetter {
    private SchemaGetter() {
    }

    public static String getResponseSchema(@NotNull String className) {
        File file = new File("src/test/resources/json_schemas/dto/response", "schema" + className + ".json");
        String fileStr;
        try (FileInputStream inputStreamReader = new FileInputStream(file)) {
            fileStr = new String(inputStreamReader.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileStr;
    }

    public static String getAuxiliarySchema(@NotNull String className) {
        if (className.endsWith("Info")) {
            className = className.substring(0, className.length() - 4);
        }
        File file = new File("src/test/resources/json_schemas/dto/auxiliary", "schema" + className + ".json");
        String fileStr;
        try (FileInputStream inputStreamReader = new FileInputStream(file)) {
            fileStr = new String(inputStreamReader.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileStr;
    }

    public static String getRequestSchema(@NotNull String className) {
        if (className.endsWith("Info")) {
            className = className.substring(0, className.length() - 4);
        }
        File file = new File("src/test/resources/json_schemas/dto/request", "schema" + className + ".json");
        String fileStr;
        try (FileInputStream inputStreamReader = new FileInputStream(file)) {
            fileStr = new String(inputStreamReader.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileStr;
    }

}
