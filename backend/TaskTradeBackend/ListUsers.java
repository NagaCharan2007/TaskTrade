import java.sql.*;
public class ListUsers {
  public static void main(String[] args) throws Exception {
    String url = "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/tasktrade?sslMode=VERIFY_IDENTITY";
    String user = "LEMcJsUUxSbFMaH.root";
    String pass = "m6TaCjLP0bZ9jUkP";
    try (Connection con = DriverManager.getConnection(url, user, pass)) {
      PreparedStatement ps = con.prepareStatement("SELECT id, name, email, password_hash FROM users WHERE LOWER(email) LIKE '%test%' OR LOWER(email) LIKE '%gmail%' OR LOWER(email) LIKE '%gamil%' ORDER BY id");
      ResultSet rs = ps.executeQuery();
      boolean found = false;
      while (rs.next()) {
        found = true;
        System.out.println(rs.getLong("id") + " | " + rs.getString("name") + " | " + rs.getString("email") + " | " + rs.getString("password_hash"));
      }
      if (!found) {
        System.out.println("NO_MATCHES");
      }
    }
  }
}
