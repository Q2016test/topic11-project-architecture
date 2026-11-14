package ru.homework.support;

import com.codeborne.selenide.Selenide;
import io.qameta.allure.Attachment;
import io.qameta.allure.Step;
import org.openqa.selenium.OutputType;

public final class BrowserEvidence {
    private BrowserEvidence() {
    }

    @Step("Сохранить скриншот после теста")
    @Attachment(value = "Страница после теста", type = "image/png", fileExtension = ".png")
    public static byte[] screenshot() {
        return Selenide.screenshot(OutputType.BYTES);
    }
}
