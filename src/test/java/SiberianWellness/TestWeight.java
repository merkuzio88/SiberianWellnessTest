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
    Integer total;
    Integer curProductPage = 1;
    @Test(priority = 1)
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

    @Test(priority = 2)
    @Description("Checking that the weight of first 500 products in stock in Almaty is not equal to 0")
    @Severity(SeverityLevel.NORMAL)
    public void CheckProducts() {
        Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec());
        Response response = given()
                .when()
                .get("product?CurrentPage=1&PerPage=500&RegionId=22&LanguageId=9&CityId=" + cityId)
                .then()
                .assertThat()
                .header("Content-Type", "application/json; charset=UTF-8")
                .extract().response();
        JsonPath jsonPath = response.jsonPath();
        total = jsonPath.getInt("Total");
        List<Integer> weights = new ArrayList<>(jsonPath.getList("List.findAll{it.ProductSaldo.Volume != 0}.collect{it.Weight.toInteger()}"));
        Assertions.assertFalse(weights.contains(0), "В списке присутствует продукт с нулевым весом!");
        System.out.println("На странице " + curProductPage + " количество продуктов в наличии: " + weights.size());
    }


    @Test(priority = 3)
    @Description("Checking that the weight of all another products in stock in Almaty is not equal to 0")
    @Severity(SeverityLevel.NORMAL)
    public void CheckAnotherProducts() {
        total -= 500;
        curProductPage += 1;
        while (total > 0) {
            Specifications.installSpecification(Specifications.requestSpec(URL), Specifications.responseSpec());
            Response response = given()
                    .when()
                    .get("product?CurrentPage=" + curProductPage.toString() + "&PerPage=500&RegionId=22&LanguageId=9&CityId=" + cityId)
                    .then()
                    .assertThat()
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .extract().response();
            JsonPath jsonPath = response.jsonPath();
            List<Integer> weights = new ArrayList<>(jsonPath.getList("List.findAll{it.ProductSaldo.Volume != 0}.collect{it.Weight.toInteger()}"));
            Assertions.assertFalse(weights.contains(0), "В списке присутствует продукт с нулевым весом!");
            System.out.println("На странице " + curProductPage + " количество продуктов в наличии: " + weights.size());
            curProductPage += 1;
            total -= 500;
        }
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