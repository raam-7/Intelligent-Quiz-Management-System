import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quiz_system",
                "root",
                ""
            );

            System.out.println("Connected Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}