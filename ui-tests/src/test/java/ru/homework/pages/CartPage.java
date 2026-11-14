package ru.homework.pages;

import io.qameta.allure.Step;
import ru.homework.config.TestConfig;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.math.BigDecimal;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public final class CartPage extends BasePage {
    public CartPage(TestConfig config) {
        super(config);
    }

    final SelenideElement modal = $("#cartModal");
    final ElementsCollection items = $$("#cart-items .cart-item");
    final SelenideElement closeButton = $("#close-modal");
    final SelenideElement checkoutButton = $("#makeOrder");
    final SelenideElement total = $("#total-price");

    @Step("Оплатить заказ")
    public MainPage checkout() {
        checkoutButton.click();
        return new MainPage(config);
    }

    @Step("Закрыть корзину")
    public MainPage close() {
        closeButton.click();
        return new MainPage(config);
    }

    @Step("Удалить товар {id} из корзины")
    public CartPage remove(long id) {
        removeButton(id).click();
        return this;
    }

    @Step("Прочитать общую стоимость")
    public BigDecimal totalPrice() {
        return new BigDecimal(total.text());
    }

    SelenideElement item(long id) {
        return items.findBy(attribute("id", "cart-item-" + id));
    }

    SelenideElement productName(long id) {
        return item(id).$("b");
    }

    SelenideElement quantity(long id) {
        return item(id).$(".qty-controls span");
    }

    SelenideElement removeButton(long id) {
        return item(id).$("[data-action='remove']");
    }

    public CartPageAssert assertions() {
        return new CartPageAssert(this);
    }
}
