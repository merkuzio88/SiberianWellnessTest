package SiberianWellness;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class TestWeight {
    private final static String URL = "https://kz.siberianwellness.com/api/v1/";

    @BeforeMethod

    public void setFilter() {

        RestAssured.filters(new AllureRestAssured());
    }

    String cityId;

    @Test
    @Description("Getting the ID of the city of Almaty")
    @Severity(SeverityLevel.NORMAL)
    public void CheckCities() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec());
        Response response = given()
                .when()
                .get("city?RegionId=22&PerPage=30&CurrentPage=1")
                .then()
                .assertThat()
                .header("Content-Type", "application/json; charset=UTF-8")
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        cityId = jsonPath.getString("List.find{it.Name == 'Алматы'}.Id");
    }

    @Test
    @Description("Checking that the weight of products in stock in Almaty is not equal to 0")
    @Severity(SeverityLevel.NORMAL)
    public void CheckProducts() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec());
        Response response = given()
                .when()
                .get("product?CurrentPage=1&PerPage=11&RegionId=22&LanguageId=9&CityId=" + cityId)
                .then()
                .assertThat()
                .header("Content-Type", "application/json; charset=UTF-8")
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        List<Integer> weights = new ArrayList<>(jsonPath.getList("List.findAll{it.ProductSaldo.Volume != 0}.collect{it.Weight.toInteger()}"));
        Assertions.assertFalse(weights.contains(0));
    }

    @AfterMethod
    public void getTestExecutionTime (ITestResult result) {
        String methodName = result.getMethod ()
                .getMethodName ();
        long totalExecutionTime = (result.getEndMillis () - result.getStartMillis ());
        System.out.println (
                "Total Execution time: " + totalExecutionTime + " milliseconds" + " for method " + methodName);
    }

}