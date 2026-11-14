package ru.homework.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@Epic("SmartShop")
@Feature("API: список товаров")
class GoodsLectureTest extends ApiTestBase {
    @Test
    @DisplayName("Пустой список товаров")
    void emptyList() {
        check(client.list()).status(success).body("goods", empty());
    }

    @Test
    @DisplayName("Пустой список с пагинацией")
    void emptyPage() {
        check(client.list(config.data().api().firstPage(), config.data().api().pageSize()))
                .status(success).body("goods", empty());
    }

    @Test
    @Tag("smoke")
    @DisplayName("Созданный товар в списке: REST Assured")
    void createdProductIsListedWithRestAssured() {
        String name = uniqueName();
        long id = createProduct(name, price("listRestAssured"));
        check(client.list()).status(success).body("goods", hasSize(1))
                .body("goods[0].id", equalTo((int) id)).body("goods[0].name", equalTo(name))
                .body("goods[0].price", equalTo(price("listRestAssured").floatValue()));
    }

    @Test
    @DisplayName("Созданный товар в списке: AssertJ")
    void createdProductIsListedWithAssertJ() {
        String name = uniqueName();
        long id = createProduct(name, price("listAssertJ"));
        check(client.list()).singleProduct(id, name, price("listAssertJ"));
    }
}
