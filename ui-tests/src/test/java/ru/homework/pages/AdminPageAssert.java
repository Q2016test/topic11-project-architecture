package ru.homework.pages;

import io.qameta.allure.Step;

import java.math.BigDecimal;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.*;

public final class AdminPageAssert {
    private final AdminPage page;

    AdminPageAssert(AdminPage page) {
        this.page = page;
    }

    @Step("Проверить элементы страницы")
    public AdminPageAssert loaded() {
        page.nameInput.shouldBe(visible, enabled).shouldHave(attribute("type", "text"));
        page.priceInput.shouldBe(visible, enabled).shouldHave(attribute("type", "number"));
        page.createButton.shouldBe(visible, enabled).shouldHave(exactText(page.config.value("ui.text.create")));
        page.storefrontLink.shouldBe(visible).shouldHave(text(page.config.value("ui.text.back")));
        page.rows.shouldHave(sizeGreaterThan(0));
        page.notifications.should(exist);
        return this;
    }

    @Step("Проверить введённые данные нового товара")
    public AdminPageAssert newProductValues(String name, BigDecimal price) {
        page.nameInput.shouldHave(value(name));
        page.priceInput.shouldHave(value(price.stripTrailingZeros().toPlainString()));
        return this;
    }

    @Step("Проверить товар {id}")
    public AdminPageAssert product(long id, String name, BigDecimal price) {
        page.productName(id).shouldBe(visible, enabled).shouldHave(value(name));
        page.productPrice(id).shouldBe(visible, enabled)
                .shouldHave(value(price.stripTrailingZeros().toPlainString()));
        page.saveButton(id).shouldBe(visible, enabled).shouldHave(exactText(page.config.value("ui.text.save")));
        return this;
    }

    @Step("Проверить уведомление о создании товара")
    public AdminPageAssert creationNotified() {
        page.notifications.shouldBe(visible).shouldHave(text(page.config.value("ui.text.product-created")));
        return this;
    }

    @Step("Проверить уведомление об изменении товара {id}")
    public AdminPageAssert updateNotified(long id) {
        page.notifications.shouldBe(visible).shouldHave(text(page.config.value("ui.text.product-updated").formatted(id)));
        return this;
    }
}
