package db;

import java.sql.*;
import org.testng.Assert;

public class DBUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/tracecircle_db";
    private static final String USER = "root";
    private static final String PASS = "root@1234";

    private boolean dbChecked = false;

    // ✅ COMMON CONNECTION METHOD
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ================= USER VALIDATION =================
    public boolean isUserPresent(String email) throws Exception {

        dbChecked = true;

        email = email.trim().toLowerCase();

        boolean existsInSignup = false;
        boolean existsInOnboarding = false;

        try (Connection con = getConnection()) {

            // ✅ tc_signup
            String signupQuery = "SELECT 1 FROM tc_signup WHERE LOWER(email_id) = ?";
            try (PreparedStatement ps = con.prepareStatement(signupQuery)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                existsInSignup = rs.next();
            }

            // onboarding table
            String onboardingQuery = "SELECT 1 FROM tc_super_admin_onboarding WHERE LOWER(email_id) = ?";
            try (PreparedStatement ps = con.prepareStatement(onboardingQuery)) {
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                existsInOnboarding = rs.next();
            }
        }

        boolean finalResult = existsInSignup || existsInOnboarding;

        System.out.println(" Checking DB for: " + email);
        System.out.println(" tc_signup: " + existsInSignup);
        System.out.println(" onboarding: " + existsInOnboarding);
        System.out.println(" FINAL RESULT: " + finalResult);

        return finalResult;
    }

    // ================= GTIN VALIDATION =================
    public boolean isGTINPresent(String gtin) throws Exception {

        dbChecked = true;

        boolean exists = false;

        try (Connection con = getConnection()) {

            String query = "SELECT 1 FROM tc_gtin_documents WHERE gtin = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, gtin);

                ResultSet rs = ps.executeQuery();
                exists = rs.next();
            }
        }

        System.out.println(" Checking GTIN in DB: " + gtin);
        System.out.println(" GTIN EXISTS: " + exists);

        return exists;
    }
    public boolean isPlantPresent(String plantName) throws Exception {

    dbChecked = true;

    boolean exists = false;

    try (Connection con = getConnection()) {

        String query = "SELECT 1 FROM tc_plants WHERE LOWER(plant_name) = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, plantName.trim().toLowerCase());

            ResultSet rs = ps.executeQuery();
            exists = rs.next();
        }
    }

    System.out.println("Checking plant in DB: " + plantName);
    System.out.println("Plant exists: " + exists);

    return exists;
}

public int getPlantCount(String plantName) throws Exception {

    dbChecked = true;

    int count = 0;

    try (Connection con = getConnection()) {

        String query = "SELECT COUNT(*) FROM tc_plants WHERE LOWER(plant_name) = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, plantName.trim().toLowerCase());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                count = rs.getInt(1);
            }
        }
    }

    System.out.println("Plant count for " + plantName + ": " + count);

    return count;
}

    // ================= ASSERT =================
    public void assertDbChecked() {
        Assert.assertTrue(dbChecked, " DB was NOT checked in test!");
    }
}