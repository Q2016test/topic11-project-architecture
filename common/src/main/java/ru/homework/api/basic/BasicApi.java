package ru.homework.api.basic;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import ru.homework.api.RestApiBuilder;
import ru.homework.config.TestConfig;

import static io.restassured.RestAssured.given;

public abstract class BasicApi {
    protected final TestConfig config;
    protected final RestApiBuilder builder;

    protected BasicApi(TestConfig config) {
        this.config = config;
        builder = new RestApiBuilder(config);
    }

    @Step("{method} {path}: запрос без авторизации")
    public Response anonymousRequest(String method, String path, Object body) {
        RequestSpecification request = given().spec(builder.anonymous());
        if (body != null) {
            request.body(body);
        }
        return request.when().request(method, path);
    }
}
