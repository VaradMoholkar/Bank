import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.ListView;

import java.sql.*;

public class TransactionHistoryPage {

    public static Scene getScene(Stage primaryStage, String rollno) {
        VBox root = new VBox(25);
        root.setPadding(new Insets(40));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f0f8ff, #e6f0ff);");

        Label title = new Label("💳 Transaction History");
        title.setFont(new Font("Segoe UI Semibold", 28));
        title.setTextFill(Color.web("#2C3E50"));
        title.setEffect(new DropShadow(2.0, Color.LIGHTGRAY));

        ListView<String> transactionListView = new ListView<>();
        transactionListView.setPrefWidth(450);
        transactionListView.setStyle("-fx-background-color: #ffffff; -fx-border-color: #3498db; -fx-border-radius: 8px;");

        Button backBtn = createStyledButton("🔙 Back");

        fetchTransactionHistory(rollno, transactionListView);

        backBtn.setOnAction(e -> {

            new User(rollno).start(primaryStage);  // Pass rollno back to user dashboard
        });

        root.getChildren().addAll(title, transactionListView, backBtn);
        Scene scene = new Scene(root, 550, 450);
        scene.getStylesheets().add(TransactionHistoryPage.class.getResource("/style.css").toExternalForm());
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

    private static void fetchTransactionHistory(String rollno, ListView<String> transactionListView) {
        try {
            String t = rollno + "_transaction_table";
            Connection conn = DBConnection.getConnection();
            String selectSql = "SELECT * FROM " + t;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectSql);

            while (rs.next()) {
                String transactionInfo = "Transaction ID: " + rs.getString("transaction_id") +
                        "\nBefore: " + rs.getString("balance_before_transaction") +
                        " | Deducted: " + rs.getString("amount_deducted") +
                        " | After: " + rs.getString("balance_after_transaction") +
                        "\nDate: " + rs.getString("date_time") +
                        " | Receiver: " + rs.getString("reciver_rollno");

                transactionListView.getItems().add(transactionInfo);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

