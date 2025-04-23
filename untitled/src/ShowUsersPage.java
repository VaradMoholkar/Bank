import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;
import javafx.scene.control.ScrollPane;


public class ShowUsersPage {

    public static Scene getScene(Stage primaryStage, String adminRollno) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f8ff, #e6f0ff);");

        Label title = new Label("👥 Registered Users");
        title.setFont(new Font("Segoe UI Semibold", 26));
        title.setTextFill(Color.web("#2C3E50"));
        title.setEffect(new DropShadow(2.0, Color.LIGHTGRAY));

        VBox userBox = new VBox(15);
        userBox.setAlignment(Pos.TOP_LEFT);
        userBox.setPadding(new Insets(10));
        userBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #3498db; -fx-border-radius: 10;");
        ScrollPane scrollPane = new ScrollPane(userBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        try {
            Connection conn = DBConnection.getConnection();
            String selectSql = "SELECT * FROM user_table";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectSql);

            while (rs.next()) {
                String userData = "👤 Name: " + rs.getString("user_name") +
                        " | 🆔 Roll No: " + rs.getString("user_rollno") +
                        " | 📞 Phone: " + rs.getLong("user_phone");

                Label userLabel = new Label(userData);
                userLabel.setFont(new Font("Segoe UI", 14));
                userLabel.setTextFill(Color.web("#34495e"));
                userBox.getChildren().add(userLabel);
            }

            stmt.close();
            rs.close();
            conn.close();

        } catch (Exception e) {
            Label errorLabel = new Label("⚠ Error loading user data.");
            errorLabel.setTextFill(Color.RED);
            userBox.getChildren().add(errorLabel);
            e.printStackTrace();
        }

        Button backBtn = new Button("🔙 Back");
        backBtn.setFont(new Font("Segoe UI", 16));
        backBtn.setPrefWidth(200);
        backBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8px;");
        backBtn.setOnMouseEntered(e -> backBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 8px;"));
        backBtn.setOnMouseExited(e -> backBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8px;"));

        backBtn.setOnAction(e -> {
            new Admin(adminRollno).start(primaryStage);
        });

        root.getChildren().addAll(title, scrollPane, backBtn);
        return new Scene(root, 600, 500);
    }
}
