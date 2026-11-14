package ru.homework.api.basic;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.homework.config.TestConfig;

import java.math.BigDecimal;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class GoodsApi extends BasicApi {
    public GoodsApi(TestConfig config) {
        super(config);
    }

    @Step("Получить список товаров")
    public Response list() {
        return given().spec(builder.anonymous()).when().get(config.value("api.path.list"));
    }

    @Step("Получить страницу {page}, размер {size}")
    public Response list(int page, int size) {
        return given().spec(builder.anonymous()).queryParam("page", page).queryParam("size", size)
                .when().get(config.value("api.path.list"));
    }

    @Step("Создать товар {name}")
    public Response add(String name, BigDecimal price) {
        return addBody(Map.of("name", name, "price", price));
    }

    @Step("Отправить запрос создания товара")
    public Response addBody(Object body) {
        return given().spec(builder.authorized()).body(body).when().post(config.value("api.path.add"));
    }

    @Step("Получить товар {id}")
    public Response get(long id) {
        return given().spec(builder.authorized()).when().get(config.value("api.path.item"), id);
    }

    @Step("Изменить товар {id}")
    public Response patch(long id, Object body) {
        return given().spec(builder.authorized()).body(body).when().patch(config.value("api.path.item"), id);
    }

    @Step("Удалить товар {id}")
    public Response delete(long id) {
        return given().spec(builder.authorized()).when().delete(config.value("api.path.item"), id);
    }

    @Step("Создать товар с неверным паролем")
    public Response addWithWrongPassword(Object body) {
        return given().spec(builder.wrongPassword()).body(body).when().post(config.value("api.path.add"));
    }
}
