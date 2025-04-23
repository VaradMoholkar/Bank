import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDateTime;

public class Withdraw_Page {

    public static Scene getScene(Stage primaryStage, String rollno) {
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffe6e6, #fff0f0);");

        Label title = new Label("💸 Withdraw Money");
        title.setFont(new Font("Segoe UI Semibold", 28));
        title.setTextFill(Color.web("#2C3E50"));
        title.setEffect(new DropShadow(2.0, Color.LIGHTGRAY));

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setFont(new Font("Segoe UI", 16));
        amountField.setMaxWidth(240);

        Button withdrawBtn = createStyledButton("💸 Withdraw");
        Button backBtn = createStyledButton("🔙 Back");

        withdrawBtn.setOnAction(e -> {
            String amountText = amountField.getText().trim();
            if (!amountText.isEmpty()) {
                try {
                    int amount = Integer.parseInt(amountText);

                    Connection conn = DBConnection.getConnection();
                    String selectSql = "SELECT user_balance FROM user_table WHERE user_rollno = ?";
                    PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                    selectStmt.setString(1, rollno);
                    ResultSet rs = selectStmt.executeQuery();

                    if (rs.next()) {
                        int currentBalance = rs.getInt("user_balance");
                        if (amount <= currentBalance) {
                            int newBalance = currentBalance - amount;

                            // Insert into transaction table
                            String transactionTable = rollno + "_transaction_table";
                            String insertSql = "INSERT INTO " + transactionTable + " (transaction_id, balance_before_transaction, amount_deducted, balance_after_transaction, date_time, reciver_rollno) VALUES (?, ?, ?, ?, ?, ?)";
                            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                            insertStmt.setLong(1, System.currentTimeMillis());
                            insertStmt.setInt(2, currentBalance);
                            insertStmt.setInt(3, amount);  // Withdraw = positive deduction
                            insertStmt.setInt(4, newBalance);
                            insertStmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                            insertStmt.setString(6, rollno);
                            insertStmt.executeUpdate();


                            String updateSql = "UPDATE user_table SET user_balance = ? WHERE user_rollno = ?";
                            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                            updateStmt.setInt(1, newBalance);
                            updateStmt.setString(2, rollno);
                            updateStmt.executeUpdate();

                            showStyledPopup("₹" + amount + " withdrawn successfully!");
                            insertStmt.close();
                            updateStmt.close();
                        } else {
                            showStyledPopup("❌ Insufficient balance!");
                        }
                    } else {
                        showStyledPopup("❌ User not found!");
                    }

                    rs.close();
                    selectStmt.close();
                    conn.close();
                    amountField.clear();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showStyledPopup("❌ Error occurred during withdrawal.");
                }
            }
        });

        backBtn.setOnAction(e -> {
            new User(rollno).start(primaryStage);
        });

        root.getChildren().addAll(title, amountField, withdrawBtn, backBtn);

        Scene scene = new Scene(root, 550, 420);
        scene.getStylesheets().add(Withdraw_Page.class.getResource("/style.css").toExternalForm());
        return scene;
    }

    private static Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setFont(new Font("Segoe UI", 16));
        button.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 8px;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 8px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 8px;"));
        return button;
    }

    private static void showStyledPopup(String message) {
        Stage popup = new Stage();
        popup.setAlwaysOnTop(true);
        popup.setResizable(false);
        popup.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        Label msgLabel = new Label(message);
        msgLabel.setFont(new Font("Segoe UI", 16));
        msgLabel.setTextFill(Color.WHITE);
        msgLabel.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 10; -fx-padding: 15px;");

        VBox box = new VBox(msgLabel);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(box);
        scene.setFill(Color.TRANSPARENT);

        popup.setScene(scene);
        popup.setWidth(320);
        popup.setHeight(100);
        popup.setX(javafx.stage.Screen.getPrimary().getBounds().getWidth() / 2 - 160);
        popup.setY(javafx.stage.Screen.getPrimary().getBounds().getHeight() / 2 - 50);
        popup.show();

        new Thread(() -> {
            try {
                Thread.sleep(2500);
                javafx.application.Platform.runLater(popup::close);
            } catch (InterruptedException ignored) {}
        }).start();
    }
}
