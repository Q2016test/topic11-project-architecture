package ru.homework.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.homework.config.TestData;
import ru.homework.model.Product;
import ru.homework.pages.AdminPage;
import ru.homework.pages.CartPage;
import ru.homework.pages.LoginPage;

import java.math.BigDecimal;
import java.util.List;

@Epic("SmartShop")
@Feature("UI: покупки и администрирование")
class ShoppingTest extends UiTestBase {
    @Step("Авторизоваться в админке")
    private AdminPage loginAsAdmin() {
        LoginPage loginPage = mainPage.openAdmin();
        loginPage.assertions().loaded();
        loginPage.enterLogin(config.admin()).enterPassword(config.admin());
        loginPage.assertions().fieldsContainText();
        AdminPage adminPage = loginPage.submit();
        adminPage.assertions().loaded();
        return adminPage;
    }

    @Test
    @Tag("smoke")
    @DisplayName("3.1 Оплата трёх единиц товара")
    void purchaseThreeUnits() {
        int quantity = config.data().ui().purchaseQuantity();
        mainPage.setQuantity(startingProduct.id(), quantity).addToCart(startingProduct.id());
        mainPage.assertions().quantity(startingProduct.id(), quantity).cartCount(quantity);
        CartPage cartPage = mainPage.openCart();
        BigDecimal expected = startingProduct.price().multiply(BigDecimal.valueOf(quantity));
        cartPage.assertions().loaded().itemCount(1)
                .product(startingProduct.id(), startingProduct.name(), quantity)
                .total(expected).totalAtMost(config.data().ui().orderLimit());
        mainPage = cartPage.checkout();
        mainPage.assertions().orderProcessed().cartCount(config.integer("ui.empty-cart.count"));
        cartPage.assertions().closed();
    }

    @Test
    @DisplayName("3.2 Общая стоимость разных товаров")
    void totalOfDifferentProductsIsCorrect() {
        TestData.UiData data = config.data().ui();
        List<Product> extra = data.cartProducts().stream()
                .map(entry -> fixtures.createUnique(entry.product())).toList();
        mainPage.refresh();
        mainPage.assertions().loaded();
        mainPage.setQuantity(startingProduct.id(), data.startingCartQuantity()).addToCart(startingProduct.id());
        BigDecimal expected = startingProduct.price().multiply(BigDecimal.valueOf(data.startingCartQuantity()));
        int totalQuantity = data.startingCartQuantity();
        for (int i = 0; i < extra.size(); i++) {
            Product product = extra.get(i);
            int quantity = data.cartProducts().get(i).quantity();
            mainPage.assertions().product(product.id(), product.name(), product.price());
            mainPage.setQuantity(product.id(), quantity).addToCart(product.id());
            expected = expected.add(product.price().multiply(BigDecimal.valueOf(quantity)));
            totalQuantity += quantity;
        }
        mainPage.assertions().cartCount(totalQuantity);
        CartPage cartPage = mainPage.openCart();
        cartPage.assertions().loaded().itemCount(extra.size() + 1)
                .product(startingProduct.id(), startingProduct.name(), data.startingCartQuantity()).total(expected);
        for (int i = 0; i < extra.size(); i++) {
            Product product = extra.get(i);
            cartPage.assertions().product(product.id(), product.name(), data.cartProducts().get(i).quantity());
        }
    }

    @Test
    @DisplayName("3.3 Уведомление после добавления товара")
    void adminCreationShowsNotification() {
        TestData.ProductData data = config.data().ui().createdProduct();
        String name = fixtures.reserveName(data.name());
        AdminPage adminPage = loginAsAdmin();
        adminPage.fillNewProduct(name, data.price());
        adminPage.assertions().newProductValues(name, data.price());
        adminPage.createProduct();
        adminPage.assertions().creationNotified();
        Product created = fixtures.findByName(name);
        adminPage.assertions().product(created.id(), name, data.price());
    }

    @Test
    @Tag("smoke")
    @DisplayName("3.4 Редактирование товара в админке")
    void editedProductAppearsInStorefront() {
        TestData.ProductData data = config.data().ui().editedProduct();
        String name = fixtures.reserveName(data.name());
        AdminPage adminPage = loginAsAdmin();
        adminPage.editProduct(startingProduct.id(), name, data.price());
        adminPage.assertions().product(startingProduct.id(), name, data.price());
        adminPage.saveProduct(startingProduct.id());
        adminPage.assertions().updateNotified(startingProduct.id());
        mainPage = adminPage.backToStorefront();
        mainPage.assertions().loaded().product(startingProduct.id(), name, data.price());
    }
}
