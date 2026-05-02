package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.testng.Assert;

public class DBUtil {

    // 🔥 Track if DB was actually checked
    private boolean dbChecked = false;

    public boolean isUserPresent(String email) throws Exception {

        dbChecked = true; // ✅ mark DB check happened

        Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/tracecircle_db",
                "root",
                "root@1234"
        );

        Statement stmt = con.createStatement();

        String query = "SELECT * FROM tc_super_admin_onboarding WHERE email_id = '" + email + "'";

        ResultSet rs = stmt.executeQuery(query);

        boolean exists = rs.next();

        System.out.println("DB Result for " + email + " : " + exists);

        // 🔥 Close connections (important)
        rs.close();
        stmt.close();
        con.close();

        return exists;
    }

    // 🔥 This will FAIL test if DB was not checked
    public void assertDbChecked() {
        Assert.assertTrue(dbChecked, "❌ DB was NOT checked in test!");
    }
}