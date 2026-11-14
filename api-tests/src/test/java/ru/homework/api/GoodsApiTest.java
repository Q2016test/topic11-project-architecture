package ru.homework.api;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.homework.config.TestConfig;
import ru.homework.config.TestData;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;

@Epic("SmartShop")
@Feature("API: операции с товарами")
class GoodsApiTest extends ApiTestBase {
    @Test
    @DisplayName("GET: пустой список")
    void listEmpty200() {
        check(client.list()).status(success).body("goods", empty());
    }

    @ParameterizedTest
    @MethodSource("paginationCases")
    @DisplayName("GET: пагинация")
    void listPagination200(int page, int size, int expectedCount) {
        TestData.ApiData data = config.data().api();
        for (int i = 0; i < data.paginationProductCount(); i++) {
            BigDecimal price = data.paginationBasePrice().add(data.paginationPriceStep().multiply(BigDecimal.valueOf(i)));
            createProduct(uniqueName(), price);
        }
        check(client.list(page, size)).status(success).body("goods", hasSize(expectedCount));
    }

    @Test
    @DisplayName("POST: товар с нулевой ценой")
    void add200() {
        String name = uniqueName();
        long id = createProduct(name, price("zero"));
        check(client.get(id)).status(success).product(id, name, price("zero"));
    }

    @ParameterizedTest
    @MethodSource("invalidProducts")
    @DisplayName("POST: невалидный товар")
    void addInvalid400(Map<String, Object> body, String field) {
        check(client.addBody(body)).status(invalid).body(field, not(emptyOrNullString()));
        check(client.list()).status(success).body("goods", empty());
    }

    @Test
    @DisplayName("POST: повторное название")
    void addDuplicate400() {
        String name = uniqueName();
        createProduct(name, price("initial"));
        check(client.addBody(product(name, price("duplicate")))).status(invalid).body("data", nullValue())
                .body("message", containsString(config.value("api.text.duplicate")));
    }

    @Test
    @Tag("smoke")
    @DisplayName("GET: получить товар")
    void get200() {
        String name = uniqueName();
        long id = createProduct(name, price("get"));
        check(client.get(id)).status(success).product(id, name, price("get"));
    }

    @Test
    @DisplayName("GET: удалённый товар")
    void getMissing404() {
        long id = createProduct(uniqueName(), price("missing"));
        check(client.delete(id)).status(success);
        check(client.get(id)).status(missing).body("data", nullValue())
                .body("message", containsString(config.value("api.text.missing")));
    }

    @Test
    @Tag("smoke")
    @DisplayName("DELETE: удалить товар")
    void delete200() {
        long id = createProduct(uniqueName(), price("missing"));
        check(client.delete(id)).status(success).text(containsString(config.value("api.text.deleted")));
        check(client.get(id)).status(missing);
    }

    @Test
    @DisplayName("DELETE: отсутствующий товар")
    void deleteMissing404() {
        check(client.delete(config.data().api().missingId())).status(missing)
                .text(containsString(config.value("api.text.missing")));
    }

    @Test
    @Tag("smoke")
    @DisplayName("PATCH: изменить товар")
    void patch200() {
        long id = createProduct(uniqueName(), price("initial"));
        String name = uniqueName();
        check(client.patch(id, product(name, price("updated")))).status(success).product(id, name, price("updated"));
        check(client.get(id)).status(success).product(id, name, price("updated"));
    }

    @ParameterizedTest
    @MethodSource("invalidProducts")
    @DisplayName("PATCH: невалидные данные")
    void patchInvalid400(Map<String, Object> body, String field) {
        String name = uniqueName();
        long id = createProduct(name, price("initial"));
        check(client.patch(id, body)).status(invalid).body(field, not(emptyOrNullString()));
        check(client.get(id)).status(success).product(id, name, price("initial"));
    }

    @Test
    @DisplayName("PATCH: занятое название")
    void patchDuplicate400() {
        long id = createProduct(uniqueName(), price("initial"));
        String name = uniqueName();
        createProduct(name, price("duplicate"));
        check(client.patch(id, product(name, price("updated")))).status(invalid)
                .text(containsString(config.value("api.text.duplicate")));
    }

    @Test
    @DisplayName("PATCH: отсутствующий товар")
    void patchMissing404() {
        check(client.patch(config.data().api().missingId(), product(uniqueName(), price("missing"))))
                .status(missing).text(containsString(config.value("api.text.missing")));
    }

    @ParameterizedTest
    @MethodSource("authCases")
    @DisplayName("Запрос без авторизации")
    void authenticationRequired401(TestData.AuthCase data) {
        Object body = data.hasBody() ? product(uniqueName(), price("initial")) : null;
        String path = config.value(data.pathKey()).replace("{id}", String.valueOf(config.data().api().missingId()));
        check(client.anonymousRequest(data.method(), path, body)).status(unauthorized)
                .header(config.value("api.auth.header"), containsString(config.value("api.auth.scheme")));
    }

    @Test
    @DisplayName("Неверный пароль")
    void incorrectCredentials401() {
        check(client.addWithWrongPassword(product(uniqueName(), price("initial")))).status(unauthorized);
    }

    static Stream<Arguments> paginationCases() {
        return TestConfig.load().data().api().paginationCases().stream()
                .map(data -> Arguments.of(data.page(), data.size(), data.expectedCount()));
    }

    static Stream<Arguments> invalidProducts() {
        return TestConfig.load().data().api().invalidProducts().stream()
                .map(data -> Arguments.of(data.body(), data.field()));
    }

    static Stream<TestData.AuthCase> authCases() {
        return TestConfig.load().data().api().authCases().stream();
    }
}
