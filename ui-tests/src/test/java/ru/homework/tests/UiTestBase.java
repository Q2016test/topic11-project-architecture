package ru.homework.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import ru.homework.config.TestConfig;
import ru.homework.model.Product;
import ru.homework.pages.MainPage;
import ru.homework.support.BrowserEvidence;
import ru.homework.support.GoodsFixtures;

import static com.codeborne.selenide.Selenide.closeWebDriver;

@Tag("ui")
abstract class UiTestBase {
    protected TestConfig config;
    protected GoodsFixtures fixtures;
    protected Product startingProduct;
    protected MainPage mainPage;

    @BeforeEach
    @Step("Подготовить браузер и стартовый товар")
    void startBrowser() {
        SelenideLogger.addListener("allure", new AllureSelenide()
                .includeSelenideSteps(false).screenshots(true).savePageSource(false));
        config = TestConfig.load();
        config.printPublicValues();
        fixtures = new GoodsFixtures(config);
        startingProduct = fixtures.createUnique(config.data().startingProduct());
        Configuration.baseUrl = config.uiUrl();
        Configuration.browser = config.value("browser.name");
        Configuration.headless = Boolean.parseBoolean(config.value("browser.headless"));
        Configuration.browserSize = config.value("browser.size");
        Configuration.timeout = config.elementTimeoutMs();
        Configuration.reportsFolder = "build/reports/selenide";
        if (!config.optional("browser.binary").isBlank()) {
            Configuration.browserBinary = config.optional("browser.binary");
        }
        mainPage = new MainPage(config).open();
        mainPage.assertions().loaded().product(startingProduct.id(), startingProduct.name(), startingProduct.price());
    }

    @AfterEach
    @Step("Сохранить результат, закрыть браузер и удалить данные")
    void closeBrowserAndDeleteData() {
        try {
            try {
                if (WebDriverRunner.hasWebDriverStarted()) {
                    BrowserEvidence.screenshot();
                }
            } finally {
                closeWebDriver();
                SelenideLogger.removeListener("allure");
            }
        } finally {
            if (fixtures != null) {
                fixtures.cleanup();
            }
        }
    }
}
