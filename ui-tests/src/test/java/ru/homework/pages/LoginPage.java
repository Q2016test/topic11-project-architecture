package ru.homework.pages;

import io.qameta.allure.Step;
import ru.homework.config.TestConfig;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public final class LoginPage extends BasePage {
    public LoginPage(TestConfig config) {
        super(config);
    }

    final SelenideElement loginInput = $("#username");
    final SelenideElement passwordInput = $("#password");
    final SelenideElement submitButton = $("button[type='submit']");

    @Step("Заполнить логин")
    public LoginPage enterLogin(TestConfig.Credentials credentials) {
        loginInput.setValue(credentials.login());
        return this;
    }

    @Step("Заполнить пароль")
    public LoginPage enterPassword(TestConfig.Credentials credentials) {
        passwordInput.setValue(credentials.password());
        return this;
    }

    @Step("Войти в админку")
    public AdminPage submit() {
        submitButton.click();
        return new AdminPage(config);
    }

    public LoginPageAssert assertions() {
        return new LoginPageAssert(this);
    }
}
