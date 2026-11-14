package ru.homework.pages;

import io.qameta.allure.Step;

import java.math.BigDecimal;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CartPageAssert {
    private final CartPage page;

    CartPageAssert(CartPage page) {
        this.page = page;
    }

    @Step("Проверить элементы страницы")
    public CartPageAssert loaded() {
        page.modal.shouldBe(visible);
        page.closeButton.shouldBe(visible).shouldHave(exactText(page.config.value("ui.text.close")));
        page.checkoutButton.shouldBe(visible, enabled).shouldHave(text(page.config.value("ui.text.checkout")));
        page.total.shouldBe(visible).shouldHave(matchText("\\d+(\\.\\d+)?"));
        return this;
    }

    @Step("Проверить число позиций: {expected}")
    public CartPageAssert itemCount(int expected) {
        page.items.shouldHave(size(expected));
        return this;
    }

    @Step("Проверить товар {id}")
    public CartPageAssert product(long id, String name, int quantity) {
        page.item(id).shouldBe(visible);
        page.productName(id).shouldBe(visible).shouldHave(exactText(name));
        page.quantity(id).shouldBe(visible).shouldHave(exactText(String.valueOf(quantity)));
        page.removeButton(id).shouldBe(visible, enabled).shouldHave(exactText(page.config.value("ui.text.remove")));
        return this;
    }

    @Step("Проверить стоимость корзины: {expected}")
    public CartPageAssert total(BigDecimal expected) {
        page.total.shouldHave(exactText(expected.stripTrailingZeros().toPlainString()));
        return this;
    }

    @Step("Проверить, что сумма не превышает {maximum}")
    public CartPageAssert totalAtMost(BigDecimal maximum) {
        BigDecimal actual = page.totalPrice();
        assertTrue(actual.compareTo(maximum) <= 0,
                () -> "Ожидалась сумма ≤ " + maximum + ", получено " + actual);
        return this;
    }

    @Step("Проверить закрытие корзины")
    public CartPageAssert closed() {
        page.modal.shouldBe(hidden);
        return this;
    }
}
