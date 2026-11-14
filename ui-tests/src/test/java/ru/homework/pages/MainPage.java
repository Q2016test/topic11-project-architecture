package ru.homework.pages;

import io.qameta.allure.Step;
import ru.homework.config.TestConfig;

import com.codeborne.selenide.DragAndDropOptions;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public final class MainPage extends BasePage {
    public MainPage(TestConfig config) {
        super(config);
    }

    final SelenideElement adminLink = $("a[href='" + config.value("ui.path.admin") + "']");
    final SelenideElement cartButton = $("#open-cart-btn");
    final ElementsCollection productCards = $$("#products-list .product-card");
    final ElementsCollection quantityInputs = $$("#products-list .qty-input");
    final ElementsCollection addButtons = $$("#products-list [data-action='add-to-cart']");
    final SelenideElement heading = $("#main-title");
    final SelenideElement notifications = $("#toast-container");
    final SelenideElement cartCounter = $("#cart-count");

    @Step("Открыть витрину")
    public MainPage open() {
        Selenide.open(config.value("ui.path.storefront"));
        return this;
    }

    @Step("Обновить витрину")
    public MainPage refresh() {
        Selenide.refresh();
        return this;
    }

    @Step("Перейти к авторизации администратора")
    public LoginPage openAdmin() {
        adminLink.click();
        return new LoginPage(config);
    }

    @Step("Открыть корзину")
    public CartPage openCart() {
        cartButton.click();
        return new CartPage(config);
    }

    @Step("Установить количество {quantity} для товара {id}")
    public MainPage setQuantity(long id, int quantity) {
        quantityInput(id).setValue(String.valueOf(quantity));
        return this;
    }

    @Step("Добавить товар {id} в корзину")
    public MainPage addToCart(long id) {
        addButton(id).click();
        return this;
    }

    @Step("Перетащить товар {id} в корзину")
    public MainPage dragToCart(long id) {
        productName(id).dragAndDrop(DragAndDropOptions.to("#open-cart-btn").usingSeleniumActions());
        return this;
    }

    @Step("Прочитать заголовок витрины")
    public String headingText() {
        return heading.text();
    }

    @Step("Прочитать уведомление")
    public String notificationText() {
        return notifications.text();
    }

    @Step("Прочитать количество товаров в корзине")
    public int cartCount() {
        return Integer.parseInt(cartCounter.text());
    }

    SelenideElement productCard(long id) {
        return productCards.findBy(attribute("data-id", String.valueOf(id)));
    }

    SelenideElement productName(long id) {
        return productCard(id).$("h4");
    }

    SelenideElement productPrice(long id) {
        return productCard(id).$("h4 + div");
    }

    SelenideElement quantityInput(long id) {
        return quantityInputs.findBy(attribute("id", "q-" + id));
    }

    SelenideElement addButton(long id) {
        return addButtons.findBy(attribute("data-id", String.valueOf(id)));
    }

    public MainPageAssert assertions() {
        return new MainPageAssert(this);
    }
}
