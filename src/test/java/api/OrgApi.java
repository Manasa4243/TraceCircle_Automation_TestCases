package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class OrgApi {

    // ================= SIGNUP / ONBOARD =================
    public Response createSuperAdmin(String orgName, String email, String location) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("orgName", orgName);
        payload.put("emailId", email);
        payload.put("location", location);
        payload.put("role", "SUPER_ADMIN");

        Response response = RestAssured
                .given()
                .baseUri("http://localhost:8080")
                .basePath("/api/v1/auth/super-admin")
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post();

        System.out.println("SIGNUP STATUS: " + response.getStatusCode());
        System.out.println("SIGNUP RESPONSE: " + response.getBody().asString());

        return response;
    }


    // ================= LOGIN =================
    public Response login(String email, String password) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("emailId", email);
        payload.put("password", password);

        Response response = RestAssured
                .given()
                .baseUri("http://localhost:8080")
                .basePath("/api/v1/auth/login")
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post();

        System.out.println("LOGIN STATUS: " + response.getStatusCode());
        System.out.println("LOGIN RESPONSE: " + response.getBody().asString());

        return response;
    }
}