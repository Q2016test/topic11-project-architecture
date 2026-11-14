package ru.homework.pages;

import ru.homework.config.TestConfig;

abstract class BasePage {
    protected final TestConfig config;

    protected BasePage(TestConfig config) {
        this.config = config;
    }
}
