package ru.homework.api;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import ru.homework.api.basic.GoodsApi;
import ru.homework.config.TestConfig;
import ru.homework.support.GoodsFixtures;

import java.math.BigDecimal;
import java.util.Map;

@Tag("api")
abstract class ApiTestBase {
    protected TestConfig config;
    protected GoodsApi client;
    protected GoodsFixtures fixtures;
    protected int success;
    protected int invalid;
    protected int unauthorized;
    protected int missing;

    @BeforeEach
    @Step("Подготовить API-клиент")
    void setUpApi() {
        config = TestConfig.load();
        config.printPublicValues();
        client = new GoodsApi(config);
        fixtures = new GoodsFixtures(config);
        success = config.integer("api.status.success");
        invalid = config.integer("api.status.invalid");
        unauthorized = config.integer("api.status.unauthorized");
        missing = config.integer("api.status.missing");
    }

    protected ApiAssert check(Response response) {
        return new ApiAssert(response, config);
    }

    protected BigDecimal price(String key) {
        return config.data().api().price(key);
    }

    @Step("Подготовить тело товара {name}")
    protected Map<String, Object> product(String name, BigDecimal price) {
        return Map.of("name", name, "price", price);
    }

    @Step("Сгенерировать название для API-теста")
    protected String uniqueName() {
        return fixtures.reserveName();
    }

    @Step("Создать товар для API-теста")
    protected long createProduct(String name, BigDecimal price) {
        return fixtures.create(name, price).id();
    }

    @AfterEach
    @Step("Очистить данные API-теста")
    void removeCreatedProducts() {
        if (fixtures != null) {
            fixtures.cleanup();
        }
    }
}
