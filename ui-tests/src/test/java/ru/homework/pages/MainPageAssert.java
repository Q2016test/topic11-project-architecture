package ru.homework.pages;

import io.qameta.allure.Step;

import java.math.BigDecimal;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;

public final class MainPageAssert {
    private final MainPage page;

    MainPageAssert(MainPage page) {
        this.page = page;
    }

    @Step("Проверить элементы страницы")
    public MainPageAssert loaded() {
        page.adminLink.shouldBe(visible).shouldHave(text(page.config.value("ui.text.admin-link")));
        page.cartButton.shouldBe(visible, enabled).shouldHave(text(page.config.value("ui.text.cart")));
        page.heading.shouldBe(visible).shouldNotBe(empty);
        page.cartCounter.shouldBe(visible).shouldHave(matchText("\\d+"));
        page.notifications.should(exist);
        page.productCards.shouldHave(sizeGreaterThan(0));
        page.quantityInputs.shouldHave(size(page.productCards.size()));
        page.addButtons.shouldHave(size(page.productCards.size()));
        page.productCards.forEach(card -> card.shouldBe(visible));
        page.quantityInputs.forEach(input -> input.shouldBe(visible, enabled).shouldNotBe(empty));
        page.addButtons.forEach(button -> button.shouldBe(visible, enabled).shouldHave(text(page.config.value("ui.text.add-to-cart"))));
        return this;
    }

    @Step("Проверить товар {id}")
    public MainPageAssert product(long id, String name, BigDecimal price) {
        page.productName(id).shouldBe(visible).shouldHave(exactText(name));
        page.productPrice(id).shouldBe(visible)
                .shouldHave(exactText(price.stripTrailingZeros().toPlainString() + " " + page.config.value("ui.text.currency")));
        page.quantityInput(id).shouldBe(visible, enabled).shouldNotBe(empty);
        page.addButton(id).shouldBe(visible, enabled);
        return this;
    }

    @Step("Проверить количество {quantity} товара {id}")
    public MainPageAssert quantity(long id, int quantity) {
        page.quantityInput(id).shouldHave(value(String.valueOf(quantity)));
        return this;
    }

    @Step("Проверить количество товаров в корзине: {count}")
    public MainPageAssert cartCount(int count) {
        page.cartCounter.shouldBe(visible).shouldHave(exactText(String.valueOf(count)));
        return this;
    }

    @Step("Проверить уведомление об обработке заказа")
    public MainPageAssert orderProcessed() {
        page.notifications.shouldBe(visible).shouldHave(text(page.config.value("ui.text.order-processed")));
        page.heading.shouldBe(visible).shouldHave(exactText(page.config.value("ui.text.order-success")));
        return this;
    }
}
