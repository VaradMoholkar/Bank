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
import java.time.*;

public class TransferMoneyPage {

    public static Scene getScene(Stage primaryStage, String rollno) {
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f8ff, #e6f0ff);");

        Label title = new Label("💸 Transfer Money");
        title.setFont(new Font("Segoe UI Semibold", 28));
        title.setTextFill(Color.web("#2C3E50"));
        title.setEffect(new DropShadow(2.0, Color.LIGHTGRAY));

        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount to transfer");
        amountField.setFont(new Font("Segoe UI", 16));
        amountField.setMaxWidth(240);

        TextField receiverRollnoField = new TextField();
        receiverRollnoField.setPromptText("Enter receiver's roll number");
        receiverRollnoField.setFont(new Font("Segoe UI", 16));
        receiverRollnoField.setMaxWidth(240);

        Button transferBtn = createStyledButton("💸 Transfer");
        Button backBtn = createStyledButton("🔙 Back");

        transferBtn.setOnAction(e -> {
            String amountText = amountField.getText().trim();
            String receiverRollno = receiverRollnoField.getText().trim();

            if (!amountText.isEmpty() && !receiverRollno.isEmpty()) {
                try {
                    Connection conn = DBConnection.getConnection();

                    String selectSenderSql = "SELECT * FROM user_table WHERE user_rollno = ?";
                    PreparedStatement senderStmt = conn.prepareStatement(selectSenderSql);
                    senderStmt.setString(1, rollno);
                    ResultSet senderRs = senderStmt.executeQuery();

                    if (senderRs.next()) {
                        int senderBalance = senderRs.getInt("user_balance");

                        int transferAmount = Integer.parseInt(amountText);
                        if (senderBalance >= transferAmount) {

                            String selectReceiverSql = "SELECT * FROM user_table WHERE user_rollno = ?";
                            PreparedStatement receiverStmt = conn.prepareStatement(selectReceiverSql);
                            receiverStmt.setString(1, receiverRollno);
                            ResultSet receiverRs = receiverStmt.executeQuery();

                            if (receiverRs.next()) {

                                String updateSenderSql = "UPDATE user_table SET user_balance = ? WHERE user_rollno = ?";
                                PreparedStatement updateSenderStmt = conn.prepareStatement(updateSenderSql);
                                updateSenderStmt.setInt(1, senderBalance - transferAmount);
                                updateSenderStmt.setString(2, rollno);
                                updateSenderStmt.executeUpdate();

                                int receiverBalance = receiverRs.getInt("user_balance");
                                String updateReceiverSql = "UPDATE user_table SET user_balance = ? WHERE user_rollno = ?";
                                PreparedStatement updateReceiverStmt = conn.prepareStatement(updateReceiverSql);
                                updateReceiverStmt.setInt(1, receiverBalance + transferAmount);
                                updateReceiverStmt.setString(2, receiverRollno);
                                updateReceiverStmt.executeUpdate();

                                logTransaction(conn, rollno, transferAmount, senderBalance - transferAmount, "sent", receiverRollno);
                                logTransaction(conn, receiverRollno, -(transferAmount), receiverBalance + transferAmount, "received", receiverRollno);

                                showPopup("Transfer successful!");
                            } else {
                                showPopup("Receiver not found!");
                            }
                            receiverRs.close();
                            receiverStmt.close();
                        } else {
                            showPopup("Insufficient balance!");
                        }
                    } else {
                        showPopup("Sender not found!");
                    }

                    senderRs.close();
                    senderStmt.close();
                    conn.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            amountField.clear();
            receiverRollnoField.clear();
        });

        backBtn.setOnAction(e -> {
            new User(rollno).start(primaryStage);
        });

        root.getChildren().addAll(title, receiverRollnoField,amountField, transferBtn, backBtn);
        Scene scene = new Scene(root, 550, 420);
        scene.getStylesheets().add(TransferMoneyPage.class.getResource("/style.css").toExternalForm());
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

    private static void showPopup(String message) {
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
            } catch (InterruptedException ignored) {
            }
        }).start();
    }

    private static void logTransaction(Connection conn, String rollno, int amount, int newBalance, String transactionType, String otherRollno) {
        try {
            String transactionTable = rollno + "_transaction_table";
            String insertSql = "INSERT INTO " + transactionTable + " (transaction_id, balance_before_transaction, amount_deducted, balance_after_transaction, date_time, reciver_rollno) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertSql);
            stmt.setLong(1, System.currentTimeMillis());
            stmt.setInt(2, newBalance + amount);
            stmt.setInt(3, amount);
            stmt.setInt(4, newBalance);
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(6, otherRollno);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

