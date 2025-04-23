import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;

public class ShowAdminsPage {

    public static Scene getScene(Stage primaryStage, String rollno) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f8ff, #e6f0ff);");

        Label title = new Label("🛠️ Registered Admins");
        title.setFont(new Font("Segoe UI Semibold", 26));
        title.setTextFill(Color.web("#2C3E50"));
        title.setEffect(new DropShadow(2.0, Color.LIGHTGRAY));

        VBox adminBox = new VBox(15);
        adminBox.setAlignment(Pos.TOP_LEFT);
        adminBox.setPadding(new Insets(10));
        adminBox.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: #3498db; -fx-border-radius: 10;");

        ScrollPane scrollPane = new ScrollPane(adminBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        try {
            Connection conn = DBConnection.getConnection();
            String selectSql = "SELECT * FROM admin_table";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectSql);

            while (rs.next()) {
                String adminData = "👤 Name: " + rs.getString("admin_name") +
                        " | 🆔 Roll No: " + rs.getString("admin_rollno") +
                        " | 📞 Phone: " + rs.getLong("admin_phone");

                Label adminLabel = new Label(adminData);
                adminLabel.setFont(new Font("Segoe UI", 14));
                adminLabel.setTextFill(Color.web("#34495e"));
                adminBox.getChildren().add(adminLabel);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            Label errorLabel = new Label("⚠ Error loading admin data.");
            errorLabel.setTextFill(Color.RED);
            adminBox.getChildren().add(errorLabel);
            e.printStackTrace();
        }

        Button backBtn = createStyledButton("🔙 Back");
        backBtn.setOnAction(e -> new Admin(rollno).start(primaryStage));

        root.getChildren().addAll(title, scrollPane, backBtn);
        Scene scene = new Scene(root, 600, 500);
        return scene;
    }

    private static Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setFont(new Font("Segoe UI", 16));
        button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 8px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 8px;"));
        return button;
    }
}
