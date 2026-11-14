package ru.homework.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;

public final class TestConfig {
    private final Properties properties;
    private final TestData data;

    private TestConfig(Properties properties, TestData data) {
        this.properties = properties;
        this.data = data;
        httpUrl("ui.url");
        httpUrl("api.url");
        admin();
        loggingMode();
        positive("element.timeout.ms");
        positive("http.connect.timeout.ms");
        positive("http.read.timeout.ms");
        if (data.ui().purchaseQuantity() <= 0 || data.startingProduct().price().signum() < 0) {
            throw new IllegalArgumentException("Некорректные данные стартового товара");
        }
    }

    private static final class Holder {
        private static final TestConfig INSTANCE = read();
    }

    public static TestConfig load() {
        return Holder.INSTANCE;
    }

    private static TestConfig read() {
        Properties properties = new Properties();
        String override = System.getProperty("test.config");
        Path configPath = override == null ? null : Path.of(override).toAbsolutePath();
        try {
            try (Reader reader = new InputStreamReader(resource("test.properties"), StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            if (configPath != null) {
                try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
                    properties.load(reader);
                }
            }
            String dataPath = properties.getProperty("data.file");
            try (InputStream input = dataPath.startsWith("classpath:")
                    ? resource(dataPath.substring("classpath:".length()))
                    : Files.newInputStream(resolveDataPath(dataPath, configPath))) {
                return new TestConfig(properties, new ObjectMapper().readValue(input, TestData.class));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать конфигурацию common", e);
        }
    }

    private static Path resolveDataPath(String value, Path configPath) {
        Path path = Path.of(value);
        return path.isAbsolute() || configPath == null ? path : configPath.getParent().resolve(path);
    }

    private static InputStream resource(String name) {
        InputStream stream = TestConfig.class.getResourceAsStream("/" + name);
        if (stream == null) {
            throw new IllegalStateException("Не найден ресурс common: " + name);
        }
        return stream;
    }

    public String value(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Не задан параметр " + key);
        }
        if (value.startsWith("${") && value.endsWith("}")) {
            String name = value.substring(2, value.length() - 1);
            String environmentValue = System.getenv(name);
            if (environmentValue == null || environmentValue.isBlank()) {
                throw new IllegalArgumentException("Не задана переменная окружения " + name);
            }
            return environmentValue;
        }
        return value;
    }

    public String optional(String key) {
        return properties.getProperty(key, "");
    }

    public int integer(String key) {
        return Integer.parseInt(value(key).trim());
    }

    private int positive(String key) {
        int result = integer(key);
        if (result <= 0) {
            throw new IllegalArgumentException(key + " должен быть положительным");
        }
        return result;
    }

    private String httpUrl(String key) {
        String value = value(key).trim();
        URI uri = URI.create(value);
        if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                || uri.getHost() == null || uri.getUserInfo() != null) {
            throw new IllegalArgumentException("Некорректный URL: " + key);
        }
        return value;
    }

    public String uiUrl() {
        return httpUrl("ui.url");
    }

    public String apiUrl() {
        return httpUrl("api.url");
    }

    public long elementTimeoutMs() {
        return positive("element.timeout.ms");
    }

    public LoggingMode loggingMode() {
        return LoggingMode.valueOf(value("logging.mode").trim().toUpperCase(Locale.ROOT));
    }

    public Credentials admin() {
        return new Credentials(value("admin.login"), value("admin.password"));
    }

    public TestData data() {
        return data;
    }

    public void printPublicValues() {
        properties.stringPropertyNames().stream().sorted().filter(key -> !key.startsWith("admin."))
                .forEach(key -> System.out.println(key + "=" + properties.getProperty(key)));
        System.out.println("test.data=" + data);
    }

    public enum LoggingMode {
        OFF, INFO, DEBUG
    }

    public record Credentials(String login, String password) {
        @Override
        public String toString() {
            return "Credentials[hidden]";
        }
    }
}
