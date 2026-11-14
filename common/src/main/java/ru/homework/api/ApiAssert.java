package ru.homework.api;

import io.qameta.allure.Step;
import ru.homework.config.TestConfig;
import io.restassured.response.Response;
import org.hamcrest.Matcher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

public final class ApiAssert {
    private final Response response;
    private final TestConfig config;

    public ApiAssert(Response response, TestConfig config) {
        this.response = response;
        this.config = config;
    }

    @Step("Проверить код ответа: {expected}")
    public ApiAssert status(int expected) {
        response.then().statusCode(expected);
        return this;
    }

    @Step("Проверить допустимый код ответа при удалении")
    public ApiAssert deletedOrAbsent() {
        response.then().statusCode(anyOf(is(config.integer("api.status.success")), is(config.integer("api.status.missing"))));
        return this;
    }

    @Step("Проверить поле ответа {path}")
    public ApiAssert body(String path, Matcher<?> matcher) {
        response.then().body(path, matcher);
        return this;
    }

    @Step("Проверить текст ответа")
    public ApiAssert text(Matcher<String> matcher) {
        response.then().body(matcher);
        return this;
    }

    @Step("Проверить заголовок {name}")
    public ApiAssert header(String name, Matcher<String> matcher) {
        response.then().header(name, matcher);
        return this;
    }

    @Step("Проверить успешное создание товара и его идентификатор")
    public ApiAssert created() {
        status(config.integer("api.status.success")).body("message", equalTo(config.value("api.text.success"))).body("data.id", notNullValue());
        assertThat(response.jsonPath().getLong("data.id")).as("Идентификатор созданного товара").isPositive();
        return this;
    }

    @Step("Проверить данные товара {id}")
    public ApiAssert product(long id, String name, BigDecimal price) {
        body("id", equalTo((int) id)).body("name", equalTo(name))
                .body("price", equalTo(price.floatValue()));
        return this;
    }

    @Step("Проверить единственный товар в списке через AssertJ")
    public ApiAssert singleProduct(long id, String name, BigDecimal price) {
        status(config.integer("api.status.success"));
        List<Map<String, Object>> goods = response.jsonPath().getList("goods");
        assertThat(goods).as("Список товаров").hasSize(1);
        Map<String, Object> item = goods.get(0);
        assertThat(item).as("Название товара").containsEntry("name", name);
        assertThat(((Number) item.get("id")).longValue()).as("ID товара").isEqualTo(id);
        assertThat(new BigDecimal(item.get("price").toString())).as("Цена товара")
                .isEqualByComparingTo(price);
        return this;
    }
}
