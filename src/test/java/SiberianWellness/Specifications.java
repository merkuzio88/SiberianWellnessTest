package SiberianWellness;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.lessThan;

public class Specifications {
    public static RequestSpecification requestSpec(String url){
        return new RequestSpecBuilder()
                .setBaseUri(url)
                .addHeader("token", "fd49d3cd-789c-4d00-912d-3382ca4f3968")
                .build();
    }

    public static ResponseSpecification responseSpec(){
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectHeader("Content-Encoding", "gzip")
                .expectHeader("Connection", "keep-alive")
                .expectResponseTime(lessThan(5000L))
                .build();
    }

    public static void installSpecification(RequestSpecification request, ResponseSpecification response){
        RestAssured.requestSpecification = request;
        RestAssured.responseSpecification = response;
    }
}
