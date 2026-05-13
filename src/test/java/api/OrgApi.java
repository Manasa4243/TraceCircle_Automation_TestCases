package api;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrgApi {

    private final String BASE_URI = "http://localhost:8080";

    // ================= SIGNUP / ONBOARD =================
    public Response createSuperAdmin(String orgName, String email, String location) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("orgName", orgName);
        payload.put("emailId", email);
        payload.put("location", location);
        payload.put("role", "SUPER_ADMIN");

        Response response = RestAssured
                .given()
                .baseUri(BASE_URI)
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
                .baseUri(BASE_URI)
                .basePath("/api/v1/auth/login")
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post();

        System.out.println("LOGIN STATUS: " + response.getStatusCode());
        System.out.println("LOGIN RESPONSE: " + response.getBody().asString());

        return response;
    }

    // ================= FORGOT PASSWORD =================
    public Response sendForgotPasswordOtp(String email) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("emailId", email);

        Response response = RestAssured
                .given()
                .baseUri(BASE_URI)
                .basePath("/api/v1/auth/password/send-otp")
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post();

        System.out.println("FORGOT PASSWORD STATUS: " + response.getStatusCode());
        System.out.println("FORGOT PASSWORD RESPONSE: " + response.getBody().asString());

        return response;
    }

    // ================= 🔥 NEW: EXTRACT OTP =================
    public String extractOtpFromResponse(Response response) {

        String otp = null;

        try {
            // ✅ Case 1: OTP directly in JSON (best case)
            otp = response.jsonPath().getString("otp");

            // ✅ Case 2: OTP inside message (e.g., "Your OTP is 123456")
            if (otp == null) {
                String message = response.jsonPath().getString("message");

                if (message != null) {
                    Pattern pattern = Pattern.compile("\\d{4,6}");
                    Matcher matcher = pattern.matcher(message);

                    if (matcher.find()) {
                        otp = matcher.group();
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error extracting OTP");
        }

        System.out.println("📌 Captured OTP: " + otp);

        return otp;
    }

    // ================= 🔥 NEW: GET OTP DIRECTLY =================
    public String getOtp(String email) {

        Response response = sendForgotPasswordOtp(email);
        return extractOtpFromResponse(response);
    }
    // ================= GTIN FETCH =================

public Response getGTIN(String gtin) {

    Response response = RestAssured
            .given()
            .baseUri(BASE_URI)
            .basePath("/api/v1/gtin-documents/" + gtin) // ✅ dynamic
            .when()
            .get();

    System.out.println("GTIN API STATUS: " + response.getStatusCode());
    System.out.println("GTIN API RESPONSE: " + response.getBody().asString());

    return response;
}
public Response createPlant(
        String token,
        String plantName,
        String location,
        String country,
        int orgId
) {

    Map<String, Object> payload = new HashMap<>();

    payload.put("plantName", plantName);
    payload.put("plantLocation", location);
    payload.put("country", country);
    payload.put("organizationId", orgId);

    Response response = RestAssured
            .given()
            .baseUri(BASE_URI)
            .basePath("/api/v1/plants")
            .header("Authorization", "Bearer " + token)
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post();

    System.out.println("PLANT API STATUS: " + response.getStatusCode());
    System.out.println("PLANT API RESPONSE: " + response.getBody().asString());

    return response;
}
public String getAuthToken(String email, String password) {

    Map<String, Object> payload = new HashMap<>();
    payload.put("emailId", email);
    payload.put("password", password);

    Response response = RestAssured
            .given()
            .baseUri(BASE_URI)
            .basePath("/api/v1/auth/login")
            .contentType(ContentType.JSON)
            .body(payload)
            .when()
            .post();

    System.out.println("LOGIN RESPONSE: " + response.getBody().asString());

    String token = null;

    try {

        token = response.jsonPath().getString("token");

        // if backend returns jwt instead of token
        if (token == null) {
            token = response.jsonPath().getString("jwt");
        }

        // sometimes inside data.token
        if (token == null) {
            token = response.jsonPath().getString("data.token");
        }

    } catch (Exception e) {
        System.out.println("Unable to fetch token");
    }

    System.out.println("TOKEN: " + token);

    return token;
}
}