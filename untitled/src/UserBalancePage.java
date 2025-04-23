import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserBalancePage {

    public static Scene getScene(Stage stage, String adminRollno) {
        VBox root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f8ff, #e6f0ff);");

        Label title = new Label("💰 Users' Balances");
        title.setFont(new Font("Segoe UI Semibold", 26));
        title.setTextFill(Color.web("#2C3E50"));
        title.setEffect(new DropShadow(2.0, Color.LIGHTGRAY));

        VBox balanceList = new VBox(15);
        balanceList.setPadding(new Insets(10));
        balanceList.setAlignment(Pos.TOP_LEFT);

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT user_name, user_rollno, user_balance FROM user_table";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String name = rs.getString("user_name");
                String roll = rs.getString("user_rollno");
                double balance = rs.getDouble("user_balance");

                Label userLabel = new Label("👤 " + name + " (Roll No: " + roll + ") → ₹" + balance);
                userLabel.setFont(new Font("Segoe UI", 16));
                userLabel.setTextFill(Color.DARKSLATEBLUE);
                balanceList.getChildren().add(userLabel);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Failed to load balances.");
            errorLabel.setTextFill(Color.RED);
            balanceList.getChildren().add(errorLabel);
        }

        ScrollPane scrollPane = new ScrollPane(balanceList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(250);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        Button backBtn = createStyledButton("🔙 Back");
        backBtn.setOnAction(e -> new Admin(adminRollno).start(stage));

        root.getChildren().addAll(title, scrollPane, backBtn);
        Scene scene = new Scene(root, 550, 420);
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

