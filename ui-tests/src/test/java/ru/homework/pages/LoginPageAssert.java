package ru.homework.pages;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;

public final class LoginPageAssert {
    private final LoginPage page;

    LoginPageAssert(LoginPage page) {
        this.page = page;
    }

    @Step("Проверить элементы страницы")
    public LoginPageAssert loaded() {
        page.loginInput.shouldBe(visible, enabled).shouldHave(attribute("type", "text"));
        page.passwordInput.shouldBe(visible, enabled).shouldHave(attribute("type", "password"));
        page.submitButton.shouldBe(visible, enabled).shouldHave(text(page.config.value("ui.text.sign-in")));
        return this;
    }

    @Step("Проверить заполнение логина и пароля")
    public LoginPageAssert fieldsContainText() {
        page.loginInput.shouldNotBe(empty);
        page.passwordInput.shouldNotBe(empty);
        return this;
    }
}
