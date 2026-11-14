package ru.homework.pages;

import io.qameta.allure.Step;
import ru.homework.config.TestConfig;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import java.math.BigDecimal;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public final class AdminPage extends BasePage {
    public AdminPage(TestConfig config) {
        super(config);
    }

    final SelenideElement nameInput = $("#n-name");
    final SelenideElement priceInput = $("#n-price");
    final SelenideElement createButton = $("#add-btn");
    final ElementsCollection rows = $$("#tbody tr");
    final SelenideElement storefrontLink = $("a[href='" + config.value("ui.path.storefront") + "']");
    final SelenideElement notifications = $("#toast-container");

    @Step("Заполнить новый товар {name} стоимостью {price}")
    public AdminPage fillNewProduct(String name, BigDecimal price) {
        nameInput.setValue(name);
        priceInput.setValue(price.stripTrailingZeros().toPlainString());
        return this;
    }

    @Step("Создать товар через админку")
    public AdminPage createProduct() {
        createButton.click();
        return this;
    }

    @Step("Изменить поля товара {id}")
    public AdminPage editProduct(long id, String name, BigDecimal price) {
        productName(id).setValue(name);
        productPrice(id).setValue(price.stripTrailingZeros().toPlainString());
        return this;
    }

    @Step("Сохранить товар {id}")
    public AdminPage saveProduct(long id) {
        saveButton(id).click();
        return this;
    }

    @Step("Вернуться на витрину")
    public MainPage backToStorefront() {
        storefrontLink.click();
        return new MainPage(config);
    }

    @Step("Прочитать уведомление")
    public String notificationText() {
        return notifications.text();
    }

    SelenideElement productName(long id) {
        return $("#nm-" + id);
    }

    SelenideElement productPrice(long id) {
        return $("#pr-" + id);
    }

    SelenideElement saveButton(long id) {
        return $("#tbody button[data-action='update'][data-id='" + id + "']");
    }

    public AdminPageAssert assertions() {
        return new AdminPageAssert(this);
    }
}
