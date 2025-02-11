import praktikum.BaseHttpClient;
import praktikum.constants.EndPoints;
import praktikum.pojo.User;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class UserLoginTest extends BaseHttpClient {
    Faker faker = new Faker();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    String name = faker.name().username();
    Response response;

    @Before
    @Step("Создание нового пользователя")
    public void setData() {
        User user = new User(email, password, name);
        response = postRequest(EndPoints.USER_CREATE_POST, user);
    }
    @After
    @Step("Удаление созданного пользователя")
    public  void cleanData() {
        String accessToken;
        int statusCode = response.then().extract().statusCode();
        if (statusCode == 200) {
            accessToken = response.then().extract().path("accessToken").toString();
            deleteRequest(EndPoints.ACTIONS_WITH_USER, accessToken);
        }
    }

    @Test
    @DisplayName("Авторизация существующего пользователя с обязательными полями")
    @Step("Проверка статуса 200 и поля 'success': true")
    public void checkUserLogin() {
        Response loginResponse = postRequest(EndPoints.USER_LOGIN_POST, new User(email, password));
        loginResponse.then().statusCode(200)
                .and()
                .body("success", equalTo(true));

    }

    @Test
    @DisplayName("Авторизация существующего пользователя без ввода почты")
    @Step("Проверка статуса 401 и поля 'success': false")
    public void checkUserLoginWithoutEmail() {
        Response loginResponse = postRequest(EndPoints.USER_LOGIN_POST, new User(null, password));
        loginResponse.then().statusCode(401)
                .and()
                .body("success", equalTo(false));

    }

    @Test
    @DisplayName("Авторизация существующего пользователя без ввода пароля")
    @Step("Проверка статуса 401 и поля 'success': false")
    public void checkUserLoginWithoutPassword() {
        Response loginResponse = postRequest(EndPoints.USER_LOGIN_POST, new User(email, null));
        loginResponse.then().statusCode(401)
                .and()
                .body("success", equalTo(false));

    }
}
