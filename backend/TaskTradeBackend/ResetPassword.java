import java.sql.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class ResetPassword {
  public static void main(String[] args) throws Exception {
    String url = "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/tasktrade?sslMode=VERIFY_IDENTITY";
    String user = "LEMcJsUUxSbFMaH.root";
    String pass = "m6TaCjLP0bZ9jUkP";
    String email = "test@gmail.com";
    String plain = "Charan@2007";
    try (Connection con = DriverManager.getConnection(url, user, pass)) {
      PreparedStatement select = con.prepareStatement("SELECT id, email, password_hash FROM users WHERE LOWER(email) = LOWER(?)");
      select.setString(1, email);
      ResultSet rs = select.executeQuery();
      int found = 0;
      while (rs.next()) {
        found++;
        System.out.println("BEFORE id=" + rs.getLong("id") + " email=" + rs.getString("email") + " old_hash=" + rs.getString("password_hash"));
      }
      if (found == 0) {
        throw new Exception("No existing user found for actual account email: " + email);
      }
      String hash = new BCryptPasswordEncoder().encode(plain);
      PreparedStatement update = con.prepareStatement("UPDATE users SET password_hash = ? WHERE LOWER(email) = LOWER(?)");
      update.setString(1, hash);
      update.setString(2, email);
      int rows = update.executeUpdate();
      System.out.println("UPDATED_ROWS=" + rows);
      PreparedStatement verify = con.prepareStatement("SELECT password_hash FROM users WHERE LOWER(email) = LOWER(?)");
      verify.setString(1, email);
      ResultSet vr = verify.executeQuery();
      if (vr.next()) {
        String storedHash = vr.getString("password_hash");
        boolean matches = new BCryptPasswordEncoder().matches(plain, storedHash);
        System.out.println("MATCHES=" + matches);
      }
    }
  }
}
