import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
public class LoginCheck {
  public static void main(String[] args) throws Exception {
    String payload = "{\"email\":\"test@gmail.com\",\"password\":\"Charan@2007\"}";
    HttpURLConnection conn = (HttpURLConnection) new URL("http://localhost:8080/api/auth/login").openConnection();
    conn.setRequestMethod("POST");
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setConnectTimeout(15000);
    conn.setReadTimeout(15000);
    try (OutputStream os = conn.getOutputStream()) {
      os.write(payload.getBytes(StandardCharsets.UTF_8));
    }
    int status = conn.getResponseCode();
    System.out.println("STATUS=" + status);
    InputStream is = (status >= 200 && status < 300) ? conn.getInputStream() : conn.getErrorStream();
    if (is == null) { System.out.println("NO_RESPONSE_BODY"); return; }
    String body;
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) sb.append(line);
      body = sb.toString();
    }
    System.out.println(body);
  }
}
