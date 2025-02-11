import praktikum.BaseHttpClient;
import praktikum.constants.EndPoints;
import praktikum.pojo.User;
import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;

public class UserCreateTest extends BaseHttpClient {
    Faker faker = new Faker();
    String email = faker.internet().emailAddress();
    String password = faker.internet().password();
    String name = faker.name().username();
    Response response;

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
    @DisplayName("Создание уникального пользователя")
    @Step("Проверка статуса 200 и поля 'success': true")
    public void checkUserRegistration() {
        User user = new User(email, password, name);
        response = postRequest(EndPoints.USER_CREATE_POST, user);
        response.then().statusCode(200)
                .and()
                .body("success", equalTo(true));

    }

    @Test
    @DisplayName("Повторное создание уже существующего пользователя")
    @Step("Проверка статуса 403 и поля 'success': false")
    public void checkUserRegistrationAlreadyRegistered() {
        User user = new User(email, password, name);
        response = postRequest(EndPoints.USER_CREATE_POST, user);

        Response responseSecond = postRequest(EndPoints.USER_CREATE_POST, user);
        responseSecond.then().statusCode(403)
                .and()
                .body("success", equalTo(false));

    }

    @Test
    @DisplayName("Создание уникального пользователя без поля email")
    @Step("Проверка статуса 403 и поля 'success': false")
    public void checkUserRegistrationWithoutEmail() {
        User user = new User(null, password, name);
        response = postRequest(EndPoints.USER_CREATE_POST, user);
        response.then().statusCode(403)
                .and()
                .body("success", equalTo(false));

    }

    @Test
    @DisplayName("Создание уникального пользователя без поля password")
    @Step("Проверка статуса 403 и поля 'success': false")
    public void checkUserRegistrationWithoutPassword() {
        User user = new User(email, null, name);
        response = postRequest(EndPoints.USER_CREATE_POST, user);
        response.then().statusCode(403)
                .and()
                .body("success", equalTo(false));

    }

    @Test
    @DisplayName("Создание уникального пользователя без поля name")
    @Step("Проверка статуса 403 и поля 'success': false")
    public void checkUserRegistrationWithoutName() {
        User user = new User(email, password, null);
        response = postRequest(EndPoints.USER_CREATE_POST, user);
        response.then().statusCode(403)
                .and()
                .body("success", equalTo(false));

    }
}
