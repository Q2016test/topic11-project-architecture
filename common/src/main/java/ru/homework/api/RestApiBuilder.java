package ru.homework.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import ru.homework.config.TestConfig;

import static io.restassured.RestAssured.preemptive;

public final class RestApiBuilder {
    private final TestConfig config;

    public RestApiBuilder(TestConfig config) {
        this.config = config;
    }

    public RequestSpecification anonymous() {
        RequestSpecBuilder builder = new RequestSpecBuilder().setBaseUri(config.apiUrl())
                .setContentType(ContentType.JSON)
                .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", config.integer("http.connect.timeout.ms"))
                        .setParam("http.socket.timeout", config.integer("http.read.timeout.ms"))))
                .addFilter(new AllureRestAssured().setRequestTemplate("safe-request.ftl"));
        if (config.loggingMode() == TestConfig.LoggingMode.DEBUG) {
            builder.addFilter(new RequestLoggingFilter(LogDetail.URI));
        }
        if (config.loggingMode() != TestConfig.LoggingMode.OFF) {
            builder.addFilter(new ResponseLoggingFilter(LogDetail.STATUS));
        }
        return builder.build();
    }

    public RequestSpecification authorized() {
        return new RequestSpecBuilder().addRequestSpecification(anonymous())
                .setAuth(preemptive().basic(config.admin().login(), config.admin().password())).build();
    }

    public RequestSpecification wrongPassword() {
        return new RequestSpecBuilder().addRequestSpecification(anonymous())
                .setAuth(preemptive().basic(config.admin().login(),
                        config.admin().password() + config.value("api.auth.wrong-password-suffix"))).build();
    }
}
