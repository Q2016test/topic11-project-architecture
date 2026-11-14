package ru.homework.support;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.homework.api.ApiAssert;
import ru.homework.api.basic.GoodsApi;
import ru.homework.config.TestData;
import ru.homework.model.Product;
import ru.homework.config.TestConfig;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class GoodsFixtures {
    private final GoodsApi client;
    private final TestConfig config;
    private final Set<Long> createdIds = new LinkedHashSet<>();
    private final Set<String> reservedNames = new HashSet<>();

    public GoodsFixtures(TestConfig config) {
        this.config = config;
        client = new GoodsApi(config);
    }

    @Step("Подготовить уникальное название товара")
    public String reserveName() {
        return reserveName(config.value("fixture.name.prefix"));
    }

    @Step("Сгенерировать название на основе {prefix}")
    public String reserveName(String prefix) {
        String name = prefix + "-" + UUID.randomUUID();
        reservedNames.add(name);
        return name;
    }

    @Step("Подготовить товар {name} стоимостью {price}")
    public Product create(String name, BigDecimal price) {
        Response response = client.add(name, price);
        new ApiAssert(response, config).status(config.integer("api.status.success"));
        long id = response.jsonPath().getLong("data.id");
        createdIds.add(id);
        new ApiAssert(response, config).created();
        return new Product(id, name, price);
    }

    @Step("Получить товары для подготовки и очистки данных")
    private List<Map<String, Object>> products() {
        Response response = client.list(config.data().api().firstPage(), config.data().api().lookupPageSize());
        new ApiAssert(response, config).status(config.integer("api.status.success"));
        return response.jsonPath().getList("goods");
    }

    @Step("Найти созданный товар {name}")
    public Product findByName(String name) {
        Map<String, Object> product = products().stream().filter(item -> name.equals(item.get("name")))
                .findFirst().orElseThrow(() -> new IllegalStateException("Товар не найден: " + name));
        long id = ((Number) product.get("id")).longValue();
        createdIds.add(id);
        return new Product(id, name, new BigDecimal(product.get("price").toString()));
    }

    @Step("Удалить созданные тестовые товары")
    public void cleanup() {
        if (!reservedNames.isEmpty()) {
            for (Map<String, Object> product : products()) {
                if (reservedNames.contains(product.get("name"))) {
                    createdIds.add(((Number) product.get("id")).longValue());
                }
            }
        }
        for (long id : createdIds) {
            new ApiAssert(client.delete(id), config).deletedOrAbsent();
        }
        createdIds.clear();
        reservedNames.clear();
    }

    @Step("Подготовить товар из конфига")
    public Product createUnique(TestData.ProductData data) {
        return create(reserveName(data.name()), data.price());
    }
}
