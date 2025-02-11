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

public class UserUpdateTest extends BaseHttpClient {
    Faker faker = new Faker();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    String name = faker.name().username();
    String emailPatch = faker.internet().emailAddress();
    String passwordPatch = faker.internet().password();
    String namePatch= faker.name().username();
    String accessToken;
    Response response;

    @Before
    @Step("Создание нового пользователя")
    public void setData() {
        User user = new User(email, password, name);
        response = postRequest(EndPoints.USER_CREATE_POST, user);
        accessToken = response.then().extract().path("accessToken").toString();
    }
    @After
    @Step("Удаление созданного пользователя")
    public  void cleanData() {
        int statusCode = response.then().extract().statusCode();
        if (statusCode == 200) {
            deleteRequest(EndPoints.ACTIONS_WITH_USER, accessToken);
        }
    }

    @Test
    @DisplayName("Изменение данных пользователя с авторизацией")
    @Step("Проверка статуса 200 и поля 'success': true")
    public void checkUpdateDataWhenUserIsAuthorized() {
        postRequest(EndPoints.USER_LOGIN_POST, new User(email, password));
        Response patchResponse = patchRequest(EndPoints.ACTIONS_WITH_USER,new User(emailPatch, passwordPatch, namePatch) ,accessToken);
        patchResponse.then().statusCode(200)
                .and()
                .body("user.email", equalTo(emailPatch))
                .body("user.name", equalTo(namePatch));

    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Step("Проверка статуса 401 и поля 'success': false")
    public void checkUpdateDataWhenUserIsUnauthorized() {
        Response patchResponse = patchRequest(EndPoints.ACTIONS_WITH_USER,new User(emailPatch, passwordPatch, namePatch) ,accessToken);
        patchResponse.then().statusCode(401)
                .and()
                .body("success", equalTo(false));

    }
}
