package fun.cyhgraph.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserDishIntegrationTest {

    // 👈 彻底放弃 @Test，直接用标准的 main 方法
    public static void main(String[] args) {
        try {
            // 1. 本地后端接口地址（对准你的 8081 端口）
            URL url = new URL("http://localhost:8081/user/dish/list/1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 2. 设置请求方法和请求头
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token", "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3ODA3Mjc4NTIsInVzZXJJZCI6MX0.ZoD8_DXl52RlUBoVGrozK7YWXDzfh8JHLKbEGiuK2lI");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            // 3. 获取响应状态码
            int responseCode = conn.getResponseCode();
            System.out.println("==========================================");
            System.out.println("Integration Test HTTP Response Code: " + responseCode);

            // 4. 读取返回数据
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println("✔ [Success] 轻量级集成测试运行成功！");
                System.out.println("接口返回业务数据: " + response.toString());
                System.out.println("==========================================");
            } else {
                System.out.println("❌ [Error] 接口返回错误码: " + responseCode);
            }

        } catch (Exception e) {
            System.out.println("测试捕获异常: " + e.getMessage());
        }
    }
}